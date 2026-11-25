package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commandGroups;

import com.seattlesolvers.solverslib.command.CommandBase;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.indexer.Indexer;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.intake.Intake;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.pollenSensor.PollenSensor;

public class AutoCollectCommand extends CommandBase {
    private final Intake intake;
    private final Indexer indexer;
    private final PollenSensor pollenSensor;

    public AutoCollectCommand(Intake intake, Indexer indexer, PollenSensor pollenSensor) {
        this.intake = intake;
        this.indexer = indexer;
        this.pollenSensor = pollenSensor;

        addRequirements(intake, indexer);
    }

    @Override
    public void execute() {
        if (pollenSensor.hasPollenOnTopSensor()) {
            indexer.setState(Indexer.State.STOP);
        } else {
            indexer.setState(Indexer.State.FEED);
        }

        if (pollenSensor.isIndexerFull()) {
            intake.setState(Intake.State.STOP);
        } else {
            intake.setState(Intake.State.FEED);
        }
    }

    @Override
    public void end(boolean interrupted) {
        intake.setState(Intake.State.STOP);
        indexer.setState(Indexer.State.STOP);
    }

    @Override
    public boolean isFinished() {
        return false; // Fica ativo continuamente até ser interrompido por outro comando
    }
}