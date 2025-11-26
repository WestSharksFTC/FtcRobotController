package org.firstinspires.ftc.teamcode.Decode.Tsunami;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

@Autonomous
public class AutonomousTeste extends OpMode {

    TsunamiChassis drive = new TsunamiChassis();

    enum State {
        MOVE_FWD,
        TURN_90,
        STRAFE,
        STOP
    }

    State state = State.MOVE_FWD;

    double startX, startY, startHeading;

    @Override
    public void init() {
        drive.init(hardwareMap);
    }

    @Override
    public void start() {
        startX = drive.getX();
        startY = drive.getY();
        startHeading = drive.getHeading();
    }

    @Override
    public void loop() {

        switch(state) {

            case MOVE_FWD: {
                double dist = Math.abs(drive.getX() - startX);

                if (dist < 50) {  // 50cm
                    drive.driveForward(0.3);
                } else {
                    drive.drive(0,0,0);
                    state = State.STRAFE;
                }
                break;
            }

            case TURN_90: {
                double current = drive.getHeading();
                double target = startHeading - 90;

                double error = target - current;

                if (Math.abs(error) > 2) {
                    drive.driveTurn(error * 0.01);
                } else {
                    drive.drive(0,0,0);
                    state = State.STRAFE;
                }
                break;
            }

            case STRAFE: {
                double distX = Math.abs(drive.getY() - startY);

                if (distX < 40) { // 40cm
                    drive.driveStrafe(0.3);
                } else {
                    drive.drive(0,0,0);
                    state = State.STOP;
                }
                break;
            }

            case STOP:
                drive.drive(0,0,0);
                break;
        }

        telemetry.addData("State", state);
        telemetry.addData("X", drive.getX());
        telemetry.addData("Y", drive.getY());
        telemetry.addData("Heading", drive.getHeading());
        telemetry.update();
    }
}
