package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.indexer;

import com.seattlesolvers.solverslib.command.CommandBase;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.indexer.Indexer;

public class IndexerFeed extends CommandBase {
    private final Indexer indexer;

    public IndexerFeed(Indexer indexer){
        this.indexer = indexer;
    }

    @Override
    public void initialize() {
        indexer.setState(Indexer.State.FEED);
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}