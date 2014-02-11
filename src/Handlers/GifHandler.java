package Handlers;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;

import Interfaces.DrawEvent;
import Main.ImageLoader;
import Main.TDPanel;

/*
 * Loads and plays sequences of images.  Like a boss.
 * Can be configured to optimize memory.
 */
public enum GifHandler {
	Boom("resources/images/Bullets/Explode/", ".gif", 63, 63),
	WrapperTower("resources/images/Towers/CandyWrappers/", ".gif", 45, 45),
	SplashTower("resources/images/Towers/Splash/", ".gif", 63, 63)
	,Lightning("resources/images/Bullets/Lightning/", ".gif", 60, 60)
	,bossMob("resources/images/Mobs/bossMob/", ".gif", 45, 45),
	Mouse("resources/images/Other/flyCursor/", ".gif", 45, 45);
	public BufferedImage[] sequence;
	int playing = 0;

	GifHandler(String dir_name, String type, int width, int height) {
		int numImgs = new File(dir_name).list().length;
		if (sequence == null) {
			sequence = new BufferedImage[numImgs];
			for (int i = 0; i < numImgs; i++) {
				sequence[i] = ImageLoader.loadScaledImage(dir_name + i + type, width, height, false);
			}
		}
	}

	/*
	 * Draws the gif sequence at the specified location with the specified gap.
	 */
	public void draw(int x, int y, int gap) {
		TDPanel.level.addDraw(new GifEvent(1, x, y, gap, this));
		playing++;
	}

	public void draw(Point2D p, int gap) {
		TDPanel.level.addDraw(new GifEvent(1, (int) Math.round(p.getX()), (int) Math.round(p.getY()), gap, this));
		playing++;
	}

	public void draw(AffineTransform at, int gap) {
		TDPanel.level.addDraw(new GifEvent(1, at, gap, this));
		playing++;
	}

	public boolean isPlaying() {
		return playing > 0;
	}

	private class GifEvent extends DrawEvent {
		int x, y, frame, timeLeft, gap;
		GifHandler type;
		AffineTransform at;
		// gap is time that gif is shown.
		GifEvent(int start, int x, int y, int gap, GifHandler type) {
			super(start);
			this.x = x;
			this.y = y;
			this.gap = this.timeLeft = gap;
			frame = 0;
			this.type = type;
		}

		GifEvent(int start, AffineTransform at, int gap, GifHandler type) {
			super(start);
			this.at = at;
			this.gap = this.timeLeft = gap;
			frame = 0;
			this.type = type;
		}

		private GifEvent(int start, int x, int y, int frame, int timeLeft, int gap, GifHandler type) {
			super(start);
			this.x = x;
			this.y = y;
			this.gap = gap;
			this.timeLeft = timeLeft;
			this.frame = frame;
			this.type = type;
		}

		private GifEvent(int start, AffineTransform at, int frame, int timeLeft, int gap, GifHandler type) {
			super(start);
			this.at = at;
			this.gap = gap;
			this.timeLeft = timeLeft;
			this.frame = frame;
			this.type = type;
		}

		@Override
		public void draw(Graphics2D g2d) {
			if (at == null) {
				AffineTransform trans = new AffineTransform();
				trans.translate(x - type.sequence[frame].getWidth()/2, y - type.sequence[frame].getHeight()/2);
				g2d.drawImage(type.sequence[frame], trans, null);
				if (timeLeft > 0)
					TDPanel.level.addDraw(new GifEvent(1, x, y, frame, timeLeft-1, gap, type));
				else if (frame < type.sequence.length-1)
					TDPanel.level.addDraw(new GifEvent(1, x, y, frame+1, gap, gap, type));
			}
			else {
				g2d.drawImage(type.sequence[frame], at, null);
				if (timeLeft > 0)
					TDPanel.level.addDraw(new GifEvent(1, at, frame, timeLeft-1, gap, type));
				else if (frame < type.sequence.length-1)
					TDPanel.level.addDraw(new GifEvent(1, at, frame+1, gap, gap, type));
			}
		}

	}

	public boolean isDrawing() {

		return playing > 0;
	}
}
