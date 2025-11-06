package org.firstinspires.ftc.teamcode.Decode.Fenix;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@Disabled
@TeleOp
public class FenixTeleOp extends OpMode {
    //instanciando as classes
    FenixChassis chassis;
    FenixIntake intake;
    FenixOuttake outtake;

    @Override
    public void init(){
        chassis = new FenixChassis(hardwareMap);
        intake = new FenixIntake(hardwareMap);
        outtake = new FenixOuttake(hardwareMap);
    }

    @Override
    public void loop(){
        //Telemetrias
        telemetry.addData("Hardware: ", "Running");

        //Movimento
        chassis.run(gamepad1.left_stick_y, gamepad1.right_stick_x, gamepad1.left_stick_x, gamepad1.right_trigger);

        //Intake
        intake.runIntake(gamepad1.dpad_right, gamepad1.dpad_down, gamepad1.dpad_left, gamepad1.dpad_up);

        //Outtake
        outtake.runOuttake(gamepad2.dpad_right, gamepad2.dpad_down, gamepad2.dpad_left, gamepad2.dpad_up);

        telemetry.update();
    }
}
