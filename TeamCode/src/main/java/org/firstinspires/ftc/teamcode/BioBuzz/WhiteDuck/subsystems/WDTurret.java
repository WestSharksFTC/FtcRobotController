package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuck.subsystems;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class WDTurret {
    public DcMotorEx turretMotor;
    public Limelight3A limelight;

    // Cálculo exato considerando a redução interna (537.7) e externa (99/20)
    private final double TICKS_PER_DEGREE = (537.7 * (104.0 / 25.0)) / 360.0; // ~7.3934 ticks por grau
    private final double MAX_DEGREES = 180.0;
    private final int MAX_TICKS = (int) (MAX_DEGREES * TICKS_PER_DEGREE);

    // Flag para saber se a torre entrou em modo de fuga após bater no limite
    private boolean isEscapingLimit = false;
    private int escapeDirection = 0; // -1 para esquerda, 1 para direita

    public void init(HardwareMap hardwareMap, int pipeline) {
        turretMotor = hardwareMap.get(DcMotorEx.class, "turret");

        turretMotor.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        turretMotor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

        turretMotor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        turretMotor.setDirection(DcMotorEx.Direction.REVERSE);

        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.pipelineSwitch(pipeline); // 0 - VERMELHO e 1 - AZUL
        limelight.start();
    }

    // Controle Manual: Joystick com travamento rígido nos limites
    public void setPower(double power) {
        int currentPosition = turretMotor.getCurrentPosition();

        if (currentPosition >= MAX_TICKS && power > 0) {
            turretMotor.setPower(0.0);
        } else if (currentPosition <= -MAX_TICKS && power < 0) {
            turretMotor.setPower(0.0);
        } else {
            turretMotor.setPower(power);
        }
    }

    // Control da limelight para voltar para o centro
    public void recentralize() {
        int currentPosition = turretMotor.getCurrentPosition();

        if (currentPosition > 10){
            turretMotor.setPower(-0.2);
        }else if(currentPosition < -10){
            turretMotor.setPower(0.2);
        }else{
            turretMotor.setPower(0.0);
        }
    }

    // Lógica de Mira Automática com Fuga Inteligente ao Atingir os Limites
    public void autoAim() {
        LLResult result = limelight.getLatestResult();
        int currentPosition = turretMotor.getCurrentPosition();

        // 1. SE A TORRE JÁ ESTÁ NO MODO DE FUGA
        if (isEscapingLimit) {
            // Continua girando rápido no sentido oposto para descolar do batente
            turretMotor.setPower(escapeDirection * 0.8);

            // Condição para encerrar a fuga: assim que a Limelight enxergar um resultado válido
            // E a torre já tiver recuado um pouco para fora do limite crítico, voltamos ao normal.
            boolean safeFromRightLimit = (escapeDirection < 0 && currentPosition < MAX_TICKS);
            boolean safeFromLeftLimit  = (escapeDirection > 0 && currentPosition > -MAX_TICKS);

            if (result != null && result.isValid() && (safeFromRightLimit || safeFromLeftLimit)) {
                isEscapingLimit = false; // Desativa a fuga e retoma o tracking normal
            }
            return;
        }

        // 2. OPERAÇÃO NORMAL (SE NÃO ESTIVER FUGINDO)
        if (result != null && result.isValid()) {
            // Verifica se bateu no limite ENQUANTO tentava mirar
            if (currentPosition >= MAX_TICKS) {
                isEscapingLimit = true;
                escapeDirection = -1; // Bateu na direita -> foge forte para a esquerda (-0.8)
                turretMotor.setPower(escapeDirection * 0.8);
                return;
            }
            else if (currentPosition <= -MAX_TICKS) {
                isEscapingLimit = true;
                escapeDirection = 1;  // Bateu na esquerda -> foge forte para a direita (0.8)
                turretMotor.setPower(escapeDirection * 0.8);
                return;
            }

            // Tracking normal proporcional se estiver tudo livre dentro dos 180°
            double tx = result.getTx();
            double kP = 0.03;
            double power = tx * kP;

            // Limite de segurança na potência de tracking
            if (power > 1.0) power = 1.0;
            if (power < -1.0) power = -1.0;

            turretMotor.setPower(power);
        } else {
            // Se perdeu o alvo completamente, fica parada
            turretMotor.setPower(0.0);
        }
    }

    // Retorna o tx (ângulo horizontal do alvo) se a leitura for válida, ou 0.0 caso contrário
    public double getTx() {
        LLResult result = limelight.getLatestResult();
        if (result != null && result.isValid()) {
            return result.getTx();
        }
        return 0.0;
    }

    // Retorna o ty (ângulo vertical do alvo) se a leitura for válida, ou 0.0 caso contrário
    public double getTy() {
        LLResult result = limelight.getLatestResult();
        if (result != null && result.isValid()) {
            return result.getTy();
        }
        return 0.0;
    }

    // Retorn a posição da turret
    public double getTurretPosition(){
        return turretMotor.getCurrentPosition();
    }
}