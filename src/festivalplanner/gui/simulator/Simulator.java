package festivalplanner.gui.simulator;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * @author Maarten Nieuwenhuize
 */
public class Simulator extends JPanel {


    public Simulator() {
        super(null);
        setSize(1080,960);
        setName("Test for the simulator");
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;


        Rectangle2D.Double background = new Rectangle2D.Double(0,0,2000,960);
        BufferedImage texture;
        try
        {
            texture = ImageIO.read(getClass().getResource("/backgroundTest.png"));
            g2d.setPaint(new TexturePaint(texture, new Rectangle2D.Double(0,0,1920,960)));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        g2d.fill(background);
    }
}
