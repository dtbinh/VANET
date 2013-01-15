package simulation.statistics.criteria.custom;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;


import simulation.events.Event;
import simulation.events.system.EnergyModificationEvent;
import simulation.events.system.ReceivedMessageEvent;
import simulation.events.system.SendedMessageEvent;
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
public class CumuledVolumeOfSendedMessagesUserCriteria extends UserCriterion{

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
		return true;
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
		return "Allows to see the cumuled volume of all sended messages in the MAS or by an Agent/Object";
	}

	/**
	 * returns the name of the criteria
	 * @return the name of the criteria
	 */
	public String getName() {
		return "Cumuled volume of sended messages.";
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


		long cumul=0,lastCumul=0;
		long lastDate=0;
	
		DataBlock data = new DataBlock("Cumuled volume of sended messages","Time",DataBlock.TIME_IN_MS,"Cumuled volume",DataBlock.SIMPLE_VALUE_INT);
			data.addPoint(0,0);		
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
			if (id ==-1 || id==evt.getRaiser().getId())
			{
				if (evt instanceof SendedMessageEvent) cumul+=((SendedMessageEvent) evt).getMessage().volume();
			
				if (evt.getDate().getTimeInMillis()-lastDate>this.SLOT_TIME) 
				{
					long dateOfEvent=evt.getDate().getTimeInMillis();
					while((dateOfEvent-lastDate-this.SLOT_TIME)>this.SLOT_TIME){lastDate+=this.SLOT_TIME;data.addPoint(lastDate,lastCumul);}

					lastDate+=this.SLOT_TIME;
					data.addPoint(lastDate,cumul);
					lastCumul=cumul;
				}
		
			}
		}
		if (evt!=null) 
			data.addPoint(evt.getDate().getTimeInMillis(),cumul);
		else 
			data.addPoint(0,0);
			
		eventList.clear();
		return data;
	}

}
