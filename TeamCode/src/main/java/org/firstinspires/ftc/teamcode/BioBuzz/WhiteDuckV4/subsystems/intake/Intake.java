package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.intake;

import static org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.intake.IntakeConstants.*;
import static org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.intake.IntakeHardware.*;

import com.seattlesolvers.solverslib.command.SubsystemBase;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.pollenSensor.PollenSensor;

public class Intake extends SubsystemBase{
    private final DcMotor intake;
    private PollenSensor pollenSensor;

    public enum State{
        TRANSFER,
        FEED,
        REVERSE,
        STOP
    }

    private State state = State.STOP;

    public Intake(HardwareMap hardwareMap) {
        intake = hardwareMap.get(DcMotor.class, INTAKE_MOTOR);

        intake.setDirection(DcMotor.Direction.FORWARD);

        intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void setPollenSensor(PollenSensor pollenSensor){
        this.pollenSensor = pollenSensor;
    }

    public void setState(State state){
        this.state = state;
        update();
    }

    public State getState(){
        return state;
    }

    private void update(){
        switch(state){
            case TRANSFER:
                intake.setPower(TRANSFER_POWER);
                break;
            case FEED:
                if (pollenSensor.isIndexerFull()) {
                    intake.setPower(STOP_POWER);
                } else {
                    intake.setPower(FEED_POWER);
                }
                break;
            case REVERSE:
                intake.setPower(REVERSE_POWER);
                break;
            case STOP:
                intake.setPower(STOP_POWER);
                break;
        }
    }

    @Override
    public void periodic(){
        update();
    }
}