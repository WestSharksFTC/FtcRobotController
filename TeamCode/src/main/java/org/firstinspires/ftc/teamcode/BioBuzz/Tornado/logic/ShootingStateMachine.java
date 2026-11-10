package org.firstinspires.ftc.teamcode.BioBuzz.Tornado.logic;

/**
 * A maquina de estados do TeleOp (INTAKING -> HOLDING -> SHOOTING_SPINUP -> SHOOTING_FIRE),
 * separada do hardware. Ela so decide o ESTADO e o que cada mecanismo deve fazer nele;
 * quem liga os motores e o OpMode (TornadoTeleOp).
 *
 * Mesmas regras do TheTornado antigo:
 *  - INTAKING: conta pollens; com 'capacity' vai para HOLDING; B vai para SPINUP (B ganha se os dois ocorrerem juntos)
 *  - HOLDING: B vai para SPINUP
 *  - SPINUP: quando o outtake chega na velocidade, vai para FIRE
 *  - FIRE: passados 'fireDurationS' segundos (estritamente maior), zera a contagem e volta a INTAKING
 */
public class ShootingStateMachine {
    public enum State {
        INTAKING, HOLDING, SHOOTING_SPINUP, SHOOTING_FIRE
    }

    public enum MotorMode {
        FEED, REVERSE, STOP
    }

    /** O que cada mecanismo deve fazer num estado. */
    public static final class Outputs {
        public final MotorMode intake;
        public final MotorMode indexer;
        public final boolean gateOpen;
        public final boolean outtakeOn;

        Outputs(MotorMode intake, MotorMode indexer, boolean gateOpen, boolean outtakeOn) {
            this.intake = intake;
            this.indexer = indexer;
            this.gateOpen = gateOpen;
            this.outtakeOn = outtakeOn;
        }
    }

    private static final Outputs INTAKING_OUT = new Outputs(MotorMode.FEED, MotorMode.FEED, false, false);
    private static final Outputs HOLDING_OUT = new Outputs(MotorMode.REVERSE, MotorMode.STOP, false, false);
    private static final Outputs SPINUP_OUT = new Outputs(MotorMode.REVERSE, MotorMode.STOP, false, true);
    private static final Outputs FIRE_OUT = new Outputs(MotorMode.REVERSE, MotorMode.FEED, true, true);

    /** Pode ser atualizado a cada ciclo a partir das constantes (ajuste ao vivo). */
    public int capacity = 4;
    public double fireDurationS = 3.0;

    private State state = State.INTAKING;
    private final PollenCounter counter = new PollenCounter();
    private double fireStartS = 0;

    public State getState() {
        return state;
    }

    public int pollenCount() {
        return counter.count();
    }

    public void reset() {
        state = State.INTAKING;
        counter.reset();
    }

    /**
     * Chame uma vez por ciclo.
     *
     * @param pollenDetected  sensores do indexer veem uma peca agora?
     * @param shootPressed    o operador apertou o botao de tiro neste ciclo?
     * @param outtakeAtSpeed  o outtake esta na velocidade-alvo?
     * @param nowS            relogio em segundos
     */
    public State update(boolean pollenDetected, boolean shootPressed, boolean outtakeAtSpeed, double nowS) {
        switch (state) {
            case INTAKING:
                counter.update(pollenDetected);
                if (counter.count() >= capacity) {
                    state = State.HOLDING;
                }
                if (shootPressed) {
                    state = State.SHOOTING_SPINUP;
                }
                break;

            case HOLDING:
                if (shootPressed) {
                    state = State.SHOOTING_SPINUP;
                }
                break;

            case SHOOTING_SPINUP:
                if (outtakeAtSpeed) {
                    fireStartS = nowS;
                    state = State.SHOOTING_FIRE;
                }
                break;

            case SHOOTING_FIRE:
                if (nowS - fireStartS > fireDurationS) {
                    counter.reset();
                    state = State.INTAKING;
                }
                break;
        }
        return state;
    }

    public Outputs outputs() {
        switch (state) {
            case HOLDING:
                return HOLDING_OUT;
            case SHOOTING_SPINUP:
                return SPINUP_OUT;
            case SHOOTING_FIRE:
                return FIRE_OUT;
            case INTAKING:
            default:
                return INTAKING_OUT;
        }
    }
}
