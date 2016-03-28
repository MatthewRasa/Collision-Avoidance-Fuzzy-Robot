package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

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
	private static final int WIDTH = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(),
	                         HEIGHT = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight(),
	                         COLOR_BLANK = Color.WHITE.getRGB(),
	                         COLOR_OBS = Color.GRAY.getRGB(),
	                         REFRESH_RATE = 1000 / 60,
	                         REVERSE_DELAY = 20,
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
		for (int xi = 0; xi < size; xi++) {
			for (int yi = 0; yi < size; yi++)
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
	private double mX, mY, mRotation, mRevX, mRevY;
	private int[][] mGridData;
	private int mBlockSize, mReversing;
	private boolean mRunning;

	/**
	 * Create a new Grid with the specified grid data.
	 *
	 * @param gridData - grid data to display in GUI
	 */
	public Grid(int[][] gridData) {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setContentPane(new DrawingPanel());
		setSize(WIDTH, HEIGHT);
		
		// Instantiate members
		mRotation = mX = mY = mRevX = mRevY = 0;
		mGridData = gridData;
		mBlockSize = useLesser(WIDTH / gridData[0].length, HEIGHT / gridData.length);
		mReversing = 0;
		mRunning = true;

		// Create grid image
		mGrid = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		for (int x = 0; x < gridData[0].length; x++) {
			for (int y = 0; y < gridData.length; y++)
				drawBlock(mGrid, x, y, mBlockSize, gridData[y][x] == 0 ? COLOR_BLANK : COLOR_OBS);
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
		return new int[] {(int) Math.round(mX), (int) Math.round(mY)};
	}

	/**
	 * Move the robot through the grid.
	 */
	private void move() {
		// Move in direction of rotation or reverse if recovering from collision
		double xVel, yVel;
		if (mReversing > 0) {
			xVel = mRevX;
			yVel = mRevY;
			mReversing--;
		} else {
			xVel = SPEED * Math.cos(mRotation);
			yVel = SPEED * Math.sin(mRotation);
		}

		// Check collision
		if (mGridData[bound(0, (int) (mY + yVel) / mBlockSize + 1, mGridData.length)][bound(0, (int) (mX + xVel) / mBlockSize + 1, mGridData[0].length)]== 1) {
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
			double radius = mBlockSize / 2;
			g.drawImage(mGrid, 0, 0, null);
			g.setColor(Color.BLUE);
			g.fillOval((int) Math.round(mX), (int) Math.round(mY), mBlockSize, mBlockSize);
			g.setColor(Color.BLACK);
			g.fillOval((int) (mX + radius * (1 + Math.cos(mRotation))/2), (int) (mY + radius * (1 + Math.sin(mRotation))/2), (int) radius, (int) radius);
		}

	}

}
