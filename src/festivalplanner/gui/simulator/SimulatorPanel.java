package festivalplanner.gui.simulator;

import festivalplanner.simulator.TileMap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * The SimulatorPanel displays a TileMap. The map can be dragged and zoomed using mouse
 * input, but also exports hooks to control these values.
 * If the available size is more than the map's (scaled) size, the panel will center it.
 * If there is less room, only a part of the map will be visible and the user will be
 * allowed to move the map using translateBy(). The map is kept between the bounds.
 *
 * @author Coen Boelhouwers
 */
public class SimulatorPanel extends JPanel implements MouseMotionListener, MouseWheelListener, MouseListener {

	private static final double MIN_ZOOM = 0.65;
	private static final double MAX_ZOOM = 5.65;

	private TileMap map;
	private Point2D mousePosition;
	private double scale;
	private double translateX;
	private double translateY;
	private boolean init;
	private ArrayList<Visitor> visitors;

	public SimulatorPanel() {
		super(null);
		init = false;
		map = new TileMap(getClass().getResource("/maps/Map+Colliosion.json").getPath());
		map.buildMap(6);
		scale = 0.65;
		mousePosition = new Point2D.Double(0, 0);
		visitors = new ArrayList<>();
		for(int index = 0; index < 50; index++) {
			Point2D.Double posistion = new  Point2D.Double(Math.random() * 1000, Math.random() * 1000);
			Visitor visitor = new Visitor(1.0, posistion );
			visitors.add(visitor);
		}
		addMouseMotionListener(this);
		addMouseListener(this);
		addMouseWheelListener(this);

		System.out.println("Height: " + map.getMapHeight() + " | Width: " + map.getMapWidth());
	}

	public void update() {
		for (Visitor v : visitors) {
			v.update();
			if (v.getPosition().getX() > map.getMapWidth()) {
				v.setPosition(new Point2D.Double(map.getMapWidth(), v.getPosition().getY()));
			} else {
				if (v.getPosition().getX() < 0) {
					v.setPosition(new Point2D.Double(0, v.getPosition().getY()));
				}

				if (v.getPosition().getY() > map.getMapHeight()) {
					v.setPosition(new Point2D.Double(v.getPosition().getX(), map.getMapHeight()));
				} else {
					if (v.getPosition().getY() < 0) {
						v.setPosition(new Point2D.Double(v.getPosition().getX(), 0));
					}

				}

				v.checkcollision(visitors);
			}
		}
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
		for(Visitor v : visitors)
		{
			v.draw(g2d);
		}

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
		if (scale < MIN_ZOOM) this.scale = MIN_ZOOM;
		else if (scale > MAX_ZOOM) this.scale = MAX_ZOOM;
		else this.scale = scale;
		//Check map position after zooming in/out.
		translateBy(0, 0);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		translateBy(new Point2D.Double( e.getX() - mousePosition.getX()  ,  e.getY() - mousePosition.getY()));
		mousePosition = new Point2D.Double(e.getX(), e.getY());
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	for (Visitor v : visitors)
	{
	v.setxDestination(e.getX());
	v.setyDestination(e.getY());
	}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		mousePosition = new Point2D.Double(e.getX(), e.getY());
	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mouseMoved(MouseEvent e) {

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		double value = e.getPreciseWheelRotation() * -0.1;
		if ((value < 0 && getScale() > MIN_ZOOM) ||
				(value > 0 && getScale() < MAX_ZOOM)) {
			scaleBy(value);
			//repaint();
		}
	}
}
