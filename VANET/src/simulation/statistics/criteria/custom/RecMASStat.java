package simulation.statistics.criteria.custom;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;


import simulation.events.Event;
import simulation.events.system.EnergyModificationEvent;
import simulation.events.system.ObjectCreationEvent;
import simulation.events.system.ReceivedFrameEvent;
import simulation.events.system.SendedFrameEvent;
import simulation.journal.SimulationJournal;
import simulation.solutions.custom.AntMWAC.MWAC.MWACRouteAssistant;
import simulation.solutions.custom.RecMAS.MWAC.MWACAgent;
import simulation.solutions.custom.RecMAS.RecursiveAgent.Messages.RecMASMessage;
import simulation.solutions.custom.RecMAS.RecursiveAgent.Messages.RecMASMessage_ComposeRequest;
import simulation.solutions.custom.RecMAS.RecursiveAgent.Messages.RecMASMessage_ComposeUpdate;
import simulation.solutions.custom.RecMAS.RecursiveAgent.Messages.RecMASMessage_msgExternSystemMessageTransport;
import simulation.solutions.custom.RecMAS.RecursiveAgent.Messages.RecMASMessage_msgInternSystemMessageTransport;
import simulation.statistics.*;
import simulation.statistics.criteria.SystemCriterion;
import simulation.statistics.criteria.UserCriterion;
import simulation.statistics.system.SystemBasicMeasurement;
import simulation.statistics.system.SystemDataPerformanceItem;

/**
 * This class allow to generate statistics about the number of events occured in the simulation
 * @author Jean-Paul Jamont
 */ 
