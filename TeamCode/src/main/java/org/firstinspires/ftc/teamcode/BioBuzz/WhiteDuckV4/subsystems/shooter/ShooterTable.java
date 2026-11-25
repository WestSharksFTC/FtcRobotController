package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.shooter;

import com.qualcomm.robotcore.util.Range;

public class ShooterTable {
    public static final double[] SHOOTER_DISTANCE_IN = {45.0, 50.0, 55.0, 90.0};
    public static final double[] SHOOTER_VELOCITY_RPM = {1580.0, 1600.0, 1620.0, 1800.0};

    public double getShooterVelocity(double currentDistanceIN) {
        double[] distances = SHOOTER_DISTANCE_IN;
        double[] velocities = SHOOTER_VELOCITY_RPM;

        if (distances.length != velocities.length || distances.length == 0) {
            return 0;
        }

        if (currentDistanceIN <= distances[0]) {
            return velocities[0];
        }

        int lastIndex = distances.length - 1;

        if (currentDistanceIN >= distances[lastIndex]) {
            return velocities[lastIndex];
        }

        for (int i = 0; i < distances.length - 1; i++) {
            double d1 = distances[i];
            double d2 = distances[i + 1];

            if (currentDistanceIN >= d1 && currentDistanceIN <= d2) {
                double v1 = velocities[i];
                double v2 = velocities[i + 1];

                return Range.scale(currentDistanceIN, d1, d2, v1, v2);
            }
        }

        return 0;
    }
}
