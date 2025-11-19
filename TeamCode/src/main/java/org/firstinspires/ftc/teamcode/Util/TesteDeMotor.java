package org.firstinspires.ftc.teamcode.Util;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Disabled
@TeleOp
public class TesteDeMotor extends OpMode {
    DcMotor motor1;
    DcMotor motor2;
    DcMotor motor3;
    DcMotor motor4;
    DcMotor motor5;
    DcMotor motor6;
    DcMotor motor7;
    DcMotor motor8;

    @Override
    public void init() {

        motor1 = hardwareMap.get(DcMotor.class, "1");
        motor2 = hardwareMap.get(DcMotor.class, "2");
        motor3 = hardwareMap.get(DcMotor.class, "3");
        motor4 = hardwareMap.get(DcMotor.class, "4");
        motor5 = hardwareMap.get(DcMotor.class, "5");
        motor6 = hardwareMap.get(DcMotor.class, "6");
        motor7 = hardwareMap.get(DcMotor.class, "7");
        motor8 = hardwareMap.get(DcMotor.class, "8");

        motor1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor3.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor4.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor5.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor6.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor7.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor8.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        telemetry.addData("Hardware: ", "Initialized");
    }

    //public void init_loop(){}
    //public void start(){}

    @Override
    public void loop(){
        telemetry.addData("Hardware", "Running");

        double velocity = motor1.getPower() * -6000;
        telemetry.addData("Velocidade(RPM)", velocity);

        double speed = gamepad1.left_stick_y;

        motor1.setPower(speed);
        motor2.setPower(speed);
        motor3.setPower(speed);
        motor4.setPower(speed);
        motor5.setPower(speed);
        motor6.setPower(speed);
        motor7.setPower(speed);
        motor8.setPower(speed);

        if(gamepad1.a){
            motor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            motor2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            motor3.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            motor4.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            motor5.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            motor6.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            motor7.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            motor8.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        } else if (gamepad1.b){
            motor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            motor2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            motor3.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            motor4.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            motor5.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            motor6.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            motor7.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            motor8.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        }

        telemetry.addData("Modo de condução", motor1.getZeroPowerBehavior());

        telemetry.update();
    }
}