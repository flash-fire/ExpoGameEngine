package Mobs;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import Bullets.GasBullet;
import Interfaces.Bullet;
import Interfaces.Mob;
import Interfaces.Tower;
import Main.GamePanel;
import Main.MonsterPath;
import Main.TDPanel;
import Towers.GasTower;
import Towers.SplashTower;

public class GaseousMob implements Mob {

	private long health;
	private long maxHP;
	private Point2D.Double loc;  
	private boolean[] status; // monster status code
	private int[] statusTime; // time left on status
	private double distLeft; // distance to next point
	private int countPath; // array index of next point in path
	private Point2D.Double next;
	private MonsterPath path; // path that mob follows
	private boolean isKillable = false;
	private double dir;
	private double vel;
	public static final int goldOnDeath = 1;
	private long powerCons;
	private Tower killer;

	// Default
	public GaseousMob() {
		this.powerCons = TDPanel.frame();
		this.loc = (Point2D.Double) TDPanel.source.clone();
		maxHP = (int) (300 + powerCons/5);
		health = maxHP;
		vel = 3; // this is what causes the game to be lost eventually.  These guys can't be stopped later in game.
		path = TDPanel.getPath();
		next = path.next(0);
		countPath = 0;
		distLeft = loc.distance(next);
		dir = path.getDir(loc, next);
		status = new boolean[2];
		statusTime = new int[2];
	}
	
	GaseousMob(Point2D.Double loc, MonsterPath p, int select, long powerCons){
		this.powerCons = powerCons;
		this.loc = loc;
		maxHP= (int) (300 + powerCons/5);
		health = maxHP;
		vel = 3 + powerCons/5000; // this is what causes the game to be lost eventually.  Thse guys can't be stopped later in game.
		path = p;
		next = p.next(select);
		countPath = select;
		distLeft = loc.distance(next);
		dir = path.getDir(loc, next);
		status = new boolean[2];
		statusTime = new int[2];
	}
		
	
	@Override
	public void draw(Graphics2D g2d) {
		if (health > maxHP*2/3)
			g2d.setColor(Color.white);
		else if( health > maxHP/3)
			g2d.setColor(Color.LIGHT_GRAY);
		else
			g2d.setColor(Color.red);
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
		g2d.fillOval(r(loc.x), r(loc.y), 16, 13);
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
		return health >= 0;
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
		return health <= 0 || isKillable;
	}

	
	/*
	 * This specifies how the monster will move.
	 */
	private void move() {
		if (distLeft <= 0) { // If time to update direction
			next = path.next(++countPath); // update next target point
			assert next != null: "We need to edit the gassymob move code to handle nulls.";
			dir = path.getDir2(loc, countPath);
			distLeft = loc.distance(next);
		}
		double speed = TDPanel.speedFactor;
		if (status[0])
			speed /= 2;
		distLeft -= vel*speed;
		
		// Coordinates of new Location
		double newX = loc.x + Math.cos(dir)*vel*speed;
		double newY = loc.y + Math.sin(dir)*vel*speed;
		
		if (path.targIsEnd(countPath) && distLeft <= 0) { // If heading towards end AND the end is reached!
			TDPanel.health -= 10;
			isKillable = true; // A monster can only strike once.  May change this.
		}		
		loc.setLocation(newX, newY);
	}
	
	
	/*
	 * Specifies how a Pentamob should act.
	 */
	@Override
	public void update() {
		for (int i = 0;i < status.length;i++) {
			if (statusTime[i] > 0)
				statusTime[i]--;
			if (statusTime[i] <= 0)
				status[i] = false;
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
		if (b instanceof GasBullet && !b.getShooter().canUpg()) // ignores gasbullets
			return;
		if (health > 0) {
			health -= b.getDmg();
			if (health <= 0) {
				if (! TDPanel.hyperMode) {
				TDPanel.gold += goldOnDeath;
				}
				b.getShooter().incKill();
				killer = b.getShooter();
			}
		}
	}
	
	@Override
	public void defend(int dmg, Tower t) {
		if (t instanceof SplashTower) // ignores splash tower damage
			return;
		health -= dmg;
		if (health <= 0) {
			t.incKill();
			if (killer == null)
				killer = t;
		}
	}
	

	@Override
	public void onDeath() {
		TDPanel.score += (powerCons+1)*TDPanel.speedFactor;
		if (killer != null)
			if (killer instanceof GasTower && !killer.canUpg()) {
				double tarx = Math.random() * GamePanel.PWIDTH;
				double tary = Math.random() * GamePanel.PHEIGHT;
				TDPanel.getBulletHandler().add(new GasBullet(loc, new Point2D.Double(tarx, tary), 200, 25, 6, killer));
			}
	}
	
	@Override
	public int getPathLoc() {
		return 0;
	}

	@Override
	public double getDistLeft() {
		return distLeft;
	}

	@Override
	public void setStatus(int x, int time, Tower t) {
		if (x != 1 && !t.canUpg()) {
			status[x] = true;
			statusTime[x] = time;
		}
	}

	@Override
	public boolean[] getStatus() {
		return status;
	}
	
}
