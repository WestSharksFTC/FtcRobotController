package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.shooter;

import com.acmerobotics.dashboard.config.Config;

@Config("WDV4 Shooter")
public class ShooterConstants {
    public static double MOTOR_TICKS_PER_REV = 28.0;
    public static double VELOCITY_TOLERANCE_RPM = 10.0;
    public static double NOMINAL_VOLTAGE = 12.0;
    public static double BASE_KF = 20.75;
    public static double SHOOTER_KP = 250.0;
    public static double SHOOTER_KI = 0.0;
    public static double SHOOTER_KD = 0.0;


    public static final double[] SHOOTER_DISTANCE_IN = {20.0, 100.0};
    public static final double[] SHOOTER_VELOCITY_RPM = {1500.0, 2000.0};
}