package Buttons;

import java.awt.Graphics2D;

public interface AbsButton {
	/*
	 * Draws Button.
	 */
	public void draw(Graphics2D g2d);
	
	/*
	 * Runs button's task.
	 */
	public void run();
	
	/*
	 * Returns true if coord is inside of button space.
	 */
	public boolean contains(int x, int y);
}
