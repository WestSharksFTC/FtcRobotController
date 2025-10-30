package org.firstinspires.ftc.teamcode.Util.Mechanism;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class useRobotColorSensorPractice extends OpMode {

    robotColorSensorPractice colorSensor = new robotColorSensorPractice();

    @Override
    public void init() {
        colorSensor.init(hardwareMap);
    }

    @Override
    public void loop() {
        colorSensor.getDetectedColor(telemetry);
    }
}
