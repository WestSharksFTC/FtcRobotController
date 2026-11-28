package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commandGroups;

import com.seattlesolvers.solverslib.command.ParallelCommandGroup;
import com.seattlesolvers.solverslib.command.ParallelDeadlineGroup;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;
import com.seattlesolvers.solverslib.command.WaitCommand;

import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.gate.GateClose;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.gate.GateOpen;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.indexer.IndexerReverse;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.indexer.IndexerStop;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.intake.IntakeReverse;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.intake.IntakeStop;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.gate.Gate;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.indexer.Indexer;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.intake.Intake;

public class ReverseCycleCommand extends SequentialCommandGroup {
    public ReverseCycleCommand(Intake intake, Indexer indexer, Gate gate) {
        addCommands(
                new GateOpen(gate),

                new ParallelDeadlineGroup(
                        new WaitCommand(1000),

                        new IntakeReverse(intake),
                        new IndexerReverse(indexer)
                ),

                new ParallelCommandGroup(
                        new GateClose(gate),
                        new AutoCollectCommand(intake, indexer, gate)
                )
        );

        addRequirements(intake, indexer, gate);
    }
}