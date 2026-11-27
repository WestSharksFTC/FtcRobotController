package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.turret;

import static org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.turret.TurretConstants.*;
import static org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.turret.TurretHardware.*;

import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.targeting.TargetingSystem;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.utils.MathUtils;

import com.seattlesolvers.solverslib.command.SubsystemBase;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;
import com.seattlesolvers.solverslib.util.TelemetryData;

public class Turret extends SubsystemBase {
    private final DcMotorEx turretMotor;
    private TargetingSystem targetingSystem;

    public enum State{
        AUTO_AIM,
        MANUAL_ANGLE,
        STOP
    }

    private State state = State.STOP;

    private double targetAngleDegrees = 0.0;

    public Turret(HardwareMap hardwareMap) {
        turretMotor = hardwareMap.get(DcMotorEx.class, TURRET_MOTOR);

        turretMotor.setDirection(DcMotorEx.Direction.REVERSE);

        turretMotor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);

        turretMotor.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        turretMotor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
    }

    public void setTargetingSystem(TargetingSystem targetingSystem){
        this.targetingSystem = targetingSystem;
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
            case AUTO_AIM:
                if(targetingSystem != null){
                    applyTargetAngle(targetingSystem.getTurretAngle());
                }else{
                    applyStop();
                }
                break;
            case MANUAL_ANGLE:
                applyTargetAngle(targetAngleDegrees);
                break;
            case STOP:
                applyStop();
                break;
        }
    }

    public void setManualAngle(double degrees){
        this.targetAngleDegrees = Range.clip(degrees, -MAX_ANGLE_DEGREES, MAX_ANGLE_DEGREES);
        this.state = State.MANUAL_ANGLE;
        update();
    }

    private void applyTargetAngle(double degrees) {
        targetAngleDegrees = Range.clip(degrees, -MAX_ANGLE_DEGREES, MAX_ANGLE_DEGREES);

        int targetTicks = (int) Math.round(MathUtils.degreesToTicks(targetAngleDegrees, getTicksPerDegree()));
        turretMotor.setTargetPosition(targetTicks);

        if (turretMotor.getMode() != DcMotorEx.RunMode.RUN_TO_POSITION) {
            turretMotor.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
        }

        turretMotor.setPower(POSITION_POWER);
    }

    private void applyStop(){
        turretMotor.setPower(0.0);
    }

    public boolean isAtTarget() {
        return Math.abs(targetAngleDegrees - getCurrentPositionDegrees()) <= POSITION_TOLERANCE_DEGREES;
    }

    public void stop() {
        state = State.STOP;
        applyStop();
    }

    public int getCurrentPositionTicks(){
        return turretMotor.getCurrentPosition();
    }

    public double getCurrentPositionDegrees(){
        return MathUtils.ticksToDegrees(getCurrentPositionTicks(), getTicksPerDegree());
    }

    public double getTargetAngleDegrees() {
        return targetAngleDegrees;
    }

    public double getPower(){
        return turretMotor.getPower();
    }


    public void addTelemetry(TelemetryData telemetry) {
        telemetry.addData("Turret State", getState());
        telemetry.addData("Current Position", getCurrentPositionDegrees());
        telemetry.addData("Current Position (Ticks)", getCurrentPositionTicks());
        telemetry.addData("Target Angle", getTargetAngleDegrees());
        telemetry.addData("Error Position", targetAngleDegrees - getCurrentPositionDegrees());
        telemetry.addData("Current Power", getPower());
        telemetry.addData("Current Motor Mode", turretMotor.getMode());
        telemetry.addData("At Target", isAtTarget());
    }

    @Override
    public void periodic(){
        update();
    }
}
