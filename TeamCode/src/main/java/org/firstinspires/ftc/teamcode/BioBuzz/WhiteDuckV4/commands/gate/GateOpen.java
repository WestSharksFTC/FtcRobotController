package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.gate;

import com.seattlesolvers.solverslib.command.CommandBase;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.gate.Gate;

public class GateOpen extends CommandBase {
    private final Gate gate;

    public GateOpen(Gate gate){
        this.gate = gate;
    }

    @Override
    public void initialize() {
        gate.setState(Gate.State.OPEN);
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}
