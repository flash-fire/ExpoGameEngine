package Buttons;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import Interfaces.Updateable;

public class ImgButton implements AbsButton {
	protected BufferedImage img;
	protected AffineTransform at;
	protected Updateable run;
	
	public ImgButton(Updateable run, String file, int x, int y, int width, int height) {
		img = Main.ImageLoader.loadScaledImage(file, width, height, false);
		at = new AffineTransform();
		at.translate(x, y);
		this.run = run;
	}

	public ImgButton(Updateable run, BufferedImage img, int x, int y, int width, int height) {
		this.img = img;
		at = new AffineTransform();
		at.translate(x, y);
		this.run = run;
	}
	
	public ImgButton(Updateable run, String file, AffineTransform at, int width, int height) {
		img = Main.ImageLoader.loadScaledImage(file, width, height, false);
		this.at = at;
		this.run = run;
	}


	@Override
	public void draw(Graphics2D g2d) {
		g2d.drawImage(img, at, null);
	}

	/*
	 * (non-Javadoc)
	 * @see Buttons.AbsButton#contains(int, int)
	 * Very basic contains method.  Yay.
	 */
	public boolean contains(int x, int y) {
		if (at.getTranslateX() < x && x < at.getTranslateX() + img.getWidth())
			if (at.getTranslateY() < y && y < at.getTranslateY() + img.getHeight())
				return true;
		return false;
	}
	/*
	 * (non-Javadoc)
	 * @see Buttons.AbsButton#run()
	 */
	public void run() {
		run.run();
	}
}
