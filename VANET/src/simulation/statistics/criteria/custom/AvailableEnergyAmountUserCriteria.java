package simulation.statistics.criteria.custom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map.Entry;


import simulation.events.Event;
import simulation.events.system.EnergyModificationEvent;
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
public class AvailableEnergyAmountUserCriteria extends UserCriterion{

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
		return "Allows to see the amount of available energy  in the Agent/Object";
	}

	/**
	 * returns the name of the criteria
	 * @return the name of the criteria
	 */
	public String getName() {
		return "Available agent/object energy";
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



		long lastDate=0;


		DataBlock data = new DataBlock("Available energy","Time",DataBlock.TIME_IN_MS,"Amount of energy",DataBlock.SIMPLE_VALUE_INT);

		SLOT_TIME = journal.getLastEventDate().getTimeInMillis()/1000;
		if (SLOT_TIME==0) SLOT_TIME=1;
		//System.out.println("LAST EVENT:"+journal.getLastEventDate().getTimeInMillis()+"    SLOT TIME:"+SLOT_TIME);
		LinkedList<Event> eventList = journal.extractAllEvents();
		//System.out.println("ON DONNE A NOEuser "+eventList.size()+" events");

		HashMap<Integer,Double> lastAmount = new HashMap<Integer,Double>();

		ListIterator<Event> iter =eventList.listIterator();
		Event evt=null;

		if (id==-1)
		{
			double cumul=0,lastCumul=0;

			// We are interested by the amount of energy in the whole system
			while(iter.hasNext())
			{
				evt=iter.next();

				if (evt instanceof EnergyModificationEvent)
				{
					lastAmount.put(evt.getRaiser().getId(),((EnergyModificationEvent) evt).getActualEnergyAmount());
				}

				if (evt.getDate().getTimeInMillis()-lastDate>this.SLOT_TIME) 
				{
					long dateOfEvent=evt.getDate().getTimeInMillis();
					while((dateOfEvent-lastDate-this.SLOT_TIME)>this.SLOT_TIME){lastDate+=this.SLOT_TIME;data.addPoint(lastDate,lastCumul);}

					Iterator<Entry<Integer, Double>> iterMap = lastAmount.entrySet().iterator();
					Entry<Integer, Double> entry;

					cumul=0;

					while(iterMap.hasNext())
					{
						entry = iterMap.next();
						cumul+=entry.getValue();
					}
					lastDate+=this.SLOT_TIME;
					data.addPoint(lastDate,cumul);
					lastCumul=cumul;
				}


			}
			if (evt!=null) 
			{
				Iterator<Entry<Integer, Double>> iterMap = lastAmount.entrySet().iterator();
				Entry<Integer, Double> entry;

				cumul=0;

				while(iterMap.hasNext())
				{
					entry = iterMap.next();
					cumul+=entry.getValue();
				}
				lastDate+=this.SLOT_TIME;
				data.addPoint(lastDate,cumul);
				lastCumul=cumul;
			}
			else 
				data.addPoint(0,0);
		}
		else
		{
			double last=0,previousLast=0;

			// We are interested by the amount of energy in a specified agent/object
			while(iter.hasNext())
			{
				evt=iter.next();

				if (id==evt.getRaiser().getId())
				{
					if (evt instanceof EnergyModificationEvent) last = ((EnergyModificationEvent) evt).getActualEnergyAmount();


					if (evt.getDate().getTimeInMillis()-lastDate>this.SLOT_TIME) 
					{
						long dateOfEvent=evt.getDate().getTimeInMillis();
						while((dateOfEvent-lastDate-this.SLOT_TIME)>this.SLOT_TIME){lastDate+=this.SLOT_TIME;data.addPoint(lastDate,previousLast);}

						lastDate+=this.SLOT_TIME;
						data.addPoint(lastDate,last);
						previousLast=last;
					}

				}
			}
			if (evt!=null) 
				data.addPoint(evt.getDate().getTimeInMillis(),last);
			else 
				data.addPoint(0,0);
		}
		eventList.clear();
		return data;
	}

}
