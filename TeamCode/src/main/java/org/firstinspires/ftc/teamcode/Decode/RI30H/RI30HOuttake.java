package org.firstinspires.ftc.teamcode.Decode.RI30H;

import static android.os.SystemClock.sleep;
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
            //Primeiro ciclo
            servoBreak.setPosition(0.5);
            sleep(300);
            servoBreak.setPosition(0);
            sleep(2500);
            //Segundo ciclo
            servoBreak.setPosition(0.5);
            sleep(300);
            servoBreak.setPosition(0);
            sleep(2500);
            //Terceiro ciclo
            servoBreak.setPosition(0.5);
            sleep(300);
            servoBreak.setPosition(0);
            sleep(2500);
        }else if(left){
            servoBreak.setPosition(0.5);
        }
    }

    public void servoAuto(){
        //Primeiro ciclo
        servoBreak.setPosition(0.5);
        sleep(300);
        servoBreak.setPosition(0);
        sleep(2500);
        //Segundo ciclo
        servoBreak.setPosition(0.5);
        sleep(300);
        servoBreak.setPosition(0);
        sleep(2500);
        //Terceiro ciclo
        servoBreak.setPosition(0.5);
        sleep(300);
        servoBreak.setPosition(0);
        sleep(2500);

    }
}