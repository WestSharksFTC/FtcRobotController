package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.targeting;

import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.chassis.Chassis;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.field.FieldConstants;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.utils.MathUtils;

import com.pedropathing.math.Pose;
import com.seattlesolvers.solverslib.util.TelemetryData;

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

    private double getTurretX() {
        Pose robotPose = chassis.getPose();

        return robotPose.x() - TargetingSystemConstants.TURRET_OFFSET_IN * Math.cos(robotPose.heading());
    }

    private double getTurretY() {
        Pose robotPose = chassis.getPose();

        return robotPose.y() - TargetingSystemConstants.TURRET_OFFSET_IN * Math.sin(robotPose.heading());
    }

    public double getTargetHeading(){
        double deltaX = getTargetX() - getTurretX();
        double deltaY = getTargetY() - getTurretY();

        return Math.toDegrees(Math.atan2(deltaY, deltaX));
    }

    public double getTurretAngle(){
        double robotHeading = Math.toDegrees(chassis.getPose().heading());

        return MathUtils.normalizeAngle(robotHeading - getTargetHeading());
    }

    public double getTargetDistanceIN() {
        double deltaX = getTargetX() - getTurretX();
        double deltaY = getTargetY() - getTurretY();

        return Math.hypot(deltaX, deltaY);
    }

    public void addTelemetry(TelemetryData telemetry) {
        telemetry.addData("Target Hive", atAudienceSide() ? "Audience" : "Back");
        telemetry.addData("Target X", getTargetX());
        telemetry.addData("Target Y", getTargetY());
        telemetry.addData("Distance (in)", getTargetDistanceIN());
        telemetry.addData("Target Heading", getTargetHeading());
        telemetry.addData("Turret Angle", getTurretAngle());
    }
}