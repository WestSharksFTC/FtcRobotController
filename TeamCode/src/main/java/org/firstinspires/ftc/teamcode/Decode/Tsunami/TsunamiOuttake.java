package org.firstinspires.ftc.teamcode.Decode.Tsunami;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class TsunamiOuttake {

    // CORREÇÃO: Constantes de conversão de RPM
    private static final double TICKS_PER_REV = 28;  // Para motores REV HD Hex
    // Para converter RPM para ticks/segundo: RPM * TICKS_PER_REV / 60
    private static final double RPM_TO_TICKS_PER_SEC = TICKS_PER_REV / 60.0;  // = 0.4667

    // --- 2. VARIÁVEIS DE HARDWARE E CONTROLE ---
    private DcMotorEx outtakeL;
    private DcMotorEx outtakeR;
    private Servo servoOuttake;
    private Servo servoTurretL;
    private Servo servoTurretR;

    private double targetRPM = 0.0;

    // Variáveis para controle PID
    private double lastError = 0.0;
    private double integralSum = 0.0;
    private long lastUpdateTime = 0;


    public void init(HardwareMap hardwareMap){
        outtakeL = hardwareMap.get(DcMotorEx.class, "outtake_left");
        outtakeR = hardwareMap.get(DcMotorEx.class, "outtake_right");

        outtakeL.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        outtakeR.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

        outtakeL.setDirection(DcMotorSimple.Direction.REVERSE);

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

        lastUpdateTime = System.nanoTime();
    }

    public double getVoltage(HardwareMap hardwareMap){
        double voltage = hardwareMap.voltageSensor.iterator().next().getVoltage();
        return voltage;
    }

    public double clamp(double v, double a, double b) {
        return Math.max(a, Math.min(b, v));
    }

    public double getServoTurretXCurrentPosition(){
        double servoPos = (servoTurretL.getPosition() + servoTurretR.getPosition()) / 2.0;
        return servoPos;
    }

    public void setTurretAngleX(double angleX){
        servoTurretL.setPosition(angleX);
        servoTurretR.setPosition(angleX);
    }

    public void setTurretAngleY(double angleY){
        servoOuttake.setPosition(angleY);
    }

    /**
     * MÉTODO PRINCIPAL DE CONTROLE - CORRIGIDO
     * Este método deve ser chamado continuamente no loop()
     */
    public void updateShooterControl() {
        long currentTime = System.nanoTime();
        double dt = (currentTime - lastUpdateTime) / 1.0e9; // Converter para segundos
        lastUpdateTime = currentTime;

        double currentRPM = getCurrentRPM();
        double error = targetRPM - currentRPM;

        // Ganhos PID ajustados
        double kP = 0.003;   // Aumentado significativamente (era 0.0005)
        double kI = 0.0001;  // Pequeno termo integral
        double kD = 0.0002;  // Pequeno termo derivativo

        // Termo Feedforward - essencial para flywheels!
        // F = targetRPM / maxRPM
        double maxRPM = 6000.0;  // Ajuste conforme seu motor
        double kF = 1.0 / maxRPM;
        double feedforward = targetRPM * kF;

        // Cálculo PID
        integralSum += error * dt;

        // Anti-windup: limita o integral
        integralSum = clamp(integralSum, -100, 100);

        double derivative = (error - lastError) / dt;
        lastError = error;

        double pidOutput = kP * error + kI * integralSum + kD * derivative;

        // Potência total = Feedforward + PID
        double power = feedforward + pidOutput;

        // IMPORTANTE: Não limitar apenas entre 0 e 1!
        // Se o RPM está alto demais, precisamos frear (potência menor)
        power = clamp(power, -0.1, 1.0);  // Permite pequena frenagem

        setShooterPower(power);
    }

    /**
     * Calcula o RPM atual dos motores
     * CORREÇÃO: Conversão correta de velocity (ticks/seg) para RPM
     */
    public double getCurrentRPM() {
        // getVelocity() retorna ticks por segundo
        double avgVelocityTicksPerSec = (outtakeL.getVelocity() + outtakeR.getVelocity()) / 2.0;

        // Converter ticks/segundo para RPM
        // RPM = (ticks/sec) * (60 sec/min) / (ticks/rev)
        double rpm = Math.abs(avgVelocityTicksPerSec * 60.0 / TICKS_PER_REV);

        return rpm;
    }

    public void setTargetRPM(double rpm){
        targetRPM = rpm;
        // Reset do controle quando muda target
        integralSum = 0;
        lastError = 0;
    }

    public boolean isAtTargetRPM(double tolerance) {
        double currentRPM = getCurrentRPM();
        return Math.abs(currentRPM - targetRPM) <= tolerance;
    }

    public void setShooterPower(double power) {
        outtakeL.setPower(power);
        outtakeR.setPower(power);
    }

    public void showOuttakeTelemetry(Telemetry telemetry){
        double currentRPM = getCurrentRPM();
        double error = targetRPM - currentRPM;
        double currentPower = (outtakeL.getPower() + outtakeR.getPower()) / 2.0;

        telemetry.addData("Flywheel Atual (RPM)", "%.1f", currentRPM);
        telemetry.addData("Flywheel Objetivo (RPM)", "%.1f", targetRPM);
        telemetry.addData("Flywheel Potência Motor Esquerdo", "%.3f", outtakeL.getPower());
        telemetry.addData("Flywheel Potência Motor Direito", "%.3f", outtakeR.getPower());
        telemetry.addData("Flywheel Erro (RPM)", "%.1f", error);
        telemetry.addData("Flywheel Velocidade L (ticks/s)", "%.1f", outtakeL.getVelocity());
        telemetry.addData("Flywheel Velocidade R (ticks/s)", "%.1f", outtakeR.getVelocity());
    }

    // Setar a velocidade do outtake manualmente
    public void setOuttakePower(double power){
        outtakeL.setPower(power);
        outtakeR.setPower(power);
    }
}