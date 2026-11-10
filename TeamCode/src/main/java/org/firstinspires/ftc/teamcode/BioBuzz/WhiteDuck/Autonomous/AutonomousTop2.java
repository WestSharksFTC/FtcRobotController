package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuck.Autonomous;

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
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name = "White Duck Autonomous Tope 2", group = "WhiteDuck")
public class AutonomousTop2 extends LinearOpMode {

    private Follower follower;

    WDIntake intake = new WDIntake();
    WDIndexer indexer = new WDIndexer();
    WDOuttake outtake = new WDOuttake();
    WDTurret turret = new WDTurret();

    private final PoseFactory poseFactory = PoseFactory.degrees();

    private final Pose start = poseFactory.of(58, 132, 270);
    private final Pose parkPos = poseFactory.of(10, 118, 0);
    private final Pose parkPosControl1 = poseFactory.of(58, 110, 0);


    public Path parkPos() {
        return Paths.curve(start, parkPosControl1, parkPos).linear(start, parkPos);
    }

    // --- Comandos Auxiliares ---
    public Command runOuttakeDynamic() {
        return instant(() -> outtake.setVelocityFromLimelight(turret.getTy()));
    }

    public Command runOuttake(double velocity){
        return instant(() -> outtake.setVelocity(velocity));
    }

    public Command stopOuttake() {
        return instant(() -> outtake.stop());
    }

    public Command runIndexerFeed() {
        return instant(() -> indexer.feed());
    }

    public Command runIndexer(double power){
        return instant(() -> indexer.setPower(power));
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

    public Command stopIntake() {
        return instant(() -> intake.stop());
    }

    // Rotina autônoma corrigida sem caminhos duplicados de zero comprimento
    public Command autoRoutine() {
        return sequential(
                closeIndexerServo(),
                waitMs(15000),
                runOuttake(780),
                waitMs(3000),
                openIndexerServo(),
                runIndexer(1.0),
                waitMs(2000),
                closeIndexerServo(),
                stopOuttake(),
                stopIndexer(),
                stopIntake(),

                // Estaciona
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
        turret.init(hardwareMap, 3);

        follower.setPose(start);
        follower.update();

        waitForStart();

        schedule(autoRoutine());

        while (opModeIsActive() && !isStopRequested()) {
            follower.update();
            Scheduler.execute();

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