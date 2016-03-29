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
 * Filename:      Driver.java
 * Description:   Helper class to test PathGenerator.java.
 * Last Modified: 2016-3-28
 */

/* Setting Package */
package com.kevinbohinski.CSC47002;

public class Driver {

	/**
	 * Main method, executes testing of path generation.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		PathGenerator pg = new PathGenerator(3, 30, 16);
		pg.printPaths();
	}

}
