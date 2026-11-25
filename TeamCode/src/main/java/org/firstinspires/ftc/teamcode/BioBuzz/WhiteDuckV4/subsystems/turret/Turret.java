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
    private DcMotorEx turretMotor;
    private TargetingSystem targetingSystem;

    public enum State{
        AUTO_AIM,
        MANUAL_ANGLE,
        MANUAL_POWER,
        STOP
    }

    private State state = State.STOP;

    private double targetAngleDegrees = 0.0;
    private double manualPower = 0.0;

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
            case MANUAL_POWER:
                applyManualPower(manualPower);
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

    public void setManualPower(double power){
        this.manualPower = Range.clip(power, -1.0, 1.0);
        this.state = State.MANUAL_POWER;
        update();
    }

    private void applyTargetAngle(double degrees) {
        targetAngleDegrees = Range.clip(degrees, -MAX_ANGLE_DEGREES, MAX_ANGLE_DEGREES);
        double currentPosition = getCurrentPositionDegrees();

        int targetTicks = (int) Math.round(MathUtils.degreesToTicks(targetAngleDegrees, getTicksPerDegree()));
        turretMotor.setTargetPosition(targetTicks);

        if (turretMotor.getMode() != DcMotorEx.RunMode.RUN_TO_POSITION) {
            turretMotor.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
        }

        turretMotor.setPower(POSITION_POWER);
    }

    private void applyManualPower(double power) {
        if (turretMotor.getMode() != DcMotorEx.RunMode.RUN_USING_ENCODER) {
            turretMotor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        }

        double currentPosition = getCurrentPositionDegrees();
        double clippedPower = Range.clip(power, -1.0, 1.0);

        boolean leftLimitReached = currentPosition <= -MAX_ANGLE_DEGREES && clippedPower < 0;
        boolean rightLimitReached = currentPosition >= MAX_ANGLE_DEGREES && clippedPower > 0;

        if (leftLimitReached || rightLimitReached) {
            turretMotor.setPower(0.0);
        } else {
            turretMotor.setPower(clippedPower);
        }
    }

    private void applyStop(){
        manualPower = 0.0;
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


    // Telemetria da Turret
    public void addTelemetry(TelemetryData telemetry) {
        telemetry.addData("Turret State", getState());
        telemetry.addData("Current Position", getCurrentPositionDegrees());
        telemetry.addData("Current Position (Ticks)", getCurrentPositionTicks());
        telemetry.addData("Target Position", targetAngleDegrees);
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
