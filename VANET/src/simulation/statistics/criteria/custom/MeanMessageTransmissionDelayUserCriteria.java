package simulation.statistics.criteria.custom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map.Entry;


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
public class MeanMessageTransmissionDelayUserCriteria extends UserCriterion{

	/** The slot time used to resume a subset of measures */
	private long SLOT_TIME;
	/** to process calculation of the mean message transmission delay */
	HashMap<Integer,MeanMessageTransmissionDelayMeasure> transmissionDelayMeasures;

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
		return "Allows to see the mean consumed time to deliver a message";
	}

	/**
	 * returns the name of the criteria
	 * @return the name of the criteria
	 */
	public String getName() {
		return "Mean message transmission delay";
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


		DataBlock data = new DataBlock("Mean message transmission delay","Time",DataBlock.TIME_IN_MS,"Mean time of transmission",DataBlock.SIMPLE_VALUE_REAL);

		SLOT_TIME = journal.getLastEventDate().getTimeInMillis()/1000;
		if (SLOT_TIME==0) SLOT_TIME=1;
		//System.out.println("LAST EVENT:"+journal.getLastEventDate().getTimeInMillis()+"    SLOT TIME:"+SLOT_TIME);
		LinkedList<Event> eventList = journal.extractAllEvents();
		//System.out.println("ON DONNE A NOEuser "+eventList.size()+" events");

		this.transmissionDelayMeasures = new HashMap<Integer,MeanMessageTransmissionDelayMeasure>();

		MeanMessageTransmissionDelayMeasure measure;

		ListIterator<Event> iter =eventList.listIterator();
		Event evt=null;

		// Statistic for the whole MAS
		if(id==-1)
		{
			double meanDelay=0,lastMeanDelay=0;
			long numberOfProcessedRoute=0;

			// Iteration on all event...
			while(iter.hasNext())
			{
				evt=iter.next();

				// but we are only interested by sendedMessageEvent and ReceivedMessageEvent
				if (evt instanceof SendedMessageEvent)
					this.getMeanMessageTransmissionDelayMeasure(evt.getRaiser().getId()).sendedMessage((SendedMessageEvent) evt);
				else if (evt instanceof ReceivedMessageEvent)
					this.getMeanMessageTransmissionDelayMeasure(((ReceivedMessageEvent) evt).getMessage().getSender()).receivedMessage((ReceivedMessageEvent) evt);

				// A slot time is elapsed
				if (evt.getDate().getTimeInMillis()-lastDate>this.SLOT_TIME) 
				{
					long dateOfEvent=evt.getDate().getTimeInMillis();
					while((dateOfEvent-lastDate-this.SLOT_TIME)>this.SLOT_TIME){lastDate+=this.SLOT_TIME;data.addPoint(lastDate,lastMeanDelay);}

					Iterator<Entry<Integer, MeanMessageTransmissionDelayMeasure>> iterMap = this.transmissionDelayMeasures.entrySet().iterator();
					Entry<Integer, MeanMessageTransmissionDelayMeasure> entry;

					meanDelay=0;
					numberOfProcessedRoute=0;

					// Compute the mean time in the whole system from the individual calculated mean times
					while(iterMap.hasNext())
					{
						entry = iterMap.next();
						if(numberOfProcessedRoute==0)
						{
							meanDelay=entry.getValue().meanDelay;
							numberOfProcessedRoute=entry.getValue().numberOfProcessedMessage;
						}
						else
						{
							meanDelay=(meanDelay*numberOfProcessedRoute)+(entry.getValue().meanDelay*entry.getValue().numberOfProcessedMessage);
							numberOfProcessedRoute+=entry.getValue().numberOfProcessedMessage;
							meanDelay/=numberOfProcessedRoute;
						}
					}
					lastDate+=this.SLOT_TIME;
					data.addPoint(lastDate,meanDelay);
					lastMeanDelay=meanDelay;
				}


			}
			// For non complete slot time
			if (evt!=null) 
			{
				Iterator<Entry<Integer, MeanMessageTransmissionDelayMeasure>> iterMap = this.transmissionDelayMeasures.entrySet().iterator();
				Entry<Integer, MeanMessageTransmissionDelayMeasure> entry;

				meanDelay=0;
				numberOfProcessedRoute=0;

				while(iterMap.hasNext())
				{
					entry = iterMap.next();
					if(numberOfProcessedRoute==0)
					{
						meanDelay=entry.getValue().meanDelay;
						numberOfProcessedRoute=entry.getValue().numberOfProcessedMessage;

					}
					else
					{
						meanDelay=(meanDelay*numberOfProcessedRoute)+(entry.getValue().meanDelay*entry.getValue().numberOfProcessedMessage);
						numberOfProcessedRoute+=entry.getValue().numberOfProcessedMessage;
						meanDelay/=numberOfProcessedRoute;
					}
				}
				lastDate+=this.SLOT_TIME;
				data.addPoint(lastDate,meanDelay);
				lastMeanDelay=meanDelay;
			}
			else 
				data.addPoint(0,lastMeanDelay);
		}
		// Statistic for a specified agent :  We are interested by the mean time for a specified agent/object
		else
		{
			double previousMeanDelay=0;


			while(iter.hasNext())
			{
				evt=iter.next();

				// Is the event interesting for our calculation
				if ((evt instanceof SendedMessageEvent && evt.getRaiser().getId()==id) || (evt instanceof ReceivedMessageEvent && (((ReceivedMessageEvent) evt).getMessage().getSender()==id)))
				{
					if (evt instanceof SendedMessageEvent)
						this.getMeanMessageTransmissionDelayMeasure(id).sendedMessage((SendedMessageEvent) evt);
					else if (evt instanceof ReceivedMessageEvent)
						this.getMeanMessageTransmissionDelayMeasure(id).receivedMessage((ReceivedMessageEvent) evt);


					if (evt.getDate().getTimeInMillis()-lastDate>this.SLOT_TIME) 
					{
						long dateOfEvent=evt.getDate().getTimeInMillis();
						while((dateOfEvent-lastDate-this.SLOT_TIME)>this.SLOT_TIME){lastDate+=this.SLOT_TIME;data.addPoint(lastDate,previousMeanDelay);}

						lastDate+=this.SLOT_TIME;
						data.addPoint(lastDate,this.getMeanMessageTransmissionDelayMeasure(id).meanDelay);
						previousMeanDelay=this.getMeanMessageTransmissionDelayMeasure(id).meanDelay;
					}

				}
			}
			if (evt!=null) 
				data.addPoint(evt.getDate().getTimeInMillis(),this.getMeanMessageTransmissionDelayMeasure(id).meanDelay);
			else 
				data.addPoint(0,previousMeanDelay);
		}
		eventList.clear();
		return data;
	}


	private MeanMessageTransmissionDelayMeasure getMeanMessageTransmissionDelayMeasure(int identifier)
	{
		if (transmissionDelayMeasures.containsKey(identifier))
			return transmissionDelayMeasures.get(identifier);
		else
		{
			MeanMessageTransmissionDelayMeasure measure=new MeanMessageTransmissionDelayMeasure();
			transmissionDelayMeasures.put(identifier,measure);
			return measure;
		}

	}


	/**
	 * Class to compute the calculation of each mean message transmission delay
	 * @author JPeG
	 *
	 */
	private class MeanMessageTransmissionDelayMeasure
	{
		// Maximum acceptable delay to transmit a message
		public static final int MAX_DELAY_BETWEEN_SENDED_AND_RECEIVED_MESSAGE = 60*1000;

		// mean delay
		public double meanDelay;
		// number of message involved in the calculation of the mean delay
		public int numberOfProcessedMessage;
		// list of waiting messages (messages not yet received)
		public LinkedList<SendedMessageEvent> waitingMessages;

		public MeanMessageTransmissionDelayMeasure()
		{
			this.meanDelay=0.0;
			this.numberOfProcessedMessage=0;
			this.waitingMessages=new LinkedList<SendedMessageEvent>();
		}

		public void sendedMessage(SendedMessageEvent event)
		{
			this.waitingMessages.add(event);
		}

		public void receivedMessage(ReceivedMessageEvent event)
		{
			SendedMessageEvent item;
			if (this.waitingMessages.isEmpty())
				System.out.println("ERREUR ANORMALE DANS LE CALCUL D'UN TEMPS DE TRANSMISSION");
			else
			{
				boolean processed = false;

				ListIterator<SendedMessageEvent> iter = this.waitingMessages.listIterator();
				while(!processed && iter.hasNext())
				{
					item=iter.next();
					if( 
							(item.getMessage().getReceiver()==event.getRaiser().getId())
							&& (item.getRaiser().getId()==event.getMessage().getSender())
							&& (event.getMessage().isSameMessageThan(item.getMessage()))
					)
					{
						// ATTENTION!!! Il y a des approximations.
						// SI un agent envoie un meme message n fois de suite sans attente entre chaque message et qu'un d'eux est perdu ca peut poser problème!
						long delay = event.getDateInMs()-item.getDateInMs();

						if (delay<MAX_DELAY_BETWEEN_SENDED_AND_RECEIVED_MESSAGE)
						{
							iter.remove();
							if(this.numberOfProcessedMessage==0)
							{
								this.meanDelay=delay;
								this.numberOfProcessedMessage++;
							}
							else
							{
								this.meanDelay=((this.meanDelay*this.numberOfProcessedMessage)+delay)/(1+this.numberOfProcessedMessage);
								this.numberOfProcessedMessage++;
							}
							processed=true;
						}
						else
							iter.remove();
					} 
				}
			}
		}


	}



}
