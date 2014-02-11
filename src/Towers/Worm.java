package Towers;


// Worm.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

/* Contains the worm's internal data structure (a circular buffer)
   and code for deciding on the position and compass direction
   of the next worm move.
*/

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

import Interfaces.Mob;
import Interfaces.Tower;
import Main.ImageLoader;
import Main.TDPanel;


public class Worm
{
  // size and number of dots in a worm
  public static final int[] DOTSIZES = new int[] {8, 12, 16};
  public static final int[] RADII = new int[] {4, 6, 8};
  public static final int[] RANGE = new int[] {50, 100, 150};
  public static final int[] DMG = new int[] {50, 100, 150};
  int upg;
  private static final int[] MAXPOINTS = new int[] {10, 20, 50};

  // compass direction/bearing constants
  private static final int NUM_DIRS = 8;
  private static final int N = 0;  // north, etc going clockwise
  private static final int NE = 1;
  private static final int E = 2;
  private static final int SE = 3;
  private static final int S = 4;
  private static final int SW = 5;
  private static final int W = 6;
  private static final int NW = 7;
  
  public static BufferedImage[] sword; 

  private int currCompass;  // stores the current compass dir/bearing

  // Stores the increments in each of the compass dirs.
  // An increment is added to the old head position to get the
  // new position.
  Point2D.Double incrs[];

  // probabiliy info for selecting a compass dir.
  private static final int NUM_PROBS = 9;
  private int probsForOffset[];

  // cells[] stores the dots making up the worm
  // it is treated like a circular buffer
  private Point cells[];
  private int nPoints;
  private int tailPosn, headPosn;   // the tail and head of the buffer

  private int pWidth, pHeight;   // panel dimensions
  private long startTime;        // in ms
  private Tower tow;

  public Worm(int pW, int pH, int upg, Tower tow)
  {
	this.upg = upg;
	this.tow = tow;
	if (sword == null) {
		sword = new BufferedImage[3];
		sword[0] = ImageLoader.loadScaledImage("resources/images/Towers/sword.png", 50, 20, false);
		sword[1] = ImageLoader.loadScaledImage("resources/images/Towers/sword.png", 100, 40, false);
		sword[2] = ImageLoader.loadScaledImage("resources/images/Towers/sword.png", 150, 60, false);
	}
    pWidth = pW; pHeight = pH;
    cells = new Point[MAXPOINTS[upg]];   // initialise buffer
    nPoints = 0;
    headPosn = -1;  tailPosn = -1;

    // increments for each compass dir
    incrs = new Point2D.Double[NUM_DIRS];
    incrs[N] = new Point2D.Double(0.0, -1.0);
    incrs[NE] = new Point2D.Double(0.7, -0.7);
    incrs[E] = new Point2D.Double(1.0, 0.0);
    incrs[SE] = new Point2D.Double(0.7, 0.7);
    incrs[S] = new Point2D.Double(0.0, 1.0);
    incrs[SW] = new Point2D.Double(-0.7, 0.7);
    incrs[W] = new Point2D.Double(-1.0, 0.0);
    incrs[NW] = new Point2D.Double(-0.7, -0.7);

    // probability info for selecting a compass dir.
    //    0 = no change, -1 means 1 step anti-clockwise,
    //    1 means 1 step clockwise, etc.
    /* The array means that usually the worm continues in
       the same direction but may bear slightly to the left
       or right. */
    probsForOffset = new int[NUM_PROBS];
    probsForOffset[0] = 0;  probsForOffset[1] = 0;
    probsForOffset[2] = 0;  probsForOffset[3] = 1;
    probsForOffset[4] = 1;  probsForOffset[5] = 2;
    probsForOffset[6] = -1;  probsForOffset[7] = -1;
    probsForOffset[8] = -2;

  } // end of Worm()


  public boolean nearHead(int x, int y)
  // is (x,y) near the worm's head?
  { if (nPoints > 0) {
      if( (Math.abs( cells[headPosn].x + RADII[upg] - x) <= DOTSIZES[upg]) &&
           (Math.abs( cells[headPosn].y + RADII[upg] - y) <= DOTSIZES[upg]) )
        return true;
    }
    return false;
  } // end of nearHead()


  public boolean touchedAt(int x, int y, int rad)
  // is (x,y) near any part of the worm's body?
  {
    int i = tailPosn;
    while (i != headPosn) {
      if( (Math.abs( cells[i].x + rad - x) <= rad) &&
          (Math.abs( cells[i].y + rad - y) <= rad) )
        return true;
      i = (i+1) % MAXPOINTS[upg];
    }
    return false;
  }  // end of touchedAt()

  
  public void attack(int rad)
  // is (x,y) near any part of the worm's body?
  {
    int i = tailPosn;
    while (i != headPosn) {
    	Mob next;
    	if ((next = TDPanel.getMobHandler().nearMobProx(new Point2D.Double(cells[i].x, cells[i].y), rad)) != null)
    		next.defend(DMG[upg], tow);
      i = (i+1) % MAXPOINTS[upg];
    }
  }  // end of touchedAt()

