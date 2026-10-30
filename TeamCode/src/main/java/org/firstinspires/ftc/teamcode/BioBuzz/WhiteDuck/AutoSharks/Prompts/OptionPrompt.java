package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuck.AutoSharks.Prompts;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class OptionPrompt<T> implements Prompt {
    private T[] options;
    private int currentIndex = 0;

    @SafeVarargs
    public OptionPrompt(T... options) {
        this.options = options;
    }

    @Override
    public void nextOption() {
        currentIndex = (currentIndex + 1) % options.length;
    }

    @Override
    public void previousOption() {
        currentIndex = (currentIndex - 1 + options.length) % options.length;
    }

    @Override
    public void display(Telemetry telemetry, String label) {
        telemetry.addData("--- CONFIGURAÇÃO ---", label);
        for (int i = 0; i < options.length; i++) {
            String indicator = (i == currentIndex) ? "==> " : "    ";
            telemetry.addData(indicator, options[i].toString());
        }
    }

    @Override
    public Object getSelectedValue() {
        return options[currentIndex];
    }
}