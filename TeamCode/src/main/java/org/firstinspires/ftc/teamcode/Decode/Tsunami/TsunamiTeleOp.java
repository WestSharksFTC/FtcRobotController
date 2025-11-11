package org.firstinspires.ftc.teamcode.Decode.Tsunami;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@Disabled
@TeleOp
public class TsunamiTeleOp extends OpMode {
    TsunamiChassis drive = new TsunamiChassis();
    TsunamiIntake intake = new TsunamiIntake();
    TsunamiOuttake outtake = new TsunamiOuttake();

    TsunamiIntake.DetectedColor detectedColor;

    double forward, strafe, turn, motorIntake, motorOuttake, robotAngle;
    boolean imuReset, autoIndexer, turnIndexer;

    @Override
    public void init() {
        drive.init(hardwareMap);
        intake.init(hardwareMap);
        outtake.init(hardwareMap);
    }

    @Override
    public void loop() {
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

        intake.setPowerMotorIn(motorIntake);
        outtake.setOuttakePower(motorOuttake);

        if(gamepad2.dpad_down) {
            intake.setServoPos(0.0);
        }else if(gamepad2.dpad_up){
            intake.setServoPos(0.40);
        }

        if(turnIndexer) {
            intake.setMotorIndexer();
        }



        // Telemetrias
        telemetry.addLine("POSIÇÃO DO SELETOR DE COR");
        telemetry.addData("Posição do indexer", intake.getPositionIndexer());
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
    }
}