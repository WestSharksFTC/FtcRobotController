package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuck.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.VoltageSensor;

public class WDOuttake {
    public DcMotorEx outtake;
    private VoltageSensor batteryVoltageSensor;
    private static final double NOMINAL_VOLTAGE = 12.0;
    private double baseKF = 20.75;

    public void init(HardwareMap hardwareMap){
        outtake = hardwareMap.get(DcMotorEx.class, "outtake");
        batteryVoltageSensor = hardwareMap.voltageSensor.iterator().next(); // Pega o sensor de voltagem

        outtake.setDirection(DcMotorEx.Direction.FORWARD);
        outtake.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        outtake.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);

        // Aplica o PIDF inicial
        updatePIDF(baseKF);
    }

    public void setVelocity(double targetVelocity){
        // Lê a voltagem atual da bateria em tempo real
        double currentVoltage = batteryVoltageSensor.getVoltage();

        // Evita divisão por zero por segurança
        if (currentVoltage <= 0) {
            currentVoltage = NOMINAL_VOLTAGE;
        }

        // Calcula o kF compensado: se a bateria cair, o kF sobe para compensar
        double adjustedKF = baseKF * (NOMINAL_VOLTAGE / currentVoltage);

        // Atualiza os coeficientes PIDF com o kF corrigido para a voltagem atual
        updatePIDF(adjustedKF);

        // Envia a velocidade para o motor
        outtake.setVelocity(targetVelocity);
    }

    private void updatePIDF(double kF) {
        // P(0), I(0), D(0), F(kF dinâmico) para RUN_USING_ENCODER
        outtake.setVelocityPIDFCoefficients(350.0, 0.0, 0.75, kF);
    }

    public double getVelocity(){
        return outtake.getVelocity();
    }

    public double getBatteryVoltage() {
        return batteryVoltageSensor.getVoltage();
    }
}