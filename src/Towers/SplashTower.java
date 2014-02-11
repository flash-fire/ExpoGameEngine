package Towers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import Interfaces.Bullet;
import Interfaces.Mob;
import Bullets.SplashBullet;
import Handlers.AbstractHandler;
import Main.ImageLoader;
import Main.SoundEffect;
import Main.TDPanel;


public class SplashTower extends AbstractTower {

	/*
	 * The arrays are used to signify the values of fields at various upgrades.
	 */
	public static final int[] ranges = {150, 200, 250};
	public static final int[] costs = {800, 1000, 4000};
	public static final int[] splashs = {30, 40, 50};
	public static final int[] dmgs = {200, 500, 750};
	public static final int[] reloadTimes = {2000, 2000, 800};
	public static final int cost = costs[0];
	public double dir = 0;
	public static final String[] names = {"Ice Cream Tower", "Praline Dream", "Sugar Blast"};
	public static final String[] bios = { ": Shoots scoops of ice cream!" 
										, ": Mauls 'em with sugar!"
										, ": Sugar Overdose!"};
	
	public static final Color[] colors = {new Color(34, 0, 255), new Color(156, 230, 187), new Color(82, 45, 26)};

	private static BufferedImage[] balls;
	private AbstractHandler<Bullet> bh;
	private int count = 0;
	
	public SplashTower(double x, double y, int id, int upg) {
		super(new Point2D.Double(x,y), ranges[upg], costs[upg], splashs[upg], dmgs[upg], reloadTimes[upg], 0, id);
		this.upg = upg;
		bh = TDPanel.getBulletHandler();	
		initPic();
	}
	
	
	public SplashTower(Point2D.Double loc, int id) {
		super(loc, ranges[0], costs[0], splashs[0], dmgs[0], reloadTimes[0], 1, id);
		upg = 0;
		bh = TDPanel.getBulletHandler();
		initPic();
	}
	
	public SplashTower() {
		super();
	}
	
	@Override
	public void defaultStats() {
		easyDef(new Point2D.Double(0 , 0), ranges[0], costs[0]
		           , splashs[0], dmgs[0], reloadTimes[0], 1, TDPanel.incTowID());
		bh = TDPanel.getBulletHandler();	
		initPic();
	}

	
	public void initPic() {
		if (balls == null) {
			balls = new BufferedImage[4];
			balls[0] = ImageLoader.loadImage("resources/images/Towers/ball2.png");
			balls[1] = ImageLoader.loadImage("resources/images/Towers/ball3.png");
			balls[2] = ImageLoader.loadImage("resources/images/Towers/ball4.png");
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see GameObject#update()
	 */
	@Override
	public void update() {
		if (isFailing()) // halts tower progress when failing
			lastShot = System.nanoTime()/1000000+1000;
			
		if (!isReloading()) { // reloading is checked first since isReloading is a cheap call compared to nextMob
			Mob closestMob = super.nextMob();
			if (closestMob != null) { // check if next mob is null
				SoundEffect.CHEWBACCA.play();
				lastShot = System.nanoTime()/1000000;
				bh.add(new SplashBullet((Point2D.Double)loc.clone(), closestMob.getLoc(),splash, dmgs[upg], 8.1, this));
				bh.add(new SplashBullet((Point2D.Double)loc.clone(), closestMob.getLoc(),splash, dmgs[upg], 1, this));
				TDPanel.health -= upg*3;
				Point2D.Double mobLoc = closestMob.getLoc();
				dir = Math.atan2(mobLoc.y-loc.y,mobLoc.x-loc.x);
			}
		}
	}

	@Override
	public String getBio() {
		return names[upg] + bios[upg];
	}

	@Override
	public void draw(Graphics2D g2d) {
		AffineTransform at = new AffineTransform();
		at.setToRotation(dir+Math.PI,loc.x,loc.y); // rotates tower	
		at.translate(loc.x - balls[upg].getWidth()/2, loc.y - balls[upg].getHeight()/2);
		if (isFailing())
			g2d.drawImage(balls[0], at, null); // fail draws different image
		else 
			g2d.drawImage(balls[upg], at, null);
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
	
	/*
	 * (non-Javadoc)
	 * @see Tower#getPic()
	 */
	@Override
	public BufferedImage getPic() {
		if (balls == null)
			initPic();
		return balls[0];
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