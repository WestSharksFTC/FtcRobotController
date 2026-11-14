package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.utils;

public class MathUtils {
    public static double ticksToDegrees(double ticks, double ticksPerDegree){
        return ticks / ticksPerDegree;
    }

    public static double degreesToTicks(double degrees, double ticksPerDegree){
        return degrees * ticksPerDegree;
    }

    public static double ticksPerSecondToRPM(double ticksPerSecond, double ticksPerRevolution) {
        return (ticksPerSecond * 60.0) / ticksPerRevolution;
    }

    public static double rpmToTicksPerSecond(double rpm, double ticksPerRevolution) {
        return (rpm * ticksPerRevolution) / 60.0;
    }

    public static double normalizeAngle(double angle) {
        while (angle > 180) {
            angle -= 360;
        }

        while (angle < -180) {
            angle += 360;
        }

        return angle;
    }
}
