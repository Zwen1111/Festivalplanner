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
	private boolean init;

	public TileMapPanel() {
		super(null);
		init = false;
		map = new TileMap(getClass().getResource("/maps/Map+Colliosion.json").getPath());
		map.buildMap(6);
		scale = 0.65;
	}

	public void update() {

	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		//The width and height of the panel are only known on the first layout.
		//The smart-scaling can only be done once panel size is known (= paintComponent being called).
		if (!init) {
			smartScale();
			init = true;
		}
		g2d.translate(translateX, translateY);
		g2d.scale(scale, scale);
		g2d.drawImage(map.getMapImage(), null, null);
	}

	private void smartScale() {
		//Scale until the map matches the screen's height and/or width.
		//First, find the smallest distance to scale: either the height or width
		//of the screen. Then calculate the scale.
		double xdiff = Math.abs(getWidth() - map.getMapWidth());
		double ydiff = Math.abs(getHeight() - map.getMapHeight());
		if (ydiff <= xdiff) {
			//The screen is more width (or equal to) than the map.
			//Scale the map to match height of the screen.
			scale = (double) getHeight() / map.getMapHeight();
		} else {
			//The screen has more height than the map.
			//Scale the map to match width of the screen.
			scale = map.getMapWidth() / getWidth();
		}
		//Then, center the map on the screen.
		translateBy(0, 0);
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
