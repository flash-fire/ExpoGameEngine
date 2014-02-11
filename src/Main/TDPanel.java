package Main;

import highScores.*;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Toolkit;

import Buttons.*;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.SwingUtilities;

import Handlers.*;
import Interfaces.*;
import Towers.*;
import Main.SoundEffect.Volume;
import Mobs.*;

public class TDPanel extends GamePanel implements MouseMotionListener {
// I made a change
	public static String mode = "EASY";
	public static ButtonHandler buttons = new ButtonHandler();
	public static ImageSFXs effects = new ImageSFXs();
	static double goldSpent = 0; // current depreciated, but still may be used
	// later
	private boolean scoreSaved = false;
	private HighScoreManager hsm;
	private boolean highScore = false;
	private String playerName = "";
	private boolean showName = false;
	private BufferedImage introScreen = ImageLoader
			.loadImage("resources/images/intro/introScreen.png");
	private BufferedImage pattern = ImageLoader
			.loadImage("resources/images/Shop/Pattern.png");
	private BufferedImage sugarBag = ImageLoader
			.loadImage("resources/images/Other/youlose.png");
	private BufferedImage sourceImg = ImageLoader
			.loadImage("resources/images/Other/source.png");
	private Button upgradeButton;
	public static boolean changedDisp = false;
	public static Level level; // level handler
	public static LevelLoader lvlLoader;
	public static MapLoader mapLoader;
	public static final int maxLvl = 25; // highest level
	public static double sugar_int = 250; // starting sugar level
	public static double sellLoss = .8; // percent lost of tower when selling
	public static double failFactor = .15; // determines when towers start
	// failing
	public static boolean hyperMode = true; // true when towers are in hyper
	// mode.
	public static final double hyperFactor = .90; // % max hp needed to enter
	// hyper mode.

	public static double buffer = 1; // determines how stable the sugar is.
	private boolean lvlMode = true; // determines if game uses levels. Good for
	// testing.
	static int lvl = 10; // current level
	private static boolean intro = true;
	private static boolean showScores = false;
	public static final boolean defaultMode = false; // in this mode, the
	// tdpanel creates a
	// default game that is
	// used for testing.
	private static final long serialVersionUID = 115167490545858491L;
	private static MobHandler mh; // The Mob Handler handles the mobs.
	private static AbstractHandler<Bullet> bh; // The Bullet Handler handles the
	// bullets.
	public static int shopWd;
	public static int shopEnd;
	private static TowerHandler th; // tower handler!
	public static Font font;
	static int towerID = 0; // ID for towers
	public static long gold; // Gold for player
	public static double health; // Player health
	public static double costMult = .5;
	private static final int maxScores = 5;
	public static final long goldDef = 1000; // starting gold
	public static final long healthDef = 500; // starting hp
	public static final long sugarMax = 1000;
	private static boolean gameOver = false; // is game over?
	public static boolean win = false;
	static MonsterPath path; // Mob Path
	public static Point2D.Double source, target; // Mob source and sink
	private boolean tutMode = false; // is player in tutorial showing mode.
	private Point mouseLoc; // Mouse location
	private HealthBar hb = new HealthBar(); // health bar
	// Cursor shape and tower selected type selected in cursor for buying.
	private int select;

	// List of tower shapes
	private ArrayList<BufferedImage> towerPics;
	private ArrayList<Tower> towerTypes; // contains an array of towers that
	// aren't displayed, but used for
	// accessing purposes.

	// private DecimalFormat df = new DecimalFormat("0.##"); // 2 dp

	private boolean buyMode = false; // Is the player buying stuff?
	private int shopYLoc;
	private boolean upgMode = false; // is player upgrading?
//	private UpgradeHelper uph; // this helps to absract the buttons in the
	// upgrade interface.

	private Tower nextUpg; // This is the tower that will be upgraded.
	// this gives the ILLUSION of changing the game speed but is still useful.
	// It works by dividing the reload speeds, multiplying the velocities and
	// dividing spawn times!
	public static double speedFactor = 1;
	public static double towerFactor = 1; // speed factor for towers only
	public static int score; // score counter. Nerfed since prevous
	// version.
	public static long pauseTime = 0; // dictates when pause time starts so the
	// game knows how much to offset events.
	public static boolean god = false;

	/*
	 * Simple Constructor
	 */
	TDPanel(TDRunner tdr, long period) {
		super(period);
	}


	@Override
	protected void simpleInitialize() {
		shopWd = shopEnd = PWIDTH/10*7;
		System.out.println("width: " + PWIDTH);
		System.out.println("height: " + PHEIGHT);
		System.out.println("ShopWd " + shopWd);
		score = 0;
		hsm = new HighScoreManager();
		shopYLoc = PHEIGHT / 6 * 5; // this is the y offset of the shop.
		level = new Level();
		lvlLoader = new LevelLoader();
		SoundEffect.concBackground();
		speedFactor = 1;
		addMouseMotionListener(this); // adds mouse motion listener
		// Initialize handlers
		mh = new MobHandler();
		bh = new AbstractHandler<Bullet>();
		th = new TowerHandler();
		gold = goldDef; // starting gold.
		health = healthDef; // starting health.

		// Initialize tower types. This contains a list of all tower types in
		// game.
		towerTypes = new ArrayList<Tower>();
		for (int i = 0; i < 8; i++)
			towerTypes.add(TowerFactory.newTower(i));

		// Initialize Upgrade helper. This helps with the tower upgrade modes.
//		uph = new UpgradeHelper();
//		int startX = shopEnd + PWIDTH/84; // designates starting location of
//										// upg/mode boxes
//		int xCons = PWIDTH / 12; // designates width of box
//		int xGap = PWIDTH / 15; // designates gap between each button
//		int yCons = PHEIGHT / 11; // designates height of button
//		uph.add(startX, PHEIGHT / 20 * 17, xCons * 2 - xGap, yCons, " ");
//		uph.add(startX + PWIDTH/1680*18, PHEIGHT / 20 * 17, xCons * 2 - xGap, yCons,
//				"Sell");

		// This initializes the tower pictures for drawing.
		towerPics = new ArrayList<BufferedImage>();
		for (Tower t : towerTypes)
			// Initialized tower pics. The pics are used directly in draw to
			// avoid constant access calls
			towerPics.add(t.getPic());

		source = new Point2D.Double(50, 350); // monster start location
		target = new Point2D.Double(700, 300); // monster end
		// path = new MonsterPath(source, target); // Path most monsters follow
		path = new MapLoader().loadMap(2);
		/*
		 * path.add(new int[] {210, 210, 550, 460, 600 , 700, 400, 400 }, new
		 * int[] {260, 465, 470, 276, 100 , 500, 300, 200});
		 */
		source = path.source;
		target = path.getTarget();
		if(PWIDTH > 1500){
		font = new Font("SansSerif", Font.BOLD, 22); // creates default font.
		}
		else
			font = new Font("SansSerif", Font.BOLD, 16); // creates default font.
		if (defaultMode) {
			defaultTester();
		}
		createButtons();
		buttons.hideAll();
		buttons.show("INTRO");
		isPaused = true;

	}

