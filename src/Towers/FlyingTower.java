package Towers;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import Bullets.FlyerBullet;
import Handlers.AbstractHandler;
import Handlers.GifHandler;
import Interfaces.*;
import Main.ImageLoader;
import Main.TDPanel;

public class FlyingTower extends AbstractTower {
	
	public static final int[] ranges = {300, 400, 500};
	public static final int[] costs = {175, 160, 160};
	public static final int[] splashs = {20, 40, 120};
	public static final int[] dmgs = {125, 150, 250};
	public static final int[] reloadTimes = {350, 350, 350};
	public static final int[] vels = {1, 1, 1};
	public static final int cost = costs[0];
	public static final String[] names = {"Glider", "Ace", "Unreal Air"};
	public static final String[] bios = { ": Single glider with bullets galore!" 
										, ": Splashy Bullets for Aces!"
										, ": Our splash shall blot out the sun"};
	private AirBaseTower abt; // if spawned by airport, this is used
	private int killTime; // these two vars are used for the aircraft carrier
//	private int maxKillTime; // another airport var
	private boolean temporary; // anothe airport var
	private boolean hasDec = false; // another airport var. Specifies death of this.
	protected AbstractHandler<Bullet> bh;
	private int lastMove = 0;
	private int bulletTime = 1500;
	private static BufferedImage[] flyPics;
//	private static BufferedImage tur;
	double prevX = 0;
	double prevY = 0;
	private int count = 0;

	
	public FlyingTower() {
		super();
		
	}
	/*
	 *  Constructor.  Takes given starting loc and adds a tower there.
	 */
	FlyingTower(Point2D.Double loc, int id) {
		super(loc, ranges[0], costs[0], splashs[0], dmgs[0], reloadTimes[0], 1, id);
		bh = TDPanel.getBulletHandler();
		killTime = 1; // not used in this version
		temporary = false;
		upg = 0;
		if (flyPics == null) {
			flyPics = new BufferedImage[4];
			flyPics[0] = ImageLoader.loadImage("resources/images/Towers/flying1.png");
			flyPics[1] = ImageLoader.loadImage("resources/images/Towers/flying2.png");
			flyPics[2] = ImageLoader.loadImage("resources/images/Towers/flying3.png");
//			tur =  ImageLoader.loadImage("resources/images/Towers/flyingTur.png");
		}
	}
	/*
	 * Kill time is implimented for easy use with aircraft carrier tower.
	 */
	FlyingTower(int killTime, Point2D.Double loc, AirBaseTower abt, int upg) {
		super(loc, ranges[upg], costs[upg], splashs[upg], dmgs[upg], reloadTimes[upg], 1, -1);
		bh = TDPanel.getBulletHandler();
		this.abt = abt;
		this.killTime = killTime;
//		maxKillTime = killTime;
		bh = TDPanel.getBulletHandler();
		this.id = -1; // null since tower is temporary!
		temporary = true;
		this.upg = upg;
		failHP = (int) Math.round(TDPanel.healthDef*TDPanel.failFactor*upg);
		initPics();
	}
	
	/*
	 * Constructor using coordinates to specify location.
	 */
	public FlyingTower(double x, double y, int id) {
		super( new Point2D.Double(x , y), ranges[0], costs[0], splashs[0], dmgs[0], reloadTimes[0], 1, id);
		bh = TDPanel.getBulletHandler();
		temporary = false;
		upg = 0;
		initPics();
	}
	
	@Override
	public void defaultStats() {
		easyDef(new Point2D.Double(0 , 0), ranges[0], costs[0]
		           , splashs[0], dmgs[0], reloadTimes[0], 1, TDPanel.incTowID());
		bh = TDPanel.getBulletHandler();
		killTime = 1; // not used in this version
		temporary = false;
		upg = 0;
		initPics();
		
	}
	

	private void initPics() {
		if (flyPics == null) {
			flyPics = new BufferedImage[4];
			flyPics[0] = ImageLoader.loadImage("resources/images/Towers/flying1.png");
			flyPics[1] = ImageLoader.loadImage("resources/images/Towers/flying2.png");
			flyPics[2] = ImageLoader.loadImage("resources/images/Towers/flying3.png");
//			tur =  ImageLoader.loadImage("resources/images/Towers/flyingTur.png");
		}
	}
	
	@Override
	public BufferedImage getPic() {
		if (flyPics == null)
			initPics();
		return flyPics[0];
	}
	
