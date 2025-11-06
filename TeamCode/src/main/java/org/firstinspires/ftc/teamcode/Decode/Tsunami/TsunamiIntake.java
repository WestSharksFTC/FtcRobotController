package org.firstinspires.ftc.teamcode.Decode.Tsunami;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class TsunamiIntake {

    private DcMotor motorIn;
    private DcMotor motorIndexer;
    private Servo servoRampL;
    private Servo servoRampR;

    public void init(HardwareMap hardwareMap){
        motorIn = hardwareMap.get(DcMotor.class, "motorIntake");
        motorIn.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorIn.setDirection(DcMotor.Direction.REVERSE);

        motorIndexer = hardwareMap.get(DcMotor.class, "motor_indexer");
        motorIndexer.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorIndexer.setPower(0.0);
        motorIndexer.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorIndexer.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorIndexer.setTargetPosition(0);

        servoRampL = hardwareMap.get(Servo.class, "servoRampLeft");
        servoRampR = hardwareMap.get(Servo.class, "servoRampRight");

        servoRampR.setDirection(Servo.Direction.REVERSE);

        servoRampL.scaleRange(0.0, 0.85);
        servoRampR.scaleRange(0.0, 0.85);

        servoRampL.setPosition(0.0);
        servoRampR.setPosition(0.0);
    }

    public void setPowerMotorIn(double power) {
        motorIn.setPower(power);
    }

    public void setServoPos(double angle){
        servoRampL.setPosition(angle);
        servoRampR.setPosition(angle);
    }

    public void getServoPos(Telemetry telemetry){
        double servoLeftPos = servoRampL.getPosition();
        double servoRightPos = servoRampR.getPosition();

        telemetry.addData("Servo Esquerdo", servoLeftPos);
        telemetry.addData("Servo Direito", servoRightPos);
    }

    public void setMotorIndexer(){
        double position;
    }
}
