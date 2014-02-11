package Main;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
	
public class ImageLoader {
	
	public static BufferedImage loadImage(String fileName) {
		try { 
            File file = new File(fileName);           
            //System.out.println("Is the file there for " + fileName + " : "  + file.exists());
            BufferedImage sub = ImageIO.read(file); 
            return toCompatibleImage(sub);
		}
		catch (IOException e) {	
			System.out.println("Image load failed");
//			e.printStackTrace();
			return null;
		}
	}
	
	
	
	public static BufferedImage loadScaledImage(String fileName, int width, int height, boolean alpha) {
		return createResizedCopy(loadImage(fileName), width, height, alpha);
		
	}
	
	public static BufferedImage toCompatibleImage(BufferedImage image) {
		// System graphics
		GraphicsConfiguration gfx_config = GraphicsEnvironment.getLocalGraphicsEnvironment()
		.getDefaultScreenDevice().getDefaultConfiguration();
		
		if (image.getColorModel().equals(gfx_config.getColorModel()))
			return image;
		
		BufferedImage opt = gfx_config.createCompatibleImage(image.getWidth(), image.getHeight(), image.getTransparency());
		Graphics2D g2d = (Graphics2D) opt.getGraphics();
		g2d.drawImage(image, 0, 0 , null);
		g2d.dispose();
		return opt;	
	}
	
	public static BufferedImage createResizedCopy(BufferedImage originalImage, 
    		int scaledWidth, int scaledHeight, 
    		boolean preserveAlpha)
    {
    	System.out.println("resizing...");
    	int imageType = preserveAlpha ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
    	BufferedImage scaledBI = new BufferedImage(scaledWidth, scaledHeight, imageType);
    	Graphics2D g = scaledBI.createGraphics();
    	if (preserveAlpha) {
    		g.setComposite(AlphaComposite.Src);
    	}
    	g.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null); 
    	g.dispose();
    	return toCompatibleImage(scaledBI);
    }

}
