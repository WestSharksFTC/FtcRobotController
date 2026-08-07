package org.firstinspires.ftc.teamcode.BioBuzz;


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class Prototype extends OpMode {
    // Declaração dos membros OpMode
    private DcMotor rightFrontMotor = null;
    private DcMotor leftFrontMotor = null;
    private DcMotor rightBackMotor = null;
    private DcMotor leftBackMotor = null;
    private DcMotor intake = null;
    private DcMotor outtake = null;
    private Servo servoOuttake = null;

    boolean v = false;
    boolean in = false;
    double speed = 1;
    double intakePower = 0;
    int gear = 1;


    @Override
    public void init() {
        // Connection with physical motors
        rightFrontMotor = hardwareMap.get(DcMotor.class, "rf");
        rightBackMotor = hardwareMap.get(DcMotor.class, "rb");
        leftFrontMotor = hardwareMap.get(DcMotor.class, "lf");
        leftBackMotor = hardwareMap.get(DcMotor.class, "lb");
        intake = hardwareMap.get(DcMotor.class, "in");
        outtake = hardwareMap.get(DcMotor.class, "viper");

        rightFrontMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightBackMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftFrontMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftBackMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        outtake.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        rightFrontMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBackMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftFrontMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBackMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        outtake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


        rightFrontMotor.setDirection(DcMotor.Direction.FORWARD);
        rightBackMotor.setDirection(DcMotor.Direction.FORWARD);
        leftFrontMotor.setDirection(DcMotor.Direction.FORWARD);
        leftBackMotor.setDirection(DcMotor.Direction.REVERSE);
        intake.setDirection(DcMotor.Direction.REVERSE);
        outtake.setDirection(DcMotorSimple.Direction.REVERSE);

        outtake.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        outtake.setTargetPosition(0);
        outtake.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        outtake.setPower(1);


        servoOuttake = hardwareMap.get(Servo.class, "servo_outtake");
        servoOuttake.setDirection(Servo.Direction.REVERSE);
        servoOuttake.scaleRange(0.275, 0.375);
        servoOuttake.setPosition(0.0);

        telemetry.addData("status", "INITIALIZED");
    }

    public void loop() {
        telemetry.addData("status", "RUNNING");

        if(gamepad1.aWasPressed()){
            in = !in;
            intakePower = in ? 0.5 : 0;
            intake.setPower(intakePower);
        }

        if (gamepad1.bWasPressed()) {
            v = !v;
            speed = v ? 0.5 : 1;
        }

        if(gamepad1.rightBumperWasPressed() && gear < 5){
            gear += 1;
        }

        if(gamepad1.leftBumperWasPressed() && gear > 1){
            gear -= 1;
        }

        if(gamepad1.dpadRightWasPressed()){
            servoOuttake.setPosition(1.0);
        }

        if(gamepad1.dpadLeftWasPressed()){
            servoOuttake.setPosition(0.0);
        }

        if(gamepad1.dpadUpWasPressed()){
            outtake.setTargetPosition(3250);
        }

        if(gamepad1.dpadDownWasPressed()){
            outtake.setTargetPosition(0);
        }

        double velocity = 1 - (gamepad1.right_trigger * 0.5);

        double turnStick = gamepad1.right_stick_x * speed * (gear * 0.2);
        double drive = gamepad1.left_stick_y * velocity * (gear * 0.2);
        double strafe = gamepad1.left_stick_x * velocity * (gear * 0.2);

        rightFrontMotor.setPower(drive + turnStick + strafe);
        rightBackMotor.setPower(drive + turnStick - strafe);
        leftFrontMotor.setPower(drive - turnStick - strafe);
        leftBackMotor.setPower(drive - turnStick + strafe);

        telemetry.addLine();
        telemetry.addData("intakePower", in ? "ATIVO" : "INATIVO");
        telemetry.addData("turnBrake", v ? "ATIVO" : "INATIVO");
        telemetry.addData("driveBrake", "%.2f %%", (-200 * velocity + 200));
        telemetry.addLine();
        telemetry.addData("Marcha", gear);
        telemetry.addLine();
        telemetry.addData("Servo Position", servoOuttake.getPosition());
        telemetry.addData("Viper Position", outtake.getCurrentPosition());
    }
}