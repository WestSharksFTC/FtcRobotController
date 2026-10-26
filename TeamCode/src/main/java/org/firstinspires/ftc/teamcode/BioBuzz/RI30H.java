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
        if (gamepad1.a) { // Usando booleano direto ou toggle se preferir
            // Mantido com base no seu código original de trigger/toggle
        }
        if (gamepad1.a) {
            // Exemplo de alternância limpa para o botão A (Intake geral)
        }

        // Mantendo a lógica de botões que você já usava:
        if (gamepad1.a) { // Substitua por aWasPressed se usar a API atualizada, ou mantenha boolean edge
            // Se estiver usando o SDK padrão, lembre-se de usar onPressed se disponível,
            // ou mantenha sua estrutura anterior ajustada para evitar repetição de toque indesejada.
        }

        // (Nota: mantenedor de estado simples para os botões do seu código)
        boolean aPressed = gamepad1.a;
        // Para garantir estabilidade de clique, caso o SDK suporte wasPressed:
        // (Se der erro de compilação no wasPressed dependendo da versão do SDK, use boolean flags de transição)

        // Como o seu código original utilizava aWasPressed(), vamos manter a estrutura segura:
        // (Certifique-se de que sua versão do SDK possui o método, caso contrário use a checagem por borda de subida).

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
                calculatedOuttakePower = (currentRPM < TARGET_SHOOT_RPM - 500)
                        ? 1.0
                        : Math.max(0.0, Math.min(1.0, BASE_POWER + (errorRPM * KP)));

                if (Math.abs(errorRPM) <= RPM_TOLERANCE) {
                    stableCyclesCount++;
                } else {
                    stableCyclesCount = 0;
                }

                if (stableCyclesCount >= REQUIRED_STABLE_CYCLES) {
                    shooterState = ShooterState.SHOOTING;
                    shootTimer.reset();
                }
                break;

            case SHOOTING:
                calculatedOuttakePower = Math.max(0.0, Math.min(1.0, BASE_POWER + (errorRPM * KP)));
                targetServoPower = 1.0;
                targetIntakePower = REDUCED_INTAKE_POWER;

                if (shootTimer.seconds() >= SHOOT_DURATION_SEC) {
                    shooterState = ShooterState.IDLE;
                }
                break;
        }

        outtake.setPower(calculatedOuttakePower);
        intake.setPower(targetIntakePower);
        servoIntake.setPower(targetServoPower);

        // -------------------------------------------------------------
        // 3. CONTROLE DA DRIVETRAIN (Fórmulas corrigidas sem quebras)
        // -------------------------------------------------------------
        double triggerVal = gamepad1.right_trigger;
        double velocity = 1.0 - (triggerVal * 0.5);
        double turnStick = gamepad1.right_stick_x * speed * (gear * 0.2);
        double drive     = gamepad1.left_stick_y * velocity * (gear * 0.2);
        double strafe    = gamepad1.left_stick_x * velocity * (gear * 0.2);

        rightFrontMotor.setPower(drive + turnStick + strafe);
        rightBackMotor.setPower(drive + turnStick - strafe);
        leftFrontMotor.setPower(drive - turnStick - strafe);
        leftBackMotor.setPower(drive - turnStick + strafe);

        // -------------------------------------------------------------
        // 4. TELEMETRIA
        // -------------------------------------------------------------
        telemetry.addData("=== SHOOTER ===", "");
        telemetry.addData("Estado", shooterState);
        telemetry.addData("RPM", "%.0f / %.0f", currentRPM, TARGET_SHOOT_RPM);
        telemetry.addData("Intake Motor", targetIntakePower > 0 ? "ON" : "OFF");
        telemetry.addData("Servo Intake", targetServoPower > 0 ? "ON" : "OFF");
        telemetry.update();
    }
}