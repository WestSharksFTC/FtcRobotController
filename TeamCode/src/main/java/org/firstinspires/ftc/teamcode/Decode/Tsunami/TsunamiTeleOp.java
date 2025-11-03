package org.firstinspires.ftc.teamcode.Decode.Tsunami;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class TsunamiTeleOp extends OpMode {
    TsunamiChassis drive = new TsunamiChassis();
    double forward, strafe, turn;

    @Override
    public void init() {
        drive.init(hardwareMap);
    }

    @Override
    public void loop() {
        forward = gamepad1.left_stick_y;
        strafe = gamepad1.right_stick_x;
        turn = gamepad1.right_stick_x;

        drive.driveFieldRelative(forward, strafe, turn);
    }
}
