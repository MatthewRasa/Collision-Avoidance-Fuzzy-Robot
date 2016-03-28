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
	                         SPEED = 1;
	
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
	private double mRotation;
	private int mX, mY, mBlockSize;
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
		
		mRotation = 0;
		mX = mY = 0;
		mRunning = true;
		mBlockSize = useLesser(WIDTH / gridData[0].length, HEIGHT / gridData.length);

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
		while(true) {
			repaint();
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
		return new int[] {mX, mY};
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
			double xComp = Math.cos(mRotation),
			       yComp = Math.sin(mRotation),
			       radius = mBlockSize / 2;
			g.drawImage(mGrid, 0, 0, null);
			g.setColor(Color.BLUE);
			g.fillOval(mX, mY, mBlockSize, mBlockSize);
			g.setColor(Color.BLACK);
			g.fillOval((int) (mX + radius * (1 + xComp)/2), (int) (mY + radius * (1 + yComp)/2), (int) radius, (int) radius);
			mX += SPEED * xComp;
			mY += SPEED * yComp;
		}

	}

}
