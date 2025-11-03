package org.firstinspires.ftc.teamcode.Util.Mechanism;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

public class robotPinpointPractice {
    private GoBildaPinpointDriver pinpoint;

    public void init(HardwareMap hardwareMap){
        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");

        pinpoint.setOffsets(-85.0, -160.0, DistanceUnit.MM);

        pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_SWINGARM_POD);

        pinpoint.setEncoderDirections(
                GoBildaPinpointDriver.EncoderDirection.FORWARD,
                GoBildaPinpointDriver.EncoderDirection.FORWARD
        );

        pinpoint.resetPosAndIMU();

        pinpoint.setPosition(new Pose2D(DistanceUnit.CM, 0, 0, AngleUnit.DEGREES, 0));
    }

    public void update(){
        pinpoint.update();
    }

    public void setPosition(double x, double y, double heading){
        pinpoint.setPosition(new Pose2D(DistanceUnit.CM, x, y, AngleUnit.DEGREES, heading));
    }

    public void drive(){

    }

    public void strafe(){

    }

    public void rotate(){

    }
}
