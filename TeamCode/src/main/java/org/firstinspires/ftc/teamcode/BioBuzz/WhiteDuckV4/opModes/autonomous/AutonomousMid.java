package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.opModes.autonomous;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.command.ParallelCommandGroup;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;
import com.seattlesolvers.solverslib.command.WaitCommand;
import com.seattlesolvers.solverslib.pedroCommand.FollowPathCommand;
import com.seattlesolvers.solverslib.util.TelemetryData;

import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commandGroups.ReverseCycleCommand;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commandGroups.TransferCycleCommand;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.flowerRamp.FlowerRampDown;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.flowerRamp.FlowerRampUp;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.indexer.IndexerStop;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.intake.IntakeStop;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.shooter.ShooterAutoVelocityCommand;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.shooter.ShooterStopCommand;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.turret.TurretAutoAimCommand;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.turret.TurretRecentralizeCommand;
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

@Autonomous
public class AutonomousMid extends CommandOpMode{

    // Telemetry
    private TelemetryData telemetryData;

    // Follower
    private Follower follower;
    private AutonomousPoses autonomousPoses;
    private OpModeStorage opModeStorage;

    // Subsystems
    private Chassis chassis;
    private FlowerRamp flowerRamp;
    private Gate gate;
    private Intake intake;
    private Indexer indexer;
    private PollenSensor pollenSensor;
    private Shooter shooter;
    private Turret turret;


    @Override
    public void initialize() {
        super.reset();

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

        intake.setPollenSensor(pollenSensor);
        indexer.setPollenSensor(pollenSensor);

        TargetingSystem targetingSystem = new TargetingSystem(chassis);
        ShooterTable shooterTable = new ShooterTable();

        turret.setTargetingSystem(targetingSystem);
        shooter.setTargetingSystemAndShooterTable(targetingSystem, shooterTable);

        autonomousPoses = new AutonomousPoses();
        opModeStorage = new OpModeStorage();
        follower = chassis.getFollower();
        follower.setPose(opModeStorage.getAutonomousStartPose());

        schedule(autoRoutine());
    }

    @Override
    public void run(){
        super.run();

        opModeStorage.setAutonomousEndPose(chassis.getPose());

        // Telemetry
        telemetryData.addData("==========", "CHASSIS ==========");
        telemetryData.addData("startAutoPose", opModeStorage.getAutonomousStartPose());
        telemetryData.addData("endAutoPose", opModeStorage.getAutonomousEndPose());
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

    private Command autoRoutine() {
        return new SequentialCommandGroup(
                new TurretAutoAimCommand(turret),
                new ShooterAutoVelocityCommand(shooter),

                new WaitCommand(2000),
                new TransferCycleCommand(intake, indexer, gate),

                new FollowPathCommand(follower, autonomousPoses.shoot1PosToCollect1Pos()),
                new WaitCommand(2000),

                new FollowPathCommand(follower, autonomousPoses.collect1PosToShoot2Pos()),
                new TransferCycleCommand(intake, indexer, gate),

                new FlowerRampDown(flowerRamp),
                new ReverseCycleCommand(intake, indexer, gate),

                new FollowPathCommand(follower, autonomousPoses.shoot2PosToCollect2AndShoot3Pos()),
                new WaitCommand(3000),
                new TransferCycleCommand(intake, indexer, gate),

                new ParallelCommandGroup(
                        new ReverseCycleCommand(intake, indexer, gate),
                        new FollowPathCommand(follower, autonomousPoses.shoot3PosToCollect3Pos())
                ),

                new WaitCommand(3000),

                new FollowPathCommand(follower, autonomousPoses.collect3PosToShoot4Pos()),
                new TransferCycleCommand(intake, indexer, gate),

                new ParallelCommandGroup(
                        new FlowerRampUp(flowerRamp),
                        new ReverseCycleCommand(intake, indexer, gate),
                        new FollowPathCommand(follower, autonomousPoses.shoot4PosToParkPos())
                ),

                new IntakeStop(intake),
                new IndexerStop(indexer),
                new ShooterStopCommand(shooter),
                new TurretRecentralizeCommand(turret)
        );
    }
}
