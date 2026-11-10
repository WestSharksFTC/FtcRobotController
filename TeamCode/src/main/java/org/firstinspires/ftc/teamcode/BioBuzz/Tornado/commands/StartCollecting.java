package org.firstinspires.ftc.teamcode.BioBuzz.Tornado.commands;

import com.seattlesolvers.solverslib.command.InstantCommand;

import org.firstinspires.ftc.teamcode.BioBuzz.Tornado.subsystems.indexer.Indexer;
import org.firstinspires.ftc.teamcode.BioBuzz.Tornado.subsystems.intake.Intake;

/** Liga intake e indexer para puxar pollens. */
public class StartCollecting extends InstantCommand {
    public StartCollecting(Intake intake, Indexer indexer) {
        super(() -> {
            intake.feed();
            indexer.feed();
        });
    }
}
