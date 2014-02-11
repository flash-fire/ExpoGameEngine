package Mobs;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.awt.image.ShortLookupTable;

import Bullets.GasBullet;
import Interfaces.Bullet;
import Interfaces.Mob;
import Main.ImageLoader;
import Main.MonsterPath;
import Main.TDPanel;
import Towers.GasTower;
import Towers.SplashTower;
import Interfaces.Tower;


public class RainbowMob implements Mob{
	// Houston
	protected Point2D.Double loc;
	private double dir; // direction of mob
	protected double vel; // Specifies PentaMob's speed
	protected int hp, maxHP, hpPrev; // Mob's health
	
	public static final int goldOnDeath = 1;
	public boolean isKillable = false;
	private int stage;
	private Tower killer;
	public static final int[] hpStage = {100,200,400,500,600,750,800,1000,2000,3000,4000,5000};
	public static final double[] vels = {1, 1, 1.4, 1.8, 3, 4, 5, 4, 3, 1.8, 1.4, 1};
	public static final Color[] colors = new Color[] {new Color(255,0,0), new Color(161,0,0), 
		new Color(162,82,3), new Color(161,161,0), new Color(46,72,0),
		new Color(7,132,70), new Color(0,130,130), new Color(0, 86, 130),
		new Color(0,0,86), new Color(106,0,106), new Color(43,0,87),
		new Color(119,0,60)}; 
	public static double[][] colorRats = {{1 , 0, 0}, {.63, 0, 0}, 
			{.635, .32, .01}, {.63, .63, 0}, {.18, .282, 0}, 
			{.027, .517, .275}, {0, .51, .51}, {0, .337, .51},
			{0, 0, .337}, {.416, 0, .416}, {.168, 0, .341},
			{.467, 0, .235}}; // used in color calculations
	
	// These three should be combined into one awesome class.  As well as extra code in move.
	protected static MonsterPath path;
	private double distLeft = 0;
	private int countPath;
	private Point2D.Double next;
	private boolean[] status;
	private int statusTime[];
	private boolean immune = false;
	
	public static BufferedImage body; // 7.35kb in size
	public static BufferedImage[] currBody; // ~85kb in size.
	private int count = 0;
	
	/*
	 * Constructor using a point to specify location-- used in orange respawner to fix stuff.
	 */
	public RainbowMob(Point2D.Double loc, MonsterPath p, int select, int stage, boolean immune){
		this.immune = immune;
		this.loc = loc;
		hp = hpStage[stage];
		maxHP = hpStage[stage];
		vel = vels[stage];
		path = p;
		next = p.next(select);
		countPath = select;
		distLeft = loc.distance(next);
		dir = path.getDir(loc, next);
		this.stage = stage;
		status = new boolean[2];
		statusTime = new int[2];
		initImage();
		hpPrev = hp;
	}
	
	public RainbowMob(int i) {
		this.immune = false;
		stage = i;
		this.loc = (Point2D.Double) TDPanel.source.clone();
		hp = hpStage[i];
		maxHP = hpStage[i];
		vel = vels[i];
		path = TDPanel.getPath();
		next = path.next(0);
		countPath = 0;
		distLeft = loc.distance(next);
		dir = path.getDir(loc, next);
		status = new boolean[2];
		statusTime = new int[2];
		initImage();
		hpPrev = hp;
	}

	private void initImage() {
		if (body == null) {
			body = ImageLoader.loadImage("resources/images/Mobs/skittle.png"); 
			currBody = new BufferedImage[12];
			for (int i = 0;i < currBody.length; i ++) {
				currBody[i] = new BufferedImage(body.getWidth(), body.getHeight(), BufferedImage.TYPE_INT_ARGB);
				setColor(currBody[i], i);
			}
		}
	}
	
	/*
	 * Sets color on buffered image b to stage i.
	 */
	private void setColor(BufferedImage b, int i) {
		b = getColorOp(colorRats[i][0], colorRats[i][1], colorRats[i][2]).filter(body, b);
	}
	/*
	 * Returns the blended color based on HP
	 */
	private LookupOp blend() {
		short[] red = new short[256];
		short[] green = new short[256];
		short[] blue = new short[256];
		short[] alpha = new short[256];

		for (short i = 0; i < 256; i++) {
			 double rat = (0.0D + hp)/(0.0D + hpStage[stage]);
			if (stage == 0) {
			  green[i] = (short) (rat*colorRats[stage][2]*i);
			  blue[i] = (short) (rat*colorRats[stage][1]*i);
			  red[i] = (short) (rat*colorRats[stage][0]*i);
			}
			else {
				  green[i] = (short) ((rat*colorRats[stage][1] + (1D-rat)*colorRats[stage-1][1])*(i));
				  blue[i] = (short) ((rat*colorRats[stage][2] + (1D-rat)*colorRats[stage-1][2])*(i));
				  red[i] = (short) ((rat*colorRats[stage][0] + (1D-rat)*colorRats[stage-1][0])*(i));
			}
		  alpha[i] = i;
		}
		short[][] data = new short[][] {
		    red, green, blue, alpha
		};
		LookupTable lookupTable = new ShortLookupTable(0, data);
		return new LookupOp(lookupTable, null);
	}
	
	
	private LookupOp getColorOp(double r1, double g1, double b1) {
		short[] red = new short[256];
		short[] green = new short[256];
		short[] blue = new short[256];
		short[] alpha = new short[256];

		for (short i = 0; i < 256; i++) {
		  green[i] = (short) (g1*i);
		  blue[i] = (short) (b1*i);
		  red[i] = (short) (r1*i);
		  alpha[i] = i;
		}
		short[][] data = new short[][] {
		    red, green, blue, alpha
		};
		LookupTable lookupTable = new ShortLookupTable(0, data);
		return new LookupOp(lookupTable, null);
	}
	
