package org.firstinspires.ftc.teamcode.Decode.RI30H;

import static android.os.SystemClock.sleep;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous
public class RI30HAutonomous extends OpMode {
    //instanciando as classes
    RI30HChassisAUTO chassis;

    @Override
    public void init(){
        chassis = new RI30HChassisAUTO(hardwareMap);
    }

    @Override
    public void start(){
        telemetry.addLine("distanciaFL: " + (chassis.motorFL.getCurrentPosition() / 16.4));
        telemetry.addLine("distanciaFR: " + (chassis.motorFR.getCurrentPosition() / 16.4));
        telemetry.addLine("distanciaBL: " + (chassis.motorBL.getCurrentPosition() / 16.4));
        telemetry.addLine("distanciaBR: " + (chassis.motorBR.getCurrentPosition() / 16.4));

        chassis.andar(100);
        sleep(5000);
        chassis.andar(-100);
        sleep(5000);
        chassis.andar(350);

        telemetry.update();
    }

    @Override
    public void loop(){

    }
}