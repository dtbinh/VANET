package simulation.views.entity.basic;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import simulation.utils.Dimension;
import simulation.utils.ImageToolkit;

public class RectangleView extends BasicView {

	public final static int DEFAULT_WIDTH = 8;
	public final static int DEFAULT_HEIGTH = 8;

	private int height;
	private int width;

	public RectangleView(String text)
	{
		this(RectangleView.DEFAULT_WIDTH,RectangleView.DEFAULT_HEIGTH,text);
	}
	public RectangleView(Dimension d, String text)
	{
		this(BasicView.DEFAULT_BACKGROUND_COLOR,BasicView.DEFAULT_LINE_COLOR,BasicView.DEFAUT_TEXT_COLOR,d.x,d.y,text);
	}
	public RectangleView(int width,int height, String text)
	{
		this(BasicView.DEFAULT_BACKGROUND_COLOR,BasicView.DEFAULT_LINE_COLOR,BasicView.DEFAUT_TEXT_COLOR,width,height,text);
	}
	public RectangleView(Color backgroundColor, Color lineColor, Color textColor,Dimension d,String text)
	{
		this(backgroundColor.getRGB(), lineColor.getRGB(), textColor.getRGB(),d.x,d.y,text);		
	}
	public RectangleView(Color backgroundColor, Color lineColor, Color textColor,int width,int height,String text)
	{
		this(backgroundColor.getRGB(), lineColor.getRGB(), textColor.getRGB(),width,height,text);
	}
	public RectangleView(int backgroundColor, int lineColor, int textColor,int width,int height,String text)
	{
		super(backgroundColor, lineColor, textColor,text);
		this.width=width;
		this.height=height;
	}

	@Override
	public BufferedImage graphicalView(double zoom) {
		return this.graphicalView(zoom,false);
	}
	public BufferedImage graphicalView(double zoom,boolean noText) {
		BufferedImage res=ImageToolkit.CreateTransparencyBufferedImage(this.width, this.height);
		Graphics2D grph = (Graphics2D) res.getGraphics();
		grph.setColor(this.getBackgroundColor());
		grph.fillRect(0, 0, this.width, this.height);
		if(!noText)
		{
			grph.setFont(new Font("Arial",Font.BOLD,9));
			grph.setColor(this.getTextColor());
			grph.drawString(this.text, 2, this.height/2);
		}

		return res;
	}


}
