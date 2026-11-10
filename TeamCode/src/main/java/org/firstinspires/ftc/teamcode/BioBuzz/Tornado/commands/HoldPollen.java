package org.firstinspires.ftc.teamcode.BioBuzz.Tornado.commands;

import com.seattlesolvers.solverslib.command.InstantCommand;

import org.firstinspires.ftc.teamcode.BioBuzz.Tornado.subsystems.indexer.Indexer;
import org.firstinspires.ftc.teamcode.BioBuzz.Tornado.subsystems.intake.Intake;

/** Para o indexer e segura o intake em reverso (nao deixa pollen extra entrar). */
public class HoldPollen extends InstantCommand {
    public HoldPollen(Intake intake, Indexer indexer) {
        super(() -> {
            indexer.stop();
            intake.reverse();
        });
    }
}