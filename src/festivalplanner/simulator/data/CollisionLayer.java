package festivalplanner.simulator.data;

import javax.json.JsonObject;

/**
 * Dataholder for layers of the map.
 *
 * @author Coen Boelhouwers, Zwen van Erkelens
 */
public class CollisionLayer extends TileLayer {

	public static final int WALL_TILE = 1;
	public static final int PATH_TILE = 2;
	public static final int STAGE_TILE = 3;

	public CollisionLayer(JsonObject layerJson) {
		super(layerJson);
	}
}
