package Interfaces;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;


public interface Tower extends GameObject {
	
	
	/*
	 * Move tower to specified location.
	 */
	public void move(Point2D.Double newLoc);
	/*
	 * Returns the picture drawn for the tower.
	 */
	public BufferedImage getPic();
	
	/*
	 * Returns the tower's location
	 */
	public Point2D.Double getLoc();
	
	/*
	 * Returns the tower's range
	 */
	public int getRng();
	
	/*
	 * Return info about tower.
	 */
	public String getBio();
	
	/*
	 * Returns cost of tower
	 */
	public int getUpgCost();
	
	/*
	 * sets the tower's mode
	 */
	public void setMode(int mode);
	
	/*
	 * recieves tower's mode
	 */
	public int getMode();
	
	/*
	 * returns name of next upgrade
	 */
	public String upgName();
	
	/*
	 * returns whether the tower can upgrade
	 */
	public boolean canUpg();
	
	/*
	 * Upgrades the toewr!
	 */
	public void upgrade();
	
	/*
	 * Returns initial cost
	 */
	public int getCost();
	
	/*
	 * Increments tower kill counter
	 */
	public void incKill();
	
	/*
	 * Returns number kills tower has
	 */
	public long killCount();
	
	/*
	 * Returns tower sell price
	 */
	public double sellPrice();
	
	/*
	 * Sells tower
	 */
	public void sell();
	
	/*
	 * Returns tower upg level
	 */
	public int getUpg();

}

