package simulation.multiagentSystem;

import simulation.utils.aDate;

/**
 * this class is using to debug a solution. It allows to measure easily the time elasped between to "timepoint"
 * @author Jean-Paul Jamont
 */
public class Perf {

	/** the date of the initialization */
	private aDate date;


	/** Initialize the date to the current date*/
	public Perf()
	{
		date=new aDate();
	}

	/** Initialize the date to the current date*/
	public void init()
	{
		System.out.println("Init perf engine");
		date=new aDate();
	}

	/** Time elapsed since the date initialization*/
	public long elapsed()
	{
		return (new aDate()).differenceToMS(date);
	}

	/** return the string representation of the time elaspsed since the initialization */
	public String elapsedToString()
	{
		return ((new aDate()).differenceToHHMMSSCCC(date));
	}
	
	/** print the time elaspsed since the initialization */
	public void printElapsed()
	{
		System.out.println(elapsedToString());
	}
}
