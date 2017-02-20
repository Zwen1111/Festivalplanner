package festivalplanner.gui.simulator;

import festivalplanner.simulator.TileMap;

import javax.imageio.ImageIO;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Maarten Nieuwenhuize
 */
public class Simulator extends JPanel {
    TilesetManager t;

    public static void main(String[] args) {

        JsonObject jsonObject = null;

        try (
                InputStream is = new FileInputStream("resources\\maps\\Map+Colliosion.json");
                JsonReader js = Json.createReader(is);
        ) {

            jsonObject = js.readObject();

        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }

        Simulator simulator = new Simulator();
        if(jsonObject == null) {
            System.err.println("jsonObject == null");
        }else {


            simulator.t = new TilesetManager(jsonObject.getJsonArray("tilesets"));


        }

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(1000,1000);
        frame.setVisible(true);
        frame.setContentPane(simulator);

    }


    public Simulator() {
        super(null);
        setSize(1080,960);
        setName("Test for the simulator");
		TileMap map = new TileMap("/Map+Colliosion.json");
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;


        g2d.drawImage(t.getImage(5),new AffineTransform(),null);
    }
}
