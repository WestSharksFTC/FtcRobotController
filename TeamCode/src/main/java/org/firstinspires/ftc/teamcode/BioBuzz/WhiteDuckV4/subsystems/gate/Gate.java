package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.gate;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import com.seattlesolvers.solverslib.command.SubsystemBase;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Gate extends SubsystemBase {
    private Servo pollenLimiter;

    public void init(HardwareMap hardwareMap) {
        pollenLimiter = hardwareMap.get(Servo.class, GateHardware.SERVO_GATE);

        pollenLimiter.setDirection(Servo.Direction.FORWARD);

        pollenLimiter.scaleRange(GateConstants.GATE_SCALE_RANGE_MIN, GateConstants.GATE_SCALE_RANGE_MAX);

        closeServo();
    }

    // Servo do Indexer
    public void openServo() {
        pollenLimiter.setPosition(GateConstants.GATE_POSITION_OPEN);
    }

    public void closeServo() {
        pollenLimiter.setPosition(GateConstants.GATE_POSITION_CLOSE);
    }

    public boolean isClosed() {
        return pollenLimiter.getPosition() >= GateConstants.GATE_POSITION_CLOSE - 0.25;
    }

    // Telemetria do Indexer
    public void addTelemetry(Telemetry telemetry) {
        telemetry.addLine("========== GATE ==========");
        telemetry.addData("Gate Closed", isClosed());
        telemetry.addLine();
    }

    public void sendDashboardTelemetry() {
        TelemetryPacket packet = new TelemetryPacket();

        packet.put("Gate/Servo Position", pollenLimiter.getPosition());
        packet.put("Gate/Gate Closed", isClosed() ? 1 : 0);

        FtcDashboard.getInstance().sendTelemetryPacket(packet);
    }


    @Override
    public void periodic(){
        pollenLimiter.scaleRange(GateConstants.GATE_SCALE_RANGE_MIN, GateConstants.GATE_SCALE_RANGE_MAX);
        sendDashboardTelemetry();
    }
}