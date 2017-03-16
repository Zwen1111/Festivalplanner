package festivalplanner.data;

import festivalplanner.simulator.Target;
import festivalplanner.simulator.data.ObjectLayer;
import festivalplanner.simulator.map.TileMap;

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

	private static final List<Performance> performances = new ArrayList<>();
	private static final List<Artist> artists = new ArrayList<>();
	private static final List<Stage> stages = new ArrayList<>();
	private static final List<OnDataChangedListener> listeners = new ArrayList<>();

	/**
	 * Creates a new database.
	 *
	 * @deprecated Database now is static. Lists are created automagically.
	 */
	@Deprecated
	public Database() {

	}

	public static void addArtist(Artist artist) {
		if (!artists.contains(artist)) {
			artists.add(artist);
			notifyDataChanged("Added Artist: " + artist);
		}
	}

	public static void addArtists(Collection<Artist> artists) {
		artists.forEach(Database::addArtist);
	}

	public static void addOnDataChangedListener(OnDataChangedListener l) {
		listeners.add(l);
	}

	public static void addPerformance(Performance performance) {
		performances.add(performance);
		addArtists(performance.getArtists());
		addStage(performance.getStage());
		notifyDataChanged("Added Performance: " + performance);
	}

	public static void addPerformances(Collection<Performance> performances) {
		performances.forEach(Database::addPerformance);
	}

	public static void addStage(Stage stage) {
		if (!stages.contains(stage)) {
			stages.add(stage);
			notifyDataChanged("Added Stage: " + stage);
		}
	}

	public static void addStages(Collection<Stage> stages) {
		stages.forEach(Database::addStage);
	}

	public static void clearPerformances() {
		performances.clear();
		artists.clear();
		notifyDataChanged("Cleared all Performances and Artists");
	}

	public static void clearStages() {
		stages.clear();
		notifyDataChanged("Cleared all stages");
	}

	public static List<Artist> getArtists() {
		return Collections.unmodifiableList(artists);
	}

	public static List<Performance> getPerformances() {
		return Collections.unmodifiableList(performances);
	}

	/**
	 *
	 * @return a list of all stages.
	 */
	public static List<Stage> getStages() {
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
	public static LocalTime findNextEmptyStageTime(Stage stage, Duration duration) {
		LocalTime nextTime = LocalTime.MIDNIGHT;
		for (Performance perf : performances) {
			if (perf.getStage().equals(stage)) {
				if (perf.getStartTime().isBefore(nextTime.plus(duration)))
					nextTime = perf.getEndTime();
			}
		}
		return nextTime;
	}

	public static boolean isStageInUse(Stage stage, LocalTime start, LocalTime end, Performance ignore) {
		return performances.stream().anyMatch(perf -> (ignore == null || !perf.equals(ignore)) &&
				perf.getStage().equals(stage) &&
				((perf.getStartTime().isAfter(start) && perf.getStartTime().isBefore(end)) ||
						(perf.getEndTime().isAfter(start) && perf.getEndTime().isBefore(end))));
	}

	public static void removeOnDataChangedListener(OnDataChangedListener l) {
		listeners.remove(l);
	}

	/**
	 * Removes the performance from the list of performances
	 *
	 * @param performance the performance that needs to be removed
	 * @return returns true if the performance is removed else it wil return false
	 */
	public static boolean removePerformance(Performance performance) {
		int numerOfPerformance = performances.indexOf(performance);
		if (performances.get(numerOfPerformance).equals(performance)) {
			performances.remove(numerOfPerformance);
			notifyDataChanged("Removed Performance: " + performance);
			return true;
		} else return false;
	}



	/**
	 * Notify all listeners that data in the database has changed.
	 */
	public static void notifyDataChanged() {
		notifyDataChanged(null);
	}

	/**
	 * Notify all listeners that data in the database has changed.
	 *
	 */
	public static void notifyDataChanged(String message) {
		System.out.println("Data changed: " + message);
		listeners.forEach(OnDataChangedListener::onDataChanged);
	}

	@FunctionalInterface
	public interface OnDataChangedListener {
		/**
		 * Called when something changed in the data. The type of change is unknown.
		 */
		void onDataChanged();
	}

	private static List<Target> targets;

	public static void addTargetsFromLayer(ObjectLayer layer, TileMap map) {
		targets = layer.parseAsTargets(map);
	}

	public static List<Target> getTargets(){
		return targets;
	}
}
