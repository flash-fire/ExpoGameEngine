package Main;

// GamePanel.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

import javax.swing.JPanel;


public abstract class GamePanel extends JPanel implements Runnable {
	private static final long serialVersionUID = 1863596360846514344L;
	private static long MAX_STATS_INTERVAL = 1000000000L;
	// private static long MAX_STATS_INTERVAL = 1000L;
	// record stats every 1 second (roughly)

	private static int NUM_FPS = 10;
	// number of FPS values stored to get an average

	public static int PWIDTH = 800; // size of panel
	public static int PHEIGHT = 600;

	protected static final int NO_DELAYS_PER_YIELD = 16;
	/*
	 * Number of frames with a delay of 0 ms before the animation thread yields
	 * to other running threads.
	 */

	protected static int MAX_FRAME_SKIPS = 5; // was 2;
	// no. of frames that can be skipped in any one animation loop
	// i.e the games state is updated but not rendered

	private Thread animator; // the thread that performs the animation
	protected boolean running = false; // used to stop the animation thread
	protected static boolean isPaused = false;

	protected long period; // period between drawing in _nanosecs_

	// used for gathering statistics
	private long statsInterval = 0L; // in ns
	private long prevStatsTime;
	private long totalElapsedTime = 0L;
	private long gameStartTime;
	protected int timeSpentInGame = 0; // in seconds

	protected static long frameCount = 0;
	private double fpsStore[];
	private long statsCount = 0;
	protected double averageFPS = 0.0;

	private long framesSkipped = 0L;
	private long totalFramesSkipped = 0L;
	private double upsStore[];
	protected double averageUPS = 0.0;
	private DecimalFormat df = new DecimalFormat("0.##"); // 2 dp

	// used at game termination
	protected boolean gameOver = false;
	BufferedImage background;
	// off screen rendering
	private Graphics2D dbg;
	private Image dbImage = null;

