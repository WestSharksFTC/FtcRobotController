package org.firstinspires.ftc.teamcode.Decode.RI30H;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class RI30HTeleOp extends OpMode {
    //instanciando as classes
    RI30HChassis chassis;
    RI30HOuttake outtake;

    @Override
    public void init(){
        chassis = new RI30HChassis(hardwareMap);
        outtake = new RI30HOuttake(hardwareMap);
    }

    @Override
    public void loop(){
        telemetry.addLine("Angulo do robô: " + chassis.imu.getRobotYawPitchRollAngles().getYaw());
        telemetry.addLine("Velocidade do spinner: " + outtake.motorSpinner.getPower());
        telemetry.addLine("Posição do servo: " + outtake.servoBreak.getPosition());
        chassis.andar(gamepad1.left_stick_y, -gamepad1.right_stick_x, -gamepad1.left_stick_x, gamepad1.right_trigger, gamepad1.left_bumper);
        outtake.spin(gamepad2.left_stick_y);
        outtake.servo(gamepad1.dpad_right, gamepad1.dpad_left);

        telemetry.update();
    }
}