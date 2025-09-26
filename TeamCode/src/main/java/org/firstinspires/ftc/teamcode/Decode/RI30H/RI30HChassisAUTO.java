package org.firstinspires.ftc.teamcode.Decode.RI30H;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class RI30HChassisAUTO {
    // Declaração dos motores do chassi
    DcMotor motorFL;
    DcMotor motorFR;
    DcMotor motorBL;
    DcMotor motorBR;

    // Declaração da Unidade de Medição Inercial (IMU)
    IMU imu;

    int distancia = 0;

    //Construtor da classe RI30HChassisAUTO.
    public RI30HChassisAUTO(HardwareMap hardwareMap) {
        imu = hardwareMap.get(IMU.class, "imu");
        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.RIGHT,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD
        ));
        imu.initialize(parameters);

        // Mapeia os motores do chassi pelos seus nomes de configuração no hardwareMap.
        motorFL = hardwareMap.get(DcMotor.class, "FL"); // Motor Frontal Esquerdo
        motorFR = hardwareMap.get(DcMotor.class, "FR"); // Motor Frontal Direito
        motorBL = hardwareMap.get(DcMotor.class, "BL"); // Motor Traseiro Esquerdo
        motorBR = hardwareMap.get(DcMotor.class, "BR"); // Motor Traseiro Direito

        // Define o modo de operação dos motores para rodar sem encoders (controle de potência).
        motorFL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorFR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // Define o comportamento dos motores quando a potência é zero para 'BRAKE' (frear).
        motorFL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorFR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorBL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorBR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Inverte a direção dos motores do lado esquerdo para que todos os motores girem na direção correta para o movimento.
        motorFR.setDirection(DcMotorSimple.Direction.REVERSE);
        motorBR.setDirection(DcMotorSimple.Direction.REVERSE);

        motorFL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorFR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorBL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorBR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        motorFL.setTargetPosition(0);
        motorFR.setTargetPosition(0);
        motorBL.setTargetPosition(0);
        motorBR.setTargetPosition(0);

        motorFL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorFR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorBL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorBR.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        motorFL.setPower(1);
        motorFR.setPower(1);
        motorBL.setPower(1);
        motorBR.setPower(1);
    }

    public void andar(int cm){
        cm *= 16.4;
        distancia += cm;
        motorFL.setTargetPosition(distancia);
        motorFR.setTargetPosition(distancia);
        motorBL.setTargetPosition(distancia);
        motorBR.setTargetPosition(distancia);
    }
}
