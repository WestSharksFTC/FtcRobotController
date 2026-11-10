package org.firstinspires.ftc.teamcode.BioBuzz.Tornado.util;

import com.pedropathing.math.Pose;

/** Guarda pose e alianca do autonomo para o TeleOp (variaveis static sobrevivem entre OpModes). */
public final class PosePersistency {
    private static Pose pose = null;
    private static Boolean red = null;

    private PosePersistency() {
    }

    public static void savePose(Pose p) { pose = p; }
    public static boolean hasPose() { return pose != null; }
    public static Pose getPose(Pose fallback) { return pose != null ? pose : fallback; }

    public static void saveAlliance(boolean isRed) { red = isRed; }
    public static boolean isRed(boolean fallback) { return red != null ? red : fallback; }

    public static void clear() {
        pose = null;
        red = null;
    }
}
