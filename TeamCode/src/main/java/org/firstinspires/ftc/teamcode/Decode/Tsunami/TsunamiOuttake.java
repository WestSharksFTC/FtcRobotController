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
    private static final double P = 0.0; // Kp
    private static final double I = 0.0;  // Ki
    private static final double D = 0.0;  // Kd
    private static final double F = 0.0;  // Kf (Feedforward)

    // --- 2. VARIÁVEIS DE HARDWARE E CONTROLE ---
    private DcMotorEx outtakeL;
    private DcMotorEx outtakeR;
    private Servo servoOuttake;

    private double targetRPM = 0.0;
    public double targetVelocityTicks = 0.0; // Velocidade alvo em ticks/segundo



    public void init(HardwareMap hardwareMap){
        outtakeL = hardwareMap.get(DcMotorEx.class, "outtake_left");
        outtakeR = hardwareMap.get(DcMotorEx.class, "outtake_right");

        outtakeL.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        outtakeR.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

        outtakeL.setDirection(DcMotorSimple.Direction.REVERSE);

        //PIDFCoefficients velocityPIDF = new PIDFCoefficients(P, I, D, F);
        //outtakeL.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, velocityPIDF);
        //outtakeR.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, velocityPIDF);


        servoOuttake = hardwareMap.get(Servo.class, "servo_outtake");
        servoOuttake.scaleRange(0.0, 0.2);
        servoOuttake.setPosition(0.0);
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
        double currentRPM = outtakeL.getVelocity() / RPM_TO_TICKS_PER_SEC;
        return Math.abs(targetRPM - currentRPM) <= toleranceRPM;
    }

    public void showOuttakeTelemetry(Telemetry telemetry){
        double currentRPM = outtakeL.getVelocity() / RPM_TO_TICKS_PER_SEC;

        telemetry.addData("Flywheel Target (RPM)", targetRPM);
        telemetry.addData("Flywheel Atual (RPM)", currentRPM);
        telemetry.addData("Flywheel Potência Aplicada ao motor Esquerdo", outtakeL.getPower());
        telemetry.addData("Flywheel Potência Aplicada ao motor Direita", outtakeR.getPower());
        telemetry.addData("Flywheel Erro (Ticks/s)", targetVelocityTicks - outtakeL.getVelocity());
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
