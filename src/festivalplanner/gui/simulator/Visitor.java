package festivalplanner.gui.simulator;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;


/**
 * Created by Gebruiker on 6-3-2017.
 */
public class Visitor  {
private double speed;
private int size;
//private Color look;
private double angle;
private Point2D position;
private double xDestination;
private double yDestination;
private Point2D newPosition;


    public double getxDestination() {
        return xDestination;
    }

    public void setxDestination(double xDestination) {
        this.xDestination = xDestination;
    }

    public double getyDestination() {
        return yDestination;
    }

    public void setyDestination(double yDestination) {
        this.yDestination = yDestination;
    }

    public Visitor(double speed, Point2D position) {
        this.speed = speed;
        size = 10;
        //this.look = look;
        this.angle = 0.0;
        this.position = position;
        this.yDestination = 0;
        this.xDestination = 0;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    //public Color getLook() {
    //    return look;
//
   // public void setLook(Color look) {
     //   this.look = look;
    //}

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }



    public void draw(Graphics2D g)
    {
        g.setColor(Color.RED);
        Ellipse2D.Double circle = new Ellipse2D.Double(position.getX(),position.getY(),size,size);
        g.fill(circle);




    }

    public Point2D getPosition() {
        return position;
    }

    public void setPosition(Point2D position) {
        this.position = position;
    }

    public void update()
    {
        double dx = xDestination - position.getX();
        double dy = yDestination - position.getY();

        double newAngle = Math.atan2(dy, dx);

        double difference = newAngle - angle;
        while(difference < -Math.PI)
            difference += 2 * Math.PI;
        while(difference > Math.PI)
            difference -= 2 * Math.PI;

        if(difference > 0.1)
            angle += 0.1;
        else if(difference < -0.1)
            angle -= 0.1;
        else
            angle = newAngle;

        newPosition = new Point2D.Double(
                position.getX() + speed * Math.cos(angle),
                position.getY() + speed * Math.sin(angle));
        //position = newPosition;




    }

    public boolean checkcollision(ArrayList<Visitor> visitors)
    {
        boolean collision = false;

        for(Visitor v : visitors )
        {


            if(v.position.distance(newPosition) < size && !v.equals(this)) {
                collision = true;
                break;
            }

        }

        if(!collision)
        {
        position = newPosition;
        }else angle += 0.2;


    return collision;
    }


}
