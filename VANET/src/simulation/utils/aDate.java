package simulation.utils;

import java.util.*;
import java.io.*;

/**
 *  This class add ms manipulation method to a GregorianCalendar
 * @author Jean-Paul Jamont
 */
public class aDate extends GregorianCalendar implements Serializable 
{

	/** default constructor */
	public aDate()
	{
		super();
	}
	/** parametrized constructor
	 * Build a date from its ms representation
	 * @param time_ms the date in ms
	 */
	public aDate(long time_ms)
	{
		super();
		this.setTimeInMillis(time_ms);
	}	


	/** returns the time periode between this date and another one
	 * @param date the other date 
	 * @return  returns a positive value of the difference of the two dates
	 */
	public long differenceToMS(aDate date)
	{
		return Math.abs((this.getTimeInMillis()-date.getTimeInMillis()));
	}

	/** returns a string representation the time difference between two dates 
	 * @see differenceToMS
	 * @param nDate the other date
	 * @return the time difference (HH:MM:SS string)
	 */
	public String differenceToHHMMSS(aDate nDate)
	{
		return msToHHMMSS(differenceToMS(nDate));		
	}


	/** returns a string representation the time difference between two dates 
	 * @see differenceToMS
	 * @param nDate the other date
	 * @return the time difference (HH:MM:SS.CCC string)
	 */
	public String differenceToHHMMSSCCC(aDate nDate)
	{
		return msToHHMMSSCCC(differenceToMS(nDate));		
	}

	/** returns a string representation the time 
	 * @param ms the time in ms 
	 * @return the time (HH:MM:SS string)
	 */
	public static String msToHHMMSS(long ms)
	{

		long dif = ms/1000;

		int i;

		//HH
		i=(int)(dif/3600);
		String h = new String(new Integer(i).toString());
		if (h.length()==1) h = (new String("0")).concat(h);
		dif = dif - i * 3600;

		//MM
		i=(int)(dif/60);
		String m = new String(new Integer(i).toString());
		if (m.length()==1) m = (new String("0")).concat(m);
		dif = dif - i * 60;

		// SS
		String s = new String(new Integer((int)dif).toString());
		if (s.length()==1) s = (new String("0")).concat(s);

		return h.concat(":").concat(m).concat(":").concat(s);
	}


	/** returns a string representation the time 
	 * @param ms the time in ms 
	 * @return the time (HH:MM:SS.CCC string)
	 */
	public static String msToHHMMSSCCC(long ms)
	{

		long dif = ms/1000;
		long difNoMS = dif * 1000;

		int i;

		//HH
		i=(int)(dif/3600);
		String h = new String(new Integer(i).toString());
		if (h.length()==1) h = (new String("0")).concat(h);
		dif = dif - i * 3600;

		//MM
		i=(int)(dif/60);
		String m = new String(new Integer(i).toString());
		if (m.length()==1) m = (new String("0")).concat(m);
		dif = dif - i * 60;

		// SS
		String s = new String(new Integer((int)dif).toString());
		if (s.length()==1) s = (new String("0")).concat(s);

		// CC
		String c = new String(new Integer( (int)(ms-difNoMS) ).toString());
		if (c.length()==1) c = (new String("00")).concat(s);
		if (c.length()==2) c = (new String("0")).concat(s);

		return h.concat(":").concat(m).concat(":").concat(s).concat(".").concat(c);
	}

	/** is the date newer than the parameter
	 * @param date another date
	 * @return true if the parameter if older than the object, false else
	 */
	public boolean beforeThan(aDate date)
	{
		return this.compareTo(date)<0;
	}

	/** is the date older than the parameter
	 * @param date another date
	 * @return true if the date if older than parameter, false else
	 */
	public boolean afterThan(aDate date)
	{
		return this.compareTo(date)>0;
	}
	
	/**
	 * return the current time in millisec
	 * @return current time in ms
	 */
	public static long getCurrentTimeInMS()
	{
		return (new aDate()).getTimeInMillis();
	}

}