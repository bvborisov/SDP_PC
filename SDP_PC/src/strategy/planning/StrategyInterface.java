package strategy.planning;

import movement.RobotMover;
import vision.ObjectLocations;

public abstract class StrategyInterface implements Runnable {
	protected boolean shouldidie;

	ObjectLocations obj;
	RobotMover mover;

	public StrategyInterface(ObjectLocations obj, RobotMover mover) {
		this.shouldidie = false;
		this.obj = obj;
		this.mover = mover;
	}

	public void kill() {
		shouldidie = true;
		// Terminate any active movements
		// NOTE: does NOT tell the robot to stop, it only breaks any loops in
		// the mover
		try {
			mover.resetQueue();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		mover.interruptMove();
		try { // Sleep for a bit, because we want movement to die.
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}