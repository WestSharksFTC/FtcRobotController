package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.intake;

import com.seattlesolvers.solverslib.command.CommandBase;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.intake.Intake;

public class IntakeStop extends CommandBase {
    private final Intake intake;

    public IntakeStop(Intake intake){
        this.intake = intake;
    }

    @Override
    public void initialize() {
        intake.setState(Intake.State.STOP);
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}