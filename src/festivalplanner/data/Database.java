package festivalplanner.data;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Coen Boelhouwers
 */
public class Database {

	private List<Performance> performances;

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
}
