package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.turret;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.utils.MathUtils;

import com.seattlesolvers.solverslib.command.SubsystemBase;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;

public class Turret extends SubsystemBase {
    private DcMotorEx turretMotor;

    private double targetAngleDegrees = 0.0;

    public void init(HardwareMap hardwareMap) {
        turretMotor = hardwareMap.get(DcMotorEx.class, TurretHardware.TURRET_MOTOR);

        turretMotor.setDirection(DcMotorEx.Direction.REVERSE);

        turretMotor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);

        turretMotor.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        turretMotor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
    }

    public void setPower(double power) {
        if (turretMotor.getMode() != DcMotorEx.RunMode.RUN_USING_ENCODER) {
            turretMotor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        }

        double currentPosition = getCurrentPositionDegrees();

        power = Range.clip(power, -1.0, 1.0);

        boolean leftLimitReached = currentPosition <= -TurretConstants.MAX_ANGLE_DEGREES && power < 0;
        boolean rightLimitReached = currentPosition >= TurretConstants.MAX_ANGLE_DEGREES && power > 0;

        if (leftLimitReached || rightLimitReached) {
            turretMotor.setPower(0.0);
        } else {
            turretMotor.setPower(power);
        }
    }

    public void setTargetAngle(double degrees) {
        targetAngleDegrees = Range.clip(degrees, -TurretConstants.MAX_ANGLE_DEGREES, TurretConstants.MAX_ANGLE_DEGREES);

        int targetTicks = (int) Math.round(MathUtils.degreesToTicks(targetAngleDegrees, TurretConstants.getTicksPerDegree()));

        turretMotor.setTargetPosition(targetTicks);
        if (turretMotor.getMode() != DcMotorEx.RunMode.RUN_TO_POSITION) {
            turretMotor.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
        }
        turretMotor.setPower(TurretConstants.POSITION_POWER);
    }

    public void recentralize() {
        setTargetAngle(0.0);
    }

    public boolean isAtTarget() {
        return Math.abs(targetAngleDegrees - getCurrentPositionDegrees()) <= TurretConstants.POSITION_TOLERANCE_DEGREES;
    }

    public void stop(){
        turretMotor.setPower(0.0);
    }

    public int getCurrentPositionTicks(){
        return turretMotor.getCurrentPosition();
    }

    public double getCurrentPositionDegrees(){
        return MathUtils.ticksToDegrees(getCurrentPositionTicks(), TurretConstants.getTicksPerDegree());
    }

    public double getPower(){
        return turretMotor.getPower();
    }


    // Telemetria da Turret
    public void getTelemetry(Telemetry telemetry) {
        telemetry.addLine("========== TURRET ==========");
        telemetry.addData("Current Position", "%.2f°", getCurrentPositionDegrees());
        telemetry.addData("Current Position (Ticks)", getCurrentPositionTicks());
        telemetry.addData("Target Position", "%.0f°", targetAngleDegrees);
        telemetry.addData("Error Position", "%.2f°", targetAngleDegrees - getCurrentPositionDegrees());
        telemetry.addData("Current Power", "%.2f", getPower());
        telemetry.addData("Current Motor Mode", turretMotor.getMode());
        telemetry.addData("At Target", isAtTarget());
        telemetry.addLine();
    }

    public void sendDashboardTelemetry() {
        TelemetryPacket packet = new TelemetryPacket();

        packet.put("Turret/Current Position (degrees)", getCurrentPositionDegrees());
        packet.put("Turret/Current Position (ticks)", getCurrentPositionTicks());

        packet.put("Turret/Target Position (degrees)", targetAngleDegrees);
        packet.put("Turret/Position Error (degrees)", targetAngleDegrees - getCurrentPositionDegrees());

        packet.put("Turret/Current Power", getPower());

        packet.put("Turret/At Target", isAtTarget() ? 1 : 0);

        FtcDashboard.getInstance().sendTelemetryPacket(packet);
    }
}
