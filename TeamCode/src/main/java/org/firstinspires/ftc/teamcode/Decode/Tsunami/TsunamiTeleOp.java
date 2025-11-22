package org.firstinspires.ftc.teamcode.Decode.Tsunami;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;

@TeleOp
public class TsunamiTeleOp extends OpMode {

    private ElapsedTime timer = new ElapsedTime();
    private ElapsedTime outtakeTimer = new ElapsedTime(); // Timer para o Outtake

    TsunamiChassis drive = new TsunamiChassis();
    TsunamiIntake intake = new TsunamiIntake();
    TsunamiOuttake outtake = new TsunamiOuttake();

    TsunamiIntake.DetectedColor detectedColor;

    private Limelight3A limelight;

    double forward, strafe, turn, motorIntake, motorOuttake, robotAngle;
    boolean imuReset, autoIndexer, turnIndexer, turnMeioIndexer;

    // Variáveis de controle de botão para detectar o "aperto" (borda de subida)
    private boolean botao_x_pressed = false;
    private boolean botao_b_pressed = false;

    // --- Máquina de Estados do INTAKE ---
    private enum IntakeState {
        INACTIVE, // Parado, esperando o comando
        WAITING_FOR_ARTIFACT_1, // Esperando o primeiro artifact
        MOVING_TO_SLOT_2, // Movendo para a posição 2
        WAITING_FOR_ARTIFACT_2, // Esperando o segundo artifact
        MOVING_TO_SLOT_3, // Movendo para a posição 3
        WAITING_FOR_ARTIFACT_3, // Esperando o terceiro artifact
        FULL // Intake cheio
    }
    private IntakeState currentIntakeState = IntakeState.INACTIVE;

    // Máquina de Estados do OUTTAKE
    private enum OuttakeState {
        INACTIVE,
        START,
        WAIT_FOR_POS_1,
        DROP_ARTIFACT_1_WAIT_1,
        DROP_ARTIFACT_1_SERVO_OPEN,
        DROP_ARTIFACT_1_SERVO_CLOSE,
        DROP_ARTIFACT_1_WAIT_3,
        START_SLOT_1,
        WAIT_FOR_POS_2,
        DROP_ARTIFACT_2_WAIT_1,
        DROP_ARTIFACT_2_SERVO_OPEN,
        DROP_ARTIFACT_2_SERVO_CLOSE,
        DROP_ARTIFACT_2_WAIT_3,
        START_SLOT_2,
        WAIT_FOR_POS_3,
        DROP_ARTIFACT_3_WAIT_1,
        DROP_ARTIFACT_3_SERVO_OPEN,
        DROP_ARTIFACT_3_SERVO_CLOSE,
        DROP_ARTIFACT_3_WAIT_3,
        GO_TO_HOME,
        WAIT_FOR_HOME,
        FINISHED
    }
    private OuttakeState currentOuttakeState = OuttakeState.INACTIVE;


    @Override
    public void init() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        drive.init(hardwareMap);
        intake.init(hardwareMap);
        outtake.init(hardwareMap);

        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.pipelineSwitch(9); // April tag #24 pipeline (cesta vermelha)

        timer.reset();

