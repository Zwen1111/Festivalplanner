package festivalplanner.gui.simulator;

import festivalplanner.simulator.TileMap;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

/**
 * The TileMapPanel displays a TileMap.
 * The class has hooks to set the scaling of the map, as well as the offset position.
 * If the available size is more than the map's (scaled) size, the panel will center it.
 * If there is less room, only a part of the map will be visible and the user will be
 * allowed to move the map using translateBy(). The map is kept between the bounds.
 *
 * @author Coen Boelhouwers
 */
public class TileMapPanel extends JPanel {

	private TileMap map;
	private double scale;
	private double translateX;
	private double translateY;
	private boolean init = false;

	public TileMapPanel() {
		super(null);
		map = new TileMap(getClass().getResource("/Map+Colliosion.json").getPath());
		map.buildMap(6);
		scale = 0.65;
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
		if (!init) {
			//Center on screen once panel size is known.
			translateBy(0, 0);
			init = true;
		}
		g2d.translate(translateX, translateY);
		g2d.scale(scale, scale);
		g2d.drawImage(map.getMapImage(), null, null);
	}

	public void scaleBy(double amount) {
		setScale(scale + amount);
	}

	public void translateBy(Point2D position) {
		if (position == null) {
			translateBy(0, 0);
		} else {
			translateBy(position.getX(), position.getY());
		}
	}

	public void translateBy(double deltaX, double deltaY) {
		translateX += deltaX;
		translateY += deltaY;
		if (map.getMapWidth() * scale < getWidth())
			translateX = (getWidth() - map.getMapWidth() * scale)/2;
		else if (translateX >= 0)
			translateX = 0;
		else if (translateX + map.getMapWidth() * scale < getWidth())
			translateX = getWidth() - map.getMapWidth() * scale;

		if (map.getMapHeight() * scale < getHeight())
			translateY = (getHeight() - map.getMapHeight() * scale)/2;
		else if (translateY >= 0 || map.getMapHeight() * scale < getHeight())
			translateY = 0;
		else if (translateY + map.getMapHeight() * scale < getHeight())
			translateY = getHeight() - map.getMapHeight() * scale;
	}

	public double getScale() {
		return scale;
	}

	public void setScale(double scale) {
		this.scale = scale;
		//Check map position after zooming in/out.
		translateBy(0, 0);
	}
}
