package Towers;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import Interfaces.Mob;
import Main.ImageLoader;
import Main.TDPanel;


public class SwordTower extends AbstractTower {
	public static int cost = 250;
	public static int[] costs = new int[] {500, 1000, 2000};
	public static int[] widths = new int[] {12, 16, 25};
	public static int[] heights = new int[] {48, 64, 100};
	public static int[] dmg = new int[] {25, 50, 100};
	public boolean pos = true;

	public static final String[] names = {"Sword Staff", "Tri Sword", "Penta Sword"};
	public static final String[] bios = {": A sword staff that can cut through sugar."
		, ": More blades. More power"
		, ": Your number one fan."};
	private static BufferedImage[] sword;
	private static double[] SPINRATE = new double[] {.05, .06, .1};
	public int id;
	public static int sordCount = 0;
	private double dir;
	private int kill;

	public SwordTower() {
		super();
		initPic();
		dir = Math.random()*Math.PI*2;
	}

	public int getCost() {
		return costs[upg];
	}

	public SwordTower(int i, int j, int id) {
		super(new Point2D.Double(i,j), 0, cost, 0, 0, 1000, 0, 0);
		this.id = id;
		this.loc = new Point2D.Double(i,j);
		sordCount++;
		initPic();
		dir = Math.random()*Math.PI*2;
	}

	private void initPic() {
		if (sword == null) {
			sword = new BufferedImage[3];
			sword[0] =  ImageLoader.loadScaledImage("resources/images/Towers/sword.png", heights[0]*2, widths[0]*2, false);
			sword[1] =  ImageLoader.loadScaledImage("resources/images/Towers/sword.png", heights[1]*2, widths[1]*2, false);
			sword[2] =  ImageLoader.loadScaledImage("resources/images/Towers/sword.png", heights[2]*2, widths[2]*2, false);
		}
	}

	@Override
	public void defaultStats() {
		easyDef(new Point2D.Double(1 , 1), 0, -10
				, 0, 0, 1000, 1, TDPanel.incTowID());
		initPic();
		sordCount++;
		dir = Math.random()*Math.PI*2;
	}

	@Override
	public String toString() {
		return "Sword";
	}
	@Override
	public void update() {
		// This tower does nothing
		if (pos) {
			dir += SPINRATE[upg];
		}
		else {
			dir -= SPINRATE[upg];
		}
		
		if (Math.random() > .994) {
			pos = !pos;
		}
		if (dir >= Math.PI*2)
			dir -= Math.PI*2;
		ArrayList<Mob> near;
		for (int i = 0; i < 4;i++) {
			near = TDPanel.getMobHandler().swordProx(loc, heights[upg], widths[upg], dir + Math.PI*i/2);
			for (Mob m: near) {
				m.defend(dmg[upg], this);
			}
		}

	}

	private int r(double x) {
		return(int)Math.round(x);
	}
	@Override
	public void draw(Graphics2D g2d) {


		AffineTransform at;
		for (int i = 1;i < 5; i++) {
			at = new AffineTransform();
			//TDPanel.getMobHandler().swordProx(g2d, loc, heights[upg], widths[upg], dir + Math.PI*i/2);
			at.setToRotation(dir+Math.PI*i/2,loc.x ,loc.y); // rotates tower	
			at.translate(loc.x - sword[upg].getWidth()/2, loc.y - sword[upg].getHeight()/2);
			g2d.drawImage(sword[upg], at, null);
		}
	}

	@Override
	public void sell() {
		TDPanel.gold += sellPrice()*TDPanel.costMult;
		isRemoveable = true;
		sordCount--;
	}


	@Override
	public BufferedImage getPic() {
		if (sword == null)
			initPic();
		return sword[0];
	}

	@Override
	public int getRng() {
		// TODO Auto-generated method stub
		return heights[upg];
	}

	@Override
	public String getBio() {
		return "Throws arround a candy cane shuriken.";
	}

	@Override
	public int getUpgCost() {
		return costs[Math.min(2, upg+1)];
	}

	@Override
	public String upgName() {
		return toString();
	}

	@Override
	public boolean canUpg() {
		return upg < 2;
	}

	@Override
	public void upgrade() {
		upg++;
		if (upg > 2) {
			upg = 2;
			return;
		}
		
			
	}

	@Override
	public void incKill() {
		kill++;
	}

	@Override
	public long killCount() {
		return kill;
	}

	@Override
	public double sellPrice() {
		double price = 0;
		for (int i = 0;i < costs.length && i <= upg;i++) {
			price += costs[i];
		}
		return price*TDPanel.sellLoss;
	}

}
