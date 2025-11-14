package org.firstinspires.ftc.teamcode.Decode.Tsunami;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class TsunamiOuttake {
    private DcMotor outtakeL;
    private DcMotor outtakeR;
    private Servo servoOuttake;



    public void init(HardwareMap hardwareMap){
        outtakeL = hardwareMap.get(DcMotor.class, "outtake_left");
        outtakeR = hardwareMap.get(DcMotor.class, "outtake_right");

        outtakeL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        outtakeR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        outtakeL.setDirection(DcMotorSimple.Direction.REVERSE);

        servoOuttake = hardwareMap.get(Servo.class, "servo_outtake");
        servoOuttake.scaleRange(0.0, 0.2);
        servoOuttake.setPosition(0.0);

    }

    public void setOuttakePower(double power){
        outtakeL.setPower(power);
        outtakeR.setPower(power);
    }
}
