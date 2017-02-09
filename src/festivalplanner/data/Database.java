package festivalplanner.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Coen Boelhouwers
 */
public class Database implements Serializable {

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
