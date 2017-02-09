package festivalplanner.data;

import java.io.Serializable;

/**
 * Dataholder for a single artist that can perform on stages.
 *
 * @author Coen Boelhouwers, Björn Taks
 */
public class Artist implements Serializable {
	private String name;
	private String genre;
	private int popularity;

	/**
	 * Creates a new unnamed artist of an unknown genre and a not yet determined popularity.
	 */
	public Artist() {
		this(null, null, 0);
	}

	/**
	 * Creates a new artist of an unknown genre and a not yet determined popularity.
	 * @param name the name of this artist.
	 */
	public Artist(String name) {
		this(name, null, 0);
	}

	/**
	 * Creates a new artist of the specified genre and popularity.
	 *
	 * @param name the name of this artist.
	 * @param genre the genre of this artist's sound.
	 * @param popularity the popularity [0-10] of this artist.
	 */
	public Artist(String name, String genre, int popularity) {
		this.name = name == null ? "Unnamed Artist" : name;
		this.genre = genre == null ? "Unknown Genre" : genre;
		if (popularity < 0 || popularity > 10)
			throw new IllegalArgumentException("Popularity (" + popularity + ") should be a value between 0 and 10");
		this.popularity = popularity;
	}

	/**
	 * Returns the genre of this artist's sound.
	 *
	 * @return the genre of this artist.
	 */
	public String getGenre() {
		return genre;
	}

	/**
	 * Returns the artist-name of this artist.
	 *
	 * @return the name of this artist.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the popularity of this artist. Popularity is measured on a scale from 0 to 10.
	 *
	 * @return the popularity [0-10] of this artist.
	 */
	public int getPopularity() {
		return popularity;
	}
}
