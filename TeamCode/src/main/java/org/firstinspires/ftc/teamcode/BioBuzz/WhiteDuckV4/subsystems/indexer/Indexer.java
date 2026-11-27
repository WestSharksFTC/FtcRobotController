package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.indexer;

import static org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.indexer.IndexerConstants.*;

import com.seattlesolvers.solverslib.command.SubsystemBase;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.pollenSensor.PollenSensor;

public class Indexer extends SubsystemBase {
    private final DcMotor indexer;
    private PollenSensor pollenSensor;

    public enum State{
        TRANSFER,
        FEED,
        REVERSE,
        STOP
    }

    private State state = State.STOP;

    public Indexer(HardwareMap hardwareMap) {
        indexer = hardwareMap.get(DcMotor.class, IndexerHardware.INDEXER_MOTOR);

        indexer.setDirection(DcMotor.Direction.FORWARD);

        indexer.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        indexer.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
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
                indexer.setPower(TRANSFER_POWER);
                break;
            case FEED:
                if (pollenSensor.hasPollenOnTopSensor()) {
                    indexer.setPower(STOP_POWER);
                } else {
                    indexer.setPower(FEED_POWER);
                }
                break;
            case REVERSE:
                indexer.setPower(REVERSE_POWER);
                break;
            case STOP:
                indexer.setPower(STOP_POWER);
                break;
        }
    }

    @Override
    public void periodic(){
        update();
    }
}