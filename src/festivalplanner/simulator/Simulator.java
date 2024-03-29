package festivalplanner.simulator;

import festivalplanner.gui.simulator.Visitor;
import festivalplanner.simulator.map.TileMap;
import festivalplanner.simulator.target.SimpleTarget;
import festivalplanner.simulator.target.Target;

import javax.imageio.ImageIO;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * @author Coen Boelhouwers
 */
public class Simulator {

	public static final LocalDateTime START_TIME = LocalDateTime.of(LocalDate.now(), LocalTime.of(5, 45));
	public static final int MAX_SNAPSHOTS = 40;
	public static final Duration DEFAULT_SNAPSHOT_INTERVAL = Duration.ofMinutes(15);

	private static int maxVisitors;
	private static Duration saveInterval;

	private SimulatorState state;
	private int stateCounter;
	private int currentStateIndex;
	private LocalDateTime lastSave;
	private List<File> saveLocations;

	public static java.util.List<BufferedImage> images;

	public Simulator(TileMap map) {
		this(map, DEFAULT_SNAPSHOT_INTERVAL);
	}

	public Simulator(TileMap map, Duration wantedSaveInterval) {
		setSaveInterval(wantedSaveInterval);
		Navigator.clearTargets();
		Navigator.addTargets(map.getTargets());
		SimpleTarget target = new SimpleTarget(new Point2D.Double(1710,750));
		target.setupDistances(map);
		Navigator.addTarget(target);
		state = new SimulatorState(START_TIME);
		lastSave = START_TIME;
		stateCounter = 0;
		saveLocations = new ArrayList<>();
		currentStateIndex = 0;
		getImages();
		for (int i = 0; i < MAX_SNAPSHOTS; i++) {
			File location = null;
			try {
				location = File.createTempFile("simulator_state_", null);
				location.deleteOnExit();
				saveLocations.add(location);
				System.out.println("New snapshot file: " + location + " [" + i + "]");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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
		return state.currentTime.getHour() >= 6 && !visitor.checkcollision(state.visitors);
	}

	public void clearAllVisitors() {
		ListIterator<Visitor> it = state.visitors.listIterator();
		while (it.hasNext()) {
			Visitor v = it.next();
			v.resetAction();
			it.remove();
		}
	}

	public void runSimulation(LocalDateTime time) {
		state.currentTime = time;
		stateCounter = currentStateIndex;
		if (saveInterval != null && time.isAfter(lastSave.plus(saveInterval))) {
			saveState();
			lastSave = time;
		}
		if (state.visitors.size() < maxVisitors) {
			Point2D.Double position = new  Point2D.Double(1710, 750);
			Visitor visitor = new Visitor(3, position, (int) (Math.random() * 8));
			visitor.setImage(images.get(visitor.getImageId()));
			if(canSpawn(visitor)) {
				state.visitors.add(visitor);
			}
		}

		Iterator<Visitor> visitorIterator = state.visitors.iterator();
		while (visitorIterator.hasNext())
		{
			Visitor v = visitorIterator.next();
			v.update(time.toLocalTime());
			if(v.getRemove()){
				visitorIterator.remove();
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
	 * The future is unpredictable. Once the simulation is run from a point in the
	 * past, you can't go back to the future. However, if the simulation stays halted while
	 * you're in the past, this method does allow you to go back.
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
				saveLocations.get(newIndex % MAX_SNAPSHOTS)))) {
			state.visitors.forEach(Visitor::resetAction);
			state = (SimulatorState) ois.readObject();
			state.visitors.forEach(v -> {
				v.setImage(images.get(v.getImageId()));
				Navigator.getTargets().stream()
						.filter(target -> target.equals(v.getTarget()))
						.findFirst()
						.ifPresent(v::setTarget);
				v.replayAction();
			});
			currentStateIndex = newIndex;
			lastSave = state.currentTime;
			System.out.println("Back to " + lastSave + "(index: " + currentStateIndex + "/" + stateCounter + ")");
			return true;
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void saveState() {
		File location = saveLocations.get(stateCounter % MAX_SNAPSHOTS);
		System.out.println("Reused [" + stateCounter % MAX_SNAPSHOTS + "]");
		try (FileOutputStream fos = new FileOutputStream(location);
			 ObjectOutputStream oos = new ObjectOutputStream(fos)) {
			oos.writeObject(this.state);
			stateCounter++;
			currentStateIndex = stateCounter;
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
		return state.currentTime.toLocalTime();
	}

	public List<Visitor> getVisitors() {
		return state.visitors;
	}

	public static int getMaxVisitors() {
		return maxVisitors;
	}

	public static void setMaxVisitors(int visitorsAmount) {
	   maxVisitors = visitorsAmount;
    }

    public static Duration getSaveInterval() {
		return saveInterval;
	}

    public static void setSaveInterval(Duration duration) {
		saveInterval = duration;
		System.out.println("Snapshot interval set to " + saveInterval);
	}

    public Visitor intersectsVisitors(Point2D point) {
		for (Visitor visitor : getVisitors()) {
			if(visitor.intersects(point)){
				return visitor;
			}
		}
		return null;
    }

    private static class SimulatorState implements Serializable {
		private List<Visitor> visitors;
		private LocalDateTime currentTime;

		SimulatorState(LocalDateTime startTime) {
			visitors = new ArrayList<>();
			currentTime = startTime;
		}
	}
}
