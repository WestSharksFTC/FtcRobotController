package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.chassis;

import com.seattlesolvers.solverslib.command.SubsystemBase;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

import com.pedropathing.math.Pose;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.ManualDrive;
import com.pedropathing.drivetrain.DrivePowers;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.util.TelemetryData;

public class Chassis extends SubsystemBase {
    private final Follower follower;

    public Chassis(HardwareMap hardwareMap) {
        follower = Constants.create(hardwareMap);
    }

    public Follower getFollower() {
        return follower;
    }

    public void driveRobotCentric(double forward, double strafe, double turn) {
        follower.manual(forward, strafe, turn);
    }

    public void driveRobotCentricHold(double forward, double strafe, double turn) {
        ManualDrive.driveOrHold(follower, forward, strafe, turn);
    }

    public void driveFieldCentric(double forward, double strafe, double turn) {
        DrivePowers powers = ManualDrive.fieldCentric(forward, strafe, turn, follower.pose().heading());

        follower.manual(powers);
    }

    public void driveFieldCentricHold(double forward, double strafe, double turn) {
        DrivePowers powers = ManualDrive.fieldCentric(forward, strafe, turn, follower.pose().heading());

        ManualDrive.driveOrHold(follower, powers);
    }

    public void setPose(Pose pose) {
        follower.setPose(pose);
    }

    public Pose getPose() {
        return follower.pose();
    }

    public void stop() {
        follower.stop();
    }

    private void update() {
        follower.update();
    }


    // Telemetry do Chassis
    public void addTelemetry(TelemetryData telemetry) {
        telemetry.addData("Robot X", getPose().x());
        telemetry.addData("Robot Y", getPose().y());
        telemetry.addData("Robot Heading", Math.toDegrees(getPose().heading()));
    }

    @Override
    public void periodic(){
        update();
    }
}
