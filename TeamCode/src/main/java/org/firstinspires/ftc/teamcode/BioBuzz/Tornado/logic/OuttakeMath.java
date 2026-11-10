package org.firstinspires.ftc.teamcode.BioBuzz.Tornado.logic;

/**
 * Contas do outtake, separadas do hardware para poderem ser testadas no computador.
 * Nenhum import do SDK aqui de proposito.
 */
public final class OuttakeMath {
    private OuttakeMath() {
    }

    public static double clip(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Converte o ty da Limelight (graus) na velocidade-alvo do outtake (ticks/s).
     * ty = minTy da velAtMinTy, ty = maxTy da velAtMaxTy, linear entre os dois.
     * Fora do intervalo o ty e travado nas pontas (mesmo comportamento do WDOuttake antigo).
     */
    public static double velocityFromTy(double ty, double minTy, double maxTy,
                                        double velAtMinTy, double velAtMaxTy) {
        if (maxTy == minTy) return velAtMinTy;
        double clamped = clip(ty, minTy, maxTy);
        return velAtMinTy + (clamped - minTy) * (velAtMaxTy - velAtMinTy) / (maxTy - minTy);
    }

    /**
     * kF compensado pela bateria: bateria fraca pede kF maior.
     * Voltagem invalida (<= 0) cai para a nominal, como no WDOuttake antigo.
     */
    public static double compensatedKf(double baseKf, double nominalVoltage, double voltage) {
        double v = voltage > 0 ? voltage : nominalVoltage;
        return baseKf * (nominalVoltage / v);
    }

    /** Verdadeiro quando a velocidade atual esta a menos de "tolerance" do alvo (estrito). */
    public static boolean atSpeed(double current, double target, double tolerance) {
        return Math.abs(current - target) < tolerance;
    }
}
