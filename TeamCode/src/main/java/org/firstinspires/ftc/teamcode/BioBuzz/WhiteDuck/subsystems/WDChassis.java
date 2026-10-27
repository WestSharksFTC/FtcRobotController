package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuck.subsystems;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class WDChassis {
    // Motores
    public DcMotor frontLeftMotor, backLeftMotor, frontRightMotor, backRightMotor;

    // IMU
    public IMU imu;

    public void init(HardwareMap hardwareMap){
        // Motores
        frontLeftMotor = hardwareMap.get(DcMotor.class, "lf");
        backLeftMotor = hardwareMap.get(DcMotor.class, "lb");
        frontRightMotor = hardwareMap.get(DcMotor.class, "rf");
        backRightMotor = hardwareMap.get(DcMotor.class, "rb");

        frontLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotor.Direction.FORWARD);
        frontRightMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        backRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);

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
                RevHubOrientationOnRobot.LogoFacingDirection.LEFT,
                RevHubOrientationOnRobot.UsbFacingDirection.UP
        );

        imu.initialize(new IMU.Parameters(RevOrientation));
    }

    //Controle básico do chassis (robot-centric)
    public void drive(double forward, double strafe, double rotate){
        double frontRightPower = forward + strafe + rotate;
        double backRightPower = forward + strafe - rotate;
        double frontLeftPower = forward - strafe - rotate;
        double backLeftPower = forward - strafe + rotate;

        double maxPower = 1.0;
        double maxSpeed = 0.8;

        maxPower = Math.max(maxPower, Math.abs(frontLeftPower));
        maxPower = Math.max(maxPower, Math.abs(backLeftPower));
        maxPower = Math.max(maxPower, Math.abs(frontRightPower));
        maxPower = Math.max(maxPower, Math.abs(backRightPower));

        frontLeftMotor.setPower(maxSpeed * (frontLeftPower / maxPower));
        backLeftMotor.setPower(maxSpeed * (backLeftPower / maxPower));
        frontRightMotor.setPower(maxSpeed * (frontRightPower / maxPower));
        backRightMotor.setPower(maxSpeed * (backRightPower / maxPower));
    }

    //Controle field-centric (relativo ao campo)
    public void driveFieldRelative(double forward, double strafe, double rotate){
        double theta = Math.atan2(strafe, forward);
        double r = Math.hypot(strafe, forward);

        theta = AngleUnit.normalizeRadians(theta - imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS));

        double newForward = r * Math.cos(theta);
        double newStrafe = r * Math.sin(theta);

        this.drive(newForward, newStrafe, rotate);
    }

    //Retorna ângulo da IMU (para field-centric)
    public double getImuYaw() {
        return imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
    }

    public void resetYaw() {
        imu.resetYaw();
    }
}