package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.shooter;

import com.seattlesolvers.solverslib.command.CommandBase;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.shooter.Shooter;

public class ShooterAutoVelocityCommand extends CommandBase {
    private final Shooter shooter;

    public ShooterAutoVelocityCommand(Shooter shooter){
        this.shooter = shooter;
    }

    @Override
    public void initialize(){
        shooter.setState(Shooter.State.AUTO_VELOCITY);
    }

    @Override
    public boolean isFinished(){
        return true;
    }
}
