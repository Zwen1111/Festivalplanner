package festivalplanner.gui.simulator;

import festivalplanner.data.Performance;
import festivalplanner.simulator.Navigator;
import festivalplanner.simulator.target.*;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;


/**
 * Created by Gebruiker on 6-3-2017.
 */
public class Visitor implements Serializable {

	private static final int NEAR_DISTANCE = 5;

	private double speed;
	private double angle;
	private Point2D position;
	private Point2D destination;
	private Point2D newPosition;
	private Target target;
	private ToiletBlockTarget currentToiletBlock;
	private Performance currentPerformance;
	private double performanceRandom;
	/**A random, visitor-specific time that the visitor will head towards a performance before it starts.*/
	private Duration preLookTime;

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
		preLookTime = Duration.ofMinutes((long) (Math.random() * 40 + 20));
		radius = 10;
		imageId = id;

		currentAction = CurrentAction.IDLE;

		performanceRandom = Math.random() * 10;
		blather = Math.random();
		peeSpeed = (Math.random() * 5 + 5) / 1000;
		hydration = Math.random();
	}

	/**
	 * Manages proper registration into and out of targets.
	 * WARNING: make sure the proper target is set before calling this method.
	 *
	 * @param newAction the new Action of this visitor.
	 */
	private void changeAction(CurrentAction newAction) {
		switch (newAction) {
			case IDLE:
				switch (currentAction) {
					case ATTENDING_PERFORMANCE:
					case RESTING:
					case PEEING:
					case BUYING_DRINKS:
						target.changeAttendency(-1);
						break;
				}
				break;
			case ATTENDING_PERFORMANCE:
			case RESTING:
			case PEEING:
			case BUYING_DRINKS:
				target.changeAttendency(+1);
				break;
		}
		currentAction = newAction;
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
		boolean attending = currentAction == CurrentAction.ATTENDING_PERFORMANCE;
		String targetString = "Target: " + target.getClass().getSimpleName() + ": " + target.getPosition();
		int w = (int) g.getFontMetrics().getStringBounds(targetString, g).getWidth();
		g.setColor(attending ? (currentPerformance != null ? Color.MAGENTA : Color.WHITE) : Color.WHITE);
		g.fillRect((int) position.getX() - 5, (int) position.getY() - 10, w + 10, 80);
		if (currentAction == CurrentAction.GOING_TO_PERMORMANCE || attending) {
			g.setColor(Color.MAGENTA);
			g.setStroke(new BasicStroke(4));
			g.drawRect((int) position.getX() - 5, (int) position.getY() - 10, w + 10, 80);
		}
		g.setColor(attending ? (currentPerformance != null ? Color.WHITE : Color.BLACK) : Color.BLACK);
		g.drawString(targetString, (float) position.getX(), (float) position.getY());
		g.drawString("Action: " + currentAction, (float) position.getX(), (float) position.getY() + 20);
		if (currentAction == CurrentAction.ATTENDING_PERFORMANCE && currentPerformance != null)
			g.drawString("On stage: " + currentPerformance.getArtists(),
					(float) position.getX(), (float) position.getY() + 40);
		g.drawString(String.format("Blather: %.0f%%, Hydra: %.0f%%", blather * 100, hydration * 100),
				(float) position.getX(), (float) position.getY() + 60);
	}

	private void drink() {
		blather += 0.2;
		hydration += 1.0;
		changeAction(CurrentAction.IDLE);
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

	private boolean isAtTarget() {
		return target.getDistance(position) == 0 ||
				position.distance(target.getPosition()) < 20;
	}

	public boolean isDestinationSet() {
		return destination != null;
	}

	private boolean isNearTarget() {
		return target.getDistance(position) < NEAR_DISTANCE;
	}

	public boolean isTargetSet() {
		return target != null;
	}

	private void pee() {
		blather -= peeSpeed;
		newPosition = position;
		if (blather <= 0) {
			changeAction(CurrentAction.IDLE);
			blather = 0;
			currentToiletBlock.freeToilet((ToiletTarget) getTarget());
		}
	}

	/**
	 * Intended for use by Simulator only.
	 *
	 * Resets this Visitor's current action to IDLE. Causes this visitor to unregister from its previous
	 * target if necessary.
	 */
	public void resetAction() {
		changeAction(CurrentAction.IDLE);
	}

	/**
	 * Intended for use by Simulator only.
	 *
	 * Replays this Visitor's current action as if it came from IDLE. Causes this visitor to re-register
	 * to its current target if necessary.
	 */
	public void replayAction() {
		CurrentAction action = currentAction;
		currentAction = CurrentAction.IDLE;
		changeAction(action);
	}

	private void searchTarget(Target ignore) {
		switch (currentAction) {
			case GOING_HOME:
				setTarget(Navigator.getTargets().get(Navigator.getTargets().size() - 1));
				break;
			case GOING_TO_TOILET:
				Target toilet = Navigator.getNearestToilet(position, ignore);
				if (toilet == null) {
					System.out.println("Could not find any toilet.");
					changeAction(CurrentAction.IDLE);
				} else setTarget(toilet);
				break;
			case GOING_TO_STAND:
				Target stand = Navigator.getNearestStand(position, ignore);
				if (stand == null) {
					System.out.println("Could not find any stand.");
					changeAction(CurrentAction.IDLE);
				} else setTarget(stand);
				break;
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
		hydration -= 0.00005;
		blather += 0.00005;

		switch (currentAction) {
			case IDLE:
				timeAtTarget = 0;
				//Decide next action, in order of priority:
				if (time.getHour() < 6) {
					changeAction(CurrentAction.GOING_HOME);
					blather = 0;
					hydration = 1.0;
				} else if (blather >= 0.8) {
					changeAction(CurrentAction.GOING_TO_TOILET);
				} else if (hydration <= 0.2) {
					changeAction(CurrentAction.GOING_TO_STAND);
				} else {
					PerformanceWrapper wrapper = Navigator.getPopularityBasedRandomStage(
							performanceRandom, time, preLookTime);
					if (wrapper == null) {
						changeAction(CurrentAction.GOING_TO_GRASS);
						setTarget(Navigator.getRandomEmptyStage(time, preLookTime));
					} else {
						StageTarget stageToGo = wrapper.getTarget();
						if (time.isAfter(wrapper.getPerformance().getStartTime()))
							currentPerformance = wrapper.getPerformance();
						changeAction(CurrentAction.GOING_TO_PERMORMANCE);
						setTarget(stageToGo);
					}
				}
				searchTarget(null);
				break;
			case PEEING:
				if (position.distance(target.getPosition()) < 10) pee();
				break;
			case RESTING:
				if (timeAtTarget >= 300) {
					changeAction(CurrentAction.IDLE);
				}
				break;
			case ATTENDING_PERFORMANCE:
				if ((currentPerformance != null &&  time.isAfter(currentPerformance.getEndTime())) ||
						timeAtTarget >= 50) {
					changeAction(CurrentAction.IDLE);
					currentPerformance = null;
				}
				break;
			case GOING_TO_GRASS:
				if (isNearTarget() && target.isFull())
					searchTarget(target);
				else if (timeAtTarget >= 1) {
					changeAction(CurrentAction.RESTING);
				}
				break;
			case GOING_TO_PERMORMANCE:
				//If the stage of his favorite artist is full, the visitor can't do much.
				if (isNearTarget() && target.isFull())
					changeAction(CurrentAction.IDLE);
				else if (timeAtTarget >= 1) {
					changeAction(CurrentAction.ATTENDING_PERFORMANCE);
				}
				break;
			case GOING_TO_STAND:
				if (isNearTarget() && target.isFull())
					searchTarget(target);
				else if (timeAtTarget >= 1) {
					changeAction(CurrentAction.BUYING_DRINKS);
				}
				break;
			case GOING_TO_TOILET:
				if (isNearTarget() && target.isFull())
					searchTarget(target);
				else if (timeAtTarget >= 1 && !target.isFull()) {
					currentToiletBlock = (ToiletBlockTarget) target;
					setTarget(((ToiletBlockTarget) target).useToilet());
					changeAction(CurrentAction.PEEING);
				}
				break;
			case BUYING_DRINKS:
				if (timeAtTarget >= 24) drink();
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
