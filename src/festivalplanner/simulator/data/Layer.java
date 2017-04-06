package festivalplanner.simulator.data;

import javax.json.JsonObject;
import java.io.IOException;
import java.io.Serializable;

/**
 * Dataholder for layers of the map.
 *
 * @author Coen Boelhouwers, Zwen van Erkelens
 */
public abstract class Layer implements Serializable {

	public static final String TILE_LAYER_TYPE = "tilelayer";
	public static final String OBJECT_LAYER_TYPE = "objectgroup";
	public static final String COLLISION_LAYER_NAME = "ColisonPersonPlaceMent";

	private int x;
	private int y;
	private int width;
	private int height;
	private double alpha;
	private String title;

	public Layer(JsonObject layerJson) {
		x = layerJson.getInt("x");
		y = layerJson.getInt("y");
		width = layerJson.getInt("width");
		height = layerJson.getInt("height");
		alpha = layerJson.getInt("opacity");
		title = layerJson.getString("name");
	}

	public static Layer parseJson(JsonObject layerJson) throws UnsupportedLayerTypeException {
		String type = layerJson.getString("type");
		switch (type) {
			case TILE_LAYER_TYPE:
				String name = layerJson.getString("name");
				switch (name) {
					case COLLISION_LAYER_NAME:
						return new CollisionLayer(layerJson);
					default:
						return new TileLayer(layerJson);
				}
			case OBJECT_LAYER_TYPE:
				return new ObjectLayer(layerJson);
			default:
				throw new UnsupportedLayerTypeException("Unsupported layer type: " + type);
		}
	}

	public String getTitle() {
		return title;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public static class UnsupportedLayerTypeException extends IOException {

		public UnsupportedLayerTypeException(String message) {
			super(message);
		}
	}
}
