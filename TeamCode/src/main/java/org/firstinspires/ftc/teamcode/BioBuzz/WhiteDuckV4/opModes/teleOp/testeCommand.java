package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.opModes.teleOp;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;
import com.seattlesolvers.solverslib.util.TelemetryData;

import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.chassis.DriveCommand;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.flowerRamp.FlowerRampDown;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.flowerRamp.FlowerRampUp;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.gate.GateClose;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.gate.GateOpen;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.indexer.IndexerFeed;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.indexer.IndexerReverse;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.indexer.IndexerStop;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.indexer.IndexerTransfer;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.intake.IntakeFeed;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.intake.IntakeReverse;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.intake.IntakeStop;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.intake.IntakeTransfer;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.chassis.Chassis;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.flowerRamp.FlowerRamp;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.gate.Gate;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.indexer.Indexer;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.intake.Intake;

@TeleOp
public class testeCommand extends CommandOpMode {

    // Controllers
    private GamepadEx driver;

    // Telemetry
    private TelemetryData telemetryData;

    private FlowerRamp flowerRamp;
    private Gate gate;
    private Intake intake;
    private Indexer indexer;


    // Commands
    private DriveCommand driveCommand;


    @Override
    public void initialize(){
        super.reset();

        // Controllers
        driver = new GamepadEx(gamepad1);
        GamepadEx operator = new GamepadEx(gamepad2);

        // Telemetry
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        telemetryData = new TelemetryData(telemetry);

        // Subsystems
        // Subsystems
        Chassis chassis = new Chassis(hardwareMap);
        flowerRamp = new FlowerRamp(hardwareMap);
        gate = new Gate(hardwareMap);
        intake = new Intake(hardwareMap);
        indexer = new Indexer(hardwareMap);

        // Commands
        driveCommand = new DriveCommand(
                chassis,
                () -> -driver.getLeftY(),
                () -> driver.getLeftX(),
                () -> driver.getRightX()
        );

        operator.getGamepadButton(GamepadKeys.Button.DPAD_UP).whenActive(new IntakeTransfer(intake));
        operator.getGamepadButton(GamepadKeys.Button.DPAD_RIGHT).whenActive(new IntakeFeed(intake));
        operator.getGamepadButton(GamepadKeys.Button.DPAD_DOWN).whenActive(new IntakeReverse(intake));
        operator.getGamepadButton(GamepadKeys.Button.DPAD_LEFT).whenActive(new IntakeStop(intake));

        operator.getGamepadButton(GamepadKeys.Button.Y).whenActive(new IndexerTransfer(indexer));
        operator.getGamepadButton(GamepadKeys.Button.B).whenActive(new IndexerFeed(indexer));
        operator.getGamepadButton(GamepadKeys.Button.A).whenActive(new IndexerReverse(indexer));
        operator.getGamepadButton(GamepadKeys.Button.X).whenActive(new IndexerStop(indexer));

        operator.getGamepadButton(GamepadKeys.Button.RIGHT_BUMPER).toggleWhenActive(new GateOpen(gate), new GateClose(gate));

        operator.getGamepadButton(GamepadKeys.Button.LEFT_BUMPER).toggleWhenActive(new FlowerRampDown(flowerRamp), new FlowerRampUp(flowerRamp));
    }

    @Override
    public void preRun(){
        schedule(driveCommand);
    }

    @Override
    public void run(){
        super.run();

        if(driver.wasJustPressed(GamepadKeys.Button.LEFT_BUMPER)){
            driveCommand.toggleFieldCentric();
        }

        if(driver.wasJustPressed(GamepadKeys.Button.RIGHT_BUMPER)){
            driveCommand.toggleHold();
        }

        // Telemetry
        telemetryData.addData("Field Centric", driveCommand.isFieldCentric() ? "ON" : "OFF");
        telemetryData.addData("Hold Mode", driveCommand.isHoldEnabled() ? "ON" : "OFF");
        telemetryData.addData("Intake State", intake.getState());
        telemetryData.addData("Indexer State", indexer.getState());
        telemetryData.addData("Gate State", gate.getState());
        telemetryData.addData("Flower Ramp State", flowerRamp.getState());
        telemetryData.update();
    }
}
