package org.firstinspires.ftc.teamcode.Decode.RampRobot;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class RampRobotIntake {
    DcMotor intake;
    public RampRobotIntake(HardwareMap hardwareMap) {
        intake = hardwareMap.get(DcMotor.class, "IN");
        intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intake.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void runIntake(boolean one, boolean two, boolean three, boolean four){
        if(one){
            intake.setPower(0.25);
        }else if(two){
            intake.setPower(0.5);
        }else if(three){
            intake.setPower(0.75);
        } else if(four){
            intake.setPower(1);
        }
    }
}