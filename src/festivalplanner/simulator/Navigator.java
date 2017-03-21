package festivalplanner.simulator;

import festivalplanner.data.Artist;
import festivalplanner.data.Database;
import festivalplanner.data.Performance;
import festivalplanner.data.Stage;
import festivalplanner.simulator.target.*;

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

	public static ToiletBlockTarget getNearestToilet(Point2D position) {
		List<Target> toilets = TARGETS.stream()
				.filter(target -> target instanceof ToiletBlockTarget)
				.sorted(Comparator.comparing(target -> target.getDistance(position)))
				.collect(Collectors.toList());
		return (ToiletBlockTarget) toilets.get(0);
	}

	public static StandTarget getNearestStand(Point2D position) {
		List<Target> stands = TARGETS.stream()
				.filter(target -> target instanceof StandTarget)
				.sorted(Comparator.comparing(target -> target.getDistance(position)))
				.collect(Collectors.toList());
		return (StandTarget) stands.get(0);
	}

	public static List<Target> getTargets() {
		return Collections.unmodifiableList(TARGETS);
	}

	/**
	 * @deprecated use {@link #getPopularityBasedRandomStage(double, LocalTime)} or
	 * {@link #getRandomEmptyStage(LocalTime)} instead.
	 * @return
	 */
	@Deprecated
	public static StageTarget getDummyStage() {
		List<Target> stages =  TARGETS.stream()
				.filter(target -> target instanceof StageTarget)
				.collect(Collectors.toList());
		return (StageTarget) stages.get((int) (Math.random() * stages.size()));
	}

	public static StageTarget getRandomEmptyStage(LocalTime time) {
		List<Performance> ps = Database.getNowPerforming(time);
		List<Stage> usedStages = new ArrayList<>();
		ps.forEach(p -> usedStages.add(p.getStage()));
		List<Target> stages =  TARGETS.stream()
				.filter(target -> target instanceof StageTarget &&
						!usedStages.contains(((StageTarget) target).getStage()))
				.collect(Collectors.toList());
		return (StageTarget) stages.get((int) (Math.random() * stages.size()));
	}

	public static PerformanceTarget getPopularityBasedRandomStage(double visitorsRandom, LocalTime time) {
		List<Performance> ps = Database.getNowPerforming(time);
		Performance chosen = null;
		for (int i = 0;i < ps.size(); i++) {
			double popularity = ps.get(i).generatePopularity();
			if (visitorsRandom <= popularity) {
				chosen = ps.get(i);
				break;
			}
			visitorsRandom -= popularity;
		}
		if (chosen == null) return null;
		final Performance finalChosen = chosen;
		StageTarget st = (StageTarget) TARGETS.stream()
				.filter(target -> target instanceof StageTarget &&
						((StageTarget) target).getStage().equals(finalChosen.getStage()))
				.findFirst().orElse(null);
		if (st != null) return new PerformanceTarget(st, finalChosen);
		else {
			System.out.println("Unexpected: No target found for Stage!");
			return null;
		}
	}

	/**
	 * Searches the Database for performances which host a specific Artist and start
	 * after the specified time. Returns a list of Stage-targets that represent the
	 * location of that stage, as well as contains the tile-distance to it.
	 *
	 * @param artists the preferred Artists.
	 * @param afterTime the time after which it should start.
	 * @return a list of Targets matching preferences. Empty if no matches.
	 */
	public static List<PerformanceTarget> getArtistPerformances(Collection<Artist> artists, LocalTime afterTime) {
		if (artists == null) return new ArrayList<>();
		List<PerformanceTarget> targets = new ArrayList<>();
		List<Performance> performances = new ArrayList<>();
		artists.forEach(e-> performances.addAll(Database.getPerformacesOfArtist(e)));
		performances.stream()
				.filter(performance -> performance.getStartTime().isAfter(afterTime))
				.forEach(performance -> TARGETS.stream()
						.filter(target -> target instanceof StageTarget
								&& ((StageTarget) target).getStage().equals(performance.getStage()))
						.findFirst()
						.ifPresent(target -> targets.add((PerformanceTarget) target)));
		return targets;
	}

	public static void addTarget(SimpleTarget target) {
		TARGETS.add(target);
	}
}
