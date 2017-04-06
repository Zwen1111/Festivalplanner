package festivalplanner.gui.simulator;

import festivalplanner.simulator.Navigator;
import festivalplanner.simulator.target.StageTarget;
import festivalplanner.simulator.target.Target;
import festivalplanner.simulator.target.SimpleTarget;
import festivalplanner.simulator.map.TileMap;
import festivalplanner.simulator.Simulator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.time.Duration;
import java.time.LocalTime;

/**
 * The SimulatorPanel displays a TileMap. The map can be dragged and zoomed using mouse
 * input, but also exports hooks to control these values.
 * If the available size is more than the map's (scaled) size, the panel will center it.
 * If there is less room, only a part of the map will be visible and the user will be
 * allowed to move the map using translateBy(). The map is kept between the bounds.
 *
 * @author Coen Boelhouwers
 */
public class SimulatorPanel extends JPanel implements MouseMotionListener, MouseWheelListener, MouseListener{

	private static final double MIN_ZOOM = 0.65;
	private static final double MAX_ZOOM = 5.65;

	private Simulator simulator;
	private TileMap map;
	private Point2D mousePosition;
	private double scale;
	private double translateX;
	private double translateY;
	private boolean init;
	private Rectangle2D nightOverlay;
	private int darkIndex;
	private final Font debugFont = new Font("Monospaced", Font.PLAIN, 14);
	private int debug;
	private AlphaComposite alcom;

	public SimulatorPanel() {
		super(null);
		init = false;

		map = new TileMap("/Map+Colliosion.json");
		map.buildMap();
		simulator = new Simulator(map);
		Simulator.setVisitorsAmount(10);
		simulator.setSaveInterval(Duration.ofMinutes(15));
		scale = 0.65;
		mousePosition = new Point2D.Double(0, 0);

		nightOverlay = new Rectangle2D.Double(0,0,map.getMapWidth(),map.getMapHeight());

		addMouseMotionListener(this);
		addMouseListener(this);
		addMouseWheelListener(this);
	}

	public int getDebugLevel() {
		return debug;
	}

	public Simulator getSimulator() {
		return simulator;
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


		//simulator.runSimulation();
		g2d.translate(translateX, translateY);
		g2d.scale(scale, scale);

		g2d.setFont(debugFont);
		g2d.drawImage(map.getMapImage(), null, null);
		for (Visitor v : simulator.getVisitors()) {
			if (debug == 2) v.drawDebugCircle(g2d);
			if (debug == 3) v.drawDebugInfo(g2d);
			v.draw(g2d);
		}

		if (debug >= 1) {
			Navigator.getTargets()
					.forEach(t -> {
						g2d.setColor(Color.BLACK);
						g2d.fillRect((int) t.getPosition().getX(), (int) t.getPosition().getY(), 70, 22);
						g2d.setColor(Color.WHITE);
						g2d.setStroke(new BasicStroke(2));
						g2d.drawRect((int) t.getPosition().getX(), (int) t.getPosition().getY(), 70, 22);
						g2d.drawString(String.format("%3d/%3d", t.getAttendants(), t.getCapacity()),
								(int) t.getPosition().getX() + 5, (int) t.getPosition().getY() + 15);
					});
		}

		updateDayNightCycle();
		g2d.setComposite(alcom);
		g2d.drawImage(map.getNightOverlayImage(), null, null);
	}

	private void updateDayNightCycle() {
		LocalTime time = simulator.getSimulatedTime();
		if (time.getHour() >= 18){
			int seconds = (time.getHour() * 60 * 60 + time.getMinute() * 60 + time.getSecond()) - 64800;
			darkIndex = (int) (seconds * 0.3);
		}else if (time.getHour() >= 0 && time.getHour() < 2){
			int seconds = (time.getHour() * 60 * 60 + time.getMinute() * 60 + time.getSecond());
			darkIndex = (int) ((seconds + 21600) * 0.3);
		}else if (time.getHour() >= 2 && time.getHour() < 10) {
			int seconds = (time.getHour() * 60 * 60 + time.getMinute() * 60 + time.getSecond()) - 7200;
			darkIndex = (int) (seconds * -0.3) + 8640;
		}else
			darkIndex = 0;

		float alpha = darkIndex * 0.0001f;
		alcom = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
		System.out.println(darkIndex);
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

	public void setDebugLevel(int value) {
		debug = value % 4;
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

    public void resetSimulator() {
		simulator.clearAllVisitors();
	    simulator = new Simulator(map);
    }
}
