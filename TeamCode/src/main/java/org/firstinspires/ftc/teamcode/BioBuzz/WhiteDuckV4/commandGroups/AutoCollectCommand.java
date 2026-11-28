package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commandGroups;

import com.seattlesolvers.solverslib.command.CommandBase;

import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.gate.Gate;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.indexer.Indexer;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.intake.Intake;

public class AutoCollectCommand extends CommandBase {
    private final Intake intake;
    private final Indexer indexer;
    private final Gate gate;

    public AutoCollectCommand(Intake intake, Indexer indexer, Gate gate) {
        this.intake = intake;
        this.indexer = indexer;
        this.gate = gate;

        addRequirements(intake, indexer, gate);
    }

    @Override
    public void initialize() {
        gate.setState(Gate.State.CLOSE);
        indexer.setState(Indexer.State.FEED);
        intake.setState(Intake.State.FEED);
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}