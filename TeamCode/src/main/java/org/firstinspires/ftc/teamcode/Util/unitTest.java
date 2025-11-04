package org.firstinspires.ftc.teamcode.Util;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp
public class unitTest extends OpMode {

    DcMotor motorL;
    DcMotor motorR;

    @Override
    public void init() {
        motorL = hardwareMap.get(DcMotor.class, "motorL");
        motorR = hardwareMap.get(DcMotor.class, "motorR");

        motorL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        motorR.setDirection(DcMotor.Direction.REVERSE);

        telemetry.addData("Hardware", "Initialized");
    }

    @Override
    public void loop() {
        telemetry.addData("Hardware", "Running");

        double velocityLeft = motorL.getPower();
        double velocityRight = motorR.getPower();

        telemetry.addData("Velocidade", velocityLeft);
        telemetry.addData("Velocidade", velocityRight);

        double speed = gamepad1.left_stick_y;

        motorL.setPower(speed);
        motorR.setPower(speed);
    }
}
