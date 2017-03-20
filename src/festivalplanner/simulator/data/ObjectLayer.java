package festivalplanner.simulator.data;

import festivalplanner.data.Stage;
import festivalplanner.simulator.target.StageTarget;
import festivalplanner.simulator.target.StandTarget;
import festivalplanner.simulator.target.Target;
import festivalplanner.simulator.map.TileMap;
import festivalplanner.simulator.target.ToiletTarget;

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

	public static final String TYPE_STAGE = "stage";
	public static final String TYPE_TOILET = "toilet";
	public static final String TYPE_STAND = "stand";

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

	/**
	 * Parses this object-layer as a Target-layer and therefor tries to construct
	 * specific Targets (Toilets, Stages, ...) from the objects.
	 *
	 * @return a list of targets parsed.
	 * @throws ClassCastException if a expected field is not present.
	 */
	public List<Target> parseAsTargetLayer(TileMap map) {
		List<Target> parsedTargets = new ArrayList<>();
		for (int i = 0; i < objectArray.size(); i++) {
			JsonObject object = objectArray.getJsonObject(i);

			//Basic target values. Width and height used for centering.
			int targetX = object.getInt("x");
			int targetY = object.getInt("y");
			int targetWidth = object.getInt("width");
			int targetHeigth = object.getInt("height");
			String targetName = object.getString("name");
			String targetType = object.getString("type");

			//The capacity of a certain target is hidden in the additional 'properties' object.
			JsonObject properties = object.getJsonObject("properties");
			int targetCapacity = 0;
			if (properties != null) {
				targetCapacity = properties.getInt("capacity");
			}

			switch (targetType) {
				case TYPE_STAGE:
					parsedTargets.add(new StageTarget(new Point2D.Double(targetX + targetWidth / 2,
							targetY + targetHeigth / 2), new Stage(targetName, targetCapacity)));
					break;
				case TYPE_TOILET:
					parsedTargets.add(new ToiletTarget(new Point2D.Double(targetX + targetWidth / 2,
							targetY + targetHeigth / 2), targetCapacity));
					break;
				case TYPE_STAND:
					parsedTargets.add(new StandTarget(new Point2D.Double(targetX + targetWidth / 2,
							targetY + targetHeigth / 2), targetCapacity));
					break;
			}
		}
		return parsedTargets;
	}
}
