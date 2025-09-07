package org.firstinspires.ftc.teamcode.Decode.RI30H;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class RI30HChassis {
    // Declaração dos motores do chassi
    DcMotor motorBL, motorBR, motorFL, motorFR;

    // Declaração da Unidade de Medição Inercial (IMU)
    IMU imu;

    //Construtor da classe RI30HChassis.
    public RI30HChassis(HardwareMap hardwareMap){
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
        motorFL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorFR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorBL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorBR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Define o comportamento dos motores quando a potência é zero para 'BRAKE' (frear).
        motorFL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorFR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorBL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorBR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Inverte a direção dos motores do lado esquerdo para que todos os motores girem na direção correta para o movimento.
        motorFL.setDirection(DcMotorSimple.Direction.REVERSE);
        motorBL.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void andar(double drive, double turn, double strafe, double powerReduction, boolean imuReset){
        // Normaliza os valores de entrada para evitar que a potência exceda 1.
        double max = Math.max(Math.abs(strafe) + Math.abs(drive) + Math.abs(turn), 1);

        // Calcula o fator de redução de potência. powerReduction deve ser um valor entre 0 e 1.
        // Ex: se powerReduction=0, drivePower=1; se powerReduction=1, drivePower=0.5
        double drivePower = -(0.5 * powerReduction) + 1;

        // Reseta o yaw da IMU se imuReset for verdadeiro.
        if(imuReset){
            imu.resetYaw();
        }

        // Obtém o heading atual do robô em radianos a partir da IMU.
        double heading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

        // Ajusta os valores de drive e strafe com base no heading do robô (field-centric drive).
        // Isso permite que o robô se mova em relação ao campo, independentemente de sua orientação.
        double adjustedStrafe = drive * Math.sin(heading) + strafe * Math.cos(heading);
        double adjustedDrive = drive * Math.cos(heading) - strafe * Math.sin(heading);

        // Calcula e define a potência para cada motor, normalizando pelo valor máximo.
        motorFL.setPower(((adjustedDrive + adjustedStrafe + turn) / max) * drivePower);
        motorFR.setPower(((adjustedDrive - adjustedStrafe - turn) / max) * drivePower);
        motorBL.setPower(((adjustedDrive - adjustedStrafe + turn) / max) * drivePower);
        motorBR.setPower(((adjustedDrive + adjustedStrafe - turn) / max) * drivePower);
    }
}