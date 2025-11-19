package org.firstinspires.ftc.teamcode.Util.Mechanism;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@Disabled
@TeleOp
public class useRobotColorSensorPractice extends OpMode {

    robotColorSensorPractice colorSensor = new robotColorSensorPractice();
    robotColorSensorPractice.DetectedColor detectedColor;

    @Override
    public void init() {
        colorSensor.init(hardwareMap);
    }

    @Override
    public void loop() {
        detectedColor = colorSensor.getDetectedColor(telemetry);
        telemetry.addData("Color Detected", detectedColor);
    }
}
