package festivalplanner.gui.simulator;

import festivalplanner.simulator.TileMap;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

/**
 * @author Maarten Nieuwenhuize
 */
public class Simulator extends JPanel {

	private TileMap map;
	private double scaleX;
	private double scaleY;
	private double translateX;
	private double translateY;

	public Simulator() {
		super(null);
		setSize(1080, 960);
		setName("Test for the simulator");
		map = new TileMap(getClass().getResource("/Map+Colliosion.json").getPath());
		map.buildMap(6);
		scaleX = 0.65;
		scaleY = 0.75;
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(1000, 1000);
		frame.setVisible(true);
		frame.setContentPane(new SimulatorPanel());

	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.translate(translateX, translateY);
		g2d.scale(scaleX, scaleY);
		g2d.drawImage(map.getMapImage(), null, null);
	}

	public void setPosition(Point2D position) {
		if (position == null) {
			translateX = 0;
			translateY = 0;
		} else {
			translateX += position.getX();
			translateY += position.getY();
		}
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
}
