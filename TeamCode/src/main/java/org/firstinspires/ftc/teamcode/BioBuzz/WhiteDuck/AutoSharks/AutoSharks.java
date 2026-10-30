package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuck.AutoSharks;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Gamepad;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuck.AutoSharks.Prompts.Prompt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AutoSharks {
    private Telemetry telemetry;
    private Gamepad gamepad;

    private List<Prompt> prompts = new ArrayList<>();
    private List<String> keys = new ArrayList<>();
    private List<String> labels = new ArrayList<>();
    private Map<String, Object> results = new HashMap<>();

    private int currentIndex = 0;
    private boolean isFinished = false;
    private String currentLabel = "";

    public AutoSharks(OpMode opMode) {
        this.telemetry = opMode.telemetry;
        this.gamepad = opMode.gamepad1;
    }

    public AutoSharks prompt(String key, Prompt prompt) {
        prompts.add(prompt);
        keys.add(key);
        labels.add(key); // Label padrão igual à chave
        return this;
    }

    public AutoSharks label(String labelText) {
        if (!labels.isEmpty()) {
            labels.set(labels.size() - 1, labelText);
        }
        return this;
    }

    public void run() {
        if (isFinished) {
            telemetry.addData("Status", "Configurações Salvas com Sucesso! Aguardando Start...");
            telemetry.update();
            return;
        }

        if (prompts.isEmpty()) return;

        Prompt currentPrompt = prompts.get(currentIndex);
        currentLabel = labels.get(currentIndex);

        // Navegação usando os métodos nativos do SDK que você mencionou
        if (gamepad.dpadUpWasPressed() || gamepad.dpadLeftWasPressed()) {
            currentPrompt.nextOption();
        }
        if (gamepad.dpadDownWasPressed() || gamepad.dpadRightWasPressed()) {
            currentPrompt.previousOption();
        }

        // Confirmar com o botão A
        if (gamepad.aWasPressed()) {
            results.put(keys.get(currentIndex), currentPrompt.getSelectedValue());
            currentIndex++;
            if (currentIndex >= prompts.size()) {
                isFinished = true;
            }
        }

        // Voltar com o botão B
        if (gamepad.bWasPressed() && currentIndex > 0) {
            currentIndex--;
        }

        // Exibir na tela
        telemetry.addData("=== AUTOSHARKS MENU ===", "Passo " + (currentIndex + 1) + "/" + prompts.size());
        currentPrompt.display(telemetry, currentLabel);
        telemetry.addData("Controles", "D-Pad: Mudar | A: Confirmar | B: Voltar");
        telemetry.update();
    }

    public boolean isFinished() {
        return isFinished;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) results.get(key);
    }
}