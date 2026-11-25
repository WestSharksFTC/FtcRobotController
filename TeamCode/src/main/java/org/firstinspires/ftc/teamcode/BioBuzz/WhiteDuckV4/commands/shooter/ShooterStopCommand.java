package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.shooter;

import com.seattlesolvers.solverslib.command.CommandBase;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.shooter.Shooter;

public class ShooterStopCommand extends CommandBase {
    private final Shooter shooter;

    public ShooterStopCommand(Shooter shooter){
        this.shooter = shooter;
    }

    @Override
    public void initialize() {
        shooter.setState(Shooter.State.STOP);
    }

    @Override
    public boolean isFinished(){
        return true;
    }
}