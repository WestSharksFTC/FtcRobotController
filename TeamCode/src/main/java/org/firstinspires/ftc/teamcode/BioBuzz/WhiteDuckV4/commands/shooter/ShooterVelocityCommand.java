package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.shooter;

import com.seattlesolvers.solverslib.command.CommandBase;

import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.shooter.Shooter;

import java.util.function.DoubleSupplier;

public class ShooterVelocityCommand extends CommandBase {
    private final Shooter shooter;
    private final DoubleSupplier velocitySupplierRPM;

    public ShooterVelocityCommand(Shooter shooter, DoubleSupplier velocitySupplierRPM) {
        this.shooter = shooter;
        this.velocitySupplierRPM = velocitySupplierRPM;

        addRequirements(shooter);
    }

    @Override
    public void execute() {
        shooter.setVelocityRPM(velocitySupplierRPM.getAsDouble());
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void end(boolean interrupted) {
        shooter.stop();
    }
}