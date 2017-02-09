package festivalplanner.data;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Dataholder for a singel performance featuring one or more Artists
 * on some Stage.
 *
 * @author Coen Boelhouwers, Björn Taks
 */
public class Performance implements Serializable {

	private Stage stage;
	private List<Artist> artists;
	private LocalTime startTime;
	private LocalTime endTime;

	/**
	 * Constructs a new performance featuring the specified artist(s).
	 *
	 * @param stage the Stage on which this performance takes place.
	 * @param startTime the Time at which the performance starts.
	 * @param endTime the Time at which the performance ends.
	 * @param artists the Artist(s) who will be performing.
	 */
	public Performance(Stage stage, LocalTime startTime, LocalTime endTime,
					   Artist... artists) {
		this(stage, startTime, endTime, Arrays.asList(artists));
	}

	/**
	 * Constructs a new performance featuring the specified artist(s).
	 *
	 * @param stage the Stage on which this performance takes place.
	 * @param startTime the Time at which the performance starts.
	 * @param duration the duration of the performance.
	 * @param artists the Artist(s) who will be performing.
	 */
	public Performance(Stage stage, LocalTime startTime, Duration duration,
					   Artist... artists) {
		this(stage, startTime, startTime.plus(duration), Arrays.asList(artists));
	}

	/**
	 * Constructs a new performance featuring the specified artist(s).
	 *
	 * @param stage the Stage on which this performance takes place.
	 * @param startTime the Time at which the performance starts.
	 * @param endTime the Time at which the performance ends.
	 * @param artists the Artist(s) who will be performing.
	 */
	public Performance(Stage stage, LocalTime startTime, LocalTime endTime,
					   Collection<Artist> artists) {
		this.stage = stage;
		this.startTime = startTime;
		this.endTime = endTime;
		this.artists = new ArrayList<>(artists);
		check();
	}

	private void check() {
		if (stage == null)
			throw new IllegalArgumentException("No stage supplied.");
		if (artists == null || artists.size() < 1)
			throw new IllegalArgumentException("A performance should have at least 1 artist.");
	}

	/**
	 * Calculates the popularity of this performance, based on the artist(s) performing as well
	 * as the time of day the performance starts (currently not implemented).
	 *
	 * @return the popularity of this performance, on a scale from 0-10.
	 */
	public int generatePopularity() {
		check();
		int artResult = artists.stream().mapToInt(Artist::getPopularity)
				.sum()/artists.size();
		//TODO Implement time-based influence.
		/*int start = startTime.getHour() * 60 + startTime.getMinute();
		if (start < SLEEP_TILL_VALUE)
		int timeResult = (int) ((start - SLEEP_TILL_VALUE)/1440.0*10);
		int result = artResult + timeResult / 2;
		return result;*/
		return artResult;
	}

	/**
	 * Returns the backing list of artists that will perform at this performance.
	 * When modifying, ensure to keep at least one artist in this list, otherwise
	 * an error will be thrown on the next operation.
	 *
	 * @return the list of artists.
	 */
	public List<Artist> getArtists() {
		return artists;
	}

	/**
	 * Returns a string containing the artists that will perform at this performance,
	 * separated by comma's.
	 *
	 * @return a string representation of the artists performing.
	 */
	public String getArtistNames() {
		check();
		StringBuilder builder = new StringBuilder(artists.get(0).getName());
		for (int i = 1; i < artists.size(); i++)
			builder.append(", ").append(artists.get(i).getName());
		return builder.toString();
	}

	/**
	 * Returns a string containing the genres of the individual artists that will perform at this performance,
	 * separated by comma's. This enumeration could contain duplicates.
	 *
	 * @return a string representation of the artists performing.
	 */
	public String getArtistGenres() {
		check();
		StringBuilder builder = new StringBuilder(artists.get(0).getGenre());
		for (int i = 1; i < artists.size(); i++)
			builder.append(", ").append(artists.get(i).getGenre());
		return builder.toString();
	}

	/**
	 * Returns the time this performance is scheduled to end.
	 * @return the end time.
	 */
	public LocalTime getEndTime() {
		return endTime;
	}

	/**
	 * Returns the stage on which this performance takes place. A performance is bound to a specific
	 * stage and this should therefore not be modified. Instead, create a new performance.
	 * @return
	 */
	public Stage getStage() {
		return stage;
	}

	/**
	 * Returns the time this performance is scheduled to begin.
	 * @return the starting time.
	 */
	public LocalTime getStartTime() {
		return startTime;
	}
}
