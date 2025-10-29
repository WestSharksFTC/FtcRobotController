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
        servoPos.scaleRange(0.5, 1.0); // set range from midpoint to 180* (when is a 180* servo)
        servoPos.setDirection(Servo.Direction.REVERSE);
        servoRot.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void setServoPos(double angle){
        servoPos.setPosition(angle);
    }

    public void setServoRot(double power){
        servoRot.setPower(power);
    }
}