	private void createButtons() {
		Updateable pause = new Updateable() {
			public void run() {
				pause();
			}
		};

		// Updateable, x, y, width, height -- Button initiation
		buttons.add(new Button(new String[] { "TEST", "EXPERIMENT" },
				new ImgButton(pause, "resources/images/Other/pause.png",
						PWIDTH / 18, PHEIGHT / 15, PWIDTH/16, PWIDTH/16)));

		Updateable leveled = new Updateable() {
			public void run() {
				buttons.hide("LEVELED");
				isPaused = false;
			}
		};

		buttons.add(new Button(new String[] { "LEVELED" }, new ImgButton(
				leveled, "resources/images/Other/LevelUp.png",
				PWIDTH / 2 - PWIDTH/4, PHEIGHT / 2 - PWIDTH/4, PWIDTH/4, PWIDTH/4)));

		Updateable easy = new Updateable() {
			public void run() {
				sellLoss = .9;
				buffer = 8;
				speedFactor = 1;
				towerFactor = 1.5;
				mode = "EASY";
				buttons.hide("OPTIONS");
				showName = false;
			}
		};

		buttons.add(new Button(
				new String[] { "EASY", "DIFFICULTY", "OPTIONS" },
				new ImgButton(easy, "resources/images/Other/easy.png",
						PWIDTH / 2 + PWIDTH/32, PHEIGHT / 2 + PHEIGHT/9, PWIDTH/33, PHEIGHT/13)));
		
		
		Updateable upgradeMenu = new Updateable() { //XXX upgrade button
			public void run() {
							
			}
		};

		upgradeButton = (new Button(new String[] {"UPGRADE"},new ImgButton(upgradeMenu, "resources/images/Other/upgradeMenu.png",
						TDPanel.shopEnd, PHEIGHT - (PHEIGHT-shopYLoc), PWIDTH-shopWd, PHEIGHT - shopYLoc)));
		TDPanel.buttons.add(upgradeButton);
		
		Updateable upgrade = new Updateable() { // XXX sell button
			public void run() {
				upgradeTower();
				System.out.println("upgraded just now");
			}
		};
		TDPanel.buttons.add(new Button(
				new String[] {"UPGRADE"},
				new ImgButton(upgrade, "resources/images/Other/upgradeArrow.png",
						TDPanel.shopEnd + (PWIDTH-shopWd)/10, PHEIGHT - (PHEIGHT-shopYLoc)+(PHEIGHT - shopYLoc)/4 , (PWIDTH-shopWd) / 5, (PHEIGHT - shopYLoc)/2)));

		
		
		Updateable sell = new Updateable() { // XXX sell button
			public void run() {
				upgradeTower();
			}
		};
		TDPanel.buttons.add(new Button(
				new String[] {"UPGRADE, SELL"},
				new ImgButton(upgradeMenu, "resources/images/Other/upgradeMenu.png",
						TDPanel.shopEnd, GamePanel.PHEIGHT / 20 * 17, GamePanel.PWIDTH / 12 - GamePanel.PWIDTH / 15, GamePanel.PHEIGHT / 11)));

		//XXX update pictures to include wording and pricing.
		Updateable hard = new Updateable() {
			public void run() {
				sellLoss = .9;
				buffer = 1;
				speedFactor = 2;
				towerFactor = 1;
				mode = "HARD";
				buttons.hide("OPTIONS");
				showName = false;
			}
		};

		buttons.add(new Button(
				new String[] { "HARD", "DIFFICULTY", "OPTIONS" },
				new ImgButton(hard, "resources/images/Other/hard.png",
						PWIDTH / 2 + PWIDTH/16, PHEIGHT / 2 + PHEIGHT/9, PWIDTH/33, PHEIGHT/13)));

		Updateable hide = new Updateable() {
			public void run() {
				buttons.toggle("OPTIONS");
				showName = false;
				// upgMode = false;
			}
		};
		//
		// buttons.add(new Button( new String[] {"TOGGLE"} ,
		// new ImgButton(hide, "resources/images/Other/toggle.png",
		// 200, 330, 80, 80)));

		// Tower Shop buttons
		Updateable shopBuy = new Updateable() {
			public void run() {
				if (mouseLoc == null) {
					System.out
							.println("******MOUSE LOC IS NULL BUT TRYING TO SELECT TOWER FOR BUYING.  ERROR!!");
					return;
				}
				Button clicking = buttons.get("SHOP", mouseLoc.x, mouseLoc.y); // button
				// that
				// clicked
				// this
				// event.
				if (clicking.button != null
						&& clicking.button instanceof TowerShopButton
						&& buttons.isHiding("PAUSE MENU")) {
					select = towerTypes
							.indexOf(((TowerShopButton) clicking.button)
									.getTower());
					buyMode = true;
					// upgMode = false;
				}

				else {
					System.out
							.println("TRYING TO BUY IN SHOP BUTTON A NONSHOP BUTTON. THIS SHOULD BE IMPOSSIBLE! ERROR!");
				}
			}
		};
		for (int i = 0; i < towerTypes.size(); i++) {
			Button next = new Button(new String[] { "SHOP",
					towerTypes.get(i).toString() }, new TowerShopButton(
					shopBuy, towerTypes.get(i), towerPics.get(i), shopWd/8*(i) + towerPics.get(0).getWidth()/2, shopYLoc + PWIDTH/32, shopWd/towerTypes.size(), PHEIGHT - shopYLoc));//XXX
			buttons.add(next);
			System.out.println("shop wd " + shopWd);
			System.out.println("pwidth " + PWIDTH);
		}

		Updateable toggleShop = new Updateable() {
			public void run() {
				changedDisp = true;
				buttons.toggle("SHOP");
				upgMode = false;
			}
		};
		// buttons.add(new Button(new String[] { "TOGGLE SHOP" }, new ImgButton(
		// toggleShop, "resources/images/Other/ShopToggle.png",
		// PWIDTH / 12 * 11, shopYLoc + 50, 80, 80)));

		Updateable toggleTut = new Updateable() {
			public void run() {
				tutMode = !tutMode;
			}
		};
		buttons.add(new Button(new String[] { "TOGGLE TUT" }, new ImgButton(
				toggleTut, "resources/images/Other/tutToggle.png",
				PWIDTH / 12 * 11, shopYLoc + PWIDTH/32, PWIDTH/22, PWIDTH/32)));

		Updateable playB = new Updateable() {
			public void run() {
				isPaused = false;
				intro = false;
				buttons.hide("INTRO");
				buttons.hide("OPTIONS");
				showName = false;
				buttons.show("TOGGLE TUT");
				buttons.show("SHOP");
				buttons.show("TEST");

			}
		};
		buttons.add(new Button(new String[] { "INTRO" }, new ImgButton(playB,
				"resources/images/intro/playButton.png", PWIDTH / 5 * 2 + PWIDTH/22,
				PHEIGHT / 3, PWIDTH/10, PWIDTH/16)));

		Updateable infoB = new Updateable() {
			public void run() {
				buttons.hide("INTRO");
				buttons.hide("OPTIONS");
				showName = false;
				buttons.show("INFO");
			}
		};
		buttons.add(new Button(new String[] { "INTRO" }, new ImgButton(infoB,
				"resources/images/intro/infoButton.png", PWIDTH / 5 * 2 + PWIDTH/22,
				PHEIGHT / 3 + PWIDTH/11, PWIDTH/10, PWIDTH/16)));

		Updateable infoToggled = new Updateable() {
			public void run() {
				buttons.hide("INFO");
				buttons.hide("OPTIONS");
				showName = false;
				buttons.show("INTRO");
			}
		};
		buttons.add(new Button(new String[] { "INFO" }, new ImgButton(
				infoToggled, "resources/images/intro/infoScreen.png",
				PWIDTH / 2, (PHEIGHT / 2) - PWIDTH/5, PHEIGHT/2, PWIDTH/2)));

		Updateable optionsB = new Updateable() {
			public void run() {
				buttons.toggle("OPTIONS");
				showName = !showName;
			}
		};
		buttons.add(new Button(new String[] { "INTRO" }, new ImgButton(
				optionsB, "resources/images/intro/optionsButton.png",
				PWIDTH / 5 * 2 + PWIDTH/22,
				PHEIGHT / 3 + 2*PWIDTH/11, PWIDTH/10, PWIDTH/16)));

		Updateable hsB = new Updateable() { // high score
			public void run() {
				buttons.show("HIGHSCORE");
				showScores = true;
				buttons.hide("OPTIONS");
				showName = false;
				buttons.hide("INTRO");
			}
		};
		buttons.add(new Button(new String[] { "INTRO" }, new ImgButton(
				// intro screen button to show high score board
				hsB, "resources/images/intro/highScoreButton.png",
				PWIDTH / 5 * 2 + PWIDTH/22,
				PHEIGHT / 3 + 3*PWIDTH/11, PWIDTH/10, PWIDTH/16)));

		Updateable hsToggle = new Updateable() { // high score board
			public void run() {
				showScores = false;
				buttons.hide("HIGHSCORE");
				buttons.hide("OPTIONS");
				showName = false;
				buttons.show("INTRO");

			}
		};
		buttons.add(new Button(new String[] { "HIGHSCORE" }, new ImgButton(
				hsToggle, "resources/images/Other/youlose.png",
				PWIDTH / 4 + PHEIGHT/7, PHEIGHT / 4, PHEIGHT/2 + PHEIGHT/20, PHEIGHT/2 + PHEIGHT/20)));

		Updateable toggleMenu = new Updateable() {
			public void run() {
				pause();
				changedDisp = true;
				buttons.toggle("PAUSE MENU");
				upgMode = false;
			}
		};
		// buttons.add(new Button(new String[] {"TOGGLE MENU"}, new
		// ImgButton(toggleMenu, "resources/images/Other/menu.png",
		// PWIDTH*11/12, shopYLoc, 60, 80)));
		//
		Updateable pauseMenu = new Updateable() {
			public void run() {
				// SoundEffect.setAllSound(Volume.MUTE);
				SoundEffect.BOOM1.vol = Volume.MUTE;
				SoundEffect.BOOM2.vol = Volume.MUTE;
			}
		};
		buttons.add(new Button(new String[] { "PAUSE MENU" }, new ImgButton(
				pauseMenu, "resources/images/Other/pauseMenu.png",
				PWIDTH / 2 - PHEIGHT/10*4, PHEIGHT / 2 - PHEIGHT/10*4, PHEIGHT/10*9, PHEIGHT/10*9)));
		buttons.add(new Button(new String[] { "PAUSE MENU" }, new InvisButton(
				pause, new Rectangle(PWIDTH / 2 - PWIDTH/16, PHEIGHT / 2 + PHEIGHT/40, PHEIGHT/10*3,
						PHEIGHT/15))));
		buttons.hide("PAUSE MENU");

		Updateable nameEntry = new Updateable() {
			public void run() {
				if (!scoreSaved && highScore) {
					hsm.addScore(playerName, score);
					scoreSaved = true;
					buttons.hide("NAMEENTRY");
				}
			}
		};
		buttons.add(new Button(new String[] { "NAMEENTRY" }, new ImgButton(
				nameEntry, "resources/images/intro/alphabet.png",
				PWIDTH / 2 - PHEIGHT/10, PHEIGHT / 5 * 2 - PHEIGHT/10, PHEIGHT/10*2, PHEIGHT/10*2)));
	};

