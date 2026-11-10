package org.firstinspires.ftc.teamcode.BioBuzz.Tornado.commands;

import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;
import com.seattlesolvers.solverslib.command.WaitCommand;
import com.seattlesolvers.solverslib.command.WaitUntilCommand;

import org.firstinspires.ftc.teamcode.BioBuzz.Tornado.subsystems.indexer.Indexer;
import org.firstinspires.ftc.teamcode.BioBuzz.Tornado.subsystems.outtake.Outtake;
import org.firstinspires.ftc.teamcode.BioBuzz.Tornado.subsystems.turret.Turret;

/**
 * Atira tudo: liga o outtake (velocidade pelo ty), espera chegar na velocidade (com timeout),
 * abre o portao e alimenta, espera, fecha e desliga o outtake.
 * Mesma sequencia que o autonomo antigo fazia com waitMs.
 */
public class ShootAll extends SequentialCommandGroup {
    public ShootAll(Outtake outtake, Indexer indexer, Turret turret) {
        super(
                new InstantCommand(() -> {
                    indexer.closeGate();
                    indexer.stop();
                    outtake.spin(turret::getTy);
                }),
                new WaitUntilCommand(outtake::atSpeed).withTimeout(CommandConstants.SPINUP_TIMEOUT_MS),
                new InstantCommand(() -> {
                    indexer.openGate();
                    indexer.feed();
                }),
                new WaitCommand(CommandConstants.FIRE_MS),
                new InstantCommand(() -> {
                    indexer.closeGate();
                    outtake.stop();
                })
        );
    }
}
