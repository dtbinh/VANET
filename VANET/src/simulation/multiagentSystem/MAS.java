package simulation.multiagentSystem;




import simulation.views.InexistantLevelGraphicalViewException;
import simulation.views.IHM.IHMViewParametersInterface;
import simulation.views.entity.basic.AgentViewEnumType;
import simulation.views.entity.basic.BasicView;
import simulation.views.entity.basic.CircleView;
import simulation.views.entity.basic.CrossView;
import simulation.views.entity.basic.RectangleView;
import simulation.views.entity.basic.SquareView;
import simulation.views.environment.EnvironmentViewInterface;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import javax.imageio.ImageIO;

import simulation.embeddedObject.EmbeddedObjectsManager;
import simulation.embeddedObject.serialCommunication.FrameReceivedInterface;
import simulation.entities.Agent;
import simulation.entities.Object;
import simulation.environment.AttributeNotFoundException;
import simulation.environment.Environment;
import simulation.events.Event;
import simulation.events.EventMapNotificationInterface;
import simulation.events.EventNotificationInterface;
import simulation.events.system.ColorModificationEvent;
import simulation.events.system.EnergyModificationEvent;
import simulation.events.system.PositionModificationEvent;
import simulation.events.system.RangeModificationEvent;
import simulation.events.system.ReceivedBytesByEmbeddedObjectEvent;
import simulation.events.system.ReceivedFrameEvent;
import simulation.events.system.ReceivedMessageEvent;
import simulation.events.system.RoleModificationEvent;
import simulation.events.system.SendedBytesByEmbeddedObjectEvent;
import simulation.events.system.SendedFrameEvent;
import simulation.events.system.SendedMessageEvent;
import simulation.events.system.SystemExceptionEvent;
import simulation.journal.SimulationJournal;
import simulation.messages.Frame;
import simulation.messages.Message;
import simulation.messages.system.NotSystemFrameException;
import simulation.messages.system.NotUnderstantableSystemFrameException;
import simulation.messages.system.SystemFrame;
import simulation.messages.system.custom.AgentInformationReplySystemFrame;
import simulation.messages.system.custom.AgentInformationRequestSystemFrame;
import simulation.messages.system.custom.PerceivedAgentsInformationReplySystemFrame;
import simulation.messages.system.custom.PerceivedAgentsInformationRequestSystemFrame;
import simulation.multiagentSystem.ObjectSystemIdentifier;
import simulation.solutions.custom.MWAC.Messages.MWACFrame;
import simulation.solutions.custom.MWAC.Messages.MWACMessage;
import simulation.solutions.custom.RecMAS.RecursiveAgent.Messages.RecMASMessage;
import simulation.statistics.StatisticManager;
import simulation.statistics.system.SystemBasicMeasurement;
import simulation.statistics.system.SystemDataPerformanceItem;
import simulation.utils.BytesArray;
import simulation.utils.IntegerPosition;
import simulation.utils.StaticPerf;
import simulation.utils.aDate;

/**
 * According the Vowels decomposition [Demazeau95]: A=Agent, E=Environment, I=Interaction, O=Organization, U=User
 *
 * Here the multiagent system (MAS) is seen as a collection of agent (A) and objects of the environment (E).
 * All aspects of multiagent systems (I,O,U) are implemented in the agents.
 * @param <wMain>
 * @author Jean-Paul Jamont
 */
public class MAS implements EventNotificationInterface,FrameReceivedInterface{

	public static boolean NOTIFY_FRAME_RECEIVED_EVENT = true;
	public final static int ACTIVE_LEVEL = -1;


	/** to comment */
	private final static int DELAY_BETWEEN_TWO_EVENT_LIST_UPDATE = 1000;
	/** when the simulation is stoped, this delay is elapsed to signal to all thread that time is over */ 
	private final static int DELAY_TO_KILL_AGENT_THREAD = 3500;


	/** List of agents and objects (of the MAS environment)*/
	private ObjectListManager itemList;
	/** Is the simulation running? */
	private boolean isInSimulation;
	/** The group of agent's thread (allow to start/suspend/resume/kill all agents)*/
	private AgentThreadGroup agentThreadGroup;
	/** To manage the simulation time (know elapsed time since the begin of simulation etc.)*/
	private SimulationElapsedTime simulationElapsedTime;
	/** Table of spyied agent (optimization)*/
	private ObjectSystemIdentifierProtectedHashSet spyiedObjectsIdentifiantSet;
	/** Reference to the main software object */
	private EventMapNotificationInterface mainSoftware;
	/** Event list manager used to notify only a subset of event to the HMI*/
	private EventsListToProcessManager eventsListManager;
	/** System basic performances measurement */
	private SystemBasicMeasurement measurementSystem;
	/** journal of the whole simulation */
	private SimulationJournal journal;
	/** user defined statistic manager */
	private StatisticManager userDefinedStatisticManager;
	/** embedded agents/objects manager */
	private EmbeddedObjectsManager embeddedObjectsManager;
	/** environment model */
	private Environment environment;
	/** default agent view */
	private DefaultAgentView defaultAgentView;
	/** base frame to construct frame from the received bytes. Useful only for real world embedded agent */
	private Class baseFrame;

	
	
	
	/** Initializes a newly created MAS object (empty list, no agent thread group, no running simulation)
	 * main a reference to the object which must receive a copy of the notification
	 * statisticManager a reference to the statistic manager
	 * performancesFolder the folder where must be stored the performances
	 */
	public MAS(EventMapNotificationInterface main, Environment environment, EmbeddedObjectsManager embeddedObjectsManager,StatisticManager statisticManager,String performancesFolder, Class baseFrame)
	{
		this.itemList = new ObjectListManager();
		this.isInSimulation=false;
		this.simulationElapsedTime=null;
		this.agentThreadGroup = new AgentThreadGroup();
		this.spyiedObjectsIdentifiantSet = new ObjectSystemIdentifierProtectedHashSet();
		this.journal = new SimulationJournal(performancesFolder);
		this.measurementSystem = new SystemBasicMeasurement(this);
		this.embeddedObjectsManager=embeddedObjectsManager;

		this.userDefinedStatisticManager=statisticManager;
		this.userDefinedStatisticManager.setJournal(this.journal);
		this.userDefinedStatisticManager.setSystemBasicMeasurement(this.measurementSystem);

		this.mainSoftware=main;

		this.eventsListManager=null;

		this.environment=environment;

		this.defaultAgentView=new DefaultAgentView();

		this.baseFrame=baseFrame;
	}


