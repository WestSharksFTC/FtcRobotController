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
        forward = -gamepad1.left_stick_y;
        strafe = gamepad1.left_stick_x;
        turn = gamepad1.right_stick_x;
        imuReset = gamepad1.left_bumper;
        motorIntake = gamepad1.left_trigger;
        motorOuttake = gamepad1.right_trigger;
        robotAngle = drive.getRobotAngle();

        drive.driveFieldRelative(forward, strafe, turn, imuReset);

        intake.setPowerMotorIn(motorIntake);

        outtake.setOuttakePower(motorOuttake);

        if(gamepad1.dpad_down) {
            intake.setServoPos(0.0);
        }else if(gamepad1.dpad_up){
            intake.setServoPos(0.30);
        }

        if(gamepad1.dpad_left) {
            intake.setPowerMotorIn(0.0);
        }else if(gamepad1.dpad_right){
            intake.setPowerMotorIn(0.5);
        }

        if(gamepad1.a) {
            outtake.setOuttakePower(0.0);
        }else if(gamepad1.y){
            outtake.setOuttakePower(1.0);
        }

        // Telemetrias
        intake.getServoPos(telemetry);

        telemetry.addData("Angulo do robô", robotAngle);

        telemetry.addData("Odometria X", drive.getOdometryX());
        telemetry.addData("Odometria X", drive.getOdometryY());
        telemetry.addData("Odometria X", drive.getOdometryAngle());
    }
}
