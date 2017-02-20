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


    public Simulator() {
        super(null);
        setSize(1080,960);
        setName("Test for the simulator");
		map = new TileMap(getClass().getResource("/Map+Colliosion.json").getPath());
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        map.draw((Graphics2D) g);
    }
}
