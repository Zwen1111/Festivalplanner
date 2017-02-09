package festivalplanner.data;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The Database manages all Performances and offers utility methods
 * to retrieve useful data.
 *
 * @author Coen Boelhouwers
 */
public class Database {

	private List<Performance> performances;
	private LocalTime nextTime;

	/**
	 * Creates a new database.
	 */
	public Database() {
		performances = new ArrayList<>();
	}

	public List<Performance> getPerformances() {
		return performances;
	}

	/**
	 * Filters out all unique stages on which performances take place.
	 * This method has to search trough all performances, so users are
	 * asked to limit the amount of calls and keep a copy where appropriate.
	 *
	 * @return a list of all stages.
	 */
	public List<Stage> getStages() {
		Collection<Stage> stages = new ArrayList<>();
		performances.forEach(perf -> {
			if (!stages.contains(perf.getStage())) stages.add(perf.getStage());
		});
		return new ArrayList<>(stages);
	}

	/**
	 * Searches the specified Stage for the first next opportunity to give a performance
	 * of a certain duration on that stage and returns the start time.
	 *
	 * @param stage the Stage on which the performance should take place.
	 * @param duration the wanted duration.
	 * @return the start time of a space matching the requirements, or null if no space
	 * could be found on this Stage.
	 */
	public LocalTime findNextEmptyStageTime(Stage stage, Duration duration) {
		nextTime = LocalTime.MIDNIGHT;
		performances.stream().filter(perf -> perf.getStage().equals(stage)).forEach(perf -> {
			if (perf.getStartTime().isBefore(nextTime.plus(duration)))
				nextTime = perf.getEndTime();
		});
		return nextTime;
	}
}
