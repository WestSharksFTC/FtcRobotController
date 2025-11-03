package org.firstinspires.ftc.teamcode.Util.Mechanism;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class robotMotorPractice {
    private DcMotor motor; // linearSlideMotor0
    private double ticksPerRev; // revolution

    public void init(HardwareMap hardwareMap){
        // DC motor

        motor = hardwareMap.get(DcMotor.class, "motor");
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        ticksPerRev = motor.getMotorType().getTicksPerRev();
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motor.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void setMotorSpeed(double speed){
        motor.setPower(speed);
    }

    public double getMotorRevs() {
        return motor.getCurrentPosition() / ticksPerRev * 2; // normalize ticks to revolutions 2:1
    }

    public void setMotorZeroBehavior(DcMotor.ZeroPowerBehavior zeroBehavior){
        motor.setZeroPowerBehavior(zeroBehavior);
    }

    public void setMotorBrake(String brakeType){
        if(brakeType.equals("BRAKE")){
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }else if(brakeType.equals("FLOAT")){
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        }
    }
}
