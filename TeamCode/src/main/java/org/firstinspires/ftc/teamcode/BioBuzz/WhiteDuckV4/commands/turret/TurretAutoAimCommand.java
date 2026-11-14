package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.turret;

import com.seattlesolvers.solverslib.command.CommandBase;

import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.turret.Turret;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.targeting.TargetingSystem;

public class TurretAutoAimCommand extends CommandBase {
    private final Turret turret;
    private final TargetingSystem targetingSystem;

    public TurretAutoAimCommand(Turret turret, TargetingSystem targetingSystem) {
        this.turret = turret;
        this.targetingSystem = targetingSystem;

        addRequirements(turret);
    }

    @Override
    public void execute() {
        turret.setTargetAngle(targetingSystem.getTurretAngle());
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void end(boolean interrupted) {
        turret.stop();
    }
}