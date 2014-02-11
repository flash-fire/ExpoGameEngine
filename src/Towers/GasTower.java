package Towers;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import Bullets.BasicBullet;
import Bullets.GasBullet;
import Bullets.SplashBullet;
import Handlers.AbstractHandler;
import Interfaces.Bullet;
import Interfaces.Mob;
import Main.ImageLoader;
import Main.SoundEffect;
import Main.TDPanel;
import Mobs.GaseousMob;


public class GasTower extends AbstractTower {
	
	public static final int[] ranges = {150, 200, 250, 400};
	public static final int[] costs = {2000, 2000, 4000, 6000};
	public static final int[] splashs = {25, 75, 100, 125};
	public static final int[] dmgs = {5, 10, 15, 50};
	public static final int[] reloadTimes = {5000, 4500, 1000, 800};
	public static final int cost = costs[0];
	
	public static final String[] names = {"Gas Tower", "Gasp Tower", "Gasket Blower", "Gascade"};
	public static final String[] bios = { ": Bring in the gas! Slows and Infects." 
										, ": Gives a gas aura!"
										, ": That which causes unending destruction"
										, ": This tower is amazing."};
	private static BufferedImage[] gasPics;
	private AbstractHandler<Bullet> bh;
	
	public GasTower(double x, double y, int id) {
		super(new Point2D.Double(x , y), ranges[0], costs[0], splashs[0], dmgs[0], reloadTimes[0], 1, id);
		bh = TDPanel.getBulletHandler();
		upg = 0;
		initPic();
	}
	
	public GasTower() {
		super();
	}
	
	@Override
	public void defaultStats() {
		easyDef(new Point2D.Double(0 , 0), ranges[0], costs[0]
		           , splashs[0], dmgs[0], reloadTimes[0], 1, TDPanel.incTowID());
		bh = TDPanel.getBulletHandler();
		upg = 0;
	}

	private void initPic() {
		if (gasPics == null) {
			gasPics = new BufferedImage[4];
			gasPics[0] = ImageLoader.loadImage("resources/images/Towers/GasTower1.png");
			gasPics[1] = ImageLoader.loadImage("resources/images/Towers/GasTower2.png");
			gasPics[2] = ImageLoader.loadImage("resources/images/Towers/GasTower3.png");
			gasPics[3] = ImageLoader.loadImage("resources/images/Towers/GasTower4.png");
		}
	}
	
	@Override
	public BufferedImage getPic() {
		if (gasPics == null)
			initPic();
		return gasPics[0];
	}
	
	@Override
	public void update() {
		if (isFailing())
			return;
		if (!isReloading()) {
			Mob closestMob = super.nextMob();
			if (closestMob != null) {
				if (upg == 3) {
					double rand = Math.random();
					if (rand > .2 && rand < .5)
						bh.add(new SplashBullet((Point2D.Double)loc.clone(), closestMob.getLoc(),splash, dmg*10, 8.1, this));
					else if (rand > .5) {
						ArrayList<Mob> near = mh.proxMobs(loc, range/3, 100);
						for (Mob m: near) {
							m.defend(dmg*250, this);
							if (m instanceof GaseousMob)
								m.defend(dmg*1000, this);
						}
					}
						
					else if (rand > .9) {
						//bh.add(new BasicBullet((Point2D.Double)loc.clone(), closestMob.getLoc(), 9.5, dmg, splash, null));
						Point2D.Double mobLoc = closestMob.getLoc();
						double dir = Math.atan2(mobLoc.y-loc.y,mobLoc.x-loc.x);
						bh.add(new BasicBullet((Point2D.Double)loc.clone(), dir, 5.5, dmg, splash,
								this, upg));
					}
				}		
				lastShot = System.nanoTime()/1000000;
				SoundEffect.RAYGUN.play();
				bh.add(new GasBullet((Point2D.Double) loc.clone(), closestMob.getLoc(), 25000, splash, dmg, this));
			}
		}
	}
	
	/*
	 * Draws basic tower.
	 */
	@Override
	public void draw(Graphics2D g2d) {	
		AffineTransform at = new AffineTransform();
		at.translate(loc.x - gasPics[upg].getWidth()/2, loc.y - gasPics[upg].getHeight()/2);
		if (isFailing())
			g2d.drawImage(gasPics[0], at, null);
		else
			g2d.drawImage(gasPics[upg], at, null);
	} 
	
	/*
	 * Rounding shorthand.
	 */
	protected int r(double x) {
		return (int) Math.round(x);
	}
	
	@Override
	public String toString() {
		return names[upg];
	}
	
	@Override
	public int getUpgCost() {
		if (!canUpg())
			return costs[upg];
		return costs[upg+1];
	}

	@Override
	public String getBio() {
		return names[upg] + bios[upg];
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
	public void upgrade() {
		if (canUpg()) {
			failHP += TDPanel.healthDef*TDPanel.failFactor;
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
		for (int i = 0;i < costs.length && i <= upg;i++) {
			price += costs[i];
		}
		return price*TDPanel.sellLoss;
	}
}