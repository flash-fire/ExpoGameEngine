package Main;

import java.awt.Component;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
 
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
 
import org.w3c.dom.NodeList;
 
import com.sun.imageio.plugins.gif.GIFImageMetadata;
 
public final class Tester {
	public static void main(String[] args) throws IOException {
		JFrame frame = new JFrame("Test");
		frame.setSize(800, 600);
 
		// the gif with 0 delay
		ImageIcon ico = new ImageIcon(readImgFromFile("/home/klaue/Desktop/DancingPeaks.gif", frame));
		
		frame.add(new JLabel(ico));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public static Image readImgFromFile(String sFile, Component drawTarget) {
		if (sFile == null || !new File(sFile).exists()) return null;
		File img = new File(sFile);
		Image awtImg = null;
		
		try {
			if (sFile.substring(sFile.length() - 4).equalsIgnoreCase(".gif")) {
				// fix for bug when delaytime is 0
				ImageReader irImgRead = ImageIO.getImageReadersByFormatName("gif").next();
				irImgRead.setInput(ImageIO.createImageInputStream(img));
				
				// just check first frame
				GIFImageMetadata metadatazero = (GIFImageMetadata)irImgRead.getImageMetadata(0);
				if (metadatazero.delayTime != 0) {
					// buisness as usual
					awtImg = Toolkit.getDefaultToolkit().createImage(img.getAbsolutePath());
				} else {
					// write new image to byte[]
					irImgRead.setInput(ImageIO.createImageInputStream(img));
					ImageWriter iwImgWrit = ImageIO.getImageWriter(irImgRead);
					ByteArrayOutputStream baoStream = new ByteArrayOutputStream();
					ImageOutputStream ios = ImageIO.createImageOutputStream(baoStream);
					iwImgWrit.setOutput(ios);
					iwImgWrit.prepareWriteSequence(null);
					for (int i = 0; i < irImgRead.getNumImages(true); ++i) {
						BufferedImage src = irImgRead.read(i);     
 
						//prepare new metadata
						IIOMetadata metadata = iwImgWrit.getDefaultImageMetadata(new ImageTypeSpecifier(src), null);
						
						// get old metadata
						IIOMetadataNode root = (IIOMetadataNode)irImgRead.getImageMetadata(i).getAsTree("javax_imageio_gif_image_1.0");
						
						// add delay
						NodeList children = root.getChildNodes();
						for (int j = 0; j < children.getLength(); ++j) {
							if (!children.item(j).getNodeName().equals("GraphicControlExtension")) continue;
							IIOMetadataNode gce = (IIOMetadataNode)children.item(j);
							gce.setAttribute("delayTime", "10");
							break;
						}
						
						// set new metadata
						metadata.setFromTree(metadata.getNativeMetadataFormatName(), root);
 
						IIOImage ii = new IIOImage(src, null, metadata);
						iwImgWrit.writeToSequence( ii, iwImgWrit.getDefaultWriteParam());
					}
					iwImgWrit.endWriteSequence();
					ios.close();
					
					awtImg = Toolkit.getDefaultToolkit().createImage(baoStream.toByteArray());
				}
			} else {
				awtImg = Toolkit.getDefaultToolkit().createImage(img.getAbsolutePath());
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		MediaTracker mdr = new MediaTracker(drawTarget);
		mdr.addImage(awtImg, 0);
		try {
			mdr.waitForID(0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return awtImg;
	}
}
