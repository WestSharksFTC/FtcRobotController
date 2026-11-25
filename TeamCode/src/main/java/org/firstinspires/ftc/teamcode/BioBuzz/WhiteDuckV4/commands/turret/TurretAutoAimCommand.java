package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.turret;

import com.seattlesolvers.solverslib.command.CommandBase;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.turret.Turret;

public class TurretAutoAimCommand extends CommandBase {
    private final Turret turret;

    public TurretAutoAimCommand(Turret turret){
        this.turret = turret;
    }

    @Override
    public void initialize() {
        turret.setState(Turret.State.AUTO_AIM);
    }

    @Override
    public boolean isFinished(){
        return true;
    }
}