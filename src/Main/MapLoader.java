package Main;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class MapLoader {
	Scanner lvlFile; // Scanner that loads levels.

	int lastMap; // last level loaded
	ArrayList<MonsterPath> loaded;
	public static int NUM_FILES = 6;
	public boolean initialized;
 	public MapLoader() {
 		initialized = true;
 		lastMap = 1;
 		loaded = new ArrayList<MonsterPath>();
		try {
			lvlFile = new Scanner(new File("resources/levelData/MapData.txt"));
		} catch (FileNotFoundException e) {
		
			e.printStackTrace();
		}
 		init();
	}

 	public void init() {
 		MonsterPath prev = loadMap(1);
 		int i = 1;
 		while (prev != null) {
 			loaded.add(prev);
 			System.out.println(prev.toString());
 			prev = loadNextMap();

 		}
 		System.out.println(loaded.size());
 		initialized = true;
 	}
	/*
	 * resets scanner.
	 */
	public void reset() {

		try {
			lvlFile.close();	
			lvlFile = new Scanner(new File("resources/levelData/MapData.txt"));
		} catch (FileNotFoundException e) {
		
			e.printStackTrace();			
		}
	}
	/*
	 * Loads specified level.  Returns false if it failed to load.
	 */
	public MonsterPath loadMap(int targ) {
		//System.out.println("*******" + targ + " size of loaded " + loaded.size());
		if (initialized && targ-1 < loaded.size())
			return loaded.get(targ-1);
		else if (!initialized) {
			System.out.println("@#$#@$@$#@$@$");
			init();
		}

		lastMap = targ; // sets level to last level loaded;
		String next = ""; // next line from file.
		while (lvlFile.hasNext()) { // safety first
			if (ignoreable(next)) {// ignores comments
				//System.out.println(next);
				next = lvlFile.next();
				continue;
			}
			if (next.equals(""+targ)) { // if we are at target level.
				//int[] sourceAry = parseNext();
				Point2D.Double source = new Point2D.Double(lvlFile.nextInt(), lvlFile.nextInt());
				// Each time I load a line, I near to ignore the comments.
				// Load Target
				System.out.println(source.toString());
				Point2D.Double target = new Point2D.Double(lvlFile.nextInt(), lvlFile.nextInt());
				System.out.println(target.toString());
				//System.out.println("****************"+(next = lvlFile.nextLine()));
				next = lvlFile.nextLine();
				int[] aryX = parseNext();			
				int[] aryY = parseNext();
				MonsterPath out = new MonsterPath(source, target);
				out.addScaled(aryX, aryY);
				//System.out.println("Out: " + out.toString());

				return out;
			}
			next = lvlFile.nextLine(); // iterates loop
		}
		System.out.println("MAP LOADER encountered EOF");
		return null;
	}

	public int[] parseNext() {
		String next = lvlFile.nextLine();
		//while (lvlFile.hasNext() && !(next = lvlFile.nextLine()).equals("")&& ignoreable(next));
		//System.out.println("next" + next);
		String[] strArray = next.split(" "); // parses event data
		int[] intArray = new int[strArray.length];
		/*System.out.println();
		for (String str: strArray) {
			System.out.print(str + " , ");
		}
		System.out.println();*/
		for(int i = 0; i < strArray.length; i++) { //converts to ints
			intArray[i] = Integer.parseInt(strArray[i]);
		}
		return intArray;
	}
	
	public static void main(String args[]) {
		MapLoader ml = new MapLoader();
		Level lvl = new Level();
		LevelLoader loader = new LevelLoader();
		loader.loadLvl(9);
		//System.out.println(ml.loaded.get(1).toString())
		;
	}
	/*
	 * Loads next level.
	 */
	public MonsterPath loadNextMap() {
		return loadMap(++lastMap);
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