        // Garante que o indexador comece na posição inicial
        intake.goToInPos1();
    }

    @Override
    public void start() {
        limelight.start();
    }

    @Override
    public void loop() {
        // Gamepad 1 - CHASSIS
        // controles
        forward = -gamepad1.left_stick_y;
        strafe = gamepad1.left_stick_x;
        turn = gamepad1.right_stick_x;
        imuReset = gamepad1.left_bumper;

        robotAngle = drive.getHeading();

        if(imuReset){
            drive.imu.resetYaw();
        }
        drive.driveFieldRelative(forward, strafe, turn);


        // Gamepad 2 - SUBSISTEMAS
        // controles
        motorIntake = gamepad2.left_trigger;
        motorOuttake = gamepad2.right_trigger;
        autoIndexer = gamepad2.aWasPressed();
        turnIndexer = gamepad2.dpadRightWasPressed();
        turnMeioIndexer = gamepad2.dpadLeftWasPressed();

        LLResult llResult = limelight.getLatestResult();
        double tx = llResult.getTx();
        double ty = llResult.getTy();

        // limites medidos no robô para o ajuste de angulo vertical do shooter
        double tyMin = -2.8;   // mais longe
        double tyMax = 16.5;   // mais perto
        // interpolação linear invertida
        double normalized = (ty - tyMin) / (tyMax - tyMin);
        double servoOutPos = 1.0 - normalized;   // invertido porque 0=baixo e 1=alto

        // rpm desejado para cada extremo
        double rpmNear = 2100;   // perto (tyMax)
        double rpmFar  = 3300;   // longe (tyMin)

        // interpolation: tyMax → rpmNear   |   tyMin → rpmFar
        double targetRPM = rpmFar - normalized * (rpmFar - rpmNear);

        // Lógica de Detecção de Botão

        // Aciona o INTAKE (X)
        if (gamepad2.x && !botao_x_pressed) {
            // Alterna o estado do Intake: INACTIVE -> WAITING_FOR_ARTIFACT_1 ou vice-versa
            if (currentIntakeState == IntakeState.INACTIVE || currentIntakeState == IntakeState.FULL) {
                currentIntakeState = IntakeState.WAITING_FOR_ARTIFACT_1;
            } else {
                currentIntakeState = IntakeState.INACTIVE;
            }
        }
        botao_x_pressed = gamepad2.x; // Atualiza o estado do botão

        // Aciona o OUTTAKE (B)
        if (gamepad2.b && !botao_b_pressed && currentOuttakeState == OuttakeState.INACTIVE) {
            currentOuttakeState = OuttakeState.START;
            outtake.setTargetRPM(targetRPM); // Liga o motor do Outtake
        }
        botao_b_pressed = gamepad2.b; // Atualiza o estado do botão


        // Motor do Intake
        // O motor só deve ligar se o estado não for INACTIVE ou FULL
        if (currentIntakeState != IntakeState.INACTIVE && currentIntakeState != IntakeState.FULL) {
            intake.setPowerMotorIn(0.75);
        } else {
            intake.setPowerMotorIn(motorIntake); // Permite controle manual se o automático estiver inativo
        }

        // Motores do Outtake
        // O motor do Outtake é ligado no início da máquina de estados do Outtake
        if (currentOuttakeState == OuttakeState.INACTIVE) {
            outtake.setOuttakePower(motorOuttake); // Permite controle manual se o automático estiver inativo
        }else{
            outtake.updateShooterControl();
        }


        // Máquina de Estados do INTAKE
        switch (currentIntakeState) {
            case WAITING_FOR_ARTIFACT_1:
                // Fica neste estado até um artifact ser detectado
                if (detectedColor == TsunamiIntake.DetectedColor.PURPLE || detectedColor == TsunamiIntake.DetectedColor.GREEN) {
                    intake.goToInPos2(); // Manda o motor para a próxima posição
                    currentIntakeState = IntakeState.MOVING_TO_SLOT_2;
                }
                break;

            case MOVING_TO_SLOT_2:
                // Espera o motor terminar o movimento
                if (!intake.motorIndexer.isBusy()) {
                    currentIntakeState = IntakeState.WAITING_FOR_ARTIFACT_2; // Pronto para o próximo artifact
                }
                break;

            case WAITING_FOR_ARTIFACT_2:
                if (detectedColor == TsunamiIntake.DetectedColor.PURPLE || detectedColor == TsunamiIntake.DetectedColor.GREEN) {
                    intake.goToInPos3();
                    currentIntakeState = IntakeState.MOVING_TO_SLOT_3;
                }
                break;

            case MOVING_TO_SLOT_3:
                if (!intake.motorIndexer.isBusy()) {
                    currentIntakeState = IntakeState.WAITING_FOR_ARTIFACT_3;
                }
                break;

            case WAITING_FOR_ARTIFACT_3:
                if (detectedColor == TsunamiIntake.DetectedColor.PURPLE || detectedColor == TsunamiIntake.DetectedColor.GREEN) {
                    // O intake está cheio, para o motor e muda de estado
                    intake.setPowerMotorIn(0.0);
                    currentIntakeState = IntakeState.FULL;
                }
                break;

            case FULL:
            case INACTIVE:
                // Não faz nada, espera o comando do piloto
                break;
        }


        // Máquina de Estados do OUTTAKE
        switch (currentOuttakeState) {
            case START:
                intake.goToOutPos1();
                outtakeTimer.reset();
                currentOuttakeState = OuttakeState.WAIT_FOR_POS_1;
                break;

            case WAIT_FOR_POS_1:
                // Espera o motor chegar E o tempo de 2000ms
                if (!intake.motorIndexer.isBusy() && outtake.isAtTargetRPM(50)) {
                    outtakeTimer.reset();
                    currentOuttakeState = OuttakeState.DROP_ARTIFACT_1_WAIT_1;
                }
                break;

            case DROP_ARTIFACT_1_WAIT_1:
                // Espera 250ms
                if (outtakeTimer.milliseconds() >= 100 && outtake.isAtTargetRPM(50)) {
                    intake.setServoPos(0.3); // Sobe o servo
                    outtakeTimer.reset();
                    currentOuttakeState = OuttakeState.DROP_ARTIFACT_1_SERVO_OPEN;
                }
                break;

            case DROP_ARTIFACT_1_SERVO_OPEN:
                // Espera 400ms (sleep(400))
                if (outtakeTimer.milliseconds() >= 250 && outtake.isAtTargetRPM(50)) {
                    intake.setServoPos(0.0); // Fecha o servo
                    outtakeTimer.reset();
                    currentOuttakeState = OuttakeState.DROP_ARTIFACT_1_SERVO_CLOSE;
                }
                break;

            case DROP_ARTIFACT_1_SERVO_CLOSE:
                // Espera 250ms (sleep(250))
                if (outtakeTimer.milliseconds() >= 100 && outtake.isAtTargetRPM(50)) {
                    outtakeTimer.reset();
                    currentOuttakeState = OuttakeState.DROP_ARTIFACT_1_WAIT_3;
                }
                break;

            case DROP_ARTIFACT_1_WAIT_3:
                // Espera 250ms (sleep(250))
                if (outtakeTimer.milliseconds() >= 100 && outtake.isAtTargetRPM(50)) {
                    currentOuttakeState = OuttakeState.START_SLOT_1;
                }
                break;

            // --- SLOT 1 (Antigo slotOut == 1) ---
            case START_SLOT_1:
                intake.goToOutPos2();
                outtakeTimer.reset();
                currentOuttakeState = OuttakeState.WAIT_FOR_POS_2;
                break;

            case WAIT_FOR_POS_2:
                // Espera o motor chegar E o tempo de 500ms
                if (!intake.motorIndexer.isBusy() && outtake.isAtTargetRPM(50)) {
                    outtakeTimer.reset();
                    currentOuttakeState = OuttakeState.DROP_ARTIFACT_2_WAIT_1;
                }
                break;

            case DROP_ARTIFACT_2_WAIT_1:
                // Espera 500ms (sleep(500) - do seu código original)
                if (outtakeTimer.milliseconds() >= 100 && outtake.isAtTargetRPM(50)) {
                    intake.setServoPos(0.3); // Abre o servo
                    outtakeTimer.reset();
                    currentOuttakeState = OuttakeState.DROP_ARTIFACT_2_SERVO_OPEN;
                }
                break;

            case DROP_ARTIFACT_2_SERVO_OPEN:
                // Espera 400ms (sleep(400))
                if (outtakeTimer.milliseconds() >= 250 && outtake.isAtTargetRPM(50)) {
                    intake.setServoPos(0.0); // Fecha o servo
                    outtakeTimer.reset();
                    currentOuttakeState = OuttakeState.DROP_ARTIFACT_2_SERVO_CLOSE;
                }
                break;

            case DROP_ARTIFACT_2_SERVO_CLOSE:
                // Espera 250ms (sleep(250))
                if (outtakeTimer.milliseconds() >= 100 && outtake.isAtTargetRPM(50)) {
                    outtakeTimer.reset();
                    currentOuttakeState = OuttakeState.DROP_ARTIFACT_2_WAIT_3;
                }
                break;

            case DROP_ARTIFACT_2_WAIT_3:
                // Espera 250ms (sleep(250))
                if (outtakeTimer.milliseconds() >= 100 && outtake.isAtTargetRPM(50)) {
                    currentOuttakeState = OuttakeState.START_SLOT_2;
                }
                break;

            // --- SLOT 2 (Antigo slotOut == 2) ---
            case START_SLOT_2:
                intake.goToOutPos3();
                outtakeTimer.reset();
                currentOuttakeState = OuttakeState.WAIT_FOR_POS_3;
                break;

            case WAIT_FOR_POS_3:
                // Espera o motor chegar E o tempo de 250ms
                if (!intake.motorIndexer.isBusy() && outtake.isAtTargetRPM(50)) {
                    outtakeTimer.reset();
                    currentOuttakeState = OuttakeState.DROP_ARTIFACT_3_WAIT_1;
                }
                break;

            case DROP_ARTIFACT_3_WAIT_1:
                // Espera 250ms (sleep(250))
                if (outtakeTimer.milliseconds() >= 100 && outtake.isAtTargetRPM(50)) {
                    intake.setServoPos(0.3); // Abre o servo
                    outtakeTimer.reset();
                    currentOuttakeState = OuttakeState.DROP_ARTIFACT_3_SERVO_OPEN;
                }
                break;

            case DROP_ARTIFACT_3_SERVO_OPEN:
                // Espera 400ms (sleep(400))
                if (outtakeTimer.milliseconds() >= 250 && outtake.isAtTargetRPM(50)) {
                    intake.setServoPos(0.0); // Fecha o servo
                    outtakeTimer.reset();
                    currentOuttakeState = OuttakeState.DROP_ARTIFACT_3_SERVO_CLOSE;
                }
                break;

            case DROP_ARTIFACT_3_SERVO_CLOSE:
                // Espera 250ms (sleep(250))
                if (outtakeTimer.milliseconds() >= 100 && outtake.isAtTargetRPM(50)) {
                    outtakeTimer.reset();
                    currentOuttakeState = OuttakeState.GO_TO_HOME;
                }
                break;

            case GO_TO_HOME:
                // Espera 250ms (sleep(250))
                if (outtakeTimer.milliseconds() >= 100 && outtake.isAtTargetRPM(50)) {
                    intake.goToInPos1(); // Volta para a posição inicial
                    outtakeTimer.reset();
                    currentOuttakeState = OuttakeState.WAIT_FOR_HOME;
                }
                break;

            case WAIT_FOR_HOME:
                // Espera 1000ms (sleep(1000))
                if (outtakeTimer.milliseconds() >= 100 && outtake.isAtTargetRPM(50)) {
                    currentOuttakeState = OuttakeState.FINISHED;
                }
                break;

            case FINISHED:
                outtake.setTargetRPM(0.0);
                outtake.setOuttakePower(motorOuttake); // Volta o controle do motor para o gamepad
                currentOuttakeState = OuttakeState.INACTIVE;
                break;

            case INACTIVE:
                // Não faz nada, espera o botão ser pressionado.
                break;
        }


        // --- Outros Controles (Mantidos) ---
        if(gamepad2.dpad_down) {
            intake.setServoPos(0.0);
        }else if(gamepad2.dpad_up){
            intake.setServoPos(0.3);
        }

        if(turnIndexer) {
            intake.setMotorIndexer();
        }

        if(turnMeioIndexer){
            intake.setMeioMotorIndexer();
        }

        if(llResult != null && llResult.isValid()){
            if (Math.abs(tx) >= 0.5) {
                double error = tx;
                double k = 0.001;

                // Ajuste proporcional direto
                double delta = error * k * -1;

                double newPos = outtake.getServoTurretXCurrentPosition() - delta;

                outtake.setTurretAngleX(outtake.clamp(newPos, 0.0, 1.0));
            }
        }

        servoOutPos = outtake.clamp(servoOutPos, 0.0, 1.0);

        outtake.setTurretAngleY(servoOutPos);



        // Telemetrias
        telemetry.addLine("POSIÇÃO DO SELETOR DE COR");
        telemetry.addData("Posição do indexer", intake.getPositionIndexer());
        telemetry.addLine();

        telemetry.addLine("LIGA/DESLIGA O SELETOR DE COR - INTAKE");
        telemetry.addData("Estado do Intake", currentIntakeState);
        telemetry.addLine();

        telemetry.addLine("LIGA/DESLIGA O SELETOR DE COR - OUTTAKE");
        telemetry.addData("Estado do Outtake", currentOuttakeState);
        telemetry.addData("Outtake Timer", "%.2f", outtakeTimer.seconds());
        telemetry.addLine();

        telemetry.addLine("COR DETECTADA PELO SENSOR");
        detectedColor = intake.getDetectedColor(telemetry);
        telemetry.addData("Color Detected", detectedColor);
        telemetry.addLine();

        telemetry.addLine("POSIÇÕES DO SERVO DA RAMPA");
        intake.getServoPos(telemetry);
        telemetry.addLine();

        telemetry.addLine("DADOS DA ODOMETRIA DO ROBÔ");
        telemetry.addData("Odometria X", drive.getX());
        telemetry.addData("Odometria y", drive.getY());
        telemetry.addData("Odometria Ângulo", drive.getHeading());
        telemetry.addLine();

        telemetry.addLine("ÂNGULO DO ROBÔ EM RELAÇÃO A ARENA");
        telemetry.addData("Angulo do robô", robotAngle);
        telemetry.addLine();

        telemetry.addLine("DADOS DO SUBSISTEMA DE OUTTAKE");
        outtake.showOuttakeTelemetry(telemetry);
        telemetry.addLine();

        telemetry.addLine("DADOS DA LIMELIGHT");
        if(llResult != null && llResult.isValid()){
            Pose3D botPose = llResult.getBotpose_MT2();
            telemetry.addData("Target X", llResult.getTx());
            telemetry.addData("Target Y", llResult.getTy());
            telemetry.addData("Target Area", llResult.getTa());
            telemetry.addLine();
            telemetry.addData("BotPose", botPose.toString());
            telemetry.addData("Yaw", botPose.getOrientation().getYaw());
        }
        telemetry.addLine();

        telemetry.addLine("DADOS DA TORRETA");
        telemetry.addData("Posição X da torreta", outtake.getServoTurretXCurrentPosition());
        telemetry.addLine();


        telemetry.addLine("TIMER");
        telemetry.addData("Status", "Run Time: " + timer.seconds());
        telemetry.addLine();

        telemetry.addLine("ENERGIA");
        telemetry.addData("Voltagem", outtake.getVoltage(hardwareMap));
        telemetry.addLine();
    }
}