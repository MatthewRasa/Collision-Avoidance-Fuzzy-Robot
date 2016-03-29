package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.Stack;

/**
 * JFrame to display the GUI.
 */
public class Grid extends JFrame implements Runnable {
	
	/**
	 * WIDTH - Screen width
	 * HEIGHT - Screen height
	 * COLOR_BLANK - RGB component of blank space
	 * COLOR_OBS - RGB component of obstruction
	 * REFRESH_RATE - Rate to refresh the GUI
	 */
	private static final int SCREEN_WIDTH = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(),
	                         SCREEN_HEIGHT = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight(),
	                         COLOR_BLANK = Color.WHITE.getRGB(),
	                         COLOR_OBS = Color.GRAY.getRGB(),
	                         REFRESH_RATE = 1000 / 60,
	                         REVERSE_DELAY = 50,
	                         SPEED = 1;
	
	/**
	 * Bound a value between the set min and max.
	 *
	 * @param min - minimum bound
	 * @param val - value to bound
	 * @param max - maximum bound
	 * @return the value bounded between the min and max
	 */
	private static int bound(int min, int val, int max) {
		return val < min ? min :
		       val > max ? max : val;
	}

	/**
	 * Draw a block at the specified coordinates.
	 *
	 * @param img - image to draw block on
	 * @param x - grid x-coordinate
	 * @param y - grid y-coordinate
	 * @param size - size of block
	 * @param rgb - RGB component of block
	 */
	private static void drawBlock(BufferedImage img, int x, int y, int size, int rgb) {
		x *= size;
		y *= size;
		for (int yi = 0; yi < size; yi++) {
			for (int xi = 0; xi < size; xi++)
				img.setRGB(x + xi, y + yi, rgb);
		}
	}

	/**
	 * @return the lesser of the two values
	 */
	private static int useLesser(int val0, int val1) {
		return val0 < val1 ? val0 : val1;
	}
	
	/**
	 * mGrid - grid image
	 * mRotation - angle of rotation of robot
	 * mX - x-coordinate of robot
	 * mY - y-coordinate of robot
	 * mBlockSize - size of grid blocks, calculated at runtime
	 * mRunning - true if the GUI is still running
	 */
	private BufferedImage mGrid;
	private String mVisionData, mVisionDataReduced;
	private double mX, mY, mRadius, mRotation, mRevX, mRevY;	
	private int[][] mGridData;
	private int mBlockSize, mReversing;
	private boolean mDebug, mRunning;

	/**
	 * Create a new Grid with the specified grid data.
	 *
	 * @param gridData - grid data to display in GUI
	 */
	public Grid(int[][] gridData, boolean debug) {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setContentPane(new DrawingPanel());
		setSize(SCREEN_WIDTH, SCREEN_HEIGHT);	

		// Instantiate members
		mGridData = gridData;
		mVisionData = mVisionDataReduced = "";
		mBlockSize = useLesser(SCREEN_WIDTH / gridData[0].length, SCREEN_HEIGHT / gridData.length);
		mX = mY = mBlockSize;
		mRadius = mBlockSize / 2;
		mRotation = mRevX = mRevY = 0;	
		mReversing = 0;
		mDebug = debug;
		mRunning = true;

		// Create grid image
		mGrid = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_INT_ARGB);
		for (int row = 0; row < gridData.length; row++) {
			for (int col = 0; col < gridData[0].length; col++)
				drawBlock(mGrid, col, row, mBlockSize, gridData[row][col] == 0 ? COLOR_BLANK : COLOR_OBS);
		}

		// Add listener for JFrame close	
		addWindowListener(new java.awt.event.WindowAdapter() {
    			@Override
    			public void windowClosing(WindowEvent e) {
				mRunning = false;
        		}
		});	

		setVisible(true);

		new Thread(this).start();
	}

	/**
	 * @return true if the GUI is still running.
	 */
	public boolean isRunning() {
		return mRunning;
	}
	
	/**
	 * Reduce an array by averaging its subarrays of size 2, then working
	 * upwards with larger subarrays.
	 *
	 * @param data - array to reduce
	 * @return the reduce array of size 2
	 */
	public int[] reduce(int[] data) {
		Stack<Integer> stk = new Stack<Integer>();

		for (int i = 0; i < data.length; i++)
			stk.push(data[i]);

		while (stk.size() > 2) {
			Stack<Integer> tmpStk = new Stack<Integer>();
			int count = stk.size() / 2;
			while (count > 0) {
				tmpStk.push(((stk.pop() + stk.pop()) / 2));
				count--;
			}
			stk = tmpStk;
		}
		
		int[] toReturn = new int[2];
		mVisionDataReduced = "[ ";
		mVisionDataReduced += (toReturn[0] = stk.pop()) + " ";
		mVisionDataReduced += (toReturn[1] = stk.pop()) + " ";
		mVisionDataReduced += "]";
		return toReturn;
	}
	
	/**
	 * Refresh the application at the REFRESH_RATE.
	 */
	@Override
	public void run() {
		while(mRunning) {	
			repaint();
			move();
			try {
				Thread.sleep(REFRESH_RATE);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}	
	}

	/**
	 * Update the robot's rotation. Used by Matlab to update the GUI
	 *
	 * @param rotation - angle of rotation
	 * @return the updated position of the robot
	 */
	public int[] updateRotation(double theta) {
		mRotation = theta;
		return new int[] {(int) Math.round(mX / mBlockSize) + 1, (int) Math.round(mY / mBlockSize) + 1};
	}

	/**
	 * Update the raw vision data; used for debugging.
	 *
	 * @param data - vision data collected by robot
	 */
	public void updateVisionData(int[] data) {
		mVisionData = "[ ";
		for (int i : data)
			mVisionData += i + " ";
		mVisionData += "]";
	}

	/**
	 * Move the robot through the grid.
	 * If collision is detected, move backwards for a short time.
	 */
	private void move() {
		// Move in direction of rotation or reverse if recovering from collision
		double xVel, yVel, cX = mX + mRadius, cY = mY + mRadius;
		if (mReversing > 0) {
			xVel = mRevX;
			yVel = mRevY;
			mReversing--;
		} else {
			xVel = SPEED * Math.cos(mRotation);
			yVel = SPEED * Math.sin(mRotation);
		}

		// Check collision
		int row = yVel > 0 ? (int) (mY + yVel) / mBlockSize + 1: (int) (mY + yVel) / mBlockSize,
		    col = xVel > 0 ? (int) (mX + xVel) / mBlockSize + 1: (int) (mX + xVel) / mBlockSize;
		if (mGridData[row][col] == 1) {
			mReversing = REVERSE_DELAY;
			mRevX = -xVel;
			mRevY = -yVel;
			return;
		}

		// Move robot
		mX += xVel;
		mY += yVel;
	}

	/**
	 * JPanel for drawing the GUI.
	 */
	public class DrawingPanel extends JPanel {

		/**
		 * Draw the grid image and the robot.
		 *
		 * @param g - Graphics object
		 */
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(mGrid, 0, 0, null);
			g.setColor(Color.BLUE);
			g.fillOval((int) Math.round(mX), (int) Math.round(mY), mBlockSize, mBlockSize);
			g.setColor(Color.BLACK);
			g.fillOval((int) (mX + mRadius * (1 + Math.cos(mRotation))/2), (int) (mY + mRadius * (1 + Math.sin(mRotation))/2), (int) mRadius, (int) mRadius);
			if (mDebug) {
				g.drawString(mVisionData, 25, 25);
				g.drawString(mVisionDataReduced, 100, 50);
			}
		}

	}

}
