package festivalplanner.gui.simulator;

import festivalplanner.simulator.Navigator;
import festivalplanner.simulator.target.*;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.time.LocalTime;
import java.util.List;


/**
 * Created by Gebruiker on 6-3-2017.
 */
public class Visitor {

	private static boolean debug = true;

	private double speed;
	private double angle;
	private Point2D position;
	private Point2D destination;
	private Point2D newPosition;
	private Target target;
	private ToiletBlockTarget currentToiletBlock;

	private BufferedImage image;
	private int radius;


	private int timeAtTarget;

	private double peeSpeed;
	private double blather;

	private double hydration;

	private boolean remove;

	private CurrentAction currentAction;

	public boolean getRemove() {
		return remove;
	}

	public Visitor(double speed, Point2D position, BufferedImage image) {
		this.speed = speed;
		angle = 0.0;
		this.position = position;
		newPosition = position;
		//destination = new Point2D.Double(500, 500);
		//setTarget(Navigator.getDummyStage());
		radius = 10;
		this.image = image;

		currentAction = CurrentAction.IDLE;

		blather = Math.random();
		peeSpeed = (Math.random() * 5 + 5) / 1000;
		hydration = 1.0;
		//blather = maxBlather;

	}

	public void draw(Graphics2D g) {
		g.setColor(Color.RED);
		AffineTransform af = new AffineTransform();
		af.translate(position.getX(), position.getY());
		af.translate(image.getWidth() / 2,image.getHeight() / 2);
		af.rotate(angle);
		af.translate(- image.getWidth() / 2, - image.getHeight() / 2);

		if (debug) {
			String targetString = "Target: " + target.getClass().getSimpleName() + ": " + target.getPosition();
			int w = (int) g.getFontMetrics().getStringBounds(targetString, g).getWidth();
			g.setColor(Color.WHITE);
			g.fillRect((int) position.getX() - 5, (int) position.getY() - 10, w + 10, 60);
			g.setColor(Color.BLACK);//isDummy ? Color.RED : Color.black);
			g.drawString(targetString, (float) position.getX(), (float) position.getY());
			g.setColor(Color.black);
			g.drawString("Action: " + currentAction, (float) position.getX(), (float) position.getY() + 20);
			g.drawString(String.format("Blather: %.0f%%, Hydra: %.0f%%", blather * 100, hydration * 100),
					(float) position.getX(), (float) position.getY() + 40);
		}
		g.drawImage(image, af, null);
	}

	public void setTarget(Target newTarget) {
		target = newTarget;
	}

	public boolean isTargetSet() {
		return target != null;
	}

	public Target getTarget() {
		return target;
	}

	public Point2D getPosition() {
		return position;
	}

	public void setPosition(Point2D position) {
		this.position = position;
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
		preferences(time);
	}

	private void preferences(LocalTime time) {
		if (isAtTarget()) timeAtTarget++;
		hydration -= 0.001;

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
					double coin = Math.random();
					currentAction = CurrentAction.GOING_TO_GRASS;
					setTarget(Navigator.getDummyStage());
				}
				break;
			case PEEING:
				if (position.distance(target.getPosition()) < 10) pee();
				break;
			case RESTING:
				if (timeAtTarget >= 600) currentAction = CurrentAction.IDLE;
				break;
			case ATTENDING_PERFORMANCE:
				if (timeAtTarget >= 600) currentAction = CurrentAction.IDLE;
				break;
			case GOING_TO_GRASS:
				if (timeAtTarget >= 1) {
					currentAction = CurrentAction.RESTING;
				}
				break;
			case GOING_TO_PERMORMANCE:
				if (timeAtTarget >= 1) {
					currentAction = CurrentAction.ATTENDING_PERFORMANCE;
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

			/*//peeing
			if(currentAction == CurrentAction.PEEING) {
				if(target !=  null && target instanceof ToiletTarget) {
					if(!((ToiletTarget) target).isFull() && !peeing) {
						((ToiletTarget) target).changeAttendency(+1);
						peeing = true;
					}
					newPosition = position;
				}
				pee();
			}else if(currentAction == CurrentAction.GOING_HOME && !hasToPee && !isThirsty){
				remove = true;
			}


			if (currentAction == CurrentAction.BUYING_DRINKS && timeAtTarget >= 12) {
				drink();
			} else if (currentAction == CurrentAction.ATTENDING_PERFORMANCE && timeAtTarget >= 600) {
				currentAction = CurrentAction.IDLE;
			}
		}


		//checks if the currentaction  = idle. if currentAction = idle then it wil select a random action
		if (currentAction == CurrentAction.IDLE && hasToPee) {
			currentAction = CurrentAction.PEEING;
			setTarget(Navigator.getNearestToilet(position));
		} else if (currentAction == CurrentAction.IDLE && isThirsty) {
			currentAction = CurrentAction.BUYING_DRINKS;
			target = Navigator.getNearestStand(position);
		}else if (currentAction == CurrentAction.IDLE) {
					timeAtTarget = 0;
					int action = (int) (Math.random() * 2);
					if (action < 0.1) {
						isThirsty = true;
					} else {
						currentAction = CurrentAction.ATTENDING_PERFORMANCE;
						List<PerformanceTarget> stageTargets = Navigator.getArtistPerformances(artists, time);
						if (stageTargets.size() > 0) {
							target = stageTargets.get((int) (Math.random() * stageTargets.size()));
							isDummy = false;
						} else {
							target = Navigator.getDummyStage();
							isDummy = true;
						}
					}
				}
		if(time.getHour() < 6) {
			target = Navigator.getTargets().get(Navigator.getTargets().size() - 1);
			currentAction = CurrentAction.GOING_HOME;
			isThirsty = false;
			hasToPee =false;
			peeing = false;
		}*/
	}

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
//			} else if (target.getDistances(position).getCenter() < 0) {
			//Respawn!
//				remove = true;
		} else {
			angle += 0.2;
		}
		//}
		return collision;
	}

	public Point2D getDestination() {
		return destination;
	}

	public boolean isDestinationSet() {
		return destination != null;
	}

    public void setDestination(Point2D destination) {
        this.destination = destination;
    }

    public void drink(){
        //isThirsty = false;
        //blather += 200;
		blather += 0.2;
		hydration += 1.0;
        currentAction = CurrentAction.IDLE;
		//if (blather >= maxBlather) {
		//	hasToPee = true;
		//}
    }

    public void pee() {
		blather -= peeSpeed;
		newPosition = position;
    	if (blather <= 0) {
			currentAction = CurrentAction.IDLE;
			blather = 0;
			currentToiletBlock.freeToilet((ToiletTarget) getTarget());
			//ToiletTarget currentToilet = (ToiletTarget) getTarget();
			//currentToilet.changeAttendency(-1);
		}
    }

    public boolean isAtTarget(){
    	if(target instanceof StageTarget){
    		if(target.getDistance(position) == 0){
    			return true;
			}else return false;
		}
        return position.distance(target.getPosition()) < 20;
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
