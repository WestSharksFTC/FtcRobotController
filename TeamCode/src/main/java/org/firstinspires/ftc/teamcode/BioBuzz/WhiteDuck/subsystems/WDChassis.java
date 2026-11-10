package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuck.subsystems;

import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

public class WDChassis {
    // Motores
    public DcMotor frontLeftMotor, backLeftMotor, frontRightMotor, backRightMotor;

    // IMU
    public IMU imu;

    public Follower follower;
    private final PoseFactory poseFactory = PoseFactory.degrees();
    private final Pose parkPosition = poseFactory.of(10, 95, 0);

    // Limite de velocidade global
    private static final double MAX_SPEED = 0.8;

    public void init(HardwareMap hardwareMap) {
        // Inicializa o Follower do Pedro Pathing
        follower = Constants.create(hardwareMap);

        // Mapeamento dos motores
        frontLeftMotor = hardwareMap.get(DcMotor.class, "lf");
        backLeftMotor = hardwareMap.get(DcMotor.class, "lb");
        frontRightMotor = hardwareMap.get(DcMotor.class, "rf");
        backRightMotor = hardwareMap.get(DcMotor.class, "rb");

        // Direções dos motores
        frontLeftMotor.setDirection(DcMotor.Direction.FORWARD);
        backLeftMotor.setDirection(DcMotor.Direction.FORWARD);
        frontRightMotor.setDirection(DcMotor.Direction.REVERSE);
        backRightMotor.setDirection(DcMotor.Direction.REVERSE);

        // Sem encoders para o controle manual dos motores
        frontLeftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Frenagem ativa dos motores
        frontLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Configurações da IMU
        imu = hardwareMap.get(IMU.class, "imu");

        RevHubOrientationOnRobot RevOrientation = new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.LEFT,
                RevHubOrientationOnRobot.UsbFacingDirection.UP
        );

        imu.initialize(new IMU.Parameters(RevOrientation));
    }

    // Controle básico do chassis (robot-centric)
    public void drive(double forward, double strafe, double rotate) {
        // Equações padrão das rodas Mecanum
        double frontLeftPower  = forward + strafe + rotate;
        double backLeftPower   = forward - strafe + rotate;
        double frontRightPower = forward - strafe - rotate;
        double backRightPower  = forward + strafe - rotate;

        // Encontra a maior potência absoluta para normalizar se passar de 1.0
        double max = Math.max(1.0, Math.max(
                Math.max(Math.abs(frontLeftPower), Math.abs(backLeftPower)),
                Math.max(Math.abs(frontRightPower), Math.abs(backRightPower))
        ));

        // Aplica a normalização e limita pela velocidade máxima desejada
        frontLeftMotor.setPower((frontLeftPower / max) * MAX_SPEED);
        backLeftMotor.setPower((backLeftPower / max) * MAX_SPEED);
        frontRightMotor.setPower((frontRightPower / max) * MAX_SPEED);
        backRightMotor.setPower((backRightPower / max) * MAX_SPEED);
    }

    // Controle do chassis relativo ao campo (field-centric)
    public void driveFieldRelative(double forward, double strafe, double rotate) {
        double theta = Math.atan2(strafe, forward);
        double r = Math.hypot(strafe, forward);

        // Subtrai a orientação atual da IMU do ângulo de deslocamento
        theta = AngleUnit.normalizeRadians(theta - imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS));

        double newForward = r * Math.cos(theta);
        double newStrafe = r * Math.sin(theta);

        this.drive(newForward, newStrafe, rotate);
    }

    // Retorna o ângulo da IMU em graus
    public double getImuYaw() {
        return imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
    }

    public void resetYaw() {
        imu.resetYaw();
    }

    public Path parkPosTeleOp() {
        Pose currentPose = follower.pose();
        return Paths.line(currentPose, parkPosition).linear(currentPose, parkPosition);
    }
}