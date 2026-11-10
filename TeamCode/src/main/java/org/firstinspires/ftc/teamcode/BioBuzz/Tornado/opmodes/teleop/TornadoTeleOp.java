package org.firstinspires.ftc.teamcode.BioBuzz.Tornado.opmodes.teleop;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;

import org.firstinspires.ftc.teamcode.BioBuzz.Tornado.logic.ShootingStateMachine;
import org.firstinspires.ftc.teamcode.BioBuzz.Tornado.logic.ShootingStateMachine.Outputs;
import org.firstinspires.ftc.teamcode.BioBuzz.Tornado.subsystems.chassis.Chassis;
import org.firstinspires.ftc.teamcode.BioBuzz.Tornado.subsystems.indexer.Indexer;
import org.firstinspires.ftc.teamcode.BioBuzz.Tornado.subsystems.indexer.IndexerConstants;
import org.firstinspires.ftc.teamcode.BioBuzz.Tornado.subsystems.intake.Intake;
import org.firstinspires.ftc.teamcode.BioBuzz.Tornado.subsystems.outtake.Outtake;
import org.firstinspires.ftc.teamcode.BioBuzz.Tornado.subsystems.turret.Turret;
import org.firstinspires.ftc.teamcode.BioBuzz.Tornado.util.PosePersistency;

/**
 * TeleOp unico. Alianca no init: operador DPAD_UP = VERMELHO, DPAD_DOWN = AZUL
 * (se veio de um autonomo, ja comeca com a alianca dele).
 * Operador X: alterna a mira da torreta entre LIMELIGHT e ODOMETRIA.
 */
@TeleOp(name = "Tornado TeleOp", group = "Tornado")
public class TornadoTeleOp extends CommandOpMode {
    private Chassis chassis;
    private Intake intake;
    private Indexer indexer;
    private Outtake outtake;
    private Turret turret;

    private GamepadEx driver, operator;
    private final ShootingStateMachine machine = new ShootingStateMachine();

    private boolean shootRequested = false;
    private boolean fieldCentric = false;
    private boolean parking = false;
    private boolean redAlliance = false;
    private boolean odometryAim = false;

    @Override
    public void initialize() {
        super.reset();
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        chassis = new Chassis(hardwareMap);
        intake = new Intake(hardwareMap);
        indexer = new Indexer(hardwareMap);
        outtake = new Outtake(hardwareMap);
        turret = new Turret(hardwareMap);
        register(chassis, intake, indexer, outtake, turret);

        // pose e alianca que o autonomo deixou (se rodou antes)
        if (PosePersistency.hasPose()) {
            chassis.follower.setPose(PosePersistency.getPose(null));
        }
        redAlliance = PosePersistency.isRed(false);
        turret.setAlliance(redAlliance);
        turret.setPoseSource(chassis.follower::pose);
        turret.setMode(Turret.Mode.AUTO);

        driver = new GamepadEx(gamepad1);
        operator = new GamepadEx(gamepad2);

        operator.getGamepadButton(GamepadKeys.Button.B)
                .whenPressed(new InstantCommand(() -> shootRequested = true));
        operator.getGamepadButton(GamepadKeys.Button.X)
                .whenPressed(new InstantCommand(this::toggleOdometryAim));
        operator.getGamepadButton(GamepadKeys.Button.DPAD_UP)
                .whenPressed(new InstantCommand(intake::rampUp));
        operator.getGamepadButton(GamepadKeys.Button.DPAD_DOWN)
                .whenPressed(new InstantCommand(intake::rampDown));
        driver.getGamepadButton(GamepadKeys.Button.LEFT_STICK_BUTTON)
                .whenPressed(new InstantCommand(chassis::resetYaw));
        driver.getGamepadButton(GamepadKeys.Button.RIGHT_STICK_BUTTON)
                .whenPressed(new InstantCommand(() -> fieldCentric = !fieldCentric));
        driver.getGamepadButton(GamepadKeys.Button.BACK)
                .whenPressed(new InstantCommand(this::toggleParking));

        chassis.resetYaw();
        intake.rampUp();
    }

    @Override
    public void initialize_loop() {
        if (gamepad2.dpad_up) {
            redAlliance = true;
            turret.setAlliance(true);
        } else if (gamepad2.dpad_down) {
            redAlliance = false;
            turret.setAlliance(false);
        }
        telemetry.addData("Alianca", redAlliance ? "VERMELHO" : "AZUL");
        telemetry.addData("Pose salva do autonomo", PosePersistency.hasPose() ? "sim" : "NAO (odometria sem referencia)");
        telemetry.update();
    }

    private void toggleOdometryAim() {
        odometryAim = !odometryAim;
        turret.setMode(odometryAim ? Turret.Mode.ODOMETRY : Turret.Mode.AUTO);
    }

    private void toggleParking() {
        parking = !parking;
        if (parking) chassis.follower.follow(chassis.parkPath());
        else chassis.follower.stop();
    }

    @Override
    public void run() {
        machine.capacity = IndexerConstants.CAPACITY;

        chassis.follower.update();
        if (!parking) {
            double f = -gamepad1.left_stick_y, s = gamepad1.left_stick_x, r = gamepad1.right_stick_x;
            if (fieldCentric) chassis.driveFieldRelative(f, s, r);
            else chassis.drive(f, s, r);
        }

        // 1) botoes + periodic() de todos os subsistemas
        super.run();

        // 2) decide o estado com os dados ja atualizados
        machine.update(indexer.hasPollen(), shootRequested, outtake.atSpeed(), getRuntime());
        shootRequested = false;

        // 3) aplica as saidas do estado atual
        apply(machine.outputs());

        PosePersistency.savePose(chassis.follower.pose());

        telemetry.addData("Estado", machine.getState());
        telemetry.addData("Pollens", machine.pollenCount());
        telemetry.addData("Mira da torreta", odometryAim ? "ODOMETRIA" : "LIMELIGHT");
        telemetry.addData("Outtake vel/alvo", "%.0f / %.0f", outtake.getVelocity(), outtake.getTarget());
        telemetry.addData("Limelight", "valido=%b tx=%.1f ty=%.1f", turret.hasTarget(), turret.getTx(), turret.getTy());
        telemetry.addData("Torreta ticks / fugindo", "%d / %b", turret.getTicks(), turret.isEscaping());
        telemetry.addData("Distancia (odometria)", "%.1f", turret.getOdometryDistance());
        telemetry.addData("Bateria", outtake.getVoltage());
        telemetry.update();
    }

    @Override
    public void end() {
        PosePersistency.savePose(chassis.follower.pose());
    }

    private void apply(Outputs o) {
        switch (o.intake) {
            case FEED: intake.feed(); break;
            case REVERSE: intake.reverse(); break;
            default: intake.stop();
        }
        switch (o.indexer) {
            case FEED: indexer.feed(); break;
            case REVERSE: indexer.reverse(); break;
            default: indexer.stop();
        }
        if (o.gateOpen) indexer.openGate(); else indexer.closeGate();
        if (o.outtakeOn) outtake.spin(turret::getTy); else outtake.stop();
    }
}
