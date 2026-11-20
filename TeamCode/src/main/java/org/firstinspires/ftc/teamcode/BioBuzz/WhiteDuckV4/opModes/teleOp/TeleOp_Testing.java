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
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.gate.GateCommand;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.indexer.IndexerCommand;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.intake.IntakeCommand;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.shooter.ShooterAutoVelocityCommand;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.shooter.ShooterVelocityCommand;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.turret.TurretAutoAimCommand;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.turret.TurretManualCommand;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.turret.TurretToAngleCommand;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.chassis.Chassis;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.flowerRamp.FlowerRamp;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.gate.Gate;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.indexer.Indexer;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.intake.Intake;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.shooter.Shooter;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.turret.Turret;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.targeting.TargetingSystem;

@TeleOp(name = "White Duck V4 Testing", group = "WhiteDuckV4")
public class TeleOp_Testing extends OpMode {

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
    private IntakeCommand intakeCommand;
    private IndexerCommand indexerCommand;
    private GateCommand gateCommand;
    private TurretAutoAimCommand turretAutoAimCommand;
    private TurretToAngleCommand turretToAngleCommand;
    private TurretManualCommand turretManualCommand;
    private ShooterAutoVelocityCommand shooterAutoVelocityCommand;
    private ShooterVelocityCommand shooterVelocityCommand;

    // Command Groups
    private CollectCommandGroup collectCommandGroup;
    private TransferCommandGroup transferCommandGroup;

    // Variáveis
    private boolean autoInitialized = false;
    private boolean transferRequested = false;
    private boolean rampToggle = true;
    private boolean gateToggle = false;
    private double turretAngle = 0.0;
    private double shooterVelocityRPM = 0.0;

    private enum SubsystemControl{
        INTAKE,
        INDEXER,
        TURRET,
        SHOOTER
    }

    private enum MechanismAction {
        TRANSFER,
        FEED,
        REVERSE,
        STOP
    }

    private enum RobotState{
        MANUAL,
        AUTO
    }

    private enum TurretControlMode {
        MANUAL,
        SET_ANGLE
    }

    private SubsystemControl selectedSubsystem = SubsystemControl.INTAKE;

    private MechanismAction intakeState = MechanismAction.STOP;
    private MechanismAction indexerState = MechanismAction.STOP;

    private TurretControlMode turretControlMode = TurretControlMode.MANUAL;

    private RobotState currentRobotState = RobotState.MANUAL;

