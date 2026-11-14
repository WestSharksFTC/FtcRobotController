
package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.flowerRamp;

import com.seattlesolvers.solverslib.command.CommandBase;

import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.flowerRamp.FlowerRamp;

import java.util.function.BooleanSupplier;

public class FlowerRampCommand extends CommandBase {
    private final FlowerRamp flowerRamp;

    private final BooleanSupplier rampUpButton;
    private final BooleanSupplier rampDownButton;

    // Estados anteriores dos botões
    private boolean previousRampUp = false;
    private boolean previousRampDown = false;

    public FlowerRampCommand(FlowerRamp flowerRamp, BooleanSupplier rampUpButton, BooleanSupplier rampDownButton){
        this.flowerRamp = flowerRamp;

        this.rampUpButton = rampUpButton;
        this.rampDownButton = rampDownButton;

        addRequirements(flowerRamp);
    }

    @Override
    public void execute() {
        // Leitura dos botões da rampa
        boolean currentRampUp = rampUpButton.getAsBoolean();
        boolean currentRampDown = rampDownButton.getAsBoolean();

        // Rampa: detecta apenas o momento do pressionamento
        if (currentRampUp && !previousRampUp) {
            flowerRamp.upFlowerRamp();
        } else if (currentRampDown && !previousRampDown) {
            flowerRamp.downFlowerRamp();
        }

        // Atualiza os estados anteriores
        previousRampUp = currentRampUp;
        previousRampDown = currentRampDown;
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void end(boolean interrupted) {
    }
}