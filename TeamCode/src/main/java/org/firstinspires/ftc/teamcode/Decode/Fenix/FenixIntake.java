package org.firstinspires.ftc.teamcode.Decode.Fenix;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class FenixIntake{
    // Declaração do motor do intake.
    DcMotor intake;

    //Construtor da classe FenixIntake.
    public FenixIntake(HardwareMap hardwareMap) {
        // Mapeia o motor do intake pelo seu nome de configuração no hardwareMap.
        intake = hardwareMap.get(DcMotor.class, "IN");

        // Define o modo de operação do motor para rodar sem encoder (controle de potência).
        intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Define o comportamento do motor quando a potência é zero para 'BRAKE' (frear).
        intake.setDirection(DcMotorSimple.Direction.REVERSE);

        // Inverte a direção do motor para que ele gire na direção correta para o movimento.
    }

    //Criação da função para funcionar o intake.
    public void runIntake(boolean one, boolean two, boolean three, boolean four){
        if(one){
            intake.setPower(0);
        }else if(two){
            intake.setPower(0.5);
        }else if(three){
            intake.setPower(0.75);
        } else if(four){
            intake.setPower(1);
        }
    }
}