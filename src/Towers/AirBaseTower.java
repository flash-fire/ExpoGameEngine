package Towers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import Handlers.TowerHandler;
import Main.ImageLoader;
import Main.SoundEffect;
import Main.TDPanel;


public class AirBaseTower extends AbstractTower {

	private TowerHandler th; // since airbase edits tower list, it needs a TowerHandle reference
	public static final int[] costs = {700, 2000, 4700};
	public static final int cost = costs[0];
	public static final int[] maxSpawns = {4, 6, 10};
	public static final String[] names = {"Airbase", "AirHub", "Airport"};
	public static final String[] bios = { ": Group of Gliders, Weak Attack."
		, ": Spawns more flying candy!"
		, ": Spawns more and stronger flying towers!"}; 
	public static final Color[] colors = {Color.green, new Color(150, 150, 225), new Color(100, 250, 100)};
	private int currSpawn = 0;
	private static BufferedImage airport;
//	private int count = 0;

	private volatile boolean updated = false;
	private Thread addMob;



	public AirBaseTower() {
		super();
	}
	/*
	 * Constructor for airbaseTower!
	 */
	public AirBaseTower(TowerHandler th, double x, double y, int upg, int id) {
		super(new Point2D.Double(x,y), 0, cost, 0, 0, 0, 1, id); // airbase tower doesn't have much
		this.th = th;
		this.upg = upg;
		currSpawn = 0;
		initPic();
		initThread();
	}

	/*
	 * Default upg is 0.
	 */
	public AirBaseTower(double x, double y, int id) {
		super(new Point2D.Double(x,y), 0, 0, 0, 0, 0, 1, id); // airbase tower doesn't have much
	}

	private void initThread() {
		addMob = new Thread(new Runnable()
		{
			@Override
			public void run() { // Creates the flying towers in a seperate thread so the main thread doesn't get halted.
				while (!isRemoveable)	{  // might glitch on next if statement: orig is "currSpawn < maxSpawn"
					if (currSpawn < maxSpawns[upg]) {
						if (!TDPanel.isPaused()  && !isFailing()) {
							currSpawn++;
							th.add(new FlyingTower((int) Math.round(Math.random()*500), (Point2D.Double)loc.clone(), AirBaseTower.this, upg));
							if (Math.random() > .85) // plays ambient jet noise
								SoundEffect.AIRPORT.play();
						}
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}		
		});
	}

	@Override
	public void update() {
		if (!updated) { // if the spawning thread has not started, start it!
			updated = true;
			// start the thread
			addMob.start();
		}
	}

	protected void dec() {
		currSpawn--;
	}

	private void initPic() {
		airport = ImageLoader.createResizedCopy(ImageLoader.loadImage("resources/images/Towers/airport.png"), 30, 50, false);
	}

	@Override
	public void draw(Graphics2D g2d) {
		AffineTransform at = new AffineTransform();
		at.translate(loc.x - airport.getWidth()/2, loc.y - airport.getHeight()/2);
//		if (TDPanel.frame() % 2 == 0) {
//			count++;
//			count %= GifHandler.WrapperTower.sequence.length;
//		}
		g2d.drawImage(airport, at, null);
	}

	public int r(double x) {
		return (int) Math.round(x);
	}

	@Override
	public BufferedImage getPic() {
		if (airport == null) {
			initPic();
		}
		return airport;
	}

	@Override
	public String getBio() {
		return names[upg] + bios[upg];
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
	public void upgrade() {
		if (canUpg()) {
			failHP += TDPanel.healthDef*TDPanel.failFactor;
			upg++;
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


	/*
	 * (non-Javadoc)
	 * Sets tower to default factory settigns. :D
	 * @see Towers.AbstractTower#defaultStats()
	 */
	@Override
	public void defaultStats() {
		loc = new Point2D.Double(0,0);
		range = 0;
		initCost = cost;
		splash = 0;
		dmg = 0;
		reloadTime = 0;
		mode = 1;
		id = TDPanel.incTowID();
		th = TDPanel.getTowerHandler();
		upg = 0;
		currSpawn = 0;
		initPic();
		initThread();
	}
}