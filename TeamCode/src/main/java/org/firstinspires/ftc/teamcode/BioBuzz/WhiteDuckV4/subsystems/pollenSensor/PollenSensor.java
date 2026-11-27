package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.pollenSensor;

import static org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.pollenSensor.PollenSensorHardware.*;
import static org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.pollenSensor.PollenSensorConstants.*;

import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.seattlesolvers.solverslib.util.TelemetryData;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class PollenSensor extends SubsystemBase {
    private final RevColorSensorV3 distanceSensorTop;
    private final RevColorSensorV3 distanceSensorBottom;
    private final ElapsedTime fullConfirmTimer = new ElapsedTime();
    private boolean wasBothDetected = false;
    private boolean isFull = false;


    public PollenSensor(HardwareMap hardwareMap){
        distanceSensorTop = hardwareMap.get(RevColorSensorV3.class, DISTANCE_SENSOR_TOP);
        distanceSensorBottom = hardwareMap.get(RevColorSensorV3.class, DISTANCE_SENSOR_BOTTOM);
    }

    private double getDistanceTopCM() {
        return distanceSensorTop.getDistance(DistanceUnit.CM);
    }

    private double getDistanceBottomCM() {
        return distanceSensorBottom.getDistance(DistanceUnit.CM);
    }

    public boolean hasPollenOnTopSensor(){
        return getDistanceTopCM() <= DETECTION_THRESHOLD_CM;
    }

    public boolean hasPollenOnBottomSensor(){
        return getDistanceBottomCM() <= DETECTION_THRESHOLD_CM;
    }

    private void updateFullState(){
        boolean bothDetected = hasPollenOnTopSensor() && hasPollenOnBottomSensor();

        if(bothDetected){
            if(!wasBothDetected){
                fullConfirmTimer.reset();
            }
            if(fullConfirmTimer.milliseconds() >= FULL_CONFIRM_TIME_MS){
                isFull = true;
            }
        }else{
            fullConfirmTimer.reset();
            isFull = false;
        }

        wasBothDetected = bothDetected;
    }

    public boolean isIndexerFull(){
        return isFull;
    }

    @Override
    public void periodic(){
        updateFullState();
    }

    public void addTelemetry(TelemetryData telemetry) {
        telemetry.addData("Indexer Full", isIndexerFull());
        telemetry.addData("Top Distance (cm)", getDistanceTopCM());
        telemetry.addData("Bottom Distance (cm)", getDistanceBottomCM());
        telemetry.addData("Pollen Sensor Top / Bottom", hasPollenOnTopSensor() + " / " + hasPollenOnBottomSensor());
    }
}
