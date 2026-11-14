package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.turret;

import com.seattlesolvers.solverslib.command.CommandBase;

import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.turret.Turret;

import java.util.function.DoubleSupplier;

public class TurretManualCommand extends CommandBase {
    private final Turret turret;
    private final DoubleSupplier powerSupplier;

    public TurretManualCommand(Turret turret, DoubleSupplier powerSupplier) {
        this.turret = turret;
        this.powerSupplier = powerSupplier;

        addRequirements(turret);
    }

    @Override
    public void execute() {
        turret.setPower(powerSupplier.getAsDouble());
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