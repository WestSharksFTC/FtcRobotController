package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.opModes.autonomous;

import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;

public class AutonomousPoses {
    private final PoseFactory poseFactory = PoseFactory.degrees();

    // Poses
    private final Pose startAndShoot1Pos = poseFactory.of(56, 8, 180);
    private final Pose collect1GardenPos = poseFactory.of(8, 8, 180);
    private final Pose shoot2AndShoot4Pos = poseFactory.of(38, 118, 90);
    private final Pose collect2FlowerAndShoot3Pos = poseFactory.of(47, 128, 90);
    private final Pose collect3FlowerPos = poseFactory.of(12, 46, 180);
    private final Pose parkPos = poseFactory.of(10, 95, 0);

    // Control Points
    private final Pose shoot1PosToParkPosControlPoint1 = poseFactory.of(25, 25, 0); // Min
    private final Pose shoot2PosToCollect2FlowerAndShoot3PosControlPoint1 = poseFactory.of(47, 118, 90);

    private final Pose collect2FlowerAndShoot3PosToCollect3FlowerPosControlPoint1 = poseFactory.of(16, 95, 180);
    private final Pose collect2FlowerAndShoot3PosToCollect3FlowerPosControlPoint2 = poseFactory.of(46, 46, 180);

    private final Pose collect3FlowerPosToShoot2AndShoot4PosControlPoint1 = poseFactory.of(36, 46, 90);
    private final Pose collect3FlowerPosToShoot2AndShoot4PosControlPoint2 = poseFactory.of(20, 95, 90);

    // Paths Min
    public Path shoot1PosToParkPos(){
        return Paths.curve(startAndShoot1Pos, shoot1PosToParkPosControlPoint1, parkPos).linear(startAndShoot1Pos, parkPos);
    }

    // Paths Mid/Max
    public Path shoot1PosToCollect1Pos(){
        return Paths.line(startAndShoot1Pos, collect1GardenPos).linear(startAndShoot1Pos, collect1GardenPos);
    }
    public Path collect1PosToShoot2Pos(){
        return Paths.line(collect1GardenPos, shoot2AndShoot4Pos).linear(collect1GardenPos, shoot2AndShoot4Pos);
    }

    public Path shoot2PosToCollect2AndShoot3Pos(){
        return Paths.curve(shoot2AndShoot4Pos, shoot2PosToCollect2FlowerAndShoot3PosControlPoint1, collect2FlowerAndShoot3Pos).linear(shoot2AndShoot4Pos, collect2FlowerAndShoot3Pos);
    }
    public Path shoot3PosToCollect3Pos(){
        return Paths.curve(collect2FlowerAndShoot3Pos, collect2FlowerAndShoot3PosToCollect3FlowerPosControlPoint1, collect2FlowerAndShoot3PosToCollect3FlowerPosControlPoint2, collect3FlowerPos).linear(collect2FlowerAndShoot3Pos, collect3FlowerPos);
    }
    public Path collect3PosToShoot4Pos(){
        return Paths.curve(collect3FlowerPos, collect3FlowerPosToShoot2AndShoot4PosControlPoint1, collect3FlowerPosToShoot2AndShoot4PosControlPoint2, shoot2AndShoot4Pos).linear(collect3FlowerPos, shoot2AndShoot4Pos);
    }

    public Path shoot4PosToParkPos(){
        return Paths.line(shoot2AndShoot4Pos, parkPos).linear(shoot2AndShoot4Pos, parkPos);
    }

}
