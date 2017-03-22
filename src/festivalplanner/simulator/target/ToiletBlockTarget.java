package festivalplanner.simulator.target;

import festivalplanner.simulator.map.TileMap;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Coen Boelhouwers
 * @version 1.0
 */
public class ToiletBlockTarget extends Target {

	private List<ToiletTarget> toilets;

    public ToiletBlockTarget(String name, Point2D position) {
        super(position);
        setName(name);
        toilets = new ArrayList<>();
    }

	@Override
	public void setupDistances(TileMap map) {
    	super.setupDistances(map);
		toilets.forEach(toilet -> toilet.setupDistances(map));
	}

	public void addToilet(ToiletTarget box) {
    	if (box == null) return;
		if (toilets.add(box)) setCapacity(getCapacity() + box.getCapacity());
	}

	public void freeToilet(ToiletTarget toilet) {
		if (toilet != null) {
			toilet.changeAttendency(-1);
			this.changeAttendency(-1);
		}
	}

	public void removeToilet(ToiletTarget box) {
		if (box == null) return;
		if (toilets.remove(box)) setCapacity(getCapacity() - box.getCapacity());
	}

	public ToiletTarget useToilet() {
		ToiletTarget toilet = toilets.stream()
				.filter(Target::hasRoom)
				.findFirst()
				.orElse(null);
		if (toilet != null) {
			toilet.changeAttendency(+1);
			this.changeAttendency(+1);
			return toilet;
		} else {
			return null;
		}
	}
}
