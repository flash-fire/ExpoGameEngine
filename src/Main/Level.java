package Main;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.PriorityQueue;

import Interfaces.DrawEvent;
import Interfaces.Event;
import Interfaces.Mob;
import Mobs.GaseousMob;
import Mobs.GiantMob;
import Mobs.RainbowMob;
 

// Has a construct for a level
public class Level {
	private PriorityQueue<Event> eventList; //  contains mob events
	private PriorityQueue<DrawEvent> drawList; //  contains mob draw events
	protected static int mobsLeft = 0; // mobs left in this level
	
	public Level() {
		eventList = new PriorityQueue<Event>(15);
		drawList = new PriorityQueue<DrawEvent>(15); // for special drawing things.  Drawing outside of the object itself.  Fun stuff.
	}
	
	public int mobsLeft() {
		return mobsLeft;
	}
	
	public void clear() {
		eventList.clear();
		drawList.clear();
		mobsLeft = 0;
	}
	
	public synchronized void offsetEvents(long offset) {
		Iterator<Event> iter = eventList.iterator();
		while (iter.hasNext()) {
			iter.next().offset(offset);
		}
		
		Iterator<DrawEvent> iter2 = drawList.iterator();
		while (iter2.hasNext()) {
			iter2.next().offset(offset);
		}
	}
	
	public boolean isEndOfLevel() {
		return eventList.isEmpty();
	}
	
	public synchronized void add(int startTime, int mobType, int numMob, int interval) {
		mobsLeft += numMob*(mobType+1);
		eventList.add(new MobLevelEvent(startTime, mobType, numMob, interval));
	}


	public synchronized void add(Event e) {
		eventList.add(e);
	}
	/*
	 * Adds MobLevelEvent directly from lvl loader using imput array.
	 */
	public synchronized void addMLE(int[] imp) {
		if (imp.length != 4) {
			System.out.println("Failure to adhere to level format.");
			return;
		}
		add(imp[0], imp[1], imp[2], imp[3]);
	}
	public synchronized void addDraw(DrawEvent draw) {
		drawList.add(draw);
	}
	
	/*
	 * The level ends if the eventList is empty.
	 */
	public synchronized void update() {
		if (!eventList.isEmpty()) {
			Event next = eventList.peek(); // gets next event.
			while (next != null && next.isReady()) {
				next.run(); // this might cause errors. 
				if (!next.isRemovable())
					eventList.add(next);
				next = eventList.poll();
			}
		}
	}
	
	public void draw(Graphics2D g2d) {
	/*	int i= 0;
		for (Event e: eventList) {
			g2d.drawString(e.toString(), i,100+50*i);
			i++;
		}*/ // debug printer
		
		if (!drawList.isEmpty()) {
			DrawEvent next = drawList.peek(); // gets next event.
			while (next != null && next.isReady()) {
				next.draw(g2d); // this might cause errors. 
				if (!next.isRemovable())
					drawList.add(next);
				next = drawList.poll();
			}
		}
	}
	
	/*
	 * Returns true if the level is over.
	 */
	public boolean isDone() {
		return eventList.isEmpty();
	}
	
}



class MobLevelEvent extends Event {
	protected int mobType; 
	protected int numMob;
	protected int interval;
	
	/*
	 * Constructs a mob spawning event.  The start time of spawning is relative to the current frame rate.
	 */
	public MobLevelEvent(int startTime, int mobType, int numMob, int spawnInterval) {
		super((int) (startTime + spawnInterval));
		this.mobType = mobType;
		this.numMob = numMob;
		interval = spawnInterval;
	}
	@Override
	public void offset(long offset) {
		start += offset;
	}
	
	/*
	 * Returns the next mob to be used by event.
	 */
	private Mob nextMob() {
		if (mobType <= 11)
			return new RainbowMob(nextType(mobType));
		else if (mobType == 12)
			return new GaseousMob();
		else
			return new GiantMob();
	}
	
	/*
	 * Returns true if the event is done and should be removed.
	 */
	@Override
	public boolean isRemovable() {
		return numMob <= 0;
	}

	private Point2D.Double randLoc() {
		Point2D.Double ret = new Point2D.Double(Math.random()*GamePanel.PWIDTH, Math.random()*GamePanel.PHEIGHT);
		while (ret.distance(TDPanel.target) < 200) {
			 ret = new Point2D.Double(Math.random()*GamePanel.PWIDTH, Math.random()*GamePanel.PHEIGHT);
		}
		return ret;
	}
	
	public int nextType(int cap) {
		double rand = Math.random()*cap+1;
		return (int) Math.min(cap, Math.round(rand));
	}
	@Override
	public void run() {
		assert isReady(): "Process event called when event wasn't ready";
		numMob--;
		start += interval;
		if (TDPanel.lvl > 11 && mobType <= 11 && Math.random() > 1- (TDPanel.lvl-8)/23d) {
			TDPanel.getMobHandler().add(new RainbowMob(randLoc(), TDPanel.path, 8, nextType(mobType), false));
		}
		else {
			TDPanel.getMobHandler().add(nextMob());	
		}
		Level.mobsLeft -= (1+mobType);
	}
	
	@Override
	public String toString() {
		return "MOB EVENT!\nTIME LEFT TIL NEXT RUN: " + (-TDPanel.frame()+start) + " MOB: " + mobType + " NUM LEFT: " + numMob +  " INTERVAL: " + interval;
	}
}


