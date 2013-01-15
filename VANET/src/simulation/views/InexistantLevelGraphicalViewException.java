package simulation.views;

import java.awt.image.BufferedImage;

public class InexistantLevelGraphicalViewException extends Exception {

	final static public int NOT_DETERMINATED = -99;
	private BufferedImage img=null;
	private int level;
	
	public InexistantLevelGraphicalViewException (BufferedImage img)
	{
		this(InexistantLevelGraphicalViewException.NOT_DETERMINATED,img);
	}
	public InexistantLevelGraphicalViewException (int level,BufferedImage img)
	{
		this.img=img;
		this.level=level;
	}
	
	public BufferedImage getImage()
	{
		return this.img;
	}

	public int getLevel()
	{
		return this.level;
	}
	
	public String toString()
	{
		return "Inexistant graphical view for level "+this.level;
	}
}
