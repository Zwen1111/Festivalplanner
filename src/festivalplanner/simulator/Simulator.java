package festivalplanner.simulator;

import festivalplanner.gui.simulator.Visitor;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Coen Boelhouwers
 */
public class Simulator {

	private List<Visitor> visitors;

	public Simulator() {
		visitors = new ArrayList<>();
		for(int index = 0; index < 50; index++) {
			Point2D.Double position = new  Point2D.Double(Math.random() * 1000, Math.random() * 1000);
			Visitor visitor = new Visitor(2.0, position );
			visitors.add(visitor);
		}
	}

	public void setVisitorTarget(Visitor v, Target t) {
		Point2D newDest = getNextWayPoint(v.getPosition(), t);
		if (newDest != null) {
			v.setTarget(t);
			v.setxDestination(newDest.getX());
			v.setyDestination(newDest.getY());
		}
	}

	public void runSimulation() {
		for (Visitor v : visitors) {
			v.update();
			boolean collided = v.checkcollision((ArrayList<Visitor>) visitors);
			if (collided) {
				Point2D newDest = getNextWayPoint(v.getPosition(), v.getTarget());
				if (newDest != null) {
					v.setxDestination(newDest.getX());
					v.setyDestination(newDest.getY());
				}
			}
			if (v.isTargetSet()) {
				Point2D destiny = new Point2D.Double(v.getxDestination(), v.getyDestination());
				if (v.getPosition().distance(destiny) < 20) {
					Point2D newDest = getNextWayPoint(destiny, v.getTarget());
					if (newDest != null) {
						v.setxDestination(newDest.getX());
						v.setyDestination(newDest.getY());
					}
				}
			}
			/*if (v.getTarget() != null) {
				Point2D destiny = new Point2D.Double(v.getxDestination(), v.getyDestination());
				if (!v.isTargetSet() || v.getPosition().distance(destiny) < 20) {
					Point2D newDest = getNextWayPoint(v.isTargetSet() ? destiny : v.getTarget().getPosition(), v.getTarget());
					if (newDest != null) {
						v.setTargetSet(true);
						v.setxDestination(newDest.getX());
						v.setyDestination(newDest.getY());
					} else {
						if (v.isTargetSet()) {
							v.setxDestination(v.getTarget().getPosition().getX());
							v.setyDestination(v.getTarget().getPosition().getY());
						}
						//v.setTargetSet(false);
					}
				}
			}*/
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
		/*int n = distance.getNorth();
		int e = distance.getEast();
		int s = distance.getSouth();
		int w = distance.getWest();
		while (true) {
			switch ((int) (Math.random() * 4)) {
				case 0: if (n == c - 1) return distance.getNorthPoint();
				case 1: if (e == c - 1) return distance.getEastPoint();
				case 2: if (s == c - 1) return distance.getSouthPoint();
				case 3: if (w == c - 1) return distance.getWestPoint();
			}
		}*/
		/*if (distance.getNorth() == distance.getCenter() - 1) {
			System.out.println("North: " + distance.getNorthPoint());
			return distance.getNorthPoint();
		} else if (distance.getEast() == distance.getCenter() - 1) {
			System.out.println("East: " + distance.getEastPoint());
			return distance.getEastPoint();
		} else if (distance.getSouth() == distance.getCenter() - 1) {
			System.out.println("South: " + distance.getSouthPoint());
		return distance.getSouthPoint();
	}
		else if (distance.getWest() == distance.getCenter() - 1) {
			System.out.println("West: " + distance.getWestPoint());
			return distance.getWestPoint();
		}
		else return currentPosition;*/
	}

	public List<Visitor> getVisitors() {
		return visitors;
	}
}
