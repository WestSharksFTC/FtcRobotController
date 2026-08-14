package org.firstinspires.ftc.teamcode.pedroPathing;

import com.pedropathing.control.FilteredPIDFCoefficients;
import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.control.PredictiveBrakingCoefficients;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.constants.PinpointConstants;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Constants {
    public static FollowerConstants followerConstants = new FollowerConstants()
            .mass(6) // Robot mass in Kg
            .forwardZeroPowerAcceleration(-54.17400197073291) // Automatic -> Forward Zero Power Acceleration Tuner
            .lateralZeroPowerAcceleration(-64.29726930830856) // Automatic -> Lateral Zero Power Acceleration Tuner

            .translationalPIDFCoefficients(new PIDFCoefficients(0.05, 0, 0.005, 0.03)) // Manual -> Translational Tuner
                // Tuning -> follower -> constants -> coefficientsTranslationalPIDf
                // P = 0, I = 0, D = 0, F = 0
                // F -> Decrease until the robot stop shaking (pull the robot)
                // P -> Pull the robot and increase until the correction is correct (undercorrect > CORRECT < overcorrect)
                // D -> Pull the robot and increase until the robot doesn't pass the original position (overSmooth(very slow) > CORRECT < underSmooth(very fast))

            .headingPIDFCoefficients(new PIDFCoefficients(1, 0, 0.05, 0.025)) // Manual -> Heading Tuner
                // Tuning -> follower -> constants -> coefficientsHeadingPIDf
                // P = 0, I = 0, D = 0, F = 0
                // F -> Decrease until the robot stop shaking (pull the robot)
                // P -> Pull the robot and increase until the correction is correct (undercorrect > CORRECT < overcorrect)
                // D -> Pull the robot and increase until the robot doesn't pass the original position (overSmooth(very slow) > CORRECT < underSmooth(very fast))

            .drivePIDFCoefficients(new FilteredPIDFCoefficients(0.025, 0, 0.00001, 0.6, 0.01)); // Manual -> Drive Tuner (IMPORTANT: Just tune this part after tune the braking strength)
                // Tuning -> follower -> constants -> coefficientsDrivePIDf
                // P = 0, I = 0, D = 0, T = don't touch in this value, F = 0
                // F -> IDK part 1
                // P -> IDK part 2
                // D -> IDK part 3

            // .centripetalScaling(0); // Manual -> Centripetal Tuner (IMPORTANT: It's the last tune you'll do)
                // Tuning -> follower -> centripetalScaling
                // centipetalScaling -> IDK part 4

            // .predictiveBrakingCoefficients();

    public static MecanumConstants driveConstants = new MecanumConstants()
            .maxPower(1)
            .rightFrontMotorName("rf")
            .rightRearMotorName("rb")
            .leftRearMotorName("lb")
            .leftFrontMotorName("lf")
            .leftFrontMotorDirection(DcMotor.Direction.FORWARD)
            .leftRearMotorDirection(DcMotor.Direction.FORWARD)
            .rightFrontMotorDirection(DcMotor.Direction.REVERSE)
            .rightRearMotorDirection(DcMotor.Direction.REVERSE)
            .xVelocity(73.34618227500617) // Automatic -> Forward Velocity Tuner
            .yVelocity(61.637128214197844); // Automatic -> Lateral Velocity Tuner

    public static PathConstraints pathConstraints = new PathConstraints(0.99, 100, 1, 1); // Manual -> Drive Tuner
        // Tuning -> follower -> pathConstraints
        // brakingStrength = 1
        // brakingStrength -> Watch the robot and increase until the position when it brakes is correct (underBrake(very smooth and stop before) > CORRECT < overBrake(very abrupt and stop after))

    public static PinpointConstants localizerConstants = new PinpointConstants()
            .forwardPodY(6)
            .strafePodX(7)
            .distanceUnit(DistanceUnit.INCH)
            .hardwareMapName("pinpoint")
            .encoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_SWINGARM_POD)
            .forwardEncoderDirection(GoBildaPinpointDriver.EncoderDirection.FORWARD)
            .strafeEncoderDirection(GoBildaPinpointDriver.EncoderDirection.REVERSED);

    public static Follower createFollower(HardwareMap hardwareMap) {
        return new FollowerBuilder(followerConstants, hardwareMap)
                .pinpointLocalizer(localizerConstants)
                .pathConstraints(pathConstraints)
                .mecanumDrivetrain(driveConstants)
                .build();
    }
}
