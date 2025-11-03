package org.firstinspires.ftc.teamcode.Decode.Tsunami;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous
public class TsunamiAutonomous extends LinearOpMode {

    TsunamiChassis drive = new TsunamiChassis();

    @Override
    public void runOpMode() {
        drive.init(hardwareMap);
        
        waitForStart();

        if (opModeIsActive()) {
            drive.goToPosition(100, 200, 90, opModeIsActive());

            drive.goToPosition(200, 200, 0, opModeIsActive());
        }
    }
}