    private void scheduleManualCommands() {
        commandScheduler.schedule(intakeCommand);
        commandScheduler.schedule(indexerCommand);
        commandScheduler.schedule(gateCommand);

        if(turretControlMode == TurretControlMode.MANUAL){
            commandScheduler.schedule(turretManualCommand);
        }else{
            commandScheduler.schedule(turretToAngleCommand);
        }

        commandScheduler.schedule(shooterVelocityCommand);
    }

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
        intakeCommand = new IntakeCommand(
                intake,
                () -> intakeState == MechanismAction.TRANSFER,
                () -> intakeState == MechanismAction.FEED,
                () -> intakeState == MechanismAction.REVERSE,
                () -> intakeState == MechanismAction.STOP
        );
        indexerCommand = new IndexerCommand(
                indexer,
                () -> indexerState == MechanismAction.TRANSFER,
                () -> indexerState == MechanismAction.FEED,
                () -> indexerState == MechanismAction.REVERSE,
                () -> indexerState == MechanismAction.STOP
        );
        gateCommand = new GateCommand(
                gate,
                () -> gateToggle
        );
        turretAutoAimCommand = new TurretAutoAimCommand(
                turret,
                targetingSystem
        );
        turretToAngleCommand = new TurretToAngleCommand(
                turret,
                () -> turretAngle
        );
        turretManualCommand = new TurretManualCommand(
                turret,
                () -> operator.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER) - operator.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER)
        );
        shooterAutoVelocityCommand = new ShooterAutoVelocityCommand(
                shooter,
                targetingSystem
        );
        shooterVelocityCommand = new ShooterVelocityCommand(
                shooter,
                () -> shooterVelocityRPM
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

        scheduleManualCommands();
    }

    @Override
    public void loop(){
        driver.readButtons();
        operator.readButtons();

        if(operator.getButton(GamepadKeys.Button.LEFT_STICK_BUTTON) && operator.wasJustPressed(GamepadKeys.Button.RIGHT_STICK_BUTTON)){
            if(currentRobotState == RobotState.MANUAL){
                currentRobotState = RobotState.AUTO;
            }else if(currentRobotState == RobotState.AUTO){
                currentRobotState = RobotState.MANUAL;
            }
        }

        if(driver.wasJustPressed(GamepadKeys.Button.LEFT_BUMPER)){
            driveCommand.toggleFieldCentric();
        }

        if(driver.wasJustPressed(GamepadKeys.Button.RIGHT_BUMPER)){
            driveCommand.toggleHold();
        }

        if(operator.wasJustPressed(GamepadKeys.Button.LEFT_BUMPER)){
            rampToggle = !rampToggle;
        }

        if(currentRobotState == RobotState.MANUAL) {
            if (autoInitialized) {
                autoInitialized = false;
                transferRequested = false;

                commandScheduler.cancel(collectCommandGroup);
                commandScheduler.cancel(transferCommandGroup);
                commandScheduler.cancel(turretAutoAimCommand);
                commandScheduler.cancel(shooterAutoVelocityCommand);

                intakeState = MechanismAction.STOP;
                indexerState = MechanismAction.STOP;
                gateToggle = false;

                scheduleManualCommands();
            }

            if (operator.wasJustPressed(GamepadKeys.Button.RIGHT_BUMPER)){
                gateToggle = !gateToggle;
            }

            if (operator.wasJustPressed(GamepadKeys.Button.DPAD_UP)) {
                selectedSubsystem = SubsystemControl.INTAKE;
            } else if (operator.wasJustPressed(GamepadKeys.Button.DPAD_RIGHT)) {
                selectedSubsystem = SubsystemControl.INDEXER;
            } else if (operator.wasJustPressed(GamepadKeys.Button.DPAD_DOWN)) {
                selectedSubsystem = SubsystemControl.TURRET;
            } else if (operator.wasJustPressed(GamepadKeys.Button.DPAD_LEFT)) {
                selectedSubsystem = SubsystemControl.SHOOTER;
            }

            if (selectedSubsystem == SubsystemControl.INTAKE) {
                if (operator.wasJustPressed(GamepadKeys.Button.Y)) {
                    intakeState = MechanismAction.TRANSFER;
                } else if (operator.wasJustPressed(GamepadKeys.Button.B)) {
                    intakeState = MechanismAction.FEED;
                } else if (operator.wasJustPressed(GamepadKeys.Button.A)) {
                    intakeState = MechanismAction.REVERSE;
                } else if (operator.wasJustPressed(GamepadKeys.Button.X)) {
                    intakeState = MechanismAction.STOP;
                }
            } else if (selectedSubsystem == SubsystemControl.INDEXER) {
                if (operator.wasJustPressed(GamepadKeys.Button.Y)) {
                    indexerState = MechanismAction.TRANSFER;
                } else if (operator.wasJustPressed(GamepadKeys.Button.B)) {
                    indexerState = MechanismAction.FEED;
                } else if (operator.wasJustPressed(GamepadKeys.Button.A)) {
                    indexerState = MechanismAction.REVERSE;
                } else if (operator.wasJustPressed(GamepadKeys.Button.X)) {
                    indexerState = MechanismAction.STOP;
                }
            } else if (selectedSubsystem == SubsystemControl.TURRET) {
                if (operator.wasJustPressed(GamepadKeys.Button.B)) {
                    if (turretControlMode == TurretControlMode.MANUAL) {
                        turretControlMode = TurretControlMode.SET_ANGLE;
                        commandScheduler.schedule(turretToAngleCommand);
                    } else {
                        turretControlMode = TurretControlMode.MANUAL;
                        commandScheduler.schedule(turretManualCommand);
                    }
                }

                if (turretControlMode == TurretControlMode.SET_ANGLE) {
                    if (operator.wasJustPressed(GamepadKeys.Button.Y)) {
                        turretAngle += 5.0;
                    } else if (operator.wasJustPressed(GamepadKeys.Button.A)) {
                        turretAngle -= 5.0;
                    }
                }
            } else if (selectedSubsystem == SubsystemControl.SHOOTER) {
                if (operator.wasJustPressed(GamepadKeys.Button.Y)) {
                    shooterVelocityRPM += 10.0;
                } else if (operator.wasJustPressed(GamepadKeys.Button.A)) {
                    shooterVelocityRPM -= 10.0;
                }
            }
        }else if(currentRobotState == RobotState.AUTO){
            if(!autoInitialized){
                commandScheduler.schedule(collectCommandGroup);
                commandScheduler.schedule(turretAutoAimCommand);
                commandScheduler.schedule(shooterAutoVelocityCommand);
                autoInitialized = true;
            }

            if(operator.wasJustPressed(GamepadKeys.Button.B) && !transferRequested){
                transferRequested = true;
                commandScheduler.schedule(transferCommandGroup);
            }
        }

        // Telemetry
        telemetry.addLine("========== TELOP TELEMETRY ==========");
        telemetry.addData("Current Robot State", currentRobotState);
        telemetry.addData("Field Centric", driveCommand.isFieldCentric() ? "ON" : "OFF");
        telemetry.addData("Hold Mode", driveCommand.isHoldEnabled() ? "ON" : "OFF");
        telemetry.addData("Selected Mechanism", selectedSubsystem);
        telemetry.addData("Intake State", intakeState);
        telemetry.addData("Indexer State", indexerState);
        telemetry.addData("Turret Control Mode", turretControlMode);
        telemetry.addData("Turret Angle", "%.2f°", turretAngle);
        telemetry.addData("Shooter Velocity (RPM)", shooterVelocityRPM);
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

        if(currentRobotState == RobotState.AUTO && transferRequested && !transferCommandGroup.isScheduled()) {
            transferRequested = false;
            commandScheduler.schedule(collectCommandGroup);
        }
    }

    @Override
    public void stop() {
        commandScheduler.cancelAll();
    }
}