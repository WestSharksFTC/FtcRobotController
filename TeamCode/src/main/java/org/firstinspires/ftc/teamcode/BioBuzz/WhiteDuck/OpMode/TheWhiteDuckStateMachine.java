package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuck.OpMode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuck.subsystems.WDChassis;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuck.subsystems.WDIndexer;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuck.subsystems.WDIntake;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuck.subsystems.WDOuttake;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuck.subsystems.WDTurret;

@TeleOp(name = "The WhiteDuck Completo", group = "WhiteDuck")
public class TheWhiteDuckStateMachine extends OpMode {
    WDChassis chassis = new WDChassis();
    WDIntake intake = new WDIntake();
    WDIndexer indexer = new WDIndexer();
    WDOuttake outtake = new WDOuttake();
    WDTurret turret = new WDTurret();

    boolean isParking, isAbleToShoot;
    boolean statusResetTimer = true;
    boolean isIndexerActive = true;

    // Máquina de estados sem o IDLE (robot opera de forma contínua focada em coletar e atirar)
    public enum RobotState {
        INTAKING,
        SHOOTING_SPINUP,
        SHOOTING_FIRE
    }

    private RobotState currentState = RobotState.INTAKING;
    private ElapsedTime shootTimer = new ElapsedTime();
    private ElapsedTime isAbleToShootTimer = new ElapsedTime();
    private int pipeline;

    @Override
    public void init() {
        chassis.init(hardwareMap);
        intake.init(hardwareMap);
        indexer.init(hardwareMap);
        outtake.init(hardwareMap);

        telemetry.addLine("RED");
        telemetry.addLine("BLUE");

        chassis.resetYaw();
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

            chassis.drive(drive, strafe, turn);
        }

        // ========== 3. MÁQUINA DE ESTADOS (COLETA E DISPARO AUTOMATIZADOS) ==========
        switch (currentState) {
            case INTAKING:
                indexer.closeServo();
                outtake.stop();
                intake.feed();      // Usa o método padrão do intake
                turret.recentralize();

                if(gamepad2.yWasPressed()){
                    isIndexerActive = !isIndexerActive;
                }

                if(isIndexerActive){
                    indexer.setPower(1.0);     // Puxa os pollens para dentro
                }else{
                    indexer.stop();
                }

                // Se o piloto apertar 'B' a qualquer momento, vai para o spinup do tiro
                if (gamepad2.bWasPressed()) {
                    currentState = RobotState.SHOOTING_SPINUP;
                }
                break;

            case SHOOTING_SPINUP:
                indexer.closeServo();
                intake.stop();
                indexer.stop();
                turret.autoAim();

                // O outtake calcula a velocidade dinamicamente com base no 'ty' da Limelight
                double currentTy = turret.getTy();
                outtake.setVelocityFromLimelight(currentTy);

                isAbleToShoot = Math.abs(outtake.getVelocity() - outtake.getTargetVelocity()) < 10.0;
                if(statusResetTimer && isAbleToShoot){
                    isAbleToShootTimer.reset();
                    statusResetTimer = false;
                }

                // Quando a roda do outtake atingir a velocidade ideal calculada, dispara
                if (isAbleToShoot && isAbleToShootTimer.milliseconds() > 1000) {
                    shootTimer.reset();
                    currentState = RobotState.SHOOTING_FIRE;
                }
                break;

            case SHOOTING_FIRE:
                intake.stop();
                indexer.openServo(); // Abre o portão do pollen
                indexer.feed();      // Joga para fora
                turret.autoAim();

                // Mantém o outtake rodando durante o despejo
                outtake.setVelocityFromLimelight(turret.getTy());

                // Tempo de duração do disparo (3 segundos para esvaziar)
                if (shootTimer.seconds() > 3.0) {
                    indexer.closeServo();         // Fecha o portão novamente
                    isIndexerActive = true;
                    statusResetTimer = true;
                    currentState = RobotState.INTAKING; // Retorna direto para a coleta
                }
                break;
        }


        // ========== 4. TELEMETRIA POR SUBSISTEMAS ==========
        telemetry.addLine("========== MÁQUINA DE ESTADOS ==========");
        telemetry.addData("Estado do Robô", currentState);

        telemetry.addLine("========== GATE ==========");
        telemetry.addData("Portão Indexer", indexer.isClosed() ? "Fechado" : "Aberto");

        telemetry.addLine("========== OUTTAKE & TURRET ==========");
        telemetry.addData("Pipeline da Limelight", pipeline == 0 ? "VERMELHO" : "AZUL");
        telemetry.addData("Outtake Velocity", outtake.getVelocity());
        telemetry.addData("Turret Position", turret.getTurretPosition());
        telemetry.addData("Battery Voltage", outtake.getBatteryVoltage());

        telemetry.update();
    }
}