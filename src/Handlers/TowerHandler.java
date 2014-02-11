package Handlers;
import Interfaces.*;

import java.awt.Point;
import java.awt.geom.Point2D;


public class TowerHandler extends AbstractHandler<Tower>{
	
	public TowerHandler() {
		super();
	}

	public Tower nearestTower(Point2D.Double target) {
		if (ls.size() == 0)
			return null;
		rwl.readLock().lock();
		double minDist = Double.POSITIVE_INFINITY;
		Tower closest = ls.get(0);
		double dist = 0;
		for (Tower m: ls) {
			dist = target.distance(m.getLoc());
			if (dist < minDist && !m.isRemoveable()) { // is m closer to target?
				minDist = dist;				
				closest = m;
			}
		}
		rwl.readLock().unlock();
		return closest;
	}
	public Tower nearestTower(Point target) {
		return nearestTower(new Point2D.Double(target.x, target.y));
	}
	public boolean isTowerNear(Point2D.Double target, int range) {
		Tower close =  nearestTower(target);
		if (close == null)
			return false;
		return close.getLoc().distance(target) <= range;
	}
}
