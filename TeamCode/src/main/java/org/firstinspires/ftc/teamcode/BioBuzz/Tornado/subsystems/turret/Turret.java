package org.firstinspires.ftc.teamcode.BioBuzz.Tornado.subsystems.turret;

import com.pedropathing.math.Pose;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.SubsystemBase;

import java.util.function.Supplier;

import org.firstinspires.ftc.teamcode.BioBuzz.Tornado.field.FieldConstants;
import org.firstinspires.ftc.teamcode.BioBuzz.Tornado.logic.AimMath;
import org.firstinspires.ftc.teamcode.BioBuzz.Tornado.logic.TurretAim;
import org.firstinspires.ftc.teamcode.BioBuzz.Tornado.logic.TurretMath;

public class Turret extends SubsystemBase {
    /** MANUAL = joystick, AUTO = Limelight (tx), ODOMETRY = pose do Pedro + posicao do gol. */
    public enum Mode { MANUAL, AUTO, ODOMETRY }

    private final DcMotorEx motor;
    private final Limelight3A limelight;
    private final TurretAim aim = new TurretAim();

    private Mode mode = Mode.MANUAL;
    private double manualPower = 0.0;
    private boolean red = false;
    private Supplier<Pose> poseSource = null;
    private boolean runToPosition = false;

    // cache lido 1x por ciclo
    private int ticks = 0;
    private boolean valid = false;
    private double tx = 0.0, ty = 0.0;
    private double odometryDistance = 0.0;

    public Turret(HardwareMap hw) {
        motor = hw.get(DcMotorEx.class, TurretConstants.MOTOR);
        motor.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        motor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        motor.setDirection(DcMotorEx.Direction.REVERSE);

        limelight = hw.get(Limelight3A.class, TurretConstants.LIMELIGHT);
        limelight.pipelineSwitch(TurretConstants.PIPELINE_BLUE);
        limelight.start();
    }

    /** Define a alianca: escolhe o pipeline da Limelight e o gol usado pelo modo ODOMETRY. */
    public void setAlliance(boolean isRed) {
        red = isRed;
        limelight.pipelineSwitch(isRed ? TurretConstants.PIPELINE_RED : TurretConstants.PIPELINE_BLUE);
    }

    /** Fonte da pose do robo (ex.: follower::pose). Obrigatoria para o modo ODOMETRY. */
    public void setPoseSource(Supplier<Pose> source) { poseSource = source; }

    public void setMode(Mode m) {
        if (m != mode) aim.reset();
        mode = m;
    }

    public Mode getMode() { return mode; }
    public void setManualPower(double p) { manualPower = p; }

    private double ticksPerDegree() {
        return TurretMath.ticksPerDegree(TurretConstants.MOTOR_TICKS_PER_REV, TurretConstants.GEAR_RATIO);
    }

    private int maxTicks() {
        return TurretMath.maxTicks(TurretConstants.MAX_DEGREES, ticksPerDegree());
    }

    private void leaveRunToPosition() {
        if (runToPosition) {
            motor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
            runToPosition = false;
        }
    }

    @Override
    public void periodic() {
        ticks = motor.getCurrentPosition();
        LLResult r = limelight.getLatestResult();
        valid = r != null && r.isValid();
        tx = valid ? r.getTx() : 0.0;
        ty = valid ? r.getTy() : 0.0;

        switch (mode) {
            case ODOMETRY:
                if (poseSource == null || poseSource.get() == null) {
                    leaveRunToPosition();
                    motor.setPower(0.0);
                    break;
                }
                Pose p = poseSource.get();
                double goalX = red ? FieldConstants.RED_GOAL_X : FieldConstants.BLUE_GOAL_X;
                double goalY = red ? FieldConstants.RED_GOAL_Y : FieldConstants.BLUE_GOAL_Y;
                double targetDeg = AimMath.targetTurretDeg(p.x(), p.y(), p.heading(),
                        TurretConstants.OFFSET_X, TurretConstants.OFFSET_Y, goalX, goalY)
                        * TurretConstants.TURRET_SIGN;
                odometryDistance = AimMath.distance(p.x(), p.y(), p.heading(),
                        TurretConstants.OFFSET_X, TurretConstants.OFFSET_Y, goalX, goalY);
                int target = AimMath.targetTicks(targetDeg, ticks, ticksPerDegree(), maxTicks());
                motor.setTargetPosition(target);          // alvo ANTES de trocar para RUN_TO_POSITION
                if (!runToPosition) {
                    motor.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
                    runToPosition = true;
                }
                motor.setPower(TurretConstants.ODOMETRY_POWER);
                break;

            case AUTO:
                leaveRunToPosition();
                motor.setPower(aim.update(valid, tx, ticks, maxTicks(),
                        TurretConstants.KP, TurretConstants.ESCAPE_POWER));
                break;

            default: // MANUAL
                leaveRunToPosition();
                motor.setPower(TurretMath.manualPower(manualPower, ticks, maxTicks()));
        }
    }

    public boolean hasTarget() { return valid; }
    public double getTx() { return tx; }
    public double getTy() { return ty; }
    public int getTicks() { return ticks; }
    public boolean isEscaping() { return aim.isEscaping(); }
    /** Distancia ate o gol calculada pela odometria (so atualiza no modo ODOMETRY). Util para calibrar a tabela. */
    public double getOdometryDistance() { return odometryDistance; }
}
