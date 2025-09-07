package org.firstinspires.ftc.teamcode.Decode.RI30H;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public class RI30HTeleOp extends OpMode {
    //instanciando as classes
    RI30HChassis chassis;
    RI30HIntake intake;
    RI30HOuttake outtake;

    double velocity = 0;


    public void init(){
        chassis = new RI30HChassis(hardwareMap);
        outtake = new RI30HOuttake(hardwareMap);
    }

    public void loop(){
        telemetry.addLine("Angulo do robô: " + chassis.imu.getRobotYawPitchRollAngles().getYaw());
        telemetry.addLine("Velocidade do spinner: " + velocity);
        chassis.andar(-gamepad1.left_stick_y, -gamepad1.right_stick_x, -gamepad1.right_stick_x, gamepad1.right_trigger, gamepad1.left_bumper);
        outtake.spin(gamepad1.right_stick_y, velocity);

        telemetry.update();
    }
}
