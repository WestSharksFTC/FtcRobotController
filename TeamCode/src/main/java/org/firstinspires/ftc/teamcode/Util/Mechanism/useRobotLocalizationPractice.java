package org.firstinspires.ftc.teamcode.Util.Mechanism;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class useRobotLocalizationPractice extends OpMode {

    robotLocalizationPractice robotLocalizationPractice = new robotLocalizationPractice(0);

    @Override
    public void init() {
        robotLocalizationPractice.setAngle(0);
        robotLocalizationPractice.setX(0);
        robotLocalizationPractice.setY(0);
    }

    @Override
    public void loop() {
        if(gamepad1.a){
            robotLocalizationPractice.turnRobot(0.1);
        }else if(gamepad1.b){
            robotLocalizationPractice.turnRobot(-0.1);
        }

        if(gamepad1.dpad_left){
            robotLocalizationPractice.changeX(0.1);
        }else if(gamepad1.dpad_right){
            robotLocalizationPractice.changeX(-0.1);
        }

        if(gamepad1.dpad_down){
            robotLocalizationPractice.changeY(0.1);
        }else if(gamepad1.dpad_up){
            robotLocalizationPractice.changeY(-0.1);
        }

        telemetry.addData("Heading", robotLocalizationPractice.getHeading());
        telemetry.addData("Angle", robotLocalizationPractice.getAngle());
        telemetry.addData("X value", robotLocalizationPractice.getX());
        telemetry.addData("Y value", robotLocalizationPractice.getY());
    }
}
