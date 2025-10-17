package org.firstinspires.ftc.teamcode.Decode.RampRobot;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class RampRobotIntake {
    Servo servoG;
    public RampRobotIntake(HardwareMap hardwareMap) {
        servoG = hardwareMap.get(Servo.class, "Servo"); // Motor da trava das bolas
    }

    //trava manual das bolas
    public void servo(boolean left, boolean right){
        if(left){
            servoG.setPosition(0);
        }else if(right){
            servoG.setPosition(0.5);
        }
    }
}