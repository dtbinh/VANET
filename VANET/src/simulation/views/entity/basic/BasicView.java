package simulation.views.entity.basic;

import java.awt.Color;

import simulation.views.entity.EntityViewInterface;

public abstract class BasicView implements EntityViewInterface{

	public final static int DEFAULT_BACKGROUND_COLOR = Color.WHITE.getRGB();
	public final static int DEFAULT_LINE_COLOR = Color.LIGHT_GRAY.getRGB();
	public final static int DEFAUT_TEXT_COLOR = Color.DARK_GRAY.getRGB();

	private int backgroundColor;
	private int lineColor;
	private int textColor;

	protected boolean updated;

	protected String text;

	public BasicView()
	{
		this("");
	}
	public BasicView(String text)
	{
		this(BasicView.DEFAULT_BACKGROUND_COLOR,BasicView.DEFAULT_LINE_COLOR,BasicView.DEFAUT_TEXT_COLOR,text);
	}

	public BasicView(Color backgroundColor, Color lineColor, Color textColor,String text)
	{
		this(backgroundColor.getRGB(),lineColor.getRGB(),textColor.getRGB(),text);
	}

	public BasicView(int backgroundColor, int lineColor, int textColor,String text)
	{
		this.backgroundColor=backgroundColor;
		this.lineColor=lineColor;
		this.textColor=textColor;
		this.text=text;
		this.updated=true;
	}

	public String getText()
	{
		return this.text;
	}
	public Color getBackgroundColor()
	{
		return new Color(this.backgroundColor);
	}
	public Color getLineColor()
	{
		return new Color(this.lineColor);
	}
	public Color getTextColor()
	{
		return new Color(this.textColor);
	}


	public void setBackgroundColor(int background)
	{
		this.backgroundColor=background;
	}
	public void setLineColor(int line)
	{
		this.lineColor=line;
	}
	public void setTextColor(int text)
	{
		this.textColor=text;
	}
	public void setBackgroundColor(Color background)
	{
		this.setBackgroundColor(background.getRGB());
	}
	public void setLineColor(Color line)
	{
		this.setLineColor(line.getRGB());
	}
	public void setTextColor(Color text)
	{
		this.setTextColor(text.getRGB());
	}
	public void setText(String text)
	{
		this.text=text;
	}
	public boolean hasBeenUpdated()
	{
		return this.updated;
	}

}
