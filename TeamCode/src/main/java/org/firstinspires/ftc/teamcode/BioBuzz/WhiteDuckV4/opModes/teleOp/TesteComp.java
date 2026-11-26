package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.opModes.teleOp;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;
import com.seattlesolvers.solverslib.util.TelemetryData;

import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commandGroups.AutoCollectCommand;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commandGroups.ReverseCycleCommand;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commandGroups.TransferCycleCommand;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.chassis.DriveCommand;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.flowerRamp.FlowerRampDown;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.flowerRamp.FlowerRampUp;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.shooter.ShooterAutoVelocityCommand;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.shooter.ShooterStopCommand;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.turret.TurretAutoAimCommand;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.turret.TurretStopCommand;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.chassis.Chassis;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.flowerRamp.FlowerRamp;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.gate.Gate;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.indexer.Indexer;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.intake.Intake;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.pollenSensor.PollenSensor;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.shooter.Shooter;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.shooter.ShooterTable;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.turret.Turret;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.targeting.TargetingSystem;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.utils.OpModeStorage;

@TeleOp
public class TesteComp extends CommandOpMode{

    // Controllers
    private GamepadEx driver;
    private GamepadEx operator;

    // Pose
    private OpModeStorage opModeStorage;

    // Telemetry
    private TelemetryData telemetryData;

    // Subsystems
    private Chassis chassis;
    private FlowerRamp flowerRamp;
    private Gate gate;
    private Intake intake;
    private Indexer indexer;
    private PollenSensor pollenSensor;
    private Shooter shooter;
    private Turret turret;
    private TargetingSystem targetingSystem;
    private ShooterTable shooterTable;


    // Commands
    private DriveCommand driveCommand;
    private AutoCollectCommand autoCollect;


    @Override
    public void initialize(){
        super.reset();

        // Controllers
        driver = new GamepadEx(gamepad1);
        operator = new GamepadEx(gamepad2);

        // Telemetry
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        telemetryData = new TelemetryData(telemetry);

        // Subsystems
        chassis = new Chassis(hardwareMap);
        flowerRamp = new FlowerRamp(hardwareMap);
        gate = new Gate(hardwareMap);
        intake = new Intake(hardwareMap);
        indexer = new Indexer(hardwareMap);
        pollenSensor = new PollenSensor(hardwareMap);
        shooter = new Shooter(hardwareMap);
        turret = new Turret(hardwareMap);

        targetingSystem = new TargetingSystem(chassis);
        shooterTable = new ShooterTable();

        shooter.setTargetingSystemAndShooterTable(targetingSystem, shooterTable);
        turret.setTargetingSystem(targetingSystem);

        opModeStorage = new OpModeStorage();

        chassis.setPose(opModeStorage.getAutonomousEndPose());

        // Commands
        driveCommand = new DriveCommand(
                chassis,
                () -> operator.getLeftY(),
                () -> -operator.getLeftX(),
                () -> -operator.getRightX()
        );

        chassis.setDefaultCommand(driveCommand);

        autoCollect = new AutoCollectCommand(
                intake,
                indexer,
                pollenSensor
        );

        intake.setDefaultCommand(autoCollect);
        indexer.setDefaultCommand(autoCollect);

        driver.getGamepadButton(GamepadKeys.Button.LEFT_BUMPER).whenPressed(new InstantCommand(() -> driveCommand.toggleFieldCentric()));
        driver.getGamepadButton(GamepadKeys.Button.RIGHT_BUMPER).whenPressed(new InstantCommand(() -> driveCommand.toggleHold()));

        operator.getGamepadButton(GamepadKeys.Button.DPAD_UP).whenPressed(new FlowerRampUp(flowerRamp));
        operator.getGamepadButton(GamepadKeys.Button.DPAD_DOWN).whenPressed(new FlowerRampDown(flowerRamp));

        operator.getGamepadButton(GamepadKeys.Button.B).whenPressed(new TransferCycleCommand(intake, indexer, gate));
        operator.getGamepadButton(GamepadKeys.Button.A).whenPressed(new ReverseCycleCommand(intake, indexer, gate));

        operator.getGamepadButton(GamepadKeys.Button.LEFT_BUMPER).toggleWhenPressed(new TurretAutoAimCommand(turret), new TurretStopCommand(turret));

        operator.getGamepadButton(GamepadKeys.Button.RIGHT_BUMPER).toggleWhenPressed(new ShooterAutoVelocityCommand(shooter), new ShooterStopCommand(shooter));
    }

    @Override
    public void run(){
        super.run();

        if(pollenSensor.isIndexerFull()){
            gamepad1.rumble(-1);
            gamepad2.rumble(-1);
            gamepad1.setLedColor(255, 0, 0, -1);
            gamepad2.setLedColor(255, 0, 0, -1);
        }else{
            gamepad1.stopRumble();
            gamepad2.stopRumble();
            gamepad1.setLedColor(0, 0, 255, -1);
            gamepad2.setLedColor(0, 0, 255, -1);
        }

        // Telemetry
        telemetryData.addData("==========", "CHASSIS ==========");
        telemetryData.addData("Field Centric", driveCommand.isFieldCentric() ? "ON" : "OFF");
        telemetryData.addData("Hold Mode", driveCommand.isHoldEnabled() ? "ON" : "OFF");
        chassis.addTelemetry(telemetryData);
        telemetryData.addData("", "");


        telemetryData.addData("==========", "FLOWER RAMP ==========");
        telemetryData.addData("Flower Ramp State", flowerRamp.getState());
        telemetryData.addData("", "");


        telemetryData.addData("==========", "INTAKE ==========");
        telemetryData.addData("Intake State", intake.getState());
        telemetryData.addData("", "");


        telemetryData.addData("==========", "POLLEN SENSOR ==========");
        pollenSensor.addTelemetry(telemetryData);
        telemetryData.addData("", "");


        telemetryData.addData("==========", "INDEXER ==========");
        telemetryData.addData("Indexer State", indexer.getState());
        telemetryData.addData("", "");


        telemetryData.addData("==========", "GATE ==========");
        telemetryData.addData("Gate State", gate.getState());
        gate.addTelemetry(telemetryData);
        telemetryData.addData("", "");


        telemetryData.addData("==========", "TURRET ==========");
        telemetryData.addData("Turret State", turret.getState());
        turret.addTelemetry(telemetryData);
        telemetryData.addData("", "");


        telemetryData.addData("==========", "SHOOTER ==========");
        telemetryData.addData("Shooter State", shooter.getState());
        shooter.addTelemetry(telemetryData);
        telemetryData.addData("", "");

        telemetryData.update();
    }
}
