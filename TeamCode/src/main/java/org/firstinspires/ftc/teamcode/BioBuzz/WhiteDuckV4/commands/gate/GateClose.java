package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.gate;

import com.seattlesolvers.solverslib.command.CommandBase;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.gate.Gate;

public class GateClose extends CommandBase {
    private final Gate gate;

    public GateClose(Gate gate){
        this.gate = gate;
    }

    @Override
    public void initialize() {
        gate.setState(Gate.State.CLOSE);
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}
