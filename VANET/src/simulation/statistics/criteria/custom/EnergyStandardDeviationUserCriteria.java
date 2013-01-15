package simulation.statistics.criteria.custom;


import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map.Entry;


import simulation.events.Event;
import simulation.events.system.EnergyModificationEvent;
import simulation.journal.SimulationJournal;
import simulation.statistics.*;
import simulation.statistics.criteria.UserCriterion;

/**
 * This class allow to generate statistics about Energy standard Deviation
 * @author Yacine LATTAB && Ouerdia SLAOUTI
 */ 
public class EnergyStandardDeviationUserCriteria extends UserCriterion{

	/** The slot time used to resume a subset of measures */
	private long SLOT_TIME;

	/**
	 * is this criteria concerning agents?
	 * @return yes if this criteria can be applied to an agent
	 */
	public boolean concernsAgent() {
		return false;
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
		return "Allows to see the energy standard deviation in the MAS";
	}

	/**
	 * returns the name of the criteria
	 * @return the name of the criteria
	 */
	public String getName() {
		return "Energy standard deviation";
	}

	/**
	 * returns the author name
	 * @return the name of the author(s)
	 */
	public String getAuthor() {
		return "Yacine LATTAB && Ouerdia SLAOUTI";
	}

	@Override
	public AbstractDataBlock getDataBlock(SimulationJournal journal) {
		return getDataBlock(journal,-1);
	}

	@Override
	public AbstractDataBlock getDataBlock(SimulationJournal journal, int id) {


		long lastDate=0;


		DataBlock data = new DataBlock("Energy standard deviation","Time",DataBlock.TIME_IN_MS,"Energy standard deviation",DataBlock.SIMPLE_VALUE_REAL);

		SLOT_TIME = journal.getLastEventDate().getTimeInMillis()/1000;
		if (SLOT_TIME==0) SLOT_TIME=1;
		//System.out.println("LAST EVENT:"+journal.getLastEventDate().getTimeInMillis()+"    SLOT TIME:"+SLOT_TIME);
		LinkedList<Event> eventList = journal.extractAllEvents();
		//System.out.println("ON DONNE A NOEuser "+eventList.size()+" events");

		HashMap<Integer,Double> lastAmount = new HashMap<Integer,Double>();

		ListIterator<Event> iter =eventList.listIterator();
		Event evt=null;

		double cumul, lastEnergyStandardDeviation=0;
		double moyenne, variance, squareType;
		int nbr_agent = 0;
		
		if (id==-1)
		{
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
					while ((dateOfEvent-lastDate-this.SLOT_TIME)>this.SLOT_TIME ) {lastDate+=this.SLOT_TIME; data.addPoint(lastDate,lastEnergyStandardDeviation);}

					Iterator<Entry<Integer, Double>> iterMap = lastAmount.entrySet().iterator();
					Iterator<Entry<Integer, Double>> iterMap_2 = lastAmount.entrySet().iterator();
					Entry<Integer, Double> entry;

					cumul=0;																	//total energy calculus
					nbr_agent = 0;

					while(iterMap.hasNext())
					{
						entry = iterMap.next();
						cumul+=entry.getValue();
						nbr_agent ++; 															//numbers of agents calculus 
					}
					
					moyenne = cumul / nbr_agent;    											//energy average calculus
					variance = 0;
					
					while(iterMap_2.hasNext())  												//type square calculus
					{
						entry = iterMap_2.next();
						variance += Math.pow((moyenne - entry.getValue()), 2);
					}	
					
					squareType = Math.sqrt((float) (((int)(((variance/nbr_agent) )+ (float) 0.5))));   //square type calculus ( * 100 )
					
					
					lastDate+=this.SLOT_TIME;
					data.addPoint(lastDate, squareType);
					System.out.println("->>>"+squareType);
					
					lastEnergyStandardDeviation = squareType;
				}

			}
			if (evt!=null) 
			{
				Iterator<Entry<Integer, Double>> iterMap = lastAmount.entrySet().iterator();
				Iterator<Entry<Integer, Double>> iterMap_2 = lastAmount.entrySet().iterator();
				Entry<Integer, Double> entry;

				cumul=0;																	//total energy calculus
				nbr_agent = 0;

				while(iterMap.hasNext())
				{
					entry = iterMap.next();
					cumul+=entry.getValue();
					nbr_agent ++; 															//numbers of agents calculus 
				}
				
				moyenne = cumul / nbr_agent;    											//energy average calculus
				variance = 0;
									
				while(iterMap_2.hasNext())  												//type squart calculus
				{
					entry = iterMap_2.next();
					variance += Math.pow((moyenne - entry.getValue()), 2);
				}	
				
				squareType = Math.sqrt(((int) (((variance/nbr_agent) )+0.5f)) );   //square type calculus ( *1000 )
				
				
				lastDate+=this.SLOT_TIME;
				data.addPoint(lastDate, squareType);
			}
			else 
				data.addPoint(0,0);
		}
		
		eventList.clear();
		return data;
	}

}
