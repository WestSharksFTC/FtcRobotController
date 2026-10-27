package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuck.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class WDOuttake {
    public DcMotorEx outtake;

    public void init(HardwareMap hardwareMap){
        // Motor
        outtake = hardwareMap.get(DcMotorEx.class, "outtake");

        outtake.setDirection(DcMotorEx.Direction.FORWARD);

        outtake.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

        outtake.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);

        outtake.setVelocityPIDFCoefficients(0.0, 0.0, 0.0, 20.0);
    }

    public void setVelocity(double velocity){
        outtake.setVelocity(velocity);
    }

    public double getVelocity(){
        return outtake.getVelocity();
    }
}