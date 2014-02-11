package Towers;

import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import Bullets.BasicBullet;
import Handlers.AbstractHandler;
import Interfaces.Bullet;
import Interfaces.Mob;
import Main.ImageLoader;
import Main.SoundEffect;
import Main.TDPanel;


public class FastTower extends AbstractTower {

	// Variables
	public static final int[] ranges = {100, 125, 175};
	public static final int[] costs = {250, 750, 1500};
	public static final int[] splashs = {30, 40, 50};
	public static final int[] dmgs = {270, 360, 900};
	public static final int[] reloadTimes = {100, 50, 25};
	public static final int cost = costs[0];
	public static final String[] names = {"Sweet Turret", "Machine Gun", "Sulfuric Sugar"};
	public static final String[] bios = { ": Rapid Fire Sugar Destroyer!" 
										, ": Machine gun power!"
										, ": Machine gun overload"};
	protected AbstractHandler<Bullet> bh;
	protected static Area shape;
	private static BufferedImage[] turrets;
	double dir = 0;
	
	
	public FastTower() {
		super();
		bh = TDPanel.getBulletHandler();
		upg = 0;
		initGraphic();
		init();
	}
	/*
	 * Constructor using coordinates to specify location.
	 */
	public FastTower(double x, double y, int id) {
		super(new Point2D.Double(x , y), ranges[0], costs[0], splashs[0], dmgs[0], reloadTimes[0], 1, id);
		bh = TDPanel.getBulletHandler();
		upg = 0;
		initGraphic();
		init();
	}
	
	public FastTower(double x, double y, int id, int upg) {
		super(new Point2D.Double(x , y), ranges[upg], costs[upg], splashs[upg], dmgs[upg], reloadTimes[upg], 1, id);
		bh = TDPanel.getBulletHandler();
		this.upg = upg;
		initGraphic();
		init();
	}
	
	@Override
	public void defaultStats() {
		easyDef(new Point2D.Double(0 , 0), ranges[upg], costs[upg]
		     , splashs[upg], dmgs[upg], reloadTimes[upg], 1, id);		
	}
	
	public void init() {
		if (turrets == null) {
			turrets = new BufferedImage[3];
			turrets[0] = ImageLoader.loadImage("resources/images/Towers/turret.png");
			turrets[1] = ImageLoader.loadImage("resources/images/Towers/turret1.png");
			turrets[2] = ImageLoader.loadImage("resources/images/Towers/turret2.png");
		}
	}
	
	/*
	 * (non-Javadoc)
	 * Returns default image
	 * @see Interfaces.Tower#getPic()
	 */
	@Override
	public BufferedImage getPic() {
		if (turrets == null) {
			init();
		}
		return turrets[0];
	}

	public static void initGraphic() {
		Polygon temp = new Polygon();
		temp.addPoint(27, 15);
		temp.addPoint(10, -8);
		temp.addPoint(-15, 0);
		temp.addPoint(15, 15);
		shape = new Area(temp);
	}
	
	@Override
	public void update() {
		if (isFailing())  {// halts tower progress when failing
			//lastShot = System.nanoTime()/1000000+1000;
			return;
		}
		if (!isReloading())  {
			Mob closestMob = super.nextMob();
			if (closestMob != null) {
				lastShot = System.nanoTime()/1000000;
				TDPanel.health -= .135 * (upg + 1);
				SoundEffect.FASTSHOT.play();
				Point2D.Double mobLoc = closestMob.getLoc();
				dir = Math.atan2(mobLoc.y-loc.y,mobLoc.x-loc.x);
				bh.add(new BasicBullet((Point2D.Double)loc.clone(), dir, 3.5, dmg, splash, this, upg));
			}
		}
	}
	
	/*
	 * Draws the tower.
	 */
	@Override
	public void draw(Graphics2D g2d) {
		//g2d.drawString("HP: " + TDPanel.health + " TEST: " + (double) (TDPanel.healthDef*TDPanel.failFactor*upg+50), 100, 100);
		AffineTransform at = new AffineTransform();
		at.setToRotation(dir,loc.x,loc.y); // rotates tower	
		at.translate(loc.x - turrets[upg].getWidth()/2, loc.y - turrets[upg].getHeight()/2);
		if (isFailing())
			g2d.drawImage(turrets[0], at, null);
		else
			g2d.drawImage(turrets[upg], at, null);
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