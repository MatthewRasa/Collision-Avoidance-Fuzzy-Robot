/**
 * @author Kevin Bohinski <bohinsk1@tcnj.edu>
 * @version 1.0
 * @since 2016-3-28
 *
 * Course:        CSC 470 - 02 (Topics: Machine Learning)
 * Instructor:    Dr. Elangovan
 * Project Name:  Project 2
 * Description:   Fuzzy Logic
 *
 * Filename:      PathGenerator.java
 * Description:   Helper class to generate valid paths for our fuzzy logic testing/training.
 * Last Modified: 2016-3-28
 */

/* Setting Package */
package com.kevinbohinski.CSC47002;

/* Setting Imports */
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PathGenerator {

	/* Private vars */
	private final int wall = 1;
	private final int path = 0;
	private List<int[][]> paths;
	private int n;
	private int width;
	private int height;
	private Random rng;

	/**
	 * Constructor: Creates a new PathGenerator object from params. Starts path
	 * generation process.
	 * 
	 * @param n
	 *            : How many paths to make.
	 * @param width
	 *            : How wide the map should be.
	 * @param height
	 *            : How tall the map should be.
	 */
	public PathGenerator(int n, int width, int height) {
		this.n = n;
		this.width = width;
		this.height = height;
		paths = new ArrayList<int[][]>();
		rng = new Random();
		for (int i = 0; i < n; i++) {
			generatePath();
		}
	}

	/**
	 * Generates a map with a valid path.
	 */
	private void generatePath() {
		int[][] arr = new int[width][height];
		int y = 0;

		for (int x = 0; x < width; x++) {
			int pathWidth = randInt(1, height);
			int pathStart = randInt(0, height - pathWidth);

			/* Make sure there is a valid path. */
			if (x != 0) {
				boolean check = false;
				while (!check) {
					for (int i = pathStart; i < pathStart + pathWidth; i++) {
						if (arr[x - 1][i] == path) {
							check = true;
							break;
						}
					}
					if (!check) {
						pathStart = randInt(0, height - pathWidth);
					}
				}
			}

			/* For simplicity set everything in this col to a wall. */
			for (y = 0; y < height; y++) {
				arr[x][y] = wall;
			}

			/* Go back a set the paths. */
			while (pathWidth > 0) {
				arr[x][pathStart] = path;
				pathStart++;
				pathWidth--;
			}
		}

		/* Add this path to the list of paths. */
		paths.add(arr);
	}

	/**
	 * Helper function to return a random integer within bounds.
	 * 
	 * @param min
	 *            : Lower bound.
	 * @param max
	 *            : Upper bound.
	 * @return : Random integer between lower and upper bounds.
	 */
	private int randInt(int min, int max) {
		return rng.nextInt((max - min) + 1) + min;
	}

	/**
	 * Helper function to print paths.
	 */
	public void printPaths() {
		/* Variables to print output with color */
		final String ANSI_RESET = "\u001B[0m";
		final String ANSI_RED = "\u001B[31m";
		final String ANSI_GREEN = "\u001B[32m";

		for (int[][] arr : paths) {
			System.out.println();
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					int tmp = arr[x][y];
					if (tmp == wall) {
						System.out.print(ANSI_RED + tmp + " ");
					} else if (tmp == path) {
						System.out.print(ANSI_GREEN + tmp + " ");
					}
					System.out.print(ANSI_RESET);
				}
				System.out.println();
			}
		}
	}

	/**
	 * Getter function for the list of paths.
	 * 
	 * @return : List of paths.
	 */
	public List<int[][]> getPaths() {
		return paths;
	}

	/**
	 * Getter function for a map.
	 * 
	 * @param id
	 *            : Index of requested map.
	 * @return : The map at that index.
	 */
	public int[][] getPath(int id) {
		return paths.get(id);
	}

	@Override
	/**
	 * Simple toString() function.
	 */
	public String toString() {
		return "PathGenerator [wall=" + wall + ", path=" + path + ", paths=" + paths + ", n=" + n + ", width=" + width
				+ ", height=" + height + ", rng=" + rng + "]";
	}

}
