package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commandGroups;

import com.seattlesolvers.solverslib.command.CommandBase;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.indexer.Indexer;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.intake.Intake;

public class AutoCollectCommand extends CommandBase {
    private final Intake intake;
    private final Indexer indexer;

    public AutoCollectCommand(Intake intake, Indexer indexer) {
        this.intake = intake;
        this.indexer = indexer;

        addRequirements(intake, indexer);
    }

    @Override
    public void initialize() {
        intake.setState(Intake.State.FEED);
        indexer.setState(Indexer.State.FEED);
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}