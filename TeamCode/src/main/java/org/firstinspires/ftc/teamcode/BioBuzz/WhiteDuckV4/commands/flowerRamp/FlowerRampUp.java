package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.flowerRamp;

import com.seattlesolvers.solverslib.command.CommandBase;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.flowerRamp.FlowerRamp;

public class FlowerRampUp extends CommandBase {
    private final FlowerRamp flowerRamp;

    public FlowerRampUp(FlowerRamp flowerRamp){
        this.flowerRamp = flowerRamp;
    }

    @Override
    public void initialize() {
        flowerRamp.setState(FlowerRamp.State.UP);
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}