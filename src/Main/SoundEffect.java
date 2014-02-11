package Main;
import java.io.*;
import javax.sound.sampled.*;

import Interfaces.Event;
   
/**
 * 
 * I used http://www3.ntu.edu.sg/home/ehchua/programming/java/J8c_PlayingSound.html for ENUM code.
 * I did some editing to encapsulate my needs.  For example, I loaded several clips at one time.
 *  Just so you know the code source.  Once I saw the ENUM idea... I couldn't resist!
 * This enum encapsulates all the sound effects of a game, so as to separate the sound playing
 * codes from the game codes.
 * 1. Define all your sound effect names and the associated wave file.
 * 2. To play a specific sound, simply invoke SoundEffect.SOUND_NAME.play().
 * 3. You might optionally invoke the static method SoundEffect.init() to pre-load all the
 *    sound files, so that the play is not paused while loading the file for the first time.
 * 4. You can use the static variable SoundEffect.volume to mute the sound.
 */

// Cassie Goodnight
public enum SoundEffect {
   BASICSHOT("resources/sounds/bullets/basic0.wav", 3),   // basic gun fire
   RAYGUN("resources/sounds/bullets/raygun.wav", 3),      // raygun
   BOOM1("resources/sounds/bullets/bomb1.wav", 5),       // explosion 1
   BOOM2("resources/sounds/bullets/bomb2.wav", 5),		  // explosion 2
   CHEWBACCA("resources/sounds/bullets/raygun2.wav", 3),
   BOUNCE("resources/sounds/bullets/pipe.wav", 5),
   FASTSHOT("resources/sounds/bullets/click.wav", 3),
   AIRPORT("resources/sounds/bullets/airport.wav", 5),
   SWEEP("resources/sounds/bullets/sweep2.wav", 5),
   SPLASH("resources/sounds/bullets/raydeath.wav", 15);
   
   // Nested class for specifying volume
   public static enum Volume {
      MUTE, LOW, MEDIUM, HIGH, EARDRUM_KILLER, TINNITUS_HEAVEN;
      
   }
   
   // array of background music string file names.
   static final String[] background = new String[] {/*"resources/sounds/background/Dream.wav",  */
	   "resources/sounds/background/Enigma.wav"};
   
   
   public static Volume volume = Volume.LOW; // default volume.
   public static boolean isOverride = false;
   public Volume vol; // overriden volume
   
   // Each sound effect has its own clip, loaded with its own sound file.
   private Clip[] clips;
   private int soundLoc; // records what sound in the pool the player is on.  The player cycles through the pool.  If sound gets cut early, too bad. I don't really care. It's better than just one copy, and I gotta save on memory! 
   
   // Constructor to construct each element of the enum with its own sound file.
   SoundEffect(String soundFileName, int poolSize) {
      try {
    	  File file = new File(soundFileName);
         // Get a clip resource.
         clips = new Clip[poolSize];
         for (int i = 0; i < clips.length;i++) {
	         clips[i] = AudioSystem.getClip();
	         // Open audio clip and load samples from the audio input stream.
		     AudioInputStream ais = AudioSystem.getAudioInputStream(file);
		     clips[i].open(ais);
         }
      } catch (UnsupportedAudioFileException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      } catch (LineUnavailableException e) {
         e.printStackTrace();
      }
      
      vol = Volume.HIGH;
   }
   /*
    * Sets volume of the individual sound.
    * To do this, call something like AIRPORT.setVol(Volume.HIGH);
    */
   public void setVol(Volume vol) {
	   this.vol = vol;
   }
   
   /* sets volume for all sounds as vol. */
   public static void setAllSound(Volume vol) {
	   isOverride = true;
	   volume = vol;
   }

   /*
    * Returns volumes back to previous state.
    */
   public static void unsetAllSound() {
	   isOverride = false;
   }
   private int getGain() {
	   Volume usedVol;
	   if (isOverride)
		   usedVol = volume;
	   else
		   usedVol = vol;
	   switch (usedVol) {
	   case MUTE:
		   return 0;
	   case LOW:
		   return -15;
	   case MEDIUM:
		   return -10;
	   case HIGH:
		   return -5;
	   case EARDRUM_KILLER:
		   return 10;
	   case TINNITUS_HEAVEN:
		   return 25;
       default:
    	   return 0;
	   }
   }   
   
   
   // Plays first background song concurrently
   public static void concBackground() {
	   concBackground(background[0]);
   }
   
   // plays background music concurrently.
   public static void concBackground(final String filename) {
	   Runnable run = new Runnable() {
		    @Override
			public void run() {
		    	background(filename);
		    }
		 };
		 new Thread(run).start();
   }
   public static void background(String filename) {
	
	   int total, totalToRead, numBytesRead, numBytesToRead;
       byte[] buffer;
       boolean         stopped;
       SourceDataLine  lineIn;
       FileInputStream fis = null;
       
       AudioFormat wav = new AudioFormat(22500, 16, 2, true, false);
       DataLine.Info info = new DataLine.Info(SourceDataLine.class, wav);


       buffer = new byte[1024*332];
       numBytesToRead = 1024*332;
       total=0;
       stopped = false;

       if (!AudioSystem.isLineSupported(info)) {
           System.out.print("no support for " + wav.toString() );
       }
       try {
    	   fis = new FileInputStream(new File(filename));
           // Obtain and open the line.
           lineIn = (SourceDataLine) AudioSystem.getLine(info);
           lineIn.open(wav);
           lineIn.start();
           

           totalToRead = fis.available();
        	 FloatControl gainControl = 
      		    (FloatControl) lineIn.getControl(FloatControl.Type.MASTER_GAIN);
        	 gainControl.setValue(SoundEffect.BOOM1.getGain());
           while (total < totalToRead && !stopped){
               numBytesRead = fis.read(buffer, 0, numBytesToRead);
               if (numBytesRead == -1) { 
            	   break;
               }
               total += numBytesRead;
               
               lineIn.write(buffer, 0, numBytesRead);
           }

       } catch (Exception e) {
    	   e.printStackTrace();
       }
       finally {
    	   try {
			fis.close();
		} catch (IOException e) {
			// 
			e.printStackTrace();
		}
    	   TDPanel.level.add(new BackgroundEnd(0, background[0]));
       }
   }
   
   // Play or Re-play the sound effect from the beginning, by rewinding.
   public void play() {
	      if (volume != Volume.MUTE) {
	    	 FloatControl gainControl = 
	    		    (FloatControl) clips[soundLoc].getControl(FloatControl.Type.MASTER_GAIN);
	    	gainControl.setValue(getGain());
	         if (clips[soundLoc].isRunning())
	            clips[soundLoc].stop();   // Stop the player if it is still running
	         clips[soundLoc].setFramePosition(0); // rewind to the beginning
	         clips[soundLoc].start();     // Start playing
	         soundLoc = (soundLoc+1) % clips.length; // iterates clip so that the sound is unlikely to reset clip in play.
	      }
   }
   
   // Optional static method to pre-load all the sound files.
   static void init() {
      values(); // calls the constructor for all the elements
   }
}

class BackgroundEnd extends Event {
	String next;
	public BackgroundEnd(int startTime, String next) {
		super(startTime);
		this.next = next;
	}
	

	@Override
	public void run() {
		System.out.println("lol");
		SoundEffect.concBackground(next);
	}
}