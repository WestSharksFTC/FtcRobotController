package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.turret;

import com.seattlesolvers.solverslib.command.CommandBase;

import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.turret.Turret;

import java.util.function.DoubleSupplier;

public class TurretToAngleCommand extends CommandBase {
    private final Turret turret;
    private final DoubleSupplier targetAngle;

    public TurretToAngleCommand(Turret turret, DoubleSupplier targetAngle) {
        this.turret = turret;
        this.targetAngle = targetAngle;

        addRequirements(turret);
    }

    @Override
    public void execute() {
        turret.setTargetAngle(targetAngle.getAsDouble());
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