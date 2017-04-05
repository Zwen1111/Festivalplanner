package festivalplanner.simulator;

import festivalplanner.data.Artist;
import festivalplanner.data.Database;
import festivalplanner.data.Performance;
import festivalplanner.data.Stage;
import festivalplanner.simulator.target.*;

import java.awt.geom.Point2D;
import java.time.Duration;
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

	public static ToiletBlockTarget getNearestToilet(Point2D position, Target ignore) {
		return (ToiletBlockTarget) TARGETS.stream()
				.filter(target -> target instanceof ToiletBlockTarget && !target.equals(ignore))
				.sorted(Comparator.comparing(target -> target.getDistance(position)))
				.findFirst().orElse(null);
	}

	public static StandTarget getNearestStand(Point2D position, Target ignore) {
		return (StandTarget) TARGETS.stream()
				.filter(target -> target instanceof StandTarget && !target.equals(ignore))
				.sorted(Comparator.comparing(target -> target.getDistance(position)))
				.findFirst().orElse(null);
	}

	public static List<Target> getTargets() {
		return Collections.unmodifiableList(TARGETS);
	}

	/**
	 * @deprecated use {@link #getPopularityBasedRandomStage(double, LocalTime, Duration)} or
	 * {@link #getRandomEmptyStage(LocalTime, Duration)} instead.
	 * @return
	 */
	@Deprecated
	public static StageTarget getDummyStage() {
		List<Target> stages =  TARGETS.stream()
				.filter(target -> target instanceof StageTarget)
				.collect(Collectors.toList());
		return (StageTarget) stages.get((int) (Math.random() * stages.size()));
	}

	public static StageTarget getRandomEmptyStage(LocalTime time, Duration preLook) {
		List<Performance> ps = Database.getNowPerforming(time, preLook);
		List<Stage> usedStages = new ArrayList<>();
		ps.forEach(p -> usedStages.add(p.getStage()));
		List<Target> stages =  TARGETS.stream()
				.filter(target -> target instanceof StageTarget &&
						!usedStages.contains(((StageTarget) target).getStage()))
				.collect(Collectors.toList());
		return (StageTarget) stages.get((int) (Math.random() * stages.size()));
	}

	public static PerformanceWrapper getPopularityBasedRandomStage(double visitorsRandom, LocalTime time,
																   Duration preLook) {
		List<Performance> ps = Database.getNowPerforming(time, preLook);
		Performance chosen = null;
		for (Performance p : ps) {
			double popularity = p.generatePopularity();
			if (visitorsRandom <= popularity) {
				chosen = p;
				break;
			}
			visitorsRandom = (visitorsRandom - popularity) / (10 - popularity) * 10;
		}
		if (chosen == null) return null;
		final Performance finalChosen = chosen;
		StageTarget st = (StageTarget) TARGETS.stream()
				.filter(target -> target instanceof StageTarget &&
						((StageTarget) target).getStage().equals(finalChosen.getStage()))
				.findFirst().orElse(null);
		if (st != null) return new PerformanceWrapper(st, finalChosen);
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
	public static List<PerformanceWrapper> getArtistPerformances(Collection<Artist> artists, LocalTime afterTime) {
		if (artists == null) return new ArrayList<>();
		List<PerformanceWrapper> targets = new ArrayList<>();
		List<Performance> performances = new ArrayList<>();
		artists.forEach(e-> performances.addAll(Database.getPerformacesOfArtist(e)));
		performances.stream()
				.filter(performance -> performance.getStartTime().isAfter(afterTime))
				.forEach(performance -> TARGETS.stream()
						.filter(target -> target instanceof StageTarget
								&& ((StageTarget) target).getStage().equals(performance.getStage()))
						.findFirst()
						.ifPresent(target -> targets.add(new PerformanceWrapper((StageTarget) target, performance)))
				);
		return targets;
	}

	public static void addTarget(SimpleTarget target) {
		TARGETS.add(target);
	}
}
