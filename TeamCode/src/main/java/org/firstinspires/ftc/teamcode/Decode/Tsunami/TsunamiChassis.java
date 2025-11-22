package org.firstinspires.ftc.teamcode.Decode.Tsunami;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

public class TsunamiChassis {
    // Motores
    private DcMotor frontLeftMotor, backLeftMotor, frontRightMotor, backRightMotor;

    // IMU
    public IMU imu;

    // Pinpoint
    private GoBildaPinpointDriver pinpoint;

    // Tolerâncias
    private static final double POSITION_TOLERANCE = 5.0; // 5cm
    private static final double HEADING_TOLERANCE = 5.0;  // 5 graus

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

        pinpoint.setOffsets(-160.0, -85.0, DistanceUnit.MM);

        pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_SWINGARM_POD);

        pinpoint.setEncoderDirections(
                GoBildaPinpointDriver.EncoderDirection.FORWARD,
                GoBildaPinpointDriver.EncoderDirection.REVERSED
        );

        pinpoint.resetPosAndIMU();

        pinpoint.setPosition(new Pose2D(DistanceUnit.CM, 0, 0, AngleUnit.DEGREES, 0));
    }

    //Atualiza a odometria
    public void update(){
        pinpoint.update();
    }

    //Controle básico do chassis (robot-centric)
    public void drive(double forward, double strafe, double rotate){
        double frontLeftPower = forward + strafe + rotate;
        double backLeftPower = forward - strafe + rotate;
        double frontRightPower = forward - strafe - rotate;
        double backRightPower = forward + strafe - rotate;

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
        double theta = Math.atan2(forward, strafe);
        double r = Math.hypot(strafe, forward);

        theta = AngleUnit.normalizeRadians(theta - imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS));

        double newForward = r * Math.sin(theta);
        double newStrafe = r * Math.cos(theta);

        this.drive(newForward, newStrafe, rotate);
    }

    //Vai para uma posição específica (NÃO BLOQUEANTE)
    //Retorna TRUE quando chegou, FALSE enquanto está indo
    public boolean goToPosition(double targetX, double targetY, double targetHeading) {
        this.update(); // Atualiza odometria

        double currentX = this.getX();
        double currentY = this.getY();
        double currentHeading = this.getHeading();

        // Calcula erros
        double errorX = targetX - currentX;    // Erro em X (lateral)
        double errorY = targetY - currentY;    // Erro em Y (frente)
        double errorHeading = normalizeAngle(targetHeading - currentHeading);

        double distance = Math.hypot(errorX, errorY);

        // ========== VERIFICA SE CHEGOU ==========
        if (distance < POSITION_TOLERANCE && Math.abs(errorHeading) < HEADING_TOLERANCE) {
            this.drive(0, 0, 0);
            return true; // CHEGOU!
        }

        double forwardPower = 0;
        double strafePower = 0;
        double rotatePower = 0;

        // ========== ESTRATÉGIA: PRIMEIRO HEADING, DEPOIS POSIÇÃO ==========

        if (Math.abs(errorHeading) > 10) {
            // SÓ CORRIGE HEADING
            rotatePower = errorHeading * 0.01;
            rotatePower = clamp(rotatePower, -0.3, 0.3);

        } else {
            // MOVE COM ANTI-OSCILAÇÃO

            // Ganho proporcional
            double kP = 0.02;  // Ajuste esse valor!

            forwardPower = errorY * kP;
            strafePower = errorX * kP;

            // ========== ANTI-OSCILAÇÃO ==========

            // 1. VELOCIDADE MÍNIMA (senão robô não se move)
            double minPower = 0.15;

            if (Math.abs(forwardPower) > 0.01 && Math.abs(forwardPower) < minPower) {
                forwardPower = Math.signum(forwardPower) * minPower;
            }
            if (Math.abs(strafePower) > 0.01 && Math.abs(strafePower) < minPower) {
                strafePower = Math.signum(strafePower) * minPower;
            }

            // 2. DESACELERAÇÃO PRÓXIMO AO ALVO
            if (distance < 30) {  // Quando está a menos de 30cm
                double slowFactor = distance / 30.0;  // 0.0 a 1.0
                slowFactor = Math.max(slowFactor, 0.3);  // Mínimo 30% da velocidade

                forwardPower *= slowFactor;
                strafePower *= slowFactor;
            }

            // 3. ZONA MORTA - Para quando muito perto
            if (distance < 3) {
                forwardPower = 0;
                strafePower = 0;
            }

            // Correção suave de heading
            rotatePower = errorHeading * 0.008;

            // Limita
            forwardPower = clamp(forwardPower, -0.6, 0.6);
            strafePower = clamp(strafePower, -0.6, 0.6);
            rotatePower = clamp(rotatePower, -0.3, 0.3);
        }

        this.drive(forwardPower, strafePower, rotatePower);
        return false; // Ainda não chegou
    }

    //Normaliza ângulo para -180 a 180 graus
    private double normalizeAngle(double angle) {
        while (angle > 180) angle -= 360;
        while (angle < -180) angle += 360;
        return angle;
    }

    //Limita valor entre min e max
    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    //Reseta a pose do Pinpoint para uma posição específica
    public void resetPose(double x, double y, double heading) {
        pinpoint.setPosition(new Pose2D(DistanceUnit.CM, x, y, AngleUnit.DEGREES, heading));
    }

    //Retorna a pose atual do robô
    public Pose2D getPose() {
        return pinpoint.getPosition();
    }

    //Retorna X em centímetros
    public double getX() {
        this.update();
        return pinpoint.getPosition().getX(DistanceUnit.CM);
    }

    //Retorna Y em centímetros
    public double getY() {
        this.update();
        return pinpoint.getPosition().getY(DistanceUnit.CM);
    }

    //Retorna heading em graus
    public double getHeading() {
        this.update();
        return pinpoint.getPosition().getHeading(AngleUnit.DEGREES);
    }

    //Retorna ângulo da IMU (para field-centric)
    public double getImuYaw() {
        return imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
    }
}