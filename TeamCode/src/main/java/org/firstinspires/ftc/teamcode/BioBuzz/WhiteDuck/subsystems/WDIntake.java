package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuck.subsystems;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class WDIntake {

    public DcMotor intake;
    public Servo flowerRampRight;
    public Servo flowerRampLeft;

    public void init(HardwareMap hardwareMap){
        // Motor
        intake = hardwareMap.get(DcMotor.class, "intake");
        flowerRampRight = hardwareMap.get(Servo.class, "flowerRampRight");
        flowerRampLeft = hardwareMap.get(Servo.class, "flowerRampLeft");

        intake.setDirection(DcMotor.Direction.FORWARD);
        flowerRampRight.setDirection(Servo.Direction.REVERSE);
        flowerRampLeft.setDirection(Servo.Direction.FORWARD);

        intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        flowerRampRight.scaleRange(0.050, 0.125);
        flowerRampLeft.scaleRange(0.90, 0.975);
    }

    public void setPower(double power){
        intake.setPower(power);
    }

    private void setFlowerRampPosition(double position){
        flowerRampRight.setPosition(position);
        flowerRampLeft.setPosition(position);
    }

    public void downFlowerRamp(){
        setFlowerRampPosition(0.0);
    }

    public void upFlowerRamp(){
        setFlowerRampPosition(1.0);
    }

    public void setRightFlowerRampPosition(double servoPosition){
        flowerRampRight.setPosition(servoPosition);
    }

    public void setLeftFlowerRampPosition(double servoPosition){
        flowerRampLeft.setPosition(servoPosition);
    }

    public double getLeftServoPosition(){
        return flowerRampLeft.getPosition();
    }

    public double getRightServoPosition(){
        return flowerRampRight.getPosition();
    }
}