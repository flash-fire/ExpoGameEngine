package Bullets;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import Interfaces.DrawEvent;
import Main.ImageLoader;
import Main.TDPanel;


public class ExplodeEvent extends DrawEvent {
	double x;
	double y;
	static BufferedImage[] explosion;
	int frame;
	int timeLeft;
	public static final int maxTime = 3;
	public ExplodeEvent(int start, double x, double y) {
		super(start);
		this.x = x;
		this.y = y;
		timeLeft = maxTime;
		if (explosion == null) {
			explosion = new BufferedImage[11];
			for (int i = 0; i < 11; i++) {
				explosion[i] = ImageLoader.loadScaledImage("resources/images/Bullets/Explode/" + i + ".gif", 63, 63, false);
				//explosion[i-1] = TDPanel.effects.getMixedColouredImage(explosion[i-1]);
			}
			frame = 0;
		}
	}

	private ExplodeEvent(int start, double x, double y, int frame, int timeLeft) {
		super(start);
		this.x = x;
		this.y = y;
		this.frame = frame;
		this.timeLeft = timeLeft;
	}

	@Override
	public void draw(Graphics2D g2d) {
		AffineTransform at = new AffineTransform();
		System.out.println("STUCK");
		at.translate(x - explosion[frame].getWidth()/2, y - explosion[frame].getHeight()/2);
		g2d.drawImage(explosion[frame], at, null);
		if (timeLeft > 0)
			TDPanel.level.addDraw(new ExplodeEvent(1, x, y, frame, timeLeft-1));
		else if (frame < explosion.length-1)
			TDPanel.level.addDraw(new ExplodeEvent(1, x, y, frame+1, maxTime));
	}


}


