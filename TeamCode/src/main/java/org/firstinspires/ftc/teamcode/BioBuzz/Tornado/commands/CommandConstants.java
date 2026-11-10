package org.firstinspires.ftc.teamcode.BioBuzz.Tornado.commands;

import com.acmerobotics.dashboard.config.Config;

@Config("TN Commands")
public class CommandConstants {
    public static long SPINUP_TIMEOUT_MS = 3000;   // espera maxima pelo outtake chegar na velocidade
    public static long FIRE_MS = 1000;             // tempo com o portao aberto jogando pollens
    public static long COLLECT_WAIT_MS = 2000;     // tempo coletando depois de chegar no ponto
}
