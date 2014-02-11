package Towers;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import Handlers.GifHandler;
import Interfaces.DrawEvent;
import Interfaces.Mob;
import Main.ImageLoader;
import Main.SoundEffect;
import Main.TDPanel;


public class RingTower extends AbstractTower {
	public static final int[] ranges = {90, 95, 100, 125};
	public static final int[] dmgs = {100, 125, 150, 500};
	public static final int[] reloadTimes = {400, 300, 200, 100};
	public static final int[] costs = {400, 350, 900, 8000};
	public static final int[] splashs = {60, 60, 10, 25};
	public static final int cost = costs[0];
	public static final String[] names = {"Zap Tower ", "Electric Frenzy ", "Lightning Mania", "Fire Bloom"};
	public static final String[] bios = {": Melt Candies with Electricity!"
										, ": Now Extra Zappy!"
										, ": Electric Frenzy!"
										, ": ZAP ZAP ZAP"};
	public static final Color[] colors = {Color.orange, new Color(85, 50, 80), new Color(50, 50 ,50), Color.darkGray};
	//private long lastShot;
	private static BufferedImage[] tesla;
	private int count = 0;
	private int frame;
	
	/*
	 *  Constructor.  Used for testing tower at a higher upgrade to start.
	 */
	public RingTower(double x, double y, int id, int upg) {
		super(new Point2D.Double(x , y), ranges[0], costs[0], splashs[0], dmgs[0], reloadTimes[0], 1, id);
		upg = 0;
		init();
		frame = 0;
	}
	
	public RingTower() {
		super();
		frame = 0;
	}
	

	/*
	 * Constructor using coordinates to specify location.
	 */
	public RingTower(double x, double y, int id) {
		super(new Point2D.Double(x , y), ranges[0], costs[0], splashs[0], dmgs[0], reloadTimes[0], 1, id);
		upg = 0;
		init();
	}
	
	@Override
	public void defaultStats() {
		easyDef(new Point2D.Double(0 , 0), ranges[0], costs[0]
		           , splashs[0], dmgs[0], reloadTimes[0], 1, TDPanel.incTowID());
		upg = 0;
		init();
	}

	
	public void init() {
		if (tesla == null) {
			tesla = new BufferedImage[4];
			tesla[0] = ImageLoader.loadImage("resources/images/Towers/tesla0.png");
			tesla[1] = ImageLoader.loadImage("resources/images/Towers/tesla1.png");
			tesla[2] = ImageLoader.loadImage("resources/images/Towers/tesla2.png");
			tesla[3] = ImageLoader.loadImage("resources/images/Towers/tesla3.png");
		}
	}


	
	@Override
	public BufferedImage getPic() {
		if (tesla == null)
			init();
		return tesla[0];
	}
	
	@Override
	public void update() {
		if (isFailing()) { // stops tower if failing
			lastShot = System.nanoTime()/1000000+1000;
		}
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
	public int r(double x) {
		return (int)Math.round(x);
	}
	@Override
	public void draw(Graphics2D g2d) {
		AffineTransform at = new AffineTransform();
		double xbar = GifHandler.SplashTower.sequence[count].getWidth()/2;
		double ybar = GifHandler.SplashTower.sequence[count].getHeight()/2;
		at.translate(loc.x -  xbar, loc.y - ybar);
				
		g2d.drawImage(GifHandler.SplashTower.sequence[count], at, null);
		
		if (TDPanel.frame() % 2 == 0) {
			count++;
			count %= GifHandler.SplashTower.sequence.length;
		}
		
		if (!isReloading()) { // reloading is checked first since isReloading is a cheap call compared to nextMob
			ArrayList<Mob> close = mh.proxMobs(loc, ranges[upg], 40);
			if (close != null) {
				for (Mob m: close) {
					m.defend(dmgs[upg], this);
					m.setStatus(1, 500*upg+500, this);
					
					AffineTransform at2 = new AffineTransform();
					double dir = Math.atan2(-loc.y + m.getLoc().y, -loc.x + m.getLoc().x);
					double distx = loc.x - m.getLoc().x;
					double disty = loc.y - m.getLoc().y;
					if (frame >= GifHandler.Lightning.sequence.length)
						frame = 0;
					BufferedImage next = GifHandler.Lightning.sequence[frame];
					at2.setToRotation(dir - Math.PI/2, loc.x - next.getWidth()/2 + xbar, loc.y-next.getHeight()/2 + ybar); // rotates tower	
					at2.translate( loc.x - next.getWidth()/2 + xbar, loc.y-next.getHeight()/2 + ybar);  // the -15 was found experimentally lol
					GifHandler.Lightning.draw(at2, 3);
				}
				lastShot = System.nanoTime()/1000000;
				//GifHandler.WrapperTower.draw((int) loc.x, (int)loc.y, 2);
				SoundEffect.SWEEP.play();
			}
		}
		
		
		
		frame++;
		if (frame > GifHandler.Lightning.sequence.length)
			frame = 0;
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
		TDPanel.health -= .5*(upg+1);
	}
}