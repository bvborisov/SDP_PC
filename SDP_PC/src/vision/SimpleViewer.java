package vision;

import georegression.metric.UtilAngle;
import georegression.struct.point.Point2D_I32;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import boofcv.alg.color.ColorHsv;
import boofcv.alg.filter.binary.BinaryImageOps;
import boofcv.alg.filter.binary.Contour;
import boofcv.alg.filter.binary.ThresholdImageOps;
import boofcv.core.image.ConvertBufferedImage;
import boofcv.gui.binary.VisualizeBinaryData;
import boofcv.gui.image.ShowImages;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageSInt32;
import boofcv.struct.image.ImageUInt8;
import boofcv.struct.image.MultiSpectral;
import au.edu.jcu.v4l4j.FrameGrabber;
import au.edu.jcu.v4l4j.CaptureCallback;
import au.edu.jcu.v4l4j.V4L4JConstants;
import au.edu.jcu.v4l4j.VideoDevice;
import au.edu.jcu.v4l4j.VideoFrame;
import au.edu.jcu.v4l4j.exceptions.StateException;
import au.edu.jcu.v4l4j.exceptions.V4L4JException;

/**
 * This class demonstrates how to perform a simple push-mode capture.
 * It starts the capture and display the video stream in a JLabel
 * @author bilyan, jason
 *
 */
public class SimpleViewer extends WindowAdapter implements CaptureCallback{
	private static int      width = 640, height = 480, std = V4L4JConstants.STANDARD_WEBCAM, channel = 0;
	private static String   device = "/dev/video0";
	private long lastFrame = System.currentTimeMillis(); // used for calculating FPS
	private int frameCounter = 0; // we let device capture frames from the 3rd onwards
	private static ArrayList<Polygon> regions = new ArrayList<Polygon>();

	private VideoDevice     videoDevice;
	private FrameGrabber    frameGrabber;

	private JLabel          label;
	private JFrame          frame;
	private static BufferedImage segOutputBall;
	private static List<Point2D_I32> ballPos;

	private static boolean lock = true;

	public static void main(String args[]){

		ObjectLocations.setYellowDefendingLeft(true);

		ObjectLocations.setYellowUs(true);
		try {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					new SimpleViewer();
				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("i failed miserably and now I must die to repent for my sins... ");
		}

	}

	/**
	 * Builds a WebcamViewer object
	 * @throws V4L4JException if any parameter if invalid
	 */
	public SimpleViewer(){
		// Initialise video device and frame grabber
		try {
			initFrameGrabber();
		} catch (V4L4JException e1) {
			System.err.println("Error setting up capture");
			e1.printStackTrace();

			// cleanup and exit
			cleanupCapture();
			return;
		}

		// create and initialise UI
		initGUI();

		// start capture
		try {
			frameGrabber.startCapture();
		} catch (V4L4JException e){
			System.err.println("Error starting the capture");
			e.printStackTrace();
		}
	}

	/**
	 * Initialises the FrameGrabber object
	 * @throws V4L4JException if any parameter if invalid
	 */
	private void initFrameGrabber() throws V4L4JException{
		videoDevice = new VideoDevice(device);
		frameGrabber = videoDevice.getJPEGFrameGrabber(width, height, channel, std, 80);
		frameGrabber.setCaptureCallback(this);
		width = frameGrabber.getWidth();
		height = frameGrabber.getHeight();
		System.out.println("Starting capture at "+width+"x"+height);
	}

	/** 
	 * Creates the UI components and initialises them
	 */
	private void initGUI(){
		frame = new JFrame();
		label = new JLabel();
		frame.getContentPane().add(label);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.addWindowListener(this);
		frame.setVisible(true);
		frame.setSize(width, height);
	}

	/**
	 * this method stops the capture and releases the frame grabber and video device
	 */
	private void cleanupCapture() {
		try {
			frameGrabber.stopCapture();
		} catch (StateException ex) {
			// the frame grabber may be already stopped, so we just ignore
			// any exception and simply continue.
		}

		// release the frame grabber and video device
		videoDevice.releaseFrameGrabber();
		videoDevice.release();
	}

