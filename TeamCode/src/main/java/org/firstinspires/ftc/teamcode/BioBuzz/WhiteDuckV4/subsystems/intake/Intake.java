package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.intake;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import com.seattlesolvers.solverslib.command.SubsystemBase;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Intake extends SubsystemBase{

    private DcMotor intake;
    private Servo flowerRampRight;
    private Servo flowerRampLeft;

    private String currentState = "STOP";
    private double commandedPower = 0.0;
    private String rampState = "UNKNOWN";

    public void init(HardwareMap hardwareMap) {
        intake = hardwareMap.get(DcMotor.class, IntakeHardware.INTAKE_MOTOR);
        flowerRampRight = hardwareMap.get(Servo.class, IntakeHardware.FLOWER_RAMP_SERVO_RIGHT);
        flowerRampLeft = hardwareMap.get(Servo.class, IntakeHardware.FLOWER_RAMP_SERVO_LEFT);

        intake.setDirection(DcMotor.Direction.FORWARD);
        flowerRampRight.setDirection(Servo.Direction.REVERSE);
        flowerRampLeft.setDirection(Servo.Direction.FORWARD);

        intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        flowerRampRight.scaleRange(IntakeConstants.RAMP_SCALE_RANGE_MIN, IntakeConstants.RAMP_SCALE_RANGE_MAX);
        flowerRampLeft.scaleRange(1 - IntakeConstants.RAMP_SCALE_RANGE_MAX, 1 - IntakeConstants.RAMP_SCALE_RANGE_MIN);
    }


    // Motor do Intake
    public void transfer(){
        intake.setPower(IntakeConstants.TRANSFER_POWER);
    }

    public void feed() {
        intake.setPower(IntakeConstants.FEED_POWER);
    }

    public void reverse() {
        intake.setPower(IntakeConstants.REVERSE_POWER);
    }

    public void stop() {
        intake.setPower(IntakeConstants.STOP_POWER);
    }

    public double getPower(){
        return intake.getPower();
    }


    // Servo da Rampa
    public void downFlowerRamp() {
        flowerRampRight.setPosition(IntakeConstants.RAMP_POSITION_DOWN);
        flowerRampLeft.setPosition(IntakeConstants.RAMP_POSITION_DOWN);
    }

    public void upFlowerRamp() {
        flowerRampRight.setPosition(IntakeConstants.RAMP_POSITION_UP);
        flowerRampLeft.setPosition(IntakeConstants.RAMP_POSITION_UP);
    }

    public double getLeftServoPosition() {
        return flowerRampLeft.getPosition();
    }

    public double getRightServoPosition() {
        return flowerRampRight.getPosition();
    }


    // Telemetria do Intake
    public void addTelemetry(Telemetry telemetry) {
        telemetry.addLine("========== INTAKE ==========");
        telemetry.addData("Current Power", "%.2f", getPower());
        telemetry.addData("Ramp Right Position", "%.2f", getRightServoPosition());
        telemetry.addData("Ramp Left Position", "%.2f", getLeftServoPosition());
        telemetry.addLine();
    }


    @Override
    public void periodic(){
        flowerRampRight.scaleRange(IntakeConstants.RAMP_SCALE_RANGE_MIN, IntakeConstants.RAMP_SCALE_RANGE_MAX);
        flowerRampLeft.scaleRange(1 - IntakeConstants.RAMP_SCALE_RANGE_MAX, 1 - IntakeConstants.RAMP_SCALE_RANGE_MIN);
    }
}