package Interfaces;

import java.awt.Graphics2D;

public abstract class DrawEvent extends Event {
	public DrawEvent(int start) {
		super(start);
	}
	
	public abstract void draw(Graphics2D g2d);
}
