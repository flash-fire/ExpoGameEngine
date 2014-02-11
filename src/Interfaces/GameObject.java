package Interfaces;

import java.awt.Graphics2D;

/*
 * GameObject
 * 
 * This class describes the essential properties of any object in the Tower Defense Game.
 * They must have three properties:
 * 	1) The object must be able to be updated.
 *  2) The object must have some draw method.  [even if it is to have no implementation]
 *  3) The object must be able to inform others of when its time for the object time.
 *  
 */
public interface GameObject {
	
	/*
	 * This method describes the behavior of an object during an update.
	 */
	public void update();
	
	/*
	 * This method renders the object into the graphics.
	 */
	public void draw(Graphics2D g2d);
	
	/*
	 * Returns if the object is ready to be removed from the game.
	 */
	public boolean isRemoveable();
	
}

