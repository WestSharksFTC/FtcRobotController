package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.indexer;

import com.seattlesolvers.solverslib.command.SubsystemBase;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Indexer extends SubsystemBase {
    private final DcMotor indexer;

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
                indexer.setPower(IndexerConstants.TRANSFER_POWER);
                break;
            case FEED:
                indexer.setPower(IndexerConstants.FEED_POWER);
                break;
            case REVERSE:
                indexer.setPower(IndexerConstants.REVERSE_POWER);
                break;
            case STOP:
                indexer.setPower(IndexerConstants.STOP_POWER);
                break;
        }
    }

    @Override
    public void periodic(){
        update();
    }
}