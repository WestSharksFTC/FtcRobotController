package org.firstinspires.ftc.teamcode.BioBuzz;

import static com.pedropathing.api.Paths.*;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.commands.Commands.*;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name = "PS5_V2-AutonomousComplete", group = "Autonomous")
public class PS5_V2 extends LinearOpMode {

    private Follower follower;

    // A fábrica de Poses em graus está correta para a nova versão!
    private final PoseFactory poseFactory = PoseFactory.degrees();

    private final Pose start = poseFactory.of(56, 8, 90);
    private final Pose moveToGarden = poseFactory.of(20, 8, 180);
    private final Pose collectGarden = poseFactory.of(8, 8, 180);
    private final Pose shoot2 = poseFactory.of(60, 132, 270);

    // Ajustado para 2 pontos de controle (Bézier Cúbica)
    private final Pose shoot2Control1 = poseFactory.of(35, 35, 0);
    private final Pose shoot2Control2 = poseFactory.of(15, 125, 0);

    private final Pose collectFlower1 = poseFactory.of(47, 132, 90);
    private final Pose collectFlower1Control1 = poseFactory.of(54, 120, 0);

    private final Pose shoot3 = poseFactory.of(60, 132, 270);
    private final Pose shoot3Control1 = poseFactory.of(54, 120, 0);

    private final Pose point6 = poseFactory.of(56, 30, -92.2457);
    private final Pose point7 = poseFactory.of(56, 8, 90);
    private final Pose point8 = poseFactory.of(8, 47, 180);
    private final Pose point9 = poseFactory.of(56, 8, 90);
    private final Pose point10 = poseFactory.of(10, 95, 90);
    private final Pose point10Control1 = poseFactory.of(20, 30, 0);

    public Command autoRoutine() {
        return sequential(
                follow(follower, moveToGarden()),
                follow(follower, collectGarden()),
                follow(follower, shoot2()),
                follow(follower, collectFlower1()),
                follow(follower, shoot3()),
                follow(follower, path6()),
                follow(follower, path7()),
                follow(follower, path8()),
                follow(follower, path9()),
                follow(follower, path10())
        );
    }

    @Override
    public void runOpMode() {
        Scheduler.reset();
        follower = Constants.create(hardwareMap);
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

            if (follower.currentPath() != null) {
                telemetry.addData("Current path distance remaining", follower.distanceToEndpoint());
            }

            telemetry.update();
        }
    }

    public Path moveToGarden() {
        return line(start, moveToGarden).linear(start.heading(), moveToGarden.heading());
    }

    public Path collectGarden() {
        return line(moveToGarden, collectGarden).linear(moveToGarden.heading(), collectGarden.heading());
    }

    public Path shoot2() {
        // Correção de Bézier Cúbica (Máximo de 2 pontos de controle)
        return curve(collectGarden, shoot2Control1, shoot2Control2, shoot2)
                .linear(collectGarden.heading(), shoot2.heading());
    }

    public Path collectFlower1() {
        return curve(shoot2, collectFlower1Control1, collectFlower1)
                .linear(shoot2.heading(), collectFlower1.heading());
    }

    public Path shoot3() {
        return curve(collectFlower1, shoot3Control1, shoot3)
                .linear(collectFlower1.heading(), shoot3.heading());
    }

    public Path path6() {
        return line(shoot3, point6).linear(shoot3.heading(), point6.heading());
    }

    public Path path7() {
        // Conectado diretamente a partir de point6
        return line(point6, point7).linear(point6.heading(), point7.heading());
    }

    public Path path8() {
        return line(point7, point8).linear(point7.heading(), point8.heading());
    }

    public Path path9() {
        return line(point8, point9).linear(point8.heading(), point9.heading());
    }

    public Path path10() {
        return curve(point9, point10Control1, point10).constant(point10.heading());
    }
}