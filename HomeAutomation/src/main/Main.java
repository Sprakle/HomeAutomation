/*
 * Resuming from break:
 * 
 * - Commands to the database work well, but some checking could be done to make sure error's don't happen
 * - The next goal is to make data requests from the OD work. (not yet linked to arduino) After this, Walter is at version 1.
 * 		The next thing would be adding modules such as weather, personality, etc
 */

package main;


public class Main {

	public static void main(String[] args) { // that break points is just there so I stop hitting F11 instead of Ctrl-F11

		ApplicationFactory.createInstance();
	}
}