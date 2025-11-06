package org.firstinspires.ftc.teamcode.Decode.Tsunami;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class TsunamiTeleOp extends OpMode {
    TsunamiChassis drive = new TsunamiChassis();
    TsunamiIntake intake = new TsunamiIntake();
    TsunamiOuttake outtake = new TsunamiOuttake();
    double forward, strafe, turn, motorIntake, motorOuttake, robotAngle;
    boolean imuReset;

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

        intake.setPowerMotorIn(motorIntake);

        outtake.setOuttakePower(motorOuttake);

        if(gamepad2.dpad_down) {
            intake.setServoPos(0.0);
        }else if(gamepad2.dpad_up){
            intake.setServoPos(0.30);
        }

        if(gamepad2.dpadRightWasPressed()) {
            intake.setMotorIndexer();
        }

        if(gamepad2.x){
            intake.goToInPos1();
        }else if(gamepad2.y){
            intake.goToInPos2();
        }else if(gamepad2.a){
            intake.goToOutPos1();
        }else if(gamepad2.b){
            intake.goToOutPos2();
        }


        // Telemetrias
        telemetry.addLine("POSIÇÃO DO SELETOR DE COR");
        telemetry.addData("Posição do indexer", intake.getPositionIndexer());
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
