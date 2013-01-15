package simulation.views.entity;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Buffered image used when an problem occurs during the agent rendering
 * @author JPeG
 */
public abstract class BadObjectRenderingBufferedImage {

	/**
	 * return the image to display when an agent rendering occurs
	 * @return the BufferedImage
	 */
	public static BufferedImage getBadAgentRenderingBufferedImage()
	{
	 BufferedImage error = new BufferedImage(100,15,BufferedImage.TYPE_INT_RGB); 
	 Graphics2D grp = (Graphics2D) error.getGraphics();
	 grp.setFont(new Font("Arial",Font.BOLD,8));
	 grp.setColor(Color.RED);
	 grp.drawString("Bad object rendering!!!", 10, 10);
	 return error;
	}
}
