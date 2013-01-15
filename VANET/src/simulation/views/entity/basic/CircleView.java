package simulation.views.entity.basic;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import simulation.utils.ImageToolkit;
import simulation.utils.StaticPerf;

public class CircleView extends BasicView {

	public static int DEFAULT_RADIUS = 8;

	private int radius;


	public CircleView(String text)
	{
		this(CircleView.DEFAULT_RADIUS,text);
	}
	public CircleView(int radius, String text)
	{
		this(BasicView.DEFAULT_BACKGROUND_COLOR,BasicView.DEFAULT_LINE_COLOR,BasicView.DEFAUT_TEXT_COLOR,radius,text);
	}
	public CircleView(Color backgroundColor, Color lineColor, Color textColor,int radius,String text)
	{
		this(backgroundColor.getRGB(), lineColor.getRGB(), textColor.getRGB(),radius,text);
	}
	public CircleView(int backgroundColor, int lineColor, int textColor,int radius,String text)
	{
		super(backgroundColor, lineColor, textColor,text);
		this.radius=radius;
	}

	@Override
	public BufferedImage graphicalView(double zoom) {
		return this.graphicalView(zoom,false);
	}
	public BufferedImage graphicalView(double zoom,boolean noText) {
		
		BufferedImage res=ImageToolkit.CreateTransparencyBufferedImage(2*this.radius, 2*this.radius);
		Graphics2D grph = (Graphics2D) res.getGraphics();
	
		grph.setColor(this.getBackgroundColor());
		grph.fillOval(0, 0, 2*radius, 2*radius);
		if(!noText)
		{
			grph.setFont(new Font("Arial",Font.BOLD,9));
			grph.setColor(this.getTextColor());
			grph.drawString(this.text, 2, radius+2);
		}


		return res;
	}


	


}
