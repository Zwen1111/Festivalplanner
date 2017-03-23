package festivalplanner.gui.simulator;

import festivalplanner.data.Performance;
import festivalplanner.simulator.Navigator;
import festivalplanner.simulator.target.*;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.time.LocalTime;
import java.util.List;


/**
 * Created by Gebruiker on 6-3-2017.
 */
public class Visitor implements Serializable {

	private double speed;
	private double angle;
	private Point2D position;
	private Point2D destination;
	private Point2D newPosition;
	private Target target;
	private ToiletBlockTarget currentToiletBlock;
	private Performance currentPerformance;
	private double performanceRandom;

	private int imageId;
	private transient BufferedImage image;
	private int radius;


	private int timeAtTarget;

	private double peeSpeed;
	private double blather;

	private double hydration;

	private boolean remove;

	private CurrentAction currentAction;

	public Visitor(double speed, Point2D position, int id) {
		this.speed = speed;
		angle = 0.0;
		this.position = position;
		newPosition = position;
		//destination = new Point2D.Double(500, 500);
		//setTarget(Navigator.getDummyStage());
		radius = 10;
		imageId = id;

		currentAction = CurrentAction.IDLE;

		performanceRandom = Math.random() * 10;
		blather = Math.random();
		peeSpeed = (Math.random() * 5 + 5) / 1000;
		hydration = 1.0;
	}

	public void draw(Graphics2D g) {
		g.setColor(Color.RED);
		AffineTransform af = new AffineTransform();
		af.translate(position.getX(), position.getY());
		af.translate(image.getWidth() / 2, image.getHeight() / 2);
		af.rotate(angle);
		af.translate(-image.getWidth() / 2, -image.getHeight() / 2);
		g.drawImage(image, af, null);
	}

	public void drawDebugInfo(Graphics2D g) {
		String targetString = "Target: " + target.getClass().getSimpleName() + ": " + target.getPosition();
		int w = (int) g.getFontMetrics().getStringBounds(targetString, g).getWidth();
		g.setColor(currentAction == CurrentAction.ATTENDING_PERFORMANCE ? Color.MAGENTA : Color.WHITE);
		g.fillRect((int) position.getX() - 5, (int) position.getY() - 10, w + 10, 80);
		g.setColor(currentAction == CurrentAction.ATTENDING_PERFORMANCE ? Color.WHITE : Color.BLACK);
		g.drawString(targetString, (float) position.getX(), (float) position.getY());
		g.drawString("Action: " + currentAction, (float) position.getX(), (float) position.getY() + 20);
		if (currentAction == CurrentAction.ATTENDING_PERFORMANCE)
			g.drawString("On stage: " + currentPerformance.getArtists(),
					(float) position.getX(), (float) position.getY() + 40);
		g.drawString(String.format("Blather: %.0f%%, Hydra: %.0f%%", blather * 100, hydration * 100),
				(float) position.getX(), (float) position.getY() + 60);
	}

	private void drink() {
		blather += 0.2;
		hydration += 1.0;
		currentAction = CurrentAction.IDLE;
	}

	public Point2D getDestination() {
		return destination;
	}

	public int getImageId() {
		return imageId;
	}

	public Point2D getPosition() {
		return position;
	}

	public boolean getRemove() {
		return remove;
	}

	public Target getTarget() {
		return target;
	}

	private boolean isAtTarget(){
		if (target instanceof StageTarget){
			return target.getDistance(position) == 0;
		}
		return position.distance(target.getPosition()) < 20;
	}

	public boolean isDestinationSet() {
		return destination != null;
	}

	public boolean isTargetSet() {
		return target != null;
	}

	private void pee() {
		blather -= peeSpeed;
		newPosition = position;
		if (blather <= 0) {
			currentAction = CurrentAction.IDLE;
			blather = 0;
			currentToiletBlock.freeToilet((ToiletTarget) getTarget());
		}
	}

