package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuck;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuck.subsystems.WDChassis;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuck.subsystems.WDIndexer;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuck.subsystems.WDIntake;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuck.subsystems.WDOuttake;

@TeleOp(name = "WhiteDuck Completo", group = "WhiteDuck")
public class TheWhiteDuck extends OpMode {
    WDChassis chassis = new WDChassis();
    WDIntake intake = new WDIntake();
    WDIndexer indexer = new WDIndexer();
    WDOuttake outtake = new WDOuttake();
    boolean driveMode;

    public enum RobotState {
        IDLE,
        INTAKING,
        HOLDING,
        SHOOTING_SPINUP,
        SHOOTING_FIRE
    }

    private RobotState currentState = RobotState.IDLE;
    private int pollenCount = 0;
    private boolean previousSensorState = false;
    private ElapsedTime shootTimer = new ElapsedTime();

    @Override
    public void init() {
        chassis.init(hardwareMap);
        intake.init(hardwareMap);
        indexer.init(hardwareMap);
        outtake.init(hardwareMap);

        intake.upFlowerRamp();
    }

    @Override
    public void loop() {
        // Chassis
        double drive = gamepad1.left_stick_y;
        double strafe = gamepad1.left_stick_x;
        double turn = gamepad1.right_stick_x;

        if (gamepad1.leftStickButtonWasPressed()) chassis.resetYaw();
        if (gamepad1.rightStickButtonWasPressed()) driveMode = !driveMode;

        if (driveMode) {
            chassis.driveFieldRelative(drive, strafe, turn);
        } else {
            chassis.drive(drive, strafe, turn);
        }

        // Servo da rampa da flower
        if(gamepad1.dpadUpWasPressed()){
            intake.upFlowerRamp();
        }else if(gamepad1.dpadDownWasPressed()){
            intake.downFlowerRamp();
        }

        // State machine para coleta e disparo
        switch (currentState) {
            case IDLE:
                intake.setPower(0.0);
                indexer.setPower(0.0);
                outtake.setVelocity(0.0);
                indexer.closeServo();
                pollenCount = 0;

                // Inicia o ciclo
                if (gamepad1.xWasPressed()) {
                    currentState = RobotState.INTAKING;
                }
                break;

            case INTAKING:
                indexer.closeServo();
                outtake.setVelocity(0.0);
                intake.setPower(0.8);
                indexer.setPower(0.8);

                boolean currentSensorState = (indexer.getDistanceCM() < 4.0);

                if (currentSensorState && !previousSensorState) {
                    pollenCount++;
                }
                previousSensorState = currentSensorState;

                if (pollenCount >= 4) {
                    currentState = RobotState.HOLDING;
                }

                // OPCIONAL: Botão para o shoot de forma manual
                if (gamepad1.yWasPressed()) {
                    currentState = RobotState.SHOOTING_SPINUP;
                }
                break;

            case HOLDING:
                indexer.setPower(0.0);
                intake.setPower(-0.2);

                // Botão para iniciar o shoot
                if (gamepad1.yWasPressed()) {
                    currentState = RobotState.SHOOTING_SPINUP;
                }
                break;

            case SHOOTING_SPINUP:
                intake.setPower(-0.2);
                indexer.setPower(0.0);
                outtake.setVelocity(800.0);

                if (Math.abs(outtake.getVelocity() - 800.0) < 25.0) {
                    shootTimer.reset(); // Reseta um cronômetro para o tempo de disparo
                    currentState = RobotState.SHOOTING_FIRE;
                }
                break;

            case SHOOTING_FIRE:
                indexer.openServo();
                indexer.setPower(1.0);

                if (shootTimer.seconds() > 3.0) {
                    pollenCount = 0;
                    currentState = RobotState.INTAKING;
                }
                break;
        }

        telemetry.addData("Estado do Robô", currentState);
        telemetry.addData("Contagem de Pollens", pollenCount);
        telemetry.addData("Distância Sensor (CM)", indexer.getDistanceCM());
        telemetry.addData("Outtake Velocity", outtake.getVelocity());
        telemetry.addData("Outtake Timer", shootTimer);
        telemetry.update();
    }
}