  public void move()
  /* A move causes the addition of a new dot to the front of
     the worm, which becomes its new head. A dot has a position
     and compass direction/bearing, which is derived from the
     position and bearing of the old head.

     move() is complicated by having to deal with 3 cases:
       * when the worm is first created
       * when the worm is growing
       * when the worm is MAXPOINTS[upg] long (then the addition
         of a new head must be balanced by the removal of a
         tail dot)
  */
  {
	attack(RANGE[upg]);
    int prevPosn = headPosn;  // save old head posn while creating new one
    headPosn = (headPosn + 1) % MAXPOINTS[upg];

    if (nPoints == 0) {   // empty array at start
      tailPosn = headPosn;
      currCompass = (int)( Math.random()*NUM_DIRS );  // random dir.
      cells[headPosn] = new Point( pWidth/2, pHeight/2 ); // center pt
      nPoints++;
    }
    else if (nPoints == MAXPOINTS[upg]) {    // array is full
      tailPosn = (tailPosn + 1) % MAXPOINTS[upg];    // forget last tail
      newHead(prevPosn);
    }
    else {     // still room in cells[]
      newHead(prevPosn);
      nPoints++;
    }
  }  // end of move()


  private void newHead(int prevPosn)
  /* Create new head position and compass direction/bearing.

     This has two main parts. Initially we try to generate
     a head by varying the old position/bearing. But if
     the new head hits an obstacle, then we shift
     to a second phase. 

     In the second phase we try a head which is 90 degrees
     clockwise, 90 degress clockwise, or 180 degrees reversed
     so that the obstacle is avoided. These bearings are 
     stored in fixedOffs[].
  */
  {
    Point newPt;
    int newBearing;
    int fixedOffs[] = {-2, 2, -4};  // offsets to avoid an obstacle

    newBearing = varyBearing();
    newPt = nextPoint(prevPosn, newBearing );
    Point2D.Double next = new Point2D.Double(newPt.x, newPt.y);
      // Get a new position based on a semi-random
      // variation of the current position.
    Mob mob = TDPanel.getMobHandler().nearestMob(next, 100);
    if (mob != null) {
      for (int i=0; i < fixedOffs.length; i++) {
        newBearing = calcBearing(fixedOffs[i]);
        newPt = nextPoint(prevPosn, newBearing);
        if (TDPanel.getMobHandler().nearestMob(next, 100) != null)
          break;     // one of the fixed offsets will work
      }
    }
    cells[headPosn] = newPt;     // new head position
    currCompass = newBearing;    // new compass direction
  }  // end of newHead()


  private int varyBearing()
  // vary the compass bearing semi-randomly 
  {
    int newOffset = probsForOffset[ (int)( Math.random()*NUM_PROBS )];
    return calcBearing( newOffset );
  }  // end of varyBearing()


  private int calcBearing(int offset)
  // Use the offset to calculate a new compass bearing based
  // on the current compass direction.
  {
    int turn = currCompass + offset;
    // ensure that turn is between N to NW (0 to 7)
    if (turn >= NUM_DIRS)
      turn = turn - NUM_DIRS;
    else if (turn < 0)
      turn = NUM_DIRS + turn;
    return turn;
  }  // end of calcBearing()



  private Point nextPoint(int prevPosn, int bearing)
  /* Return the next coordinate based on the previous position
     and a compass bearing.

     Convert the compass bearing into predetermined increments 
     (stored in incrs[]). Add the increments multiplied by the 
     DOTSIZE to the old head position.
     Deal with wraparound.
  */
  { 
    // get the increments for the compass bearing
    Point2D.Double incr = incrs[bearing];

    int newX = cells[prevPosn].x + (int)(DOTSIZES[upg] * incr.x);
    int newY = cells[prevPosn].y + (int)(DOTSIZES[upg] * incr.y);

    // modify newX/newY if < 0, or > pWidth/pHeight; use wraparound 
    if (newX+DOTSIZES[upg] < 0)     // is circle off the left edge of the canvas?
      newX = newX + pWidth;
    else  if (newX > pWidth)  // is circle off the right edge of the canvas?
      newX = newX - pWidth;  

    if (newY+DOTSIZES[upg] < 0)     // is circle off the top of the canvas?
      newY = newY + pHeight;
    else  if (newY > pHeight) // is circle off the bottom of the canvas?
      newY = newY - pHeight;

    return new Point(newX,newY);
  }  // end of nextPoint()

  
  public void draw(Graphics2D g)
  // draw a black worm with a red head
  {
    if (nPoints > 0) {
      g.setColor(Color.black);
      int i = tailPosn;
      while (i != headPosn) {
    	  AffineTransform at = new AffineTransform();
    	  if (cells[(i+MAXPOINTS[upg]-1)%MAXPOINTS[upg]] != null)  {
    	  at.translate(cells[i].x, cells[i].y);
    	  at.rotate(cells[i].y-cells[(i+MAXPOINTS[upg]-1)%MAXPOINTS[upg]].y,
    			  cells[i].x-cells[(i+MAXPOINTS[upg]-1)%MAXPOINTS[upg]].x);
    	  }
    	  g.drawImage(sword[upg], at, null);
    	  g.fillOval(cells[i].x, cells[i].y, DOTSIZES[upg], DOTSIZES[upg]);
    	  i = (i+1) % MAXPOINTS[upg];
      }
      g.setColor(Color.red);
      g.fillOval( cells[headPosn].x, cells[headPosn].y, DOTSIZES[upg], DOTSIZES[upg]);
    }
  }  // end of draw()

}  // end of Worm class

