package festivalplanner.simulator.target;

import festivalplanner.simulator.target.Target;

import java.awt.geom.Point2D;

/**
 * @author Coen Boelhouwers
 */
public class StandTarget extends Target {
	public StandTarget(Point2D position, int targetCapacity) {
		super(position);
		setCapacity(targetCapacity);
	}
}
