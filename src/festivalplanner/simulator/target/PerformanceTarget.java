package festivalplanner.simulator.target;

import festivalplanner.data.Performance;
import festivalplanner.data.Stage;

import java.awt.geom.Point2D;

/**
 * @author Coen Boelhouwers
 */
public class PerformanceTarget extends StageTarget {

	private Performance performance;

	public PerformanceTarget(StageTarget target, Performance performance){
		super(target.getPosition(), target.getStage());
		this.performance = performance;
	}

	public Performance getPerformance() {
		return performance;
	}

	@Override
	public String toString() {
		return "PerformanceTarget{" +
				"stage=" + getStage() +
				", performance=" + getPerformance() +
				'}';
	}
}
