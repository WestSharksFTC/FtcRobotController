package org.firstinspires.ftc.teamcode.Decode.Tsunami;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

@Autonomous
public class TsunamiAutonomous extends OpMode {

    TsunamiChassis drive = new TsunamiChassis();

    @Override
    public void init() {
        drive.init(hardwareMap);
    }

    @Override
    public void loop() {
        drive.goToPosition(100, 200, 90);
    }
}
