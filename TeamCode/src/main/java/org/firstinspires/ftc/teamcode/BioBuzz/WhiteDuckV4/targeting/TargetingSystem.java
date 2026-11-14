package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.targeting;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.chassis.Chassis;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.field.FieldConstants;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.utils.MathUtils;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.pedropathing.math.Pose;

public class TargetingSystem {
    private final Chassis chassis;

    public TargetingSystem(Chassis chassis) {
        this.chassis = chassis;
    }

    public boolean atAudienceSide() {
        return chassis.getPose().y() <= FieldConstants.FIELD_CENTER_Y;
    }

    public double getTargetX() {
        return atAudienceSide() ? FieldConstants.AUDIENCE_HIVE_X : FieldConstants.BACK_HIVE_X;
    }

    public double getTargetY() {
        return atAudienceSide() ? FieldConstants.AUDIENCE_HIVE_Y : FieldConstants.BACK_HIVE_Y;
    }

    public double getTargetHeading(){
        Pose robotPose = chassis.getPose();

        double deltaX = getTargetX() - robotPose.x();
        double deltaY = getTargetY() - robotPose.y();

        return Math.toDegrees(Math.atan2(deltaY, deltaX));
    }

    public double getTurretAngle(){
        double robotHeading = Math.toDegrees(chassis.getPose().heading());

        return MathUtils.normalizeAngle(robotHeading - getTargetHeading());
    }

    public double getTargetDistanceIN() {
        Pose robotPose = chassis.getPose();

        double deltaX = getTargetX() - robotPose.x();
        double deltaY = getTargetY() - robotPose.y();

        return Math.hypot(deltaX, deltaY);
    }

    public void getTelemetry(Telemetry telemetry) {
        telemetry.addLine("========== TARGETING ==========");
        telemetry.addData("Target Hive", atAudienceSide() ? "Audience" : "Back");
        telemetry.addData("Target X", "%.2f", getTargetX());
        telemetry.addData("Target Y", "%.2f", getTargetY());
        telemetry.addData("Distance (in)", "%.2f", getTargetDistanceIN());
        telemetry.addData("Target Heading", "%.2f°", getTargetHeading());
        telemetry.addData("Turret Angle", "%.2f°", getTurretAngle());
        telemetry.addLine();
    }

    public void sendDashboardTelemetry() {
        TelemetryPacket packet = new TelemetryPacket();

        packet.put("Targeting/Target Hive", atAudienceSide() ? 0 : 1);
        packet.put("Targeting/Target X", getTargetX());
        packet.put("Targeting/Target Y", getTargetY());
        packet.put("Targeting/Distance (in)", getTargetDistanceIN());
        packet.put("Targeting/Target Heading (degrees)", getTargetHeading());
        packet.put("Targeting/Turret Angle (degrees)", getTurretAngle());

        FtcDashboard.getInstance().sendTelemetryPacket(packet);
    }
}
