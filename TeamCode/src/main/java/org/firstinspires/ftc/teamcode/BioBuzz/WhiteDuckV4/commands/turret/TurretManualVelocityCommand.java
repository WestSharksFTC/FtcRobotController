package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.turret;

import com.seattlesolvers.solverslib.command.CommandBase;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.turret.Turret;

import java.util.function.DoubleSupplier;

public class TurretManualVelocityCommand extends CommandBase {
    private final Turret turret;
    private final DoubleSupplier velocitySupplierPower;

    public TurretManualVelocityCommand(Turret turret, DoubleSupplier velocitySupplierPower){
        this.turret = turret;
        this.velocitySupplierPower = velocitySupplierPower;
    }

    @Override
    public void execute() {
        turret.setManualPower(velocitySupplierPower.getAsDouble());
    }

    @Override
    public boolean isFinished(){
        return true;
    }
}