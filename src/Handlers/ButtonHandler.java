package Handlers;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

import Buttons.Button;;

/*
 * What a boring class.  Lol
 * This does button stuff.  Have fun.
 * Amusedly, calling update on abstract handle would click all buttons simultaneously.  That'd be lolzy.
 */
public class ButtonHandler extends AbstractHandler<Button> {

	public ButtonHandler() {
		super();
	}

	public void click(String target) {
		for (Button bt: ls) {
			if (!bt.hiding && bt.hasTag(target))
				bt.update();
		}
	}
	// Clicks button that contains point x y
	public void click(String target, int x, int y) {
		for (Button bt: ls) {
			if (bt.contains(x, y))
				if (!bt.hiding && bt.hasTag(target))
					bt.update();
		}
	}
	
	public void clickAvoid(String avoid, int x, int y) {
		for (Button bt: ls) {
			if (bt.contains(x, y))
				if (!bt.hiding && !bt.hasTag(avoid))
					bt.update();
		}
	}
	// Clicks visible buttons that contains point x y.
	public void click(int x, int y) {
		for (Button bt: ls) {
			if (bt.contains(x, y))
				if (!bt.hiding)
					bt.update();
		}
	}
	
	/*
	 * Returns first button with this tag.
	 */
	public Button get(String tag) {
		for (Button bt: ls) {
			if (bt.hasTag(tag))
				if (!bt.hiding)
					return bt;
		}
		return null;
	}
	

	/*
	 * Returns first button with this tag.
	 */
	public ArrayList<Button> getAll(String tag) {
		ArrayList<Button> ret = new ArrayList<Button>();
		for (Button bt: ls) {
			if (bt.hasTag(tag))
				if (!bt.hiding)
					ret.add(bt);
		}
		return ret;
	}
	
	/*
	 * Returns all buttons with this tag at this location
	 */
	public ArrayList<Button> getAll(String tag, int x, int y) {
		ArrayList<Button> ret = new ArrayList<Button>();
		for (Button bt: ls) {
			if (!bt.hiding)
				if (bt.hasTag(tag))
					if (bt.contains(x, y))
						ret.add(bt);
		}
		return ret;
	}
	
	/*
	 * Returns first button at location x,y with tag tag.
	 */
	public Button get(String tag, int x, int y) {
		for (Button bt: ls) {
			if (!bt.hiding)
				if (bt.hasTag(tag))
					if (bt.contains(x, y))
						return bt;
		}
		return null;
	}
	
	public void click(Point2D p) {
		click((int) p.getX(), (int) p.getY());
	}
	
	public void click(String target, Point2D p) {
		click(target, (int) p.getX(), (int) p.getY());
	}
	
	/*
	 * I have no idea why you would use this method.  But here it is.  
	 */
	public void hideAll() {
		for (Button bt: ls) {
				bt.hide();
		}
	}
	
	/*
	 * A button that does not exist is hiding.  Checks to see if first button of that tag is hiding.
	 */
	public boolean isHiding(String targ) {
		for (Button bt: ls) {
			if (bt.hasTag(targ))
				return bt.hiding;
		}
		return true;
	}
	
	// Clicks button that contains point x y
	public void toggle(String target) {
		for (Button bt: ls) {
			if (bt.hasTag(target)) {
				if (bt.hiding)
					bt.show();
				else {
					bt.hide();
				}
			}
		}
	}
	
	
	
	/*
	 * I guess this is more useful than hide all.
	 */
	public void showAll() {
		for (Button bt: ls) {
			bt.show();
		}
	}
	
	/*
	 * Returns true if a button[s] were hidden.
	 * The tag has some fun uses.  You can set a bunch of buttons to the same name to hide them all at once.
	 * This is nice to hide all update buttons or all shop buttons at same time.
	 * I got the idea from tumblr's tag blocking lol.
	 */
	public boolean hide(String target) {
		boolean ret = false;
		for (Button bt: ls) {
			if (bt.hasTag(target)) {
				bt.hide();
				ret = true;
			}
		}
		return ret;
	}

	/*
	 * Returns true if a button[s] were shown.
	 * The name has some funny uses.  You can set a bunch of buttons to the same name to show them all at once.
	 */
	public boolean show(String target) {
		boolean ret = false;
		for (Button bt: ls) {
			if (bt.hasTag(target)) {
				bt.show();
				ret = true;
			}
		}
		return ret;
	}
	
	/*
	 * Purges all buttons from handler that have string.  This can be used to free up memory since theoretically,
	 * the only link to the button SHOULD BE in the buttonhandler.
	 */
	public ArrayList<Button> purge(String target) {
		if (ls.size() == 0)
			return null;
		rwl.writeLock().lock();
		ArrayList<Button> removed = new ArrayList<Button>();
		Iterator<Button> entries = ls.iterator();
		Button next;
		while (entries.hasNext()) {
		    next = entries.next();
			if (next.hasTag(target)) {
				removed.add(next);
	            entries.remove();
			}
		}
		rwl.writeLock().unlock();	
		return removed;
	}
	

}
