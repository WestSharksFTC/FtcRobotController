package org.firstinspires.ftc.teamcode.Decode.RI30H;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class RI30HOuttake {
    DcMotor motorSpinner;
    Servo servoBreak;
    public RI30HOuttake(HardwareMap hardwareMap){
        // Mapeia o motor do spinner pelo seu nome de configuração no hardwareMap.
        motorSpinner = hardwareMap.get(DcMotor.class, "Spinner"); // Motor do spinner
        servoBreak = hardwareMap.get(Servo.class, "Servo"); // Motor da trava das bolas

        // Define o modo de operação do motore para rodar sem encoders (controle de potência).
        motorSpinner.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Inverte a direção do motor do spinner para que o motor gire na direção correta para o movimento.
        motorSpinner.setDirection(DcMotorSimple.Direction.FORWARD);
    }

    public void spin(double velo){
        motorSpinner.setPower(-velo);
    }

    public void servo(boolean right, boolean left){
        if(right){
            servoBreak.setPosition(0);
        }else if(left){
            servoBreak.setPosition(0.8);
        }
    }
}