	/*
	 * Draw method for PentaMob.  Makes a pretty rectangle
	 */
	@Override
	public void draw(Graphics2D g2d) {	
		if (stage > 7)
			body = blend().filter(currBody[stage], body);
	
		AffineTransform at = new AffineTransform();
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .90f));
		at.setToRotation(dir, loc.x, loc.y);
		at.translate(loc.x - body.getWidth()/2, loc.y - body.getHeight()/2);
		if (stage > 7)
			g2d.drawImage(body, at, null);
		else
			g2d.drawImage(currBody[stage], at, null);
		// Body
		if (status[0]) {
			g2d.drawImage(body, at, null);
		}
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
	}

	/*
	 * Abbreviating the rounding calls inside of the code.
	 */
	protected int r(double n) {
		return (int) Math.round(n);
	}

	/*
	 * Returns whether this PentaMob is alive.
	 */
	@Override
	public boolean isAlive() {
		return hp > 0;
	}

	/*
	 * (non-Javadoc)
	 * @see Mob#getLoc()
	 */
	@Override
	public Point2D.Double getLoc() {
		return loc;
	}

	/*
	 * (non-Javadoc)
	 * @see Mob#isRemoveable()
	 */
	@Override
	public boolean isRemoveable() {
		return isKillable;
	}

	/*
	 * Returns the stage of the monster.
	 */
	public int getStage() {
		return stage;
	}

	/*
	 * This specifies how the monster will move.
	 */
	private void move() {
		if (TDPanel.god)
			return;
		double speed = TDPanel.speedFactor;
		if (status[1])
			speed /= 2;
		
		if (distLeft <= 0) {           
			next = path.next(++countPath);
			dir = path.getDir2(loc, countPath);
			distLeft = loc.distance(next);

		}
		distLeft -= vel * speed;

		// Coordinates of new Location
		double newX = loc.x + Math.cos(dir)*vel*speed;
		double newY = loc.y + Math.sin(dir)*vel*speed;

		if (path.targIsEnd(countPath) && distLeft <= 0) { // If heading towards end AND the end is reached!
			
			TDPanel.health +=  ((10d+stage)*(TDPanel.buffer)); // you get some blood sugar man!
			isKillable = true; // A monster can only strike once.  May change this.
		}		
		loc.setLocation(newX, newY);
	}


	/*
	 * Specifies how a Pentamob should act.
	 */
	@Override
	public void update() {
		for (int i = 0;i<status.length;i++) {
			if (status[i]) {
				statusTime[i]--;
				if (statusTime[i] <= 0) {
					status[i] = false;
				}
			}
		}
		if (isAlive()) {
			move();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see Mob#defend(BasicBullet)
	 */
	@Override
	public void defend(Bullet b) {
		defend(b.getDmg(), b.getShooter());
	}

	@Override
	public void defend(int dmg, Tower t) {
		killer = t; // killer is the last tower to attack mob.  So this will set the killer to the last tower guaranteed.
		if (stage != 7 || !(t instanceof SplashTower))
			hp -= dmg;
		else {
			hp -= dmg/12;
		}
	}
	@Override
	public void onDeath() {
		if (! TDPanel.hyperMode) {
			TDPanel.gold += goldOnDeath;
		}
		else if (stage % 4 == 0) {
			TDPanel.gold += goldOnDeath;
		}
		TDPanel.score += TDPanel.speedFactor;
		// Gas tower checks
		if (status[0] && ! immune) { // I would love to do this in the on death method, but I ran into some bugs guaranteeing the shooter was the tower.
			double dir = Math.PI*2*Math.random();
			if (killer == null || !killer.canUpg()) {
				TDPanel.getBulletHandler().add(new GasBullet(loc, dir, 9, 25, 25, killer));
			}
			else
				TDPanel.getBulletHandler().add(new GasBullet(loc, dir, 9, 25, 2, killer));
		}

		if (stage != 0) { // Decrements stage by one.  
			stage--;
			hp = hpStage[stage];
			maxHP = hpStage[stage];
			vel = vels[stage];
			if (killer != null && !status[0])
				killer.incKill();
		}
		else {
			isKillable = true;
			if (killer != null && !status[0])
				killer.incKill();
		}
	}


	@Override
	public int getPathLoc() {
		return countPath;
	}

	@Override
	public double getDistLeft() {
		return distLeft;
	}

	@Override
	public void setStatus(int x, int time, Tower t) {
		if (!immune || (t instanceof GasTower && !t.canUpg())) {
			status[x] = true;
			statusTime[x] = time;
		}

	}

	@Override
	public boolean[] getStatus() {
		return status;
	}
}