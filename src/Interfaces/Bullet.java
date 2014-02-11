package Interfaces;

import java.awt.geom.Point2D;


public interface Bullet extends GameObject {
	public Point2D.Double getLoc();

	public int getDmg();
	
	public Tower getShooter();
}
