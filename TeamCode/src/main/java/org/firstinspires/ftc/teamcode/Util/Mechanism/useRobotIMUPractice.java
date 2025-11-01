package org.firstinspires.ftc.teamcode.Util.Mechanism;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp
public class useRobotIMUPractice extends OpMode {
    robotIMUPractice test = new robotIMUPractice();

    double heading;

    @Override
    public void init() {
        test.init(hardwareMap);
    }

    @Override
    public void loop() {
        heading = test.getHeading(AngleUnit.DEGREES);

        if(heading < 0.5 && heading > -0.5){
            test.setMotor(0.0);
        }else if(heading > 0.5){
            test.setMotor(0.5);
        }else{
            test.setMotor(-0.5);
        }

        telemetry.addData("Heading", test.getHeading(AngleUnit.DEGREES));
        telemetry.addData("Heading", test.getHeading(AngleUnit.RADIANS));
    }
}
