package festivalplanner.gui;

import festivalplanner.data.Performance;

/**
 * @author Coen Boelhouwers
 */
@FunctionalInterface
public interface OnPerformanceCreatedListener {
	void performanceCreated(Performance performance);
}
