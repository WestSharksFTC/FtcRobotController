package org.firstinspires.ftc.teamcode.Decode.Tsunami;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

@Autonomous
public class TsunamiAutonomous extends OpMode {

    TsunamiChassis drive = new TsunamiChassis();

    @Override
    public void init() {
        drive.init(hardwareMap);
    }

    @Override
    public void loop() {
        drive.goToPoint(70, 10, 0);


        // Telemetrias
        telemetry.addLine("DADOS DA ODOMETRIA DO ROBÔ");
        telemetry.addData("Odometria X", drive.getOdometryX());
        telemetry.addData("Odometria y", drive.getOdometryY());
        telemetry.addData("Odometria Ângulo", drive.getOdometryAngle());
        telemetry.addLine();
    }
}
