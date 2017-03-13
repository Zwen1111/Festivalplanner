package festivalplanner.gui.simulator;

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
			Visitor visitor = new Visitor(1.0, position );
			visitors.add(visitor);
		}
	}

	public void runSimulation() {
		for (Visitor v : visitors) {
			v.update();
		}
	}

	public List<Visitor> getVisitors() {
		return visitors;
	}
}
