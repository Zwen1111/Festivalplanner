package festivalplanner.simulator.map;

import festivalplanner.data.Database;
import festivalplanner.simulator.Navigator;
import festivalplanner.simulator.target.StageTarget;
import festivalplanner.simulator.target.Target;
import festivalplanner.simulator.data.CollisionLayer;
import festivalplanner.simulator.data.Layer;
import festivalplanner.simulator.data.ObjectLayer;
import festivalplanner.simulator.data.TileLayer;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Coen Boelhouwers, Zwen van Erkelens
 */
public class TileMap {

	private TilesetManager tilesetManager;
	private List<TileLayer> layers;
	private List<Target> targets;
	private CollisionLayer collisionLayer;
	private BufferedImage currentMap;
	private int mapHeight;
	private int mapWidth;
	private int tileHeight;
	private int tileWidth;

	public TileMap(String location) {
		try (InputStream source = getClass().getResourceAsStream(location)) {
			JsonObject baseObject = Json.createReader(source).readObject();

			//Tile sets:
			tilesetManager = new TilesetManager(baseObject.getJsonArray("tilesets"));

			//Tile height and width
			tileWidth = baseObject.getInt("tilewidth");
			tileHeight = baseObject.getInt("tileheight");

			//Layers:
			JsonArray layerArray = baseObject.getJsonArray("layers");
			layers = new ArrayList<>(layerArray.size());
			for (int i = 0; i < layerArray.size(); i++) {
				try {
					Layer layer = Layer.parseJson(layerArray.getJsonObject(i));
					if (layer instanceof CollisionLayer) {
						collisionLayer = ((CollisionLayer) layer);
					} else if (layer instanceof TileLayer) {
						layers.add((TileLayer) layer);
					} else if (layer instanceof ObjectLayer) {
						if (layer.getTitle().equals("Objects")) {
							targets = ((ObjectLayer) layer).parseAsTargetLayer(this);
							Database.clearStages();
							targets.forEach(target -> {
								if (target instanceof StageTarget)
									Database.addStage(((StageTarget) target).getStage());
							});
						}
					}
				} catch (TileLayer.UnsupportedLayerTypeException e) {
					System.out.println("Some layer was of the wrong type:");
					e.printStackTrace();
				}
			}

			//After parsing all layers, we can setup the targets using the collision-layer.
			//This prevents a crash if the objectLayer is parsed before the collisionLayer is.
			targets.forEach(target -> target.setupDistances(this));

		} catch (FileNotFoundException e) {
			System.err.println("Could not find file");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void buildMap() {
		if (layers.isEmpty()) {
			System.err.println("Could not build map: no layers.");
			return;
		}
		currentMap = new BufferedImage(layers.get(0).getWidth() * tileWidth,
				layers.get(0).getHeight() * tileHeight,
				BufferedImage.TYPE_INT_RGB);
		mapHeight = mapWidth = 0;

		for (int i = 0; i < layers.size(); i++) {
			TileLayer testLayer = layers.get(i);
			if (mapHeight < testLayer.getHeight()) mapHeight = testLayer.getHeight();
			if (mapWidth < testLayer.getWidth()) mapWidth = testLayer.getWidth();
			buildMap(currentMap, testLayer);
		}
	}

	private void buildMap(BufferedImage image, TileLayer layer) {
		Graphics2D g = image.createGraphics();
		for (int x = 0; x < layer.getWidth(); x++) {
			for (int y = 0; y < layer.getHeight(); y++) {
				Image tileImage = tilesetManager.getImage(layer.getData(x, y));
				AffineTransform at = new AffineTransform();
				at.translate(x * tileWidth,
						y * tileHeight);
				//at.scale(0.5, 0.5);
				g.drawImage(tileImage, at, null);
			}
		}
	}

	public CollisionLayer getCollisionLayer() {
		return collisionLayer;
	}

	public List<Target> getTargets() {
		return Collections.unmodifiableList(targets);
	}

	public Image getMapImage() {
		return currentMap;
	}

	/**
	 * Returns the actual map height, based on the amount of tiles and the tile's height.
	 * @return the map's height.
	 */
	public int getMapHeight() {
		return mapHeight * tileHeight;
	}

	/**
	 * Returns the actual map width, based on the amount of tiles and the tile's width.
	 * @return the map's width.
	 */
	public int getMapWidth() {
		return mapWidth * tileWidth;
	}

	/**
	 * Returns the height of a individual tile.
	 * @return a tile's height.
	 */
	public int getTileHeight() {
		return tileHeight;
	}

	/**
	 * Returns the width of a individual tile.
	 * @return a tile's width.
	 */
	public int getTileWidth() {
		return tileWidth;
	}
}
