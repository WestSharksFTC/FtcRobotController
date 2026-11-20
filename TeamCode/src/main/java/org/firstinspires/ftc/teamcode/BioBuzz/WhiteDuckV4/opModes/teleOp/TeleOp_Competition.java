package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.opModes.teleOp;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;

import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commandGroups.CollectCommandGroup;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commandGroups.TransferCommandGroup;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.chassis.DriveCommand;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.flowerRamp.FlowerRampCommand;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.shooter.ShooterAutoVelocityCommand;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.turret.TurretAutoAimCommand;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.chassis.Chassis;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.flowerRamp.FlowerRamp;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.gate.Gate;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.indexer.Indexer;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.intake.Intake;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.shooter.Shooter;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.turret.Turret;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.targeting.TargetingSystem;

@TeleOp(name = "White Duck V4 Competition", group = "WhiteDuckV4")
public class TeleOp_Competition extends OpMode {

    // Scheduler
    private CommandScheduler commandScheduler;

    // Controllers
    private GamepadEx driver;
    private GamepadEx operator;

    // Subsystems
    private Chassis chassis;
    private FlowerRamp flowerRamp;
    private Intake intake;
    private Indexer indexer;
    private Gate gate;
    private Turret turret;
    private Shooter shooter;

    // Targeting
    private TargetingSystem targetingSystem;

    // Commands
    private DriveCommand driveCommand;
    private FlowerRampCommand flowerRampCommand;
    private TurretAutoAimCommand turretAutoAimCommand;
    private ShooterAutoVelocityCommand shooterAutoVelocityCommand;

    // Command Groups
    private CollectCommandGroup collectCommandGroup;
    private TransferCommandGroup transferCommandGroup;

    // Variáveis
    private boolean transferRequested = false;
    private boolean rampToggle = true;

    @Override
    public void init(){
        // Scheduler
        commandScheduler = CommandScheduler.getInstance();

        // Controllers
        driver = new GamepadEx(gamepad1);
        operator = new GamepadEx(gamepad2);

        // Subsystems
        chassis = new Chassis();
        flowerRamp = new FlowerRamp();
        intake = new Intake();
        indexer = new Indexer();
        gate = new Gate();
        turret = new Turret();
        shooter = new Shooter();

        // HardwareMap dos Subsystems
        chassis.init(hardwareMap);
        flowerRamp.init(hardwareMap);
        intake.init(hardwareMap);
        indexer.init(hardwareMap);
        gate.init(hardwareMap);
        turret.init(hardwareMap);
        shooter.init(hardwareMap);

        // Targeting System
        targetingSystem = new TargetingSystem(chassis);

        // Commands
        driveCommand = new DriveCommand(
                chassis,
                () -> driver.getLeftY(),
                () -> driver.getLeftX(),
                () -> driver.getRightX()
        );
        flowerRampCommand = new FlowerRampCommand(
                flowerRamp,
                () -> rampToggle
        );
        turretAutoAimCommand = new TurretAutoAimCommand(
                turret,
                targetingSystem
        );
        shooterAutoVelocityCommand = new ShooterAutoVelocityCommand(
                shooter,
                targetingSystem
        );

        // Command Groups
        collectCommandGroup = new CollectCommandGroup(
                gate,
                intake,
                indexer
        );
        transferCommandGroup = new TransferCommandGroup(
                gate,
                intake,
                indexer,
                TeleOpConstants.TRANSFER_TIME_MS
        );

    }

    @Override
    public void start(){
        commandScheduler.schedule(driveCommand);
        commandScheduler.schedule(flowerRampCommand);
        commandScheduler.schedule(collectCommandGroup);
        commandScheduler.schedule(turretAutoAimCommand);
        commandScheduler.schedule(shooterAutoVelocityCommand);
    }

    @Override
    public void loop(){
        driver.readButtons();
        operator.readButtons();

        if(driver.wasJustPressed(GamepadKeys.Button.LEFT_BUMPER)){
            driveCommand.toggleFieldCentric();
        }

        if(driver.wasJustPressed(GamepadKeys.Button.RIGHT_BUMPER)){
            driveCommand.toggleHold();
        }

        if(operator.wasJustPressed(GamepadKeys.Button.LEFT_BUMPER)){
            rampToggle = !rampToggle;
        }

        if(operator.wasJustPressed(GamepadKeys.Button.B) && !transferRequested){
            transferRequested = true;
            commandScheduler.schedule(transferCommandGroup);
        }


        // Telemetry
        telemetry.addLine("========== TELOP TELEMETRY ==========");
        telemetry.addData("Field Centric", driveCommand.isFieldCentric() ? "ON" : "OFF");
        telemetry.addData("Hold Mode", driveCommand.isHoldEnabled() ? "ON" : "OFF");
        telemetry.addLine();

        chassis.getTelemetry(telemetry);
        flowerRamp.getTelemetry(telemetry);
        intake.getTelemetry(telemetry);
        indexer.getTelemetry(telemetry);
        gate.getTelemetry(telemetry);
        turret.getTelemetry(telemetry);
        shooter.getTelemetry(telemetry);
        targetingSystem.getTelemetry(telemetry);

        commandScheduler.run();

        if(transferRequested && !transferCommandGroup.isScheduled()) {
            transferRequested = false;
            commandScheduler.schedule(collectCommandGroup);
        }
    }

    @Override
    public void stop() {
        commandScheduler.cancelAll();
    }
}