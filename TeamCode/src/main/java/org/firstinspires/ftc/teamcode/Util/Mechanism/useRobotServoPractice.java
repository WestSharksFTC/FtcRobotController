package org.firstinspires.ftc.teamcode.Util.Mechanism;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class useRobotServoPractice extends OpMode {

    robotServoPractice servo = new robotServoPractice();
    double leftTrigger, rightTrigger;

    @Override
    public void init() {
        servo.init(hardwareMap);
        leftTrigger = 0.0;
        rightTrigger = 0.0;
    }

    @Override
    public void loop() {
        leftTrigger = gamepad1.left_trigger;
        rightTrigger = gamepad1.right_trigger;

        servo.setServoPos(leftTrigger);
        servo.setServoRot(rightTrigger);

        if(gamepad1.a){
            servo.setServoPos(-1.0);
        }else{
            servo.setServoPos(1.0);
        }

        if(gamepad1.b){
            servo.setServoRot(1.0);
        }else{
            servo.setServoRot(0);
        }
    }
}
