package festivalplanner.simulator;

import festivalplanner.gui.simulator.TilesetManager;
import festivalplanner.simulator.data.TileLayer;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Coen Boelhouwers, Zwen van Erkelens
 */
public class TileMap {

	private TilesetManager tilesetManager;
	private List<TileLayer> layers;
	private BufferedImage currentMap;

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

	public void buildMap(int levels) {
		if (layers.isEmpty()) {
			System.err.println("Could not build map: no layers.");
			return;
		}
		currentMap = new BufferedImage(layers.get(0).getWidth() * 32, layers.get(0).getHeight() * 32,
				BufferedImage.TYPE_INT_RGB);

		for (int i = 0; i < levels; i++) {
			TileLayer testLayer = layers.get(i);
			buildMap(currentMap, testLayer);
		}
	}

	public void buildMap(BufferedImage image, TileLayer layer) {
		Graphics2D g = image.createGraphics();
		for (int x = 0; x < layer.getWidth(); x++) {
			for (int y = 0; y < layer.getHeight(); y++) {
				Image tileImage = tilesetManager.getImage(layer.getData(y * layer.getWidth() + x));
				AffineTransform at = new AffineTransform();
				at.translate(x * 32, y * 32);
				//at.scale(0.5, 0.5);
				g.drawImage(tileImage, at, null);
			}
		}
	}

	public Image getMapImage() {
		return currentMap;
	}

	public void draw(Graphics2D g) {
		//g.drawImage(tilesetManager.getImage(5),new AffineTransform(),null);
		//Test: draw layer 1:
		//for (int i = 0; i < 5; i++) {
		//	TileLayer testLayer = layers.get(i);
		//}
	}
}
