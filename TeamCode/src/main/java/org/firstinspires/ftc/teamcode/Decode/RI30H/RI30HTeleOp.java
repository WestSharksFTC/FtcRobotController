package org.firstinspires.ftc.teamcode.Decode.RI30H;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public class RI30HTeleOp extends OpMode {
    RI30HChassis chassis;
    RI30HIntake intake;
    RI30HOuttake outtake;
    public void init(){
        chassis = new RI30HChassis(hardwareMap);
    }
    public void loop(){
        telemetry.addLine("Angulo do robô: "+ chassis.imu.getRobotYawPitchRollAngles().getYaw());
        chassis.andar(-gamepad1.left_stick_y, -gamepad1.right_stick_x, -gamepad1.right_stick_x, gamepad1.right_trigger, gamepad1.left_bumper);

        telemetry.update();
    }
}
