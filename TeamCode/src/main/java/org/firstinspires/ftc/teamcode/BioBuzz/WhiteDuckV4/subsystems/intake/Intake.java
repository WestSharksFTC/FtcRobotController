package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.intake;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import com.seattlesolvers.solverslib.command.SubsystemBase;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Intake extends SubsystemBase{
    private DcMotor intake;

    public void init(HardwareMap hardwareMap) {
        intake = hardwareMap.get(DcMotor.class, IntakeHardware.INTAKE_MOTOR);

        intake.setDirection(DcMotor.Direction.FORWARD);

        intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }


    // Motor do Intake
    public void transfer(){
        intake.setPower(IntakeConstants.TRANSFER_POWER);
    }

    public void feed() {
        intake.setPower(IntakeConstants.FEED_POWER);
    }

    public void reverse() {
        intake.setPower(IntakeConstants.REVERSE_POWER);
    }

    public void stop() {
        intake.setPower(IntakeConstants.STOP_POWER);
    }

    public double getPower(){
        return intake.getPower();
    }

    // Telemetria do Intake
    public void addTelemetry(Telemetry telemetry) {
        telemetry.addLine("========== INTAKE ==========");
        telemetry.addData("Current Power", "%.2f", getPower());
        telemetry.addLine();
    }

    public void sendDashboardTelemetry() {
        TelemetryPacket packet = new TelemetryPacket();

        packet.put("Intake/Current Power", getPower());

        FtcDashboard.getInstance().sendTelemetryPacket(packet);
    }

    @Override
    public void periodic(){
        sendDashboardTelemetry();
    }
}