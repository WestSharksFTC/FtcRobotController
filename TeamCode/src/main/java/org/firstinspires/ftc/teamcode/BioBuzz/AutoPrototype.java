package org.firstinspires.ftc.teamcode.BioBuzz;


import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import com.pedropathing.util.Timer;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

@TeleOp
public class AutoPrototype extends OpMode {
    private Follower follower;
    private Timer pathTimer, opModeTimer;

    public enum PathState {
        // START POSITION -> END POSITION
        // DRIVE = MOVEMENT STATE
        // SHOOT = ATTEMPT TO SCORE THE ARTIFACT

        DRIVE_START_POS_TO_COLLECT_POS,
        DRIVE_FORWARD_TO_COLLECT,
        DRIVE_TO_SHOOT_POS,
        AT_THE_END_POS
    }

    PathState pathState;

    private final Pose startPose = new Pose(70, 10, Math.toRadians(90));
    private final Pose startCollectPose = new Pose(105, 35, Math.toRadians(0));
    private final Pose artifactsCollectedPose = new Pose(125, 35, Math.toRadians(0));
    private final Pose shootPose = new Pose(100, 110, Math.toRadians(45));

    private PathChain driveStartPosToCollectPos;
    private PathChain driveForwardToCollect;
    private PathChain driveToShootPos;

    public void buildPaths(){
        // Put in cordinates for starting pose > ending pose

        driveStartPosToCollectPos = follower.pathBuilder()
                .addPath(new BezierLine(startPose, startCollectPose))
                .setLinearHeadingInterpolation(startPose.getHeading(), startCollectPose.getHeading())
                .build();

        driveForwardToCollect = follower.pathBuilder()
                .addPath(new BezierLine(startCollectPose, artifactsCollectedPose))
                .setLinearHeadingInterpolation(startCollectPose.getHeading(), artifactsCollectedPose.getHeading())
                .build();

        driveToShootPos = follower.pathBuilder()
                .addPath(new BezierLine(artifactsCollectedPose, shootPose))
                .setLinearHeadingInterpolation(artifactsCollectedPose.getHeading(), shootPose.getHeading())
                .build();
    }

    public void statePathUpdate(){
        switch (pathState){
            case DRIVE_START_POS_TO_COLLECT_POS:
                follower.followPath(driveStartPosToCollectPos, true);
                setPathState(PathState.DRIVE_FORWARD_TO_COLLECT); // Reset the timer
                break;

            case DRIVE_FORWARD_TO_COLLECT:
                follower.followPath(driveForwardToCollect, true);
                setPathState(PathState.DRIVE_TO_SHOOT_POS); // Reset the timer
                break;

            case DRIVE_TO_SHOOT_POS:
                follower.followPath(driveToShootPos, true);
                setPathState(PathState.AT_THE_END_POS); // Reset the timer
                break;

            case AT_THE_END_POS:
                if (!follower.isBusy()){
                    telemetry.addLine("Done path test succesfully!!!");
                }
                break;

            default:
                telemetry.addLine("No state commanded");
                break;
        }
    }

    public void setPathState(PathState newState){
        pathState = newState;
        pathTimer.resetTimer();
    }

    @Override
    public void init() {
        pathState = PathState.DRIVE_START_POS_TO_COLLECT_POS;
        pathTimer = new Timer();
        opModeTimer = new Timer();
        follower = Constants.createFollower(hardwareMap);

        buildPaths();
        follower.setPose(startPose);
    }

    @Override
    public void start() {
        opModeTimer.resetTimer();
        setPathState(pathState);
    }

    @Override
    public void loop() {
        follower.update();
        statePathUpdate();

        telemetry.addData("path state", pathState.toString());
        telemetry.addData("x", follower.getPose().getX());
        telemetry.addData("y", follower.getPose().getY());
        telemetry.addData("heading", follower.getPose().getHeading());
        telemetry.addData("path timer", pathTimer.getElapsedTimeSeconds());
    }
}