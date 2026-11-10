package org.firstinspires.ftc.teamcode.BioBuzz.Tornado.subsystems.turret;

import com.acmerobotics.dashboard.config.Config;

@Config("TN Turret")
public class TurretConstants {
    public static String MOTOR = "turret", LIMELIGHT = "limelight";
    public static double MOTOR_TICKS_PER_REV = 537.7;
    // ATENCAO: o comentario antigo dizia 99/20 (4.95), mas o codigo usava 104/25 (4.16).
    // Confira a reducao REAL na torreta e ajuste aqui. Aqui mantive o que o CODIGO usava.
    public static double GEAR_RATIO = 104.0 / 25.0;
    public static double MAX_DEGREES = 180.0;
    public static double KP = 0.03;
    public static double ESCAPE_POWER = 0.8;
    public static int PIPELINE_RED = 0, PIPELINE_BLUE = 1;

    // --- modo ODOMETRY (valores PLACEHOLDER: medir no robo) ---
    public static double OFFSET_X = 0, OFFSET_Y = 0;   // posicao da torreta em relacao ao centro do robo (pol)
    public static double TURRET_SIGN = 1;              // -1 se a torreta girar para o lado errado
    public static double ODOMETRY_POWER = 1.0;         // potencia maxima do RUN_TO_POSITION
}
