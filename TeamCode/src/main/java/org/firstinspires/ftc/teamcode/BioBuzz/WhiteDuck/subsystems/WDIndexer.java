package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuck.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class WDIndexer {
    public DcMotor indexer;
    public Servo pollenLimiter;
    public DistanceSensor pollenSensorLeft;
    public DistanceSensor pollenSensorRight;

    // Limite em cm para considerar que a peça está cobrindo o sensor
    private static final double DETECTION_THRESHOLD_CM = 3.0;

    public void init(HardwareMap hardwareMap) {
        // Mapeamento do motor, servo e sensores
        indexer = hardwareMap.get(DcMotor.class, "indexer");
        pollenLimiter = hardwareMap.get(Servo.class, "pollenLimiter");
        pollenSensorLeft = hardwareMap.get(DistanceSensor.class, "pollenSensorLeft");
        pollenSensorRight = hardwareMap.get(DistanceSensor.class, "pollenSensorRight");

        // Direções
        indexer.setDirection(DcMotor.Direction.REVERSE);
        pollenLimiter.setDirection(Servo.Direction.FORWARD);

        // Configurações do Motor para não utilizar o encoder e deixar o freio inativo
        indexer.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        indexer.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        // Limite físico do servo
        pollenLimiter.scaleRange(0.05, 0.3);
    }

    // Ações claras para o Motor do Indexer
    public void feed() {
        indexer.setPower(0.75);
    }

    public void reverse() {
        indexer.setPower(-0.25);
    }

    public void stop() {
        indexer.setPower(0.0);
    }

    // Controle Lógico do Servo
    public void openServo() {
        pollenLimiter.setPosition(0.0);
    }

    public void closeServo() {
        pollenLimiter.setPosition(1.0);
    }

    // Verificação segura do estado do servo
    public boolean isClosed() {
        return pollenLimiter.getPosition() >= 0.25;
    }

    // Leitura individual dos sensores de distância em CM
    public double getDistanceLeftCM() {
        return pollenSensorLeft.getDistance(DistanceUnit.CM);
    }

    public double getDistanceRightCM() {
        return pollenSensorRight.getDistance(DistanceUnit.CM);
    }

    // Verificação unificada: se qualquer um dos lados detectar a peça a <= 3cm, retorna true
    public boolean hasPollen() {
        boolean leftDetected = getDistanceLeftCM() <= DETECTION_THRESHOLD_CM;
        boolean rightDetected = getDistanceRightCM() <= DETECTION_THRESHOLD_CM;

        return leftDetected || rightDetected;
    }
}