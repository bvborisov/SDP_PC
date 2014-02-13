package strategy.movement;

import World.Robot;
import comms.RobotController;

import vision.ObjectLocations;
import World.RobotType;

public class MoveToPoint {

	private static final int distanceFromPointToStop = 20;

	public void moveToPoint(ObjectLocations objs, RobotController robot,
			int moveToX, int moveToY) throws InterruptedException {

		objs.setYellowUs(true);
		Robot us = new Robot(RobotType.Us);
		
		// Plan:
		// 0. Get bearings
		// 1. Turn to face ball
		// 2. Move forwards
		double distance = DistanceToBall.Distance(us.x, us.y, moveToX, moveToY);
		System.out.println(String.format("Distance to point is %f", distance));
		double angle = TurnToBall.AngleTurner(us, moveToX, moveToY);
		System.out.println(String
				.format("Angle of point to robot is %f", angle));

		if (Math.abs(angle) > 20) {
			// Turn to angle required
			System.out.println("Stop and turn");
			robot.stop();
			robot.rotateLEFT((int) angle);
		}

		while (distance > distanceFromPointToStop) {

			angle = TurnToBall.AngleTurner(us, moveToX, moveToY);

			if ((Math.abs(angle) > 15) && (Math.abs(angle) < 50)) {
				// Stop everything and turn
				System.out.println("The final angle is " + angle);
				robot.stop();
				robot.rotateLEFT((int) (angle / 2));
			} else if (Math.abs(angle) > 50) {
				robot.stop();
				robot.rotateLEFT((int) angle);
			}

			if ((distance - distanceFromPointToStop) > 100)
				robot.move(0, 100);
			else
				robot.move(0, 50);

			distance = DistanceToBall.Distance(us.x, us.y, moveToX, moveToY);
			System.out.println("Distance to ball: " + distance);
			Thread.sleep(100);
		}

		// Being close to the ball we can perform one last minor turn
		angle = TurnToBall.AngleTurner(us, moveToX, moveToY);
		if (Math.abs(angle) > 15) {
			// Stop everything and turn
			System.out.println("Making final correction");
			robot.stop();
			robot.rotateLEFT((int) angle);
		} else {
			robot.stop();
		}
		if (distance > distanceFromPointToStop) {
			moveToPoint(objs, robot, moveToX, moveToY);
		}
	}
	
}
