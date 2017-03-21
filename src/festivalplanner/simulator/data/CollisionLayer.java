package festivalplanner.simulator.data;

import javax.json.JsonObject;

/**
 * Dataholder for layers of the map.
 *
 * @author Coen Boelhouwers, Zwen van Erkelens
 */
public class CollisionLayer extends TileLayer {

	public static final int WALL_TILE = 2049;
	public static final int PATH_TILE = 2050;
	public static final int STAGE_TILE = 2051;
	public static final int TOILET_TILE = 2052;

	public CollisionLayer(JsonObject layerJson) {
		super(layerJson);
	}
}
