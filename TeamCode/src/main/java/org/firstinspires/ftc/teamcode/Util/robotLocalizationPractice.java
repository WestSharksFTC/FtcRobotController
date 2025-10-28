package org.firstinspires.ftc.teamcode.Util;

public class robotLocalizationPractice {

    double angle;
    double x;
    double y;

    // Constructor method
    public robotLocalizationPractice (double angle){
        this.angle = angle;
    }

    public double getHeading(){
        // This method normalize robot heading between -180 and 180
        // This is useful for calculate turn angles, especially when crossing the 0,360 boundary

        double angle = this.angle; // Copy the angle of IMU

        while(angle > 180){
            angle -= 360; // Subtract until in target range
        }
        while(angle <= -180){
            angle += 360; // Add until in target range
        }
        return angle; // Return normalized value
    }

    public void turnRobot(double angleChange){
        angle += angleChange; // Or this.angle... it's the same thing
    }

    public void setAngle(double angle){
        this.angle = angle;
    }

    public double getAngle(){
        return this.angle;
    }

    public void changeX(double changeAmount){
        x += changeAmount;
    }

    public void setX(double x){
        this.x = x;
    }

    public double getX(){
        return this.x;
    }

    public void changeY(double changeAmount){
        y += changeAmount;
    }

    public void setY(double y){
        this.y = y;
    }

    public double getY(){
        return this.y;
    }
}
