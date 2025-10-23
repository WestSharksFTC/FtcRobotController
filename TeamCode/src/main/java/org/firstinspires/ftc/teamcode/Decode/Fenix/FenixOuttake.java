package org.firstinspires.ftc.teamcode.Decode.Fenix;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;


public class FenixOuttake {
    // Declaração do motor do chassi.
    DcMotor outtake;

    //Construtor da classe FenixOuttake.
    public FenixOuttake(HardwareMap hardwareMap) {
        // Mapeia o motor do outtake pelo seu nome de configuração no hardwareMap.
        outtake = hardwareMap.get(DcMotor.class, "OUT"); // Motor do outtake

        // Define o modo de operação do motor para rodar sem encoder (controle de potência).
        outtake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Inverte a direção do motor para que ele gire na direção correta para o movimento.
    }

    //Criação da função para funcionar o outtake.
    public void runOuttake(boolean one, boolean two, boolean three, boolean four){
        if(one){
            outtake.setPower(0);
        }else if(two){
            outtake.setPower(0.5);
        }else if(three){
            outtake.setPower(0.75);
        } else if(four){
            outtake.setPower(1);
        }
    }
}