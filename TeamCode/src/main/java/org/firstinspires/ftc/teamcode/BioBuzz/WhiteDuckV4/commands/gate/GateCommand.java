package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.gate;

import com.seattlesolvers.solverslib.command.CommandBase;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.gate.Gate;

import java.util.function.BooleanSupplier;

public class GateCommand extends CommandBase {
    private final Gate gate;

    private final BooleanSupplier gateOpenButton;
    private final BooleanSupplier gateCloseButton;

    private boolean previousGateOpen = false;
    private boolean previousGateClose = false;

    public GateCommand (Gate gate, BooleanSupplier gateOpenButton, BooleanSupplier gateCloseButton){
        this.gate = gate;

        this.gateOpenButton = gateOpenButton;
        this.gateCloseButton = gateCloseButton;

        addRequirements(gate);
    }

    @Override
    public void execute() {
        // Controle do gate por detecção de borda
        boolean currentGateOpen = gateOpenButton.getAsBoolean();
        boolean currentGateClose = gateCloseButton.getAsBoolean();

        if (currentGateOpen && !previousGateOpen) {
            gate.openServo();
        } else if (currentGateClose && !previousGateClose) {
            gate.closeServo();
        }

        previousGateOpen = currentGateOpen;
        previousGateClose = currentGateClose;
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void end(boolean interrupted) {
    }
}
