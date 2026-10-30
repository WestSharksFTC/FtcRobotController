package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuck.AutoSharks.Prompts;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class BooleanPrompt implements Prompt {
    private boolean value;

    public BooleanPrompt(boolean initialValue) {
        this.value = initialValue;
    }

    @Override
    public void nextOption() {
        value = !value;
    }

    @Override
    public void previousOption() {
        value = !value;
    }

    @Override
    public void display(Telemetry telemetry, String label) {
        telemetry.addData("--- CONFIGURAÇÃO ---", label);
        telemetry.addData("Selecionado", value ? "< SIM (true) >" : "< NÃO (false) >");
    }

    @Override
    public Object getSelectedValue() {
        return value;
    }
}