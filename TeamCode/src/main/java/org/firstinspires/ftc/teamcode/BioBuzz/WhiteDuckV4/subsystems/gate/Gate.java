package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.gate;

import static org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.gate.GateHardware.*;
import static org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.gate.GateConstants.*;

import com.seattlesolvers.solverslib.command.SubsystemBase;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.seattlesolvers.solverslib.util.TelemetryData;

public class Gate extends SubsystemBase {
    private final Servo pollenLimiter;

    public enum State{
        OPEN,
        CLOSE
    }

    private State state = State.CLOSE;

    public Gate(HardwareMap hardwareMap) {
        pollenLimiter = hardwareMap.get(Servo.class, SERVO_GATE);

        pollenLimiter.setDirection(Servo.Direction.FORWARD);

        pollenLimiter.scaleRange(GATE_SCALE_RANGE_MIN, GATE_SCALE_RANGE_MAX);
    }

    public void setState(State state){
        this.state = state;
        update();
    }

    public void toggle(){
        setState(getState() == State.OPEN ? State.CLOSE : State.OPEN);
    }

    public State getState(){
        return state;
    }

    private void update(){
        switch(state){
            case OPEN:
                pollenLimiter.setPosition(GATE_POSITION_OPEN);
                break;
            case CLOSE:
                pollenLimiter.setPosition(GATE_POSITION_CLOSE);
                break;
        }
    }

    public void addTelemetry(TelemetryData telemetry) {
        telemetry.addData("Gate State", state);
        telemetry.addData("Gate Position", pollenLimiter.getPosition());
    }

    @Override
    public void periodic(){
        update();
    }
}