package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.intake;

import com.seattlesolvers.solverslib.command.CommandBase;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.intake.Intake;

public class IntakeReverse extends CommandBase {
    private final Intake intake;

    public IntakeReverse(Intake intake){
        this.intake = intake;
    }

    @Override
    public void initialize() {
        intake.setState(Intake.State.REVERSE);
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}