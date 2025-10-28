package org.firstinspires.ftc.teamcode.Util.Mechanism;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class useRobotMotorPractice extends OpMode {

    robotMotorPractice motorPractice = new robotMotorPractice();

    @Override
    public void init(){
        motorPractice.init(hardwareMap);
    }

    @Override
    public void loop() {
        double motorSpeed = gamepad1.left_stick_y;

        motorPractice.setMotorSpeed(motorSpeed);

        telemetry.addData("Motor Revs", motorPractice.getMotorRevs());
    }
}
