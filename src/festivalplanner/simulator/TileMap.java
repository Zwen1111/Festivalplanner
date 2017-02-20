package festivalplanner.simulator;

import festivalplanner.gui.simulator.TilesetManager;
import festivalplanner.simulator.data.TileLayer;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.awt.*;
import java.awt.geom.AffineTransform;
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

	private TilesetManager tilesetManager;
	private List<TileLayer> layers;

	public TileMap(String location) {
		try (FileReader source = new FileReader(location)) {
			JsonObject baseObject = Json.createReader(source).readObject();

			//Tile sets:
			tilesetManager = new TilesetManager(baseObject.getJsonArray("tilesets"));

			//Layers:
			JsonArray layerArray = baseObject.getJsonArray("layers");
			layers = new ArrayList<>(layerArray.size());
			for (int i = 0; i < layerArray.size(); i++) {
				try {
					layers.add(new TileLayer(layerArray.getJsonObject(i)));
				} catch (TileLayer.UnsupportedLayerTypeException e) {
					System.out.println("Some layer was of the wrong type:");
					e.printStackTrace();
				}
			}



		} catch (FileNotFoundException e) {
			System.err.println("Could not find file");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void draw(Graphics2D g) {
		//g.drawImage(tilesetManager.getImage(5),new AffineTransform(),null);
		//Test: draw layer 1:
		for (int i = 0; i < 5; i++) {
			TileLayer testLayer = layers.get(i);
			for (int x = 0; x < testLayer.getWidth(); x++) {
				for (int y = 0; y < testLayer.getHeight(); y++) {
					Image image = tilesetManager.getImage(testLayer.getData(y * testLayer.getWidth() + x));
					AffineTransform at = new AffineTransform();
					at.translate(x * 32 * 0.5, y * 32 * 0.5);
					at.scale(0.5, 0.5);
					g.drawImage(image, at, null);
				}
			}
		}
	}
}
