package org.firstinspires.ftc.teamcode.Util;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp
public class unitTest extends OpMode {

    DcMotor motor;

    @Override
    public void init() {
        motor = hardwareMap.get(DcMotor.class, "motor");

        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        telemetry.addData("Hardware: ", "Initialized");
    }

    @Override
    public void loop() {
        telemetry.addData("Hardware", "Running");

        double velocity = motor.getPower();
        telemetry.addData("Velocidade", velocity);

        double speed = gamepad1.left_stick_y;

        motor.setPower(speed);
    }
}
