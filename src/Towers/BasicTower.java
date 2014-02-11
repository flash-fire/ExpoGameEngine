package Towers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import Bullets.BasicBullet;
import Handlers.AbstractHandler;
import Handlers.GifHandler;
import Interfaces.Bullet;
import Interfaces.Mob;
import Main.ImageLoader;
import Main.SoundEffect;
import Main.TDPanel;

public class BasicTower extends AbstractTower {

	// Variables: the array determine the value for the tower at all upgrades.
	public static final int[] ranges = {110, 125, 150, 175};
	public static final int[] dmgs = {150, 200, 300, 400};
	public static final int[] reloadTimes = {750, 500, 350, 333};
	public static final int[] costs = {100, 160, 450, 1300};
	public static final int cost = costs[0];
	public static final int[] splashs = {50, 50, 55, 60};
	public static final int[] bulletsCount = {1, 2, 4, 16};
	public static final String[] names = {"Harvester", "Super Harvester", "Sugar Sucker", "Surge"};
	public static final String[] bios = {": Sweets Harvester. Increase SUGAR!"
		, ": Twice the fun as Basic Tower!"
		, ": Twice the BANG for the buck!"
		, ": 16 bullets.  True love found until you sugar overload!"};

	public static final Color[] colors = {Color.red, new Color(255, 50, 50), new Color(235, 86, 100), new Color(235, 255, 100)};
	private AbstractHandler<Bullet> bh;
	private double dir = 0;
	private int upg;
	private static BufferedImage[] basicPics;
	static int clipNum = 0;
	int count = 0;

	public BasicTower() {
		super();
		bh = TDPanel.getBulletHandler();
		upg = 0;
		init();
	}
	/*
	 *  Constructor.  Takes given starting loc and adds a tower there.
	 */
	BasicTower(Point2D.Double loc, int id) {
		super(loc, ranges[0], costs[0], splashs[0], dmgs[0], reloadTimes[0], 1, id);
		bh = TDPanel.getBulletHandler();
		upg = 0;
		init();
	}

	/*
	 * Constructor using coordinates to specify location.
	 */
	public BasicTower(double x, double y, int id) {
		super(new Point2D.Double(x , y), ranges[0], costs[0], splashs[0], dmgs[0], reloadTimes[0], 1, id);
		bh = TDPanel.getBulletHandler();
		upg = 0;
		init();


	}

	@Override
	public void defaultStats() {
		easyDef(new Point2D.Double(0 , 0), ranges[0], costs[0]
		                                                    , splashs[0], dmgs[0], reloadTimes[0], 1, TDPanel.incTowID());
		bh = TDPanel.getBulletHandler();
	}


	public void init() {
		if (basicPics == null) {
			basicPics = new BufferedImage[4];
			basicPics[0] = ImageLoader.loadImage("resources/images/Towers/Basic1.png");
			basicPics[1] = ImageLoader.loadImage("resources/images/Towers/Basic2.png");
			basicPics[2] = ImageLoader.loadImage("resources/images/Towers/Basic3.png");
			basicPics[3] = ImageLoader.loadImage("resources/images/Towers/Basic4.png");
		}
		count = (int) (TDPanel.frame() % GifHandler.WrapperTower.sequence.length);
	}
	/*
	 * (non-Javadoc)
	 * Returns default image.
	 * @see Interfaces.Tower#getPic()
	 */
	@Override
	public BufferedImage getPic() {
		if (basicPics == null) {
			init();
		}
		return basicPics[0];
	}


	@Override
	public void update() {
		if (isFailing())
			return;
		if (!isReloading()) {
			Mob closestMob = super.nextMob();
			if (closestMob != null) {
				SoundEffect.BASICSHOT.play();
				Point2D.Double mobLoc = closestMob.getLoc();
				lastShot = System.nanoTime()/1000000;
				dir = Math.atan2(mobLoc.y-loc.y,mobLoc.x-loc.x); // sets tower facing direction
				if (upg == 0)
					bh.add(new BasicBullet((Point2D.Double)loc.clone(), dir, 3.5, dmg, splash, this, 0));
				else if(upg == 1) {
					bh.add(new BasicBullet((Point2D.Double)loc.clone(), dir+.1, 3.5, dmg, splash, this, 1));
					bh.add(new BasicBullet((Point2D.Double)loc.clone(), dir-.1, 3.5, dmg, splash, this, 1));
				}
				else if(upg == 2) {
					bh.add(new BasicBullet((Point2D.Double)loc.clone(), dir+.1, 3.5, dmg, splash, this, 2));
					bh.add(new BasicBullet((Point2D.Double)loc.clone(), dir-.1, 3.5, dmg, splash, this, 2));
					bh.add(new BasicBullet((Point2D.Double)loc.clone(), dir+.2, 3.5, dmg, splash, this, 2));
					bh.add(new BasicBullet((Point2D.Double)loc.clone(), dir-.2, 3.5, dmg, splash, this, 2));
				}
				else if (upg == 3) {
					for (int i = 0; i < 16; i++) {
						bh.add(new BasicBullet((Point2D.Double) loc.clone(), dir+.1*i-.8, 9.5, dmg, splash, this, 3));
					}
				}
			}
		}

	}

	/*
	 * Draws basic tower. Draws like a freaking gif.  Lol.
	 */

	@Override
	public void draw(Graphics2D g2d) {
		AffineTransform at = new AffineTransform();
		/*if (TDPanel.frame() % 4 == 0)
			count++;
		BufferedImage[] seq = GifHandler.WrapperTower.sequence;
		count %= seq.length;*/
		at.setToRotation(dir+Math.PI/2d,loc.x
				, loc.y ); // rotates tower	
		at.translate(loc.x - basicPics[upg].getWidth()/2, loc.y - basicPics[upg].getHeight()/2);
		g2d.drawImage(basicPics[upg], at, null);
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

	@Override
	public void incKill() {
		killCount++;
		TDPanel.health += .5*TDPanel.buffer;
	}
}