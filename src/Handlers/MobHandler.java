/*
 * The MobHandler Class
 * 
 * The MobHandler class handles actions relating to the mobs.
 * 
 * The class's methods provide a functionality to handling operations with the mobs-- such as
 * mob creation, death, and other functions.
 * 
 * By: Robbie Mudroch
 * 2-9-12
 * v2
 */
package Handlers;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

import Bullets.ExplodeEvent;
import Interfaces.*;
import Main.TDPanel;

public class MobHandler extends AbstractHandler<Mob> {
	//private ArrayList<Mob> dead;
	//private Thread handleDead;
	private Object lock = new Object();
	
	public MobHandler() {
		super();
	}

	/*
	 * This returns the nearest mob to the given point that is in range.
	 * It returns null if the list is empty or none in range
	 * This handles ties by returning the mob later in the list.
	 */

	public Mob nearestMob(Point2D.Double target, double range) {
		if (ls.isEmpty())
			return null;
		rwl.readLock().lock();
		double minDist = Double.POSITIVE_INFINITY;
		Mob closest = null;
		double dist = 0;
		
		for (Mob m: ls) {
			dist = target.distance(m.getLoc());
			if (dist < minDist && dist < range && !m.isRemoveable()) { // is m closer to target?
				minDist = dist;				
				closest = m;
			}
		}
		rwl.readLock().unlock();
		return closest;
	}	
	
	public Mob nearMobProx(Point2D.Double target, int range) {
		rwl.readLock().lock();
		double dist = 0;
		for (Mob m: ls) {
			dist = target.distance(m.getLoc());
			if (dist < range && !m.isRemoveable() && m.isAlive()) { // is m closer to target?
				rwl.readLock().unlock();
				
				return m;
			}
		}
		rwl.readLock().unlock();
		return null;
	}


	/*
	 * Returns all mobs that are in proximity-- within distance "range" of a target point.
	 * Returns null if there are no mobs or that there are no mobs near that point
	 */
	public ArrayList<Mob> proxMobs(Point2D.Double target, double range, int max) {
		if (ls.isEmpty()) // returns null if list is empty
			return null;
		rwl.readLock().lock();
		ArrayList<Mob> close = new ArrayList<Mob>();
		double dist; // current distance to mob.
		for (Mob m: ls) {
			if (close.size() == max)
				break;
			dist = target.distance(m.getLoc());
			if (dist <= range) { // is m closer to target?
				close.add(m);
			}
		}	
		rwl.readLock().unlock();
		return close;
	}
	
	
	
