package org.firstinspires.ftc.teamcode.Decode.Tsunami;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class TsunamiOuttake {
    // --- 1. CONSTANTES DE CALIBRAÇÃO ---
    private static final double TICKS_PER_REV = 28;
    private static final double RPM_TO_TICKS_PER_SEC = TICKS_PER_REV / 60.0;

    // Constantes PIDF (Exemplo - DEVE SER CALIBRADO)
    // Estes valores serão carregados no Control Hub
    private static final double P = 25.0; // Kp
    private static final double I = 0.0;  // Ki
    private static final double D = 0.0;  // Kd
    private static final double F = 17.2;  // Kf (Feedforward)

    // --- 2. VARIÁVEIS DE HARDWARE E CONTROLE ---
    private DcMotorEx outtakeL;
    private DcMotorEx outtakeR;
    private Servo servoOuttake;
    private Servo servoTurretL;
    private Servo servoTurretR;

    private double targetRPM = 0.0;
    public double targetVelocityTicks = 0.0; // Velocidade alvo em ticks/segundo



    public void init(HardwareMap hardwareMap){
        outtakeL = hardwareMap.get(DcMotorEx.class, "outtake_left");
        outtakeR = hardwareMap.get(DcMotorEx.class, "outtake_right");

        outtakeL.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        outtakeR.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

        outtakeL.setDirection(DcMotorSimple.Direction.REVERSE);

        PIDFCoefficients velocityPIDF = new PIDFCoefficients(P, I, D, F);
        outtakeL.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, velocityPIDF);
        outtakeR.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, velocityPIDF);


        servoOuttake = hardwareMap.get(Servo.class, "servo_outtake");
        servoOuttake.scaleRange(0.0, 0.15);
        servoOuttake.setPosition(0.0);

        servoTurretL = hardwareMap.get(Servo.class, "servo_turret_left");
        servoTurretR = hardwareMap.get(Servo.class, "servo_turret_right");
        servoTurretL.setDirection(Servo.Direction.REVERSE);
        servoTurretR.setDirection(Servo.Direction.REVERSE);
        servoTurretL.scaleRange(0.35, 0.63);
        servoTurretR.scaleRange(0.35, 0.63);
        servoTurretL.setPosition(0.5);
        servoTurretR.setPosition(0.5);
    }

    public double getVoltage(HardwareMap hardwareMap){
        double voltage = hardwareMap.voltageSensor.iterator().next().getVoltage();
        return voltage;
    }
    public void setTurretAngleX(double angleX){
        servoTurretL.setPosition(angleX);
        servoTurretR.setPosition(angleX);
    }

    public void setTurretAngleY(double angleY){
        servoOuttake.setPosition(angleY);
    }

    // Define a velocidade alvo da Flywheel em RPM.
    public void setTargetRPM(double rpm) {
        this.targetRPM = rpm;
        // Converte RPM para a unidade de controle (ticks/segundo)
        this.targetVelocityTicks = rpm * RPM_TO_TICKS_PER_SEC;

        // --- PASSO CRÍTICO: DEFINIR A VELOCIDADE ALVO ---
        outtakeL.setVelocity(targetVelocityTicks);
        outtakeR.setVelocity(targetVelocityTicks);
    }

    // Verifica se a Flywheel atingiu a velocidade alvo (dentro de uma tolerância).
    public boolean isAtTargetVelocity(double toleranceRPM) {
        double currentRPM = ((Math.abs(outtakeL.getVelocity()) + Math.abs(outtakeR.getVelocity())) / 2.0) / RPM_TO_TICKS_PER_SEC;
        return Math.abs(targetRPM - currentRPM) <= toleranceRPM;
    }

    public void showOuttakeTelemetry(Telemetry telemetry){
        double currentRPM = Math.abs(outtakeL.getVelocity()) / RPM_TO_TICKS_PER_SEC;

        telemetry.addData("Flywheel Target (RPM)", targetRPM);
        telemetry.addData("Flywheel Atual (RPM)", currentRPM);
        telemetry.addData("Flywheel Potência Aplicada ao motor Esquerdo", outtakeL.getPower());
        telemetry.addData("Flywheel Potência Aplicada ao motor Direita", outtakeR.getPower());
        telemetry.addData("Flywheel Erro (Ticks/s)", targetVelocityTicks - Math.abs(outtakeL.getVelocity()));
    }


    // Setar a velocidade do outtake manualmente
    public void setOuttakePower(double power){
        outtakeL.setPower(power);
        outtakeR.setPower(power);
    }

    // Para o motor da Flywheel
    public void stop() {
        setTargetRPM(0.0);
        outtakeL.setPower(0.0);
        outtakeR.setPower(0.0);
    }
}
