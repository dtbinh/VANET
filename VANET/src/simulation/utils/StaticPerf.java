package simulation.utils;


/**
 * this class is using to debug a solution. It allows to measure easily the time elasped between to "timepoint"
 * @author Jean-Paul Jamont
 */
public class StaticPerf {

	/** the date of the initialization */
	private static aDate date;


	/** Initialize the date to the current date*/
	public static void init()
	{
		//System.out.println("Init perf engine");
		date=new aDate();
	}

	/** Time elapsed since the date initialization (ms)*/
	public static long elapsed()
	{
		return (new aDate()).differenceToMS(date);
	}

	/** return the string representation of the time elaspsed since the initialization */
	public static String elapsedToString()
	{
		return ((new aDate()).differenceToHHMMSSCCC(date));
	}
	
	/** print the time elaspsed since the initialization */
	public static void printElapsed()
	{
		System.out.println(elapsedToString());
	}
}
