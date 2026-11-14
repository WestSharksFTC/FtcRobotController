package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.indexer;

import com.seattlesolvers.solverslib.command.CommandBase;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.indexer.Indexer;

import java.util.function.BooleanSupplier;

public class IndexerCommand extends CommandBase {
    private final Indexer indexer;

    private final BooleanSupplier indexerTransferButton;
    private final BooleanSupplier indexerFeedButton;
    private final BooleanSupplier indexerReverseButton;
    private final BooleanSupplier indexerStopButton;

    public IndexerCommand(Indexer indexer, BooleanSupplier indexerTransferButton, BooleanSupplier indexerFeedButton, BooleanSupplier indexerReverseButton, BooleanSupplier indexerStopButton){
        this.indexer = indexer;

        this.indexerTransferButton = indexerTransferButton;
        this.indexerFeedButton = indexerFeedButton;
        this.indexerReverseButton = indexerReverseButton;
        this.indexerStopButton = indexerStopButton;

        addRequirements(indexer);
    }

    @Override
    public void execute() {
        // Controle do motor
        if (indexerTransferButton.getAsBoolean()) {
            indexer.transfer();
        } else if (indexerFeedButton.getAsBoolean()) {
            indexer.feed();
        } else if (indexerReverseButton.getAsBoolean()) {
            indexer.reverse();
        } else if (indexerStopButton.getAsBoolean()) {
            indexer.stop();
        }
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void end(boolean interrupted) {
        indexer.stop();
    }
}