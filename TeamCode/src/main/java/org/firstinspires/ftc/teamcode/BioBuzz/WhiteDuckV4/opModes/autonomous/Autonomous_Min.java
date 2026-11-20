package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.opModes.autonomous;

import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;

import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commandGroups.CollectCommandGroup;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commandGroups.TransferCommandGroup;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.chassis.DriveCommand;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.flowerRamp.FlowerRampCommand;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.shooter.ShooterAutoVelocityCommand;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.turret.TurretAutoAimCommand;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.opModes.teleOp.TeleOpConstants;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.chassis.Chassis;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.flowerRamp.FlowerRamp;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.gate.Gate;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.indexer.Indexer;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.intake.Intake;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.shooter.Shooter;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.turret.Turret;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.targeting.TargetingSystem;

@Autonomous(name = "White Duck V4 Autonomous Min", group = "WhiteDuckV4")
public class Autonomous_Min extends OpMode {
    private CommandScheduler commandScheduler;
    private Follower follower;

    private final PoseFactory poseFactory = PoseFactory.degrees();

    private final Pose start = poseFactory.of(56, 8, 90);
    private final Pose parkPos = poseFactory.of(10, 95, 0);
    private final Pose parkPosControl1 = poseFactory.of(25, 25, 0);

    public Path parkPos() {
        return Paths.curve(start, parkPosControl1, parkPos).linear(start, parkPos);
    }

    // Subsystems
    private Chassis chassis;
    private Intake intake;
    private Indexer indexer;
    private Gate gate;
    private Turret turret;
    private Shooter shooter;

    // Targeting
    private TargetingSystem targetingSystem;

    // Commands
    private TurretAutoAimCommand turretAutoAimCommand;
    private ShooterAutoVelocityCommand shooterAutoVelocityCommand;

    // Command Groups
    private CollectCommandGroup collectCommandGroup;
    private TransferCommandGroup transferCommandGroup;

    @Override
    public void init(){
        // Scheduler
        commandScheduler = CommandScheduler.getInstance();

        // Subsystems
        chassis = new Chassis();
        intake = new Intake();
        indexer = new Indexer();
        gate = new Gate();
        turret = new Turret();
        shooter = new Shooter();

        // HardwareMap dos Subsystems
        chassis.init(hardwareMap);
        intake.init(hardwareMap);
        indexer.init(hardwareMap);
        gate.init(hardwareMap);
        turret.init(hardwareMap);
        shooter.init(hardwareMap);

        // Targeting System
        targetingSystem = new TargetingSystem(chassis);

        // Commands
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


}
