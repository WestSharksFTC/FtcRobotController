package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuck.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class WDIntake {

    public DcMotor intake;
    public Servo flowerRampRight;
    public Servo flowerRampLeft;

    public void init(HardwareMap hardwareMap) {
        // Mapeamento de Hardware
        intake = hardwareMap.get(DcMotor.class, "intake");
        flowerRampRight = hardwareMap.get(Servo.class, "flowerRampRight");
        flowerRampLeft = hardwareMap.get(Servo.class, "flowerRampLeft");

        // Direções
        intake.setDirection(DcMotor.Direction.FORWARD);
        flowerRampRight.setDirection(Servo.Direction.REVERSE);
        flowerRampLeft.setDirection(Servo.Direction.FORWARD);

        // Configurações do Motor do Intake
        intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        // Limites físicos dos servos da rampa
        flowerRampRight.scaleRange(0.050, 0.125);
        flowerRampLeft.scaleRange(0.875, 0.950);
    }

    // Ações do Motor do Intake
    public void setPower(double power){
        intake.setPower(power);
    }
    public void feed() {
        intake.setPower(0.75);
    }

    public void reverse() {
        intake.setPower(-0.25);
    }

    public void stop() {
        intake.setPower(0.0);
    }

    // Posições lógicas da Rampa
    public void downFlowerRamp() {
        flowerRampRight.setPosition(0.0);
        flowerRampLeft.setPosition(0.0);
    }

    public void upFlowerRamp() {
        flowerRampRight.setPosition(1.0);
        flowerRampLeft.setPosition(1.0);
    }

    // Leitura de status
    public double getLeftServoPosition() {
        return flowerRampLeft.getPosition();
    }

    public double getRightServoPosition() {
        return flowerRampRight.getPosition();
    }
}