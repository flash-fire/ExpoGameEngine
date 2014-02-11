package Bullets;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.awt.image.ShortLookupTable;
import java.util.ArrayList;

import Interfaces.Mob;
import Interfaces.Tower;
import Main.GamePanel;
import Main.ImageLoader;
import Main.SoundEffect;
import Main.TDPanel;


public class FlyerBullet extends BasicBullet{
	public int deathTime;
	public static int reloadTime = 0;
	private long addTime;
	private boolean addRand;
	private int pierce;
	private int maxHit;
	private int stage;
	private static BufferedImage bullet;
	private static BufferedImage[] currbullet;
	public static double[][] colorRats = {{1 , 0, 0}, {.63, 0, 0}, 
			{.635, .32, .01}, {.63, .63, 0}, {.18, .282, 0}, 
			{.027, .517, .275}, {0, .51, .51}, {0, .337, .51},
			{0, 0, .337}, {.416, 0, .416}, {.168, 0, .341},
			{.467, 0, .235}};
	
	public FlyerBullet(Point2D.Double start, Point2D.Double target, int splash, int dmg, double vel, long time, int bulletTime, boolean rand, Tower shooter) {
		super(start, target, splash, 10, vel, shooter);
		addTime = time/1000000;
		deathTime = bulletTime;
		addRand = rand;
		pierce = 25;
		maxHit = 5;
		stage = (int) (Math.random() * colorRats.length);
		initImage();
	}

	
	private void initImage() {
		if (bullet == null) {
			bullet = ImageLoader.loadScaledImage("resources/images/bullets/whitebullet.png", pierce/2, pierce/2, false);
			currbullet = new BufferedImage[12];
			for (int i = 0;i < currbullet.length; i ++) {
				currbullet[i] = new BufferedImage(bullet.getWidth(), bullet.getHeight(), BufferedImage.TYPE_INT_ARGB);
				setColor(currbullet[i], i);
			}
		}
	}
	
	/*
	 * Sets color on buffered image b to stage i.
	 */
	private void setColor(BufferedImage b, int i) {
		b = getColorOp(colorRats[i][0], colorRats[i][1], colorRats[i][2]).filter(bullet, b);
	}
	
	
	private LookupOp getColorOp(double r1, double g1, double b1) {
		short[] red = new short[256];
		short[] green = new short[256];
		short[] blue = new short[256];
		short[] alpha = new short[256];

		for (short i = 0; i < 256; i++) {
		  green[i] = (short) Math.min(255, g1*i+15);
		  blue[i] = (short) Math.min(255, b1*i+15);
		  red[i] = (short) Math.min(255, r1*i+15);
		  alpha[i] = i;
		}
		short[][] data = new short[][] {
		    red, green, blue, alpha
		};
		LookupTable lookupTable = new ShortLookupTable(0, data);
		return new LookupOp(lookupTable, null);
	}
	
	
	@Override
	public void update() {
		if (addRand)
			dir +=  Math.random()/9;
		if (System.nanoTime()/1000000-addTime <= deathTime/TDPanel.speedFactor) { // is bullet removable
			if (pierce > 0) {		
				ArrayList<Mob> closeMobs = TDPanel.getMobHandler().proxMobs(loc, splash, maxHit);
				if (closeMobs != null && closeMobs.size() != 0) {
					for (int i = 0; i< closeMobs.size() && i < maxHit; i++)
						closeMobs.get(i).defend(this);
					dir -= Math.PI/3.7;
                    pierce--;
                    SoundEffect.BOUNCE.play();
				}
			}
			move();
		}
		else {
			isKillable = true;
		}
	}
	
	@Override
	protected void move() {
		// Coordinates of new Location
		double newX = loc.x + Math.cos(dir)*vel;
		double newY = loc.y + Math.sin(dir)*vel;
		
		// Handle edges
		if (newX <= 0 || newX >= GamePanel.PWIDTH*4/5) {
			dir += Math.PI/2;
			newX = loc.x + Math.cos(dir)*vel;
			newY = loc.y + Math.sin(dir)*vel;
		}		
		if (newY <= 0 || newY >= GamePanel.PHEIGHT) {
			dir += Math.PI/2;
			newX = loc.x + Math.cos(dir)*vel;
			newY = loc.y + Math.sin(dir)*vel;
		}
		loc.setLocation(new Point2D.Double(newX, newY));
	}

	@Override 
	public void draw(Graphics2D g2d) {
		g2d.setColor(new Color(25, 255, 50));
		int x1 = r(loc.x);
		int y1 = r(loc.y);
		// draws small box where bullet is at.
		if (currbullet == null) {
			initImage();
		}
		g2d.drawImage(redBullet, x1, y1, pierce/2, pierce/2, null);
//		g2d.drawImage(currbullet[stage], x1, y1, pierce/2, pierce/2, null);
//		g2d.drawOval(x1, y1, pierce/2, pierce/2); 
		/*AffineTransform at = new AffineTransform();
		at.translate(loc.x, loc.y);
		g2d.drawImage(bullet, at, null);*/
	}
}
