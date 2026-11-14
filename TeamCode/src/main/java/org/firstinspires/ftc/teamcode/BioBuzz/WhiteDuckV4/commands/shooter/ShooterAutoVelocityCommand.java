package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.shooter;

import com.seattlesolvers.solverslib.command.CommandBase;

import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.shooter.Shooter;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.targeting.TargetingSystem;

public class ShooterAutoVelocityCommand extends CommandBase {
    private final Shooter shooter;
    private final TargetingSystem targetingSystem;

    public ShooterAutoVelocityCommand (Shooter shooter, TargetingSystem targetingSystem){
        this.shooter = shooter;
        this.targetingSystem = targetingSystem;

        addRequirements(shooter);
    }

    @Override
    public void execute(){
        double targetVelocity = shooter.getShooterVelocity(targetingSystem.getTargetDistanceIN());
        shooter.setVelocityRPM(targetVelocity);
    }

    @Override
    public boolean isFinished(){
        return false;
    }

    @Override
    public void end(boolean interrupted){
        shooter.stop();
    }
}
