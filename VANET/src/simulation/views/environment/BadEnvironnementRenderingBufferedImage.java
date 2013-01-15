package simulation.views.environment;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Buffered image used when an problem occurs during the environment rendering
 * @author JPeG
 */public abstract class BadEnvironnementRenderingBufferedImage {


	 /**
	  * return the image to display when an agent rendering occurs
	  * @return the BufferedImage
	  */
	 public static BufferedImage getBadEnvironnementRenderingBufferedImage()
	 {
		 BufferedImage error = new BufferedImage(150,15,BufferedImage.TYPE_INT_RGB); 
		 Graphics2D grp = (Graphics2D) error.getGraphics();
		 grp.setFont(new Font("Arial",Font.BOLD,10));
		 grp.setColor(Color.RED);
		 grp.drawString("Bad environment rendering!!!", 10, 10);
		 return error;
	 }
 }
