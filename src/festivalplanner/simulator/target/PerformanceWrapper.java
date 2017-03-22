package festivalplanner.simulator.target;

import festivalplanner.data.Performance;
import festivalplanner.data.Stage;

import java.awt.geom.Point2D;

/**
 * @author Coen Boelhouwers
 */
public class PerformanceWrapper {

	private StageTarget target;
	private Performance performance;

	public PerformanceWrapper(StageTarget target, Performance performance) {
		this.target = target;
		this.performance = performance;
	}

	public StageTarget getTarget() {
		return target;
	}

	public Performance getPerformance() {
		return performance;
	}
}
