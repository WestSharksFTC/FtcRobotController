package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commandGroups;

import com.seattlesolvers.solverslib.command.ParallelCommandGroup;
import com.seattlesolvers.solverslib.command.ParallelDeadlineGroup;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;
import com.seattlesolvers.solverslib.command.WaitCommand;

import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.gate.GateClose;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.gate.GateOpen;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.indexer.IndexerStop;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.indexer.IndexerTransfer;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.intake.IntakeStop;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.intake.IntakeTransfer;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.gate.Gate;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.indexer.Indexer;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.intake.Intake;

public class TransferCycleCommand extends SequentialCommandGroup {
    public TransferCycleCommand(Intake intake, Indexer indexer, Gate gate) {
        addCommands(
                new SequentialCommandGroup(
                        new GateOpen(gate),
                        new WaitCommand(250)
                ),

                new ParallelDeadlineGroup(
                        new WaitCommand(3000),

                        new IntakeTransfer(intake),
                        new IndexerTransfer(indexer)
                ),

                new ParallelCommandGroup(
                        new IntakeStop(intake),
                        new IndexerStop(indexer),
                        new GateClose(gate)
                )
        );

        addRequirements(intake, indexer);
    }
}