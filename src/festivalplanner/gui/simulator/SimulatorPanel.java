package festivalplanner.gui.simulator;

import festivalplanner.simulator.Target;
import festivalplanner.simulator.map.TileMap;
import festivalplanner.simulator.Simulator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;

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

	private Simulator simulator;
	private TileMap map;
	private Point2D mousePosition;
	private double scale;
	private double translateX;
	private double translateY;
	private boolean init;
	private Target target;

	public SimulatorPanel() {
		super(null);
		init = false;
		simulator = new Simulator();
		map = new TileMap(getClass().getResource("/maps/Map+Colliosion.json").getPath());
		map.buildMap(6);
		scale = 0.65;
		mousePosition = new Point2D.Double(0, 0);

		addMouseMotionListener(this);
		addMouseListener(this);
		addMouseWheelListener(this);
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

		simulator.runSimulation();
		g2d.translate(translateX, translateY);
		g2d.scale(scale, scale);

		g2d.drawImage(map.getMapImage(), null, null);
		if (target != null) {
			for (int x = 0; x < target.getLayer().getWidth(); x++) {
				for (int y = 0; y < target.getLayer().getHeight(); y++) {
					int dist = target.getDistance(x, y);
	/*				//g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.2f));
					g2d.setPaint(dist < 0 ? Color.blue : Color.yellow);
					g2d.fillRect(x * map.getTileWidth(), y * map.getTileHeight(),
							map.getTileWidth(), map.getTileHeight());
					g2d.setPaint(Color.black);
					//g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 1.0f));*/
					g2d.drawString(String.valueOf(dist),
							x * map.getTileWidth(),
							y * map.getTileHeight() + 10);
				}
			}
		}
		for(Visitor v : simulator.getVisitors())
		{
			v.draw(g2d);
			if (target != null && !v.isTargetSet()) {
				v.setPosition(new Point2D.Double(Math.random() * map.getMapWidth(),
						Math.random() * map.getMapHeight()));
				simulator.setVisitorTarget(v, target);
			}
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
		long mil = System.currentTimeMillis();
		target = new Target(new Point2D.Double(e.getX() / scale - translateX / scale,
				e.getY() / scale - translateY / scale), map){};
		System.out.println("took " + (System.currentTimeMillis() - mil) + " ms");
		for (Visitor v : simulator.getVisitors()) {
			simulator.setVisitorTarget(v, target);
			//v.setTarget(target);
			//v.setxDestination(e.getX() / scale - translateX / scale);
			//v.setyDestination(e.getY() / scale - translateY / scale);
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
