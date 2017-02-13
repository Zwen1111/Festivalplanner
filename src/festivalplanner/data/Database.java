package festivalplanner.data;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * The Database manages all Performances and offers utility methods
 * to retrieve useful data.
 *
 * @author Coen Boelhouwers
 */
public class Database implements Serializable {

	private List<Performance> performances;
	private List<Artist> artists;
	private List<Stage> stages;
	private List<OnDataChangedListener> listeners;
	private LocalTime nextTime;

	/**
	 * Creates a new database.
	 */
	public Database() {
		performances = new ArrayList<>();
		artists = new ArrayList<>();
		stages = new ArrayList<>();
		listeners = new ArrayList<>();
	}

	public void addArtist(Artist artist) {
		if (!artists.contains(artist)) {
			artists.add(artist);
			notifyDataChanged();
		}
	}

	public void addArtists(Collection<Artist> artists) {
		artists.forEach(this::addArtist);
	}

	public void addOnDataChangedListener(OnDataChangedListener l) {
		listeners.add(l);
	}

	public void addPerformance(Performance performance) {
		performances.add(performance);
		addArtists(performance.getArtists());
		addStage(performance.getStage());
		notifyDataChanged();
	}

	public void addPerformances(Collection<Performance> performances) {
		performances.forEach(this::addPerformance);
	}

	public void addStage(Stage stage) {
		if (!stages.contains(stage)) {
			stages.add(stage);
			notifyDataChanged();
		}
	}

	public void addStages(Collection<Stage> stages) {
		stages.forEach(this::addStage);
	}

	public void clear() {
		performances.clear();
		stages.clear();
		artists.clear();
	}

	public List<Artist> getArtists() {
		return Collections.unmodifiableList(artists);
	}

	public List<Performance> getPerformances() {
		return Collections.unmodifiableList(performances);
	}

	/**
	 *
	 * @return a list of all stages.
	 */
	public List<Stage> getStages() {
		return Collections.unmodifiableList(stages);
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

	public void removeOnDataChangedListener(OnDataChangedListener l) {
		listeners.remove(l);
	}

	/**
	 * Removes the performance from the list of performances
	 *
	 * @param performance the performance that needs to be removed
	 * @return returns true if the performance is removed else it wil return false
	 */
	public boolean removePerformance(Performance performance) {
		int numerOfPerformance =performances.indexOf(performance);
		if(performances.get(numerOfPerformance).equals(performance)) {
			performances.remove(numerOfPerformance);
			notifyDataChanged();
			return true;
		}else return false;
	}



	/**
	 * Notify all listeners that data in the database has changed.
	 */
	public void notifyDataChanged() {
		listeners.forEach(OnDataChangedListener::onDataChanged);
	}

	@FunctionalInterface
	public interface OnDataChangedListener {
		/**
		 * Called when something changed in the data. The type of change is unknown.
		 */
		void onDataChanged();
	}
}
