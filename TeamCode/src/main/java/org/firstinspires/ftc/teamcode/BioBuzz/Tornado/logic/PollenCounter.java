package org.firstinspires.ftc.teamcode.BioBuzz.Tornado.logic;

/** Conta pollens pela borda de subida do sensor (false -> true). */
public class PollenCounter {
    private int count = 0;
    private boolean previous = false;

    /** Chame uma vez por ciclo com a leitura atual. Devolve a contagem. */
    public int update(boolean detected) {
        if (detected && !previous) {
            count++;
        }
        previous = detected;
        return count;
    }

    public int count() {
        return count;
    }

    /** Zera a contagem. Mantem a ultima leitura (igual ao codigo antigo). */
    public void reset() {
        count = 0;
    }
}
