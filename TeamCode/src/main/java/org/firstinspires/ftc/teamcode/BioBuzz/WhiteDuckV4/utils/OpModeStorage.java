package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.utils;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;

public class OpModeStorage {
    private static PoseFactory poseFactory = PoseFactory.degrees();

    public static Pose autonomousEndPose = poseFactory.of(56, 8, 90);
}