package org.firstinspires.ftc.teamcode.Util;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class unitTestServo extends OpMode {
    private Servo servo;

    private double range;

    @Override
    public void init() {
        servo = hardwareMap.get(Servo.class, "servo");
    }

    @Override
    public void loop() {
        if(gamepad1.a){
            servo.setDirection(Servo.Direction.FORWARD);
        }else if(gamepad1.b){
            servo.setDirection(Servo.Direction.REVERSE);
        }

        if(gamepad1.x){
            range = 0.5;
            servo.scaleRange(range, 1.0);
        }else if(gamepad1.y){
            range = 0.0;
            servo.scaleRange(range, 1.0);
        }

        if(gamepad1.dpad_left){
            servo.setPosition(0);
        }else if(gamepad1.dpad_right){
            servo.setPosition(1);
        }

        telemetry.addData("Posição atual", servo.getPosition());
        telemetry.addData("Direção Atual", servo.getDirection());
        telemetry.addData("Range Atual", range);
    }
}
