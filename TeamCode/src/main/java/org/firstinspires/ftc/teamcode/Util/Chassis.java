package org.firstinspires.ftc.teamcode.Util;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

//@Disabled
@TeleOp
public class Chassis extends OpMode {
    DcMotor motorFL;
    DcMotor motorFR;
    DcMotor motorBL;
    DcMotor motorBR;
    
    @Override
    public void init() {

        motorFL = hardwareMap.get(DcMotor.class, "FL");
        motorFR = hardwareMap.get(DcMotor.class, "FR");
        motorBL = hardwareMap.get(DcMotor.class, "BL");
        motorBR = hardwareMap.get(DcMotor.class, "BR");
       
        motorFL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorFR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorBL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorBR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        motorFL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorFR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorBL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorBR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        motorFL.setDirection(DcMotorSimple.Direction.REVERSE);
        motorBL.setDirection(DcMotorSimple.Direction.REVERSE);

        telemetry.addData("Hardware: ", "Initialized");
    }

    //public void init_loop(){}
    //public void start(){}

    @Override
    public void loop(){
        telemetry.addData("Hardware: ", "Running");

        double drive = -gamepad1.left_stick_y;  // frente e atrás
        double turn = -gamepad1.right_stick_x;  // gira
        double strafe = gamepad1.left_stick_x; // direita e esquerda

        double max = Math.max(Math.abs(strafe) + Math.abs(drive) + Math.abs(turn), 1);

        double drivePower = -(0.5 * gamepad1.right_trigger) + 1;
        
        motorFL.setPower(drivePower * (drive + turn + strafe) / max);
        motorFR.setPower(drivePower * (drive - turn - strafe) / max);
        motorBL.setPower(drivePower * (drive + turn - strafe) / max);
        motorBR.setPower(drivePower * (drive - turn + strafe) / max);

        telemetry.update();
    }
}