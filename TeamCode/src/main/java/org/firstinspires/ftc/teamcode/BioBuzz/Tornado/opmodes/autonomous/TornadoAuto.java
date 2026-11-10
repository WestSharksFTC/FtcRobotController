package org.firstinspires.ftc.teamcode.BioBuzz.Tornado.opmodes.autonomous;

import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.ParallelCommandGroup;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;
import com.seattlesolvers.solverslib.command.WaitCommand;
import com.seattlesolvers.solverslib.pedroCommand.FollowPathCommand;

import org.firstinspires.ftc.teamcode.BioBuzz.Tornado.commands.CommandConstants;
import org.firstinspires.ftc.teamcode.BioBuzz.Tornado.commands.HoldPollen;
import org.firstinspires.ftc.teamcode.BioBuzz.Tornado.commands.ShootAll;
import org.firstinspires.ftc.teamcode.BioBuzz.Tornado.commands.StartCollecting;
import org.firstinspires.ftc.teamcode.BioBuzz.Tornado.field.AutoPoses;
import org.firstinspires.ftc.teamcode.BioBuzz.Tornado.field.FieldConstants;
import org.firstinspires.ftc.teamcode.BioBuzz.Tornado.subsystems.indexer.Indexer;
import org.firstinspires.ftc.teamcode.BioBuzz.Tornado.subsystems.intake.Intake;
import org.firstinspires.ftc.teamcode.BioBuzz.Tornado.subsystems.outtake.Outtake;
import org.firstinspires.ftc.teamcode.BioBuzz.Tornado.subsystems.turret.Turret;
import org.firstinspires.ftc.teamcode.BioBuzz.Tornado.util.PosePersistency;
import org.firstinspires.ftc.teamcode.BioBuzz.Tornado.pedroPathing.TornadoConstants;

/**
 * Autonomo com OpMode normal (CommandOpMode) em vez de LinearOpMode.
 * A rotina e uma SequentialCommandGroup: cada passo so comeca quando o anterior termina.
 * Alianca no init: gamepad1 X = AZUL, B = VERMELHO (vermelho = poses espelhadas; CONFIRA no campo).
 */
@Autonomous(name = "Tornado Auto", group = "Tornado")
public class TornadoAuto extends CommandOpMode {
    private Follower follower;
    private Intake intake;
    private Indexer indexer;
    private Outtake outtake;
    private Turret turret;

    private boolean redAlliance = false;
    private PoseFactory pf;

    private Pose start;

    private Pose p(double[] a) {
        return pf.of(a[0], a[1], a[2]);
    }

    private Command follow(Path path) {
        return new FollowPathCommand(follower, path);
    }

    private Command routine() {
        Pose collectGarden = p(AutoPoses.COLLECT_GARDEN);
        Pose shoot2Start = p(AutoPoses.SHOOT2_START), shoot2 = p(AutoPoses.SHOOT2), shoot2Ctrl = p(AutoPoses.SHOOT2_CONTROL);
        Pose flower1 = p(AutoPoses.COLLECT_FLOWER1), shoot3 = p(AutoPoses.SHOOT3);
        Pose f21Start = p(AutoPoses.FLOWER21_START), f21 = p(AutoPoses.FLOWER21), f21Ctrl = p(AutoPoses.FLOWER21_CONTROL);
        Pose f22 = p(AutoPoses.FLOWER22), shoot4 = p(AutoPoses.SHOOT4);
        Pose parkStart = p(AutoPoses.PARK_START), park = p(AutoPoses.PARK);

        Path pCollectGarden = Paths.line(start, collectGarden).linear(start, collectGarden);
        Path pShoot2 = Paths.curve(shoot2Start, shoot2Ctrl, shoot2).linear(shoot2Start, shoot2);
        Path pFlower1 = Paths.line(shoot2, flower1).constant(flower1);
        Path pShoot3 = Paths.line(flower1, shoot3).constant(shoot3);
        Path pFlower21 = Paths.curve(f21Start, f21Ctrl, f21).linear(f21Start, f21);
        Path pFlower22 = Paths.line(f21, f22).constant(f22);
        Path pShoot4 = Paths.line(f22, shoot4).constant(shoot4);
        Path pPark = Paths.line(parkStart, park).linear(parkStart, park);

        return new SequentialCommandGroup(
                new ShootAll(outtake, indexer, turret),            // pre-carga
                new StartCollecting(intake, indexer),

                follow(pCollectGarden),
                new WaitCommand(CommandConstants.COLLECT_WAIT_MS),
                new HoldPollen(intake, indexer),

                follow(pShoot2),
                new ShootAll(outtake, indexer, turret),
                new StartCollecting(intake, indexer),
                new InstantCommand(intake::rampDown),

                follow(pFlower1),
                new WaitCommand(CommandConstants.COLLECT_WAIT_MS),
                new HoldPollen(intake, indexer),

                follow(pShoot3),
                new ShootAll(outtake, indexer, turret),
                new StartCollecting(intake, indexer),

                follow(pFlower21),
                follow(pFlower22),
                new WaitCommand(CommandConstants.COLLECT_WAIT_MS),
                new HoldPollen(intake, indexer),

                new ParallelCommandGroup(
                        follow(pShoot4),
                        new InstantCommand(intake::rampUp)),
                new ShootAll(outtake, indexer, turret),
                new InstantCommand(() -> {
                    intake.stop();
                    indexer.stop();
                }),

                follow(pPark)
        );
    }

    @Override
    public void initialize() {
        super.reset();
        follower = TornadoConstants.create(hardwareMap);   // so o follower: nao cria o Chassis (ele mexe nos motores)
        intake = new Intake(hardwareMap);
        indexer = new Indexer(hardwareMap);
        outtake = new Outtake(hardwareMap);
        turret = new Turret(hardwareMap);
        register(intake, indexer, outtake, turret);

        intake.rampUp();
        indexer.closeGate();
    }

    @Override
    public void initialize_loop() {
        if (gamepad1.x) redAlliance = false;
        if (gamepad1.b) redAlliance = true;
        telemetry.addData("Alianca (X=azul, B=vermelho)", redAlliance ? "VERMELHO" : "AZUL");
        telemetry.update();
    }

    @Override
    public void preRun() {
        pf = redAlliance
                ? PoseFactory.degrees().mirrorAroundPoint(FieldConstants.CENTER_X, FieldConstants.CENTER_Y)
                : PoseFactory.degrees();
        start = p(AutoPoses.START);

        turret.setAlliance(redAlliance);
        turret.setMode(Turret.Mode.AUTO);
        PosePersistency.saveAlliance(redAlliance);

        follower.setPose(start);
        follower.update();
        schedule(routine());
    }

    @Override
    public void run() {
        follower.update();
        super.run();
        PosePersistency.savePose(follower.pose());

        telemetry.addData("x", follower.pose().x());
        telemetry.addData("y", follower.pose().y());
        telemetry.addData("heading (graus)", Math.toDegrees(follower.pose().heading()));
        telemetry.addData("Outtake vel/alvo", "%.0f / %.0f", outtake.getVelocity(), outtake.getTarget());
        telemetry.update();
    }

    @Override
    public void end() {
        intake.stop();
        indexer.stop();
        outtake.stop();
        PosePersistency.savePose(follower.pose());
    }
}
