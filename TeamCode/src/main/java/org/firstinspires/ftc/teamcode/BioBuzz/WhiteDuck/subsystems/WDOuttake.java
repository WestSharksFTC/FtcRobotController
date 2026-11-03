package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuck.subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.Range;

public class WDOuttake {
    public DcMotorEx outtake;
    private VoltageSensor batteryVoltageSensor;
    private static final double NOMINAL_VOLTAGE = 12.0;
    private final double baseKF = 20.75;

    // Variável para armazenar a última velocidade alvo definida
    private double targetVelocity = 0.0;

    public void init(HardwareMap hardwareMap) {
        outtake = hardwareMap.get(DcMotorEx.class, "outtake");
        batteryVoltageSensor = hardwareMap.voltageSensor.iterator().next();

        outtake.setDirection(DcMotorEx.Direction.FORWARD);
        outtake.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        outtake.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);

        updatePIDF(baseKF);
    }

    public void setVelocity(double targetVelocity) {
        this.targetVelocity = targetVelocity; // Salva o alvo para consulta posterior
        double currentVoltage = getBatteryVoltage();

        if (currentVoltage <= 0) {
            currentVoltage = NOMINAL_VOLTAGE;
        }

        // Compensação de voltagem da bateria aplicada ao kF
        double adjustedKF = baseKF * (NOMINAL_VOLTAGE / currentVoltage);

        updatePIDF(adjustedKF);

        outtake.setVelocity(targetVelocity);
    }

    // TODO Cálculo inteligente para ajustar a velocidade automaticamente com base no ty da Limelight
    public void setVelocityFromLimelight(double currentTy) {
        // Trava o ty entre os limites mínimo e máximo esperados da sua calibração
        double clampedTy = Range.clip(currentTy, -10.0, 15.0);

        // Mapeia o ty para a velocidade correspondente do outtake
        double calculatedVelocity = Range.scale(clampedTy, -10.0, 15.0, 1000.0, 760.0);

        setVelocity(calculatedVelocity);
    }

    public void stop() {
        targetVelocity = 0.0;
        outtake.setPower(0.0);
    }

    private void updatePIDF(double kF) {
        // P, I, D, F
        outtake.setVelocityPIDFCoefficients(250.0, 0.0, 0.0, kF);
    }

    public double getVelocity() {
        return outtake.getVelocity();
    }

    // Retorna a velocidade que foi mandada o outtake alcançar
    public double getTargetVelocity() {
        return targetVelocity;
    }

    public double getBatteryVoltage() {
        return batteryVoltageSensor.getVoltage();
    }
}