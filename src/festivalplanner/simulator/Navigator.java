package festivalplanner.simulator;

import festivalplanner.data.Artist;
import festivalplanner.data.Database;
import festivalplanner.data.Performance;
import festivalplanner.simulator.target.StageTarget;
import festivalplanner.simulator.target.StandTarget;
import festivalplanner.simulator.target.Target;
import festivalplanner.simulator.target.ToiletTarget;

import java.awt.geom.Point2D;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

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
		List<Target> toilets = TARGETS.stream()
				.filter(target -> target instanceof ToiletTarget)
				.sorted(Comparator.comparing(target -> target.getDistance(position)))
				.collect(Collectors.toList());
		return (ToiletTarget) toilets.get(toilets.size() - 1);
	}

	public static StandTarget getNearestStand(Point2D position) {
		List<Target> stands = TARGETS.stream()
				.filter(target -> target instanceof StandTarget)
				.sorted(Comparator.comparing(target -> target.getDistance(position)))
				.collect(Collectors.toList());
		return (StandTarget) stands.get(stands.size() - 1);
	}

	public static List<Target> getTargets() {
		return Collections.unmodifiableList(TARGETS);
	}

	@Deprecated
	public static StageTarget getDummyStage() {
		List<Target> stages =  TARGETS.stream()
				.filter(target -> target instanceof StageTarget)
				.collect(Collectors.toList());
		return (StageTarget) stages.get((int) (Math.random() * stages.size()));
	}

	/**
	 * Searches the Database for performances which host a specific Artist and start
	 * after the specified time. Returns a list of Stage-targets that represent the
	 * location of that stage, as well as contains the tile-distance to it.
	 *
	 * @param artist the preferred Artist.
	 * @param afterTime the time after which it should start.
	 * @return a list of Targets matching preferences. Empty if no matches.
	 */
	public static List<StageTarget> getArtistPerformances(Artist artist, LocalTime afterTime) {
		List<StageTarget> targets = new ArrayList<>();
		Database.getPerformacesOfArtist(artist).stream()
				.filter(performance -> performance.getStartTime().isAfter(afterTime))
				.forEach(performance -> TARGETS.stream()
						.filter(target -> target instanceof StageTarget
								&& ((StageTarget) target).getStage().equals(performance.getStage()))
						.findFirst()
						.ifPresent(target -> targets.add((StageTarget) target)));
		return targets;
	}
}
