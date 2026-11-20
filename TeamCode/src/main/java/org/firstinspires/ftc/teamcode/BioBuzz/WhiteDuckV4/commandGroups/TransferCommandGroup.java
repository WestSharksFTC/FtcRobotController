package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commandGroups;

import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.ParallelDeadlineGroup;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;
import com.seattlesolvers.solverslib.command.WaitCommand;

import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.gate.Gate;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.indexer.Indexer;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.intake.Intake;

import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.gate.GateCommand;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.indexer.IndexerCommand;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.intake.IntakeCommand;

public class TransferCommandGroup extends SequentialCommandGroup {
    public TransferCommandGroup(Gate gate, Intake intake, Indexer indexer, double transferTime){
        addCommands(
                new ParallelDeadlineGroup(
                        new WaitCommand((long) transferTime),

                        new GateCommand(gate, () -> true),

                        new IntakeCommand(
                                intake,
                                () -> true,
                                () -> false,
                                () -> false,
                                () -> false
                        ),

                        new IndexerCommand(
                                indexer,
                                () -> true,
                                () -> false,
                                () -> false,
                                () -> false
                        )
                ),

                new InstantCommand(
                        () -> gate.closeServo(),
                        gate
                )
        );
    }
}