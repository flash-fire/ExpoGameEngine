package Main;





import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;


public class TDRunner extends JFrame implements WindowListener
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

private static int DEFAULT_FPS = 80;

  private TDPanel TDP;        // where the worm is drawn
  private JTextField jtfBox;   // displays no.of boxes used
  private JTextField jtfTime;  // displays time spent in game


  public TDRunner(long period)
  { super("Sugar Tower Defense");
    makeGUI(period);
    addWindowListener( this );
    setUndecorated(true);
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice gs = ge.getDefaultScreenDevice();
    gs.setFullScreenWindow(this);
    validate();
    pack();
    setResizable(false);
    setVisible(true);
    
    
    /*
     * Hides that damn curse of a cursor.
     */
    fixCursor();
  } 


  
  public void fixCursor() {
	    Toolkit toolkit = Toolkit.getDefaultToolkit();
	    Point hotSpot = new Point(0,0);
	    BufferedImage cursorImage = new BufferedImage(1, 1, BufferedImage.TRANSLUCENT); 
	    Cursor invisibleCursor = toolkit.createCustomCursor(cursorImage, hotSpot, "InvisibleCursor");        
	    setCursor(invisibleCursor);
  }
  
  

  
  private void makeGUI(long period)
  {
    Container c = getContentPane();    // default BorderLayout used

    TDP = new TDPanel(this, period);
    c.add(TDP, "Center");

    JPanel ctrls = new JPanel();   // a row of textfields
    ctrls.setLayout( new BoxLayout(ctrls, BoxLayout.X_AXIS));

    jtfTime = new JTextField("Time Spent: 0 secs");
    jtfTime.setEditable(false);
    ctrls.add(jtfTime);

    c.add(ctrls, "South");
  }  // end of makeGUI()


  public void setBoxNumber(int no)
  {  jtfBox.setText("Boxes used: " + no);  }

  public void setTimeSpent(long t)
  {  jtfTime.setText("Time Spent: " + t + " secs"); }
  

  // ----------------- window listener methods -------------

  @Override
public void windowActivated(WindowEvent e) 
  { TDP.resumeGame(); 
	  
  }

  @Override
public void windowDeactivated(WindowEvent e) 
  {  TDP.pauseGame(); 
	  
  }


  @Override
public void windowDeiconified(WindowEvent e) 
  {  TDP.resumeGame();  
  }

  @Override
public void windowIconified(WindowEvent e) 
  {  TDP.pauseGame();
}


  @Override
public void windowClosing(WindowEvent e)
  {  TDP.stopGame();  
  }


  @Override
public void windowClosed(WindowEvent e) {}
  @Override
public void windowOpened(WindowEvent e) {}

  // ----------------------------------------------------

  public static void main(String args[])
  { 
    int fps = DEFAULT_FPS;
    if (args.length != 0)
      fps = Integer.parseInt(args[0]);

    long period = (long) 1000.0/fps;
    System.out.println("fps: " + fps + "; period: " + period + " ms");

    new TDRunner(period*1000000L);    // ms --> nanosecs 
  }
    

} // end of WormChase class


