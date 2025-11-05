package org.firstinspires.ftc.teamcode.Decode.Tsunami;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class TsunamiOuttake {

    private DcMotor outtakeL;
    private DcMotor outtakeR;

    public void init(HardwareMap hardwareMap){
        outtakeL = hardwareMap.get(DcMotor.class, "outtake_left");
        outtakeR = hardwareMap.get(DcMotor.class, "outtake_right");

        outtakeL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        outtakeR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        outtakeL.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void setOuttakePower(double power){
        outtakeL.setPower(power);
        outtakeR.setPower(power);
    }
}
