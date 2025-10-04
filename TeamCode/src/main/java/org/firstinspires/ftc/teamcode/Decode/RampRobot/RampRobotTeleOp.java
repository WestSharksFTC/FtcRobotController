package org.firstinspires.ftc.teamcode.Decode.RampRobot;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class RampRobotTeleOp extends OpMode {
    //instanciando as classes
    RampRobotChassis chassis;
    RampRobotIntake intake;
    RampRobotOuttake outtake;

    @Override
    public void init(){
        chassis = new RampRobotChassis(hardwareMap);
        intake = new RampRobotIntake(hardwareMap);
        outtake = new RampRobotOuttake(hardwareMap);
        //posição inicial da trava das bolas
        //outtake.servoBreak.setPosition(0);
    }

    @Override
    public void loop(){
        //telemetrias
        telemetry.addData("Hardware: ", "Running");

        //movimento
        chassis.andar(gamepad1.left_stick_y, gamepad1.right_stick_x, gamepad1.left_stick_x, gamepad1.right_trigger);

        //entrada das bolas
        intake.servo(true, false);

        //saida das bolas
        if(gamepad1.dpad_up){
            outtake.spin(1, -1);
        }else if(gamepad1.dpad_down){
            outtake.spin(-1, 1);
        }else{
            outtake.spin(0, 0);
        }

        telemetry.update();
    }
}