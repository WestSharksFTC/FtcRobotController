package org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.commands.chassis;

import com.seattlesolvers.solverslib.command.CommandBase;

import org.firstinspires.ftc.teamcode.BioBuzz.WhiteDuckV4.subsystems.chassis.Chassis;

import java.util.function.DoubleSupplier;

public class DriveCommand extends CommandBase {
    private final Chassis chassis;

    private final DoubleSupplier forward;
    private final DoubleSupplier strafe;
    private final DoubleSupplier turn;

    private boolean fieldCentric = false;
    private boolean holdEnabled = false;

    public DriveCommand (Chassis chassis, DoubleSupplier forward, DoubleSupplier strafe, DoubleSupplier turn){
        this.chassis = chassis;

        this.forward = forward;
        this.strafe = strafe;
        this.turn = turn;

        addRequirements(chassis);
    }

    @Override
    public void execute(){
        double f = forward.getAsDouble();
        double s = strafe.getAsDouble();
        double t = turn.getAsDouble();

        if(fieldCentric){
            if(holdEnabled){
                chassis.driveFieldCentricHold(f, s, t);
            }else{
                chassis.driveFieldCentric(f, s, t);
            }
        }else{
            if(holdEnabled){
                chassis.driveRobotCentricHold(f, s, t);
            }else{
                chassis.driveRobotCentric(f, s, t);
            }
        }
    }

    public void toggleFieldCentric(){
        fieldCentric = !fieldCentric;
    }

    public void toggleHold(){
        holdEnabled = !holdEnabled;
    }

    public boolean isFieldCentric(){
        return fieldCentric;
    }

    public boolean isHoldEnabled(){
        return holdEnabled;
    }

    @Override
    public void end(boolean interrupted){
        chassis.stop();
    }
}
