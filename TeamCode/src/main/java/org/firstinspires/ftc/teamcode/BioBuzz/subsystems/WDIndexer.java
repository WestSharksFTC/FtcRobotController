package org.firstinspires.ftc.teamcode.BioBuzz.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class WDIndexer {
    public DcMotor indexer;
    public Servo pollenLimiter;

    public void init(HardwareMap hardwareMap){
        // Motor
        indexer = hardwareMap.get(DcMotor.class, "indexer");
        pollenLimiter = hardwareMap.get(Servo.class, "pollenLimiter");

        indexer.setDirection(DcMotor.Direction.REVERSE);
        pollenLimiter.setDirection(Servo.Direction.FORWARD);

        indexer.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        indexer.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        pollenLimiter.scaleRange(0.05, 0.3);
    }

    public void setPower(double power){
        indexer.setPower(power);
    }

    public void openServo(){
        pollenLimiter.setPosition(0.0);
    }

    public void closeServo(){
        pollenLimiter.setPosition(1.0);
    }

    public String isClosed(){
        return pollenLimiter.getPosition() == 1.0 ? "CLOSED" : "OPENED";
    }

}
