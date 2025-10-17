package org.firstinspires.ftc.teamcode.Decode.RampRobot;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class RampRobotOuttake {
    DcMotor outtake;
    public RampRobotOuttake(HardwareMap hardwareMap) {
        outtake = hardwareMap.get(DcMotor.class, "OUT"); // Motor esquerdo do outtake
        outtake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void runOuttake(boolean one, boolean two, boolean three, boolean four){
        if(one){
            outtake.setPower(0);
        }else if(two){
            outtake.setPower(0.5);
        }else if(three){
            outtake.setPower(0.75);
        } else if(four){
            outtake.setPower(1);
        }
    }
}