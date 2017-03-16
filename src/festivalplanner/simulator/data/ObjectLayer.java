package festivalplanner.simulator.data;

import festivalplanner.data.Stage;
import festivalplanner.simulator.Target;
import festivalplanner.simulator.map.SimpleTarget;
import festivalplanner.simulator.map.TileMap;
import festivalplanner.simulator.map.ToiletTarget;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Dataholder for layers of the map.
 *
 * @author Coen Boelhouwers, Zwen van Erkelens
 */
public class ObjectLayer extends Layer {

	private JsonArray objectArray;

	public ObjectLayer(JsonObject layerJson) {
		super(layerJson);
		objectArray = layerJson.getJsonArray("objects");
	}

	/**
	 * Parses this object-layer as a Stages-layer and therefor tries to construct
	 * Stages from the objects.
	 *
	 * @return a list of stages parsed.
	 * @throws ClassCastException if a expected field is not present.
	 */
	public List<Stage> parseAsStagesLayer() {
		List<Stage> parsedStages = new ArrayList<>();
		for (int i = 0; i < objectArray.size(); i++) {
			JsonObject object = objectArray.getJsonObject(i);
			String stageName = object.getString("name");
			JsonObject properties = object.getJsonObject("properties");
			int stageCapacity = 0;
			if (properties != null) properties.getInt("capacity");
			parsedStages.add(new Stage(stageName, stageCapacity));
		}
		return parsedStages;
	}

	public List<Target> parseAsTargets(TileMap map) {
		List<Target> targets = new ArrayList<>();
		for (int i = 0; i < objectArray.size(); i++) {
			JsonObject object = objectArray.getJsonObject(i);
			int x = object.getInt("x");
			int y = object.getInt("y");
			int width = object.getInt("width");
			int heigth = object.getInt("height");
			String stageName = object.getString("name");
			String stageType = object.getString("type");
			JsonObject properties = object.getJsonObject("properties");
			int capacity = 0;
			if (properties != null) {
				capacity = properties.getInt("capacity");
			}

			if(stageType.equals("stage")) {
				SimpleTarget st = new SimpleTarget(new Point2D.Double(x + width/2,y + heigth/2),map);
				st.setCapacity(capacity);
				targets.add(st);
			}else if(stageType.equals("toilet")){
				targets.add(new ToiletTarget(new Point2D.Double(x + width/2,y + heigth/2),map,capacity));
			}

		}
		return targets;
	}
}
