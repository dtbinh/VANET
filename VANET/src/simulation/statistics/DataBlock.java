package simulation.statistics;


import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * data block as it is the most often used
 * @author Jean-Paul Jamont
 */

public class DataBlock extends AbstractDataBlock
{

	public Double tempValue;

	private LinkedList<Point2D.Double> dataList;

	public DataBlock(String title,String labelX,int typeX,String labelY,int typeY)
	{
		super(title,labelX,typeX,labelY,typeY);
		this.dataList=new LinkedList<Point2D.Double>();
	}


	public  void addPoint(double x,double y)
	{
		if (this.dataList.size()==0)	
		{
			super.xMax=x;
			super.xMin=x;
			super.yMax=y;
			super.yMin=y;
		}
		else
		{
			super.xMax=Math.max(super.xMax,x);
			super.yMax=Math.max(super.yMax,y);
			super.xMin=Math.min(super.xMin,x);
			super.yMin=Math.min(super.yMin,y);
		}

		this.dataList.add(new Point2D.Double(x,y));
	}

	public   ListIterator<Point2D.Double> listIterator()
	{
		return this.dataList.listIterator();
	}

	public  int getNbPoints()
	{
		return this.dataList.size();
	}


	public  String getTitle()
	{
		return super.title;
	}

	public  String getLabelX()
	{
		return super.labelX;
	}

	public  String getLabelY()
	{
		return super.labelY;
	}

	public  int getTypeX()
	{
		return super.typeX;
	}
	public  int getTypeY()
	{
		return super.typeY;
	}

	public  double getXMax()
	{
		return super.xMax;
	}
	public  double getXMin()
	{
		return super.xMin;
	}
	public  double getYMax()
	{
		return super.yMax;
	}
	public  double getYMin()
	{
		return super.yMin;
	}

}
