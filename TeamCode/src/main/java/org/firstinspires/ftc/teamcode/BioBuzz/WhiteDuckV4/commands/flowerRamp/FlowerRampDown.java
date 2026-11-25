package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.flowerRamp;

import com.seattlesolvers.solverslib.command.CommandBase;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.flowerRamp.FlowerRamp;

public class FlowerRampDown extends CommandBase {
    private final FlowerRamp flowerRamp;

    public FlowerRampDown(FlowerRamp flowerRamp){
        this.flowerRamp = flowerRamp;
    }

    @Override
    public void initialize() {
        flowerRamp.setState(FlowerRamp.State.DOWN);
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}