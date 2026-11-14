package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.indexer;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import com.seattlesolvers.solverslib.command.SubsystemBase;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Indexer extends SubsystemBase {
    private DcMotor indexer;
    private DistanceSensor distanceSensorTop;
    private DistanceSensor distanceSensorBottom;

    public void init(HardwareMap hardwareMap) {
        indexer = hardwareMap.get(DcMotor.class, IndexerHardware.INDEXER_MOTOR);
        distanceSensorTop = hardwareMap.get(DistanceSensor.class, IndexerHardware.DISTANCE_SENSOR_TOP);
        distanceSensorBottom = hardwareMap.get(DistanceSensor.class, IndexerHardware.DISTANCE_SENSOR_BOTTOM);

        indexer.setDirection(DcMotor.Direction.REVERSE);

        indexer.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        indexer.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }


    // Motor do Indexer
    public void transfer(){
        indexer.setPower(IndexerConstants.TRANSFER_POWER);
    }

    public void feed() {
        indexer.setPower(IndexerConstants.FEED_POWER);
    }

    public void reverse() {
        indexer.setPower(IndexerConstants.REVERSE_POWER);
    }

    public void stop() {
        indexer.setPower(IndexerConstants.STOP_POWER);
    }

    public double getPower(){
        return indexer.getPower();
    }

    // Sensores de Distância
    public double getDistanceTopCM() {
        return distanceSensorTop.getDistance(DistanceUnit.CM);
    }

    public double getDistanceBottomCM() {
        return distanceSensorBottom.getDistance(DistanceUnit.CM);
    }

    public boolean isIndexerFull() {
        boolean hasPollenOnTop = getDistanceTopCM() <= IndexerConstants.DETECTION_THRESHOLD_CM;
        boolean hasPollenOnBottom = getDistanceBottomCM() <= IndexerConstants.DETECTION_THRESHOLD_CM;

        return hasPollenOnTop && hasPollenOnBottom;
    }


    // Telemetria do Indexer
    public void addTelemetry(Telemetry telemetry) {
        telemetry.addLine("========== INDEXER ==========");
        telemetry.addData("Current Power", "%.2f", getPower());
        telemetry.addData("Indexer Full", isIndexerFull());
        telemetry.addData("Top Distance", "%.2f", getDistanceTopCM());
        telemetry.addData("Bottom Distance", "%.2f", getDistanceBottomCM());
        telemetry.addLine();
    }

    public void sendDashboardTelemetry() {
        TelemetryPacket packet = new TelemetryPacket();

        packet.put("Indexer/Current Power", getPower());

        packet.put("Indexer/Top Distance (cm)", getDistanceTopCM());
        packet.put("Indexer/Bottom Distance (cm)", getDistanceBottomCM());
        packet.put("Indexer/Full", isIndexerFull() ? 1 : 0);

        FtcDashboard.getInstance().sendTelemetryPacket(packet);
    }


    @Override
    public void periodic(){
        sendDashboardTelemetry();
    }
}