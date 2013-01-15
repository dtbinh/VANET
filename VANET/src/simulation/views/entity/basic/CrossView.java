package simulation.views.entity.basic;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import simulation.utils.ImageToolkit;

public class CrossView extends BasicView {

	public static int DEFAULT_LENGTH = 8;


	private int length;


	public CrossView(String text)
	{
		this(CrossView.DEFAULT_LENGTH,text);
	}
	public CrossView(int length, String text)
	{
		this(BasicView.DEFAULT_BACKGROUND_COLOR,BasicView.DEFAULT_LINE_COLOR,BasicView.DEFAUT_TEXT_COLOR,length,text);
	}
	public CrossView(Color backgroundColor, Color lineColor, Color textColor,int length,String text)
	{
		this(backgroundColor.getRGB(), lineColor.getRGB(), textColor.getRGB(),length,text);
	}
	public CrossView(int backgroundColor, int lineColor, int textColor,int length,String text)
	{
		super(backgroundColor, lineColor, textColor,text);
		this.length=length;
	}

	@Override
	public BufferedImage graphicalView(double zoom) {
		return this.graphicalView(zoom,false);
	}
	public BufferedImage graphicalView(double zoom,boolean noText) {
		BufferedImage res=ImageToolkit.CreateTransparencyBufferedImage(length, length);
		Graphics2D grph = (Graphics2D) res.getGraphics();
		grph.setColor(this.getBackgroundColor());
		grph.drawLine(0, length/2, length, length/2);
		grph.drawLine(length/2, 0, length/2, length);
		if(!noText)
		{
			grph.setFont(new Font("Arial",Font.BOLD,9));
			grph.setColor(this.getTextColor());
			grph.drawString(this.text, 2, length+2);
		}

		return res;
	}


}
