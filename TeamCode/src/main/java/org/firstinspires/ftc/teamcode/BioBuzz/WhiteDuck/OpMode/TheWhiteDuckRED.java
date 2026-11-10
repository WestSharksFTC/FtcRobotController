package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuck.OpMode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuck.subsystems.WDChassis;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuck.subsystems.WDIndexer;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuck.subsystems.WDIntake;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuck.subsystems.WDOuttake;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuck.subsystems.WDTurret;

@Disabled
@TeleOp(name = "WhiteDuck VERMELHO", group = "WhiteDuck")
public class TheWhiteDuckRED extends OpMode {
    WDChassis chassis = new WDChassis();
    WDIntake intake = new WDIntake();
    WDIndexer indexer = new WDIndexer();
    WDOuttake outtake = new WDOuttake();
    WDTurret turret = new WDTurret();
    double drive, strafe, turn, turretPowerRight, turretPowerLeft;
    boolean driveMode, intakeStatus, indexerStatus, outtakeStatus, turretStatus, isParking;

    @Override
    public void init() {
        chassis.init(hardwareMap);
        intake.init(hardwareMap);
        indexer.init(hardwareMap);
        outtake.init(hardwareMap);
        turret.init(hardwareMap, 0);

        intake.upFlowerRamp();
    }

    @Override
    public void loop() {
        // ========== CONTROLE DO CHASSIS & PARKING ==========
        chassis.follower.update();

        // Se apertar o botão 'A', alterna o estado de parking
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
            drive = -gamepad1.left_stick_y;
            strafe = gamepad1.left_stick_x;
            turn = gamepad1.right_stick_x;

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


        // ========== CONTROLE DO INTAKE ==========
        // Intake
        if(gamepad2.xWasPressed()) {
            intakeStatus = !intakeStatus;
        }

        if(intakeStatus){
            intake.feed();
        }else{
            intake.stop();
        }

        intake.setPower(gamepad2.left_stick_y);

        // Servo da rampa da flower
        if(gamepad2.dpadUpWasPressed()){
            intake.upFlowerRamp();
        }else if(gamepad2.dpadDownWasPressed()){
            intake.downFlowerRamp();
        }


        // ========== CONTROLE DO INDEXER ==========
        // Indexer
        if(gamepad2.yWasPressed()) {
            indexerStatus = !indexerStatus;
        }

        if(indexerStatus){
            indexer.feed();
        }else {
            indexer.stop();
        }

        indexer.setPower(gamepad2.right_stick_y);

        // Servo do indexer
        if(gamepad2.dpadRightWasPressed()){
            indexer.closeServo();
        }else if(gamepad2.dpadLeftWasPressed()){
            indexer.openServo();
        }


        // ========== CONTROLE DO OUTTAKE & TURRET ==========
        // Outtake
        if(gamepad2.bWasPressed()) {
            outtakeStatus = !outtakeStatus;
        }

        if(outtakeStatus){
            // O outtake calcula a velocidade dinamicamente com base no ty da Limelight
            double currentTy = turret.getTy();
            outtake.setVelocityFromLimelight(currentTy);
        }else{
            outtake.stop();
        }

        // Turret
        turretPowerRight = gamepad2.right_trigger;
        turretPowerLeft = -gamepad2.left_trigger;

        if(gamepad2.aWasPressed()) {
            turretStatus = !turretStatus;
        }

        if(turretStatus){
            turret.autoAim();
        }else{
            turret.setPower(turretPowerLeft + turretPowerRight);
        }


        // ========== TELEMETRIA SEPARADA POR SUBSISTEMAS ==========
        telemetry.addLine("========== CHASSIS ==========");
        telemetry.addData("Yaw (Angulo)", chassis.getImuYaw());
        telemetry.addData("Drive Mode", driveMode ? "Field Oriented" : "Robot Oriented");
        telemetry.addData("Parking State", isParking ? "Ativo (Indo para o Park)" : "Desligado");
        telemetry.addLine();

        telemetry.addLine("========== INTAKE ==========");
        telemetry.addData("Intake Status", intakeStatus ? "Ligado" : "Desligado");
        telemetry.addData("Servo Rampa Left", intake.getLeftServoPosition());
        telemetry.addData("Servo Rampa Right", intake.getRightServoPosition());
        telemetry.addLine();

        telemetry.addLine("========== INDEXER ==========");
        telemetry.addData("Indexer Status", indexerStatus ? "Ligado" : "Desligado");
        telemetry.addData("Distância Sensor da Esquerda (CM)", indexer.getDistanceLeftCM());
        telemetry.addData("Distância Sensor da Direita (CM)", indexer.getDistanceRightCM());
        telemetry.addData("Pollen Gate (Servo)", indexer.isClosed() ? "Fechado" : "Aberto");
        telemetry.addLine();

        telemetry.addLine("========== OUTTAKE ==========");
        telemetry.addData("Outtake Status", outtakeStatus ? "Ligado (Auto Velocity)" : "Desligado");
        telemetry.addData("Outtake Velocity (ticks/seg)", outtake.getVelocity());
        telemetry.addData("Battery Voltage (V)", outtake.getBatteryVoltage());
        telemetry.addLine();

        telemetry.addLine("========== TURRET & LIMELIGHT ==========");
        telemetry.addData("Turret Mode", turretStatus ? "Auto-Aim" : "Manual");
        telemetry.addData("Limelight Tx", turret.getTx());
        telemetry.addData("Limelight Ty", turret.getTy());

        telemetry.update();
    }
}