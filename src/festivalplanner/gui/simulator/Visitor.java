package festivalplanner.gui.simulator;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;


/**
 * Created by Gebruiker on 6-3-2017.
 */
public class Visitor  {

private double speed;
private double angle;
private Point2D position;
private Point2D destination;
private Point2D newPosition;

private BufferedImage image;
private int radius;



private int timeAtTarget;

private boolean hasToPee;
private int blather;
private int maxBlather;

private boolean isThirsty;

    private boolean remove;

private CurrentAction currentAction;

    public boolean getRemove() {
        return remove;
    }


    public enum CurrentAction {
        IDLE,PEEING,WATCHING,BUYINGDRINKS,
    }

public static java.util.List<BufferedImage> images;
    public static void getImages() {
        images = new ArrayList<>();
        try {

            for (int i = 0; i < 9; i++) {
                BufferedImage image = ImageIO.read(Visitor.class.getResource("/visitors/visitor" + i + ".png"));
                images.add(image);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

    }



    public Visitor(double speed, Point2D position) {
        this.speed = speed;
        angle = 0.0;
        this.position = position;
        destination = new Point2D.Double(500,500);
        radius = 10;
        image = Visitor.images.get((int) (Math.random() * 8));

        currentAction = CurrentAction.IDLE;

        maxBlather = 1000;
        blather = (int) Math.random() * 500;
        hasToPee = false;

        currentAction = CurrentAction.IDLE;
    }

    public void draw(Graphics2D g)
    {
        g.setColor(Color.RED);
        AffineTransform af = new AffineTransform();
        af.translate(position.getX(),position.getY());
        af.rotate(angle);
        g.drawImage(image,af,null);

    }

    public Point2D getPosition() {
        return position;
    }

    public void setPosition(Point2D position) {
        this.position = position;
    }

    public void update()
    {

        double dx = destination.getX() - position.getX();
        double dy = destination.getY() - position.getY();

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
        preferences();
    }

    private void preferences() {
        //if blather is full the visitor has to pee
        if(blather >= maxBlather) {
            hasToPee = true;
        }

        // checks howLong a visitor
        if(isAtTarget()){
            timeAtTarget++;
            if(destination.equals(new Point2D.Double(200,900)))
                remove = true;
            if(currentAction == CurrentAction.PEEING && timeAtTarget >= 30){
                pee();
            }else if(currentAction == CurrentAction.BUYINGDRINKS && timeAtTarget >=  12){
                drink();
            }else if(currentAction == CurrentAction.WATCHING && timeAtTarget >= 30){
                currentAction = CurrentAction.IDLE;
            }else if(currentAction != CurrentAction.IDLE)
                newPosition = position;
        }
        //checks if the currentaction  = idle. if currentAction = idle then it wil select a random action
        if(currentAction == CurrentAction.IDLE) {
            timeAtTarget = 0;
            int action = (int) (Math.random() * 2);
            switch (action) {
                case 0:
                    isThirsty = true;
                    break;
                case 1:
                    currentAction = CurrentAction.WATCHING;
                    destination = new Point2D.Double(100,100);
                    /*currentTarget = random stage check if an performance is on the change

                    if so currentTarget is that stage
                    if not check rest of the changes else do nothing

                    //currentTarget = targets.get(Math.random * target.size - 1);
                    */
            }
        }

            if (currentAction != CurrentAction.PEEING && hasToPee) {
                currentAction = CurrentAction.PEEING;
                //currentTarget = getNearestToilet;
                destination = new Point2D.Double(1000,200);
            } else if (currentAction != CurrentAction.BUYINGDRINKS && isThirsty) {
                currentAction = CurrentAction.BUYINGDRINKS;
                destination = new Point2D.Double(800,800);
                //currentTarget = getNearestStand;
            }
    }

    public boolean checkcollision(ArrayList<Visitor> visitors)
    {
        boolean collision = false;

        for(Visitor v : visitors )
        {
                if(v == this)
                    continue;
                Point2D vPosition = new Point2D.Double(v.position.getX() - 4,v.position.getY() + 8);
                Point2D newPos = new Point2D.Double(newPosition.getX() - 4,newPosition.getY() + 8);
                if(vPosition.distance(newPos) < radius){
                    collision =true;
                    break; }


        }
        if(!collision)
        {
        position = newPosition;
        } else angle += 0.2;
         return collision;
    }





    public void setDestination(Point2D destination) {
        this.destination = destination;
    }

    public void drink(){
        isThirsty = false;
        blather += 200;
        currentAction = CurrentAction.IDLE;
    }

    public void pee(){
        hasToPee = false;
        //currentAction = CurrentAction.IDLE;
        blather = 0;
        destination = new Point2D.Double(200,900);
    }

    public boolean isAtTarget(){
        return position.distance(destination/*currentTarget.getPosition*/) < 50;
        //return true;
    }

}
