package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuck.AutoSharks.Prompts;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class ValuePrompt implements Prompt {
    private double currentValue;
    private double min;
    private double max;
    private double step;

    public ValuePrompt(double initial, double min, double max, double step) {
        this.currentValue = initial;
        this.min = min;
        this.max = max;
        this.step = step;
    }

    @Override
    public void nextOption() {
        if (currentValue + step <= max) {
            currentValue += step;
        }
    }

    @Override
    public void previousOption() {
        if (currentValue - step >= min) {
            currentValue -= step;
        }
    }

    @Override
    public void display(Telemetry telemetry, String label) {
        telemetry.addData("--- CONFIGURAÇÃO ---", label);
        telemetry.addData("Valor Atual", "< " + currentValue + " >");
    }

    @Override
    public Object getSelectedValue() {
        return currentValue;
    }
}