	public ArrayList<Mob> swordProx(Point2D.Double loc, int width, int height, double dir) {
		rwl.readLock().lock();
		Rectangle rect = new Rectangle((int) loc.x, (int) loc.y, width, height);
		AffineTransform at = new AffineTransform();
		at.setToRotation(dir,loc.x ,loc.y); // This will act to rotate point.
		try {
			at = at.createInverse();
		} catch (NoninvertibleTransformException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		long rangeMax = width*width+height*height; // cheaperish check
		Point2D.Double temp = new Point2D.Double();
		Point2D.Double mobLoc = null;
		ArrayList<Mob> ret = new ArrayList<Mob>();
		for (Mob m: ls) {
			mobLoc = m.getLoc();
			if (mobLoc.distanceSq(loc) <= rangeMax) {
				at.transform(mobLoc, temp);
				if (rect.contains(temp)) {
					ret.add(m);
				}
			}
		}
		rwl.readLock().unlock();
		return ret;
	}
	
	
	/*
	 * Debugging version
	 */
	public ArrayList<Mob> swordProx(Graphics2D g2d, Point2D.Double loc, int width, int height, double dir) {
		rwl.readLock().lock();
		Rectangle rect = new Rectangle((int) loc.x, (int) loc.y, width, height);
		Shape a = rect; //debugging purposes
		AffineTransform at = new AffineTransform();
		at.setToRotation(dir,loc.x ,loc.y); // This will act to rotate point.
		
		a = at.createTransformedShape(a); // debuging
		try {
			at = at.createInverse();
		} catch (NoninvertibleTransformException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		g2d.draw(a);
		
		long rangeMax = width*width+height*height; // cheaperish check
		Point2D.Double temp = new Point2D.Double();
		Point2D.Double mobLoc = null;
		ArrayList<Mob> ret = new ArrayList<Mob>();
		for (Mob m: ls) {
			mobLoc = m.getLoc();
			if (mobLoc.distanceSq(loc) <= rangeMax) {
				at.transform(mobLoc, temp);
				if (rect.contains(temp)) {
					ret.add(m);
					g2d.drawRect((int) mobLoc.x, (int)mobLoc.y, 5, 5); // more debuging
				}
			}
		}
		rwl.readLock().unlock();
		return ret;
	}
	
	/*
	 * Returns the closest mob to start.  Returns null if no mobs are in range.
	 * 
	 * Mobs are organized by which point they are headed towards (pathLoc) and how long
	 * they have to get to the next point (loc).
	 * 
	 * The mob closest to the source minimizes these two vars.
	 */
	public Mob closeSource(Point2D.Double target, double range) {
		if (ls.size() == 0)
			return null;
		rwl.readLock().lock();
		Mob targ = null;
		int minPL = Integer.MAX_VALUE; // min path loc
		double maxDist = 0; // min distance
		
		int nextPL; // next path loc
		double nextDist; // nex distance
		for (Mob m: ls) {
			nextPL = m.getPathLoc();
			nextDist = m.getDistLeft();
			if (target.distance(m.getLoc()) <= range) {
				if (nextPL < minPL) {
					minPL = nextPL;
					maxDist = nextDist;
					targ = m;
				}
				else if (nextPL == minPL && nextDist >= maxDist) {
					minPL = nextPL;
					maxDist = nextDist;
					targ = m;
				}
				
			}
		}
		rwl.readLock().unlock();
		return targ;
	}
	
	
	
	
	
	/*
	 * Returns the closest mob to end.  Returns null if no mobs are in range.
	 * 
	 * Mobs are organized by which point they are headed towards (pathLoc) and how long
	 * they have to get to the next point (loc).
	 * 
	 * The mob closest to the end maximizes these two vars.
	 * 
	 * This is also when I had higher order functions to specify the function for comparating.
	 */
	public Mob closeEnd(Point2D.Double target, double range) {
		if (ls.size() == 0)
			return null;
		rwl.readLock().lock();
		Mob targ = null;
		int maxPL = 0; // min path loc
		double minDist = Integer.MAX_VALUE; // min distance
		
		int nextPL; // next path loc
		double nextDist; // nex distance
		for (Mob m: ls) {
			nextPL = m.getPathLoc();
			nextDist = m.getDistLeft();
			if (target.distance(m.getLoc()) <= range) 
				if (nextPL > maxPL) {
					maxPL = nextPL;
					minDist = nextDist;
					targ = m;
				}
				else if (nextPL == maxPL && nextDist <= minDist) {
					maxPL = nextPL;
					minDist = nextDist;
					targ = m;
				}
		}
		rwl.readLock().unlock();
		return targ;
	}

	/*
	 * Removes all mobs from handler that are marked as "removeable."
	 */
	@Override
	public void flush() {
		Mob next;
		if (ls.size() == 0)
			return;
		synchronized(lock) {
		rwl.writeLock().lock();
		Iterator<Mob> entries = ls.iterator();
		while (entries.hasNext()) {
			next = entries.next();
			if (TDPanel.god && TDPanel.speedFactor > 1) {
				DrawEvent explode = new ExplodeEvent(0, next.getLoc().x, next.getLoc().y);
				TDPanel.level.addDraw(explode);
				entries.remove();
			}
			if (next.isRemoveable()) { // removable mobs are like dead mobs except extra dead.
				entries.remove();
				//dead.add(next);
				//next.onDeath();
			}
			else if (!next.isAlive()){ // dead mobs are added to dead queue.
				//dead.add(next);
				next.onDeath();
			}
		}
		rwl.writeLock().unlock();
		}
	}
}