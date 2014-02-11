package Bullets;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D.Double;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import Interfaces.Mob;
import Interfaces.Tower;
import Main.ImageLoader;
import Main.TDPanel;


public class GasBullet extends BasicBullet{
	private long deathTime;
	private long addTime;
	private static BufferedImage gasBullet;

	
	public GasBullet(Double start, Double target, int deathTime, int splash, int damg, Tower shooter) {
		super(start, target, splash, damg, 2.5d, shooter);
		addTime = TDPanel.frame();
		this.deathTime = deathTime + addTime;
		addTime = System.nanoTime()/1000000;
		super.splash = splash;
		super.dmg = damg;
		addTime = TDPanel.frame();
		if (gasBullet == null)
			gasBullet = ImageLoader.loadImage("resources/images/Bullets/GasBullet1.png");
	}
	
	
	public GasBullet(Double start, double dir, int deathTime, int splash, int damg, Tower shooter) {
		super(start, dir, 2.5, damg, splash, shooter);
		addTime = TDPanel.frame();
		this.deathTime = deathTime + addTime;
		if (gasBullet == null)
			gasBullet = ImageLoader.loadImage("resources/images/Bullets/GasBullet1.png");
	}
	
	@Override
	public void update() {
		if (deathTime > TDPanel.frame()) {
			if (! isKillable) {		
				// Handle moving phase
				move();
				
				// Handle attack phase.
				ArrayList<Mob> closeMobs = TDPanel.getMobHandler().proxMobs(loc, splash,24);
				if (closeMobs != null && closeMobs.size() != 0) {
					for (Mob m: closeMobs) {
						m.defend(this);
						m.setStatus(0,100, getShooter());
					}
					
				}
			}	
		}
		else
			super.isKillable = true;
	}
	
	@Override 
	public void draw(Graphics2D g2d) {
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
        AffineTransform at = new AffineTransform();
        at.translate(loc.x - gasBullet.getWidth()/2, loc.y - gasBullet.getHeight()/2);
        at.rotate(dir, gasBullet.getWidth()/2, gasBullet.getHeight()/2);      
        g2d.drawImage(gasBullet, at, null);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
	}
}