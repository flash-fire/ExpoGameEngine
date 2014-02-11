package Main;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class MapMaker {
	public static ArrayList<Point2D> path;
	private static FileWriter fp;
	public static BufferedImage road;

	public MapMaker() { 
		path = new ArrayList<Point2D>();
		try {
			fp = new FileWriter(new File("resources/levelData/MapGen.txt"));
		} catch (IOException e) {
		
			e.printStackTrace();
		}
	}

	public static void addNode(Point2D p) {
		if (path == null)
			path = new ArrayList<Point2D>();
		path.add(p);
	}

	public static void draw(Graphics2D g2d) {
		if (path == null)
			return;
		if (road == null)
			road = ImageLoader.loadImage("resources/images/background/road2.png");
		Point2D next;
		Point2D prev;

		for (int i = 1;i<path.size();i++) {
			prev = path.get(i-1);
			next = path.get(i);
			//g2d.setColor(new Color(i*50%255,i*50%255,i*50%255)); // can't have a boring looking path!
			//g2d.drawLine(r(prev.getX()), r(prev.getY()), r(next.getX()), r(next.getY())); // Draws the walkway the monsters walk on.

			double dist = prev.distance(next);
			AffineTransform at = new AffineTransform();
			double dir;
			double x = prev.getX();
			double y = prev.getY();
			dir = Math.atan2(-y + next.getY(), 
					-x + next.getX());
			at.setToRotation(dir,x,y ); // rotates tower	
			at.translate(x-15,y-15);  // the -15 was found experimentally lol
			at.scale((dist / (road.getWidth())  ) ,1); // sizes image
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .95f));
			g2d.drawImage(road,at,null);
		} 
	}


/*
 * Dumps information to file.  Map number is 1.  That's easily changed.
 */
public static void dump() {
	if (path == null)
		return;
	System.out.println("working on dumping file info");
	if (fp == null) {
		try {
			fp = new FileWriter(new File("resources/levelData/MapGen.txt"));
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	String xs = "";
	String ys = "";
	String source = "";
	String target = "";
	for (int i = 1; i < path.size()-1; i++) {
		Point2D p = path.get(i);
		xs = xs + Math.round((1000*p.getX()/GamePanel.PWIDTH));
		ys = ys + Math.round((1000*p.getY()/GamePanel.PHEIGHT));
		if (i != path.size() - 2) {
			xs = xs + " ";
			ys = ys + " ";
		}
	}
	
	source =  "" + Math.round((1000*path.get(0).getX()/GamePanel.PWIDTH));
	source = source + " " + Math.round((1000*path.get(0).getY()/GamePanel.PHEIGHT));

	target =  "" + Math.round((1000*path.get(  path.size()-2  ).getX()/GamePanel.PWIDTH));
	target = target + " " + Math.round((1000*path.get(  path.size()-2  ).getY()/GamePanel.PHEIGHT));
	

	try {

		System.out.println("We are trying to append information");
		fp.append("1");
		fp.append("\n" + source);
		fp.append("\n" + target);
		fp.append("\n" + xs);
		fp.append("\n" + ys);
		fp.close();
	} catch (IOException e) {
	
		e.printStackTrace();
	}	
}

}
