package org.firstinspires.ftc.teamcode.Decode.RI30H;

import static android.os.SystemClock.sleep;

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
        //posição inicial da trava das bolas
        outtake.servoBreak.setPosition(0);
    }

    @Override
    public void loop(){
        //telemetrias
        telemetry.addLine("Angulo do robô: " + chassis.imu.getRobotYawPitchRollAngles().getYaw());
        telemetry.addLine("Velocidade do spinner: " + outtake.motorSpinner.getPower());
        telemetry.addLine("Posição do servo: " + outtake.servoBreak.getPosition());
        //movimento
        chassis.andar(gamepad1.left_stick_y, -gamepad1.right_stick_x, -gamepad1.left_stick_x, gamepad1.right_trigger, gamepad1.left_bumper);
        //spinner
        outtake.spin(gamepad2.left_stick_y * 0.8);
        //trava de bolas
        outtake.servo(gamepad2.dpad_right, gamepad2.dpad_left);
        //autônomo do outtake
        if(gamepad1.dpad_up){
            outtake.spin(-1);
            sleep(5000);
            outtake.servoAuto();
        }
        if(gamepad1.dpad_down){

        }

        telemetry.update();
    }
}