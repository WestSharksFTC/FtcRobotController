package org.firstinspires.ftc.teamcode.BioBuzz.Tornado.subsystems.chassis;

import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.seattlesolvers.solverslib.command.SubsystemBase;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.BioBuzz.Tornado.pedroPathing.TornadoConstants;

/** Antigo WDChassis. Mesma logica; agora e um SubsystemBase e usa ChassisConstants. */
public class Chassis extends SubsystemBase {
    private final DcMotor lf, lb, rf, rb;
    private final IMU imu;
    public final Follower follower;
    private double yawRad = 0;

    public Chassis(HardwareMap hw) {
        follower = TornadoConstants.create(hw);

        lf = hw.get(DcMotor.class, ChassisConstants.LF);
        lb = hw.get(DcMotor.class, ChassisConstants.LB);
        rf = hw.get(DcMotor.class, ChassisConstants.RF);
        rb = hw.get(DcMotor.class, ChassisConstants.RB);

        lf.setDirection(DcMotor.Direction.REVERSE);
        lb.setDirection(DcMotor.Direction.FORWARD);
        rf.setDirection(DcMotor.Direction.FORWARD);
        rb.setDirection(DcMotor.Direction.REVERSE);

        for (DcMotor m : new DcMotor[]{lf, lb, rf, rb}) {
            m.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            m.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }

        imu = hw.get(IMU.class, ChassisConstants.IMU);
        imu.initialize(new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.LEFT,
                RevHubOrientationOnRobot.UsbFacingDirection.UP)));
    }

    @Override
    public void periodic() {
        // le o IMU 1x por ciclo (leitura de hub e cara)
        yawRad = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
    }

    public void drive(double forward, double strafe, double rotate) {
        double flp = forward - strafe - rotate;
        double blp = forward + strafe - rotate;
        double frp = forward + strafe + rotate;
        double brp = forward - strafe + rotate;
        double max = Math.max(1.0, Math.max(Math.max(Math.abs(flp), Math.abs(blp)),
                Math.max(Math.abs(frp), Math.abs(brp))));
        double s = ChassisConstants.MAX_SPEED;
        lf.setPower(flp / max * s);
        lb.setPower(blp / max * s);
        rf.setPower(frp / max * s);
        rb.setPower(brp / max * s);
    }

    public void driveFieldRelative(double forward, double strafe, double rotate) {
        double theta = Math.atan2(strafe, forward);
        double r = Math.hypot(strafe, forward);
        theta = AngleUnit.normalizeRadians(theta - yawRad);
        drive(r * Math.cos(theta), r * Math.sin(theta), rotate);
    }

    public double getYawDeg() {
        return Math.toDegrees(yawRad);
    }

    public void resetYaw() {
        imu.resetYaw();
        yawRad = 0;
    }

    public Path parkPath() {
        PoseFactory pf = PoseFactory.degrees();
        Pose park = pf.of(ChassisConstants.PARK_X, ChassisConstants.PARK_Y, ChassisConstants.PARK_HEADING_DEG);
        Pose current = follower.pose();
        return Paths.line(current, park).linear(current, park);
    }
}
