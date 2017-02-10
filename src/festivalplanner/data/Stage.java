package festivalplanner.data;

import java.io.Serializable;

/**
 * Dataholder for a single performance-stage.
 *
 * @author Björn Taks
 */
public class Stage implements Serializable{
	private String name;
	private int capacity;

	/**
	 * Creates a new unnamed stage with zero capacity.
	 */
	public Stage () {
		this(null, 0);
	}

	/**
	 * Creates a new stage with zero capacity.
	 * @param name the public name of this stage.
	 */
	public Stage(String name) {
		this(name, 0);
	}

	/**
	 * Creates a new stage with the specified capacity.
	 *
	 * @param name the public name of this stage.
	 * @param capacity the number of people that can attend a performance given on this stage.
	 */
	public Stage(String name, int capacity) {
		this.name = name == null || name.isEmpty() ? "Unnamed Stage" : name;
		if (capacity < 0) throw new IllegalArgumentException();
		else this.capacity = capacity;
	}

	/**
	 * Returns the number of people that can attend a performance given on this stage.
	 *
	 * @return the capacity of this stage.
	 */
	public int getCapacity() {
		return this.capacity;
	}

	/**
	 * Returns the public naming of this stage.
	 *
	 * @return the name of this stage.
	 */
	public String getName() {
		return this.name;
	}

	@Override
	public String toString() {
		return "Stage{" +
				"name='" + name + '\'' +
				", capacity=" + capacity +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o instanceof Stage) {
			Stage stage = (Stage) o;
			return name != null ? name.equals(stage.name) : stage.name == null;
		} else return false;
	}
}
