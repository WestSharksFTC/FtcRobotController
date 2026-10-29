package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuck;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuck.subsystems.WDChassis;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuck.subsystems.WDIndexer;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuck.subsystems.WDIntake;
import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuck.subsystems.WDOuttake;

@TeleOp(name = "WhiteDuck Teste", group = "WhiteDuck")
public class TheWhiteDuckTeste extends OpMode {
    WDChassis chassis = new WDChassis();
    WDIntake intake = new WDIntake();
    WDIndexer indexer = new WDIndexer();
    WDOuttake outtake = new WDOuttake();
    double drive, strafe, turn, intakePower, indexerPower, outtakeVelocity;
    boolean driveMode, intakeStatus, indexerStatus, outtakeStatus;

    @Override
    public void init() {
        chassis.init(hardwareMap);
        intake.init(hardwareMap);
        indexer.init(hardwareMap);
        outtake.init(hardwareMap);

        intake.upFlowerRamp();
    }

    @Override
    public void loop() {
        drive = gamepad1.left_stick_y;
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

        // Intake
        if(gamepad1.xWasPressed()) {
            intakeStatus = !intakeStatus;
        }

        if(intakeStatus){
            intakePower = 0.8;
        }else{
            intakePower = 0.0;
        }

        intake.setPower(intakePower);

        // Servo da rampa da flower
        if(gamepad1.dpadUpWasPressed()){
            intake.upFlowerRamp();
        }else if(gamepad1.dpadDownWasPressed()){
            intake.downFlowerRamp();
        }

        // Indexer
        if(gamepad1.yWasPressed()) {
            indexerStatus = !indexerStatus;
        }

        if(indexerStatus){
            indexerPower = 1.0;
        }else{
            indexerPower = 0.0;
        }

        indexer.setPower(indexerPower);

        // Servo do indexer
        if(gamepad1.rightBumperWasPressed()){
            indexer.closeServo();
        }else if(gamepad1.leftBumperWasPressed()){
            indexer.openServo();
        }

        // Outtake
        if(gamepad1.bWasPressed()) {
            outtakeStatus = !outtakeStatus;
        }

        if(outtakeStatus){
            outtakeVelocity = 760.0;
        }else{
            outtakeVelocity = 0.0;
        }

        outtake.setVelocity(outtakeVelocity);


        telemetry.addData("Angulo", chassis.getImuYaw());
        telemetry.addData("Drive Mode", driveMode ? "FIELD Oriented Drive" : "ROBOT Oriented Drive");
        telemetry.addData("Intake Power", intakePower);
        telemetry.addData("Indexer Power", indexerPower);
        telemetry.addData("Pollen Gate", indexer.isClosed());
        telemetry.addData("Servo Right Position", intake.getRightServoPosition());
        telemetry.addData("Servo Left Position", intake.getLeftServoPosition());
        telemetry.addData("Outtake Velocity", outtake.getVelocity());
    }
}
