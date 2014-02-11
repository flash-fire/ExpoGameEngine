package Bullets;
import Interfaces.*;
import Main.*;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D.Double;
import java.awt.image.BufferedImage;
import java.util.ArrayList;


public class SplashBullet extends BasicBullet {
	public int pierce = 5;
	public static BufferedImage ballBullet;
	public double dir; // orientation of ball
	public SplashBullet(Double start, Double target, int splash, int dmg, double vel, Tower shooter) {
		super(start, target, splash, dmg, vel, shooter);
		if (ballBullet == null)
			 ballBullet = ImageLoader.loadImage("resources/images/Bullets/FlyingBullet1.png");
	}

	@Override
	public void update() {
		dir += .2;
		dir = dir%(Math.PI*2);
		if (! isKillable) {	
			// Handle moving phase
			move();
			ArrayList<Mob> close = mh.proxMobs(loc, splash, pierce);
			if (close != null) {
				for (Mob m: close) {
					m.defend(this);	
				}
			}
			SoundEffect.RAYGUN.play();
			if (pierce <= 0)
				isKillable = true; // remove this to make god bullets

		}
	}
	
	
	@Override
	public void draw(Graphics2D g2d) {
        AffineTransform at = new AffineTransform();
        at.translate(loc.x, loc.y);
        at.scale((double)splash/50, (double)splash/50);
        at.rotate(dir, 25, 25);      
        g2d.drawImage(ballBullet, at, null);
	}

}

