package org.firstinspires.ftc.teamcode.Decode.Tsunami;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.Servo;
import static android.os.SystemClock.sleep;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class TsunamiIntake {

    private DcMotor motorIn;
    private DcMotor motorIndexer;
    private Servo servoRampL;
    private Servo servoRampR;

    private int positionIndexer = 0;

    private static final int idexerInPos1 = 0, idexerInPos2 = 170, idexerInPos3 = 340;
    private static final int idexerOutPos1 = 255, idexerOutPos2 = 425, idexerOutPos3 = 595;

    NormalizedColorSensor colorSensor;

    public enum DetectedColor{
        PURPLE,
        GREEN,
        UNKNOWN
    }

    public enum IntakeState{
        INIT,
        MOVING_TO_POS1,
        POS1,
        MOVING_TO_POS2,
        POS2,
        MOVING_TO_POS3,
        POS3,
        DONE
    }

    DetectedColor detectedColor;

    IntakeState state;

    public void init(HardwareMap hardwareMap){
        colorSensor = hardwareMap.get(NormalizedColorSensor.class, "sensor_color");
        colorSensor.setGain(15);

        state = IntakeState.INIT;

        motorIn = hardwareMap.get(DcMotor.class, "motor_intake");
        motorIn.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorIn.setDirection(DcMotor.Direction.REVERSE);

        motorIndexer = hardwareMap.get(DcMotor.class, "motor_indexer");
        motorIndexer.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorIndexer.setPower(0.0);
        motorIndexer.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorIndexer.setTargetPosition(0);
        motorIndexer.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorIndexer.setPower(0.3);


        servoRampL = hardwareMap.get(Servo.class, "servo_ramp_left");
        servoRampR = hardwareMap.get(Servo.class, "servo_ramp_right");

        servoRampR.setDirection(Servo.Direction.REVERSE);

        servoRampL.scaleRange(0.0, 0.85);
        servoRampR.scaleRange(0.0, 0.85);

        servoRampL.setPosition(0.0);
        servoRampR.setPosition(0.0);
    }

    // Sensor de cor
    public TsunamiIntake.DetectedColor getDetectedColor(Telemetry telemetry){
        NormalizedRGBA colors = colorSensor.getNormalizedColors(); // return 4 values

        float normRed, normGreen, normBlue;
        normRed = colors.red / colors.alpha;
        normGreen = colors.green / colors.alpha;
        normBlue = colors.blue / colors.alpha;

        telemetry.addData("red", normRed);
        telemetry.addData("green", normGreen);
        telemetry.addData("blue", normBlue);
        telemetry.addData("alpha", colors.alpha);

        /*

        PURPLE = R= <.4, G= <.45, B= >.85

        GREEN = R= <.2 ,G= >.2 ,B= <.85

         */

        if(normRed > 0.3 && normGreen < 0.65 && normBlue > 0.85){
            return TsunamiIntake.DetectedColor.PURPLE;
        }else if(normRed < 0.3 && normGreen > 0.65 && normBlue < 0.85){
            return TsunamiIntake.DetectedColor.GREEN;
        }else{
            return TsunamiIntake.DetectedColor.UNKNOWN;
        }
    }

    public void runAutoIntake(){
        switch(state){
            case INIT:
                this.goToInPos1();
                this.setPowerMotorIn(0.5);
                state = IntakeState.MOVING_TO_POS1;
                break;
            case MOVING_TO_POS1:
                if (!motorIndexer.isBusy()) {
                    state = IntakeState.POS1;
                }
                break;
            case POS1:
                this.setPowerMotorIn(0.5);
                if(detectedColor == DetectedColor.PURPLE || detectedColor == DetectedColor.GREEN){
                    this.goToInPos2();
                    state = IntakeState.MOVING_TO_POS2;
                }
                break;
            case MOVING_TO_POS2:
                if (!motorIndexer.isBusy()) {
                    state = IntakeState.POS2;
                }
                break;
            case POS2:
                this.setPowerMotorIn(0.5);
                if(detectedColor == DetectedColor.PURPLE || detectedColor == DetectedColor.GREEN){
                    this.goToInPos3();
                    state = IntakeState.MOVING_TO_POS3;
                }
                break;
            case MOVING_TO_POS3:
                if (!motorIndexer.isBusy()) {
                    state = IntakeState.POS3;
                }
                break;
            case POS3:
                this.setPowerMotorIn(0.5);
                if(detectedColor == DetectedColor.PURPLE || detectedColor == DetectedColor.GREEN){
                    this.setPowerMotorIn(0.2);
                    state = IntakeState.DONE;
                }
                break;
            case DONE:
                break;
            default:
                break;
        }
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
        positionIndexer += 170;
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