package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.intake;

import com.seattlesolvers.solverslib.command.CommandBase;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.intake.Intake;

import java.util.function.BooleanSupplier;

public class IntakeCommand extends CommandBase {
    private final Intake intake;

    private final BooleanSupplier intakeTransferButton;
    private final BooleanSupplier intakeFeedButton;
    private final BooleanSupplier intakeReverseButton;
    private final BooleanSupplier intakeStopButton;

    public IntakeCommand(Intake intake, BooleanSupplier intakeTransferButton, BooleanSupplier intakeFeedButton, BooleanSupplier intakeReverseButton, BooleanSupplier intakeStopButton){
        this.intake = intake;

        this.intakeTransferButton = intakeTransferButton;
        this.intakeFeedButton = intakeFeedButton;
        this.intakeReverseButton = intakeReverseButton;
        this.intakeStopButton = intakeStopButton;

        addRequirements(intake);
    }

    @Override
    public void execute() {
        // Motor
        if (intakeTransferButton.getAsBoolean()) {
            intake.transfer();
        } else if (intakeFeedButton.getAsBoolean()) {
            intake.feed();
        } else if (intakeReverseButton.getAsBoolean()) {
            intake.reverse();
        } else if (intakeStopButton.getAsBoolean()) {
            intake.stop();
        }
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void end(boolean interrupted) {
        intake.stop();
    }
}