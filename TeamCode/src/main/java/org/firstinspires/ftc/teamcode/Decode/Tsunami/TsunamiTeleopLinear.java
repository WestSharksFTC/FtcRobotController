package org.firstinspires.ftc.teamcode.Decode.Tsunami;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

@Disabled
@TeleOp
public class TsunamiTeleopLinear extends LinearOpMode {

    private ElapsedTime runtime = new ElapsedTime();

    TsunamiChassis drive = new TsunamiChassis();
    TsunamiIntake intake = new TsunamiIntake();
    TsunamiOuttake outtake = new TsunamiOuttake();

    TsunamiIntake.DetectedColor detectedColor;

    double forward, strafe, turn, motorIntake, motorOuttake, robotAngle;
    boolean imuReset, autoIndexer, turnIndexer, turnMeioIndexer;
    private int slotIn = 0;
    private int slotOut = 0;

    private boolean botao_x = false;
    private boolean botao_b = false;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();


        drive.init(hardwareMap);
        intake.init(hardwareMap);
        outtake.init(hardwareMap);


        // Wait for the game to start (driver presses START)
        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            // Gamepad 1 - Chassis
            forward = -gamepad1.left_stick_y;
            strafe = gamepad1.left_stick_x;
            turn = gamepad1.right_stick_x;
            imuReset = gamepad1.left_bumper;
            robotAngle = drive.getRobotAngle();

            drive.driveFieldRelative(forward, strafe, turn, imuReset);

            // Gamepad 2 - Subsistemas
            motorIntake = gamepad2.left_trigger;
            motorOuttake = gamepad2.right_trigger;
            autoIndexer = gamepad2.aWasPressed();
            turnIndexer = gamepad2.dpadRightWasPressed();
            turnMeioIndexer = gamepad2.dpadLeftWasPressed();
            if(gamepad2.xWasPressed()){
                botao_x = !botao_x;
            }
            if(gamepad2.bWasPressed()){
                botao_b = !botao_b;
            }

            intake.setPowerMotorIn(motorIntake);
            outtake.setOuttakePower(motorOuttake);

            // Intake
            if(botao_x) {
                intake.setPowerMotorIn(0.75);
                if (slotIn == 0 && (detectedColor == TsunamiIntake.DetectedColor.PURPLE || detectedColor == TsunamiIntake.DetectedColor.GREEN)) {
                    intake.goToInPos2();
                    if (!intake.motorIndexer.isBusy()) {
                        slotIn = 1;
                    }
                }else if (slotIn == 1 && (detectedColor == TsunamiIntake.DetectedColor.PURPLE || detectedColor == TsunamiIntake.DetectedColor.GREEN)) {
                    intake.goToInPos3();
                    if (!intake.motorIndexer.isBusy()) {
                        slotIn = 2;
                    }
                }else if (slotIn == 2 && (detectedColor == TsunamiIntake.DetectedColor.PURPLE || detectedColor == TsunamiIntake.DetectedColor.GREEN)) {
                    slotIn = 0;
                    botao_x = false;
                }
            }else if(botao_b) {
                outtake.setOuttakePower(1.0);
                if (slotOut == 0) {
                    intake.goToOutPos1();
                    sleep(2000);
                    if (!intake.motorIndexer.isBusy()) {
                        sleep(250);
                        intake.setServoPos(0.40);
                        sleep(400);
                        intake.setServoPos(0.0);
                        sleep(250);
                        slotOut = 1;
                    }
                }else if (slotOut == 1) {
                    intake.goToOutPos2();
                    if (!intake.motorIndexer.isBusy()) {
                        sleep(250);
                        intake.setServoPos(0.40);
                        sleep(400);
                        intake.setServoPos(0.0);
                        sleep(250);
                        slotOut = 2;
                    }
                }else if (slotOut == 2) {
                    intake.goToOutPos3();
                    if (!intake.motorIndexer.isBusy()) {
                        sleep(250);
                        intake.setServoPos(0.40);
                        sleep(400);
                        intake.setServoPos(0.0);
                        sleep(250);
                        intake.goToInPos1();
                        sleep(1000);
                        slotOut = 0;
                        botao_b = false;
                    }
                }
            }

            if(gamepad2.dpad_down) {
                intake.setServoPos(0.0);
            }else if(gamepad2.dpad_up){
                intake.setServoPos(0.40);
            }

            if(turnIndexer) {
                intake.setMotorIndexer();
            }

            if(turnMeioIndexer){
                intake.setMeioMotorIndexer();
            }




            // Telemetrias
            telemetry.addLine("POSIÇÃO DO SELETOR DE COR");
            telemetry.addData("Posição do indexer", intake.getPositionIndexer());
            telemetry.addLine();

            telemetry.addLine("LIGA/DESLIGA O SELETOR DE COR - INTAKE");
            telemetry.addData("Seletor de cor", botao_x);
            telemetry.addLine();

            telemetry.addLine("LIGA/DESLIGA O SELETOR DE COR - OUTTAKE");
            telemetry.addData("Seletor de cor", botao_b);
            telemetry.addLine();

            telemetry.addLine("COR DETECTADA PELO SENSOR");
            telemetry.addData("Color Detected", detectedColor);
            telemetry.addLine();
            detectedColor = intake.getDetectedColor(telemetry);
            telemetry.addLine();

            telemetry.addLine("POSIÇÕES DO SERVO DA RAMPA");
            intake.getServoPos(telemetry);
            telemetry.addLine();

            telemetry.addLine("DADOS DA ODOMETRIA DO ROBÔ");
            telemetry.addData("Odometria X", drive.getOdometryX());
            telemetry.addData("Odometria y", drive.getOdometryY());
            telemetry.addData("Odometria Ângulo", drive.getOdometryAngle());
            telemetry.addLine();

            telemetry.addLine("ÂNGULO DO ROBÔ EM RELAÇÃO A ARENA");
            telemetry.addData("Angulo do robô", robotAngle);
            telemetry.addLine();

            // Show the elapsed game time and wheel power.
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.update();
        }
    }
}
