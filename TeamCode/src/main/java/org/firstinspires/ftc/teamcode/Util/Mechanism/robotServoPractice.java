package org.firstinspires.ftc.teamcode.Util.Mechanism;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class robotServoPractice {

    private Servo servoPos;
    private CRServo servoRot;

    public void init(HardwareMap hardwareMap){
        servoPos = hardwareMap.get(Servo.class, "servo");
        servoRot = hardwareMap.get(CRServo.class, "crservo");
    }

    public void setServoPos(double angle){
        servoPos.setPosition(angle);
    }

    public void setServoRot(double power){
        servoRot.setPower(power);
    }
}
