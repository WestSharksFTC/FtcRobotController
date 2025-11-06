package org.firstinspires.ftc.teamcode.Util.Mechanism;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class robotColorSensorPractice {

    NormalizedColorSensor colorSensor;

    public enum DetectedColor{
        PURPLE,
        GREEN,
        UNKNOWN
    }

    public void init(HardwareMap hardwareMap){
        colorSensor = hardwareMap.get(NormalizedColorSensor.class, "sensor");
        colorSensor.setGain(15);
    }

    public DetectedColor getDetectedColor(Telemetry telemetry){
        NormalizedRGBA colors = colorSensor.getNormalizedColors(); // return 4 values

        float normRed, normGreen, normBlue;
        normRed = colors.red / colors.alpha;
        normGreen = colors.green / colors.alpha;
        normBlue = colors.blue / colors.alpha;

        telemetry.addData("red", normRed);
        telemetry.addData("green", normGreen);
        telemetry.addData("blue", normBlue);
        telemetry.addData("alpha", colors.alpha);

        /*

        PURPLE = R= <.4, G= <.45, B= >.85

        GREEN = R= <.2 ,G= >.2 ,B= <.85

         */

        if(normRed > 0.3 && normGreen < 0.65 && normBlue > 0.85){
            return DetectedColor.PURPLE;
        }else if(normRed < 0.3 && normGreen > 0.65 && normBlue < 0.85){
            return DetectedColor.GREEN;
        }else{
            return DetectedColor.UNKNOWN;
        }
    }
}
