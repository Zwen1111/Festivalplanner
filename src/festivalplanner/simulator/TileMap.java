package festivalplanner.simulator;

import festivalplanner.simulator.data.TileLayer;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Coen Boelhouwers, Zwen van Erkelens
 */
public class TileMap {

	private List<TileLayer> layers;

	public TileMap(String location) {
		try (FileReader source = new FileReader(location)) {
			JsonReader reader = Json.createReader(source);
			JsonArray layerArray = reader.readObject().getJsonArray("layers");
			layers = new ArrayList<>(layerArray.size());
			for (int i = 0; i < layerArray.size(); i++) {
				layers.add(new TileLayer(layerArray.getJsonObject(i)));
			}
			new TileLayer(reader.readObject().getJsonArray("layers").getJsonObject(0));
		} catch (FileNotFoundException e) {
			System.err.println("Could not find file");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Some IO Exception");
			e.printStackTrace();
		}

	}

	public void draw(Graphics2D g) {

	}
}
