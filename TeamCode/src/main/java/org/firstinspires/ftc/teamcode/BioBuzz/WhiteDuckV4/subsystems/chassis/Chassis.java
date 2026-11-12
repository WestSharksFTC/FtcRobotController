package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.chassis;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

import com.seattlesolvers.solverslib.command.SubsystemBase;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;

import com.pedropathing.math.Pose;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.ManualDrive;
import com.pedropathing.drivetrain.DrivePowers;

import com.qualcomm.robotcore.hardware.HardwareMap;

public class Chassis extends SubsystemBase {
    private Follower follower;

    public void init(HardwareMap hardwareMap) {
        follower = Constants.create(hardwareMap);
    }

    public Follower getFollower() {
        return follower;
    }

    public void driveRobotCentric(double forward, double strafe, double turn) {
        follower.manual(-forward, strafe, turn);
    }

    public void driveRobotCentricHold(double forward, double strafe, double turn) {
        ManualDrive.driveOrHold(follower, -forward, strafe, turn);
    }

    public void driveFieldCentric(double forward, double strafe, double turn) {
        DrivePowers powers = ManualDrive.fieldCentric(-forward, strafe, turn, follower.pose().heading());

        follower.manual(powers);
    }

    public void driveFieldCentricHold(double forward, double strafe, double turn) {
        DrivePowers powers = ManualDrive.fieldCentric(-forward, strafe, turn, follower.pose().heading());

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

    public void update() {
        follower.update();
    }


    // Telemetria do Chassis
    public void getTelemetry(Telemetry telemetry) {
        telemetry.addLine("========== CHASSIS ==========");
        telemetry.addData("Robot X", "%.2f", getPose().x());
        telemetry.addData("Robot Y", "%.2f", getPose().y());
        telemetry.addData("Robot Heading", "%.2f°", Math.toDegrees(getPose().heading()));
        telemetry.addLine();
    }

    public void sendDashboardTelemetry() {
        TelemetryPacket packet = new TelemetryPacket();

        packet.put("Chassis/X", getPose().x());
        packet.put("Chassis/Y", getPose().y());
        packet.put("Chassis/Heading (degrees)", Math.toDegrees(getPose().heading()));

        FtcDashboard.getInstance().sendTelemetryPacket(packet);
    }
}
