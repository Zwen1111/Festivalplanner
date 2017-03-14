package festivalplanner.simulator.data;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Dataholder for layers of the map.
 *
 * @author Coen Boelhouwers, Zwen van Erkelens
 */
public abstract class Layer {

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
			case "tilelayer":
				return new TileLayer(layerJson);
			case "objectgroup":
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
