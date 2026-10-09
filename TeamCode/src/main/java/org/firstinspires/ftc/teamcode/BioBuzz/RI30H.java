package org.firstinspires.ftc.teamcode.BioBuzz;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "RI30H - Consistente", group = "BioBuzz")
public class RI30H extends OpMode {

    // Estados da Sequência de Disparo
    private enum ShooterState {
        IDLE,           // Flywheel desligada / Operação manual normal
        SPINNING_UP,    // Acelerando a Flywheel
        SHOOTING        // Disparando (Intake + Servo temporizado)
    }

    private ShooterState shooterState = ShooterState.IDLE;

    // Componentes de Hardware
    private DcMotor rightFrontMotor, leftFrontMotor, rightBackMotor, leftBackMotor;
    private DcMotor intake;
    private DcMotorEx outtake;
    private CRServo servoIntake;

    // Variáveis do Gamepad / Drivetrain
    private boolean v = false;
    private boolean in = false;
    private boolean servo_in = false;
    private double speed = 1.0;
    private int gear = 1;

    // Constantes de Calibração do Outtake
    private static final double MOTOR_TICKS_PER_REV = 28.0;
    private static final double TARGET_SHOOT_RPM = 3750.0;
    private static final double RPM_TOLERANCE = 100.0;

    // Constantes do Controle Proporcional (P)
    private static final double KP = 0.0008;
    private static final double BASE_POWER = 0.82;

    // Configuração do Disparo Temporizado
    private static final double SHOOT_DURATION_SEC = 2.0;
    private static final double REDUCED_INTAKE_POWER = 0.4;
    private final ElapsedTime shootTimer = new ElapsedTime();

    // Filtro de Estabilização (Histerese)
    private int stableCyclesCount = 0;
    private static final int REQUIRED_STABLE_CYCLES = 3;

    @Override
    public void init() {
        rightFrontMotor = hardwareMap.get(DcMotor.class, "rf");
        rightBackMotor  = hardwareMap.get(DcMotor.class, "rb");
        leftFrontMotor  = hardwareMap.get(DcMotor.class, "lf");
        leftBackMotor   = hardwareMap.get(DcMotor.class, "lb");
        intake          = hardwareMap.get(DcMotor.class, "in");
        outtake         = hardwareMap.get(DcMotorEx.class, "out");
        servoIntake     = hardwareMap.get(CRServo.class, "servo_intake");

        rightFrontMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightBackMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftFrontMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftBackMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        outtake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        rightFrontMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBackMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftFrontMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBackMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        outtake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        rightFrontMotor.setDirection(DcMotor.Direction.REVERSE);
        rightBackMotor.setDirection(DcMotor.Direction.FORWARD);
        leftFrontMotor.setDirection(DcMotor.Direction.REVERSE);
        leftBackMotor.setDirection(DcMotor.Direction.REVERSE);
        intake.setDirection(DcMotor.Direction.FORWARD);
        outtake.setDirection(DcMotorSimple.Direction.REVERSE);
        servoIntake.setDirection(CRServo.Direction.FORWARD);

        telemetry.addData("status", "INITIALIZED");
    }

