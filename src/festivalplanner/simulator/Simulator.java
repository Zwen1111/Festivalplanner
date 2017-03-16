package festivalplanner.simulator;

import festivalplanner.data.Database;
import festivalplanner.gui.simulator.Visitor;
import festivalplanner.simulator.map.SimpleTarget;
import festivalplanner.simulator.map.TileMap;
import festivalplanner.simulator.map.ToiletTarget;

import javax.imageio.ImageIO;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Coen Boelhouwers
 */
public class Simulator {

	private List<Visitor> visitors;
	private List<ToiletTarget> toiletTargets;
	private List<SimpleTarget> stageTargets;
	private int maxVisitors;

	public static java.util.List<BufferedImage> images;

	public Simulator(TileMap map) {
		visitors = new ArrayList<>();
		toiletTargets = new ArrayList<>();
		stageTargets = new ArrayList<>();

		maxVisitors = 200;
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

	public void setVisitorTarget(Visitor v, Target t) {
		Point2D newDest = getNextWayPoint(v.getPosition(), t);
		if (newDest != null) {
			v.setTarget(t);
			//v.setDestination(newDest);
		}
	}

	private boolean canSpawn(Visitor visitor) {
		visitor.update();
		return !visitor.checkcollision(visitors);
	}

	public void runSimulation() {
		if(stageTargets.size() == 0) {
			List<Target> targets = Database.getTargets();
			for (Target target : targets) {
				if(target instanceof  ToiletTarget){
					toiletTargets.add((ToiletTarget) target);
				}else if(target instanceof  SimpleTarget) {
					stageTargets.add((SimpleTarget) target);
				}
			}
		}
		if (visitors.size() <= maxVisitors) {
			Point2D.Double position = new  Point2D.Double(1800, 900);
			Visitor visitor = new Visitor(3, position, images.get((int) (Math.random() * 8)),this);
			if(canSpawn(visitor)) {
				visitors.add(visitor);
			}
		}

		Iterator<Visitor> visitorIterator = visitors.iterator();
		while (visitorIterator.hasNext())
		{
			Visitor v = visitorIterator.next();
			v.update();
			if(v.getRemove()){
				visitorIterator.remove();
				maxVisitors--;
				continue;
			}
			boolean collided = v.checkcollision(visitors);
			if (collided) {
				Point2D newDest = getNextWayPoint(v.getPosition(), v.getTarget());
				if (newDest != null && canSpawn(v)) {
					v.setDestination(newDest);
				}
			}
			if (v.isTargetSet()) {
				Point2D destiny = v.getDestination();
				if (v.getPosition().distance(destiny) < 20) {
					Point2D newDest = getNextWayPoint(destiny, v.getTarget());
					if (newDest != null) {
						v.setDestination(newDest);
					}
				}
			}
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

	public List<Visitor> getVisitors() {
		return visitors;
	}


	public ToiletTarget getNearestToilet(Point2D position) {
		ToiletTarget nearestTarget = null;
		for (ToiletTarget toiletTarget : toiletTargets) {
			//int distance = toiletTarget.getDistances((int) position.getX(), (int) position.getY()).getCenter();
			//if (nearestTarget == null || (nearestTarget.getDistances((int) position.getX(), (int) position.getY()).getCenter() > distance)) {
			//	nearestTarget = toiletTarget;
			//}
			int distance = (int) toiletTarget.getPosition().distance(position);
			if (nearestTarget == null || (nearestTarget.getPosition().distance(position) > distance)) {
				nearestTarget = toiletTarget;
			}
		}
		return nearestTarget;
	}

	public ToiletTarget getNearestToiletExcept(Point2D position, List<ToiletTarget> fullToilets){
		ToiletTarget nearestTarget = null;

		for (ToiletTarget fullToilet : fullToilets) {
			for (ToiletTarget toiletTarget : toiletTargets) {
				if(!fullToilet.equals(toiletTarget)) {
					int distance = toiletTarget.getDistances((int) position.getX(), (int) position.getY()).getCenter();
					if (nearestTarget == null || (nearestTarget.getDistances((int) position.getX(), (int) position.getY()).getCenter() < distance)) {
						nearestTarget = toiletTarget;
					}
				}
			}
		}

		return nearestTarget;
	}

	public int getToiletsSize() {
		return toiletTargets.size();
	}

}
