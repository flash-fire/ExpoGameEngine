package Buttons;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import Handlers.GifHandler;
import Interfaces.Updateable;

public class GifButton implements AbsButton {
	private GifHandler gh;
	private Updateable run;
	private AffineTransform at;
	private int frame = 0;
	private int frameRate;
	private int delay = 0; // how much longer til next frame change.
	
	
	/*
	 * run: What button does when clicked.
	 * GifHandler: what image to use for drawing.
	 * x,y or AffineTransform: location of button
	 * width, height: size of button.
	 * frameRate: how fast the button's frame is updated.
	 */
	public GifButton(Updateable run, GifHandler gh, int x, int y, int width, int height, int frameRate) {
		this.gh = gh;
		this.frameRate = frameRate;
		at = new AffineTransform();
		// Assumes all images are same size.
		// I freaking hate integer division.
		at.translate(x, y);
		at.scale(((double) width) / ((double)gh.sequence[0].getWidth())
				, ((double) height) / ((double) gh.sequence[0].getHeight())); 
		this.run = run;
	}
	
	public GifButton(Updateable run, GifHandler gh, AffineTransform at, int width, int height, int frameRate) {
		this.frameRate = frameRate;
		at.scale(((double) width) / ((double)gh.sequence[0].getWidth())
				, ((double) height) / ((double) gh.sequence[0].getHeight()));
		this.at = at;
		this.run = run;
	}
	
	public void setRun(Updateable run) {
		this.run = run;
	}
	
	public void draw(Graphics2D g2d) {
		if (delay >= frameRate) {
			frame++;
			frame %=gh.sequence.length;
			delay = 0;
		}
		g2d.drawImage(gh.sequence[frame], at, null);
		delay++;
	}
	
	public void run() {
		run.run();
	}

	/*
	 * Again, a very basic but fast contains method.
	 */
	@Override
	public boolean contains(int x, int y) {
		if (at.getTranslateX() < x && x < at.getTranslateX() + gh.sequence[frame].getWidth() * at.getScaleX())
			if (at.getTranslateY() < y && y < at.getTranslateY() + gh.sequence[frame].getHeight() * at.getScaleY())
				return true;
		return false;
	}
}
