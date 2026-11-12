package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.shooter;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.utils.MathUtils;

import com.seattlesolvers.solverslib.command.SubsystemBase;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.VoltageSensor;

public class Shooter extends SubsystemBase {
    private DcMotorEx outtake;
    private VoltageSensor batteryVoltageSensor;

    private double targetRPM = 0.0;
    private double lastAppliedKF = Double.NaN;

    public void init(HardwareMap hardwareMap) {
        outtake = hardwareMap.get(DcMotorEx.class, ShooterHardware.SHOOTER_MOTOR);
        batteryVoltageSensor = hardwareMap.voltageSensor.iterator().next();

        outtake.setDirection(DcMotorEx.Direction.FORWARD);
        outtake.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        outtake.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);

        updateFeedforwardCompensation();
    }

    public void setVelocityRPM(double targetRPM) {
        this.targetRPM = targetRPM;

        double targetTicksPerSecond = MathUtils.rpmToTicksPerSecond(targetRPM, ShooterConstants.MOTOR_TICKS_PER_REV);

        outtake.setVelocity(targetTicksPerSecond);
    }

    public void stop() {
        targetRPM = 0.0;
        outtake.setPower(0.0);
    }

    public boolean isAtTargetVelocity() {
        return targetRPM > 0.0 && Math.abs(getTargetVelocityRPM() - getCurrentVelocityRPM()) <= ShooterConstants.VELOCITY_TOLERANCE_RPM;
    }

    private void updateFeedforwardCompensation() {
        double currentVoltage = getBatteryVoltage();

        if (currentVoltage <= 0.0) {
            currentVoltage = ShooterConstants.NOMINAL_VOLTAGE;
        }

        double adjustedKF = ShooterConstants.BASE_KF * (ShooterConstants.NOMINAL_VOLTAGE / currentVoltage);

        if (Double.isNaN(lastAppliedKF) || Math.abs(adjustedKF - lastAppliedKF) > 0.001) {
            updatePIDF(adjustedKF);
            lastAppliedKF = adjustedKF;
        }
    }

    private void updatePIDF(double kF) {
        outtake.setVelocityPIDFCoefficients(ShooterConstants.SHOOTER_KP, ShooterConstants.SHOOTER_KI, ShooterConstants.SHOOTER_KD, kF);
    }

    public double getCurrentVelocityRPM(){
        return MathUtils.ticksPerSecondToRPM(getCurrentVelocityTicksPerSecond(), ShooterConstants.MOTOR_TICKS_PER_REV);
    }

    public double getCurrentVelocityTicksPerSecond() {
        return outtake.getVelocity();
    }

    public double getTargetVelocityRPM(){
        return targetRPM;
    }

    public double getTargetVelocityTicksPerSecond() {
        return MathUtils.rpmToTicksPerSecond(targetRPM, ShooterConstants.MOTOR_TICKS_PER_REV);
    }

    public double getBatteryVoltage() {
        return batteryVoltageSensor.getVoltage();
    }


    // Telemetria do Shooter
    public void getTelemetry(Telemetry telemetry) {
        telemetry.addLine("========== SHOOTER ==========");
        telemetry.addData("Current RPM", "%.2f", getCurrentVelocityRPM());
        telemetry.addData("Target RPM", "%.0f", getTargetVelocityRPM());
        telemetry.addData("Velocity Error", "%.2f RPM", getTargetVelocityRPM() - getCurrentVelocityRPM());
        telemetry.addData("Current Velocity", "%.2f ticks/s", getCurrentVelocityTicksPerSecond());
        telemetry.addData("Battery Voltage", "%.2f V", getBatteryVoltage());
        telemetry.addData("Applied kF", "%.4f", lastAppliedKF);
        telemetry.addData("Motor Power", "%.2f", outtake.getPower());
        telemetry.addData("At Target Velocity", isAtTargetVelocity());
        telemetry.addLine();
    }


    @Override
    public void periodic(){
        updateFeedforwardCompensation();
    }
}