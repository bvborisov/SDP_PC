package robot;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lejos.nxt.LCD;
import lejos.nxt.Button;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.Motor;
import lejos.robotics.navigation.DifferentialPilot;

public class Attacker {
	// IO control
	private static InputStream dis;
	private static OutputStream dos;

	// Commands
	private final static int NOTHING = 0;
	private final static int FORWARDS = 1;
	private final static int BACKWARDS = 2;
	private final static int STOP = 3;
	private final static int GRAB = 4;
	private final static int KICK = 5;
	private final static int SPEED = 6;
	//private final static int ROTATESPEED = 7;
	private final static int ROTATELEFT = 7;
	private final static int ROTATERIGHT = 8;
	private final static int MOVING = 9;
	private final static int QUIT = 10;
	
	private final static DifferentialPilot pilot = new DifferentialPilot(22.2, 16.2, Motor.B, Motor.A, false);

	public static void main(String [] args)  throws Exception {

			while (true) {
				try {
				//Wait for Bluetooth connection
				LCD.drawString("Waiting for connection...",0,0);
				BTConnection btc = Bluetooth.waitForConnection();

				//Show connected and open data streams
				LCD.clear();
				LCD.drawString("Connected!", 0, 0);
				dis = btc.openInputStream();
				dos = btc.openOutputStream();

				//Send PC ready state code
				byte[] readyState = { 0, 0, 0, 0 };
				dos.write(readyState);
				dos.flush();

				//Set initial command and initialise options
				int command = NOTHING;
				int option1, option2, option3;

				while ((command != QUIT) && !(Button.ENTER.isDown())) {
					// Get command from the input stream
					byte[] byteBuffer = new byte[4];
					dis.read(byteBuffer);

					// We receive 4 numbers
					command = byteBuffer[0];
					option1 = byteBuffer[1];
					option2 = byteBuffer[2];
					option3 = byteBuffer[3];

					//Based on the received command call the relevant method
					switch(command) {
						case FORWARDS:
							LCD.clear();
							LCD.drawString("Forwards!", 0, 0);
							forwards(option1, option2);
							break;
						case BACKWARDS:
							LCD.clear();
							LCD.drawString("BACKWARDS!", 0, 0);
							backwards();
							break;				
						case STOP:
							LCD.clear();
							LCD.drawString("STOP!", 0, 0);
							stop();
							break;		
						case GRAB:
							LCD.clear();
							LCD.drawString("GRAB!", 0, 0);
							grab();
							break;
						case KICK:
							LCD.clear();
							LCD.drawString("KICK!", 0, 0);
							kick();
							break;			
						case SPEED:
							LCD.clear();
							LCD.drawString("SPEED SET!", 0, 0);
							setSpeed(option1);
							break;
						/*case ROTATESPEED:
							LCD.clear();
							LCD.drawString("SPEED SET!", 0, 0);
							setRotateSpeed(option1);
							break;
						*/case ROTATELEFT:
							LCD.clear();
							LCD.drawString("ROTATE LEFT!", 0, 0);
							rotateLeft(option1, option2, option3);
							break;	
						case ROTATERIGHT:
							LCD.clear();
							LCD.drawString("ROTATE RIGHT!", 0, 0);
							rotateRight(option1, option2, option3);
							break;
						case MOVING:
							LCD.clear();
							LCD.drawString("MOVING?", 0, 0);
							moving();
							break;
						case QUIT:
							LCD.clear();
							LCD.drawString("QUIT!", 0, 0);
							//Add call to FORWARDS method
							break;						
						default:
					}
				}
				//Now close data streams and connection
				dis.close();
				dos.close();
				Thread.sleep(100);
				LCD.clear();
				LCD.drawString("Closing connection", 0, 0);
				btc.close();
				}
			    catch (Exception e) {
				LCD.drawString("Exception:", 0, 2);
				String msg = e.getMessage();
				if (msg != null)
					LCD.drawString(msg, 2, 3);
				else
					LCD.drawString("Error message is null", 2, 3);
			}
	 	}
	}
			
	
	public static void forwards(int distance, int distance2) throws Exception {
		pilot.travel(distance + distance2, false);
		done();
	}

	public static void backwards() throws Exception {
		pilot.backward();
		done();
	}
	
	public static void stop() throws Exception {
		pilot.stop();
		done();
	}
	
	public static void grab() throws Exception {
		Motor.C.setSpeed(40);
		Motor.C.rotate(-90);
		done();
	}
	
	public static void kick() throws Exception {
		Motor.C.setSpeed(900);
		Motor.C.rotate(120);
		rest();
		done();
	}
	
	public static void rest() throws Exception {
		Motor.C.setSpeed(40);
		Motor.C.rotate(-10);
		done();
	}
	
	public static void setSpeed(int speed) throws Exception {
		pilot.setTravelSpeed(speed);
		done();
	}
	
	public static void setRotateSpeed(int speed) throws Exception {
		pilot.setRotateSpeed(speed);
		done();
	}
	
	public static void rotateLeft(double angle1, double angle2, double angle3) throws Exception {
		pilot.setRotateSpeed(10);
		pilot.rotate(angle1 + angle2 + angle3, false);
		done();
	}
	public static void rotateRight(double angle1, double angle2, double angle3) throws Exception {
		double turned = 0.0;
		while (turned < angle1 + angle2 + angle3) {
			pilot.rotateRight();
			turned += pilot.getAngleIncrement();
		}
		done();
	}
	
	public static void moving() throws Exception {
		byte[] moving = {0, 0, 0, 0};
		if (pilot.isMoving()) {
			moving[3]= (byte)1;
			dos.write(moving);
			dos.flush();
		} else {
			dos.write(moving);
			dos.flush();
		}
		done();
	}
	
	public static void done() throws Exception {
		byte[] done = {12, 0, 0, 0};
	    dos.write(done);
	    dos.flush();
	}
}