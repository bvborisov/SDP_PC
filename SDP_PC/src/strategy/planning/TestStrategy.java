package strategy.planning;



import comms.Bluetooth;
import comms.BluetoothRobot;

import Calculations.DistanceCalculator;
import World.Ball;
import World.Robot;
import World.RobotType;
import World.WorldState;

public class TestStrategy extends StrategyInterface{

	private Robot ourAttackRobot;
	private WorldState World;
	private Ball ball;
	static BluetoothRobot bRobot;
	public static final String HERCULES = "0016530D4ED8";
	private static Bluetooth connection;
	
	public TestStrategy(WorldState world, BluetoothRobot bRobot) {
		super(world, bRobot);
		ball = world.ball;
		ourAttackRobot = world.getOurAttackRobot();
		}
	
	public void run() {
		while (!shouldidie && !Strategy.alldie) {
					
			int x1 = (int) ourAttackRobot.x;
			int y1 = (int) ourAttackRobot.y;
			
			int x2 = (int) ball.x;
			int y2 = (int) ball.y;

			double distance = DistanceCalculator.Distance(x1, y1, x2, y2);
			System.out.println(distance);
		}
	}
}
