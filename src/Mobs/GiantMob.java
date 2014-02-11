
package Mobs;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.image.BufferedImage;

import Main.ImageLoader;
import Main.MonsterPath;
import Handlers.GifHandler;
import Interfaces.Bullet;
import Interfaces.Mob;
import Interfaces.Tower;
import Main.TDPanel;
import Towers.GasTower;
import Towers.SplashTower;


public class GiantMob implements Mob {

	public Point2D.Double loc;
	private Point2D.Double next;
	private double dir;
	public static BufferedImage body;
	public static final float maxHPMin = 100000;
	public float maxHP;
	public static final int scoreOnDeath = 5000;
	public static final int goldOnDeath = 100;
	private double vel;
	public static int counter = 0;
	private static MonsterPath path;
	
	private int countPath;
	private double distLeft;
	public static final int dmgOnEnd = 100;
	private float hp;
	private Tower killer;
	private boolean isKillable;
	private int count = 0;
	
	public GiantMob() {
		counter++;
		loc = (Point2D.Double) TDPanel.source.clone();
		//hp = maxHPMin * TDPanel.getTowerHandler().size();
		hp = 50000 + 250 * counter;
		maxHP = hp;
		killer = null;
		path = TDPanel.getPath();
		next = path.next(0);
		countPath = 0;
		distLeft = loc.distance(next);
		dir = path.getDir(loc, next);
		vel = .75;
		isKillable = false;
//		if (body == null)
//			body = GifHandler.bossMob.sequence[count];
		
	}
	@Override
	public boolean isRemoveable() {
		return isKillable;
	}

	@Override
	public void update() {
		if (isAlive()) {
			move();
		}	
	}
	
	private void move() {
		double speed = TDPanel.speedFactor;
		
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
			TDPanel.health += dmgOnEnd * TDPanel.buffer;
			isKillable = true; // A monster can only strike once.  May change this.
		}		
		loc.setLocation(newX, newY);
	}

	@Override
	public void draw(Graphics2D g2d) {
		AffineTransform at = new AffineTransform();
		at.translate(loc.x, loc.y);
//		int R = (int)(Math.random()*256);
//		int G = (int)(Math.random()*256);
//		int B= (int)(Math.random()*256);
//		Color color = new Color(R, G, B);
//		if (TDPanel.hyperMode)
//			g2d.setXORMode(color);
//		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .1f ));
//		if (TDPanel.frame() % 2 == 0) {
//			count++;
//			count %= GifHandler.bossMob.sequence.length;
//		}

		if(hp<= 0){
			count = 15;
		}
		else if(hp > 0 && hp <2000*2){
			count = 14;
		}
		else if(hp > 2000*2 && hp <3600*2){
			count = 13;
		}
		else if(hp > 3600*2 && hp <5200*2){
			count = 12;
		}
		else if(hp > 5200*2 && hp <6800*2){
			count = 11;
		}
		else if(hp > 6800*2 && hp <8500*2){
			count = 10;
		}
		else if(hp > 8500*2 && hp <10400*2){
			count = 9;
		}
		else if(hp > 10400*2 && hp <12000*2){
			count = 8;
		}
		else if(hp > 12000*2 && hp <13800*2){
			count = 7;
		}
		else if(hp > 13800*2 && hp <15400*2){
			count = 6;
		}
		else if(hp > 15400*2 && hp <17000*2){
			count = 5;
		}
		else if(hp > 17000*2 && hp <18600*2){
			count = 4;
		}
		else if(hp > 18600*2 && hp <20000*2){
			count = 3;
		}
		else if(hp > 20000*2 && hp <22000*2){
			count = 2;
		}
		else if(hp<= 22000*2){
			count = 1;
		}
		if(hp>46000){
			count = 0;
		}
		count %= GifHandler.bossMob.sequence.length;
		g2d.drawImage(GifHandler.bossMob.sequence[count], at, null);
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
		
		
	}

	@Override
	public Double getLoc() {
		return loc;
	}

	@Override
	public void defend(Bullet b) {
		defend(b.getDmg(), b.getShooter());
	}

	@Override
	public void defend(int dmg, Tower t) {
		if (!(t instanceof GasTower || t instanceof SplashTower)) {
			killer = t;
			hp -= dmg;
		}
	}

	@Override
	public void onDeath() {
		if (killer != null)
			killer.incKill();
		if (!isAlive()) {
			if (! TDPanel.hyperMode) {
				TDPanel.gold += goldOnDeath;
			}
		TDPanel.score += scoreOnDeath;
		isKillable = true;
		}
	}

	@Override
	public boolean isAlive() {
		return hp > 0;
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
		// immune to statuses. :D
	}

	@Override
	public boolean[] getStatus() {
		return null;
	}


}
