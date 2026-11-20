
package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.flowerRamp;

import com.seattlesolvers.solverslib.command.CommandBase;

import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.flowerRamp.FlowerRamp;

import java.util.function.BooleanSupplier;

public class FlowerRampCommand extends CommandBase {
    private final FlowerRamp flowerRamp;

    private final BooleanSupplier isRampUpButton;

    public FlowerRampCommand(FlowerRamp flowerRamp, BooleanSupplier isRampUpButton){
        this.flowerRamp = flowerRamp;

        this.isRampUpButton = isRampUpButton;

        addRequirements(flowerRamp);
    }

    @Override
    public void execute() {
        if (isRampUpButton.getAsBoolean()) {
            flowerRamp.upFlowerRamp();
        } else {
            flowerRamp.downFlowerRamp();
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