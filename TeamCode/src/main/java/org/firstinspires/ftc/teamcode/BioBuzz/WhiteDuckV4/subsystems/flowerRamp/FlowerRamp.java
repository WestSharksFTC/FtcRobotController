package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.flowerRamp;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import com.seattlesolvers.solverslib.command.SubsystemBase;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class FlowerRamp extends SubsystemBase{
    private Servo flowerRampRight;
    private Servo flowerRampLeft;

    public void init(HardwareMap hardwareMap) {
        flowerRampRight = hardwareMap.get(Servo.class, FlowerRampHardware.FLOWER_RAMP_SERVO_RIGHT);
        flowerRampLeft = hardwareMap.get(Servo.class, FlowerRampHardware.FLOWER_RAMP_SERVO_LEFT);

        flowerRampRight.setDirection(Servo.Direction.REVERSE);
        flowerRampLeft.setDirection(Servo.Direction.FORWARD);

        flowerRampRight.scaleRange(FlowerRampConstants.RAMP_SCALE_RANGE_MIN, FlowerRampConstants.RAMP_SCALE_RANGE_MAX);
        flowerRampLeft.scaleRange(1 - FlowerRampConstants.RAMP_SCALE_RANGE_MAX, 1 - FlowerRampConstants.RAMP_SCALE_RANGE_MIN);
    }

    // Servo da Rampa
    public void downFlowerRamp() {
        flowerRampRight.setPosition(FlowerRampConstants.RAMP_POSITION_DOWN);
        flowerRampLeft.setPosition(FlowerRampConstants.RAMP_POSITION_DOWN);
    }

    public void upFlowerRamp() {
        flowerRampRight.setPosition(FlowerRampConstants.RAMP_POSITION_UP);
        flowerRampLeft.setPosition(FlowerRampConstants.RAMP_POSITION_UP);
    }

    public double getLeftServoPosition() {
        return flowerRampLeft.getPosition();
    }

    public double getRightServoPosition() {
        return flowerRampRight.getPosition();
    }


    // Telemetria do Intake
    public void getTelemetry(Telemetry telemetry) {
        telemetry.addLine("========== FLOWER RAMP ==========");
        telemetry.addData("Ramp Right Position", "%.2f", getRightServoPosition());
        telemetry.addData("Ramp Left Position", "%.2f", getLeftServoPosition());
        telemetry.addLine();
    }

    public void sendDashboardTelemetry() {
        TelemetryPacket packet = new TelemetryPacket();

        packet.put("Flower Ramp/Right Servo Position", getRightServoPosition());
        packet.put("Flower Ramp/Left Servo Position", getLeftServoPosition());

        FtcDashboard.getInstance().sendTelemetryPacket(packet);
    }


    @Override
    public void periodic(){
        flowerRampRight.scaleRange(FlowerRampConstants.RAMP_SCALE_RANGE_MIN, FlowerRampConstants.RAMP_SCALE_RANGE_MAX);
        flowerRampLeft.scaleRange(1 - FlowerRampConstants.RAMP_SCALE_RANGE_MAX, 1 - FlowerRampConstants.RAMP_SCALE_RANGE_MIN);
        sendDashboardTelemetry();
    }
}