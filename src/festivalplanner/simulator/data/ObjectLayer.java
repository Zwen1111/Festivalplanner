package festivalplanner.simulator.data;

import festivalplanner.data.Stage;

import javax.json.JsonArray;
import javax.json.JsonObject;
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
}
