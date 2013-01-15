package simulation.statistics;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ListIterator;

/**
 * abstract data block which must be processed to an evaluation representation
 * @author Jean-Paul Jamont
 */
public abstract class  AbstractDataBlock implements Cloneable,Serializable
{

	// Les différents types de données
	final static public int SIMPLE_VALUE_INT 	= 0;
	final static public int SIMPLE_VALUE_REAL 	= 1;
	final static public int SIMPLE_VALUE_TEXT 	= 2;
	final static public int TIME_IN_MS 			= 3;
	final static public int POURCENTAGE 		= 4;

	// Titre du graph
	protected String title;
	// Libéllé des axes
	protected String labelX;
	protected String labelY;
	// Type des données	
	protected int typeX;
	protected int typeY;
	// Le maximumum rencontré sur chacun des axes (pour le calcul des coefs de dilatation)
	protected double xMax;
	protected double yMax;

	protected double xMin;
	protected double yMin;





	public AbstractDataBlock(String title,String labelX,int typeX,String labelY,int typeY)
	{
		this.title=title;

		this.labelX=labelX;
		this.labelY=labelY;

		this.typeX=typeX;
		this.typeY=typeY;

	}



	public  String getTitle()
	{
		return title;
	}

	public  String getLabelX()
	{
		return labelX;
	}

	public  String getLabelY()
	{
		return labelY;
	}

	public  int getTypeX()
	{
		return typeX;
	}
	public  int getTypeY()
	{
		return typeY;
	}

	public  double getXMax()
	{
		return xMax;
	}
	public  double getXMin()
	{
		return xMin;
	}
	public  double getYMax()
	{
		return yMax;
	}
	public  double getYMin()
	{
		return yMin;
	}

	public   ListIterator<Point2D.Double> listIterator()
	{
		return null;
	}

	public  int getNbPoints()
	{
		return 0;
	}



}
