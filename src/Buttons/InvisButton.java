package Buttons;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;

import Interfaces.Updateable;

public class InvisButton implements AbsButton {
	public Updateable run;
	public Shape container;
	public InvisButton(Updateable run, Shape contain) {
		this.run = run;
		container = contain;
	}
	
	public void draw(Graphics2D g2d) {
		// you called it.
		//g2d.draw(container);
	}
	
	public void run() {
		run.run();
	}

	@Override
	public boolean contains(int x, int y) {
		// TODO Auto-generated method stub
		return container.contains(new Point(x,y));
	}
}
