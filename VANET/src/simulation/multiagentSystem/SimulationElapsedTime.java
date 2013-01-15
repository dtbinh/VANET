package simulation.multiagentSystem;



import java.util.Iterator;
import java.util.LinkedList;

import simulation.utils.aDate;

/**
 * this class allow to manage the time in the simulator (to take into account the simulation suspended laps of time)
 * @author Jean-Paul Jamont
 */
public class SimulationElapsedTime {

	/** date where the simulation has been started */
	private aDate beginning;
	/** date of the begin of the current pause (if the simulation is suspended)*/
	private aDate beginOfPause;
	/** cumuled laps of time lost suspended mode */
	private long timeSpendedInPause;

	/** Permet de mémoriser les dates de pauses/reprises. Util quand on demande de connaitre le temps de simulation écoulé à partir d'une heure d'horloge réélle*/
	private LinkedList<aDate> pauseDate; 

	/** basic constructor*/
	public SimulationElapsedTime()
	{
		this.beginning=new aDate();
		this.beginOfPause=null;
		this.timeSpendedInPause=0;
		this.pauseDate=new LinkedList<aDate>();
	}

	/** enters in a suspended mode - time lost in this mode will not been take into account*/
	public void suspend()
	{
		this.beginOfPause=new aDate();
		this.pauseDate.add(this.beginOfPause);
	}

	/** leaves from a suspended mode - now the elapsed time will be take into account */
	public void resume()
	{
		aDate date = new aDate();
		this.timeSpendedInPause+=beginOfPause.differenceToMS(date);
		this.pauseDate.add(date);
		this.beginOfPause=null;
	}

	/** similar to the suspend method... but the simulation is finished */
	public void stop()
	{
		this.suspend();
	}

	/** time elapsed since the start of simulation (without take into account time lost in suspended mode)
	 * @return the elapsed time in running mode
	 */
	public long elapsedTime()
	{
		aDate date = new aDate();
		if (this.beginOfPause==null)
			return beginning.differenceToMS(date) - this.timeSpendedInPause;
		else
			return beginning.differenceToMS(date) - this.timeSpendedInPause - beginOfPause.differenceToMS(date);
	}

	/** time elapsed from the start of simulation to the specified date (without take into account time lost in suspended mode)
	 * @param date the date which is the end of the inspected period
	 * @return the elapsed time in running mode from the start of simulation to the specified date
	 */
	public long elapsedTime(aDate date)
	{
		boolean finish = false;
		long spendedTime = beginning.differenceToMS(date);

		aDate date1=null;
		aDate date2=null;

		Iterator<aDate> iter = this.pauseDate.iterator(); 
		while(!finish && iter.hasNext())
		{
			if (date1==null) 
				date1=iter.next();
			else
				date2=iter.next();

			if ( (date1!=null) && (date2!=null))
			{
				//if(date1.compareTo(date)<0)  // date1 est une attente plus vieille que date

				if(date.beforeThan(date1)) 
					finish=true;
				else
				{
					if(!finish && date.beforeThan(date2)) date2=date;

					spendedTime-=date1.differenceToMS(date2);
					date1=null;		date2=null;
				}
			}
		}

		if(date1!=null && date.afterThan(date1))
		{
			spendedTime-=date1.differenceToMS(date);
		}

		return spendedTime;
	}

	/** time elapsed during the specified period (without take into account time lost in suspended mode)
	 * @param start start of the inspected period
	 * @param end end of the inspected period
	 * @return the elapsed time in running mode from the specified start to the specified end
	 */
	public long elapsedTime(aDate start, aDate end)
	{
		return this.elapsedTime(end)-this.elapsedTime(start);
	}

}
