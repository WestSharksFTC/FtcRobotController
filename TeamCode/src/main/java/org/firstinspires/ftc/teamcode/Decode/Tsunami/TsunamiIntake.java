package org.firstinspires.ftc.teamcode.Decode.Tsunami;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class TsunamiIntake {

    private DcMotor motorIn;
    private DcMotor motorIndexer;
    private Servo servoRampL;
    private Servo servoRampR;

    private int positionIndexer = 0;

    private static final int idexerInPos1 = 0, idexerInPos2 = 121, idexerInPos3 = 242;
    private static final int idexerOutPos1 = 182, idexerOutPos2 = 303, idexerOutPos3 = 424;

    public void init(HardwareMap hardwareMap){
        motorIn = hardwareMap.get(DcMotor.class, "motor_intake");
        motorIn.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorIn.setDirection(DcMotor.Direction.REVERSE);

        motorIndexer = hardwareMap.get(DcMotor.class, "motor_indexer");
        motorIndexer.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorIndexer.setPower(0.0);
        motorIndexer.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorIndexer.setTargetPosition(0);
        motorIndexer.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorIndexer.setPower(0.75);


        servoRampL = hardwareMap.get(Servo.class, "servo_ramp_left");
        servoRampR = hardwareMap.get(Servo.class, "servo_ramp_right");

        servoRampR.setDirection(Servo.Direction.REVERSE);

        servoRampL.scaleRange(0.0, 0.85);
        servoRampR.scaleRange(0.0, 0.85);

        servoRampL.setPosition(0.0);
        servoRampR.setPosition(0.0);
    }


    // Motor do intake para a coleta dos objetos de jogo
    public void setPowerMotorIn(double power) {
        motorIn.setPower(power);
    }


    // Servos da rampa que movem o objeto de jogo do seletor de cor para o outtake
    public void setServoPos(double angle){
        servoRampL.setPosition(angle);
        servoRampR.setPosition(angle);
    }

    public void getServoPos(Telemetry telemetry){
        double servoLeftPos = servoRampL.getPosition();
        double servoRightPos = servoRampR.getPosition();

        telemetry.addData("Servo Esquerdo", servoLeftPos);
        telemetry.addData("Servo Direito", servoRightPos);
    }


    // Motor do seletor de cor para movimentar para a posição dos objetos de jogo
    public int getPositionIndexer(){
        return motorIndexer.getCurrentPosition();
    }

    public void setMotorIndexer(){
        positionIndexer += 121;
        motorIndexer.setTargetPosition(positionIndexer);
    }


    // Posições para o intake
    public void goToInPos1(){
        motorIndexer.setTargetPosition(idexerInPos1);
    }

    public void goToInPos2(){
        motorIndexer.setTargetPosition(idexerInPos2);
    }

    public void goToInPos3(){
        motorIndexer.setTargetPosition(idexerInPos3);
    }


    // Posições para o outtake
    public void goToOutPos1(){
        motorIndexer.setTargetPosition(idexerOutPos1);
    }

    public void goToOutPos2(){
        motorIndexer.setTargetPosition(idexerOutPos2);
    }

    public void goToOutPos3(){
        motorIndexer.setTargetPosition(idexerOutPos3);
    }
}