package festivalplanner.simulator;

import festivalplanner.data.Stage;
import festivalplanner.simulator.map.TileMap;

import java.awt.geom.Point2D;

/**
 * @author Coen Boelhouwers
 */
public class StageTarget extends Target {

	private Stage stage;

	public StageTarget(Point2D position, TileMap map, Stage stage){
		super(position, map);
		this.stage = stage;
		setCapacity(stage.getCapacity());
	}

	public Stage getStage() {
		return stage;
	}
}
