package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.indexer;

import com.seattlesolvers.solverslib.command.CommandBase;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.indexer.Indexer;

public class IndexerReverse extends CommandBase {
    private final Indexer indexer;

    public IndexerReverse(Indexer indexer){
        this.indexer = indexer;
    }

    @Override
    public void initialize() {
        indexer.setState(Indexer.State.REVERSE);
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}