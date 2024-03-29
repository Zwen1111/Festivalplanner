package festivalplanner.simulator.target;

import festivalplanner.simulator.data.CollisionLayer;
import festivalplanner.simulator.map.TileMap;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.*;

/**
 * @author Coen Boelhouwers
 */
public abstract class Target implements Serializable {

	private String name;
	private int capacity;
	private int attendants;
	private Point2D position;
	private int[][] destinations;
	private int tileHeight;
	private int tileWidth;
	private CollisionLayer data;
	private int startIndex;

	private Queue<Integer> queue;
	private Collection<Integer> checked;

	public Target(Point2D position) {
		this.position = position;
	}

	public void setupDistances(TileMap map) {
		tileHeight = map.getTileHeight();
		tileWidth = map.getTileWidth();
		this.data = map.getCollisionLayer();
		destinations = new int[data.getWidth()][data.getHeight()];
		for (int i = 0; i < destinations.length; i++) {
			Arrays.fill(destinations[i], -5);
		}
		startIndex = data.getIndex((int) Math.floor(position.getX() / tileWidth),
				(int) Math.floor(position.getY() / tileHeight));
		bfs();
	}

	public String getName() {
		return name;
	}

	public void setName(String value) {
		name = value;
	}

	private void setCell(int index, int value) {
		destinations[index % data.getWidth()][Math.floorDiv(index, data.getWidth())] = value;
	}

	public boolean isAdjacent(Point2D base, Point2D other) {
		int baseX = (int) Math.floor(base.getX() / tileWidth);
		int baseY = (int) Math.floor(base.getY() / tileHeight);
		int otherX = (int) Math.floor(other.getX() / tileWidth);
		int otherY = (int) Math.floor(other.getY() / tileHeight);
		return Math.abs(baseX - otherX) + Math.abs(baseY - otherY) <= 1;
		/*return (otherX == baseX && otherY == baseY) ||
				(otherX == baseX && otherY == baseY - 1) ||
				(otherX == baseX + 1 && otherY == baseY) ||
				(otherX == baseX && otherY == baseY + 1) ||
				(otherX == baseX - 1 && otherY == baseY);*/
	}

	public int getData(Point2D position) {
		return data.getData((int) Math.floor(position.getX() / tileWidth),
				(int) Math.floor(position.getY() / tileHeight));
	}

	public Distance getDistances(Point2D position) {
		return getDistances((int) Math.floor(position.getX() / tileWidth),
				(int) Math.floor(position.getY() / tileHeight));
	}

	public Distance getDistances(int x, int y) {
		//System.out.println("New Distance object of " + x + ", " + y);
		return new Distance(x, y,
				getDistance(x, y),
				getDistance(x, y-1),
				getDistance(x+1, y),
				getDistance(x, y+1),
				getDistance(x-1, y));
	}

	public int getDistance(Point2D position) {
		return getDistance((int) Math.floor(position.getX() / tileWidth),
				(int) Math.floor(position.getY() / tileHeight));
	}

	public int getDistance(int x, int y) {
		return x >= 0 && x < data.getWidth() && y >= 0 && y < data.getHeight() ?
				destinations[x][y] : -1;
	}

	/**
	 * Changes the amount of current visitors by the specified amount.
	 * @param amount change value. Negative amount allowed (frees up space).
	 */
	public void changeAttendency(int amount) {
		attendants += amount;
		if (attendants < 0) attendants = 0;
		if (attendants > capacity) attendants = capacity;
	}

	public int getAttendants() {
		return attendants;
	}

	public boolean hasRoom() {
		return attendants < capacity;
	}

	public boolean isFull() {
		return attendants >= capacity;
	}

	public CollisionLayer getLayer() {
		return data;
	}

	private void bfs() {
		queue = new LinkedList<>();
		checked = new ArrayList<>();

		logic(-1, 0, startIndex, data.getData(startIndex));
		bfs(startIndex);

		while (queue.size() > 0) {
			bfs(queue.poll());
		}
	}

	private void bfs(int centerIndex) {
		int centerX = centerIndex % data.getWidth();
		int centerY = Math.floorDiv(centerIndex, data.getWidth());
		int centerDistance = destinations[centerX][centerY];

		int northIndex = data.getIndex(centerX, centerY - 1);
		int eastIndex = data.getIndex(centerX + 1, centerY);
		int southIndex = data.getIndex(centerX, centerY + 1);
		int westIndex = data.getIndex(centerX - 1, centerY);

		int dataSize = data.getData().size();

		int centerData = data.getData(centerX, centerY);
		int northData = northIndex < 0 || northIndex >= dataSize ? -1 : data.getData(northIndex);
		int eastData = eastIndex < 0 || eastIndex >= dataSize ? -1 : data.getData(eastIndex);
		int southData = southIndex < 0 || southIndex >= dataSize ? -1 : data.getData(southIndex);
		int westData = westIndex < 0 || westIndex >= dataSize ? -1 : data.getData(westIndex);

		logic(centerData, centerDistance, northIndex, northData);
		logic(centerData, centerDistance, eastIndex, eastData);
		logic(centerData, centerDistance, southIndex, southData);
		logic(centerData, centerDistance, westIndex, westData);
	}

	private void logic(int fromData, int fromDistance, int newIndex, int newData) {
		if (checked.contains(newIndex)) return;
		switch (newData) {
			case CollisionLayer.TOILET_TILE:
				setCell(newIndex, fromDistance + 1);
				queue.offer(newIndex);
				checked.add(newIndex);
				break;
			case CollisionLayer.PATH_TILE:
				//Prevent walking from a path over a stage (except when target).
				//Keep it undiscovered to find a way around.
				if (!(fromData == CollisionLayer.STAGE_TILE && fromDistance > 0)) {
					setCell(newIndex, fromDistance + 1);
					queue.offer(newIndex);
					checked.add(newIndex);
				}
				break;
			case CollisionLayer.WALL_TILE:
				setCell(newIndex, -1);
				checked.add(newIndex);
				break;
			case CollisionLayer.STAGE_TILE:
				if (fromDistance == 0) setCell(newIndex, fromDistance);
				else setCell(newIndex, fromDistance + 1);
				queue.offer(newIndex);
				checked.add(newIndex);
				break;
		}
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public Point2D getPosition() {
		return position;
	}

	public void setPosition(Point2D position) {
		this.position = position;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Target target = (Target) o;
		return capacity == target.capacity &&
				Objects.equals(name, target.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, capacity);
	}

	public class Distance {
		private int x;
		private int y;
		private int center;
		private int north;
		private int east;
		private int south;
		private int west;

		private Distance(int x, int y, int center, int north, int east, int south, int west) {
			this.x = x;
			this.y = y;
			this.center = center;
			this.north = north;
			this.east = east;
			this.south = south;
			this.west = west;
		}

		public int getCenter() {
			return center;
		}

		public Point2D getCenterPoint() {
			return new Point2D.Double(x, y);
		}

		private Point2D getPoint(int x, int y) {
			int tw = tileWidth;
			int th = tileHeight;
			return new Point2D.Double((x * tw) + (tw / 2), (y * th) + (th / 2));
		}

		public int getNorth() {
			return north;
		}

		public Point2D getNorthPoint() {
			return getPoint(x, y - 1);
		}

		public int getEast() {
			return east;
		}

		public Point2D getEastPoint() {
			return getPoint(x + 1, y);
		}

		public int getSouth() {
			return south;
		}

		public Point2D getSouthPoint() {
			return getPoint(x, y + 1);
		}

		public int getWest() {
			return west;
		}

		public Point2D getWestPoint() {
			return getPoint(x - 1, y);
		}
	}
}