	public static void loadMap(int num) {
		if (mapLoader == null)
			mapLoader = new MapLoader();
		path = mapLoader.loadMap(num);
		System.out.println(path.toString());
		source = path.source;
		target = path.getTarget();
	}

	public static MonsterPath getPath() {
		return path;
	}

	@Override
	protected void simpleRender(Graphics2D g2d) {
		MapMaker.draw(g2d);
		// shop!

		if (TDPanel.hyperMode) // changes colors in hyper mode
			g2d.setXORMode(Color.RED);

		if (isPaused) { // Sets transparency different when paused
			g2d.setColor(Color.white);
			// g2d.drawString("PAUSED!", PWIDTH / 2 - 50, PHEIGHT / 2 - 200);
			g2d.setComposite(AlphaComposite.getInstance(
					AlphaComposite.SRC_OVER, .25f));
			g2d.setColor(Color.green);
		} else {
			g2d.setComposite(AlphaComposite.getInstance(
					AlphaComposite.SRC_OVER, 1f));
		}

		// Path drawing
		path.draw(g2d);

		// Monster End Marker CHANGE MADE TO DRAW SOURCE IAMGE. ALSO ADDED
		// INSTANCE VARS.
		g2d.setColor(Color.red);
		g2d.drawImage(sourceImg, (int) source.x - sourceImg.getWidth() / 2,
				(int) source.y - sourceImg.getHeight() / 2, null);
		g2d.drawImage(sourceImg, (int) target.x - sourceImg.getWidth() / 2,
				(int) target.y - sourceImg.getHeight() / 2, null);

		// Various Handlers
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				.75f));
		bh.draw(g2d);
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				.25f));
		mh.draw(g2d);
		g2d.setComposite(AlphaComposite
				.getInstance(AlphaComposite.SRC_OVER, 1f));
		th.draw(g2d);
		drawTowerPreview(g2d);
		drawStats(g2d);
		if (tutMode) {
			tutModeOn(g2d);
		}
		if (upgMode) {
//			uph.draw(g2d, nextUpg);
		buttons.show("UPGRADE");
		}
		else
			buttons.hide("UPGRADE");
		if (win) {
			pause();
			gameOver = true;
		}
		level.draw(g2d); // draws levelly stuff.
		hb.draw(g2d);
		if (intro) {
			g2d.drawImage(introScreen, 0, 0, PWIDTH, PHEIGHT, null);
		}
		if (gameOver) {
			if (hsm.isHighScore(score, maxScores) && !scoreSaved) {
				buttons.show("NAMEENTRY");
			}
			gameOverMessage(g2d);
		}
		if (showName) {
			g2d.setColor(Color.blue);
			if(PWIDTH > 1500){
			g2d.setFont(new Font("SansSerif", Font.BOLD, 36));
			}
			else
				g2d.setFont(new Font("SansSerif", Font.BOLD, 24));
			g2d.drawString(playerName, PWIDTH / 2 - PHEIGHT/10, PHEIGHT / 2 - PWIDTH/21);
		}
		buttons.draw(g2d);
		if (showScores) {
			g2d.setFont(g2d.getFont().deriveFont(18.0f));
			g2d.setColor(Color.blue);
			g2d.drawString("HIGH SCORES", PWIDTH / 2 - PWIDTH/21,
					(PHEIGHT / 5 * 3) - PWIDTH/21);
			for (int i = 0; i < maxScores; i++) {
				g2d.drawString(hsm.getScores().get(i).getName(),
						PWIDTH / 2 - PHEIGHT/11, (PHEIGHT / 5 * 3 + (i * PWIDTH/56)) - PHEIGHT/22);
				g2d.drawString(hsm.getScores().get(i).getScore() + "",
						PWIDTH / 2 + PHEIGHT/105, (PHEIGHT / 5 * 3 + (i * PWIDTH/56)) - PHEIGHT/22);
			}
		}
		drawCursor(g2d); // tower cursor when buying

	}
