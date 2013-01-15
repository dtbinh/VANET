package simulation.statistics.criteria.custom;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

import simulation.events.*;
import simulation.journal.SimulationJournal;
import simulation.statistics.*;
import simulation.statistics.criteria.UserCriterion;

/** allows to know the number of events occured in the simulation
 * journal based version
 * @see NumberOfEvents
 * @author Jean-Paul Jamont
 */
public class NumberOfEventsUserCriteria extends UserCriterion{

	/** slot time used to mean the values */
	private long SLOT_TIME;

	@Override
	public boolean concernsAgent() {
		return true;
	}

	@Override
	public boolean concernsMAS() {
		return true;
	}

	@Override
	public boolean concernsObject() {
		return true;
	}

	@Override
	public AbstractDataBlock getDataBlock(SimulationJournal journal) {
		return getDataBlock(journal,-1);
	}

	@Override
	public AbstractDataBlock getDataBlock(SimulationJournal journal, int id) {

		long i=0,lastCumul=0;
		long lastDate=0;

		DataBlock data = new DataBlock("Number of events (user)","Time",DataBlock.TIME_IN_MS,"Number of events",DataBlock.SIMPLE_VALUE_INT);
		
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
			if (id==-1 || id==evt.getRaiser().getId())
			{
				i++;
				if (evt.getDate().getTimeInMillis()-lastDate>this.SLOT_TIME) 
				{
					long dateOfEvent=evt.getDate().getTimeInMillis();
					while((dateOfEvent-lastDate-this.SLOT_TIME)>this.SLOT_TIME){lastDate+=this.SLOT_TIME;data.addPoint(lastDate,lastCumul);}

					lastDate+=this.SLOT_TIME;
					data.addPoint(lastDate,i);
					lastCumul=i;
				}
		
			}
		}
		if (evt!=null) 
			data.addPoint(evt.getDate().getTimeInMillis(),i);
		else 
			data.addPoint(0,0);
			
		eventList.clear();
		return data;
	}

	@Override
	public String getDescription() {
		return "Allows to see the number of events associated to the MAS/Agent/Object";
	}

	@Override
	public String getName() {
		return "Number of events";
	}

	@Override
	public String getAuthor() {
		return "Jean-Paul Jamont";
	}

}
