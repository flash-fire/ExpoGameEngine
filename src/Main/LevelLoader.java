package Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class LevelLoader {
	Scanner lvlFile; // Scanner that loads levels.

	int lastLvl; // last level loaded
	public LevelLoader() {
		try {
			lvlFile = new Scanner(new File("resources/levelData/mobData.txt"));
		} catch (FileNotFoundException e) {
		
			e.printStackTrace();
		}
	}
	
	/*
	 * resets scanner.
	 */
	public void reset() {

		try {
			lvlFile.close();	
			lvlFile = new Scanner(new File("resources/levelData/mobData.txt"));
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();			
		}
	}
	/*
	 * Loads specified level.  Returns false if it failed to load.
	 */
	public boolean loadLvl(int targ) {
		
		lastLvl = targ; // sets level to last level loaded;
		String next = ""; // next line from file.
		while (lvlFile.hasNext()) { // safety first
			if (ignoreable(next)) {// ignores comments
				next = lvlFile.next();
				continue;
			}
			if (next.equals(""+targ)) { // if we are at target level.
				while ((next = lvlFile.nextLine()).equals(""));
				System.out.println("NEXT: "+ next);
				TDPanel.loadMap(Integer.parseInt(next.split(" ")[1]));
				
				while (lvlFile.hasNext() && !(next = lvlFile.nextLine()).equals("")) {
//					System.out.println("NEXT: " + next);
					if (!ignoreable(next)) {
						String[] strArray = next.split(" "); // parses event data
						int[] intArray = new int[strArray.length]; 
						for(int i = 0; i < strArray.length; i++) { //converts to ints
						    intArray[i] = Integer.parseInt(strArray[i]);
						}
						assert TDPanel.level != null: "TDPanel's level is null.  -- In LevelLoader load lvl";
						TDPanel.level.addMLE(intArray); // adds event to level.
					}
				}
				return true;
			}
			next = lvlFile.nextLine(); // iterates loop
		}
		System.out.println("Loading Level encountered EOF");
		return false;
	}
	
	/*
	 * Loads next level.
	 */
	public boolean loadNextLevel() {
		return loadLvl(++lastLvl);
	}
	
	/*
	 * Returns true when there still exists another loadable level.
	 */
	public boolean hasNextLevel() {
		return lvlFile.hasNextInt();
	}

	/*
	 * Returns true if the line is commented.
	 */
	public static boolean ignoreable(String imp) {
		return (imp != null && imp.length() >= 1 && imp.charAt(0) == '#');
	}	
}
