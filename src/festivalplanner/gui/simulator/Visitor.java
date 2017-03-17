package festivalplanner.gui.simulator;

import festivalplanner.simulator.Navigator;
import festivalplanner.simulator.Simulator;
import festivalplanner.simulator.target.Target;
import festivalplanner.simulator.target.ToiletTarget;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Gebruiker on 6-3-2017.
 */
public class Visitor {

	private double speed;
	private double angle;
	private Point2D position;
	private Point2D destination;
	private Point2D newPosition;
	private Target target;

	private BufferedImage image;
	private int radius;


	private int timeAtTarget;

	private boolean hasToPee;
	private int blather;
	private int maxBlather;
	private List<ToiletTarget> fullToilets;

	private boolean isThirsty;

	private boolean remove;

	private CurrentAction currentAction;

	public boolean getRemove() {
		return remove;
	}


	public enum CurrentAction {
		IDLE, PEEING, WATCHING, BUYINGDRINKS,
	}


	public Visitor(double speed, Point2D position, BufferedImage image) {
		fullToilets = new ArrayList<>();
		this.speed = speed;
		angle = 0.0;
		this.position = position;
		//destination = new Point2D.Double(500, 500);
		//setTarget(Navigator.getDummyStage());
		radius = 10;
		this.image = image;

		currentAction = CurrentAction.IDLE;


		maxBlather = 1000;
		blather = (int) Math.random() * 500;
		hasToPee = false;

		//blather = maxBlather;



	}

	public void draw(Graphics2D g) {
		g.setColor(Color.RED);
		AffineTransform af = new AffineTransform();
		af.translate(position.getX(), position.getY());
		af.translate(image.getWidth() / 2,image.getHeight() / 2);
		af.rotate(angle);
		af.translate(- image.getWidth() / 2, - image.getHeight() / 2);
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

	public void update() {
		if (destination == null) {
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
		preferences();
	}

	private void preferences() {
		//if blather is full the visitor has to pee
		if (blather >= maxBlather) {
			hasToPee = true;
		}

		if (currentAction != CurrentAction.PEEING && hasToPee) {
			currentAction = CurrentAction.PEEING;
			//setTarget(Navigator.getNearestToilet(position));
			//destination = currentTarget.getPosition();
		} else if (currentAction != CurrentAction.BUYINGDRINKS && isThirsty) {
			currentAction = CurrentAction.BUYINGDRINKS;
			//setTarget(Navigator.getDummyStage());
			//destination = new Point2D.Double(1200, 900);
			//currentTarget = getNearestStand;
		}

		// checks howLong a visitor
		if (isAtTarget()) {
			timeAtTarget++;
			//if (destination.equals(new Point2D.Double(200, 900)))
			//	remove = true;
			//peeing
			//ToiletTarget currentToilet = Navigator.getNearestToilet(position);
			if(currentAction == CurrentAction.PEEING) {
				/*while (currentToilet != null && currentToilet.isFull() ) {
					if(currentToilet.isFull()) {
						fullToilets.add(currentToilet);
					}
					currentToilet = simulator.getNearestToiletExcept(position, fullToilets);
				}*/

				Target currentToilet = getTarget();
				if(currentToilet !=  null && currentToilet instanceof ToiletTarget) {
					((ToiletTarget) currentToilet).use();
					//currentTarget = currentToilet;
					//destination = currentTarget.getPosition();
				}else {
					currentAction = CurrentAction.IDLE;
				}
			}

			if (currentAction == CurrentAction.PEEING && timeAtTarget >= 30 ) {
				pee();
			} else if (currentAction == CurrentAction.BUYINGDRINKS && timeAtTarget >= 12) {
				drink();
			} else if (currentAction == CurrentAction.WATCHING && timeAtTarget >= 30) {
				currentAction = CurrentAction.IDLE;
			}// else if (currentAction != CurrentAction.IDLE)
			//	newPosition = position;
		}
		//checks if the currentaction  = idle. if currentAction = idle then it wil select a random action
		if (currentAction == CurrentAction.IDLE) {
			timeAtTarget = 0;
			int action = (int) (Math.random() * 2);
			switch (action) {
				case 0:
					isThirsty = true;
					break;
				case 1:
					currentAction = CurrentAction.WATCHING;
					//setTarget(Navigator.getDummyStage());
					//destination = new Point2D.Double(1000, 100);
                    /*currentTarget = random stage check if an performance is on the change

                    if so currentTarget is that stage
                    if not check rest of the changes else do nothing

                    //currentTarget = targets.get(Math.random * target.size - 1);
                    */
			}
		}


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
        isThirsty = false;
        blather += 200;
        currentAction = CurrentAction.IDLE;
    }

    public void pee(){
        hasToPee = false;
        currentAction = CurrentAction.IDLE;
        blather = 0;
        ToiletTarget currentToilet = (ToiletTarget) getTarget();
        currentToilet.done();
        fullToilets = new ArrayList<>();
        setTarget(null);
        //destination = new Point2D.Double(200,900);
    }

    public boolean isAtTarget(){
        return position.distance(destination) < 50;
    }

}