	@Override
	public void update() {
		if (temporary) {
			killTime--;
			if (killTime <= 0 && !hasDec) {  // abt is never null if the tower is temporary
				hasDec = true;
				abt.dec();
			}
		}
		
		
		if (!isReloading()) {
			Mob closestMob;
			if (mode == 1) // closest to tower
				   closestMob = mh.nearestMob(loc, range*TDPanel.speedFactor * TDPanel.towerFactor);
				else if (mode == 2) { // closest to source
				   closestMob = mh.closeSource(loc, range*TDPanel.speedFactor * TDPanel.towerFactor);
				}
				else // closest to end
					closestMob = mh.closeEnd(loc, range*TDPanel.speedFactor * TDPanel.towerFactor);
			if (closestMob != null) {
				lastShot = System.nanoTime()/1000000;
				if (lastShot % 2 == 1) // The first bullet is what causes the ship to navigate!
					bh.add(new FlyerBullet(loc, closestMob.getLoc(), splash, dmg,3*vels[upg],System.nanoTime(), bulletTime, true, this));

				bh.add(new FlyerBullet((Point2D.Double)loc.clone(), closestMob.getLoc(), splash, dmg,5*vels[upg],System.nanoTime(), bulletTime, false, this));
				bh.add(new FlyerBullet((Point2D.Double)loc.clone(), closestMob.getLoc(), splash, dmg,8*vels[upg],System.nanoTime(), bulletTime, false, this));
				lastMove = 0;
				return;
			}
			
				lastMove++;
		}
		else if (lastMove > 400) { // this destucks the flier if it gets stuck!
			bh.add(new FlyerBullet(loc, TDPanel.source, splash, dmg,vels[upg],System.nanoTime(), bulletTime, false, this));
			bh.add(new FlyerBullet((Point2D.Double)loc.clone(), TDPanel.source, splash, dmg,5*vels[upg],System.nanoTime(), bulletTime, false, this));
			lastMove = 0; 
		}
	}
	
	@Override
	public String getBio() {
		return names[upg] + bios[upg];
	}
	
	@Override
	public void draw(Graphics2D g2d) {
//		AffineTransform at = new AffineTransform();
//		double dir = Math.atan2(prevY-loc.y,prevX-loc.x);
//		at.setToRotation(dir-Math.PI/2,loc.x,loc.y); // rotates tower	
//		at.translate(loc.x - flyPics[upg].getWidth()/2, loc.y - flyPics[upg].getHeight()/2);
//		g2d.drawImage(flyPics[upg], at, null);
//		prevX = loc.x; // For direction of plain
//		prevY = loc.y;	
//		/*Mob m = mh.nearestMob(loc, 500) //TURRET
//		if (m != null) { 
//			AffineTransform t = new AffineTransform();		
//			Point2D.Double tar = m.getLoc();
//			double direction = Math.atan2(tar.y-loc.y,tar.x-loc.x);
//			t.setToRotation(direction + Math.PI/2d,  loc.x,loc.y);		
//			t.translate(loc.x - tur.getWidth()/2, loc.y - tur.getHeight()/2);
//			//t.translate(30d*Math.cos(dir-Math.PI/2), 30d*Math.sin(dir-Math.PI/2));
//			g2d.drawImage(tur, t, null);
//		} */
		AffineTransform at = new AffineTransform();
		at.translate(loc.x, loc.y );
		if (TDPanel.frame() % 2 == 0) {
			count++;
			count %= GifHandler.WrapperTower.sequence.length;
		}
		g2d.drawImage(GifHandler.WrapperTower.sequence[count], at, null);
	} 
	
	
	/*
	 * Rounding shorthand.
	 */
	protected int r(double x) {
		return (int) Math.round(x);
	}

	@Override
	public boolean isRemoveable() {
		return (killTime <= 0 && temporary) || isRemoveable;
	}
	
	@Override
	public int getUpgCost() {
		if (!canUpg())
			return costs[upg];
		return costs[upg+1];
	}
	
	@Override
	public String toString() {
		return names[upg];
	}
	
	@Override
	public String upgName() {
		if (!canUpg())
			return "Maxed!";
		
			return names[upg+1];
	}
	
	@Override
	public boolean canUpg() {
		return upg + 1 < names.length;
	}
	@Override
	public void incKill() {
		TDPanel.health += .001*TDPanel.buffer;
		if (abt != null)
			abt.incKill();
		else
			killCount++;
	}
	@Override
	public void upgrade() {
		if (canUpg()) {
			upg++;
			range = ranges[upg];
			splash = splashs[upg];
			dmg =dmgs[upg];
			reloadTime = reloadTimes[upg];
		}
	}
	
	@Override
	public double sellPrice() {
		double price = 0;
		for (int i = 0;i < costs.length && i < upg;i++) {
			price += costs[i];
		}
		return price*TDPanel.sellLoss;
	}

}

