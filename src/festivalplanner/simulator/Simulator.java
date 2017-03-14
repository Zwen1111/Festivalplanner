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

	public void runSimulation() {
		for (Visitor v : visitors) {
			v.update();
			v.checkcollision((ArrayList<Visitor>) visitors);
			if (v.getTarget() != null) {
				Point2D destiny = new Point2D.Double(v.getxDestination(), v.getyDestination());
				if (!v.isTargetSet() || v.getPosition().distance(destiny) < 20) {
					Point2D newDest = getNextWayPoint(destiny, v.getTarget());
					if (newDest != null) {
						v.setTargetSet(true);
						v.setxDestination(newDest.getX());
						v.setyDestination(newDest.getY());
					} else {
						v.setTargetSet(false);
						v.setxDestination(v.getTarget().getPosition().getX());
						v.setyDestination(v.getTarget().getPosition().getY());
					}
				}
			}
		}
	}

	public Point2D getNextWayPoint(Point2D currentPosition, Target target) {
		Target.Distance distance = target.getDistances(currentPosition);
		//System.out.println("Current: " + currentPosition);
		if (distance.getCenter() <= 0) return null;
		int c = distance.getCenter();
		int n = distance.getNorth();
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
		}
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
