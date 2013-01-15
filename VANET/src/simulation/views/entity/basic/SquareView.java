package simulation.views.entity.basic;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import simulation.utils.ImageToolkit;

public class SquareView extends BasicView {

	public static int DEFAULT_SIDE = 8;


	private int side;


	public SquareView(String text)
	{
		this(SquareView.DEFAULT_SIDE,text);
	}
	public SquareView(int side, String text)
	{
		this(BasicView.DEFAULT_BACKGROUND_COLOR,BasicView.DEFAULT_LINE_COLOR,BasicView.DEFAUT_TEXT_COLOR,side,text);
	}
	public SquareView(Color backgroundColor, Color lineColor, Color textColor,int side,String text)
	{
		this(backgroundColor.getRGB(), lineColor.getRGB(), textColor.getRGB(),side,text);
	}
	public SquareView(int backgroundColor, int lineColor, int textColor,int side,String text)
	{
		super(backgroundColor, lineColor, textColor,text);
		this.side=side;
	}

	@Override
	public BufferedImage graphicalView(double zoom) {
		return this.graphicalView(zoom,false);
	}
	public BufferedImage graphicalView(double zoom,boolean noText) {
		BufferedImage res=ImageToolkit.CreateTransparencyBufferedImage(this.side, this.side);
		Graphics2D grph = (Graphics2D) res.getGraphics();
		grph.setColor(this.getBackgroundColor());
		grph.fillRect(0, 0,side, side);
		if(!noText)
		{
			grph.setFont(new Font("Arial",Font.BOLD,9));
			grph.setColor(this.getTextColor());
			grph.drawString(this.text, 2, side/2);
		}
		return res;
	}


}
