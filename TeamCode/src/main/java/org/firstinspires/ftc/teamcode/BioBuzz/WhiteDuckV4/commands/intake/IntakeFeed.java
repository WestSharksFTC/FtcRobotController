package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.intake;

import com.seattlesolvers.solverslib.command.CommandBase;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.intake.Intake;

public class IntakeFeed extends CommandBase {
    private final Intake intake;

    public IntakeFeed(Intake intake){
        this.intake = intake;
    }

    @Override
    public void initialize() {
        intake.setState(Intake.State.FEED);
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}