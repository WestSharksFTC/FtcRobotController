package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuck.OpMode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuck.subsystems.WDChassis;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuck.subsystems.WDIndexer;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuck.subsystems.WDIntake;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuck.subsystems.WDOuttake;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuck.subsystems.WDTurret;

@Disabled
@TeleOp(name = "WhiteDuck Completo", group = "WhiteDuck")
public class TheWhiteDuck extends OpMode {
    WDChassis chassis = new WDChassis();
    WDIntake intake = new WDIntake();
    WDIndexer indexer = new WDIndexer();
    WDOuttake outtake = new WDOuttake();
    WDTurret turret = new WDTurret();

    boolean driveMode, isParking;

    // Máquina de estados sem o IDLE (robot opera de forma contínua focada em coletar e atirar)
    public enum RobotState {
        INTAKING,
        HOLDING,
        SHOOTING_SPINUP,
        SHOOTING_FIRE
    }

    private RobotState currentState = RobotState.INTAKING;
    private int pollenCount = 0;
    private boolean previousSensorState = false;
    private ElapsedTime shootTimer = new ElapsedTime();
    private int pipeline;

    @Override
    public void init() {
        chassis.init(hardwareMap);
        intake.init(hardwareMap);
        indexer.init(hardwareMap);
        outtake.init(hardwareMap);

        chassis.resetYaw();
        intake.upFlowerRamp();
    }

    @Override
    public void init_loop(){
        if(gamepad2.dpadUpWasPressed()){
            telemetry.addLine("> RED");
            telemetry.addLine("BLUE");
            pipeline = 0;
            turret.init(hardwareMap, pipeline);
        }else if(gamepad2.dpadDownWasPressed()){
            telemetry.addLine("RED");
            telemetry.addLine("> BLUE");
            pipeline = 1;
            turret.init(hardwareMap, pipeline);
        }

    }

    @Override
    public void loop() {
        // ========== CONTROLE DO CHASSIS & PARKING ==========
        chassis.follower.update();

        // Se apertar o botão 'Back', alterna o estado de parking
        if (gamepad1.backWasPressed()) {
            isParking = !isParking;

            if (isParking) {
                // INICIA O PARK: Manda o robô seguir para o destino
                chassis.follower.follow(chassis.parkPosTeleOp());
            } else {
                // BOTÃO DE PÂNICO / CANCELAR: Para o robô na hora e devolve o controle
                chassis.follower.stop();
            }
        }

        // Se NÃO estiver estacionando, o piloto tem o controle manual total do robô
        if (!isParking) {
            // --- CONTROLE MANUAL NORMAL DO TELEOP ---
            double drive = -gamepad1.left_stick_y;
            double strafe = gamepad1.left_stick_x;
            double turn = gamepad1.right_stick_x;

            // Reset angle
            if(gamepad1.leftStickButtonWasPressed()){
                chassis.resetYaw();
            }

            // Drive mode
            if(gamepad1.rightStickButtonWasPressed()) {
                driveMode = !driveMode;
            }

            if(driveMode){
                chassis.driveFieldRelative(drive, strafe, turn);
            }else{
                chassis.drive(drive, strafe, turn);
            }
        }

        // ========== 2.1. CONTROLE MANUAL DA RAMPA DA FLOWER ==========
        if (gamepad2.dpadUpWasPressed()) {
            intake.upFlowerRamp();
        } else if (gamepad2.dpadDownWasPressed()) {
            intake.downFlowerRamp();
        }

        // ========== 2.2. CONTROLE MANUAL DO INTAKE E INDEXER ==========
        intake.setPower(gamepad2.left_stick_y);

        indexer.setPower(gamepad2.right_stick_y);

        // ========== 3. MÁQUINA DE ESTADOS (COLETA E DISPARO AUTOMATIZADOS) ==========
        // A turret faz a mira automática e escape inteligente de limite via Limelight
        turret.autoAim();

        switch (currentState) {
            case INTAKING:
                indexer.closeServo();
                outtake.stop();
                intake.feed();      // Usa o método padrão do intake
                indexer.feed();     // Puxa os pollens para dentro

                // Detecta a contagem de pollens pelo sensor do indexer (< 3cm conforme especificações)
                boolean currentSensorState = indexer.hasPollen();

                if (currentSensorState && !previousSensorState) {
                    pollenCount++;
                }
                previousSensorState = currentSensorState;

                // Se atingiu a capacidade máxima, vai para o estado de espera segurando a peça
                if (pollenCount >= 4) {
                    currentState = RobotState.HOLDING;
                }

                // Se o piloto apertar 'B' a qualquer momento, vai para o spinup do tiro
                if (gamepad2.bWasPressed()) {
                    currentState = RobotState.SHOOTING_SPINUP;
                }
                break;

            case HOLDING:
                indexer.stop();
                intake.reverse(); // Segura os pollens sob leve pressão contrária

                // Aperta 'B' para iniciar a sequência de tiro
                if (gamepad2.bWasPressed()) {
                    currentState = RobotState.SHOOTING_SPINUP;
                }
                break;

            case SHOOTING_SPINUP:
                intake.reverse();
                indexer.stop();

                // O outtake calcula a velocidade dinamicamente com base no 'ty' da Limelight
                double currentTy = turret.getTy();
                outtake.setVelocityFromLimelight(currentTy);

                // Quando a roda do outtake atingir a velocidade ideal calculada, dispara
                if (Math.abs(outtake.getVelocity() - outtake.getTargetVelocity()) < 10.0) {
                    shootTimer.reset();
                    currentState = RobotState.SHOOTING_FIRE;
                }
                break;

            case SHOOTING_FIRE:
                intake.reverse();
                indexer.openServo(); // Abre o portão do pollen
                indexer.feed();      // Joga para fora

                // Mantém o outtake rodando durante o despejo
                outtake.setVelocityFromLimelight(turret.getTy());

                // Tempo de duração do disparo (3 segundos para esvaziar)
                if (shootTimer.seconds() > 3.0) {
                    pollenCount = 0;              // Reseta a contagem
                    indexer.closeServo();         // Fecha o portão novamente
                    currentState = RobotState.INTAKING; // Retorna direto para a coleta
                }
                break;
        }


        // ========== 4. TELEMETRIA POR SUBSISTEMAS ==========
        telemetry.addData("--- MÁQUINA DE ESTADOS ---", "");
        telemetry.addData("Estado do Robô", currentState);
        telemetry.addData("Contagem de Pollens", pollenCount);

        telemetry.addData("--- CHASSIS ---", "");
        telemetry.addData("Drive Mode", driveMode ? "Field Oriented" : "Robot Oriented");
        telemetry.addData("Yaw (Angulo)", chassis.getImuYaw());

        telemetry.addData("--- INTAKE & INDEXER ---", "");
        telemetry.addData("Distância Sensor da Esquerda (CM)", indexer.getDistanceLeftCM());
        telemetry.addData("Distância Sensor da Direita (CM)", indexer.getDistanceRightCM());
        telemetry.addData("Portão Indexer", indexer.isClosed() ? "Fechado" : "Aberto");

        telemetry.addData("--- OUTTAKE & TURRET ---", "");
        telemetry.addData("Limelight Tx", turret.getTx());
        telemetry.addData("Limelight Ty", turret.getTy());
        telemetry.addData("Outtake Velocity", outtake.getVelocity());
        telemetry.addData("Battery Voltage", outtake.getBatteryVoltage());

        telemetry.update();
    }
}