public class RecMASStat extends UserCriterion{

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
		return "Allows to see the cumuled volume of all sended frames in the MAS or by an Agent/Object";
	}

	/**
	 * returns the name of the criteria
	 * @return the name of the criteria
	 */
	public String getName() {
		return "ResMAS stats.";
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

		int populationInitiale=0;
		GroupDescription[][] tabGroupDescription= new GroupDescription[10][900];



		String errorUpdateBeforeRequest="";
		DataBlock data = new DataBlock("Cumuled volume of sended frames","Time",DataBlock.TIME_IN_MS,"Cumuled volume",DataBlock.SIMPLE_VALUE_INT);
		data.addPoint(0,0);
		LinkedList<Event> eventList = journal.extractAllEvents();
		ListIterator<Event> iter =eventList.listIterator();
		Event evt=null;
		while(iter.hasNext())
		{
			evt=iter.next();
			if (id ==-1 || id==evt.getRaiser().getId())
			{

				if (evt instanceof ObjectCreationEvent)  populationInitiale++;

				if (evt instanceof SendedFrameEvent) 
				{
					RecMASMessage recMsg = RecMASMessage.createMessage(((SendedFrameEvent) evt).getFrame().getData());
					if (recMsg!=null ) 
					{

						if (recMsg.getType()==RecMASMessage.msgINTERN_SYSTEM_MESSAGE_TRANSPORT)
							recMsg=((RecMASMessage_msgInternSystemMessageTransport) recMsg).getData();
						else if (recMsg.getType()==RecMASMessage.msgEXTERN_SYSTEM_MESSAGE_TRANSPORT)
							recMsg=((RecMASMessage_msgExternSystemMessageTransport) recMsg).getData();

						if(((SendedFrameEvent) evt).getFrame().getSender()==recMsg.getSender())
						{
							// Others frame are relaying frame
							switch(recMsg.getType())
							{
							case RecMASMessage.msgCOMPOSE_REQUEST: 
							{
								RecMASMessage_ComposeRequest req=(RecMASMessage_ComposeRequest) recMsg;
								tabGroupDescription[req.getLayer()][req.getSender()]=new GroupDescription(evt.getDate().getTimeInMillis(),req);
							}
							break;
							case RecMASMessage.msgCOMPOSE_UPDATE:
							{
								RecMASMessage_ComposeUpdate req=(RecMASMessage_ComposeUpdate) recMsg;
								try
								{
									tabGroupDescription[req.getLayer()][req.getSender()].update(evt.getDate().getTimeInMillis(),req);
								}
								catch(NullPointerException e)
								{
									System.err.println("(id="+req.getSender()+","+"lvl="+req.getLayer()+") has send an update without previous request");
									errorUpdateBeforeRequest+="(id="+req.getSender()+","+"lvl="+req.getLayer()+")"; 
									e.printStackTrace();
								}
							}
							break;
							default:
								;
							}
						}
					}
				}
			}

		}


		eventList.clear();

		String res="Population intiale:"+populationInitiale;

		int numberOfAgent_thisLevel=populationInitiale;
		int numberOfAgent_previousLevel=populationInitiale;
		for(int level=0;level<10;level++)
		{
			numberOfAgent_previousLevel=numberOfAgent_thisLevel;
			numberOfAgent_thisLevel=0;
			for(int i=1;i<900;i++) if (tabGroupDescription[level][i]!=null) numberOfAgent_thisLevel++;

			if(numberOfAgent_thisLevel>0)
			{
				int densite=0;
				int densite2=0;

				long time=0;
				long time2=0;
				res+="\n\nLEVEL "+level;
				res+="\n******************\n";
				res+="\nNumber of agent:"+numberOfAgent_thisLevel;

				long dt=0;
				for(int i=1;i<900;i++) 
				{
					if (tabGroupDescription[level][i]!=null) 
					{
						densite+=tabGroupDescription[level][i].densite();
						densite2+=tabGroupDescription[level][i].densite()*tabGroupDescription[level][i].densite();

						if(tabGroupDescription[level][i].timeBeforeStability()>15000)  
						{
							System.err.println("(id="+i+","+"lvl="+level+") span time is "+dt);
							// the last dt value is not modified :(
						}
						else
						{
							dt=tabGroupDescription[level][i].timeBeforeStability();
						}
						time+=dt;
						time2+=dt*dt;

						if(tabGroupDescription[level][i].densite()<=1) System.err.println("(id="+i+","+"lvl="+level+") density is "+tabGroupDescription[level][i].densite());
						if(dt>15000)  System.err.println("(id="+i+","+"lvl="+level+") span time is "+dt);
					}
				}

				res+="\nPopulation avec fantomes:"+densite;
				res+="\nDensity - average (agents/groups)="+((double)numberOfAgent_previousLevel)/numberOfAgent_thisLevel;
				res+="\nDensity - standart deviation (agents/groups)="+Math.sqrt( (densite2/numberOfAgent_thisLevel) - (densite/numberOfAgent_thisLevel));
				res+="\nTime span of creation - average (second/groups)="+((double)time)/numberOfAgent_thisLevel;
				res+="\nTime span of creation - standart deviation (second/groups)="+Math.sqrt( (time2/numberOfAgent_thisLevel) - (time/numberOfAgent_thisLevel));

			}

		}

		if(!errorUpdateBeforeRequest.isEmpty()) res+="\n\nUpdate received before creation for :"+errorUpdateBeforeRequest;
		System.out.println(res);
		return data;
	}


	private class GroupDescription
	{
		public int[] members;
		public int representant;
		public long dateOfCreation;
		public long dateOfLastModification;
		public byte role;
		public GroupDescription(long dateOfCreation,RecMASMessage_ComposeRequest req)
		{
			this.dateOfCreation=dateOfCreation;
			this.role=MWACAgent.roleNOTHING;
			this.representant=req.getSender();
			this.dateOfLastModification=this.dateOfCreation;
			this.members=new int[1];
			this.members[0]=this.representant;
		}

		public void update(long dateOfModification,RecMASMessage_ComposeUpdate update)
		{
			this.members=update.getAggregatedAgents();
			this.role=update.getRole();
			this.dateOfLastModification=dateOfModification;
		}

		public int timeBeforeStability()
		{
			return (int) (this.dateOfLastModification-this.dateOfCreation);
		}
		public int densite()
		{
			return this.members.length;
		}


	}


}
