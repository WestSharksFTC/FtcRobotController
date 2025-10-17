package org.firstinspires.ftc.teamcode.Decode.RampRobot;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class RampRobotChassis {
    // Declaração dos motores do chassi
    DcMotor motorFL;
    DcMotor motorFR;
    DcMotor motorBL;
    DcMotor motorBR;

    //Construtor da classe RI30HChassisAUTO.
    public RampRobotChassis(HardwareMap hardwareMap){
        // Mapeia os motores do chassi pelos seus nomes de configuração no hardwareMap.
        motorFL = hardwareMap.get(DcMotor.class, "FL"); // Motor Frontal Esquerdo
        motorFR = hardwareMap.get(DcMotor.class, "FR"); // Motor Frontal Direito
        motorBL = hardwareMap.get(DcMotor.class, "BL"); // Motor Traseiro Esquerdo
        motorBR = hardwareMap.get(DcMotor.class, "BR"); // Motor Traseiro Direito

        // Define o modo de operação dos motores para rodar sem encoders (controle de potência).
        motorFL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorFR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorBL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorBR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Define o comportamento dos motores quando a potência é zero para 'BRAKE' (frear).
        motorFL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorFR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorBL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorBR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Inverte a direção do motor frontal direito para que ele gire na direção correta para o movimento.
        motorFR.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void andar(double drive, double turn, double strafe, double powerReduction){

        double max = Math.max(Math.abs(strafe) + Math.abs(drive) + Math.abs(turn), 1);

        double drivePower = -(0.5 * powerReduction) + 1;

        motorFL.setPower(drivePower * (drive - turn - strafe) / max);
        motorFR.setPower(drivePower * (drive + turn + strafe) / max);
        motorBL.setPower(drivePower * (-drive + turn - strafe) / max);
        motorBR.setPower(drivePower * (-drive - turn + strafe) / max);
    }
}