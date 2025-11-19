package org.firstinspires.ftc.teamcode.Decode.Tsunami;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

public class TsunamiChassis {
    // Motores
    private DcMotor frontLeftMotor, backLeftMotor, frontRightMotor, backRightMotor;

    // IMU
    public IMU imu;

    // Pinpoint
    private GoBildaPinpointDriver pinpoint;


    // Constantes de Controle (Ganhos P)
    private static final double KP_TRANSLATION = 0.02; // Ganho Proporcional para Translação (X e Y)
    private static final double KP_ROTATION = 0.01; // Ganho Proporcional para Rotação (Heading)

    // Tolerâncias para determinar quando o robô chegou ao destino
    private static final double TRANSLATION_TOLERANCE_CM = 3.0; // Tolerância de posição em cm
    private static final double HEADING_TOLERANCE_DEG = 2.0; // Tolerância de ângulo em graus

    public void init(HardwareMap hardwareMap){
        // Motores
        frontLeftMotor = hardwareMap.get(DcMotor.class, "front_left_motor");
        backLeftMotor = hardwareMap.get(DcMotor.class, "back_left_motor");
        frontRightMotor = hardwareMap.get(DcMotor.class, "front_right_motor");
        backRightMotor = hardwareMap.get(DcMotor.class, "back_right_motor");

        frontLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotor.Direction.REVERSE);

        frontLeftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        frontLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // IMU
        imu = hardwareMap.get(IMU.class, "imu");

        RevHubOrientationOnRobot RevOrientation = new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.RIGHT,
                RevHubOrientationOnRobot.UsbFacingDirection.UP
        );

        imu.initialize(new IMU.Parameters(RevOrientation));

        // Pinpoint
        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");

        pinpoint.setOffsets(190.0, -200.0, DistanceUnit.MM);

        pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_SWINGARM_POD);

        pinpoint.setEncoderDirections(
                GoBildaPinpointDriver.EncoderDirection.FORWARD,
                GoBildaPinpointDriver.EncoderDirection.REVERSED
        );

        pinpoint.resetPosAndIMU();

        pinpoint.setPosition(new Pose2D(DistanceUnit.CM, 0, 0, AngleUnit.DEGREES, 0));
    }


    public void update(){
        pinpoint.update();
    }

    public double getOdometryX(){
        double eixoX = pinpoint.getPosition().getX(DistanceUnit.CM);
        pinpoint.update();

        return eixoX;
    }

    public double getOdometryY(){
        double eixoY = pinpoint.getPosition().getY(DistanceUnit.CM);
        pinpoint.update();

        return eixoY;
    }

    public double getOdometryAngle(){
        double angulo = pinpoint.getPosition().getHeading(AngleUnit.DEGREES);
        pinpoint.update();

        return angulo;
    }

    public double getRobotAngle(){
        return imu.getRobotYawPitchRollAngles().getYaw();
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

    public void driveFieldRelative(double forward, double strafe, double rotate, boolean imuReset){
        // Reseta o yaw da IMU se imuReset for verdadeiro.
        if(imuReset){
            imu.resetYaw();
        }

        double theta = Math.atan2(forward, strafe);
        double r = Math.hypot(strafe, forward);

        theta = AngleUnit.normalizeRadians(theta - imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS));

        double newForward = r * Math.sin(theta);
        double newStrafe = r * Math.cos(theta);

        this.drive(newForward, newStrafe, rotate);
    }

    public boolean goToPoint(double xTarget, double yTarget, double headingTarget) {
        double currentX = this.getOdometryX();
        double currentY = this.getOdometryY();
        double currentHeading = Math.toRadians(this.getOdometryAngle());

        double xError = xTarget - currentX;
        double yError = yTarget - currentY;
        double hError = headingTarget - currentHeading;

        double kP = 0.03;

        double xPower = xError * kP;
        double yPower = yError * kP;
        double turnPower = hError * 0.015;

        this.drive(xPower, yPower, turnPower);

        double dist = Math.hypot(xError, yError);

        return dist < 1.0 && Math.abs(hError) < Math.toRadians(5);
    }

}