	/**
	 * getter to the statistic manager
	 * @return a reference to the statistic manager
	 */
	public StatisticManager getStatisticManager()
	{
		return this.userDefinedStatisticManager;
	}

	/**
	 * getter to a simulated object
	 * @param id object identifier
	 * @return a reference to the specified simulated object
	 */
	public SimulatedObject getSimulatedObject(ObjectSystemIdentifier sys_id)
	{
		try
		{
		return this.itemList.get(sys_id).getSimulatedObject();
		}
		catch(NullPointerException e)
		{
			System.err.println("\nTentative d'accès à l'agent "+sys_id);
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * getter to a simulated object
	 * @param id object identifier
	 * @return a reference to the specified simulated object
	 */
	public SimulatedObject getSimulatedObject(IntegerPosition p)
	{
		ObjectAndItsNeighboorhood res = this.itemList.get(p,5);
		if (res==null) 
			return null;
		else
			return res.getSimulatedObject();
	}
	public SimulatedObject getSimulatedObject(IntegerPosition p,int radius)
	{
		return this.itemList.get(p,radius).getSimulatedObject();
	}

	/**
	 * allows to know is an object is spyied 
	 * @param id object identifier
	 * @return true if the object is spyied, else no
	 */
	public boolean isSpyied(ObjectSystemIdentifier id)
	{
		return spyiedObjectsIdentifiantSet.contains(id);
	}

	/** Allows to add an item (agent or object) in the multiagent system
	 * @param item A simulated object or agent to add in the multiagent system
	 * @see SimulatedObject 
	 */
	public synchronized void addNewItem(SimulatedObject newObject)
	{
		ObjectSystemIdentifier id = newObject.getObject().getSystemId();

		HashSet<ObjectSystemIdentifier> newSet = new HashSet<ObjectSystemIdentifier>();

		java.lang.Object[] objectsTab = this.itemList.getClonedList();


		SimulatedObject obj;	
		ObjectAndItsNeighboorhood oan;
		for(int i=0;i<objectsTab.length;i++)
		{
			oan = (ObjectAndItsNeighboorhood) objectsTab[i];

			if (oan!=null && newObject.inRange(oan.getSimulatedObject()))
			{
				newSet.add(oan.getSimulatedObject().getObject().getSystemId());
				oan.getOriginalNeighboorhood().add(id);
			}
		}


		this.itemList.add(newObject,newSet);
		if(!this.embeddedObjectsManager.isManaged(id)) 
		{
			System.out.println("Creation du thread "+id);
			((SimulatedAgent)newObject).createThread(this.agentThreadGroup);
		}
		else
		{
			System.out.println("Creation de l'avatar d'agent réél "+id);
		}
	}

	/** Allows to know the number of items defined in the multiagent system
	 * @return Number of item (agents and objects)
	 */
	public int nbItem()
	{
		return this.itemList.size();
	}

	/** Delete all item from the multiagent system.
	 * Post-condition: le multiagent is empty
	 */
	public void removeAllItem()
	{
		this.itemList.clear();
	}

	/** Create an instance of the multiagent system (all the agent'thread are created).
	 * Post-condition: The multiagent is in a running state.
	 */
	public void startSimulation()
	{

		ObjectSystemIdentifier.init();

		this.agentThreadGroup=new AgentThreadGroup();

		if (this.eventsListManager==null) this.eventsListManager=new EventsListToProcessManager();

		java.lang.Object[] objectsTab = this.itemList.getClonedList();

		// Agent iterator
		SimulatedObject item;
		for(int i=0;i<objectsTab.length;i++)
		{				
			item=((ObjectAndItsNeighboorhood)objectsTab[i]).getSimulatedObject();
			if (item!=null && item.isAgent())	
			{
				if(!this.embeddedObjectsManager.isManaged(item.getObject().getSystemId())) 
				{
					System.out.println("Creation du thread "+item.getObject().getUserId()+" (#"+item.getObject().getSystemId()+")");
					((SimulatedAgent)item).createThread(this.agentThreadGroup);
				}
				else
				{
					System.out.println("Creation de l'avatar de l'agent réél "+item.getObject().getUserId()+" (#"+item.getObject().getSystemId()+")");
				}					
			}
		}

		// On démarre la simulation
		this.agentThreadGroup.start();

		this.isInSimulation=true;
		this.simulationElapsedTime=new SimulationElapsedTime();

	}

	/** returns the time elapsed since the beging of simulation. Time spended in suspend mode are not take into account. 
	 * @return the elasped time*/
	public long elapsedSimulationTime()
	{
		if (this.simulationElapsedTime==null)
			return 0;
		else
			return this.simulationElapsedTime.elapsedTime();
	}

	/** returns the time elapsed since the beging of simulation to the argument. Time spended in suspend mode are not take into account. 
	 * @return the elasped time*/
	public long elapsedSimulationTime(aDate date)
	{
		if (this.simulationElapsedTime==null)
			return 0;
		else
			return this.simulationElapsedTime.elapsedTime(date);
	}


	/** Stops the simulation (all instances of agents and objects are killed).*/
	public void stopSimulation()
	{
		if (agentThreadGroup!=null) this.agentThreadGroup.stop();
		try{Thread.sleep(DELAY_TO_KILL_AGENT_THREAD);}catch(Exception e){};
		if (simulationElapsedTime!=null) this.simulationElapsedTime.stop();
		this.isInSimulation=false;
		try{Thread.sleep(2*Agent.SLEEP_TIME_SLOT);}catch(Exception e){}
		if (journal!=null) this.journal.flushInFile();
		this.itemList.clear(); 
		this.spyiedObjectsIdentifiantSet.clear();
		this.eventsListManager.clear();
		this.measurementSystem.stop();
		this.environment.stop();
	}

	/** Suspends the simulation 
	 * Pré condition : The multiagent system simulation is in running state
	 * Post condition: Multiagent system simulation in suspended state 
	 */
	public void suspendSimulation()
	{

		this.agentThreadGroup.suspend();
		this.simulationElapsedTime.suspend();
		this.journal.flushInFile();
		this.measurementSystem.pause();
		this.environment.pause();
		System.out.println("IL Y A EU "+this.journal.nbEvents());
	}

	/** Resume the simulation
	 * Pré condition : The multiagent system simulation is in a suspended state
	 * Post condition: Multiagent system simulation in running state 
	 */
	public void resumeSimulation()
	{
		this.environment.resume();
		this.agentThreadGroup.resume();
		this.simulationElapsedTime.resume();
		this.measurementSystem.resume();

	}

	/** Allows to know is the multiagent system simulation is in a suspended state 
	 * @return True if the multiagent system simulation is in a suspended state, false 
	 */
	public boolean suspendedSimulation()
	{
		return this.agentThreadGroup.isSuspended();
	}

	/** Allows to know is the multiagent system simulation is in a suspended state 
	 * @return True if the multiagent is started (it can be suspended).
	 */
	public boolean startedSimulation()
	{
		return this.isInSimulation;
	}	

	/** Allows to know the number of object in the MAS
	 * @return the number of objects	 
	 * */ 
	public  int nbObjects()
	{
		return this.itemList.size();
	}

	/** Allows to know the number of object in the MAS
	 * @return the number of objects	 
	 * */ 
	public  ObjectSystemIdentifier int_to_ObjectSystemIdentifier(int int_sys_id)
	{
		return this.itemList.getObjectSystemIdentifier(int_sys_id);
	}

	/**
	 * returns the index of a specified ObjectSystemIdentifier
	 * @param sys_id the object system identifier
	 * @return index in the item list of the object
	 */
	public int getIndexOfAnObjectSystemIdentifier(ObjectSystemIdentifier sys_id)
	{
		return this.itemList.getIndexOfAnObjectSystemIdentifier(sys_id);
	}


	/** Allows to simulate the frame propagation (PHY/MAC layers).
	 * @param senderIdentifier the logical identifier (simulator identifier) of the sender to find others agents/object in range.  
	 * @param m the message which must contain the sender/receiver adresses (can be replaced by simulation identifiers)
	 */ 
	public synchronized void dispatchFrame(ObjectSystemIdentifier senderIdentifier,Frame frame) throws java.lang.IndexOutOfBoundsException
	{

		ObjectAndItsNeighboorhood sender = this.itemList.get(senderIdentifier);

		if(frame==null)
		{
			System.err.println("\nOn ne dispatche pas de trames = null)");
			return;
		}

		if(sender!=null)
		{
			int iterationCounter=0;
			Iterator<ObjectSystemIdentifier> iter = sender.getClonedNeighboorhood().iterator();

			ObjectAndItsNeighboorhood item=null;

			if(this.embeddedObjectsManager.isEmpty())
			{
				System.out.print(".");

				try
				{
					while (iter.hasNext())
					{
						item=this.itemList.get(iter.next());
						if (item.getSimulatedObject().inRange(sender.getSimulatedObject())) 
							if (!sender.equals(item.getSimulatedObject().getObject().getSystemId()))
							{
								item.getSimulatedObject().getObject().receivedFrame((Frame) frame.clone());
								iterationCounter++;
							}
					}
				}
				catch(java.lang.StackOverflowError e)
				{
					System.err.println("\nSTACK OVERFLOW in ag#"+senderIdentifier+" during dispatching to ag#"+(item!=null ? item.getSimulatedObject().getObject().getSystemId() : "null")+" frame "+frame+"/"+RecMASMessage.createMessage(frame.getData()));
					e.printStackTrace();
				}
			}
			else
			{
				Frame frm;
				while (iter.hasNext())
				{
					item=this.itemList.get(iter.next());
					frm=(Frame) frame.clone();
					if (item.getSimulatedObject().inRange(sender.getSimulatedObject()))
					{
						//System.out.println("\n"+item.getSimulatedObject().getObject().getSystemId()+" est dans la portée de "+sender.getSimulatedObject().getObject().getSystemId());

						if (this.embeddedObjectsManager.isManaged(item.getSimulatedObject().getObject().getSystemId()))
						{
							//System.out.println("Envoi d'une trame de "+sender+" a l'agent réél "+item.getSimulatedObject().getObject().getUserId());

							//						Perf.init();
							this.embeddedObjectsManager.sendFrame(item.getSimulatedObject().getObject().getSystemId(),item.getSimulatedObject().getObject().getSystemId(),frm);
							//						Perf.printElapsed();

						}
						else
						{
							//System.out.println("\n"+item.getSimulatedObject().getObject().getSystemId()+" n'est pas EMBARQUE");

							item.getSimulatedObject().getObject().receivedFrame(frm);
						}
					}
					else
					{
						//System.out.println("\n"+item.getSimulatedObject().getObject().getSystemId()+" N'est PAS dans la portée de "+sender.getSimulatedObject().getObject().getSystemId());

					}
				}

			}
		}
		else
		{
			// Sender not find 
			this.notifyEvent(new SystemExceptionEvent(senderIdentifier,"This agent don't exists"));
		}
	}

	/** Returns acquointances to the simulation link representation 
	 * @return a string with the agents representation*/ 
	public synchronized ObjectListManager acquointances()
	{
		return (ObjectListManager) this.itemList.clone();
	}


	/** 
	 * update the physical neighbor list of a specified object
	 * @param identifier id of the object
	 */
	public void updatePhysicalNeighborList(ObjectSystemIdentifier sys_id)
	{
		ObjectSystemIdentifier id;
		HashSet<ObjectSystemIdentifier> newNeighboorhoodSet = new HashSet<ObjectSystemIdentifier>();
		ObjectAndItsNeighboorhood movedObject = this.itemList.get(sys_id);
		HashSet<ObjectSystemIdentifier> oldNeighboorhoodSet;
		try
		{
			oldNeighboorhoodSet = (HashSet<ObjectSystemIdentifier>) movedObject.getClonedNeighboorhood();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			oldNeighboorhoodSet = new HashSet<ObjectSystemIdentifier>();
		}

		Iterator<ObjectSystemIdentifier> iterOldNeighboorhood = oldNeighboorhoodSet.iterator();

		// On supprime l'agent déplacé de tous les ensembles
		while(iterOldNeighboorhood.hasNext())
		{
			id=iterOldNeighboorhood.next();
			this.itemList.get(id).getOriginalNeighboorhood().remove(sys_id);
		}


		java.lang.Object[] objectsTab= this.itemList.getClonedList();

		ObjectAndItsNeighboorhood OaN;
		for(int i=0;i<objectsTab.length;i++)
		{
			OaN = (ObjectAndItsNeighboorhood) objectsTab[i];
			if (movedObject.getSimulatedObject().inRange(OaN.getSimulatedObject()))
			{
				newNeighboorhoodSet.add(OaN.getSimulatedObject().getObject().getSystemId());
				OaN.getOriginalNeighboorhood().add(sys_id);
			}
		}


		newNeighboorhoodSet.remove(sys_id);
		movedObject.setNeighboorhood(newNeighboorhoodSet);

	}

	/** Allows to know the time elapsed since the 1st event addition
	 * @return the age of the event list
	 */
	public long eventListAge()
	{
		return this.eventsListManager.age();
	}


	/**
	 * getter to the event journal
	 * @return a reference to the event journal
	 */
	public SimulationJournal getJournal()
	{
		return this.journal;
	}

	/** Allows to process the event list (this list is sended to the simulator graphical representation */
	public void processEventList()
	{
		this.notifyEvent(null);
	}

	/** notify an event and manage the current event list
	 * @param evt an event
	 */
	public synchronized void notifyEvent(Event evt)
	{

		if (evt!=null)
		{
			evt.alignDate(this.elapsedSimulationTime(evt.getDate()));

			ObjectSystemIdentifier raiser = evt.getRaiser();
			boolean toWMain = this.isSpyied(raiser);
			boolean isInstanceOfSendedBytesByEmbeddedObjectEvent =(evt instanceof SendedBytesByEmbeddedObjectEvent);
			boolean isInstanceOfPositionModificationEvent=(evt instanceof PositionModificationEvent);
			boolean isSystemExceptionEvent=(evt instanceof SystemExceptionEvent);

			//System.out.println("ON VA METTRE A JOUR LA POSITION DE "+evt.getRaiser());
			this.journal.add(evt);
			if (isInstanceOfPositionModificationEvent) this.updatePhysicalNeighborList(raiser);

			// Get the data performance of the object
			SystemDataPerformanceItem item=null;
			// if it is an instance of ReceivedBytesByEmbeddedObjectEvent the bytes can be not understandable. If it is the case, we are not sure to read really an sender idenfitier 
			if( !isSystemExceptionEvent && (!isInstanceOfSendedBytesByEmbeddedObjectEvent) || (isInstanceOfSendedBytesByEmbeddedObjectEvent && (((SendedBytesByEmbeddedObjectEvent) evt).isUnderstandable())) )
			{
				item=measurementSystem.get(raiser);
				item.nbEvent++;
			}

			if (evt instanceof ReceivedFrameEvent)
			{
				item.nbReceivedFrame++;
				item.volumeReceivedFrame+=((ReceivedFrameEvent) evt).getFrame().getVolume();
			}
			else   if (evt instanceof SendedFrameEvent)
			{
				item.nbSendedFrame++;
				item.volumeSendedFrame+=((SendedFrameEvent) evt).getFrame().getVolume();
			}
			else   if (evt instanceof ReceivedMessageEvent)
			{
				item.nbReceivedMessage++;
				item.volumeReceivedMessage+=((ReceivedMessageEvent) evt).getMessage().volume();

			}
			else   if (evt instanceof SendedMessageEvent)
			{
				item.nbSendedMessage++;
				item.volumeSendedMessage+=((SendedMessageEvent) evt).getMessage().volume();
			}
			else   if (evt instanceof RoleModificationEvent)
				item.role=((RoleModificationEvent)evt).getRole();
			else   if (evt instanceof EnergyModificationEvent)
			{
				toWMain=true;
				item.energy=((EnergyModificationEvent)evt).getPourcentOfAvailableEnergy();
			}
			else   if (!toWMain && evt instanceof ColorModificationEvent)
				toWMain=true;
			else   if (isInstanceOfPositionModificationEvent)
			{
				item.xPosition=((PositionModificationEvent)evt).getNewPosition().x;
				item.yPosition=((PositionModificationEvent)evt).getNewPosition().y;
				toWMain=true;
			}
			//else   if (evt instanceof NeighboorhoodModificationEvent)
			//else   if (evt instanceof UserDefinedEvent)
			else  if (evt instanceof RangeModificationEvent)
				item.range=((RangeModificationEvent)evt).getRange();
			else if (isInstanceOfSendedBytesByEmbeddedObjectEvent)
				toWMain=true;
			else if (evt instanceof ReceivedBytesByEmbeddedObjectEvent)
				toWMain=true;

			if (toWMain) this.eventsListManager.add(raiser,evt);
		}
		else
		{

			if (this.eventsListManager!=null && this.eventsListManager.getNbEvents()>0)
			{
				mainSoftware.notifyEvent(this.eventsListManager.get());			
				this.eventsListManager.clear();
			}
		}

	}

	/** Allows to knows the identifier of spied objects 
	 * @return the vector of spied objects identifier
	 */
	public Vector<Object> spyiedObjectsState()
	{
		Vector<Object> spyiedObjects;
		ObjectSystemIdentifier item;

		HashSet<ObjectSystemIdentifier> set = this.spyiedObjectsIdentifiantSet.cloneHashSet();

		spyiedObjects= new Vector<Object>(set.size());

		Iterator<ObjectSystemIdentifier> iter= set.iterator();
		while(iter.hasNext())
		{
			item=iter.next();
			spyiedObjects.add(this.itemList.get(item).getSimulatedObject().getObject());
		}

		return spyiedObjects;
	}


	/** update agent spyied list
	 * @param  newSet the set of int identifiers of spyied objects
	 */
	public synchronized void updateSpyiedObjectIdentifiersSet(HashSet<Integer> newSet)
	{
		Iterator<Integer> iter = newSet.iterator();
		HashSet<ObjectSystemIdentifier> newSetOfObjectSystemIdentifier = new HashSet<ObjectSystemIdentifier>();
		while(iter.hasNext()) newSetOfObjectSystemIdentifier.add(this.itemList.getObjectSystemIdentifier(iter.next()));
		this.updateSpyiedObjectSystemIdentifiersSet(newSetOfObjectSystemIdentifier);
	}

	/** update agent spyied list
	 * @param  newSet the set of system identifiers of spyied objects
	 */
	public synchronized void updateSpyiedObjectSystemIdentifiersSet(HashSet<ObjectSystemIdentifier> newSet)
	{
		System.out.println("On va actualiser l'affichage...");
		ObjectSystemIdentifierProtectedHashSet tmpSet = new ObjectSystemIdentifierProtectedHashSet(newSet);
		tmpSet.remove(this.spyiedObjectsIdentifiantSet.getHashSet());

		HashSet<ObjectSystemIdentifier> setOfNewObject = tmpSet.getHashSet();

		// Add the new identifiers
		ObjectSystemIdentifier item;
		Iterator<ObjectSystemIdentifier> iter= setOfNewObject.iterator();
		while(iter.hasNext())
		{
			item=iter.next();
			this.itemList.get(item).getSimulatedObject().getObject().spyied(true);
			this.spyiedObjectsIdentifiantSet.add(item);
		}

		// Remove spy attribute on others
		tmpSet = new ObjectSystemIdentifierProtectedHashSet(this.spyiedObjectsIdentifiantSet.cloneHashSet());
		tmpSet.remove(newSet);

		// Remove old identifiers
		iter=tmpSet.getHashSet().iterator();
		while(iter.hasNext())
		{
			item=iter.next();
			this.itemList.get(item).getSimulatedObject().getObject().spyied(false);
			this.spyiedObjectsIdentifiantSet.remove(item);
		}
	}





	/** This class allows to manage a concurrent HashSet  */
	private class ObjectSystemIdentifierProtectedHashSet
	{
		/** the set of integer */
		private HashSet<ObjectSystemIdentifier> hashSet;
		/** the hash set is under processing */
		private boolean locked;

		/** Constructs an empty hash set of Integer */
		public ObjectSystemIdentifierProtectedHashSet()
		{
			this.hashSet = new HashSet<ObjectSystemIdentifier>();
			this.locked=false;
		}

		public void clear() {
			this.hashSet.clear();

		}

		/** Constructs a hash set of Integer from the given set 
		 * @param set the set of existing integer*/
		public ObjectSystemIdentifierProtectedHashSet(HashSet<ObjectSystemIdentifier> set)
		{
			this();
			this.add(set);
		}

		/** acquires the hash set */
		private synchronized void acquire()
		{
			while (this.locked);
			this.locked=true;
		}

		/** release the hash set */
		private synchronized void release()
		{
			this.locked=false;
		}


		/** adds an integer in the hash set of Integer
		 * @param i the added integer*/
		public void add(ObjectSystemIdentifier i)
		{
			this.acquire();
			this.hashSet.add(i);
			this.release();
		}

		/** adds a set of integers in the hash set of Integer
		 * @param newSet the set of integer to add in the hash set*/
		public void add(HashSet<ObjectSystemIdentifier> newSet)
		{
			ObjectSystemIdentifier item;

			this.acquire();
			Iterator<ObjectSystemIdentifier> iter= newSet.iterator();
			while(iter.hasNext())
			{
				item=iter.next();
				this.hashSet.add(item);
			}
			this.release();
		}

		/** allows to know if an integer is included in the hash list
		 * @param i the given integer 
		 * @return true if the given integer is included in the hash set*/
		public boolean contains(ObjectSystemIdentifier i)
		{
			return this.hashSet.contains(i);
		}

		/** returns an intersection HastSet which is the result of this set and another one 
		 * @param newSet the other set of integer
		 * @result the hashset which is the intersection of the two sets
		 */
		public HashSet<ObjectSystemIdentifier> inter(HashSet<ObjectSystemIdentifier> newSet)
		{
			ObjectSystemIdentifier item;
			HashSet<ObjectSystemIdentifier> res = new HashSet<ObjectSystemIdentifier>();
			Iterator<ObjectSystemIdentifier> iter= newSet.iterator();
			while(iter.hasNext())
			{
				item=iter.next();
				if (!this.hashSet.contains(item)) res.add(item);
			}
			return res;
		}

		/** returns an union HastSet which is the result of this set and another one 
		 * @param newSet the other set of integer
		 * @result the hashset which is the union of the two sets
		 */
		public HashSet<ObjectSystemIdentifier> union(HashSet<ObjectSystemIdentifier> newSet)
		{

			HashSet<ObjectSystemIdentifier> res = (HashSet<ObjectSystemIdentifier>) this.hashSet.clone();

			Iterator<ObjectSystemIdentifier> iter= newSet.iterator();
			while(iter.hasNext()) res.add(iter.next());

			return res;
		}


		/** 
		 * Remove an item 
		 * @param i the item to remove
		 */
		public void remove(ObjectSystemIdentifier sys_id)
		{
			this.acquire();
			this.hashSet.remove(sys_id);
			this.release();
		}

		/** 
		 * Remove a set of item 
		 * @param set the set of item to remove
		 */		
		public void remove(HashSet<ObjectSystemIdentifier>  set)
		{
			this.acquire();

			Iterator<ObjectSystemIdentifier> iter = set.iterator();
			while(iter.hasNext()) this.hashSet.remove(iter.next());

			this.release();
		}

		/** 
		 * returns the encapsuled hash set
		 * @return the encapsuled HashSet
		 */		
		public HashSet<ObjectSystemIdentifier> getHashSet()
		{
			return this.hashSet;
		}

		/** 
		 * returns a clone of the encapsuled hash set
		 * @return the cloned HashSet
		 */		
		public HashSet<ObjectSystemIdentifier> cloneHashSet()
		{
			return (HashSet<ObjectSystemIdentifier>) this.hashSet.clone();
		}

	}



	/** This class allows to manage an event set */
	private class EventsListToProcessManager
	{
		/** the map of events list associated to each raiser */
		private HashMap<ObjectSystemIdentifier,ArrayList<Event>> eventsMap;
		/** the number of raised events */
		private int nbEvents;
		/** date of the oldest event in the list */
		private aDate oldestEventInList;

		/** basic constructor */
		public EventsListToProcessManager()
		{
			this.eventsMap=new HashMap<ObjectSystemIdentifier,ArrayList<Event>>();
			this.nbEvents=0;
		}

		/** age of the list (time elapsed since the raise of the oldest event in the list 
		 * @return the elasped time in ms, -1 if there is not event in the list*/
		public long age()
		{
			if (this.oldestEventInList==null) return -1;
			return (new aDate()).differenceToMS(this.oldestEventInList);
		}

		/** add an event in the map 
		 * @param raiser the event raiser
		 * @param evt:  the event
		 */
		public void add(ObjectSystemIdentifier raiser,Event evt)
		{

			if (this.eventsMap.containsKey(raiser))
				this.eventsMap.get(raiser).add(evt);
			else
			{
				ArrayList<Event> lst=new ArrayList<Event>();
				lst.add(evt);
				this.eventsMap.put(raiser,lst);
			}

			if(nbEvents++==0) this.oldestEventInList=new aDate();

		}

		/** clear all event */
		public void clear()
		{
			this.eventsMap.clear();
			this.oldestEventInList=null;
			this.nbEvents=0;
		}

		/** returns the encapsuled HashMap of events' list
		 * @return the encapsuled HashMap
		 */
		public HashMap<ObjectSystemIdentifier,ArrayList<Event>> get()
		{
			return eventsMap;
		}

		/** number of events in the list
		 * @return the number of events
		 */
		public int getNbEvents()
		{
			return nbEvents;
		}

		/** number of events concerning s specific raiser
		 * @param raiser the raiser which is concerned by the request
		 * @return the number of events associated to the specified raiser
		 */
		public int getNbEvents(ObjectSystemIdentifier raiser)
		{
			return this.eventsMap.get(raiser).size();
		}

	}


	public synchronized void sendBytes(ObjectSystemIdentifier sys_id, byte[] bytes)
	{
		this.embeddedObjectsManager.getManagedEmbeddedObject(sys_id).sendFrame(bytes);
	}


	@Override
	public synchronized void receivedBytes(ObjectSystemIdentifier sys_id, byte[] bytes) 
	{

		System.out.println("\nON A RECU : "+BytesArray.displayByteArray(bytes)+"\n*************************************************");
		BytesArray bArray = new BytesArray(bytes);

		if(this.baseFrame==null)
		{
			System.err.println("Bytes received by the simulator but there is not a Frame class defined. Bytes are ["+BytesArray.displayByteArray(bytes)+"]");
			this.notifyEvent(new SendedBytesByEmbeddedObjectEvent(sys_id,bytes,"Class error: No Frame class has been specified",false));
			return;
		}

		// Est-ce une trame systeme?
		SystemFrame sysFrame = null;
		try
		{
			sysFrame = new SystemFrame(bArray.array);
		}
		catch(NotSystemFrameException e)
		{
			e.printStackTrace();
		}

		if(sysFrame!=null)
		{
			SystemFrame interpretedSysFrame=null;

			try 
			{
				interpretedSysFrame = sysFrame.getInterpretedSystemFrame();
				System.out.println("\nTrame systeme recue --- "+interpretedSysFrame.toString());

				if(interpretedSysFrame instanceof AgentInformationRequestSystemFrame)
				{

					AgentInformationRequestSystemFrame req = (AgentInformationRequestSystemFrame) interpretedSysFrame;
					SimulatedObject sObj=this.getSimulatedObject(sys_id);
					AgentInformationReplySystemFrame resp = new AgentInformationReplySystemFrame(sObj.getObject().getSystemId().getId(),sObj.getPosition().x,sObj.getPosition().y,(byte)0x01);

					System.out.println("Réponse à la requête:"+resp);
					this.embeddedObjectsManager.getManagedEmbeddedObject(sys_id).sendFrame(resp.toBytes());
				}
				else if(interpretedSysFrame instanceof PerceivedAgentsInformationRequestSystemFrame)
				{
					PerceivedAgentsInformationRequestSystemFrame req = (PerceivedAgentsInformationRequestSystemFrame) interpretedSysFrame;
					PerceivedAgentsInformationReplySystemFrame resp = new PerceivedAgentsInformationReplySystemFrame();

					IntegerPosition position=new IntegerPosition(req.x,req.y);
					this.getSimulatedObject(sys_id).setPosition(position);

					LinkedList<SimulatedObject> res = new LinkedList<SimulatedObject>();
					SimulatedObject[] tab=this.getMASObjectArray();
					for(int i=0;i<tab.length;i++)
					{
						SimulatedObject sObj=tab[i];
						if(tab[i].getPosition().inCircleArea(position, req.radius)) resp.add(sObj.getObject().getSystemId().getId(), sObj.getPosition().x, sObj.getPosition().y, (byte) 0x01);
					}

					System.out.println("Réponse à la requête:"+resp+" bytes="+resp.toBytes());
					this.embeddedObjectsManager.getManagedEmbeddedObject(sys_id).sendFrame(resp.toBytes());

				}
				else if(interpretedSysFrame instanceof AgentInformationReplySystemFrame)
				{
					AgentInformationReplySystemFrame reply = (AgentInformationReplySystemFrame) interpretedSysFrame;

					if(sys_id.getId()!=reply.getAgentInformation().id) System.out.println("\nSUSPICIOUS SYSTEM FRAME REPLY SENDED BY +"+sys_id+"+! :"+reply);

					SimulatedObject sObj=this.getSimulatedObject(sys_id);
					sObj.setPosition(new IntegerPosition(reply.getAgentInformation().x,reply.getAgentInformation().y));
					System.out.println("Call the setPosition "+new IntegerPosition(reply.getAgentInformation().x,reply.getAgentInformation().y)+"method on "+sObj);
				}


			} 
			catch (NotUnderstantableSystemFrameException e) {
				// TODO Auto-generated catch block
				System.out.println("\nUne trame système bien formée est arrivée mais il y a erreur d'interprétation ");
				e.printStackTrace();
			}


		}
		else
		{
			System.err.println("\nCe n'est pas une trame systeme");

			// Ce n'est pas une trame system
			boolean isUnderstandable=true;
			String signification;

			Method searchedMethod = null;
			try {
				searchedMethod = this.baseFrame.getMethod("createFrameFromBytes", BytesArray.class);
			} catch (SecurityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (NoSuchMethodException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			System.out.println("FINDED : "+searchedMethod);


			Frame frm=null;
			java.lang.Object[] param = new java.lang.Object[1];
			param[0]=bArray;

			try 
			{
				frm=(Frame) searchedMethod.invoke(null,bArray);
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				this.notifyEvent(new SendedBytesByEmbeddedObjectEvent(sys_id,bytes,"Unknown: Frame is not understandable",false));
				return;
			} 				



			try
			{
				Message msg=frm.getMessage();
				if(msg==null)
				{
					signification="Unknown: Frame seem to be OK but the encapsuled message is NOK";
					isUnderstandable=false;
				}
				else
					signification=frm.toString();
			}
			catch(Exception e)
			{
				signification="Unknown: Frame seem to be OK but the encapsuled message is NOK ("+e.toString()+")";
				isUnderstandable=false;
			}



			this.notifyEvent(new SendedBytesByEmbeddedObjectEvent(sys_id,bytes,signification,isUnderstandable));

			if(isUnderstandable) this.dispatchFrame(new ObjectSystemIdentifier(frm.getSender()), frm);



			// mise en commentaire lors du passage au SystemIdentifier
			this.dispatchFrame(new ObjectSystemIdentifier(frm.getSender()), frm);
			//SimulatedObject sender = this.itemList.get(new ObjectSystemIdentifier(frm.getSender())).getSimulatedObject();
			//sender.getObject().setColor(Color.YELLOW);


		}

	}


	public void executeCallOfMethods(ObjectSystemIdentifier sys_id, String cmd) 
	{
		this.itemList.get(sys_id).getSimulatedObject().getObject().executeCallOfMethods(cmd);
	}


	public void setEnvironmentAttribute(Object object, String name, java.lang.Object value) throws AttributeNotFoundException
	{
		// TODO Auto-generated method stub
		this.environment.set(name,value,this.getSimulatedObject(object.getSystemId()).getPosition());
	}


	public java.lang.Object getEnvironmentAttribute(Object object, String name) throws AttributeNotFoundException
	{
		// TODO Auto-generated method stub
		return this.environment.get(name,this.getSimulatedObject(object.getSystemId()).getPosition());

	}

	public synchronized SimulatedObject[] getMASObjectArray()
	{
		java.lang.Object[] objectsTab = this.itemList.getClonedList();
		SimulatedObject[] res = new SimulatedObject[objectsTab.length];
		for(int i=0;i<res.length;i++) res[i]=((ObjectAndItsNeighboorhood) objectsTab[i]).getSimulatedObject();
		return res;
	}

	public  BufferedImage getMASView(IHMViewParametersInterface params) throws InexistantLevelGraphicalViewException
	{
		return this.getMASView(params,-1);
	}

	public synchronized BufferedImage getMASView(IHMViewParametersInterface params,int level) throws InexistantLevelGraphicalViewException
	{

		boolean notExistingView = false;	// init to false here in case of none agent representation required

		if (level==MAS.ACTIVE_LEVEL) level=params.getLayer();


		System.out.println("UPDATE MAS VIEW (layer="+level+")");


		BufferedImage imgEnv;
		Graphics2D grEnv;

		// ENVIRONMENT
		double scale = params.getScale();
		if(params.isVisibleEnvironmentLayerView())
		{
			imgEnv = this.environment.getEnvironmentView().graphicalView(params.getOffset().clone(), params.getViewAreaDimension().x, params.getViewAreaDimension().y, scale);
			grEnv = (Graphics2D) imgEnv.getGraphics();		
		}
		else
		{
			imgEnv = new BufferedImage(params.getViewAreaDimension().x,params.getViewAreaDimension().y,BufferedImage.TYPE_INT_RGB);
			grEnv = (Graphics2D) imgEnv.getGraphics();	
			grEnv.setColor(Color.WHITE);
			grEnv.fillRect(0, 0, imgEnv.getWidth(), imgEnv.getHeight());
		}

		// LINK
		java.lang.Object[] objectsTab = this.itemList.getClonedList();
		if(params.isVisibleLinkLayerView())
		{
			if (level==0)
			{
				grEnv.setColor(Color.BLUE);
				ObjectAndItsNeighboorhood obj;

				for(int i=0;i<objectsTab.length;i++)
				{
					obj = (ObjectAndItsNeighboorhood) objectsTab[i];
					IntegerPosition p = obj.getSimulatedObject().getPosition();
					Iterator<ObjectSystemIdentifier> iterId= obj.getClonedNeighboorhood().iterator();
					while(iterId.hasNext())
					{
						ObjectSystemIdentifier id = iterId.next();
						IntegerPosition p2 = this.getSimulatedObject(id).getPosition().clone();
						if( 	p.inRectangleArea(params.getOffset(), new IntegerPosition((int) (params.getViewAreaDimension().x/params.getScale()+params.getOffset().x),(int) (params.getViewAreaDimension().y/params.getScale()+params.getOffset().y)))
								||  p2.inRectangleArea(params.getOffset(), new IntegerPosition((int) (params.getViewAreaDimension().x/params.getScale()+params.getOffset().x),(int) (params.getViewAreaDimension().y/params.getScale()+params.getOffset().y)))
								)
						{
							IntegerPosition p1=p.clone();
							p1.sub(params.getOffset());	p1.multi((float) params.getScale());
							p2.sub(params.getOffset());	p2.multi((float) params.getScale());
							grEnv.drawLine(p1.x,p1.y,p2.x,p2.y);
						}
					}
				}
			}
			else
			{

				grEnv.setColor(Color.PINK);
				ObjectAndItsNeighboorhood obj;

				for(int i=0;i<objectsTab.length;i++)
				{
					obj = (ObjectAndItsNeighboorhood) objectsTab[i];
					SimulatedObject sObj = ((ObjectAndItsNeighboorhood)objectsTab[i]).getSimulatedObject();
					IntegerPosition p = sObj.getPosition();

					if(sObj.getObject() instanceof simulation.views.entity.RecursiveEntityViewInterface)
					{

						simulation.views.entity.RecursiveEntityViewInterface recObj =  (simulation.views.entity.RecursiveEntityViewInterface) sObj.getObject();
						Vector<Integer> lNeigh = recObj.getViewableLinkedNeighboorsUserIdentifier(level);

						if (lNeigh!=null)
						{
							Iterator<Integer> iterId= lNeigh.iterator();
							while(iterId.hasNext())
							{
								ObjectSystemIdentifier id = new ObjectSystemIdentifier(iterId.next());
								IntegerPosition p2 = this.getSimulatedObject(id).getPosition().clone();
								if( 	p.inRectangleArea(params.getOffset(), new IntegerPosition((int) (params.getViewAreaDimension().x/params.getScale()+params.getOffset().x),(int) (params.getViewAreaDimension().y/params.getScale()+params.getOffset().y)))
										||  p2.inRectangleArea(params.getOffset(), new IntegerPosition((int) (params.getViewAreaDimension().x/params.getScale()+params.getOffset().x),(int) (params.getViewAreaDimension().y/params.getScale()+params.getOffset().y)))
										)
								{
									IntegerPosition p1=p.clone();
									p1.sub(params.getOffset());	p1.multi((float) params.getScale());
									p2.sub(params.getOffset());	p2.multi((float) params.getScale());
									grEnv.drawLine(p1.x,p1.y,p2.x,p2.y);
								}
							}
						}
					}


				}
			}
		}


		// Agents
		if(params.isVisibleAgentlayerView())
		{
			if (level==0)
			{
				for(int i=0;i<objectsTab.length;i++)
				{
					SimulatedObject sObj = ((ObjectAndItsNeighboorhood)objectsTab[i]).getSimulatedObject();
					IntegerPosition p = sObj.getPosition();
					BufferedImage buffer;
					if(p.inRectangleArea(params.getOffset(), new IntegerPosition((int) (params.getViewAreaDimension().x/params.getScale()+params.getOffset().x),(int) (params.getViewAreaDimension().y/params.getScale()+params.getOffset().y))))
					{	
						IntegerPosition p1 = p.clone(); p1.sub(params.getOffset());	p1.multi((float) params.getScale());

						if(params.getTypeOfAgentViews()==AgentViewEnumType.BMP)
							buffer= sObj.getObject().getView().graphicalView(scale,!params.isVisibleTextLayerView());
						else
						{
							BasicView basic =null;
							if(params.getTypeOfAgentViews()== AgentViewEnumType.CIRCLE)
								basic=new CircleView(this.defaultAgentView.length,"");
							else if(params.getTypeOfAgentViews()==AgentViewEnumType.CROSS)
								basic=new CrossView(this.defaultAgentView.length,"");
							else if(params.getTypeOfAgentViews()==AgentViewEnumType.SQUARE)
								basic=new SquareView(this.defaultAgentView.length,"");
							else if(params.getTypeOfAgentViews()==AgentViewEnumType.RECTANGLE)
								basic=new RectangleView(2*this.defaultAgentView.length,this.defaultAgentView.length,"");
							else if(params.getTypeOfAgentViews()==AgentViewEnumType.POINT)
								basic=new CircleView(2,"");
							else
								basic=new CircleView(this.defaultAgentView.length,"");
							buffer= sObj.getObject().graphicalView(scale,basic,!params.isVisibleTextLayerView());
						}
						grEnv.drawImage(buffer, null,(int) (p1.x-buffer.getWidth()/2),(int) (p1.y-buffer.getHeight()/2));
					}						
				}
			}
			else
			{
				notExistingView=true;
				for(int i=0;i<objectsTab.length;i++)
				{
					SimulatedObject sObj = ((ObjectAndItsNeighboorhood)objectsTab[i]).getSimulatedObject();
					IntegerPosition p = sObj.getPosition();
					BufferedImage buffer=null;
					if(p.inRectangleArea(params.getOffset(), new IntegerPosition((int) (params.getViewAreaDimension().x/params.getScale()+params.getOffset().x),(int) (params.getViewAreaDimension().y/params.getScale()+params.getOffset().y))))
					{	
						IntegerPosition p1 = p.clone(); p1.sub(params.getOffset());	p1.multi((float) params.getScale());
						if(sObj.getObject() instanceof simulation.views.entity.RecursiveEntityViewInterface)
						{
							simulation.views.entity.RecursiveEntityViewInterface recObj =  (simulation.views.entity.RecursiveEntityViewInterface) sObj.getObject();
							//							System.out.println("Elementary agent #"+sObj.getObject().getUserId()+"  nblayers="+recObj.getNbOfLayer()+"  Upper layer id="+(recObj.getNbOfLayer()>=1? recObj.getLayerId(1) : -1));
							//							if(recObj.getLayerId(params.getLayer()-1)==recObj.getElementaryId())
							//							{
							//								if(params.getTypeOfAgentViews()==AgentViewEnumType.BMP)
							//									buffer= sObj.getObject().getView().graphicalView(scale,!params.isVisibleTextLayerView());
							//								else
							//								{
							BasicView basic =null;
							if(params.getTypeOfAgentViews()== AgentViewEnumType.CIRCLE)
								basic=new CircleView(this.defaultAgentView.length,"");
							else if(params.getTypeOfAgentViews()==AgentViewEnumType.CROSS)
								basic=new CrossView(this.defaultAgentView.length,"");
							else if(params.getTypeOfAgentViews()==AgentViewEnumType.SQUARE)
								basic=new SquareView(this.defaultAgentView.length,"");
							else if(params.getTypeOfAgentViews()==AgentViewEnumType.RECTANGLE)
								basic=new RectangleView(2*this.defaultAgentView.length,this.defaultAgentView.length,"");
							else if(params.getTypeOfAgentViews()==AgentViewEnumType.POINT)
								basic=new CircleView(2,"");
							else
								basic=new CircleView(this.defaultAgentView.length,"");


							try
							{
								buffer= ((simulation.views.entity.RecursiveEntityViewInterface) sObj.getObject()).graphicalView(scale,basic,!params.isVisibleTextLayerView(),level);
								notExistingView=false;
							}
							catch(InexistantLevelGraphicalViewException e)
							{
								buffer=e.getImage();
							}

							//								}
							grEnv.drawImage(buffer, null,(int) (p1.x-buffer.getWidth()/2),(int) (p1.y-buffer.getHeight()/2));
							//							}		
						}
					}
				}
			}
		}

		if (notExistingView) throw new InexistantLevelGraphicalViewException(imgEnv);

		return imgEnv;
	}

	public void updateAgentsViewModel(AgentViewEnumType agentView,int length)
	{
		this.defaultAgentView = new DefaultAgentView(agentView,length);
	}




	private class DefaultAgentView
	{
		public AgentViewEnumType type;
		public int length;

		public DefaultAgentView()
		{
			this(AgentViewEnumType.CIRCLE,8);
		}
		public DefaultAgentView(AgentViewEnumType type, int length)
		{
			this.type=type;
			this.length=length;
		}
	}



	public String getEnvironmentStringRepresentation() {
		// TODO Auto-generated method stub
		return this.environment.toString().replace("\n","\r\n");
	}
}
