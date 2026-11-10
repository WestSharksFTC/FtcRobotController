package org.firstinspires.ftc.teamcode.BioBuzz.Tornado.subsystems.intake;

import com.acmerobotics.dashboard.config.Config;

@Config("TN Intake")
public class IntakeConstants {
    public static String MOTOR = "intake", RAMP_RIGHT = "flowerRampRight", RAMP_LEFT = "flowerRampLeft";
    public static double FEED_POWER = 0.75, REVERSE_POWER = -0.25;
    public static double RAMP_RIGHT_MIN = 0.050, RAMP_RIGHT_MAX = 0.125;
    public static double RAMP_LEFT_MIN = 0.90, RAMP_LEFT_MAX = 0.975;
}
