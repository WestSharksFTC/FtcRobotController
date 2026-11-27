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
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.gate.GateClose;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.gate.GateOpen;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.shooter.ShooterStopCommand;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.shooter.ShooterManualVelocityCommand;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.turret.TurretManualAngleCommand;
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

@TeleOp
public class TesteTop extends CommandOpMode{

    // Controllers
    private GamepadEx driver;

    // Telemetry
    private TelemetryData telemetryData;

    private Intake intake;
    private Indexer indexer;
    private PollenSensor pollenSensor;
    private Shooter shooter;
    private Turret turret;
    private TargetingSystem targetingSystem;


    // Commands
    private DriveCommand driveCommand;

    private double shooterVelocityRPM = 0.0;
    private double turretAngleDegrees = 0.0;


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
        FlowerRamp flowerRamp = new FlowerRamp(hardwareMap);
        Gate gate = new Gate(hardwareMap);
        intake = new Intake(hardwareMap);
        indexer = new Indexer(hardwareMap);
        pollenSensor = new PollenSensor(hardwareMap);
        intake.setPollenSensor(pollenSensor);
        indexer.setPollenSensor(pollenSensor);
        shooter = new Shooter(hardwareMap);
        turret = new Turret(hardwareMap);
        targetingSystem = new TargetingSystem(chassis);
        ShooterTable shooterTable = new ShooterTable();
        shooter.setTargetingSystemAndShooterTable(targetingSystem, shooterTable);
        turret.setTargetingSystem(targetingSystem);

        // Commands
        driveCommand = new DriveCommand(
                chassis,
                () -> -driver.getLeftY(),
                () -> driver.getLeftX(),
                () -> driver.getRightX()
        );

        chassis.setDefaultCommand(driveCommand);

        driver.getGamepadButton(GamepadKeys.Button.LEFT_BUMPER).whenPressed(new InstantCommand(() -> driveCommand.toggleFieldCentric()));
        driver.getGamepadButton(GamepadKeys.Button.RIGHT_BUMPER).whenPressed(new InstantCommand(() -> driveCommand.toggleHold()));

        operator.getGamepadButton(GamepadKeys.Button.LEFT_BUMPER).toggleWhenPressed(new GateOpen(gate), new GateClose(gate));
        operator.getGamepadButton(GamepadKeys.Button.RIGHT_BUMPER).toggleWhenPressed(new FlowerRampDown(flowerRamp), new FlowerRampUp(flowerRamp));

        operator.getGamepadButton(GamepadKeys.Button.B).whenPressed(new TransferCycleCommand(intake, indexer, gate));
        operator.getGamepadButton(GamepadKeys.Button.A).toggleWhenPressed(new AutoCollectCommand(intake, indexer), new ReverseCycleCommand(intake, indexer, gate));

        operator.getGamepadButton(GamepadKeys.Button.DPAD_LEFT).toggleWhenPressed(new TurretManualAngleCommand(turret, () -> turretAngleDegrees), new TurretStopCommand(turret));
        operator.getGamepadButton(GamepadKeys.Button.Y).whenPressed(new InstantCommand(() -> {
            turretAngleDegrees += 5.0;
            if (turret.getState() == Turret.State.MANUAL_ANGLE) {
                turret.setManualAngle(turretAngleDegrees);
            }
        }));
        operator.getGamepadButton(GamepadKeys.Button.X).whenPressed(new InstantCommand(() -> {
            turretAngleDegrees -= 5.0;
            if (turret.getState() == Turret.State.MANUAL_ANGLE) {
                turret.setManualAngle(turretAngleDegrees);
            }
        }));


        operator.getGamepadButton(GamepadKeys.Button.DPAD_RIGHT).toggleWhenPressed(new ShooterManualVelocityCommand(shooter, () -> shooterVelocityRPM), new ShooterStopCommand(shooter));
        operator.getGamepadButton(GamepadKeys.Button.DPAD_UP).whenPressed(new InstantCommand(() -> {
            shooterVelocityRPM = Math.max(0.0, shooterVelocityRPM + 20.0);
            if (shooter.getState() == Shooter.State.MANUAL_VELOCITY) {
                shooter.setManualVelocity(shooterVelocityRPM);
            }
        }));
        operator.getGamepadButton(GamepadKeys.Button.DPAD_DOWN).whenPressed(new InstantCommand(() -> {
            shooterVelocityRPM = Math.max(0.0, shooterVelocityRPM - 20.0);
            if (shooter.getState() == Shooter.State.MANUAL_VELOCITY) {
                shooter.setManualVelocity(shooterVelocityRPM);
            }
        }));

    }

    @Override
    public void run(){
        super.run();

        // Telemetry
        telemetryData.addData("Velocity no button", shooterVelocityRPM);
        telemetryData.addData("Velocity na flywheel", shooter.getCurrentVelocityRPM());
        telemetryData.addData("Target Velocity", shooter.getTargetVelocityRPM());
        telemetryData.addData("Distance", targetingSystem.getTargetDistanceIN());
        telemetryData.addData("", "");
        shooter.addTelemetry(telemetryData);

        telemetryData.update();
    }
}
