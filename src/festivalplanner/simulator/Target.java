package festivalplanner.simulator;

import festivalplanner.simulator.data.CollisionLayer;
import festivalplanner.simulator.map.TileMap;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @author Coen Boelhouwers
 */
public abstract class Target {

	private int capacity;
	private Point2D position;
	private int[][] destinations;
	private CollisionLayer data;
	private int startIndex;

	public Target(Point2D position, TileMap map) {
		this.position = position;
		data = map.getCollisionLayer();
		destinations = new int[data.getWidth()][data.getHeight()];
		startIndex = data.getIndex((int) Math.floor(position.getX() / map.getTileWidth()),
				(int) Math.floor(position.getY() / map.getTileHeight()));
		bfs();
	}

	private void setCell(int index, int value) {
		destinations[index % data.getWidth()][Math.floorDiv(index, data.getWidth())] = value;
	}

	public int getCell(int x, int y) {
		return destinations[x][y];
	}

	public CollisionLayer getLayer() {
		return data;
	}

	private void bfs() {
		Queue<Integer> queue = new LinkedList<>();
		Collection<Integer> checked = new ArrayList<>();

		logic(queue, checked, -1, startIndex, data.getData(startIndex));
		bfs(queue, checked, startIndex);
		System.out.println("Starting index's value: " + data.getData(startIndex));

		while (queue.size() > 0) {
			bfs(queue, checked, queue.poll());
		}
	}

	private void bfs(Queue<Integer> q, Collection<Integer> checked, int cellIndex) {
		int cellX = cellIndex % data.getWidth();
		int cellY = Math.floorDiv(cellIndex, data.getWidth());
		int cellDistance = getCell(cellX, cellY);

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
			case CollisionLayer.PATH_TILE:
				setCell(cellIndex, fromDistance + 1);
				q.offer(cellIndex);
				//System.out.println(fromDistance + 1);
				break;
			case CollisionLayer.WALL_TILE:
				setCell(cellIndex, -1);
				//System.out.println(-1);
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

	public int[][] getDestinations() {
		return destinations;
	}
}