	public void setDestination(Point2D destination) {
		this.destination = destination;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public void setPosition(Point2D position) {
		this.position = position;
	}

	public void setTarget(Target newTarget) {
		target = newTarget;
	}

	public void update(LocalTime time) {
		if (destination == null) {
			target = Navigator.getDummyStage();
			newPosition = position;
			return;
		}

		double dx = destination.getX() - position.getX();
		double dy = destination.getY() - position.getY();

		double newAngle = Math.atan2(dy, dx);

		double difference = newAngle - angle;
		while (difference < -Math.PI)
			difference += 2 * Math.PI;
		while (difference > Math.PI)
			difference -= 2 * Math.PI;

		if (difference > 0.1)
			angle += 0.1;
		else if (difference < -0.1)
			angle -= 0.1;
		else
			angle = newAngle;

		newPosition = new Point2D.Double(
				position.getX() + speed * Math.cos(angle),
				position.getY() + speed * Math.sin(angle));
		updatePreferences(time);
	}

	private void updatePreferences(LocalTime time) {
		if (isAtTarget()) timeAtTarget++;
		hydration -= 0.00025;

		switch (currentAction) {
			case IDLE:
				timeAtTarget = 0;
				//Decide next action, in order of priority:
				if (time.getHour() < 6) {
					target = Navigator.getTargets().get(Navigator.getTargets().size() - 1);
					currentAction = CurrentAction.GOING_HOME;
					blather = 0;
					hydration = 1.0;
				} else if (blather >= 0.8) {
					currentAction = CurrentAction.GOING_TO_TOILET;
					setTarget(Navigator.getNearestToilet(position));
				} else if (hydration <= 0.2) {
					currentAction = CurrentAction.GOING_TO_STAND;
					setTarget(Navigator.getNearestStand(position));
				} else {
					PerformanceWrapper wrapper = Navigator.getPopularityBasedRandomStage(performanceRandom, time);
					if (wrapper == null) {
						currentAction = CurrentAction.GOING_TO_GRASS;
						setTarget(Navigator.getRandomEmptyStage(time));
					} else {
						StageTarget stageToGo = wrapper.getTarget();
						currentPerformance = wrapper.getPerformance();
						currentAction = CurrentAction.GOING_TO_PERMORMANCE;
						setTarget(stageToGo);
					}
				}
				break;
			case PEEING:
				if (position.distance(target.getPosition()) < 10) pee();
				break;
			case RESTING:
				if (timeAtTarget >= 300) {
					currentAction = CurrentAction.IDLE;
					target.changeAttendency(-1);
				}
				break;
			case ATTENDING_PERFORMANCE:
				if (time.isAfter(currentPerformance.getEndTime()) || timeAtTarget >= 50) {
					currentAction = CurrentAction.IDLE;
					target.changeAttendency(-1);
				}
				break;
			case GOING_TO_GRASS:
				if (timeAtTarget >= 1) {
					currentAction = CurrentAction.RESTING;
					target.changeAttendency(+1);
				}
				break;
			case GOING_TO_PERMORMANCE:
				if (timeAtTarget >= 1) {
					currentAction = CurrentAction.ATTENDING_PERFORMANCE;
					target.changeAttendency(+1);
				}
				break;
			case GOING_TO_STAND:
				if (timeAtTarget >= 1) {
					currentAction = CurrentAction.BUYING_DRINKS;
				}
				break;
			case GOING_TO_TOILET:
				if (timeAtTarget >= 1 && !target.isFull()) {
					currentToiletBlock = (ToiletBlockTarget) target;
					setTarget(((ToiletBlockTarget) target).useToilet());
					currentAction = CurrentAction.PEEING;
				}
				break;
			case BUYING_DRINKS:
				if (timeAtTarget >= 12) drink();
				break;
			case GOING_HOME:
				if (timeAtTarget >= 1) remove = true;
				break;
		}
	}

	//TODO: Move up to Simulator for optimizations?
	public boolean checkcollision(List<Visitor> visitors) {
		boolean collision = false;

		if (target != null) {
			if (target.getDistance(newPosition) < 0 || !target.isAdjacent(position, newPosition)) {
				collision = true;
			}
		}
		if (!collision) {
			for (Visitor v : visitors) {
				if (v == this)
					continue;

				if (v.position.distance(newPosition) < radius) {
					collision = true;
					break;
				}

			}
		}

		if (!collision) {
			position = newPosition;
		} else {
			angle += 0.2;
		}
		return collision;
	}

	public enum CurrentAction {
		IDLE,
		GOING_TO_PERMORMANCE,
		GOING_TO_GRASS,
		GOING_TO_TOILET,
		GOING_TO_STAND,
		GOING_HOME,
		ATTENDING_PERFORMANCE,
		RESTING,
		PEEING,
		BUYING_DRINKS
	}
}
