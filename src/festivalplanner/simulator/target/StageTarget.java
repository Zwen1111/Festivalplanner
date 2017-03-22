package festivalplanner.simulator.target;

import festivalplanner.data.Stage;

import java.awt.geom.Point2D;

/**
 * @author Coen Boelhouwers
 */
public class StageTarget extends Target {

	private Stage stage;

	public StageTarget(Point2D position, Stage stage){
		super(position);
		this.stage = stage;
		setCapacity(stage.getCapacity());
	}

	public Stage getStage() {
		return stage;
	}

	@Override
	public String toString() {
		return "StageTarget{" +
				"stage=" + stage +
				'}';
	}
}
