package simulation.statistics.criteria.custom;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;


import simulation.events.Event;
import simulation.events.system.EnergyModificationEvent;
import simulation.events.system.RangeModificationEvent;
import simulation.journal.SimulationJournal;
import simulation.statistics.*;
import simulation.statistics.criteria.SystemCriterion;
import simulation.statistics.criteria.UserCriterion;
import simulation.statistics.system.SystemBasicMeasurement;
import simulation.statistics.system.SystemDataPerformanceItem;

/**
 * This class allow to generate statistics about the number of events occured in the simulation
 * @author Jean-Paul Jamont
 */ 
public class MeanRangeUserCriteria extends UserCriterion{

	/** The slot time used to resume a subset of measures */
	private long SLOT_TIME;

	/**
	 * is this criteria concerning agents?
	 * @return yes if this criteria can be applied to an agent
	 */
	public boolean concernsAgent() {
		return true;
	}

	/**
	 * is this criteria concerning the whole system?
	 * @return yes if this criteria can be applied to the whole mas
	 */
	public boolean concernsMAS() {
		return false;
	}

	/**
	 * is this criteria concerning objects?
	 * @return yes if this criteria can be applied to an object
	 */
	public boolean concernsObject() {
		return true;
	}


	/**
	 * returns the description of the criteria
	 * @return the criteria descriptor
	 */
	public String getDescription() {
		return "Allow to range of Agents/Object or the mean range in the MAS";
	}

	/**
	 * returns the name of the criteria
	 * @return the name of the criteria
	 */
	public String getName() {
		return "Mean range.";
	}

	/**
	 * returns the author name
	 * @return the name of the author(s)
	 */
	public String getAuthor() {
		return "Jean-Paul Jamont";
	}


	@Override
	public AbstractDataBlock getDataBlock(SimulationJournal journal) {
		return getDataBlock(journal,-1);
	}

	@Override
	public AbstractDataBlock getDataBlock(SimulationJournal journal, int id) {

		long i=0;
		double cumul=0;
		long lastDate=0;
		float last=0;

		DataBlock data = new DataBlock("Mean range","Time",DataBlock.TIME_IN_MS,"Range",DataBlock.SIMPLE_VALUE_INT);

		SLOT_TIME = journal.getLastEventDate().getTimeInMillis()/1000;
		if (SLOT_TIME==0) SLOT_TIME=1;
		//System.out.println("LAST EVENT:"+journal.getLastEventDate().getTimeInMillis()+"    SLOT TIME:"+SLOT_TIME);
		LinkedList<Event> eventList = journal.extractAllEvents();
		//System.out.println("ON DONNE A NOEuser "+eventList.size()+" events");
		ListIterator<Event> iter =eventList.listIterator();
		Event evt=null;
		while(iter.hasNext())
		{
			evt=iter.next();
			if (id==evt.getRaiser().getId())
			{
				if (evt instanceof RangeModificationEvent)
				{
					cumul+=(last = ((RangeModificationEvent) evt).getRange());
					i++;
				}
			
				if (evt.getDate().getTimeInMillis()-lastDate>this.SLOT_TIME) 
				{
					lastDate+=this.SLOT_TIME;
					if (i>0) data.addPoint(lastDate,cumul/i);
					cumul=0;
					i=0;
				}
		
			}
		}
		if (evt!=null) 
			data.addPoint(evt.getDate().getTimeInMillis(),last);
		else 
			data.addPoint(0,0);
			
		eventList.clear();
		return data;
	}

}
