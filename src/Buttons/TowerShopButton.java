package Buttons;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import Interfaces.Tower;
import Interfaces.Updateable;
import Main.ImageLoader;
import Main.TDPanel;

public class TowerShopButton implements AbsButton {
	protected Tower tow;
	public int x, y, wd, ht;
	protected Updateable run;
	public BufferedImage img;

	public TowerShopButton(Updateable run, Tower tow, BufferedImage img, int x,
			int y, int width, int height) {
		init(run, tow, x, y, width, height);
		this.img = img;
	}

	public TowerShopButton(Updateable run, String file, Tower tow, int x,
			int y, int width, int height) {
		img = ImageLoader.loadImage(file);
		init(run, tow, x, y, width, height);
	}

	private void init(Updateable run, Tower tow, int x, int y, int width,
			int height) {
		this.x = x;
		this.y = y;
		wd = width;
		ht = height;
		this.run = run;
		this.tow = tow;
	}

	public void draw(Graphics2D g2d) {
		Font prev = TDPanel.font;
		g2d.setFont(new Font("SansSerif", Font.BOLD, 11));
		g2d.setColor(Color.yellow);
		if (tow.getCost() * TDPanel.costMult > TDPanel.gold) {
			g2d.setComposite(AlphaComposite.getInstance(
					AlphaComposite.SRC_OVER, .3f));
			g2d.drawImage(img, x, y, null);
			g2d.setComposite(AlphaComposite.getInstance(
					AlphaComposite.SRC_OVER, 1));
		} else {
			g2d.drawImage(img, x, y, null);
		}

		g2d.drawString("Cost: " + tow.getCost() * TDPanel.costMult, x, y);
		g2d.drawString(tow.toString(), x, y - y/15);

		g2d.setColor(Color.white);
		g2d.drawString("Key: " + (x * 8 / TDPanel.shopWd + 1), x, y); // hot
																			// key
		g2d.setFont(prev);

	}

	public Tower getTower() {
		return tow;
	}

	@Override
	public void run() {
		run.run();
	}

	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see Buttons.AbsButton#contains(int, int) Very basic contains method.
	 * Yay.
	 */
	public boolean contains(int x, int y) {
		if (this.x < x && x < this.x + img.getWidth())
			if (this.y < y && y < this.y + img.getHeight())
				return true;
		return false;
	}

}
