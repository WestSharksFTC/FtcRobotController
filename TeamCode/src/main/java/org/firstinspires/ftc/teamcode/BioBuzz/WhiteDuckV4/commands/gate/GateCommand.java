package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.gate;

import com.seattlesolvers.solverslib.command.CommandBase;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.gate.Gate;

import java.util.function.BooleanSupplier;

public class GateCommand extends CommandBase {
    private final Gate gate;

    private final BooleanSupplier isGateOpenButton;

    public GateCommand (Gate gate, BooleanSupplier isGateOpenButton){
        this.gate = gate;

        this.isGateOpenButton = isGateOpenButton;

        addRequirements(gate);
    }

    @Override
    public void execute() {
        if (isGateOpenButton.getAsBoolean()) {
            gate.openServo();
        } else {
            gate.closeServo();
        }
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void end(boolean interrupted) {
    }
}
