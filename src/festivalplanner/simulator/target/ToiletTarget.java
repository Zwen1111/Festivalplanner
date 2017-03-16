package festivalplanner.simulator.target;

import festivalplanner.simulator.map.TileMap;
import festivalplanner.simulator.target.Target;

import java.awt.geom.Point2D;

/**
 * Created by Maarten on 16/03/2017.
 */
public class ToiletTarget extends Target{

    private int currentCapacity;

    public ToiletTarget(Point2D position, int capacity) {
        super(position);
        setCapacity(capacity);
        currentCapacity = capacity;
    }

    public void use(){
        currentCapacity--;
        if(currentCapacity < 0) {
            currentCapacity = 0;
        }
    }

    public void done() {
        currentCapacity++;
        if(currentCapacity > getCapacity()) {
            currentCapacity = getCapacity();
        }
    }

    public boolean isFull(){
        return currentCapacity == 0;
    }
}
