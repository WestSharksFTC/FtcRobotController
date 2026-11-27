package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.utils;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;

public class OpModeStorage {
    private static final PoseFactory poseFactory = PoseFactory.degrees();

    private static final Pose autonomousStartPose = poseFactory.of(56, 8, 180);
    private static Pose autonomousEndPose = poseFactory.of(56, 8, 180);

    public Pose getAutonomousStartPose(){
        return autonomousStartPose;
    }

    public Pose getAutonomousEndPose() {
        return autonomousEndPose;
    }

    public void setAutonomousEndPose(Pose pose){
        autonomousEndPose = poseFactory.of(pose.x(), pose.y(), pose.heading());
    }
}