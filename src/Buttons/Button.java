package Buttons;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;

import Interfaces.GameObject;

public class Button implements GameObject {
	public AbsButton button;
	public boolean hiding = false;
	public boolean isRemoveable = false;
	
	public ArrayList<String> tags; // Tags contain a list of tags that this button belongs to.  The tag should include the buttons name.
	
	/*
	 * Time to confuse the heck out of everyone.  The button is a wrapper class that's all. It contains the hide/show method.
	 * This allows a button to be created with the easier almost done classes [ie. GifButton] then it adds a couple more methods.
	 * It works. And, it makes me feel happy about my ability to inject things.
	 */
	public Button(AbsButton obj) {
		button = obj;
		tags = new ArrayList<String>();
	}
	
	/*
	 * Just the name.
	 */
	public Button(String name, AbsButton obj) {
		button = obj;
		tags = new ArrayList<String>();
		tags.add(name);
	}
	
	/*
	 * all the tags.
	 */
	public Button(String[] tagList, AbsButton obj) {
		button = obj;
		hiding = false;
		tags = new ArrayList<String>();
		tags.addAll(Arrays.asList(tagList));
	}
	
	public void appendTag(String tag) {
		tags.add(tag);
	}
	
	public void unionTag(String tag) {
		if (!hasTag(tag))
			tags.add(tag);
	}
	
	public void removeTag(String tag) {
		tags.remove(tag);
	}
	
	public boolean hasTag(String tag) {
		return tags.contains(tag);
	}
	
	public void draw(Graphics2D g2d) {
		if (!hiding)
			button.draw(g2d);
	}
	
	public void update() {
		button.run();
	}
	
	public void show() {
		hiding = false;
	}
	
	public void hide() {
		hiding = true;
	}
	
	public boolean contains(int x, int y) {
		return button.contains(x, y);
	}

	@Override
	public boolean isRemoveable() {
		return isRemoveable;
	}
	
	public void remove() {
		isRemoveable = true;		
	}
}
