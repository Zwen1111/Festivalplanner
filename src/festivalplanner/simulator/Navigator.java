package festivalplanner.simulator;

import festivalplanner.simulator.target.Target;
import festivalplanner.simulator.target.ToiletTarget;

import java.awt.geom.Point2D;
import java.util.*;

/**
 * @author Coen Boelhouwers
 * @version 1.0
 */
public class Navigator {

	private static final List<Target> TARGETS = new ArrayList<>();

	public static void addTargets(Collection<Target> targets) {
		TARGETS.addAll(targets);
	}

	public static void clearTargets() {
		TARGETS.clear();
	}

	public static ToiletTarget getNearestToilet(Point2D position) {
		return (ToiletTarget) TARGETS.stream()
				.filter(target -> target instanceof ToiletTarget)
				.sorted(Comparator.comparing(target -> target.getDistance(position)))
				.findFirst().orElse(null);
	}

	public static List<Target> getTargets() {
		return Collections.unmodifiableList(TARGETS);
	}
}
