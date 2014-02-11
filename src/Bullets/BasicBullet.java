package Bullets;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import Interfaces.*;
import Handlers.*;
import Main.*;


public class BasicBullet implements Bullet {
	
	// Variables
	protected Point2D.Double loc; // Specifies location of bullet
	protected double dir; // Specifies bullet direction
	protected double vel; // Bullet velocity [remove in abs]
	protected int dmg; // Specifies damage of bullet. 
	protected boolean isKillable; // is the bullet removable?
	protected MobHandler mh; // mob handler [remove in abs]
	public int splash = 10;
	public final static int dmgCons = 250; // default damage.
	int pierce = 5;
	private Tower shooter;
	private static BufferedImage[] bulletImgs;
	private int upg;
	protected static BufferedImage redBullet = ImageLoader.loadImage("resources/images/Bullets/redBullet.png");
	
	
	/*
	 * Constructor that assumes properties about bullet
	 */
	BasicBullet(Point2D.Double start, Point2D.Double target, Tower shooter, int upgrade) {
		this(start, Math.atan2(target.y-start.y, target.x-start.x), 2, dmgCons, 50, shooter, upgrade);
	}
	
	public BasicBullet(Point2D.Double start, double dir, double vel, int dmg, int splash, Tower shooter) {
		this.shooter = shooter;
		loc = start;
		this.vel = vel;
		this.dir = dir;
		this.dmg = dmg;
		this.splash = splash;
		init(); 
	}

	/*
	 * Constructor that assumes properties about bullet
	 */
	BasicBullet(Point2D.Double start, Point2D.Double target, int splash, int dmg, double vel, Tower shooter) {
		this(start, Math.atan2(target.y-start.y, target.x-start.x), vel, dmg, splash, shooter);
	}
	
	public BasicBullet(Double clone, double dir, double vel, int dmg2, int splash2,
			Tower basicTower, int i) {
		this(clone,dir,vel,dmg2,splash2,basicTower);
		this.upg = i;
	}

	private void init() {
		mh = TDPanel.getMobHandler();
		if (bulletImgs == null) {
			bulletImgs = new BufferedImage[4];
			bulletImgs[0] = ImageLoader.loadImage("resources/images/Bullets/basicBullet0.png");
			bulletImgs[1] = ImageLoader.loadImage("resources/images/Bullets/basicBullet1.png");
			bulletImgs[2] = ImageLoader.loadImage("resources/images/Bullets/basicBullet2.png");
			bulletImgs[3] = ImageLoader.loadImage("resources/images/Bullets/basicBullet3.png");
		}

		if (shooter != null) {
			upg = shooter.getUpg();
		}
		else
			upg = 3;
	}

	 
	/*
	 * Abbreviated method for rounding [to simplify code]
	 */
	protected int r(double n) {
		return (int) Math.round(n);
	}
	
	/*
	 * Returns damage
	 */
	@Override
	public int getDmg() {
		return dmg;
	}
	
	/*
	 * getLoc() returns the bullet's location as a Point.
	 */
	@Override
	public Point2D.Double getLoc() {
		return loc;
	}
	
	/*
	 * Specifies how a bullet moves.
	 */
	protected void move() {
		double speed = TDPanel.speedFactor;
		// Coordinates of new Location
		double newX = loc.x + Math.cos(dir)*vel*speed;
		double newY = loc.y + Math.sin(dir)*vel*speed;
		
		// Handle edges
		if (newX <= 0 || newX >= GamePanel.PWIDTH) {
			isKillable = true;
			return;
		}
		
		if (newY <= 0 || newY >= GamePanel.PHEIGHT) {
			isKillable = true;
			return;
		}

		loc.setLocation(new Point2D.Double(newX, newY));
	}

	
	/*
	 * Specifies what a bullet does during an update
	 */
	@Override
	public void update() {
		if (! isKillable) {		
			// Handle moving phase
			move();		
			// Handle attack phase.
			ArrayList<Mob> closeMobs = mh.proxMobs(loc, splash,1);
			if (closeMobs != null && closeMobs.size() != 0) {
				int k = 0;
				for (Mob m: closeMobs) {
					if (k == 0) {					
						SoundEffect.BOOM1.play();
						//DrawEvent explode = new ExplodeEvent(0, m.getLoc().x, m.getLoc().y);
						//TDPanel.level.addDraw(explode);
						AffineTransform at = new AffineTransform();
						at.translate(loc.x, loc.y);
						at.rotate(dir);
						GifHandler.Boom.draw(at, 3);
						m.defend(this);
						k++;
					}
				}
				isKillable = true; // remove this to make god bullets
			}
		}
	}
	
	@Override
	public boolean isRemoveable() {
		return isKillable;
	}
	
	@Override
	public void draw(Graphics2D g2d) {
		AffineTransform at = new AffineTransform();
		at.setToRotation(dir+Math.PI/2d,loc.x,loc.y); // rotates tower	
		at.translate(loc.x - bulletImgs[upg].getWidth()/2, loc.y - bulletImgs[upg].getHeight()/2);
		g2d.drawImage(bulletImgs[upg], at, null);
	}

	@Override
	public Tower getShooter() {
		return shooter;
	}
}

