package Towers;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import Interfaces.*;
import Handlers.MobHandler;
import Main.TDPanel;

public abstract class AbstractTower implements Tower {
	protected Point2D.Double loc;
	protected int range;
	protected int initCost;
	protected int splash;
	protected int dmg;
	protected int reloadTime;
	protected int mode;
	protected int id;
	protected MobHandler mh;
	protected long lastShot;
	public long killCount = 0;
	public boolean isRemoveable = false;
	protected int upg;
	protected int failHP; // when tower starts to fail.
	
	public AbstractTower(Point2D.Double loc, int range, int cost, int splash, int dmg, int reloadTime, int mode, int id) {
		easyDef(loc, range, cost, splash, dmg, reloadTime, mode, id);
	}
	
	public AbstractTower() {
		defaultStats();
	}
	
	public abstract void defaultStats();
/*
 * The more compatible default with current constructor.  Really log parameters I know.
 */
	public void easyDef(Point2D.Double loc, int range, int cost, int splash, int dmg, int reloadTime, int mode, int id) {
		this.range = range;
		initCost = cost;
		this.splash = splash;
		this.dmg = dmg;
		this.reloadTime = reloadTime;
		this.mode = mode;
		this.id = id;
		mh = TDPanel.getMobHandler();
		this.loc = loc;
		lastShot = 0;
		upg = 0;
		failHP = 50;
	}
	
	@Override
	public void move(Point2D.Double targ) {
		loc = (Point2D.Double) targ.clone();
	}
	
	@Override
	public int getUpg() {
		return upg;
	}
	/*'
	 * (non-Javadoc)
	 * @see Tower#getCost()
	 */
	@Override
	public int getCost() {
		return initCost;
	}
	/*
	 * (non-Javadoc)
	 * @see Tower#setMode()
	 */
	@Override
	public void setMode(int mode) {
		this.mode = mode;	
	}
	/*
	 * (non-Javadoc)
	 * @see Tower#getMode()
	 */
	@Override
	public int getMode() {
		return mode;
	}
	
	/*
	 * (non-Javadoc)
	 * @see Tower#getMode
	 */
	@Override
	public int getRng() {
		return range;
	}
	
	/*
	 * Returns true if towers should be failing.
	 */
	public boolean isFailing() {
		return TDPanel.health <= failHP;
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see Tower#getMode
	 */
	@Override
	public Double getLoc() {
		return loc;
	}
	
	/*
	 * nextMob returns the mob that should be attacked next by the tower.
	 */
	protected Mob nextMob() {
		Mob closestMob;
		if (mode == 1) // closest to tower
		   closestMob = mh.nearestMob(loc, range);
		else if (mode == 2) { // closest to source
		   closestMob = mh.closeSource(loc, range);
		}
		else // closest to end
			closestMob = mh.closeEnd(loc, range);
		return closestMob;
	}
	
	/*
	 * Returns whether the tower is reloading. 
	 */
	protected boolean isReloading() {
		return System.nanoTime()/1000000-lastShot <= reloadTime/TDPanel.speedFactor/TDPanel.towerFactor;
		//return false;
	}
	
	@Override
	public void incKill() {
		killCount++;
	}
	
	@Override
	public long killCount() {
		return killCount;
	}
	
	@Override
	public void sell() {
		TDPanel.gold += sellPrice()*TDPanel.costMult;
		isRemoveable = true;
	}
	
	@Override
	public boolean isRemoveable() {
		return isRemoveable;
	}

}
