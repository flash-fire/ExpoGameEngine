package Main;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/*
 *  MonsterPath
 *  
 *  This class draws the path the the monsters use and also provides methods surrounding functionality
 *  of the path. [ie. telling monsters which direction to go]
 *   
 */


public class MonsterPath {
	private ArrayList<Point2D.Double> path; // I would use a general path, but I need to quickly determine next point.
	Point2D.Double source; // source of monsters
	private Point2D.Double target; // target of monsters
	private double[] preCalc; // pre calculated numbers
	boolean preCalced;
	public static BufferedImage road;
	MonsterPath(Point2D.Double source, Point2D.Double target) {
		this.source = source;
		this.target = target;
		path = new ArrayList<Point2D.Double>();
		path.add(source);
		path.add(target);
		preCalced = false;
		road = ImageLoader.loadImage("resources/images/background/road2.png");

	}

	public void add(Point2D.Double p) {
		assert path.get(path.size()-1).equals(target): "WHY ME?";
		path.add(path.size()-1, p);
	}
	
	public void addScaled(int[] xPos, int[] yPos) {
		if (xPos.length != yPos.length) {
			System.out.println("*******Array Size mismatch in add scaled.");
			return;
		}
		for (int i = 0;i < xPos.length;i++)
			add(new Point2D.Double(xPos[i]*GamePanel.PWIDTH/1000D, yPos[i]*GamePanel.PHEIGHT/1000D));
	}

	public Point2D.Double getTarget() {
		return target;
	}

	/*
	 * Performs Brownian motion on the path for the lolz.
	 */
	public void brownian() {
		/*
		for (int i = 0; i < path.size(); i++) {
			path.get(i).x = (.8*TDPanel.PWIDTH + path.get(i).x + 5*(Math.random()-.5D))%(.8*TDPanel.PWIDTH);
			path.get(i).y = (TDPanel.PHEIGHT +path.get(i).y + 5*(Math.random()-.5D))%TDPanel.PHEIGHT;
		}*/
	}
	/*
	 * Removes current target and sets target to be p
	 */
	public void setTarget(Point2D.Double p) {
		path.remove(target);
		path.add(p);
		target = p;
	}


	/*
	 * Given current position and target, this returns direction the mob should be moving.
	 */
	public double getDir(Point2D curr, Point2D next) {
		double x = curr.getX(); // Current X
		double y = curr.getY(); // Current Y
		return Math.atan2(next.getY() - y,next.getX() - x);
	}

	public double getDir2(Point2D.Double loc, int curr) {
		
		if (!preCalced || Math.random() > .99) { // precalculates and occasionally recalculates to account for brownian crap.
			preCalc = new double[path.size()];
			Point2D.Double temp = path.get(0);
			Point2D.Double tempNext = path.get(1);
			for (int i = 1; i < path.size()-1;) {   
				preCalc[i-1] = Math.atan2(-tempNext.y + temp.y,-tempNext.x + temp.x);
				temp = path.get(i+1);
				tempNext = path.get(i);
				i++;
			}
			preCalc[preCalc.length-2] = Math.atan2(target.y-tempNext.y, target.x-tempNext.x);
			preCalced = true;
		}
		// If monster is close, I will just use the approximation and save calculating the atangent.
		 Point2D.Double next = next(curr);
         if (path.get(curr).distance(loc) <= 10 && curr < preCalc.length) {
			return preCalc[curr];
		}
		return getDir(loc, next);
	}

	/*
	 * Returns whether the the next point is the last point.
	 */
	public boolean targIsEnd(int curr) {
		return curr+1 >= path.size();
	}

	public Point2D.Double next(int curr) {
		if (curr+1 < path.size())
			return path.get(curr+1);
		else
			return target;
	}

	/*
	 * Add ints
	 */
	public void add(int[] xPos, int[] yPos) {
		if (xPos.length != yPos.length) {
			System.out.println("******Adding unequal sized arrays in add");
			return;
		}

		for (int i = 0;i < xPos.length;i++)
			add(new Point2D.Double(xPos[i], yPos[i]));
	}

	/*
	 * Add doubles
	 */
	public void add(double[] xPos, double[] yPos) {
		if (xPos.length != yPos.length) {
			System.out.println("Adding unequal sized arrays");
			return;
		}

		for (int i = 0;i < xPos.length;i++)
			path.add(new Point2D.Double(xPos[i], yPos[i]));
	}

	private int r(double x) {
		return (int) Math.round(x);
	}
	
	
	public void draw(Graphics2D g2d) {
		g2d.setStroke(new BasicStroke(5.0f, BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND)); 
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

	@Override
	public String toString() {
		String out = "";
		for (Point2D.Double p: path) {
			out += (path.toString() + " , ");
		}
		return out;
	}
}
