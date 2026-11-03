package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuck.Autonomous;

import static com.pedropathing.api.Paths.*;
import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;

import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;

import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;

import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuck.subsystems.WDIndexer;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuck.subsystems.WDIntake;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuck.subsystems.WDOuttake;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuck.subsystems.WDTurret;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.commands.Commands.*;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.groups.Groups.parallel;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name = "White Duck Autonomous Complete", group = "WhiteDuck")
public class AutonomousComplete extends LinearOpMode {

    private Follower follower;

    WDIntake intake = new WDIntake();
    WDIndexer indexer = new WDIndexer();
    WDOuttake outtake = new WDOuttake();
    WDTurret turret = new WDTurret();

    private final PoseFactory poseFactory = PoseFactory.degrees();

    private final Pose start = poseFactory.of(56, 8, 180);
    private final Pose collectGardenPos = poseFactory.of(8, 8, 180);
    private final Pose shootPos2Start = poseFactory.of(8, 8, 180);
    private final Pose shootPos2 = poseFactory.of(46, 118, 90);
    private final Pose shootPos2Control1 = poseFactory.of(30, 110, 0);
    private final Pose collectFlowerPos1 = poseFactory.of(46, 125, 90);
    private final Pose shootPos3 = poseFactory.of(46, 125, 90);
    private final Pose collectFlowerPos21Start = poseFactory.of(46, 125, 90);
    private final Pose collectFlowerPos21 = poseFactory.of(22, 47, 180);
    private final Pose collectFlowerPos21Control1 = poseFactory.of(30, 110, 0);
    private final Pose collectFlowerPos22 = poseFactory.of(16, 47, 180);
    private final Pose shootPos4 = poseFactory.of(56, 8, 180);
    private final Pose parkPosStart = poseFactory.of(56, 8, 180);
    private final Pose parkPos = poseFactory.of(10, 95, 0);

    public Path collectGardenPos() {
        return Paths.line(start, collectGardenPos).linear(start, collectGardenPos);
    }

    public Path shootPos2() {
        return Paths.curve(shootPos2Start, shootPos2Control1, shootPos2).linear(shootPos2Start, shootPos2);
    }

    public Path collectFlowerPos1() {
        return Paths.line(shootPos2, collectFlowerPos1).constant(collectFlowerPos1);
    }

    public Path shootPos3() {
        return Paths.line(collectFlowerPos1, shootPos3).constant(shootPos3);
    }

    public Path collectFlowerPos21() {
        return Paths.curve(collectFlowerPos21Start, collectFlowerPos21Control1, collectFlowerPos21).linear(collectFlowerPos21Start, collectFlowerPos21);
    }

    public Path collectFlowerPos22() {
        return Paths.line(collectFlowerPos21, collectFlowerPos22).constant(collectFlowerPos22);
    }

    public Path shootPos4() {
        return Paths.line(collectFlowerPos22, shootPos4).constant(shootPos4);
    }

    public Path parkPos() {
        return Paths.line(parkPosStart, parkPos).linear(parkPosStart, parkPos);
    }

    // --- Comandos Auxiliares ---
    public Command runOuttakeDynamic() {
        return instant(() -> outtake.setVelocityFromLimelight(turret.getTy()));
    }

    public Command stopOuttake() {
        return instant(() -> outtake.stop());
    }

    public Command runIndexerFeed() {
        return instant(() -> indexer.feed());
    }

    public Command stopIndexer() {
        return instant(() -> indexer.stop());
    }

    public Command openIndexerServo() {
        return instant(() -> indexer.openServo());
    }

    public Command closeIndexerServo() {
        return instant(() -> indexer.closeServo());
    }

    public Command runIntakeFeed() {
        return instant(() -> intake.feed());
    }

    public Command runIntakeReverse() {
        return instant(() -> intake.reverse());
    }

    public Command stopIntake() {
        return instant(() -> intake.stop());
    }

    public Command setFlowerRampUp(boolean up) {
        return instant(() -> {
            if (up) intake.upFlowerRamp();
            else intake.downFlowerRamp();
        });
    }

    // Rotina autônoma completa com outtake dinâmico ajustado pela Limelight
    public Command autoRoutine() {
        return sequential(
                runOuttakeDynamic(), // Liga o outtake dinâmico baseado na Limelight
                waitMs(3000),
                openIndexerServo(),
                runIndexerFeed(),
                waitMs(1000),
                closeIndexerServo(),
                stopOuttake(),
                runIntakeFeed(),
                runIndexerFeed(),

                follow(follower, collectGardenPos()),
                waitMs(2000),
                stopIndexer(),
                runIntakeReverse(),

                follow(follower, shootPos2()),
                runOuttakeDynamic(), // Atualiza velocidade pelo ty da Limelight na posição 2
                waitMs(2000),
                openIndexerServo(),
                runIndexerFeed(),
                waitMs(1000),
                closeIndexerServo(),
                stopOuttake(),
                runIntakeFeed(),
                runIndexerFeed(),
                setFlowerRampUp(false),

                follow(follower, collectFlowerPos1()),
                waitMs(2000),
                stopIndexer(),
                runIntakeReverse(),

                follow(follower, shootPos3()),
                runOuttakeDynamic(),
                waitMs(2000),
                openIndexerServo(),
                runIndexerFeed(),
                waitMs(1000),
                closeIndexerServo(),
                stopOuttake(),
                runIntakeFeed(),
                runIndexerFeed(),

                follow(follower, collectFlowerPos21()),
                follow(follower, collectFlowerPos22()),
                waitMs(2000),
                stopIndexer(),
                runIntakeReverse(),

                parallel(
                        follow(follower, shootPos4()),
                        setFlowerRampUp(true)
                ),
                runOuttakeDynamic(),
                waitMs(2000),
                openIndexerServo(),
                runIndexerFeed(),
                waitMs(1000),
                closeIndexerServo(),
                stopOuttake(),
                stopIntake(),
                stopIndexer(),

                follow(follower, parkPos())
        );
    }

    @Override
    public void runOpMode() {
        Scheduler.reset();
        follower = Constants.create(hardwareMap);

        intake.init(hardwareMap);
        indexer.init(hardwareMap);
        outtake.init(hardwareMap);
        turret.init(hardwareMap);

        follower.setPose(start);
        follower.update();

        intake.upFlowerRamp();
        indexer.closeServo();

        waitForStart();

        schedule(autoRoutine());

        while (opModeIsActive() && !isStopRequested()) {
            follower.update();
            Scheduler.execute();

            turret.autoAim();

            telemetry.addData("x", follower.pose().x());
            telemetry.addData("y", follower.pose().y());
            telemetry.addData("heading", follower.pose().heading());
            telemetry.addData("Limelight Tx", turret.getTx());
            telemetry.addData("Limelight Ty", turret.getTy());
            telemetry.addData("Outtake Velocity", outtake.getVelocity());
            telemetry.update();
        }
    }
}