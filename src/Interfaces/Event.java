package Interfaces;
import Main.TDPanel;

public class Event implements Comparable<Event> {
	protected long start;
	public Event(int startTime) {
		start = startTime +TDPanel.frame();
	}
	
	/*
	 * Returns true if event can be run.
	 */
	public boolean isReady() {
		return TDPanel.frame() >= start;
	}
	
	public void run() {
		
	}
	
	@Override
	public int compareTo(Event e) {
		return (int) (start -  e.start);
	}
	
	public boolean isRemovable() {
		return TDPanel.frame() >= start;
	}

	public void offset(long offset) {
		start += offset;
	}
}