//
//	public void test(Graphics2D g) {
//		gameOverMessage(g);
//		buttons.show("NAMEENTRY");
//		g.setFont(new Font("SansSerif", Font.BOLD, 36));
//		g.drawString("joanna", PWIDTH / 2 - 65, PHEIGHT / 2 - 95);
//	}

	public static boolean isPaused() {
		return isPaused;
	}

	/**
	 * draws a box to show tower info when selecting in buy mode
	 * 
	 * @param g2d
	 */
	private void drawTowerPreview(Graphics2D g2d) {
		if (buyMode && select < towerPics.size()) {
			upgMode = false;
			g2d.setColor(Color.white);
			g2d.setFont(new Font("SansSerif", Font.BOLD, 24));
			g2d.drawImage(pattern, null, shopEnd, PHEIGHT - (PHEIGHT-shopYLoc));
			g2d.drawImage(towerTypes.get(select).getPic(), null,
					shopEnd, PHEIGHT / 20 * 17);
			g2d.drawString(" " + towerTypes.get(select).toString(),
					shopEnd, PHEIGHT / 15 * 13);
			g2d.setColor(Color.yellow);
			g2d.setFont(new Font("SansSerif", Font.BOLD, 16));
			g2d.drawString("" + towerTypes.get(select).getBio(),
					shopEnd, PHEIGHT / 15 * 14);
		}
	}

	/**
	 * displays helpful hints on the game screen when player is in tutorial
	 * mode.
	 */
	private void tutModeOn(Graphics2D g) {
		if (health > 750) {
			g.setColor(new Color(123, 104, 238));
			g.fillRect(PWIDTH / 5, PHEIGHT / 6 - PWIDTH/56, PWIDTH/2 + PHEIGHT/10, PWIDTH/28);
			g.setColor(new Color(224, 255, 255));
			g.drawString(
					"Your Sugar is getting too high! Try adding more towers, or removing Harvesters!",
					PWIDTH / 5 + PWIDTH/84, PHEIGHT / 6);
			g.drawString(
					"You can sell towers by selecting them with your mouse.",
					PWIDTH / 5 + PWIDTH/84, PHEIGHT / 6 + PWIDTH/84);
		} else if (health < 250) {
			g.setColor(new Color(123, 104, 238));
			g.fillRect(PWIDTH / 5, PHEIGHT / 6 - PWIDTH/168*3, PHEIGHT/10*9, PWIDTH/168*6);
			g.setColor(new Color(224, 255, 255));
			g.drawString(
					"Your Sugar is getting too low! Try selling towers, or adding Harvesters!",
					PWIDTH / 5 + PWIDTH/84, PHEIGHT / 6);
			g.drawString(
					"You can sell towers by selecting them with your mouse.",
					PWIDTH / 5 + PWIDTH/84, PHEIGHT / 6 + PWIDTH/84);
		} else if (buyMode && health > 250 && health < 750) { // buy mode,
																// health not in
																// danger
			g.setColor(new Color(123, 104, 238));
			g.fillRect(40, PHEIGHT / 9 * 7 - PWIDTH/84, PWIDTH/84/2*60, PWIDTH/84/2*6); // buymode & dull
			g.fillRect(PWIDTH / 5 * 3 + PWIDTH/84/2*5, PHEIGHT / 9 * 7, PHEIGHT/2, PWIDTH/84/2*3); // description
																		// box
			g.fillRect(PWIDTH / 5 + PWIDTH/84, PHEIGHT / 10, 650, 40); // gold
			g.fillRect(30, PHEIGHT / 6 + PWIDTH/84*4, PWIDTH/84/2*50, PWIDTH/84/2*3); // exit buy mode
			g.setColor(new Color(224, 255, 255));
			g.drawString(
					"This is your wealth. Use it wisely to buy and upgrade towers.",
					PWIDTH / 5 + PWIDTH/84/2*3, PHEIGHT / 10 + PWIDTH/84);
			g.drawString(
					"You are in BUY mode. Choose a tower in the shop below.",
					PWIDTH/84/2*5, PHEIGHT / 9 * 7);
			g.drawString("This box describes the tower you have selected.",
					PWIDTH / 5 * 3 + PWIDTH/84/2*7, PHEIGHT / 9 * 7 + PWIDTH/84);
			g.drawString(
					"If the tower is dull, you cannot afford it right now.",
					PWIDTH/84/2*5, PHEIGHT / 9 * 7 + PWIDTH/84);
			g.drawString("Exit BUY mode by right clicking the mouse.", PWIDTH/84/2*4,
					PHEIGHT / 6 + PWIDTH/84/2*10);
		} else if (upgMode && health > 250 && health < 750) { // upgrade mode,
																// health not in
																// danger
			g.setColor(new Color(123, 104, 238));
			g.fillRect(PWIDTH / 5 + PWIDTH/84, PHEIGHT / 10, PWIDTH/84/2*65, PWIDTH/84/2*4); // gold
			g.fillRect(PWIDTH / 5, PHEIGHT / 6 - PWIDTH/84/2, PWIDTH/84/2*110, PWIDTH/84/2*3);
			g.setColor(new Color(224, 255, 255));
			g.drawString(
					"This is your wealth. Use it wisely to buy and upgrade towers.",
					PWIDTH / 5 + PWIDTH/84/2*3, PHEIGHT / 10 + PWIDTH/84);
			g.drawString(
					"You are in UPGRADE mode. You may upgrade or sell your selected tower using the upgrade window.",
					PWIDTH / 5 + PWIDTH/84, PHEIGHT / 6 + PWIDTH/84);
		} else { // health not in danger, not upgrading or buying
			g.setColor(new Color(123, 104, 238));
			g.fillRect(PWIDTH/84, PHEIGHT / 5, PWIDTH/84/2*90, PWIDTH/84/2*9); // helpful hints
			g.fillRect(PWIDTH / 5 * 3 + PWIDTH/84/2*3, PHEIGHT / 9 + PWIDTH/84/2*4, PWIDTH/84/2*60, PWIDTH/84/2*6); // sugar
																		// bar
			g.fillRect(PWIDTH / 5 * 3 - PWIDTH/84, PHEIGHT / 9 * 7, PWIDTH/84/2*60, PWIDTH/84/2*6); // select
																		// tower
			g.fillRect(PWIDTH / 5 + PWIDTH/84, PHEIGHT / 10, PWIDTH/84/2*65, PWIDTH/84/2*4); // gold
			g.setColor(new Color(224, 255, 255));
			g.drawString("This is your Sugar Bar.", PWIDTH / 4 * 3 - PWIDTH/84,
					PHEIGHT / 9 + PWIDTH/84/2*6);
			g.drawString(
					"You want to keep your sugar levels in the GREEN zone.",
					PWIDTH / 5 * 3 + PWIDTH/84/2*5, PHEIGHT / 9 + PWIDTH/84/2*9);
			g.drawString(
					"Normal Towers use sugar to function, while Harvesters increase your sugar level.",
					PWIDTH/84/2*3, PHEIGHT / 5 + PWIDTH/84/2*2);
			g.drawString(
					"Allowing Candy to get through your defenses greatly increases your sugar level.",
					PWIDTH/84/2*3, PHEIGHT / 5 + PWIDTH/84/2*5);
			g.drawString(
					"Extend your road by ctrl clicking a new location. This is expensive!",
					PWIDTH/84/2*3, PHEIGHT / 5 + PWIDTH/84/2*7);
			g.drawString(
					"This is your wealth. Use it wisely to buy and upgrade towers.",
					PWIDTH / 5 + PWIDTH/84/2*3, PHEIGHT / 10 + PWIDTH/84);
			g.drawString(
					"Select a Tower in the shop or on the screen to begin.",
					PWIDTH / 5 * 3, PHEIGHT / 9 * 7 + PWIDTH/84);
			g.drawString(
					"Press T to get rid of this tutorial and Play on your own!",
					PWIDTH / 5 * 3, PHEIGHT / 9 * 7 + PWIDTH/84/2*5);
		}
	}

	/**
	 * displays gold, level and score for player view
	 * 
	 * @param g2d
	 */
	private void drawStats(Graphics2D g2d) {
		g2d.setColor(Color.yellow);
		g2d.setFont(new Font("SansSerif", Font.BOLD, 36));
		g2d.drawString("GOLD: " + gold, PWIDTH / 2 - PWIDTH/84/2*30, PWIDTH/84/2*8);
		g2d.setColor(Color.black);
		g2d.setFont(new Font("SansSerif", Font.BOLD, 24));
		g2d.drawString("Round: " + (lvl - 10), PWIDTH / 2 + PWIDTH/84/2*45, PWIDTH/84/2*15);
		g2d.setColor(Color.white);
		g2d.setFont(new Font("SansSerif", Font.BOLD, 36));
		g2d.drawString("Score: " + score, PWIDTH / 2, PWIDTH/84/2*8);
		g2d.setFont(font);
	}

	/*
	 * Draws the mouse cursor when buying stuff.
	 */

	private void drawCursor(Graphics2D g2d) {
		// Tower Cursor
		if (mouseLoc != null && buyMode && select < towerPics.size()) {
			int rng = towerTypes.get(select).getRng();
			g2d.setColor(Color.cyan);
			int mlx = mouseLoc.x;
			int mly = mouseLoc.y;
			if (towerTypes.get(select).getCost() * costMult > gold) {
				BufferedImage[] seq = GifHandler.Mouse.sequence;
				BufferedImage next = seq[(int) frame() % seq.length];
				g2d.drawImage(next, null, mouseLoc.x - next.getWidth() / 2,
						mouseLoc.y - next.getHeight() / 2);
				g2d.setComposite(AlphaComposite.getInstance(
						AlphaComposite.SRC_OVER, .1f));
			} else {
				g2d.setComposite(AlphaComposite.getInstance(
						AlphaComposite.SRC_OVER, .5f));
			}
			AffineTransform t = new AffineTransform();
			t.setToTranslation(mlx - towerPics.get(select).getWidth() / 2, mly
					- towerPics.get(select).getHeight() / 2);
			g2d.fillOval(mlx - rng, mly - rng, rng * 2, rng * 2);
			// g2d.draw(towerTypes.get(select).getPic().createTransformedArea(t));
			g2d.drawImage(towerPics.get(select), t, null);

		} else if (mouseLoc != null) {
			BufferedImage[] seq = GifHandler.Mouse.sequence;
			BufferedImage next = seq[(int) frame() % seq.length];
			g2d.drawImage(next, null, mouseLoc.x - next.getWidth() / 2,
					mouseLoc.y - next.getHeight() / 2);
		}
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
	}

	@Override
	protected void gameOverMessage(Graphics2D g) {
		buttons.hide("PAUSE MENU");
		isPaused = true;
		g.drawImage(sugarBag, null, PWIDTH / 4, PHEIGHT / 6);
		g.setColor(Color.blue);
		g.setFont(g.getFont().deriveFont(24.0f));
		g.drawString("FINAL SCORE ", PWIDTH / 2 - PWIDTH/84/2*7, PHEIGHT / 2 - PWIDTH/84);
		g.setColor(Color.red);
		g.setFont(g.getFont().deriveFont(48.0f));
		g.drawString(" " + score, PWIDTH / 2 - PWIDTH/84/2*6, PHEIGHT / 2 + PWIDTH/84/2*2);
		g.setFont(g.getFont().deriveFont(24.0f));
		g.drawString("Backspace to reset", PWIDTH / 5 * 2 + PWIDTH/84/2*4,
				(PHEIGHT / 5 * 4 - PWIDTH/84/2*4));
		g.setColor(Color.blue);
		for (int i = 0; i < maxScores; i++) {
			g.drawString(hsm.scores.get(i).getName(), PWIDTH / 2 - PWIDTH/84/2*9,
					(PHEIGHT / 5 * 3 + (i * PWIDTH/84/2*3)) - PWIDTH/84/2*5);
			g.drawString(hsm.scores.get(i).getScore() + "", PWIDTH / 2 + PWIDTH/84/2*4,
					(PHEIGHT / 5 * 3 + (i * PWIDTH/84/2*3)) - PWIDTH/84/2*5);
		}
	}

	@Override
	protected void simpleUpdate() {

		if (health <= 0 || health >= sugarMax) {
			pause();
			gameOver = true;
		}

		if (level.isEndOfLevel() && mh.size() == 0 && lvlMode) {
			pause();
		}
		if (!isPaused) {
			if (lvl > 3 && frameCount % sugar_int == 0) {
				if (lvl < 25)
					health -= speedFactor * buffer; // long term depletion of
													// sugar
				else
					health -= 8d * speedFactor * buffer;
			}
			hyperCheckSet(); // checks and maintains hyper mode.
			mh.update();
			bh.update();
			th.update();
			mh.flush();
			bh.flush();
			th.flush();
			level.update();
			path.brownian();
			if (god) {
				// add god mode to turn off reloads
			}
		}
	}

	/*
	 * Returns the bullet handler.
	 */
	public static AbstractHandler<Bullet> getBulletHandler() {
		return bh;
	}

	/*
	 * Returns the tower handler.
	 */
	public static TowerHandler getTowerHandler() {
		return th;
	}

	/*
	 * Returns the mob handler.\
	 */
	public static MobHandler getMobHandler() {
		return mh;
	}

	/*
	 * Towers can not be placed in shop. They also can't be too close to other
	 * towers.
	 */
	@SuppressWarnings("unused")
	private boolean canPlace(int x, int y) {
		return y < shopYLoc && !th.isTowerNear(new Point2D.Double(x, y), 25);
	}

	@Override
	protected void mousePress(MouseEvent e) {
		mouseLoc = e.getPoint();
		if (e.isAltDown())
			MapMaker.addNode(new Point2D.Double(mouseLoc.x, mouseLoc.y));
		if (e.isControlDown()) {
			if (gold > (new Point2D.Double(e.getX(), e.getY())).distance(path
					.getTarget()) * lvl / 19 + 16) {
				gold -= (new Point2D.Double(e.getX(), e.getY())).distance(path
						.getTarget()) * lvl / 19 + 16;
				path.add(path.getTarget());
				path.setTarget(new Point2D.Double(e.getX(), e.getY()));
				source = path.source;
				target = path.getTarget();
			}
		}
		int x = e.getX();
		int y = e.getY();
		buttons.click(mouseLoc);
		if (y > shopYLoc && x < shopEnd && !intro
				&& buttons.isHiding("PAUSE MENU")) { // mouse is in shop area.
			buyMode = true;
			System.out.println("buymode true");

		} else if (buyMode && !SwingUtilities.isRightMouseButton(e)) {
			shopBuy(x, y);
//		}

//		else if (upgMode && uph.inUpgBox(mouseLoc)) { //XXX replace with button
//			upgradeTower();
		} else if (th.isTowerNear(new Point2D.Double(x, y), PWIDTH/84/2*5)) {
			nextUpg = th.nearestTower(mouseLoc);
			upgMode = true;
			buyMode = false;
		} else { // not near a tower, not in buy mode, set all false.
			upgMode = false;
			buyMode = false;
		}
//		if (nextUpg != null) // XXX fix nextupg
//			uph.setUpgButton(nextUpg.upgName());
	}

	private void upgradeTower() {
//		int button = uph.whatContains(mouseLoc);
//		mode = "Trying to upgrade tower in TDPANEL\n" + "BUTTON: " + button;
		assert (nextUpg != null) : "Upgraded tower is null but wants to be updated!";
//		switch (button) {
//		case -1:
//			break;
		/*
		 * case 0: case 1 : case 2: // Set Modes nextUpg.setMode(button+1);
		 * break;
		 */
//		case 0:
			if (nextUpg.canUpg()) {
				if (gold >= costMult * nextUpg.getUpgCost()) { // do we have
					// enough gold
					// to upgrade?
					gold -= costMult * nextUpg.getUpgCost();
					goldSpent += costMult * nextUpg.getUpgCost();
					nextUpg.upgrade(); // upgrades tower
//					uph.setUpgButton(nextUpg.upgName()); // upgrades button
					// name.
				}
			}
//			break;
//		case 1:
//			nextUpg.sell();
//			nextUpg = null;
//			upgMode = false;
//			break;
//		}
	}

	private void reset() {
		System.out.println("CALLINGRESET!");
		intro = true;
		buyMode = false;
		buttons.hideAll();
		buttons.show("INTRO");
		scoreSaved = false;
		lvl = 10;
		showName = false;
		playerName = "";
		score = 0;
		mh.clear();
		th.clear();
		bh.clear();
		level.clear();
		level = new Level();
		lvlLoader.reset();
		health = healthDef;
		gameOver = false;
		win = false;

		gold = goldDef;
	}

	private void shopBuy(int x, int y) {
		if (select >= towerTypes.size()) {
			System.out.println("Trying to buy a nonexistant tower");
		}
		AbstractTower tow = TowerFactory.newTower(select); // gets new tower.
		if (gold >= tow.getCost() * costMult) { // checks cost.
			gold -= tow.getCost() * costMult;
			goldSpent += tow.getCost() * costMult;
			tow.move(new Point2D.Double(x, y)); // moves tower.
			th.add(tow); // adds tower
		}
	}

	@Override
	protected void keyPress(KeyEvent e) {

	}

	private void pause() {

		isPaused = !isPaused;
		if (isPaused) {
			if (!intro)
				buttons.show("PAUSE MENU");
			buyMode = false;
			pauseTime = frameCount;
		} else {
			buttons.hide("PAUSE MENU");
			level.offsetEvents(frameCount - pauseTime);
		}
		if (level.isEndOfLevel() && mh.size() == 0) {
			incLevel();
			buttons.hide("PAUSE MENU");
			if (score > 0) {
				buttons.show("LEVELED");
			}
		}
	}

	@Override
	protected void keyRelease(KeyEvent e) {
		int k = e.getKeyCode();
		if (intro) {
			switch (k) {
			case KeyEvent.VK_ENTER:
				isPaused = false;
				intro = false;
				buttons.hide("INTRO");
				buttons.hide("OPTIONS");
				showName = false;
				buttons.show("TOGGLE TUT");
				buttons.show("SHOP");
				buttons.show("TEST");
				break;
			}
		} else if (!buttons.isHiding("NAMEENTRY") && !scoreSaved) {
			showName = true;
			if (playerName.length() < 6) {
				switch (k) {
				case KeyEvent.VK_A:
					playerName = playerName + "A";
					break;
				case KeyEvent.VK_B:
					playerName = playerName + "B";
					break;
				case KeyEvent.VK_C:
					playerName = playerName + "C";
					break;
				case KeyEvent.VK_D:
					playerName = playerName + "D";
					break;
				case KeyEvent.VK_E:
					playerName = playerName + "E";
					break;
				case KeyEvent.VK_F:
					playerName = playerName + "F";
					break;
				case KeyEvent.VK_G:
					playerName = playerName + "G";
					break;
				case KeyEvent.VK_H:
					playerName = playerName + "H";
					break;
				case KeyEvent.VK_I:
					playerName = playerName + "I";
					break;
				case KeyEvent.VK_J:
					playerName = playerName + "J";
					break;
				case KeyEvent.VK_K:
					playerName = playerName + "K";
					break;
				case KeyEvent.VK_L:
					playerName = playerName + "L";
					break;
				case KeyEvent.VK_M:
					playerName = playerName + "M";
					break;
				case KeyEvent.VK_N:
					playerName = playerName + "N";
					break;
				case KeyEvent.VK_O:
					playerName = playerName + "O";
					break;
				case KeyEvent.VK_P:
					playerName = playerName + "P";
					break;
				case KeyEvent.VK_Q:
					playerName = playerName + "Q";
					break;
				case KeyEvent.VK_R:
					playerName = playerName + "R";
					break;
				case KeyEvent.VK_S:
					playerName = playerName + "S";
					break;
				case KeyEvent.VK_T:
					playerName = playerName + "T";
					break;
				case KeyEvent.VK_U:
					playerName = playerName + "U";
					break;
				case KeyEvent.VK_V:
					playerName = playerName + "V";
					break;
				case KeyEvent.VK_W:
					playerName = playerName + "W";
					break;
				case KeyEvent.VK_X:
					playerName = playerName + "X";
					break;
				case KeyEvent.VK_Y:
					playerName = playerName + "Y";
					break;
				case KeyEvent.VK_Z:
					playerName = playerName + "Z";
					break;
				case KeyEvent.VK_BACK_SPACE:
					playerName = "";
					break;
				case KeyEvent.VK_SPACE:
					playerName = playerName + " ";
					break;
				case KeyEvent.VK_ENTER:
					scoreSaved = true;
					buttons.hide("NAMEENTRY");
					showName = false;
					hsm.addScore(playerName, score);
					break;
				} // switch
			} // if player name length
			else {
				switch (k) {
				case KeyEvent.VK_BACK_SPACE:
					playerName = "";
					break;
				case KeyEvent.VK_SPACE:
					playerName = playerName + "\t\t";
					break;
				case KeyEvent.VK_ENTER:
					scoreSaved = true;
					buttons.hide("NAMEENTRY");
					showName = false;
					hsm.addScore(playerName, score);
					break;
				} // switch
			} // else
		} // name entry mode
		else if (!intro && buttons.isHiding("NAMEENTRY")
				&& buttons.isHiding("PAUSE MENU")) { // normal game play
			switch (k) {
			case KeyEvent.VK_P:
				pause();
				break;
			case KeyEvent.VK_0:
				if (e.isAltDown())
					gold += 10000;
				break;
			case KeyEvent.VK_I:
				// god = !god;
				// if (nextUpg != null)
				// nextUpg.upgrade();
				break;
			case KeyEvent.VK_S:
				nextUpg.sell();
				nextUpg = null;
				upgMode = false;
				break;
			case KeyEvent.VK_BACK_SPACE:
				reset();
				break;
			case KeyEvent.VK_F:
				if (speedFactor == 1)
					speedFactor = 2;
				else if (speedFactor == .5)
					speedFactor = 1;
//				else
//					speedFactor = .5;
				break;
			case KeyEvent.VK_ENTER:
				speedFactor = 10;
				break;
			// case KeyEvent.VK_B:
			// buyMode = !buyMode;
			// break;
			case KeyEvent.VK_T:
				tutMode = !tutMode;
				break;
			case KeyEvent.VK_U:
				if (mouseLoc != null) {
					mh.add(new RainbowMob(new Point2D.Double(mouseLoc.x,
							mouseLoc.y), path, 5, 10, false));
				}
				break;
			case KeyEvent.VK_1:
			case KeyEvent.VK_2:
			case KeyEvent.VK_3:
			case KeyEvent.VK_4: // For VK_1 .. VK_n we are doing hot keys.
			case KeyEvent.VK_5:
			case KeyEvent.VK_6:
			case KeyEvent.VK_7:
			case KeyEvent.VK_8:
				select = Integer.parseInt("" + e.getKeyChar()) - 1;
				buyMode = true;
				break;
			}// switch
		}// else if

		else if (!intro && !buttons.isHiding("PAUSE MENU")) {
			switch (k) {
			case KeyEvent.VK_BACK_SPACE:
				reset();
				break;
			case KeyEvent.VK_P:
				pause();
				break;
			}// switch
		}// else if
		else

		if (buyMode)
			upgMode = false;
		if (upgMode)
			buyMode = false;
	}

	@Override
	protected void mouseRelease(MouseEvent e) {
		mouseLoc = e.getPoint();
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		mouseLoc = arg0.getPoint();
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		mouseLoc = arg0.getPoint();
	}

	public static boolean isRunning() {
		return !gameOver;
	}

	public static long frame() {
		return frameCount;
	}

	/*
	 * Used for testing the game earlier.
	 */
	private void defaultTester() {
		gold = 10000;
	}

	private void incLevel() {
		if (lvl < 22) {
			lvlLoader.loadLvl(lvl++);
		} else {
			win();
		}
	}

	private static void win() {
		win = true;
	}

	@SuppressWarnings("unused")
	private int randInt(int max) {
		return (int) Math.round(max * Math.random());
	}

	public static void hyperCheckSet() {
		if (health >= 1000 * hyperFactor && !hyperMode) {
			towerFactor = 2;
			buffer /= 3d;
			hyperMode = true;
		}
		if (health < 1000 * hyperFactor && hyperMode) {
			towerFactor = 1;
			buffer *= 3d;
			hyperMode = false;
		}
	}

	public static int incTowID() {
		return ++towerID;
	}

}