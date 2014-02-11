package Main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import Buttons.Button;
import Buttons.ImgButton;
import Interfaces.Tower;
import Interfaces.Updateable;

/*
 * Helps handles the buttons for upgrades
 */
public class UpgradeHelper {
	private ArrayList<RectButton> upgButtons;
	BufferedImage upgradeMenu = ImageLoader.loadImage("resources/images/Other/upgradeMenu.png");
	BufferedImage gold =  ImageLoader.loadImage("resources/images/Shop/gold.png");
	public UpgradeHelper() {
		upgButtons = new ArrayList<RectButton>();	
	}
	
	/*
	 * adds a button with message
	 */
//	public void add(Rectangle r, String msg) {
//		upgButtons.add(new RectButton(r,msg));
//	}
	/*
	 * Returns true if the set is successful
	 */
//	public boolean set(Rectangle r, String newMsg) {
//		int index = upgButtons.indexOf(r);
//		if (index != -1) {
//			upgButtons.get(index).set(newMsg);
//			return true;
//		}
//		return false;
//	}
	
	
	/*
	 * Returns true if the set is successful
	 */
//	public boolean set(int index, String newMsg) {
//		if (index != -1 && index < upgButtons.size()) {
//			upgButtons.get(index).set(newMsg);
//			return true;
//		}
//		return false;
//	}
//	
//	public boolean setUpgButton(String newMsg) {
//		if (3 < upgButtons.size()) {
//			upgButtons.get(3).set(newMsg);
//			return true;
//		}
//		return false;
//	}
//	
	
	/*
	 * adds a new rectangle button with message
	 */
	
//	public void add(int x, int y, int w, int h, String msg) {
//		upgButtons.add(new RectButton(new Rectangle(x,y,w,h), msg));
//	}
	/*
	 * Returns whether this point is contained by the upgrade box!
	 */
//	public boolean inUpgBox(Point targ) {
//		int yCons = GamePanel.PHEIGHT / 20 * 17;
//		return new Rectangle(TDPanel.shopEnd, yCons, GamePanel.PWIDTH / 12 - GamePanel.PWIDTH / 15, GamePanel.PHEIGHT / 11).contains(targ); 
//	}
	/*
	 * Returns what button (if any) contains targ
	 */
//	public int whatContains(Point targ) {
//		for (int i = 0; i < upgButtons.size();i++) {
//			if (upgButtons.get(i).contains(targ)){
//				return i;
//			}
//		}
//		return -1;	
//	}
	
	public void draw(Graphics2D g2d, Tower nextUpg) {
		// Draws bio for tower that is selected for upgrades
//		int xCons = GamePanel.PWIDTH / 40 * 26;
//		int yCons = GamePanel.PHEIGHT / 20 * 16;
//		g2d.setColor(Color.white);
		
		// Draws upgrade box
//		g2d.drawImage(upgradeMenu, null, xCons, yCons);
//		g2d.drawString("Upgrade Menu", xCons+125,yCons+30); // draws upg menu text
		Updateable upgrade = new Updateable() {
			public void run() {
				
			}
		};

		TDPanel.buttons.add(new Button(
				new String[] {"upgrade"},
				new ImgButton(upgrade, upgradeMenu,
						TDPanel.shopEnd, GamePanel.PHEIGHT / 20 * 17, GamePanel.PWIDTH / 12 - GamePanel.PWIDTH / 15, GamePanel.PHEIGHT / 11)));
//
//		if (nextUpg != null) {
//			Font temp = new Font("SansSerif", Font.BOLD, 14);
//			g2d.setFont(temp); // Shrinks font since upgButtons are small!
//			g2d.drawImage(nextUpg.getPic(), xCons+60, yCons+90, null); 
//			g2d.drawImage(gold, xCons+240, yCons+90, null);
//			
//			g2d.drawString("Kill Count " + nextUpg.killCount(), xCons+85 , yCons + 160);
//			g2d.setColor(Color.yellow);
//			// Draws the select circle on tower so you know which tower you're upgrading
//			int x = (int) Math.round(nextUpg.getLoc().x - 5); // minus 5 is offset for center of circle
//			int y =(int) Math.round(nextUpg.getLoc().y - 5);
//			int range = nextUpg.getRng();
//			// Draws the range circle to show tower range.
//			g2d.setColor(Color.cyan);
//			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.18f));
//			g2d.drawOval(x- range, y - range, 2*range, 2*range);
//			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
//		}		
//		// draws upgButtons
//		g2d.setColor(Color.black);
//		for (int i = 0; i < upgButtons.size();i++) {
//			if (nextUpg != null ) {
//				if (i == 0) {
//					upgButtons.get(i).set(nextUpg.upgName());
//					upgButtons.get(i).draw(g2d, nextUpg.getUpgCost());
//				}
//				else if (i == 1) {
//					upgButtons.get(i).draw(g2d, nextUpg.sellPrice());
//				}
//				else { // should never be found
//					upgButtons.get(i).draw(g2d);
//				}
//			}
//		}
//		
//
	}	
}

/*
 * Contains info about rectangular upgButtons [can be generalized by changing rectangle to polygon/shape/area/ext
 */
class RectButton {
	protected Rectangle rect;
	protected String msg;
	
	public RectButton(Rectangle r, String out) {
		msg = out;
		rect = r;
	}
	
	public boolean contains(Point p) {
		return rect.contains(p);
	}
	/*
	 * default draw
	 */
	public void draw(Graphics2D g2d) {
		g2d.draw(rect);
		g2d.drawString(msg, rect.x+rect.width/8, rect.y+rect.height/2);
	}
	
	/*
	 * draws a number under default text.
	 */
	public void draw(Graphics2D g2d, double cost) {
		g2d.setColor(Color.red);
		g2d.drawString(msg, rect.x+rect.width/8, rect.y); // upg message
		g2d.setColor(Color.blue);
		g2d.drawString("" + cost * TDPanel.costMult, rect.x+rect.width/8, rect.y+25); // cost upg	
	}
	public void set(String msg) { 
		this.msg = msg; 
	}
}