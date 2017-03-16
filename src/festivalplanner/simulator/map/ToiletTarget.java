package festivalplanner.simulator.map;

import festivalplanner.simulator.Target;

import java.awt.geom.Point2D;

/**
 * Created by Maarten on 16/03/2017.
 */
public class ToiletTarget extends Target{

    private int currentCapacity;
    public ToiletTarget(Point2D position, TileMap map,int capacity) {
        super(position, map);
        this.capacity = capacity;
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
        if(currentCapacity > capacity) {
            currentCapacity = capacity;
        }
    }

    public boolean isFull(){
        return currentCapacity == 0;
    }
}
