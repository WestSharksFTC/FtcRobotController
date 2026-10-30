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
    private final Pose parkPos = poseFactory.of(10, 95, 90);

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

    // Métodos auxiliares para transformar ações dos subsistemas em Commands do Ivy
    public Command runOuttakeVelocity(double velocity) {
        return instant(() -> outtake.setVelocity(velocity));
    }

    public Command runIndexerPower(double power) {
        return instant(() -> indexer.setPower(power));
    }

    public Command openIndexerServo() {
        return instant(() -> indexer.openServo());
    }

    public Command closeIndexerServo() {
        return instant(() -> indexer.closeServo());
    }

    public Command runIntakePower(double power) {
        return instant(() -> intake.setPower(power));
    }

    public Command setFlowerRampUp(boolean up) {
        return instant(() -> {
            if (up) intake.upFlowerRamp();
            else intake.downFlowerRamp();
        });
    }

    // A rotina completa organizada usando o Ivy (Sequential / Parallel)
    public Command autoRoutine() {
        return sequential(
                runOuttakeVelocity(760),
                waitMs(3000),
                openIndexerServo(),
                runIndexerPower(1.0),
                waitMs(1000),
                closeIndexerServo(),
                runIntakePower(0.8),
                runIndexerPower(0.8),

                follow(follower, collectGardenPos()),
                waitMs(2000),
                runIndexerPower(0.0),
                runIntakePower(-0.5),

                follow(follower, shootPos2()),
                waitMs(2000),
                openIndexerServo(),
                runIndexerPower(1.0),
                waitMs(1000),
                closeIndexerServo(),
                runIntakePower(0.8),
                runIndexerPower(0.8),
                setFlowerRampUp(false), // descer rampa

                follow(follower, collectFlowerPos1()),
                waitMs(2000),
                runIndexerPower(0.0),
                runIntakePower(-0.5),

                follow(follower, shootPos3()),
                waitMs(2000),
                openIndexerServo(),
                runIndexerPower(1.0),
                waitMs(1000),
                closeIndexerServo(),
                runIntakePower(0.8),
                runIndexerPower(0.8),

                follow(follower, collectFlowerPos21()),
                follow(follower, collectFlowerPos22()),
                waitMs(2000),
                runIndexerPower(0.0),
                runIntakePower(-0.5),

                parallel(
                        follow(follower, shootPos4()),
                        setFlowerRampUp(true) // subir rampa em paralelo com o movimento
                ),
                waitMs(2000),
                openIndexerServo(),
                runIndexerPower(1.0),
                waitMs(1000),
                closeIndexerServo(),
                runIntakePower(0.0),
                runIndexerPower(0.0),

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

        follower.setPose(start);
        follower.update();

        intake.upFlowerRamp();
        indexer.closeServo();

        waitForStart();

        schedule(autoRoutine());

        while (opModeIsActive() && !isStopRequested()) {
            follower.update();
            Scheduler.execute();

            telemetry.addData("x", follower.pose().x());
            telemetry.addData("y", follower.pose().y());
            telemetry.addData("heading", follower.pose().heading());
            telemetry.addData("motor_velocity", follower.velocity());
            telemetry.update();
        }
    }
}