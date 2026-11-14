package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.turret;

import com.acmerobotics.dashboard.config.Config;

@Config("WDV4 Turret")
public class TurretConstants {
    public static double MOTOR_TICKS_PER_REV = 537.7;
    public static double GEAR_RATIO = 112.0 / 32.0;
    public static double MAX_ANGLE_DEGREES = 175.0;
    public static double POSITION_POWER = 0.5;
    public static double POSITION_TOLERANCE_DEGREES = 2.0;

    public static double getTicksPerDegree() {
        return (MOTOR_TICKS_PER_REV * GEAR_RATIO) / 360.0;
    }
}