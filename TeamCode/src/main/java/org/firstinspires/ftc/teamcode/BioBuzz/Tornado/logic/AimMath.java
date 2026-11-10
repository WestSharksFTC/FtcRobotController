package org.firstinspires.ftc.teamcode.BioBuzz.Tornado.logic;

/**
 * Mira por odometria, sem SDK (testavel no computador).
 * Convencao do Pedro: heading em radianos, anti-horario positivo, x/y em polegadas.
 * Resultado em graus no referencial da torreta (anti-horario positivo, 0 = frente do robo).
 * Se a tua torreta gira ao contrario, use TurretConstants.TURRET_SIGN = -1.
 */
public final class AimMath {
    private AimMath() {
    }

    public static double wrapDeg(double a) {
        while (a > 180) a -= 360;
        while (a < -180) a += 360;
        return a;
    }

    /** Posicao X da torreta no campo (o offset gira junto com o robo). */
    public static double turretX(double x, double headingRad, double offX, double offY) {
        return x + offX * Math.cos(headingRad) - offY * Math.sin(headingRad);
    }

    /** Posicao Y da torreta no campo. */
    public static double turretY(double y, double headingRad, double offX, double offY) {
        return y + offX * Math.sin(headingRad) + offY * Math.cos(headingRad);
    }

    /** Angulo (graus) que a torreta deve ter em relacao ao robo para apontar ao gol. */
    public static double targetTurretDeg(double x, double y, double headingRad,
                                         double offX, double offY, double goalX, double goalY) {
        double tx = turretX(x, headingRad, offX, offY);
        double ty = turretY(y, headingRad, offX, offY);
        double bearing = Math.toDegrees(Math.atan2(goalY - ty, goalX - tx));
        return wrapDeg(bearing - Math.toDegrees(headingRad));
    }

    /** Distancia da torreta ate o gol. */
    public static double distance(double x, double y, double headingRad,
                                  double offX, double offY, double goalX, double goalY) {
        return Math.hypot(goalX - turretX(x, headingRad, offX, offY),
                goalY - turretY(y, headingRad, offX, offY));
    }

    /** Alvo em ticks pelo caminho mais curto a partir da posicao atual, travado em +/-maxTicks. */
    public static int targetTicks(double targetDeg, int currentTicks, double ticksPerDeg, int maxTicks) {
        double currentDeg = currentTicks / ticksPerDeg;
        double error = wrapDeg(targetDeg - currentDeg);
        int t = currentTicks + (int) (error * ticksPerDeg);
        return Math.max(-maxTicks, Math.min(maxTicks, t));
    }
}
