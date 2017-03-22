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
public class TileLayer extends Layer {

	private List<Integer> data;

	public TileLayer(JsonObject layerJson) {
		super(layerJson);
		JsonArray dataArray = layerJson.getJsonArray("data");
		data = new ArrayList<>(dataArray.size());
		for (int i = 0; i < dataArray.size(); i++) {
			data.add(dataArray.getInt(i));
		}
	}

	public List<Integer> getData() {
		return data;
	}

	public int getData(int index) {
		return data.get(index);
	}

	public int getData(int x, int y) {
		return getData(y * getWidth() + x);
	}

	public int getIndex(int x, int y) {
		return y * getWidth() + x;
	}
}
