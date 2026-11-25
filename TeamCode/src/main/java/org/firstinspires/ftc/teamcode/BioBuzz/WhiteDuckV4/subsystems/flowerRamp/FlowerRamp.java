package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.flowerRamp;

import static org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.flowerRamp.FlowerRampHardware.*;
import static org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.flowerRamp.FlowerRampConstants.*;

import com.seattlesolvers.solverslib.command.SubsystemBase;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class FlowerRamp extends SubsystemBase{
    private final Servo flowerRampRight;
    private final Servo flowerRampLeft;

    public enum State{
        UP,
        DOWN
    }

    private State state = State.UP;

    public FlowerRamp(HardwareMap hardwareMap) {
        flowerRampRight = hardwareMap.get(Servo.class, FLOWER_RAMP_SERVO_RIGHT);
        flowerRampLeft = hardwareMap.get(Servo.class, FLOWER_RAMP_SERVO_LEFT);

        flowerRampRight.setDirection(Servo.Direction.REVERSE);
        flowerRampLeft.setDirection(Servo.Direction.FORWARD);

        flowerRampRight.scaleRange(1 - RAMP_SCALE_RANGE_MAX, 1 - RAMP_SCALE_RANGE_MIN);
        flowerRampLeft.scaleRange(RAMP_SCALE_RANGE_MIN, RAMP_SCALE_RANGE_MAX);
    }

    public void setState(State state){
        this.state = state;
        update();
    }

    public void toggle(){
        setState(getState() == State.UP ? State.DOWN : State.UP);
    }

    public State getState(){
        return state;
    }

    private void update(){
        switch(state){
            case UP:
                flowerRampRight.setPosition(RAMP_POSITION_UP);
                flowerRampLeft.setPosition(RAMP_POSITION_UP);
                break;
            case DOWN:
                flowerRampRight.setPosition(RAMP_POSITION_DOWN);
                flowerRampLeft.setPosition(RAMP_POSITION_DOWN);
                break;
        }
    }

    @Override
    public void periodic(){
        update();
    }
}