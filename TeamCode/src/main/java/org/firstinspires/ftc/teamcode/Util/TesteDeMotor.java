package org.firstinspires.ftc.teamcode.Util;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

//@Disabled
@TeleOp
public class TesteDeMotor extends OpMode {
    DcMotor motor;

    @Override
    public void init() {

        motor = hardwareMap.get(DcMotor.class, "teste");

        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        telemetry.addData("Hardware: ", "Initialized");
    }

    //public void init_loop(){}
    //public void start(){}

    @Override
    public void loop(){
        telemetry.addData("Hardware: ", "Running");

        double velocity = motor.getPower() * 1000;
        telemetry.addData("Velocidade: ", velocity);

        motor.setPower(gamepad1.left_stick_y);

        telemetry.update();
    }
}