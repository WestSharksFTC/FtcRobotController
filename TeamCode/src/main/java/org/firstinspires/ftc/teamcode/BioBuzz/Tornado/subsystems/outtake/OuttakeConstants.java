package org.firstinspires.ftc.teamcode.BioBuzz.Tornado.subsystems.outtake;

import com.acmerobotics.dashboard.config.Config;

@Config("TN Outtake")
public class OuttakeConstants {
    public static String MOTOR = "outtake";
    public static double NOMINAL_VOLTAGE = 12.0;
    public static double KP = 250.0, KI = 0.0, KD = 0.0, BASE_KF = 20.75;
    public static double TOLERANCE = 10.0;      // ticks/s para considerar "na velocidade"
    // Tabela ty (Limelight) -> velocidade
    public static double MIN_TY = -10.0, MAX_TY = 15.0;
    public static double VEL_AT_MIN_TY = 1000.0, VEL_AT_MAX_TY = 760.0;
}
