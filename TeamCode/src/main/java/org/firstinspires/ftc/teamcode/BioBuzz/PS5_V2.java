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
import static com.pedropathing.ivy.groups.Groups.parallel; // Importante para rodar intake e movimento juntos!
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Autonomous(name = "PS5_V2-AutonomousComplete", group = "Autonomous")
public class PS5_V2 extends LinearOpMode {

    private Follower follower;
    private DcMotor intake;
    private DcMotor outtake;
    private CRServo servoIntake;

    private final PoseFactory poseFactory = PoseFactory.degrees();

    private final Pose start = poseFactory.of(56, 8, 90);
    private final Pose startToGardenTurning = poseFactory.of(20, 8, 180);
    private final Pose gardenTurningToGardenCollect = poseFactory.of(8, 8, 180);
    private final Pose gardenCollectToShootPos2 = poseFactory.of(60, 132, 270);
    private final Pose gardenCollectToShootPos2Control1 = poseFactory.of(35, 35, 0);
    private final Pose gardenCollectToShootPos2Control2 = poseFactory.of(15, 125, 0);
    private final Pose gardenCollectToShootPos2Control3 = poseFactory.of(55, 100, 0);
    private final Pose shootPos2ToCollectFlower1 = poseFactory.of(47, 132, 90);
    private final Pose shootPos2ToCollectFlower1Control1 = poseFactory.of(54, 120, 0);
    private final Pose collectFlower1ToShootPos3 = poseFactory.of(60, 132, 270);
    private final Pose collectFlower1ToShootPos3Control1 = poseFactory.of(54, 120, 0);
    private final Pose shootPos3ToCollectFlower2 = poseFactory.of(8, 47, 180);
    private final Pose shootPos3ToCollectFlower2Control1 = poseFactory.of(10, 100, 0);
    private final Pose shootPos3ToCollectFlower2Control2 = poseFactory.of(37.8364, 40.7743, 0);
    private final Pose collectFlower2ToShootPos4 = poseFactory.of(56, 8, 90);
    private final Pose shootPos4ToParkPos = poseFactory.of(10.293, 95, 90);
    private final Pose shootPos4ToParkPosControl1 = poseFactory.of(20, 30, 0);

    public Path startToGardenTurning() {
        return line(start, startToGardenTurning).linear(start, startToGardenTurning);
    }

    public Path gardenTurningToGardenCollect() {
        return line(startToGardenTurning, gardenTurningToGardenCollect).tangent();
    }

    public Path gardenCollectToShootPos2() {
        return curve(gardenTurningToGardenCollect, gardenCollectToShootPos2Control1, gardenCollectToShootPos2Control2, gardenCollectToShootPos2Control3, gardenCollectToShootPos2).linear(gardenTurningToGardenCollect, gardenCollectToShootPos2);
    }

    public Path shootPos2ToCollectFlower1() {
        return curve(gardenCollectToShootPos2, shootPos2ToCollectFlower1Control1, shootPos2ToCollectFlower1).linear(gardenCollectToShootPos2, shootPos2ToCollectFlower1);
    }

    public Path collectFlower1ToShootPos3() {
        return curve(shootPos2ToCollectFlower1, collectFlower1ToShootPos3Control1, collectFlower1ToShootPos3).linear(shootPos2ToCollectFlower1, collectFlower1ToShootPos3);
    }

    public Path shootPos3ToCollectFlower2() {
        return curve(collectFlower1ToShootPos3, shootPos3ToCollectFlower2Control1, shootPos3ToCollectFlower2Control2, shootPos3ToCollectFlower2).linear(collectFlower1ToShootPos3, shootPos3ToCollectFlower2);
    }

    public Path collectFlower2ToShootPos4() {
        return line(shootPos3ToCollectFlower2, collectFlower2ToShootPos4).linear(shootPos3ToCollectFlower2, collectFlower2ToShootPos4);
    }

    public Path shootPos4ToParkPos() {
        return curve(collectFlower2ToShootPos4, shootPos4ToParkPosControl1, shootPos4ToParkPos).constant(shootPos4ToParkPos);
    }

    // Comando auxiliar customizado para ligar/desligar o intake via Ivy Scheduler
    public Command setIntakePower(double power) {
        return instant(() -> {
            if (intake != null) intake.setPower(power);
            if (servoIntake != null) servoIntake.setPower(power > 0 ? 1.0 : 0.0);
        });
    }

    public Command setOuttakePower(double power) {
        return instant(() -> {
            if (outtake != null) outtake.setPower(power);
        });
    }

    public Command autoRoutine() {
        return sequential(
                setOuttakePower(1.0),
                waitMs(500),
                setIntakePower(0.5),
                waitMs(2000),

                // 1. Vai para o jardim ligando o intake
                parallel(
                        follow(follower, startToGardenTurning()),
                        setIntakePower(0.8)
                ),

                follow(follower, gardenTurningToGardenCollect()),

                waitMs(1500),

                // 2. Vai para a posição de tiro 2 com o intake ligado
                parallel(
                        follow(follower, gardenCollectToShootPos2()),
                        setIntakePower(1.0)
                ),

                setIntakePower(0.0),

                waitMs(2000),

                // 3. Vai para coletar a flor 1 com o intake ligado
                parallel(
                        follow(follower, shootPos2ToCollectFlower1()),
                        setIntakePower(0.8)
                ),

                waitMs(1500),

                // 4. Vai para a posição de tiro 3 e desliga o intake
                parallel(
                        follow(follower, collectFlower1ToShootPos3()),
                        setIntakePower(1.0)
                ),

                setIntakePower(0.0),

                waitMs(2000),

                // 5. Demais trajetos finais
                parallel(
                        follow(follower, shootPos3ToCollectFlower2()),
                        setIntakePower(0.8)
                ),

                parallel(
                        follow(follower, collectFlower2ToShootPos4()),
                        setIntakePower(1.0)
                ),

                setIntakePower(0.0),

                waitMs(2000),

                follow(follower, shootPos4ToParkPos())
        );
    }

    @Override
    public void runOpMode() {
        Scheduler.reset();
        follower = Constants.create(hardwareMap);

        // Inicializa os motores do Intake também no autônomo
        intake = hardwareMap.get(DcMotor.class, "in");
        outtake = hardwareMap.get(DcMotor.class, "out");
        servoIntake = hardwareMap.get(CRServo.class, "servo_intake");
        intake.setDirection(DcMotor.Direction.FORWARD);
        outtake.setDirection(DcMotorSimple.Direction.REVERSE);
        servoIntake.setDirection(CRServo.Direction.FORWARD);

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
            telemetry.addData("motor_velocity", follower.velocity());
            telemetry.update();
        }
    }
}