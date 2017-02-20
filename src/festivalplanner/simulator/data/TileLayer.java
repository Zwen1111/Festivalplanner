package festivalplanner.simulator.data;

import javax.json.*;
import javax.json.stream.JsonParser;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Dataholder for layers of the map.
 *
 * @author Coen Boelhouwers, Zwen van Erkelens
 */
public class TileLayer {

	private List<Integer> data;
	private int x;
	private int y;
	private int width;
	private int height;
	private double alpha;
	private String title;

	public TileLayer(JsonObject layerJson) throws UnsupportedLayerTypeException {
		String type = layerJson.getString("type");
		if (type.equals("tilelayer")) {
			JsonArray dataArray = layerJson.getJsonArray("data");
			data = new ArrayList<>(dataArray.size());
			for (int i = 0; i < dataArray.size(); i++) {
				data.add(dataArray.getInt(i));
			}
			x = layerJson.getInt("x");
			y = layerJson.getInt("y");
			width = layerJson.getInt("width");
			height = layerJson.getInt("height");
			alpha = layerJson.getInt("opacity");
			title = layerJson.getString("name");
		} else {
			throw new UnsupportedLayerTypeException("Unsupported layer type: " + type);
		}
	}

	public List<Integer> getData() {
		return data;
	}

	public int getData(int index) {
		return data.get(index);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public class UnsupportedLayerTypeException extends IOException {

		public UnsupportedLayerTypeException(String message) {
			super(message);
		}
	}
}
