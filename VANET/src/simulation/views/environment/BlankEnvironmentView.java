package simulation.views.environment;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import simulation.utils.IntegerPosition;

public class BlankEnvironmentView implements EnvironmentViewInterface{

	private Color backgroundColor;
	private boolean hasBeenUpdated;
	private int height;
	private int width;
	
	public BlankEnvironmentView(int width, int height)
	{
		this(width,height ,Color.WHITE);
	}
	public BlankEnvironmentView(int width, int height,Color backgroundColor)
	{
		this.backgroundColor=backgroundColor;
		this.hasBeenUpdated=true;
		this.height=height;
		this.width=width;
	}	
	public int getHeight() {
		return this.height;
	}
	@Override
	public int getWidth() {
		// TODO Auto-generated method stub
		return this.width;
	}
	 public boolean hasBeenUpdated()
	 {
		 return this.hasBeenUpdated;
	 }
	 public void setUpdatedAttribute(boolean updated)
	 {
		 this.hasBeenUpdated=updated;
	 }
	 
	public BufferedImage graphicalView(IntegerPosition origine, int width, int height, double zoom) {

		BufferedImage view = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
		Graphics2D graph= (Graphics2D) view.getGraphics();
		graph.setColor(this.backgroundColor);
		graph.fillRect(0, 0, width-1, height-1);
		return view;
	}

}
