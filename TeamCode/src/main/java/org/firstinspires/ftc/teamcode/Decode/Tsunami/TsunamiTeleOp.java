package org.firstinspires.ftc.teamcode.Decode.Tsunami;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class TsunamiTeleOp extends OpMode {
    TsunamiChassis drive = new TsunamiChassis();
    TsunamiIntake intake = new TsunamiIntake();
    double forward, strafe, turn, motorIntake, robotAngle;
    boolean imuReset;

    @Override
    public void init() {
        drive.init(hardwareMap);
        intake.init(hardwareMap);
    }

    @Override
    public void loop() {
        forward = -gamepad1.left_stick_y;
        strafe = gamepad1.left_stick_x;
        turn = gamepad1.right_stick_x;
        imuReset = gamepad1.left_bumper;
        motorIntake = gamepad1.right_trigger;
        robotAngle = drive.getRobotAngle();

        drive.driveFieldRelative(forward, strafe, turn, imuReset);

        telemetry.addData("Angulo do robô", robotAngle);

        intake.setPowerMotorIn(motorIntake);

        if(gamepad1.dpad_up) {
            intake.setServoPos(0.0);
        }else if(gamepad1.dpad_right){
            intake.setServoPos(0.20);
        }else if(gamepad1.dpad_down){
            intake.setServoPos(0.30);
        }else if(gamepad1.dpad_left){
            // Sobe certo
            intake.setServoPos(0.40);
        }


        intake.getServoPos(telemetry);

        telemetry.addData("Odometria X", drive.getOdometryX());
        telemetry.addData("Odometria X", drive.getOdometryY());
        telemetry.addData("Odometria X", drive.getOdometryAngle());


        drive.getOdometryX();
        drive.getOdometryY();
        drive.getOdometryAngle();
    }
}
