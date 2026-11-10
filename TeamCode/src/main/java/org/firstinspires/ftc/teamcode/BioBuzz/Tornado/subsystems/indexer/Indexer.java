package org.firstinspires.ftc.teamcode.BioBuzz.Tornado.subsystems.indexer;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.seattlesolvers.solverslib.command.SubsystemBase;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Indexer extends SubsystemBase {
    private final DcMotor motor;
    private final Servo gate;
    private final DistanceSensor left, right;

    // Cache: os sensores sao lidos 1x por ciclo no periodic(), nao a cada chamada.
    private double leftCm = Double.MAX_VALUE, rightCm = Double.MAX_VALUE;

    public Indexer(HardwareMap hw) {
        motor = hw.get(DcMotor.class, IndexerConstants.MOTOR);
        gate = hw.get(Servo.class, IndexerConstants.GATE);
        left = hw.get(DistanceSensor.class, IndexerConstants.SENSOR_LEFT);
        right = hw.get(DistanceSensor.class, IndexerConstants.SENSOR_RIGHT);

        motor.setDirection(DcMotor.Direction.REVERSE);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        gate.setDirection(Servo.Direction.FORWARD);
        gate.scaleRange(IndexerConstants.GATE_MIN, IndexerConstants.GATE_MAX);
    }

    @Override
    public void periodic() {
        leftCm = left.getDistance(DistanceUnit.CM);
        rightCm = right.getDistance(DistanceUnit.CM);
    }

    public void feed() { motor.setPower(IndexerConstants.FEED_POWER); }
    public void reverse() { motor.setPower(IndexerConstants.REVERSE_POWER); }
    public void stop() { motor.setPower(0.0); }

    public void openGate() { gate.setPosition(0.0); }
    public void closeGate() { gate.setPosition(1.0); }
    public boolean isClosed() { return gate.getPosition() >= 0.25; }

    public double getLeftCm() { return leftCm; }
    public double getRightCm() { return rightCm; }

    public boolean hasPollen() {
        return leftCm <= IndexerConstants.DETECTION_THRESHOLD_CM
                || rightCm <= IndexerConstants.DETECTION_THRESHOLD_CM;
    }
}
