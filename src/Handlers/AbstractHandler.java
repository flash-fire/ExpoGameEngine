package Handlers;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import Interfaces.GameObject;

/*
 * AbstractHandler is an abstraction of handlers for game objects.  It contains the bare min
 * of what a good handler needs to handle game objects.  Extensions of abstract handler are used
 * when more functionality is needed.
 * 
 * Otherwise, this abstraction turned BulletHandler from 55 to 16 lines of code and
 * MobHandle from 124 to 70 lines of code at the time of writing this message!!
 * 2-9-13
 */
public class AbstractHandler<E extends GameObject> {
	protected ArrayList<E> ls;
	protected ReentrantReadWriteLock rwl;

	/*
	 *  Constructor for a general handler.
	 */
	public AbstractHandler() {
		ls = new ArrayList<E>();
		rwl = new ReentrantReadWriteLock();
	}
	
	/*
	 * Adds a new element to the handler's list
	 */
	public void add(E e) {
		rwl.writeLock().lock();
		ls.add(e);
		rwl.writeLock().unlock();
	}
	
	/*
	 * Returns number of entities in MobHandler
	 */
	public int size() {
		return ls.size();
	}
	
	public void clear() {
		rwl.writeLock().lock();
		ls.clear();
		rwl.writeLock().unlock();
	}
	
	/*
	 * Updates the entities in the list.
	 */
	public void update() {
		rwl.readLock().lock();
		for (E e: ls) {
			e.update();
		}
		rwl.readLock().unlock();
	}

	/*
	 * Removes all mobs from handler that are marked as "removeable."
	 */
	public void flush() {
		if (ls.size() == 0)
			return;
		rwl.writeLock().lock();
		Iterator<E> entries = ls.iterator();
		E next;
		while (entries.hasNext()) {
		    next = entries.next();
			if (next.isRemoveable()) {
	            entries.remove();
			}
		}
		rwl.writeLock().unlock();
		
	}

	/*
	 * Draws the entities in the handler.
	 */
	public void draw(Graphics2D g2d) {	
		rwl.readLock().lock();
		for (E e: ls) {
			if (!e.isRemoveable()) {
				e.draw(g2d);
			}
		}
		rwl.readLock().unlock();
	}
	
	
}
