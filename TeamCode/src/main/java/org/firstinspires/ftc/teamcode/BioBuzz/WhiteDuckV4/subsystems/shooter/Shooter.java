package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.shooter;

import static org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.shooter.ShooterConstants.*;
import static org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.shooter.ShooterHardware.*;

import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.targeting.TargetingSystem;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.utils.MathUtils;

import com.seattlesolvers.solverslib.command.SubsystemBase;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.seattlesolvers.solverslib.util.TelemetryData;

public class Shooter extends SubsystemBase {
    private final DcMotorEx outtake;
    private final VoltageSensor batteryVoltageSensor;
    private TargetingSystem targetingSystem;
    private ShooterTable shooterTable;

    public enum State{
        AUTO_VELOCITY,
        MANUAL_VELOCITY,
        STOP
    }

    private State state = State.STOP;

    private double targetRPM = 0.0;
    private double lastAppliedKF = Double.NaN;

    public Shooter(HardwareMap hardwareMap) {
        outtake = hardwareMap.get(DcMotorEx.class, SHOOTER_MOTOR);
        batteryVoltageSensor = hardwareMap.voltageSensor.iterator().next();

        outtake.setDirection(DcMotorEx.Direction.FORWARD);
        outtake.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        outtake.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);
    }

    public void setTargetingSystemAndShooterTable(TargetingSystem targetingSystem, ShooterTable shooterTable){
        this.targetingSystem = targetingSystem;
        this.shooterTable = shooterTable;
    }

    public void setState(State state){
        this.state = state;
        update();
    }

    public State getState(){
        return state;
    }

    public void update(){
        switch(state){
            case AUTO_VELOCITY:
                if(targetingSystem != null && shooterTable != null){
                    double distanceIN = targetingSystem.getTargetDistanceIN();
                    this.targetRPM = shooterTable.getShooterVelocity(distanceIN);
                    setVelocityRPM(targetRPM);
                }else{
                    stop();
                }
                break;
            case MANUAL_VELOCITY:
                setVelocityRPM(targetRPM);
                break;
            case STOP:
                stop();
                break;
        }
    }

    // Velocity Control
    public void setManualVelocity(double targetRPM){
        this.targetRPM = targetRPM;
        setState(State.MANUAL_VELOCITY);
    }

    private void setVelocityRPM(double targetRPM) {
        this.targetRPM = targetRPM;
        double targetTicksPerSecond = MathUtils.rpmToTicksPerSecond(targetRPM, MOTOR_TICKS_PER_REV);
        outtake.setVelocity(targetTicksPerSecond);
    }

    private void stop() {
        targetRPM = 0.0;
        outtake.setPower(0.0);
    }

    public boolean isAtTargetVelocity() {
        return targetRPM > 0.0 && Math.abs(getTargetVelocityRPM() - getCurrentVelocityRPM()) <= VELOCITY_TOLERANCE_RPM;
    }

    // State Read
    public double getCurrentVelocityTicksPerSecond(){
        return outtake.getVelocity();
    }

    public double getCurrentVelocityRPM(){
        return MathUtils.ticksPerSecondToRPM(getCurrentVelocityTicksPerSecond(), MOTOR_TICKS_PER_REV);
    }

    public double getTargetVelocityTicksPerSecond(){
        return MathUtils.rpmToTicksPerSecond(targetRPM, MOTOR_TICKS_PER_REV);
    }

    public double getTargetVelocityRPM(){
        return targetRPM;
    }

    public double getBatteryVoltage(){
        double voltage = batteryVoltageSensor.getVoltage();
        return voltage > 0.0 ? voltage : NOMINAL_VOLTAGE;
    }

    // PIDF Compensation
    private double calculateAdjustedKF() {
        return BASE_KF * (NOMINAL_VOLTAGE / getBatteryVoltage());
    }

    private void updateFeedforwardCompensation() {
        double adjustedKF = calculateAdjustedKF();

        if (Double.isNaN(lastAppliedKF) || Math.abs(adjustedKF - lastAppliedKF) > 0.001) {
            outtake.setVelocityPIDFCoefficients(SHOOTER_KP, SHOOTER_KI, SHOOTER_KD, adjustedKF);
            lastAppliedKF = adjustedKF;
        }
    }

    public void addTelemetry(TelemetryData telemetry) {
        telemetry.addData("Current RPM", getCurrentVelocityRPM());
        telemetry.addData("Target RPM", getTargetVelocityRPM());
        telemetry.addData("Velocity Error RPM", getTargetVelocityRPM() - getCurrentVelocityRPM());
        telemetry.addData("Current TPS", getCurrentVelocityTicksPerSecond());
        telemetry.addData("Target TPS", getTargetVelocityTicksPerSecond());
        telemetry.addData("Battery Voltage", getBatteryVoltage());
        telemetry.addData("Applied kF",  lastAppliedKF);
        telemetry.addData("Motor Power", outtake.getPower());
        telemetry.addData("At Target Velocity", isAtTargetVelocity());
    }

    @Override
    public void periodic(){
        updateFeedforwardCompensation();
        update();
    }
}