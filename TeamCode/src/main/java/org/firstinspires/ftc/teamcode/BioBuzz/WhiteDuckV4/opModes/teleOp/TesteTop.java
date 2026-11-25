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
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.turret.TurretManualVelocityCommand;
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
    private GamepadEx operator;

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

    private double shooterVeleocityRPM = 0.0;
    private double turretAngleDegrees = 0.0;


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

        autoCollect = new AutoCollectCommand(
                intake,
                indexer,
                pollenSensor
        );

        intake.setDefaultCommand(autoCollect);
        indexer.setDefaultCommand(autoCollect);

        driver.getGamepadButton(GamepadKeys.Button.LEFT_BUMPER).whenPressed(new InstantCommand(() -> driveCommand.toggleFieldCentric()));
        driver.getGamepadButton(GamepadKeys.Button.RIGHT_BUMPER).whenPressed(new InstantCommand(() -> driveCommand.toggleHold()));

        operator.getGamepadButton(GamepadKeys.Button.LEFT_BUMPER).toggleWhenPressed(new GateOpen(gate), new GateClose(gate));
        operator.getGamepadButton(GamepadKeys.Button.RIGHT_BUMPER).toggleWhenPressed(new FlowerRampDown(flowerRamp), new FlowerRampUp(flowerRamp));

        operator.getGamepadButton(GamepadKeys.Button.B).whenPressed(new TransferCycleCommand(intake, indexer, gate));
        operator.getGamepadButton(GamepadKeys.Button.A).whenPressed(new ReverseCycleCommand(intake, indexer, gate));

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


        operator.getGamepadButton(GamepadKeys.Button.DPAD_RIGHT).toggleWhenPressed(new ShooterManualVelocityCommand(shooter, () -> shooterVeleocityRPM), new ShooterStopCommand(shooter));
        operator.getGamepadButton(GamepadKeys.Button.DPAD_UP).whenPressed(new InstantCommand(() -> {
            shooterVeleocityRPM = Math.max(0.0, shooterVeleocityRPM + 20.0);
            if (shooter.getState() == Shooter.State.MANUAL_VELOCITY) {
                shooter.setManualVelocity(shooterVeleocityRPM);
            }
        }));
        operator.getGamepadButton(GamepadKeys.Button.DPAD_DOWN).whenPressed(new InstantCommand(() -> {
            shooterVeleocityRPM = Math.max(0.0, shooterVeleocityRPM - 20.0);
            if (shooter.getState() == Shooter.State.MANUAL_VELOCITY) {
                shooter.setManualVelocity(shooterVeleocityRPM);
            }
        }));

    }

    @Override
    public void run(){
        super.run();

        // Telemetry
        // telemetryData.addData("Field Centric", driveCommand.isFieldCentric() ? "ON" : "OFF");
        // telemetryData.addData("Hold Mode", driveCommand.isHoldEnabled() ? "ON" : "OFF");
        // telemetryData.addData("Intake State", intake.getState());
        // telemetryData.addData("Indexer State", indexer.getState());
        // telemetryData.addData("Gate State", gate.getState());
        // telemetryData.addData("Flower Ramp State", flowerRamp.getState());
        // telemetryData.addData("Velocity no button", shooterVeleocityRPM);
        telemetryData.addData("Angle no button", turretAngleDegrees);
        // pollenSensor.addTelemetry(telemetryData);
        // shooter.addTelemetry(telemetryData);
        turret.addTelemetry(telemetryData);
        telemetryData.update();
    }
}
