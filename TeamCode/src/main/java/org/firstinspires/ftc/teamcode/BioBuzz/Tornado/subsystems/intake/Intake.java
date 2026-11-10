package org.firstinspires.ftc.teamcode.BioBuzz.Tornado.subsystems.intake;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.seattlesolvers.solverslib.command.SubsystemBase;

public class Intake extends SubsystemBase {
    private final DcMotor motor;
    private final Servo rampRight, rampLeft;

    public Intake(HardwareMap hw) {
        motor = hw.get(DcMotor.class, IntakeConstants.MOTOR);
        rampRight = hw.get(Servo.class, IntakeConstants.RAMP_RIGHT);
        rampLeft = hw.get(Servo.class, IntakeConstants.RAMP_LEFT);

        motor.setDirection(DcMotor.Direction.FORWARD);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        rampRight.setDirection(Servo.Direction.REVERSE);
        rampLeft.setDirection(Servo.Direction.FORWARD);
        rampRight.scaleRange(IntakeConstants.RAMP_RIGHT_MIN, IntakeConstants.RAMP_RIGHT_MAX);
        rampLeft.scaleRange(IntakeConstants.RAMP_LEFT_MIN, IntakeConstants.RAMP_LEFT_MAX);
    }

    public void feed() { motor.setPower(IntakeConstants.FEED_POWER); }
    public void reverse() { motor.setPower(IntakeConstants.REVERSE_POWER); }
    public void stop() { motor.setPower(0.0); }

    public void rampUp() { rampRight.setPosition(1.0); rampLeft.setPosition(1.0); }
    public void rampDown() { rampRight.setPosition(0.0); rampLeft.setPosition(0.0); }

    @Override
    public void periodic() { }
}
