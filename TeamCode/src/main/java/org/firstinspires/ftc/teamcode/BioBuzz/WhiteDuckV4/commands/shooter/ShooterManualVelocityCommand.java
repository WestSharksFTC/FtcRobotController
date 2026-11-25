package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.shooter;

import com.seattlesolvers.solverslib.command.CommandBase;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.shooter.Shooter;
import java.util.function.DoubleSupplier;

public class ShooterManualVelocityCommand extends CommandBase {
    private final Shooter shooter;
    private final DoubleSupplier velocitySupplierRPM;

    public ShooterManualVelocityCommand(Shooter shooter, DoubleSupplier velocitySupplierRPM) {
        this.shooter = shooter;
        this.velocitySupplierRPM = velocitySupplierRPM;
    }

    @Override
    public void execute() {
        shooter.setManualVelocity(velocitySupplierRPM.getAsDouble());
    }

    @Override
    public boolean isFinished(){
        return true;
    }
}