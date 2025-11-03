package org.firstinspires.ftc.teamcode.Decode.Tsunami;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.opencv.core.Mat;

public class TsunamiChassis {
    // Motores
    private DcMotor frontLeftMotor, backLeftMotor, frontRightMotor, backRightMotor;

    // IMU
    private IMU imu;

    // Pinpoint
    private GoBildaPinpointDriver pinpoint;

    public void init(HardwareMap hardwareMap){
        // Motores
        frontLeftMotor = hardwareMap.get(DcMotor.class, "front_left_motor");
        backLeftMotor = hardwareMap.get(DcMotor.class, "back_left_motor");
        frontRightMotor = hardwareMap.get(DcMotor.class, "front_right_motor");
        backRightMotor = hardwareMap.get(DcMotor.class, "back_right_motor");

        frontLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotor.Direction.REVERSE);

        frontLeftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // IMU
        imu = hardwareMap.get(IMU.class, "imu");

        RevHubOrientationOnRobot RevOrientation = new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.RIGHT,
                RevHubOrientationOnRobot.UsbFacingDirection.UP
        );

        imu.initialize(new IMU.Parameters(RevOrientation));

        // Pinpoint
        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");

        pinpoint.setOffsets(-85.0, -160.0, DistanceUnit.MM);

        pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_SWINGARM_POD);

        pinpoint.setEncoderDirections(
                GoBildaPinpointDriver.EncoderDirection.FORWARD,
                GoBildaPinpointDriver.EncoderDirection.FORWARD
        );

        pinpoint.resetPosAndIMU();

        pinpoint.setPosition(new Pose2D(DistanceUnit.CM, 0, 0, AngleUnit.DEGREES, 0));
    }

    public void update(){
        pinpoint.update();
    }

    public void drive(double forward, double strafe, double rotate){
        double frontLeftPower = forward + strafe + rotate;
        double backLeftPower = forward - strafe + rotate;
        double frontRightPower = forward - strafe - rotate;
        double backRightPower = forward + strafe - rotate;

        double maxPower = 1.0;
        double maxSpeed = 1.0;

        maxPower = Math.max(maxPower, Math.abs(frontLeftPower));
        maxPower = Math.max(maxPower, Math.abs(backLeftPower));
        maxPower = Math.max(maxPower, Math.abs(frontRightPower));
        maxPower = Math.max(maxPower, Math.abs(backRightPower));

        frontLeftMotor.setPower(maxSpeed * (frontLeftPower / maxPower));
        backLeftMotor.setPower(maxSpeed * (backLeftPower / maxPower));
        frontRightMotor.setPower(maxSpeed * (frontRightPower / maxPower));
        backRightMotor.setPower(maxSpeed * (backRightPower / maxPower));
    }

    public void driveFieldRelative(double forward, double strafe, double rotate){
        double theta = Math.atan2(forward, strafe);
        double r = Math.hypot(strafe, forward);

        theta = AngleUnit.normalizeRadians(theta - imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS));

        double newForward = r * Math.sin(theta);
        double newStrafe = r * Math.cos(theta);

        this.drive(newForward, newStrafe, rotate);
    }

    public void goToPosition(double targetX, double targetY, double targetHeading){
        this.update();
        Pose2D currentPose = pinpoint.getPosition();

        double currentX = currentPose.getX(DistanceUnit.CM);
        double currentY = currentPose.getY(DistanceUnit.CM);
        double currentHeading = currentPose.getHeading(AngleUnit.DEGREES);

        // Calculo do erro
        double errorX = targetX - currentX;
        double errorY = targetY - currentY;
        double errorHeading = targetHeading - currentHeading;

        // Converte o erro para o referencial do robô
        double distance = Math.hypot(errorX, errorY);
        double angleToTarget = Math.atan2(errorY, errorX) - Math.toRadians(currentHeading);

        // Movimento no sistema do robô
        double forward = distance * Math.cos(angleToTarget);
        double strafe = distance * Math.sin(angleToTarget);

        // Controladores proporcionais
        double kP_lin = 0.05;
        double kP_rot = 0.02;

        double forwardPower = kP_lin * forward;
        double strafePower = kP_lin * strafe;
        double rotatePower = kP_rot * errorHeading;

        // Limitar potências
        forwardPower = Math.max(-1, Math.min(1, forwardPower));
        strafePower = Math.max(-1, Math.min(1, strafePower));
        rotatePower = Math.max(-1, Math.min(1, rotatePower));

        this.drive(forwardPower, strafePower, rotatePower);
    }

    public Pose2D getPose() {
        return pinpoint.getPosition();
    }
}

