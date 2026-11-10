package org.firstinspires.ftc.teamcode.AutoSharks.Prompts;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public interface Prompt {
    void nextOption();
    void previousOption();
    void display(Telemetry telemetry, String label);
    Object getSelectedValue();
}