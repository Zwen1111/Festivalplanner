package festivalplanner.data;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Coen Boelhouwers
 */
public class Database implements Serializable{

	private List<Performance> performances;
	private LocalTime nextTime;

	public Database() {
		performances = new ArrayList<>();
	}

	public List<Performance> getPerformances() {
		return performances;
	}

	public List<Stage> getStages() {
		Collection<Stage> stages = new ArrayList<>();
		performances.forEach(perf -> {
			if (!stages.contains(perf.getStage())) stages.add(perf.getStage());
		});
		return new ArrayList<>(stages);
	}

	public LocalTime findNextEmptyStageTime(Stage stage, Duration duration) {
		nextTime = LocalTime.MIDNIGHT;
		performances.stream().filter(perf -> perf.getStage().equals(stage)).forEach(perf -> {
			if (perf.getStartTime().isBefore(nextTime.plus(duration)))
				nextTime = perf.getEndTime();
		});
		return nextTime;
	}

	public void setPerformances(List<Performance> performances) {
		this.performances = performances;
	}
}
