package org.firstinspires.ftc.teamcode.Util.Mechanism;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@Disabled
@TeleOp
public class useRobotServoPractice extends OpMode {

    robotServoPractice servo = new robotServoPractice();
    double rightTrigger;

    @Override
    public void init() {
        servo.init(hardwareMap);
        rightTrigger = 0.0;// continuo
    }

    @Override
    public void loop() {
        rightTrigger = gamepad1.right_trigger; // continuo

        if(gamepad1.a){
            servo.setServoPos(0.0);
        }else if(gamepad1.b) {
            servo.setServoPos(1.0);
        }

        servo.setServoRot(rightTrigger); // continuo


    }
}
