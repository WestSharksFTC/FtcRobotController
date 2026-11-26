package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.turret;

import com.seattlesolvers.solverslib.command.CommandBase;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.turret.Turret;

import java.util.function.DoubleSupplier;

public class TurretRecentralizeCommand extends CommandBase {
    private final Turret turret;

    public TurretRecentralizeCommand(Turret turret){
        this.turret = turret;
    }

    @Override
    public void execute() {
        turret.setManualAngle(0.0);
    }

    @Override
    public boolean isFinished(){
        return turret.isAtTarget();
    }
}