package Interfaces;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import Main.ImageLoader;
import Main.TDPanel;

public class HealthBar{ // displays health bar and sliding meter based on sugar levels

	BufferedImage healthBar = ImageLoader.loadImage("resources/images/Other/healthbar.gif");
	BufferedImage candyMeter = ImageLoader.loadImage("resources/images/Other/candymeter.png");
	int w = TDPanel.PWIDTH;
	
	public void draw(Graphics2D g2d) {
		g2d.setColor(Color.red);
		
		g2d.drawString("S", w/13*10, 45);
		g2d.setColor(Color.orange);
		g2d.drawString("U", w/13*10+20, 45);
		g2d.setColor(Color.yellow);
		g2d.drawString("G", w/13*10+40, 45);
		g2d.setColor(Color.green);
		g2d.drawString("A", w/13*10+60, 45);
		g2d.setColor(Color.blue);
		g2d.drawString("R", w/13*10+80, 45);
		g2d.setColor(Color.MAGENTA);
		g2d.drawString(" Level", w/13*10+100, 45);
			g2d.drawImage(healthBar, w/15*10, 50, null); 
			// health bar x = w/15*10  >  x = w/15*14
			
			g2d.drawImage(candyMeter, w/15*10 + (int)(TDPanel.health*.43), 5, null);
	} 
}
