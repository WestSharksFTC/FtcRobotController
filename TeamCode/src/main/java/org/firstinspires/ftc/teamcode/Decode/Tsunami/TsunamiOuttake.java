package org.firstinspires.ftc.teamcode.Decode.Tsunami;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

public class TsunamiOuttake {
    TsunamiChassis drive = new TsunamiChassis();

    private DcMotor outtakeL;
    private DcMotor outtakeR;
    private Limelight3A limelight;


    public void init(HardwareMap hardwareMap){
        outtakeL = hardwareMap.get(DcMotor.class, "outtake_left");
        outtakeR = hardwareMap.get(DcMotor.class, "outtake_right");

        outtakeL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        outtakeR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        outtakeL.setDirection(DcMotorSimple.Direction.REVERSE);

        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.pipelineSwitch(9); // April tag #24 pipeline (cesta vermelha)
    }

    public void setOuttakePower(double power){
        outtakeL.setPower(power);
        outtakeR.setPower(power);
    }

    public void startLimelight(){
        limelight.start();
    }

    public void runLimelight(Telemetry telemetry){
        YawPitchRollAngles orientation = drive.imu.getRobotYawPitchRollAngles();
        limelight.updateRobotOrientation(orientation.getYaw());
        LLResult llResult = limelight.getLatestResult();
        if(llResult != null && llResult.isValid()){
            Pose3D botPose = llResult.getBotpose_MT2();
            telemetry.addData("Target X", llResult.getTx());
            telemetry.addData("Target Y", llResult.getTy());
            telemetry.addData("Target Area", llResult.getTa());
            telemetry.addLine();
            telemetry.addData("BotPose", botPose.toString());
            telemetry.addData("Yaw", botPose.getOrientation().getYaw());
        }
    }
}
