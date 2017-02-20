package festivalplanner.simulator.data;

import javax.json.*;
import javax.json.stream.JsonParser;
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

	public TileLayer(JsonObject layerJson) {
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
		System.out.print("");
	}

	public static void main(String[] args) {
		StringReader source = new StringReader("{\"heigth\":30, \"layers\":[{\"data\":[290, 380]}]}");
		JsonReader reader = Json.createReader(source);
		new TileLayer(reader.readObject().getJsonArray("layers").getJsonObject(0));
	}
}
