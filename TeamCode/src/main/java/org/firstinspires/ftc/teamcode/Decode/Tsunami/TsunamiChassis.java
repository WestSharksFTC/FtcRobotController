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
                GoBildaPinpointDriver.EncoderDirection.FORWARD
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

    public void goToPosition(double targetX, double targetY, double targetHeading, boolean opModeIsActive) {

        // Loop de controle
        while (opModeIsActive) {
            // 1. Atualiza a posição atual
            this.update();
            Pose2D currentPose = pinpoint.getPosition();

            double currentX = currentPose.getX(DistanceUnit.CM);
            double currentY = currentPose.getY(DistanceUnit.CM);
            double currentHeading = currentPose.getHeading(AngleUnit.DEGREES);

            // 2. Cálculo do Erro de Translação
            double errorX = targetX - currentX;
            double errorY = targetY - currentY;

            // Distância até o alvo
            double distance = Math.hypot(errorX, errorY);

            // Verifica se chegamos ao destino (Tolerância de Translação)
            if (distance < TRANSLATION_TOLERANCE_CM) {
                // Se a translação estiver completa, verificamos a rotação
                double errorHeading = AngleUnit.normalizeDegrees(targetHeading - currentHeading);

                if (Math.abs(errorHeading) < HEADING_TOLERANCE_DEG) {
                    // Parar o robô e sair do loop
                    drive(0, 0, 0);
                    break;
                }

                // Se a translação estiver completa, mas a rotação não, focamos apenas na rotação
                double rotatePower = KP_ROTATION * errorHeading;
                rotatePower = Math.max(-1, Math.min(1, rotatePower));
                drive(0, 0, rotatePower);
                continue; // Pula para a próxima iteração para continuar a rotação
            }

            // 3. Cálculo do Ângulo de Movimento (Field-Relative)
            // Este é o ângulo que o robô deve apontar para o alvo (em relação ao campo)
            double angleToTarget = Math.atan2(errorY, errorX); // Em radianos

            // 4. Conversão do Ângulo de Movimento para o Sistema do Robô
            // O robô precisa se mover na direção do alvo, mas o cálculo de potência
            // deve ser feito no sistema do robô (forward/strafe).
            double movementAngle = AngleUnit.normalizeRadians(angleToTarget - Math.toRadians(currentHeading));

            // 5. Cálculo da Potência de Translação (P-Controller)
            // A potência é proporcional à distância restante
            double drivePower = distance * KP_TRANSLATION;

            // Limita a potência máxima (ex: 0.8)
            drivePower = Math.min(drivePower, 0.5);

            // 6. Projeção da Potência no Sistema do Robô
            double forwardPower = drivePower * Math.sin(movementAngle);
            double strafePower = drivePower * Math.cos(movementAngle);

            // 7. Cálculo da Potência de Rotação (P-Controller)
            // O robô tenta manter o ângulo final desejado (targetHeading) enquanto se move.
            double errorHeadingRad = AngleUnit.normalizeRadians(Math.toRadians(targetHeading) - Math.toRadians(currentHeading));
            double rotatePower = KP_ROTATION * errorHeadingRad;

            // 8. Limitar e Aplicar Potências
            forwardPower = Math.max(-1, Math.min(1, forwardPower));
            strafePower = Math.max(-1, Math.min(1, strafePower));
            rotatePower = Math.max(-1, Math.min(1, rotatePower));

            this.drive(forwardPower, strafePower, rotatePower);
        }

        // Parar o robô após sair do loop
        drive(0, 0, 0);
    }

    public Pose2D getPose() {
        return pinpoint.getPosition();
    }
}