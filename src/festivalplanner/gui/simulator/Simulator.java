package festivalplanner.gui.simulator;

import festivalplanner.simulator.TileMap;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * @author Maarten Nieuwenhuize
 */
public class Simulator extends JPanel {

	private TileMap map;

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(1000,1000);
        frame.setVisible(true);
        frame.setContentPane(new Simulator());

    }


    private double scaleX;
    private double scaleY;
    private Point2D position;
    private AffineTransform transform;
    public Simulator() {
        super(null);
        setSize(1080,960);
        setName("Test for the simulator");
		map = new TileMap(getClass().getResource("/Map+Colliosion.json").getPath());
        transform = new AffineTransform();
        scaleX = 0.65;
        scaleY = 0.75;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        map.draw((Graphics2D) g);
        Graphics2D g2d = (Graphics2D) g;

        Rectangle2D.Double background = new Rectangle2D.Double(0,0,2000,960);
        BufferedImage texture;
        g2d.scale(scaleX, scaleY);
        try
        {
            texture = ImageIO.read(getClass().getResource("/backgroundTest.png"));

            if (position != null) {

                transform.translate( position.getX() * 0.2,  position.getY() * 0.2);


            }
            g2d.drawImage(texture,transform,null);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }



    }

  public void setPosition(Point2D position) {
        this.position = position;
  }

    public Point2D getPosition() {
        return position;
    }

    public double getScaleX() {
        return scaleX;
    }

    public void setScaleX(double scaleX) {
        this.scaleX = scaleX;
    }

    public double getScaleY() {
        return scaleY;
    }

    public void setScaleY(double scaleY) {
        this.scaleY = scaleY;
    }

    public void setTransform(AffineTransform transform) {
        this.transform = transform;
    }
}
