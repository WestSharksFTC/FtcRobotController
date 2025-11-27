package org.firstinspires.ftc.teamcode.Decode.Tsunami;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;

@Autonomous
public class Auto_Vermelho_Fundo_3 extends OpMode {

    private ElapsedTime outtakeTimer = new ElapsedTime(); // Timer para o Outtake
    TsunamiChassis drive = new TsunamiChassis();
    TsunamiIntake intake = new TsunamiIntake();
    TsunamiOuttake outtake = new TsunamiOuttake();

    TsunamiIntake.DetectedColor detectedColor;

    private Limelight3A limelight;

    double robotAngle;

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
    private Auto_Vermelho_Fundo_3.IntakeState currentIntakeState = Auto_Vermelho_Fundo_3.IntakeState.INACTIVE;

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
    private Auto_Vermelho_Fundo_3.OuttakeState currentOuttakeState = Auto_Vermelho_Fundo_3.OuttakeState.INACTIVE;

    // limites medidos no robô para o ajuste de angulo vertical do shooter
    private static final double tyMin = -2.8;   // mais longe
    private static final double tyMax = 16.5;   // mais perto

    // rpm desejado para cada extremo
    private static final double rpmNear = 2100;   // perto (tyMax)
    private static final double rpmFar = 3300;   // longe (tyMin)

    double startX, startY, startHeading;


    @Override
    public void init() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        drive.init(hardwareMap);
        intake.init(hardwareMap);
        outtake.init(hardwareMap);

        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.pipelineSwitch(9); // April tag #24 pipeline (cesta vermelha)

