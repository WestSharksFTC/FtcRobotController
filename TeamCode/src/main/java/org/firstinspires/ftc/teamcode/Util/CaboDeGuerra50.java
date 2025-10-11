package org.firstinspires.ftc.teamcode.Util;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

//@Disabled
@TeleOp
public class CaboDeGuerra50 extends OpMode {
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

        double velocity = motor.getPower() * 450;
        telemetry.addData("RPM do motor: ", velocity);

        motor.setPower(0.75);

        telemetry.update();
    }
}