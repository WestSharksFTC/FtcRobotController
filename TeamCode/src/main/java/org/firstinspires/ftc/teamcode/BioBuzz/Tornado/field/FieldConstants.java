package org.firstinspires.ftc.teamcode.BioBuzz.Tornado.field;

import com.acmerobotics.dashboard.config.Config;

/** Coordenadas do campo (polegadas, referencial do Pedro). Ajuste ao vivo no Dashboard. */
@Config("TN Field")
public class FieldConstants {
    public static double CENTER_X = 72, CENTER_Y = 72;
    // PLACEHOLDER: meca os gols reais no campo antes de usar a mira por odometria.
    public static double BLUE_GOAL_X = 12, BLUE_GOAL_Y = 132;
    public static double RED_GOAL_X = 132, RED_GOAL_Y = 132;
}
