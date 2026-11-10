package org.firstinspires.ftc.teamcode.BioBuzz.Tornado.logic;

/** Contas da torreta sem hardware (testaveis no computador). */
public final class TurretMath {
    private TurretMath() {
    }

    /** Ticks do motor por grau da torreta: ticks por volta do motor x reducao / 360. */
    public static double ticksPerDegree(double motorTicksPerRev, double gearRatio) {
        return motorTicksPerRev * gearRatio / 360.0;
    }

    /** Limite em ticks (truncado, como o (int) do WDTurret antigo). */
    public static int maxTicks(double maxDegrees, double ticksPerDegree) {
        return (int) (maxDegrees * ticksPerDegree);
    }

    /**
     * Controle manual (joystick) com trava rigida nos limites:
     * no limite so deixa mover para o lado de dentro.
     */
    public static double manualPower(double power, int ticks, int maxTicks) {
        if (ticks >= maxTicks && power > 0) return 0.0;
        if (ticks <= -maxTicks && power < 0) return 0.0;
        return power;
    }
}
