package festivalplanner.simulator.target;

import festivalplanner.simulator.map.TileMap;
import festivalplanner.simulator.target.Target;

import java.awt.geom.Point2D;

/**
 * Created by Maarten on 16/03/2017.
 */
public class ToiletTarget extends Target{

    public ToiletTarget(Point2D position, int capacity) {
        super(position);
        setCapacity(capacity);
    }
}
