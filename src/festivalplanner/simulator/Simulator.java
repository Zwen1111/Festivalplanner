package festivalplanner.simulator;

import festivalplanner.gui.simulator.SimulatorTabGUI;
import festivalplanner.gui.simulator.Visitor;
import festivalplanner.simulator.map.TileMap;
import festivalplanner.simulator.target.SimpleTarget;
import festivalplanner.simulator.target.Target;

import javax.imageio.ImageIO;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Coen Boelhouwers
 */
public class Simulator {

	public static final int MAX_SNAPSHOTS = 10;

	private SimulatorState state;
	private static int maxVisitors;
	private String baseLocation;
	private int stateCounter;
	private int currentStateIndex;

	public static java.util.List<BufferedImage> images;
    public static boolean debug;

	public Simulator(TileMap map) {
		Navigator.clearTargets();
		Navigator.addTargets(map.getTargets());
		SimpleTarget target = new SimpleTarget(new Point2D.Double(1710,750));
		target.setupDistances(map);
		Navigator.addTarget(target);
		state = new SimulatorState();
		state.visitors = new ArrayList<>();
		state.currentTime = SimulatorTabGUI.START_TIME;
		maxVisitors = 30;//200;
		stateCounter = 0;
		currentStateIndex = 0;
		getImages();
	}

	public static void getImages() {
		images = new ArrayList<>();
		try {

			for (int i = 0; i < 9; i++) {
				BufferedImage image = ImageIO.read(Visitor.class.getResource("/visitors/visitor" + i + ".png"));
				images.add(image);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private boolean canSpawn(Visitor visitor) {
	    if(state.currentTime.getHour() < 6) return false;
		return !visitor.checkcollision(state.visitors);
	}

	public void runSimulation(LocalTime time) {
		state.currentTime = time;
		if (state.visitors.size() < maxVisitors) {
			Point2D.Double position = new  Point2D.Double(1710, 750);
			Visitor visitor = new Visitor(3, position, images.get((int) (Math.random() * 8)));
			if(canSpawn(visitor)) {
				state.visitors.add(visitor);
			}
		}

		Iterator<Visitor> visitorIterator = state.visitors.iterator();
		while (visitorIterator.hasNext())
		{
			Visitor v = visitorIterator.next();
			v.update(time);
			if(v.getRemove()){
				visitorIterator.remove();
				maxVisitors--;
				continue;
			}
			boolean collided = v.checkcollision(state.visitors);
			if (collided) {
				Point2D newDest = getNextWayPoint(v.getPosition(), v.getTarget());
				if (newDest != null) {
					v.setDestination(newDest);
				}
			}
			if (v.isTargetSet()) {
				if (!v.isDestinationSet()) {
					//Visitor's target has changed, but the first way-point isn't set yet.
					v.setDestination(getNextWayPoint(v.getPosition(), v.getTarget()));
				} else {
					Point2D destiny = v.getDestination();
					if (v.getPosition().distance(destiny) < 30) {
						Point2D newDest = getNextWayPoint(destiny, v.getTarget());
						if (newDest != null) {
							v.setDestination(newDest);
						}
					}
				}
			}
		}
	}

	public void runSimulation(Duration duration) {
		runSimulation(state.currentTime.plus(duration));
	}

	/**
	 * Tries to restore a previous state of the simulator.
	 *
	 * @param amount the amount of snapshots the simulator should look back in the past.
	 *               A negative number for past states, positive for future.
	 * @return true if the previous state has successfully been restored, false otherwise.
	 */
	public boolean restoreState(int amount) {
		int newIndex = currentStateIndex + amount;
		int indexDiff = Math.abs(stateCounter - newIndex);
		if (indexDiff > MAX_SNAPSHOTS) {
			System.out.println("Can't restore state: snapshot bounds reached (max: " + MAX_SNAPSHOTS + ")");
			return false;
		}
		if (newIndex >= stateCounter || newIndex < 0) {
			System.out.println("Can't restore state: not (yet) existing snapshot (index: " + newIndex +
					", size: " + stateCounter);
			return false;
		}
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
				new File(baseLocation, "simulator_state_" + newIndex % MAX_SNAPSHOTS)))) {
			state = (SimulatorState) ois.readObject();
			return true;
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void saveState() {
		try {
			File location = File.createTempFile("simulator_state_" + stateCounter % MAX_SNAPSHOTS, null);
			baseLocation = location.getAbsolutePath();
			try (FileOutputStream fos = new FileOutputStream(location);
				 ObjectOutputStream oos = new ObjectOutputStream(fos)) {
				oos.writeObject(this.state);
				stateCounter++;
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Point2D getNextWayPoint(Point2D currentPosition, Target target) {
		if (currentPosition == null || target == null) return null;
		Target.Distance distance = target.getDistances(currentPosition);
		//System.out.println("Current: " + currentPosition);
		if (distance.getCenter() < 0) return null;
		List<Point2D> possibleDirections = new ArrayList<>();
		int c = distance.getCenter();
		int n = distance.getNorth();
		int e = distance.getEast();
		int s = distance.getSouth();
		int w = distance.getWest();
		if ((n == 0 || n == c - 1) && n >= 0)
			possibleDirections.add(distance.getNorthPoint());
		if ((e == 0 || e == c - 1) && e >= 0)
			possibleDirections.add(distance.getEastPoint());
		if ((s == 0 || s == c - 1) && s >= 0)
			possibleDirections.add(distance.getSouthPoint());
		if ((w == 0 || w == c - 1) && w >= 0)
			possibleDirections.add(distance.getWestPoint());
		if (possibleDirections.isEmpty()) return null;
		else return possibleDirections.get((int) Math.floor(Math.random() * possibleDirections.size()));
	}

	public LocalTime getSimulatedTime() {
		return state.currentTime;
	}

	public List<Visitor> getVisitors() {
		return state.visitors;
	}

	public static void setVisitorsAmount(int visitorsAmount) {
	   maxVisitors = visitorsAmount;
    }

	private static class SimulatorState {
		private List<Visitor> visitors;
		private LocalTime currentTime;
	}
}
