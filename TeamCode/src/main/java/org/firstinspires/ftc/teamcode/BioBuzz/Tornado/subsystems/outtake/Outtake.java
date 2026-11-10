package org.firstinspires.ftc.teamcode.BioBuzz.Tornado.subsystems.outtake;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.seattlesolvers.solverslib.command.SubsystemBase;

import java.util.function.DoubleSupplier;

import org.firstinspires.ftc.teamcode.BioBuzz.Tornado.logic.OuttakeMath;

public class Outtake extends SubsystemBase {
    private final DcMotorEx motor;
    private final VoltageSensor battery;

    private DoubleSupplier tySource = null;   // != null => outtake ligado
    private double target = 0.0;
    private double velocity = 0.0;
    private double voltage = OuttakeConstants.NOMINAL_VOLTAGE;
    private double lastKf = Double.NaN;

    public Outtake(HardwareMap hw) {
        motor = hw.get(DcMotorEx.class, OuttakeConstants.MOTOR);
        battery = hw.voltageSensor.iterator().next();
        motor.setDirection(DcMotorEx.Direction.FORWARD);
        motor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        motor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);
    }

    /** Liga o outtake; a velocidade-alvo e recalculada a cada ciclo a partir do ty. */
    public void spin(DoubleSupplier ty) {
        tySource = ty;
    }

    public void stop() {
        tySource = null;
        target = 0.0;
    }

    @Override
    public void periodic() {
        velocity = motor.getVelocity();
        voltage = battery.getVoltage();

        if (tySource == null) {
            motor.setPower(0.0);
            return;
        }
        target = OuttakeMath.velocityFromTy(tySource.getAsDouble(),
                OuttakeConstants.MIN_TY, OuttakeConstants.MAX_TY,
                OuttakeConstants.VEL_AT_MIN_TY, OuttakeConstants.VEL_AT_MAX_TY);

        double kf = OuttakeMath.compensatedKf(OuttakeConstants.BASE_KF,
                OuttakeConstants.NOMINAL_VOLTAGE, voltage);
        // so reescreve o PIDF no hub quando mudou (evita uma chamada de hub por ciclo)
        if (Double.isNaN(lastKf) || Math.abs(kf - lastKf) > 0.01) {
            motor.setVelocityPIDFCoefficients(OuttakeConstants.KP, OuttakeConstants.KI,
                    OuttakeConstants.KD, kf);
            lastKf = kf;
        }
        motor.setVelocity(target);
    }

    public boolean atSpeed() {
        return tySource != null && OuttakeMath.atSpeed(velocity, target, OuttakeConstants.TOLERANCE);
    }

    public double getVelocity() { return velocity; }
    public double getTarget() { return target; }
    public double getVoltage() { return voltage; }
}