	public GamePanel(long period) {
		// Sets to full screen!
	    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    GraphicsDevice gs = ge.getDefaultScreenDevice();
	    PWIDTH = (int) Math.round(gs.getDefaultConfiguration().getBounds().getWidth());
	    PHEIGHT = (int) Math.round(gs.getDefaultConfiguration().getBounds().getHeight());
	    
		this.period = period;

		setBackground(Color.white);
		setPreferredSize(new Dimension(PWIDTH, PHEIGHT));

		setFocusable(true);
		requestFocus(); // the JPanel now has focus, so receives key events
		//readyForTermination(); comented er code.  

		simpleInitialize();
		//background = ImageLoader.loadScaledImage("resources/images/background/jellybeans.jpg", PWIDTH, PHEIGHT);
		background = ImageLoader.loadScaledImage("resources/images/background/candyland2.jpg",PWIDTH, PHEIGHT, false);
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				mousePress(e);
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				mouseRelease(e);
			}
		});
		
		addKeyListener(new KeyAdapter() { // pasted from terminate.
			// listen for esc, q, end, ctrl-c on the canvas to
			// allow a convenient exit from the full screen configuration
			@Override
			public void keyPressed(KeyEvent e) {
				int keyCode = e.getKeyCode();
				if ((keyCode == KeyEvent.VK_ESCAPE)
						|| (keyCode == KeyEvent.VK_END)
						|| ((keyCode == KeyEvent.VK_C) && e.isControlDown())) {
					running = false;	
					MapMaker.dump();
				}
				keyPress(e);
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				keyRelease(e);
			}
		});

		// initialise timing elements
		fpsStore = new double[NUM_FPS];
		upsStore = new double[NUM_FPS];
		for (int i = 0; i < NUM_FPS; i++) {
			fpsStore[i] = 0.0;
			upsStore[i] = 0.0;
		}

	} // end of GamePanel()

	/**
	 * Sets up a listener to kill the game loop if the user attempts to quit
	 */
	@SuppressWarnings("unused")
	private void readyForTermination() {

	} // end of readyForTermination()

	/**
	 * Triggers the animation loop to start
	 */
	@Override
	public void addNotify()
	// wait for the JPanel to be added to the JFrame before starting
	{
		super.addNotify(); // creates the peer
		startGame(); // start the thread
	}

	/**
	 * Starts the animation loop
	 */
	private void startGame()
	// initialise and start the thread
	{
		if (animator == null || !running) {
			animator = new Thread(this);
			animator.start();
		}
	} // end of startGame()

	// ------------- game life cycle methods ------------
	// called by the JFrame's window listener methods

	public void resumeGame()
	// called when the JFrame is activated / deiconified
	{
		isPaused = false;
	}

	public void pauseGame()
	// called when the JFrame is deactivated / iconified
	{
		isPaused = true;
	}

	public void stopGame()
	// called when the JFrame is closing
	{
		running = false;
	}

	// ----------------------------------------------

	@Override
	public void run()
	/* The frames of the animation are drawn inside the while loop. */
	{
		long beforeTime, afterTime, timeDiff, sleepTime;
		long overSleepTime = 0L;
		int noDelays = 0;
		long excess = 0L;

		gameStartTime = System.nanoTime();
		prevStatsTime = gameStartTime;
		beforeTime = gameStartTime;

		running = true;

		while (running) {
			gameUpdate();
			gameRender();
			paintScreen();

			afterTime = System.nanoTime();
			timeDiff = afterTime - beforeTime;
			sleepTime = (period - timeDiff) - overSleepTime;

			if (sleepTime > 0) { // some time left in this cycle
				try {
					Thread.sleep(sleepTime / 1000000L); // nano -> ms
				} catch (InterruptedException ex) {
				}
				overSleepTime = (System.nanoTime() - afterTime) - sleepTime;
			} else { // sleepTime <= 0; the frame took longer than the period
				excess -= sleepTime; // store excess time value
				overSleepTime = 0L;

				if (++noDelays >= NO_DELAYS_PER_YIELD) {
					Thread.yield(); // give another thread a chance to run
					noDelays = 0;
				}
			}

			beforeTime = System.nanoTime();

			/*
			 * If frame animation is taking too long, update the game state
			 * without rendering it, to get the updates/sec nearer to the
			 * required FPS.
			 */
			int skips = 0;
			while ((excess > period) && (skips < MAX_FRAME_SKIPS)) {
				excess -= period;
				gameUpdate(); // update state but don't render
				skips++;
			}
			framesSkipped += skips;
			storeStats();
		}
		printStats();
		System.exit(0); // so window disappears
	} // end of run()

	/**
	 * Renders to the backbuffer
	 */
	private void gameRender() {
		if (dbImage == null) {
			dbImage = createImage(PWIDTH, PHEIGHT);
			if (dbImage == null) {
				System.out.println("dbImage is null");
				return;
			} 
				dbg = (Graphics2D) dbImage.getGraphics();
		}
		// clear the background
		dbg.drawImage(background, null, 0, 0);
		simpleRender(dbg);


		if (gameOver)
			gameOverMessage(dbg);
	} // end of gameRender()

	/**
	 * Paints the back buffer
	 */
	private void paintScreen()
	// use active rendering to put the buffered image on-screen
	{
		Graphics g;
		try {
			g = this.getGraphics();
			if ((g != null) && (dbImage != null))
				g.drawImage(dbImage, 0, 0, null);
			g.dispose();
		} catch (Exception e) {
			System.out.println("Graphics context error: " + e);
		}
	} // end of paintScreen()

	/**
	 * Should be update the game state
	 */
	private void gameUpdate() {
		if (!isPaused && !gameOver)
			simpleUpdate();

	} // end of gameUpdate()

	private void storeStats() {
		frameCount++;
		statsInterval += period;

		if (statsInterval >= MAX_STATS_INTERVAL) { // record stats every
			// MAX_STATS_INTERVAL
			long timeNow = System.nanoTime();
			timeSpentInGame = (int) ((timeNow - gameStartTime) / 1000000000L); 
			
			long realElapsedTime = timeNow - prevStatsTime; // time since last

			// stats collection
			totalElapsedTime += realElapsedTime;

			totalFramesSkipped += framesSkipped;

			double actualFPS = 0; // calculate the latest FPS and UPS
			double actualUPS = 0;
			if (totalElapsedTime > 0) {
				actualFPS = (((double) frameCount / totalElapsedTime) * 1000000000L);
				actualUPS = (((double) (frameCount + totalFramesSkipped) / totalElapsedTime) * 1000000000L);
			}

			// store the latest FPS and UPS
			fpsStore[(int) statsCount % NUM_FPS] = actualFPS;
			upsStore[(int) statsCount % NUM_FPS] = actualUPS;
			statsCount = statsCount + 1;

			double totalFPS = 0.0; // total the stored FPSs and UPSs
			double totalUPS = 0.0;
			for (int i = 0; i < NUM_FPS; i++) {
				totalFPS += fpsStore[i];
				totalUPS += upsStore[i];
			}

			if (statsCount < NUM_FPS) { // obtain the average FPS and UPS
				averageFPS = totalFPS / statsCount;
				averageUPS = totalUPS / statsCount;
			} else {
				averageFPS = totalFPS / NUM_FPS;
				averageUPS = totalUPS / NUM_FPS;
			}
			framesSkipped = 0;
			prevStatsTime = timeNow;
			statsInterval = 0L; // reset
		}
	} // end of storeStats()

	private void printStats() {
		System.out.println("Frame Count/Loss: " + frameCount + " / "
				+ totalFramesSkipped);
		System.out.println("Average FPS: " + df.format(averageFPS));
		System.out.println("Average UPS: " + df.format(averageUPS));
		System.out.println("Time Spent: " + timeSpentInGame + " secs");
	} // end of printStats()

	/**
	 * Should implement game specific rendering
	 * 
	 * @param g
	 */
	protected abstract void simpleRender(Graphics2D g);

	/**
	 * Should display a game specific game over message
	 * 
	 * @param g
	 */
	protected abstract void gameOverMessage(Graphics2D g);


	protected abstract void simpleUpdate();

	/**
	 * This just gets called when a click occurs, no default behavior
	 */
	protected abstract void mousePress(MouseEvent e);

	/**
	 * Should be overridden to initialize the game specific components
	 */
	protected abstract void simpleInitialize();
	
	/**
	 * Should be used for when a mouse click occurs.
	 * @param e
	 */
	protected abstract void keyPress(KeyEvent e);
	
	protected abstract void keyRelease(KeyEvent e);
	
	protected abstract void mouseRelease(MouseEvent e);
	
} // end of GamePanel class
