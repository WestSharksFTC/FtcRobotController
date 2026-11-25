package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.turret;

import com.seattlesolvers.solverslib.command.CommandBase;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.turret.Turret;

import java.util.function.DoubleSupplier;

public class TurretManualAngleCommand extends CommandBase {
    private final Turret turret;
    private final DoubleSupplier angleSupplierDegrees;

    public TurretManualAngleCommand(Turret turret, DoubleSupplier angleSupplierDegrees){
        this.turret = turret;
        this.angleSupplierDegrees = angleSupplierDegrees;
    }

    @Override
    public void execute() {
        turret.setManualAngle(angleSupplierDegrees.getAsDouble());
    }

    @Override
    public boolean isFinished(){
        return true;
    }
}