package org.firstinspires.ftc.teamcode.Decode.RI30H;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class RI30HOuttake {
    DcMotor motorSpinner;
    public RI30HOuttake(HardwareMap hardwareMap){
        // Mapeia o motor do spinner pelo seu nome de configuração no hardwareMap.
        motorSpinner = hardwareMap.get(DcMotor.class, "Spinner"); // Motor Frontal Esquerdo

        // Define o modo de operação do motore para rodar sem encoders (controle de potência).
        motorSpinner.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Inverte a direção do motor do spinner para que o motor gire na direção correta para o movimento.
        motorSpinner.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void spin(double joystick, double velo){
         velo += joystick;
        motorSpinner.setPower(velo);
    }
}
