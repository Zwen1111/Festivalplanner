package festivalplanner.simulator.target;

import festivalplanner.simulator.data.CollisionLayer;
import festivalplanner.simulator.map.TileMap;

import java.awt.geom.Point2D;
import java.util.*;

/**
 * @author Coen Boelhouwers
 */
public abstract class Target {

	private String name;
	private int capacity;
	private int attendants;
	private Point2D position;
	private int[][] destinations;
	private TileMap map;
	private CollisionLayer data;
	private int startIndex;

	public Target(Point2D position) {
		this.position = position;
	}

	public void setupDistances(TileMap map) {
		this.map = map;
		this.data = map.getCollisionLayer();
		destinations = new int[data.getWidth()][data.getHeight()];
		for (int i = 0; i < destinations.length; i++) {
			Arrays.fill(destinations[i], -5);
		}
		startIndex = data.getIndex((int) Math.floor(position.getX() / map.getTileWidth()),
				(int) Math.floor(position.getY() / map.getTileHeight()));
		bfs();
	}

	protected void shareDistances(Target other) {
		this.map = other.map;
		this.data = other.data;
		this.destinations = other.destinations;
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
		int baseX = (int) Math.floor(base.getX() / map.getTileWidth());
		int baseY = (int) Math.floor(base.getY() / map.getTileHeight());
		int otherX = (int) Math.floor(other.getX() / map.getTileWidth());
		int otherY = (int) Math.floor(other.getY() / map.getTileHeight());
		return Math.abs(baseX - otherX) + Math.abs(baseY - otherY) <= 1;
		/*return (otherX == baseX && otherY == baseY) ||
				(otherX == baseX && otherY == baseY - 1) ||
				(otherX == baseX + 1 && otherY == baseY) ||
				(otherX == baseX && otherY == baseY + 1) ||
				(otherX == baseX - 1 && otherY == baseY);*/
	}

	public int getData(Point2D position) {
		return data.getData((int) Math.floor(position.getX() / map.getTileWidth()),
				(int) Math.floor(position.getY() / map.getTileHeight()));
	}

	public Distance getDistances(Point2D position) {
		return getDistances((int) Math.floor(position.getX() / map.getTileWidth()),
				(int) Math.floor(position.getY() / map.getTileHeight()));
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
		return getDistance((int) Math.floor(position.getX() / map.getTileWidth()),
				(int) Math.floor(position.getY() / map.getTileHeight()));
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
		Queue<Integer> queue = new LinkedList<>();
		Collection<Integer> checked = new ArrayList<>();

		logic(queue, checked, 0, startIndex, data.getData(startIndex));
		bfs(queue, checked, startIndex);

		while (queue.size() > 0) {
			bfs(queue, checked, queue.poll());
		}
	}

	private void bfs(Queue<Integer> q, Collection<Integer> checked, int cellIndex) {
		int cellX = cellIndex % data.getWidth();
		int cellY = Math.floorDiv(cellIndex, data.getWidth());
		int cellDistance = destinations[cellX][cellY];

		int northIndex = data.getIndex(cellX, cellY - 1);
		int eastIndex = data.getIndex(cellX + 1, cellY);
		int southIndex = data.getIndex(cellX, cellY + 1);
		int westIndex = data.getIndex(cellX - 1, cellY);

		int dataSize = data.getData().size();

		int northData = northIndex < 0 || northIndex >= dataSize ? -1 : data.getData(northIndex);
		int eastData = eastIndex < 0 || eastIndex >= dataSize ? -1 : data.getData(eastIndex);
		int southData = southIndex < 0 || southIndex >= dataSize ? -1 : data.getData(southIndex);
		int westData = westIndex < 0 || westIndex >= dataSize ? -1 : data.getData(westIndex);

		logic(q, checked, cellDistance, northIndex, northData);
		logic(q, checked, cellDistance, eastIndex, eastData);
		logic(q, checked, cellDistance, southIndex, southData);
		logic(q, checked, cellDistance, westIndex, westData);

		//Integer nextIndex = q.poll();
		//if (nextIndex != null) bfs(q, checked, nextIndex);
	}

	private void logic(Queue<Integer> q, Collection<Integer> checked, int fromDistance, int cellIndex, int cellData) {
		if (checked.contains(cellIndex)) return;
		//System.out.print("Check " + cellIndex + ": ");
		checked.add(cellIndex);
		switch (cellData) {
			case CollisionLayer.TOILET_TILE:
			case CollisionLayer.PATH_TILE:
				setCell(cellIndex, fromDistance + 1);
				q.offer(cellIndex);
				//System.out.println(fromDistance + 1);
				break;
			case CollisionLayer.WALL_TILE:
				setCell(cellIndex, -1);
				//System.out.println(-1);
				break;
			case CollisionLayer.STAGE_TILE:
				if (fromDistance == 0) setCell(cellIndex, fromDistance);
				else setCell(cellIndex, fromDistance + 1);
				q.offer(cellIndex);
				break;
			default:
				//System.out.println("x");
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
			int tw = map.getTileWidth();
			int th = map.getTileHeight();
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
