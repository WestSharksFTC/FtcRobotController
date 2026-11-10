package org.firstinspires.ftc.teamcode.BioBuzz.Tornado.subsystems.chassis;

import com.acmerobotics.dashboard.config.Config;

@Config("TN Chassis")
public class ChassisConstants {
    public static String LF = "lf", LB = "lb", RF = "rf", RB = "rb", IMU = "imu";
    public static double MAX_SPEED = 0.8;
    // Posicao de park (polegadas / graus) usada pelo botao BACK
    public static double PARK_X = 10, PARK_Y = 95, PARK_HEADING_DEG = 0;
}
