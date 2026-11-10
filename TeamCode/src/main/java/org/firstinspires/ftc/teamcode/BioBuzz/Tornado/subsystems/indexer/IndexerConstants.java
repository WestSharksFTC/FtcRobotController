package org.firstinspires.ftc.teamcode.BioBuzz.Tornado.subsystems.indexer;

import com.acmerobotics.dashboard.config.Config;

@Config("TN Indexer")
public class IndexerConstants {
    public static String MOTOR = "indexer", GATE = "pollenLimiter";
    public static String SENSOR_LEFT = "pollenSensorLeft", SENSOR_RIGHT = "pollenSensorRight";
    public static double FEED_POWER = 0.75, REVERSE_POWER = -0.25;
    public static double GATE_MIN = 0.05, GATE_MAX = 0.3;
    public static double DETECTION_THRESHOLD_CM = 3.0;
    public static int CAPACITY = 4;
}