	/**
	 * Catch window closing event so we can free up resources before exiting
	 * @param e
	 */
	public void windowClosing(WindowEvent e) {
		cleanupCapture();

		// close window
		frame.dispose();            
	}


	@Override
	public void exceptionReceived(V4L4JException e) {
		// This method is called by v4l4j if an exception
		// occurs while waiting for a new frame to be ready.
		// The exception is available through e.getCause()
		e.printStackTrace();
	}
	/**
	 * gets new frames and applies the vision on them
	 */
	@Override
	public void nextFrame(VideoFrame frame) {
		BufferedImage img = frame.getBufferedImage();
		img = img.getSubimage(50, 60, 550, 316);
		width = 550;
		height = 316;
		long thisFrame = System.currentTimeMillis();
		int frameRate = (int) (1000 / (thisFrame - lastFrame));

		lastFrame = thisFrame;
		try {
			if(frameCounter < 3){
				frameCounter++;
				frame.recycle();
				return;

			}
			else if (frameCounter == 3){
				frameCounter++;
				ObjectLocations.setRegions(img);
				frame.recycle();
				return;
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Graphics2D g = (Graphics2D) label.getGraphics();
		g.drawImage(img, 0, 0, width, height, null);
		g.setColor(Color.white);
		g.drawString("FPS " + frameRate , 10, 10);
		

		try {
			ObjectLocations.updateObjectLocations(img);
			ObjectLocations.drawCrosses(g);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		



		if (ballPos.size()>2)
			ballPos.clear();
		
		if (frameCounter > 1 && ballPos.size()==2) {
			if (obs.ball!=null) {
				//System.out.println(obs.ball);
				ballPos.add(obs.ball);
				try {
					System.out.println(ballPos.get(0).toString() + ballPos.get(1).toString()); //0 should be position in prev frame
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
//		//draw X over ball
//		if(obs.ball != null){
//			g.drawLine(obs.ball.getX() - 10, obs.ball.getY(), obs.ball.getX() +10, obs.ball.getY());
//			g.drawLine(obs.ball.getX() , obs.ball.getY() - 10, obs.ball.getX(), obs.ball.getY() + 10);
//		}
////		//draw X over yellow markers
//		if(obs.yellowMarkers != null){
//			for(int i = 0; i < obs.yellowMarkers.length; i++){
//				if(obs.yellowMarkers[i] != null){
//					g.drawLine(obs.yellowMarkers[i].getX() - 10, obs.yellowMarkers[i].getY(), obs.yellowMarkers[i].getX() +10, obs.yellowMarkers[i].getY());
//					g.drawLine(obs.yellowMarkers[i].getX() , obs.yellowMarkers[i].getY() - 10, obs.yellowMarkers[i].getX(), obs.yellowMarkers[i].getY() + 10);
//				}
//			}
//		}
//////		//draw X over yellow markers
//		if(obs.blueMarkers != null){
//			for(int i = 0; i < obs.blueMarkers.length; i++){
//				if(obs.blueMarkers[i] != null){
//					g.drawLine(obs.blueMarkers[i].getX() - 10, obs.blueMarkers[i].getY(), obs.blueMarkers[i].getX() +10, obs.blueMarkers[i].getY());
//					g.drawLine(obs.blueMarkers[i].getX() , obs.blueMarkers[i].getY() - 10, obs.blueMarkers[i].getX(), obs.blueMarkers[i].getY() + 10);
//				}
//			}
//		}
////		if(dotsPos != null){
////		g.drawLine(dotsPos[0].getX() - 10, dotsPos[0].getY(), dotsPos[0].getX() +10, dotsPos[0].getY());
////		g.drawLine(dotsPos[0].getX() , dotsPos[0].getY() - 10, dotsPos[0].getX(), dotsPos[0].getY() + 10);
////
////		g.drawLine(dotsPos[1].getX() - 10, dotsPos[1].getY(), dotsPos[1].getX() +10, dotsPos[1].getY());
////		g.drawLine(dotsPos[1].getX() , dotsPos[1].getY() - 10, dotsPos[1].getX(), dotsPos[1].getY() + 10);
////	}
//		// recycle the frame

		frame.recycle();
	}



}
