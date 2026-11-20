package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commandGroups;

import com.seattlesolvers.solverslib.command.ParallelCommandGroup;

import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.gate.Gate;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.indexer.Indexer;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.intake.Intake;

import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.gate.GateCommand;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.indexer.IndexerCommand;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.intake.IntakeCommand;

public class CollectCommandGroup extends ParallelCommandGroup {
    public CollectCommandGroup(Gate gate, Intake intake, Indexer indexer){
        addCommands(
                new GateCommand(gate, () -> false),

                new IntakeCommand(
                        intake,
                        () -> false,
                        () -> !indexer.isIndexerFull(),
                        () -> false,
                        () -> indexer.isIndexerFull()
                ),

                new IndexerCommand(
                        indexer,
                        () -> false,
                        () -> !indexer.isIndexerTopFull(),
                        () -> false,
                        () -> indexer.isIndexerTopFull()
                )
        );
    }
}