    @Override
    public void loop() {
        // -------------------------------------------------------------
        // 1. INPUTS DO PILOTO (GAMEPAD)
        // -------------------------------------------------------------
        if (gamepad1.aWasPressed()) {
            in = !in;
        }

        if (gamepad1.bWasPressed()) {
            v = !v;
            speed = v ? 0.5 : 1.0;
        }

        if (gamepad1.rightBumperWasPressed() && gear < 5) gear++;
        if (gamepad1.leftBumperWasPressed() && gear > 1) gear--;

        if (gamepad1.dpadRightWasPressed()) {
            servo_in = !servo_in;
        }

        // Toggle do Outtake -> Alterna a Máquina de Estados
        if (gamepad1.dpadUpWasPressed()) {
            if (shooterState == ShooterState.IDLE) {
                shooterState = ShooterState.SPINNING_UP;
                stableCyclesCount = 0;
            } else {
                shooterState = ShooterState.IDLE;
            }
        }

        // -------------------------------------------------------------
        // 2. MÁQUINA DE ESTADOS DO SHOOTER & ALIMENTAÇÃO
        // -------------------------------------------------------------
        double currentVelocityTicks = outtake.getVelocity();
        double currentRPM = (currentVelocityTicks / MOTOR_TICKS_PER_REV) * 60.0;
        double errorRPM = TARGET_SHOOT_RPM - currentRPM;

        double calculatedOuttakePower = 0.0;
        double targetIntakePower = in ? 0.8 : 0.0;
        double targetServoPower = servo_in ? 1.0 : 0.0;

        switch (shooterState) {
            case IDLE:
                calculatedOuttakePower = 0.0;
                break;

            case SPINNING_UP:
                // Cálculo de Potência Proporcional
                calculatedOuttakePower = (currentRPM < TARGET_SHOOT_RPM - 500)
                        ? 1.0
                        : Math.max(0.0, Math.min(1.0, BASE_POWER + (errorRPM * KP)));

                // Valida se a velocidade se manteve estável por ciclos suficientes
                if (Math.abs(errorRPM) <= RPM_TOLERANCE) {
                    stableCyclesCount++;
                } else {
                    stableCyclesCount = 0;
                }

                // Transição de estado ao confirmar estabilização
                if (stableCyclesCount >= REQUIRED_STABLE_CYCLES) {
                    shooterState = ShooterState.SHOOTING;
                    shootTimer.reset();
                }
                break;

            case SHOOTING:
                // Mantém o motor sustentado na velocidade ideal
                calculatedOuttakePower = Math.max(0.0, Math.min(1.0, BASE_POWER + (errorRPM * KP)));

                // Alimenta o robô com as velocidades programadas
                targetServoPower = 1.0;
                targetIntakePower = REDUCED_INTAKE_POWER;

                // Fim do tempo limite do disparo -> Retorna para o repouso
                if (shootTimer.seconds() >= SHOOT_DURATION_SEC) {
                    shooterState = ShooterState.IDLE;
                }
                break;
        }

        // Aplicação direta nas atuadores
        outtake.setPower(calculatedOuttakePower);
        intake.setPower(targetIntakePower);
        servoIntake.setPower(targetServoPower);

        // -------------------------------------------------------------
        // 3. CONTROLE DA DRIVETRAIN
        // -------------------------------------------------------------
        double velocity = 1.0 - (gamepad1.right_trigger * 0.5);
        double turnStick = gamepad1.right_stick_x * speed * (gear * 0.2);
        double drive     = gamepad1.left_stick_y * velocity * (gear * 0.2);
        double strafe    = gamepad1.left_stick_x * velocity * (gear * 0.2);

        rightFrontMotor.setPower(drive + turnStick + strafe);
        rightBackMotor.setPower(drive + turnStick - strafe);
        leftFrontMotor.setPower(drive - turnStick - strafe);
        leftBackMotor.setPower(drive - turnStick + strafe);

        // -------------------------------------------------------------
        // 4. TELEMETRIA COMPLETA PARA DIAGNÓSTICO E COMPETIÇÃO
        // -------------------------------------------------------------

        // STATUS DA MÁQUINA DE ESTADOS DO SHOOTER
        telemetry.addData("=== SHOOTER ===", "");
        telemetry.addData("Estado", shooterState);
        telemetry.addData("RPM (Atual / Alvo)", "%.0f / %.0f RPM", currentRPM, TARGET_SHOOT_RPM);
        telemetry.addData("Erro de Rotação", "%.0f RPM", errorRPM);
        telemetry.addData("Potência Outtake", "%.2f (%.0f%%)", calculatedOuttakePower, calculatedOuttakePower * 100);

        // MONITORAMENTO DA ESTABILIDADE E TEMPORIZADOR
        if (shooterState == ShooterState.SPINNING_UP) {
            telemetry.addData("Estabilização", "Confirmando... (%d/%d ciclos)", stableCyclesCount, REQUIRED_STABLE_CYCLES);
        } else if (shooterState == ShooterState.SHOOTING) {
            double tempoRestante = Math.max(0, SHOOT_DURATION_SEC - shootTimer.seconds());
            telemetry.addData("Tempo Restante Rajada", "%.1f s / %.1f s", tempoRestante, SHOOT_DURATION_SEC);
        } else {
            telemetry.addData("Disparo Automático", "PRONTO / AGUARDANDO");
        }

        // STATUS DOS SUB-SISTEMAS (INTAKE E SERVO)
        telemetry.addLine();
        telemetry.addData("=== MECANISMOS ===", "");
        telemetry.addData("Intake (Motor)", (targetIntakePower > 0) ? "LIGADO (%.0f%%)" : "DESLIGADO", targetIntakePower * 100);
        telemetry.addData("Servo Intake", (targetServoPower > 0) ? "LIGADO" : "DESLIGADO");

        // STATUS DA DRIVETRAIN E GAMEPAD
        telemetry.addLine();
        telemetry.addData("=== DRIVETRAIN ===", "");
        telemetry.addData("Marcha Atual", "%d / 5", gear);
        telemetry.addData("Freio de Curva (B)", v ? "ATIVO (50%)" : "INATIVO (100%)");
        telemetry.addData("Freio de Tração (RT)", "%.0f %%", (-100 * (1 - (gamepad1.right_trigger * 0.5)) + 100));

        telemetry.update();
    }
}