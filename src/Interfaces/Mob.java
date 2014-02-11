package Interfaces;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;


/*
 * Mob
 * Describes properties of mobs in general.
 */
public interface Mob extends GameObject {
	@Override
	public void update();
	
	@Override
	public void draw(Graphics2D g2d);
	
	public Point2D.Double getLoc();
	/*
	 * Defend against bullet.
	 */
	public void defend(Bullet b);
	/*
	 * Direct damage.
	 */
	public void defend(int dmg, Tower t);
	
	public void onDeath();
	
	public boolean isAlive();
	// Returns which point number the mob is on for traversing the map;
	public int getPathLoc();
	
	// Returns the distance the mob has til the next point.
	public double getDistLeft();
	
	public void setStatus(int x, int time, Tower t);
	
	public boolean[] getStatus();
}
