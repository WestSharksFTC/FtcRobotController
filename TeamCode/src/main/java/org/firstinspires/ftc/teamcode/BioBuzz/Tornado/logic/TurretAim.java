package org.firstinspires.ftc.teamcode.BioBuzz.Tornado.logic;

/**
 * Mira automatica da torreta com "fuga" ao bater no limite.
 * E a mesma logica do WDTurret.autoAim(), mas sem Limelight nem motor:
 * recebe os numeros e devolve a potencia. Assim da para testar no computador.
 */
public class TurretAim {
    private boolean escaping = false;
    private int escapeDirection = 0; // -1 esquerda, +1 direita

    public boolean isEscaping() {
        return escaping;
    }

    public void reset() {
        escaping = false;
        escapeDirection = 0;
    }

    /**
     * @param valid       a Limelight tem um alvo valido?
     * @param tx          angulo horizontal do alvo (graus)
     * @param ticks       posicao atual da torreta (ticks)
     * @param maxTicks    limite (+/-) em ticks
     * @param kP          ganho proporcional do tracking
     * @param escapePower potencia usada na fuga (positivo)
     */
    public double update(boolean valid, double tx, int ticks, int maxTicks, double kP, double escapePower) {
        if (escaping) {
            double power = escapeDirection * escapePower;
            boolean safeFromRightLimit = escapeDirection < 0 && ticks < maxTicks;
            boolean safeFromLeftLimit = escapeDirection > 0 && ticks > -maxTicks;
            if (valid && (safeFromRightLimit || safeFromLeftLimit)) {
                escaping = false;
            }
            return power;
        }

        if (!valid) {
            return 0.0;
        }

        if (ticks >= maxTicks) {
            escaping = true;
            escapeDirection = -1;
            return -escapePower;
        }
        if (ticks <= -maxTicks) {
            escaping = true;
            escapeDirection = 1;
            return escapePower;
        }

        return OuttakeMath.clip(tx * kP, -1.0, 1.0);
    }
}