        // Garante que o indexador comece na posição inicial
        intake.goToInPos1();
    }

    @Override
    public void start() {
        limelight.start();
        startX = drive.getX();
        startY = drive.getY();
        startHeading = drive.getHeading();
    }

    @Override
    public void loop() {
        // CHASSIS
        robotAngle = drive.getHeading();

        // SUBSISTEMAS
        LLResult llResult = limelight.getLatestResult();
        double tx = llResult.getTx();
        double ty = llResult.getTy();

        // interpolação linear invertida
        double normalized = (ty - tyMin) / (tyMax - tyMin);
        double servoOutPos = 1.0 - normalized;   // invertido porque 0=baixo e 1=alto

        // interpolation: tyMax → rpmNear   |   tyMin → rpmFar
        double targetRPM = rpmFar - normalized * (rpmFar - rpmNear);


        // Alterna o estado do Intake: INACTIVE -> WAITING_FOR_ARTIFACT_1
        if (currentIntakeState == Auto_Vermelho_Fundo_3.IntakeState.INACTIVE && currentOuttakeState == OuttakeState.INACTIVE) {
            currentIntakeState = Auto_Vermelho_Fundo_3.IntakeState.WAITING_FOR_ARTIFACT_1;
        }


        if (currentIntakeState == Auto_Vermelho_Fundo_3.IntakeState.FULL && currentOuttakeState == OuttakeState.INACTIVE){
            currentIntakeState = Auto_Vermelho_Fundo_3.IntakeState.INACTIVE;
            currentOuttakeState = Auto_Vermelho_Fundo_3.OuttakeState.START;
            outtake.setTargetRPM(targetRPM); // Liga o motor do Outtake
        }


        // Motor do Intake
        // O motor só deve ligar se o estado não for INACTIVE ou FULL
        // ✅ CORRETO:
        if (currentIntakeState != IntakeState.INACTIVE && currentIntakeState != IntakeState.FULL) {
            intake.setPowerMotorIn(0.75);  // Liga quando ATIVO
        } else {
            intake.setPowerMotorIn(0.0);   // Desliga quando inativo ou cheio
        }

        // Motores do Outtake
        // O motor do Outtake é ligado no início da máquina de estados do Outtake
        if (currentOuttakeState != Auto_Vermelho_Fundo_3.OuttakeState.INACTIVE) {
            outtake.updateShooterControl();
        }


        // Máquina de Estados do INTAKE
        switch (currentIntakeState) {
            case WAITING_FOR_ARTIFACT_1:
                // Fica neste estado até um artifact ser detectado
                if (detectedColor == TsunamiIntake.DetectedColor.PURPLE || detectedColor == TsunamiIntake.DetectedColor.GREEN) {
                    intake.goToInPos2(); // Manda o motor para a próxima posição
                    double dist = drive.getX() - startX;

                    if (dist > -40) {  // 50cm
                        drive.driveForward(-0.3);
                    } else {
                        drive.drive(0,0,0);
                        currentIntakeState = Auto_Vermelho_Fundo_3.IntakeState.MOVING_TO_SLOT_2;

                    }
                }
                break;

            case MOVING_TO_SLOT_2:
                // Espera o motor terminar o movimento
                if (!intake.motorIndexer.isBusy()) {
                    currentIntakeState = Auto_Vermelho_Fundo_3.IntakeState.WAITING_FOR_ARTIFACT_2; // Pronto para o próximo artifact
                }
                break;

            case WAITING_FOR_ARTIFACT_2:
                if (detectedColor == TsunamiIntake.DetectedColor.PURPLE || detectedColor == TsunamiIntake.DetectedColor.GREEN) {
                    intake.goToInPos3();
                    currentIntakeState = Auto_Vermelho_Fundo_3.IntakeState.MOVING_TO_SLOT_3;
                }
                break;

            case MOVING_TO_SLOT_3:
                if (!intake.motorIndexer.isBusy()) {
                    currentIntakeState = Auto_Vermelho_Fundo_3.IntakeState.WAITING_FOR_ARTIFACT_3;
                }
                break;

            case WAITING_FOR_ARTIFACT_3:
                if (detectedColor == TsunamiIntake.DetectedColor.PURPLE || detectedColor == TsunamiIntake.DetectedColor.GREEN) {
                    // O intake está cheio, para o motor e muda de estado
                    intake.setPowerMotorIn(0.0);
                    currentIntakeState = Auto_Vermelho_Fundo_3.IntakeState.FULL;
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
                currentOuttakeState = Auto_Vermelho_Fundo_3.OuttakeState.WAIT_FOR_POS_1;

                break;

            case WAIT_FOR_POS_1:
                // Espera o motor chegar E o tempo de 2000ms
                if (!intake.motorIndexer.isBusy() && outtake.isAtTargetRPM(75)) {
                    outtakeTimer.reset();
                    currentOuttakeState = Auto_Vermelho_Fundo_3.OuttakeState.DROP_ARTIFACT_1_WAIT_1;
                }
                break;

            case DROP_ARTIFACT_1_WAIT_1:
                // Espera 250ms
                if (outtakeTimer.milliseconds() >= 100 && outtake.isAtTargetRPM(50)) {
                    intake.setServoPos(0.3); // Sobe o servo
                    outtakeTimer.reset();
                    currentOuttakeState = Auto_Vermelho_Fundo_3.OuttakeState.DROP_ARTIFACT_1_SERVO_OPEN;
                }
                break;

            case DROP_ARTIFACT_1_SERVO_OPEN:
                // Espera 400ms (sleep(400))
                if (outtakeTimer.milliseconds() >= 250 && outtake.isAtTargetRPM(50)) {
                    intake.setServoPos(0.0); // Fecha o servo
                    outtakeTimer.reset();
                    currentOuttakeState = Auto_Vermelho_Fundo_3.OuttakeState.DROP_ARTIFACT_1_SERVO_CLOSE;
                }
                break;

            case DROP_ARTIFACT_1_SERVO_CLOSE:
                // Espera 250ms (sleep(250))
                if (outtakeTimer.milliseconds() >= 100 && outtake.isAtTargetRPM(50)) {
                    outtakeTimer.reset();
                    currentOuttakeState = Auto_Vermelho_Fundo_3.OuttakeState.DROP_ARTIFACT_1_WAIT_3;
                }
                break;

            case DROP_ARTIFACT_1_WAIT_3:
                // Espera 250ms (sleep(250))
                if (outtakeTimer.milliseconds() >= 100 && outtake.isAtTargetRPM(50)) {
                    currentOuttakeState = Auto_Vermelho_Fundo_3.OuttakeState.START_SLOT_1;
                }
                break;

            // --- SLOT 1 (Antigo slotOut == 1) ---
            case START_SLOT_1:
                intake.goToOutPos2();
                outtakeTimer.reset();
                currentOuttakeState = Auto_Vermelho_Fundo_3.OuttakeState.WAIT_FOR_POS_2;
                break;

            case WAIT_FOR_POS_2:
                // Espera o motor chegar E o tempo de 500ms
                if (!intake.motorIndexer.isBusy() && outtake.isAtTargetRPM(50)) {
                    outtakeTimer.reset();
                    currentOuttakeState = Auto_Vermelho_Fundo_3.OuttakeState.DROP_ARTIFACT_2_WAIT_1;
                }
                break;

            case DROP_ARTIFACT_2_WAIT_1:
                // Espera 500ms (sleep(500) - do seu código original)
                if (outtakeTimer.milliseconds() >= 100 && outtake.isAtTargetRPM(50)) {
                    intake.setServoPos(0.3); // Abre o servo
                    outtakeTimer.reset();
                    currentOuttakeState = Auto_Vermelho_Fundo_3.OuttakeState.DROP_ARTIFACT_2_SERVO_OPEN;
                }
                break;

            case DROP_ARTIFACT_2_SERVO_OPEN:
                // Espera 400ms (sleep(400))
                if (outtakeTimer.milliseconds() >= 250 && outtake.isAtTargetRPM(50)) {
                    intake.setServoPos(0.0); // Fecha o servo
                    outtakeTimer.reset();
                    currentOuttakeState = Auto_Vermelho_Fundo_3.OuttakeState.DROP_ARTIFACT_2_SERVO_CLOSE;
                }
                break;

            case DROP_ARTIFACT_2_SERVO_CLOSE:
                // Espera 250ms (sleep(250))
                if (outtakeTimer.milliseconds() >= 100 && outtake.isAtTargetRPM(50)) {
                    outtakeTimer.reset();
                    currentOuttakeState = Auto_Vermelho_Fundo_3.OuttakeState.DROP_ARTIFACT_2_WAIT_3;
                }
                break;

            case DROP_ARTIFACT_2_WAIT_3:
                // Espera 250ms (sleep(250))
                if (outtakeTimer.milliseconds() >= 100 && outtake.isAtTargetRPM(50)) {
                    currentOuttakeState = Auto_Vermelho_Fundo_3.OuttakeState.START_SLOT_2;
                }
                break;

            // --- SLOT 2 (Antigo slotOut == 2) ---
            case START_SLOT_2:
                intake.goToOutPos3();
                outtakeTimer.reset();
                currentOuttakeState = Auto_Vermelho_Fundo_3.OuttakeState.WAIT_FOR_POS_3;
                break;

            case WAIT_FOR_POS_3:
                // Espera o motor chegar E o tempo de 250ms
                if (!intake.motorIndexer.isBusy() && outtake.isAtTargetRPM(50)) {
                    outtakeTimer.reset();
                    currentOuttakeState = Auto_Vermelho_Fundo_3.OuttakeState.DROP_ARTIFACT_3_WAIT_1;
                }
                break;

            case DROP_ARTIFACT_3_WAIT_1:
                // Espera 250ms (sleep(250))
                if (outtakeTimer.milliseconds() >= 100 && outtake.isAtTargetRPM(50)) {
                    intake.setServoPos(0.3); // Abre o servo
                    outtakeTimer.reset();
                    currentOuttakeState = Auto_Vermelho_Fundo_3.OuttakeState.DROP_ARTIFACT_3_SERVO_OPEN;
                }
                break;

            case DROP_ARTIFACT_3_SERVO_OPEN:
                // Espera 400ms (sleep(400))
                if (outtakeTimer.milliseconds() >= 250 && outtake.isAtTargetRPM(50)) {
                    intake.setServoPos(0.0); // Fecha o servo
                    outtakeTimer.reset();
                    currentOuttakeState = Auto_Vermelho_Fundo_3.OuttakeState.DROP_ARTIFACT_3_SERVO_CLOSE;
                }
                break;

            case DROP_ARTIFACT_3_SERVO_CLOSE:
                // Espera 250ms (sleep(250))
                if (outtakeTimer.milliseconds() >= 100 && outtake.isAtTargetRPM(50)) {
                    outtakeTimer.reset();
                    currentOuttakeState = Auto_Vermelho_Fundo_3.OuttakeState.GO_TO_HOME;
                }
                break;

            case GO_TO_HOME:
                // Espera 250ms (sleep(250))
                if (outtakeTimer.milliseconds() >= 100 && outtake.isAtTargetRPM(50)) {
                    intake.goToInPos1(); // Volta para a posição inicial
                    outtakeTimer.reset();
                    currentOuttakeState = Auto_Vermelho_Fundo_3.OuttakeState.WAIT_FOR_HOME;
                }
                break;

            case WAIT_FOR_HOME:
                // Espera 1000ms (sleep(1000))
                if (outtakeTimer.milliseconds() >= 100 && outtake.isAtTargetRPM(50)) {
                    currentOuttakeState = Auto_Vermelho_Fundo_3.OuttakeState.FINISHED;
                }
                break;

            case FINISHED:
                outtake.setTargetRPM(0.0);
                currentIntakeState = Auto_Vermelho_Fundo_3.IntakeState.INACTIVE;
                currentOuttakeState = Auto_Vermelho_Fundo_3.OuttakeState.INACTIVE;

                break;

            case INACTIVE:
                outtake.setOuttakePower(0.0);
                // Não faz nada, espera o botão ser pressionado.
                break;
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
        telemetry.addLine("DEBUG");
        telemetry.addData("WAIT_FOR_POS_1", "Indexer busy: %b, At RPM: %b", intake.motorIndexer.isBusy(), outtake.isAtTargetRPM(50));
        telemetry.addLine();

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

        telemetry.addLine("ENERGIA");
        telemetry.addData("Voltagem", outtake.getVoltage(hardwareMap));
        telemetry.addLine();
    }
}
