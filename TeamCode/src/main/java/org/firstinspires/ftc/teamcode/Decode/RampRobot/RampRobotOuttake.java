package org.firstinspires.ftc.teamcode.Decode.RampRobot;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class RampRobotOuttake {
    DcMotor outtakeL;
    DcMotor outtakeR;
    public RampRobotOuttake(HardwareMap hardwareMap) {
        // Mapeia o motor do spinner pelo seu nome de configuração no hardwareMap.
        outtakeL = hardwareMap.get(DcMotor.class, "OUT_L"); // Motor esquerdo do outtake
        outtakeR = hardwareMap.get(DcMotor.class, "OUT_R"); // Motor direito do outtake

        // Define o modo de operação do motore para rodar sem encoders (controle de potência).
        outtakeL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        outtakeR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    //metodo para o spinner do outtake
    public void spin(double veloL, double veloR){
        outtakeL.setPower(veloL);
        outtakeR.setPower(veloR);
    }
}