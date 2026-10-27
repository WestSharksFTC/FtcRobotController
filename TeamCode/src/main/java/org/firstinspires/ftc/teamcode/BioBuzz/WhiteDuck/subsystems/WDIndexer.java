package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuck.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class WDIndexer {
    public DcMotor indexer;
    public Servo pollenLimiter;
    public DistanceSensor pollenSensor;

    public void init(HardwareMap hardwareMap){
        // Motor
        indexer = hardwareMap.get(DcMotor.class, "indexer");
        pollenLimiter = hardwareMap.get(Servo.class, "pollenLimiter");
        pollenSensor = hardwareMap.get(DistanceSensor.class, "pollenSensor");

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

    public double getDistanceCM() {
        return pollenSensor.getDistance(DistanceUnit.CM);
    }
}