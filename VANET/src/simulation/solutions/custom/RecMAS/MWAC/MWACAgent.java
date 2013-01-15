package simulation.solutions.custom.RecMAS.MWAC;
import java.awt.Color;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Vector;

import simulation.entities.Agent;
import simulation.events.system.MessageNotTransmittedEvent;
import simulation.events.system.ReceivedMessageEvent;
import simulation.events.system.RoleModificationEvent;
import simulation.events.system.SendedMessageEvent;
import simulation.messages.*;
import simulation.multiagentSystem.MAS;
import simulation.solutions.custom.RecMAS.MWAC.Messages.*;
import simulation.solutions.custom.RecMAS.RecursiveAgent.RecursiveAgent;
import simulation.utils.Log;
import simulation.utils.aDate;


/**
 * This class model our solution based on the MWAC model
 * @author Jean-Paul Jamont
 */
public class MWACAgent extends Agent implements ObjectAbleToSendMessageInterface{

	// Test pour la solution reccurssive
	public final static int MAX_NB_TENTATIVE_DETECTION_VOISINAGE = 3;
	public int nbTentativeDetectionVoisinage = 0;


	



	/** Define the workstation id */
	private static int WORKSTATION_ID = 1;

	/** enables organizational incoherence verification */
	private static final boolean ENABLE_ORGANIZATIONAL_INCOHERENCE_VERIFICATION = false;
	/** enables debug print out */
	private static final boolean DEBUG = false;

	/** delay (in ms) before the detection of a possible organizational incoherence and its real notification */
	private static final int DELAY_BEFORE_ORGANIZATIONAL_INCOHERENCE_VERIFICATION = 8000;
	/** maximum distance authorized to not reorganize the organization  */
	private static final int MAX_HOP_RESEARCH_ORGANIZATIONAL_INCOHERENCE = 4;

	/** no role */
	public  final static byte roleNOTHING				=0;
	/** simple member role */
	public  final static byte roleSIMPLEMEMBER			=1;
	/** link member role */
	public  final static byte roleLINK					=2;
	/** representative member role */
	public  final static byte roleREPRESENTATIVE		=3;
	/** color of no role members */
	public  final static Color colorNOTHING 			= Color.LIGHT_GRAY;
	/** color of representative members */
	public  final static Color colorREPRESENTATIVE 		= Color.RED;
	/** color of link members */
	public  final static Color colorLINK 				= Color.GREEN;
	/** color of simple member */
	public  final static Color colorSIMPLEMEMBER 		= Color.YELLOW;





	/** member of non group */
	protected static int groupNONE = -1;

	/** role of the agent */
	protected byte role;
	/** received message queue */
	protected FrameFIFOStack receivedMessageQueue;
	/** message to send (if the agent is Representative*/
	protected MWACMessageFIFOStack messageToTransmitQueue;
	/** neighboorList */
	protected MWACNeighboorList neighboorlist;
	/** to manage already processed route request */
	protected MWACAlreadyProcessedRouteRequestManager alreadyProcessedRouteRequestManager;
	/** MWAC route fragment  manager */
	protected MWACNetworkPartialKnowledgeManager networkPartialKnowledgeManager;
	/** MWAC incoherence possible detection manager */ 
	protected MWACIncoherenceList organizationalIncoherenceManager;


	// Context of the interaction (messages process)
	protected InteractionContext interaction = new InteractionContext();



	/** Memorize the energy level when the agent has been elected representative */
	private float pourcentOfAvailableEnergyWhenElectedRepresentative;


	private long DATE_OF_LAST_INTRODUCTION_SENDED_BECAUSE_EMPTY_NEIGBOORHOOD = new aDate().getTimeInMillis();
	private static final long TIME_BETWEEN_TWO_INTRODUCTION_SENDED_WHEN_EMPTY_NEIGBOORHOOD = 10000;

	/**
	 * Constructor
	 * @param mas the multiagent system
	 * @param id identifier of the agent
	 * @param energy energy of the agent
	 * @param range range of the agent
	 */
	public MWACAgent(MAS mas,Integer  id, Float energy,Integer  range)
	{
		super(mas,id,range);
		this.role=roleNOTHING;
		this.receivedMessageQueue=new FrameFIFOStack();
		this.neighboorlist=new MWACNeighboorList();
		this.messageToTransmitQueue=null;	//R
		this.alreadyProcessedRouteRequestManager=null;
		this.networkPartialKnowledgeManager=null;
		this.organizationalIncoherenceManager=null;
		this.pourcentOfAvailableEnergyWhenElectedRepresentative=0;

	}

	public String idToString()
	{
		return "(id="+this.getSystemId()+")";
	}
	
	public void waitMessages()
	{
		if(this.neighboorlist.isEmpty() && aDate.getCurrentTimeInMS()-this.DATE_OF_LAST_INTRODUCTION_SENDED_BECAUSE_EMPTY_NEIGBOORHOOD>MWACAgent.TIME_BETWEEN_TWO_INTRODUCTION_SENDED_WHEN_EMPTY_NEIGBOORHOOD)
		{
			if(this.nbTentativeDetectionVoisinage<MWACAgent.MAX_NB_TENTATIVE_DETECTION_VOISINAGE)
			{
				System.out.println(this.idToString()+" has no neigbhoor... send an introduction");
				
				
				this.DATE_OF_LAST_INTRODUCTION_SENDED_BECAUSE_EMPTY_NEIGBOORHOOD=aDate.getCurrentTimeInMS();
				this.nbTentativeDetectionVoisinage++;
				this.sendIntroduction();
			}
		}
		else 
		{
			if (MWACAgent.ENABLE_ORGANIZATIONAL_INCOHERENCE_VERIFICATION)
			{
				// All agents look if there are detected a possible incoherence
				if (this.neighboorlist.dateOfLastPossibleIncoherenceDetection>0)
					if ( (new aDate()).differenceToMS(new aDate(this.neighboorlist.dateOfLastPossibleIncoherenceDetection))>DELAY_BEFORE_ORGANIZATIONAL_INCOHERENCE_VERIFICATION) 
					{
						// We reported the problem to our representative
						this.incoherenceNotificationProcedure();	
						this.neighboorlist.dateOfLastPossibleIncoherenceDetection=0;
					}

				// A representative agent manage suspected inconsistency 
				if (this.role==MWACAgent.roleREPRESENTATIVE)
				{
					int idOfAProblematicGroup = this.organizationalIncoherenceManager.existsProblem();
					if (idOfAProblematicGroup!=MWACIncoherenceList.NO_REAL_INCOHERENCE)
					{
						if (this.getUserId()>idOfAProblematicGroup)
						{
							System.out.println("REAL PROBLEM catch by "+this.idToString()+" with group "+idOfAProblematicGroup+"\n"+this.organizationalIncoherenceManager.toString());

							// leave its role
							interaction.lostRepresentativeRole=true;
						}
						else
							System.out.println("IGNORE REAL PROBLEM catch by "+this.idToString()+" with group "+idOfAProblematicGroup+"\n"+this.organizationalIncoherenceManager.toString());
					}
				}

			}


			// Is it the time to leave its representative mandat?
			if (this.pourcentOfAvailableEnergy()<(this.pourcentOfAvailableEnergyWhenElectedRepresentative/2.0)) interaction.lostRepresentativeRole=true;
		}

	}


	public void processInteractions()
	{



		// Process waiting messages (if we have received some messages)
		while((interaction.frame=this.receivedMessageQueue.pop())!=null) if (!interaction.lostRepresentativeRole) this.processMessage(interaction);

		if(interaction.reinitializationOfTheRole)
		{
			//this.setRole(this.roleNOTHING);
			this.neighboorlist=new MWACNeighboorList();
		}
		// Verify the role
		this.computeRoleDecision(interaction);


		// Action associated to the decision to take into account the  interaction/organization modification
		if(interaction.roleConfict)
		{
			if(MWACAgent.DEBUG) debug("Je suis en conflit avec "+this.neighboorlist.getRepresentativeIdentifiers());
			this.sendConflictResolution();
		}
		else if(interaction.mustSendWhoAreMyNeighoors)
			this.sendWhoAreMyNeighboors();
		else if(interaction.mustSendAPresentation)
			this.sendPresentation();

		interaction.init();



	}
	/**  Launched by the call of start method*/
	public void run()
	{

		int i=0;
		//this.trustManager.setManagedAgent(this);
		System.out.println("Démarrage du MWACAgent "+this.getUserId());

		try{Thread.sleep(500);}catch(Exception e){}

		if (MWACAgent.DEBUG) debug("Envoie un message d'introduction");
		sendIntroduction();
		try{Thread.sleep(2*SLEEP_TIME_SLOT);}catch(Exception e){};




		// MWAC LAYER
		while(!isKilling() && !isStopping())
		{


			// Pause
			//try{Thread.sleep(SLEEP_TIME_SLOT+this.extraWaitTime(this.role));}catch(Exception e){};
			this.sleep(SLEEP_TIME_SLOT/*+this.extraWaitTime(this.role)*/);



			// Preparation of the others threads
			while(  ((isSuspending()) && (!isKilling() && !isStopping()))) try{Thread.sleep(SLEEP_TIME_SLOT);}catch(Exception e){};



			// Attente de messages
			while( this.receivedMessageQueue.isEmpty() && (!interaction.lostRepresentativeRole) && (!interaction.reinitializationOfTheRole)) 
			{
				this.waitMessages();

				//try{Thread.sleep(SLEEP_TIME_SLOT);}catch(Exception e){};
				this.sleep(SLEEP_TIME_SLOT);
			}


			this.processInteractions();

			// Must be freezed?
			try{Thread.sleep(SLEEP_TIME_SLOT);}catch(Exception e){}
		}

		// AGENT SPECIFIC TASK
		System.out.println("Fin du thread "+this.getUserId());
	}


	/**
	 * chose a role and decide of action if necessary
	 * @param interaction
	 */
	public void computeRoleDecision(InteractionContext interaction)
	{

		// No neighboors
		byte newRole=this.role;	

		if(this.neighboorlist.isEmpty()) 
			newRole=MWACAgent.roleNOTHING;
		else
		{
			switch(this.role)
			{
			case MWACAgent.roleNOTHING:
			case MWACAgent.roleSIMPLEMEMBER:
			{
				int n = this.neighboorlist.getNbRepresentative();
				if(n==0) 
					newRole=this.roleREPRESENTATIVE;
				else if(n==1) 
				{
					newRole=this.roleSIMPLEMEMBER;
					if(interaction.hasPreviouslyLostRepresentativeRole) newRole=this.role;
				}
				else newRole=this.roleLINK;
			}
			break;
			case MWACAgent.roleLINK:
			{
				int n = this.neighboorlist.getNbRepresentative();
				if(n==0) newRole=this.roleREPRESENTATIVE;
				else if(n==1) newRole=this.roleSIMPLEMEMBER;
				else newRole=this.roleLINK;
			}
			break;
			case MWACAgent.roleREPRESENTATIVE:
			{
				if(interaction.lostRepresentativeRole||interaction.hasPreviouslyLostRepresentativeRole)
				{
					newRole=MWACAgent.roleNOTHING;
					interaction.roleConfict=false;	// Il n'y a plus de conflit
				}
				else
				{
					if(this.neighboorlist.getNbRepresentative()>0) interaction.roleConfict=true;
					newRole=MWACAgent.roleREPRESENTATIVE;
				}
			}
			break;
			}
		}



		// Initialize data and 
		if (this.role!=newRole)
		{
			if(MWACAgent.DEBUG) debug("Je change de role "+MWACAgent.roleToString(this.role)+" => "+MWACAgent.roleToString(newRole)+" lost="+interaction.lostRepresentativeRole+"  prevLost="+interaction.hasPreviouslyLostRepresentativeRole);
			this.setRole(newRole);
			switch(newRole)
			{
			case MWACAgent.roleREPRESENTATIVE:
				this.pourcentOfAvailableEnergyWhenElectedRepresentative=this.pourcentOfAvailableEnergy();
				this.alreadyProcessedRouteRequestManager=new MWACAlreadyProcessedRouteRequestManager();
				this.messageToTransmitQueue=new MWACMessageFIFOStack();
				this.networkPartialKnowledgeManager=new MWACNetworkPartialKnowledgeManager();
				this.organizationalIncoherenceManager=new MWACIncoherenceList();
				interaction.mustSendWhoAreMyNeighoors=true;
				break;
			case MWACAgent.roleLINK:
				this.pourcentOfAvailableEnergyWhenElectedRepresentative=0;
				this.alreadyProcessedRouteRequestManager=new MWACAlreadyProcessedRouteRequestManager();
				this.networkPartialKnowledgeManager=null;
				this.messageToTransmitQueue=null;
				this.organizationalIncoherenceManager=null;
				interaction.mustSendAPresentation=true;
				break;
			case MWACAgent.roleNOTHING:
				this.pourcentOfAvailableEnergyWhenElectedRepresentative=0;
				this.alreadyProcessedRouteRequestManager=null;
				this.networkPartialKnowledgeManager=null;
				this.messageToTransmitQueue=null;
				this.organizationalIncoherenceManager=null;
				this.neighboorlist=new MWACNeighboorList();
				interaction.mustSendAPresentation=false;
				this.sendIntroduction();
				this.sleep(250);	// A priori grosse modification... On prend le temps de laisser les choses se stabiliser
				break;
			case MWACAgent.roleSIMPLEMEMBER:
				this.alreadyProcessedRouteRequestManager=null;
				this.networkPartialKnowledgeManager=null;
				this.messageToTransmitQueue=null;
				this.organizationalIncoherenceManager=null;
				interaction.mustSendAPresentation=true;
				break;
			}
			if ((MWACAgent.DEBUG) && newRole==MWACAgent.roleREPRESENTATIVE) this.debug("Je deviens ROLE_REPRESENTANT");
		}


	}


	/**
	 * process a waiting message. Message is containing by the frame into the interaction context
	 * @param interaction the context of the interaction
	 */
	public void processMessage(InteractionContext interaction)
	{	
		// Message extraction from the frame
		MWACMessage msg=MWACMessage.createMessage(interaction.frame.getData());

		// compute depending the type of message
		switch(msg.getType())
		{
		case MWACMessage.msgINTRODUCTION:
		{
			Log.println("("+this.getUserId()+") traite le message d'introduction "+msg,Color.DARK_GRAY);
			this.neighboorlist.put(msg.getSender(),MWACAgent.roleNOTHING, MWACAgent.groupNONE);
			interaction.mustSendWhoAreMyNeighoors=true;
		}
		break;
		case MWACMessage.msgWHO_ARE_MY_NEIGHBOORS:
		case MWACMessage.msgPRESENTATION:
		{
			Log.println("("+this.getUserId()+") traite le message de presentation/whoAreMyNeighboor "+msg,Color.DARK_GRAY);
			this.neighboorlist.put(msg.getSender(),((MWACMessage_Presentation)msg).getRole(),  ((MWACMessage_Presentation)msg).getClonedGroupArray());
			interaction.mustSendAPresentation=   interaction.mustSendAPresentation
					|| (msg.getType()==MWACMessage.msgWHO_ARE_MY_NEIGHBOORS) 
					|| this.neighboorlist.put(msg.getSender(),((MWACMessage_Presentation)msg).getRole(), ((MWACMessage_Presentation)msg).getClonedGroupArray());
		}
		break;
		case MWACMessage.msgCONFLICT_RESOLUTION:
		{
			this.neighboorlist.put(msg.getSender(), MWACAgent.roleREPRESENTATIVE, msg.getSender());
			if(this.role==MWACAgent.roleREPRESENTATIVE && !interaction.lostRepresentativeRole)
			{
				int scoreInMsg = ((MWACMessage_ConflictResolution)msg).getScore();
				if((scoreInMsg>this.score()) || ((scoreInMsg==this.score()) && this.getUserId()<msg.getSender()))
				{
					System.out.println(this.idToString()+" PERD CONFLIT CONTRE "+msg.getSender()+"    myScore="+this.score()+" vs "+scoreInMsg);
					if(MWACAgent.DEBUG) debug("*PERDU* contre "+msg.getSender());
					interaction.lostRepresentativeRole=true;
					interaction.mustSendAPresentation=true;
					interaction.debugPerdu=msg.getSender();
					Log.println("("+this.getUserId()+") PERDS le conflict resolution "+msg,Color.DARK_GRAY);
				}
				else
				{
					System.out.println(this.idToString()+" GAGNE CONFLIT CONTRE "+msg.getSender()+"    myScore="+this.score()+" vs "+scoreInMsg);
					if(MWACAgent.DEBUG) debug("*GAGNE* contre "+msg.getSender());
					this.neighboorlist.put(msg.getSender(),this.roleNOTHING,this.groupNONE);
					if(MWACAgent.DEBUG) debug("Je montre que je gagne mon conflit");
					this.sendConflictResolution();
					interaction.mustSendAPresentation=false;
					Log.println("("+this.getUserId()+") GAGNE le conflict resolution "+msg,Color.DARK_GRAY);
				}
			}
			else
			{
				// I have already lost my reprentative role against with another agent
				if (interaction.lostRepresentativeRole) if(MWACAgent.DEBUG) debug("*DEJA PERDU* contre "+interaction.debugPerdu+" bien avant "+msg.getSender()+"    role="+this.role+"  lost="+interaction.lostRepresentativeRole);
				//this.neighboorlist.put(msg.getSender(),this.roleNOTHING,this.groupNONE);
				interaction.mustSendAPresentation=true;
			}
		}
		break;
		case MWACMessage.msgDATA:
			if(interaction.frame.getReceiver()==this.getUserId())
			{
				MWACMessage_Data tMsg = (MWACMessage_Data) msg;

				if(msg.getReceiver()==this.getUserId()) 
					this.receiveMessage(tMsg);
				else if(this.role==MWACAgent.roleREPRESENTATIVE)
				{
					// Je suis le representant de l'émetteur (mon membre envoit directement le message)
					this.sendMessage(tMsg);
				}
			}
			break;
		case MWACMessage.msgTTL_ROUTE_REQUEST:
		case MWACMessage.msgROUTE_REQUEST:
		{
			// Je suis représentant
			MWACMessage_RouteRequest tMsg = (MWACMessage_RouteRequest) msg;
			if(this.getRole()==MWACAgent.roleREPRESENTATIVE)
			{
				TripletIdRoleGroup triplet=this.neighboorlist.get(tMsg.getReceiver());
				if (triplet!=null || tMsg.getReceiver()==this.getUserId())
				{
					if(!alreadyProcessedRouteRequestManager.isAlreadyProcessed(tMsg.getSender(), tMsg.getIdRequest()))
					{
						// Est-til un voisin?
						System.out.println("REPRESENTANT #"+this.idToString()+" VA REPONDRE PAR UN ROUTE REPLY A LA RECHERCHE");
						int relais = this.neighboorlist.getLinkToRepresentant(MWACRouteAssistant.getLastId(tMsg.getRoute()));
						if (relais==MWACRouteAssistant.UNDEFINED_ID) relais=this.neighboorlist.getLinkToRepresentant(tMsg.getSender());
						if (relais==MWACNeighboorList.UNDEFINED_ID) System.out.println("!!!!!! (1) Moi repr "+this.idToString()+" ne trouve pas le précédent de "+interaction.frame.getSender()+" dans la route "+MWACRouteAssistant.routeToString(tMsg.getRoute())+" (pour un ROUTE REPLY)");
						//System.out.println("Le relais pour "+MWACRouteAssistant.getLastId(tMsg.getRoute())+" est "+relais);
						this.sendFrame(new MWACFrame(this.getUserId(),relais,new MWACMessage_RouteReply(this.getUserId(),tMsg.getSender(),tMsg.getIdRequest(),tMsg.getRoute())));
					}
					else
					{
						// Already answer by sending a route reply
					}
				}
				else
				{
					if( (!alreadyProcessedRouteRequestManager.isAlreadyProcessed(tMsg.getSender(), tMsg.getIdRequest())) && (tMsg.getSender()!=this.getUserId()))
					{	// On fait suivre
						tMsg.setRoute(MWACRouteAssistant.add(tMsg.getRoute(),this.getUserId()));



						if(msg.getType()==MWACMessage.msgTTL_ROUTE_REQUEST)
						{
							if(((MWACMessage_TTLRouteRequest) tMsg).getTTL()>0)
							{
								((MWACMessage_TTLRouteRequest) tMsg).decreaseTTL();
								this.sendFrame(new MWACFrame(this.getUserId(),MWACFrame.BROADCAST_LINK,(MWACMessage_TTLRouteRequest) tMsg));
							}
						}
						else
							this.sendFrame(new MWACFrame(this.getUserId(),MWACFrame.BROADCAST_LINK,tMsg));
					}
					else
					{
						// Already processed
					}
				}
			}
			else if(this.getRole()==MWACAgent.roleLINK)
			{
				if( (!alreadyProcessedRouteRequestManager.isAlreadyProcessed(tMsg.getSender(), tMsg.getIdRequest())) && (tMsg.getSender()!=this.getUserId()) && (interaction.frame.getReceiver()!=MWACFrame.BROADCAST_REPRESENTATIVE))
				{
					//System.out.println("LINK #"+this.getId()+" VA FAIRE SUIVRE LA RECHERCHE");
					this.sendFrame(new MWACFrame(this.getUserId(),MWACFrame.BROADCAST_REPRESENTATIVE, tMsg));
				}
				else
				{
					//System.out.println("LINK #"+this.getId()+" NE VA PAS REPONDRE A LA RECHERCHE CAR DEJA TRAITE");
				}
			}
		}
		break;	
		case MWACMessage.msgROUTE_REPLY:
		case MWACMessage.msgCHECK_ROUTE_REPLY:
			boolean isCheckRouteReply = msg instanceof MWACMessage_CheckRouteReply;
			if(interaction.frame.getReceiver()==this.getUserId())
			{

				MWACMessage_RouteReply tMsg = (MWACMessage_RouteReply) msg;

				if(this.getRole()==MWACAgent.roleREPRESENTATIVE)
				{
					if(tMsg.getReceiver()==this.getUserId())
					{
						if(isCheckRouteReply)
						{
							this.receivedCheckRoute((MWACMessage_CheckRouteReply) tMsg);
						}

						int[] route = MWACRouteAssistant.cloneRoute(tMsg.getRoute(), 1+tMsg.getRoute().length);
						route[route.length-1]=tMsg.getSender();
						this.networkPartialKnowledgeManager.addRoute(route);
						int member=this.networkPartialKnowledgeManager.getRouteRequestAssociatedReceiver(tMsg.getIdRequest());
						this.networkPartialKnowledgeManager.addIdGroupAssociation(MWACRouteAssistant.getLastId(route), member);
						this.tryToProcessWaitingSendedMessage();

						//System.out.println("Retour de "+tMsg.getSender());
						this.organizationalIncoherenceManager.contactedGroup(tMsg.getSender());
					}
					else
					{
						if(isCheckRouteReply && this.neighboorlist.contains(tMsg.getReceiver()))
						{
							// System.out.println("je transmet le CHECK route reply a un de mes membres");
							this.sendFrame(new MWACFrame(this.getUserId(),tMsg.getReceiver(),new MWACMessage_CheckRouteReply(tMsg.getSender(),tMsg.getReceiver(),tMsg.getIdRequest(),tMsg.getRoute())));

							return;
						}
						//System.out.println("RECU PAR repr "+this.getUserId()+" (CHECK) ROUTE REPLY "+tMsg.toString());
						int relais = this.neighboorlist.getLinkToRepresentant(MWACRouteAssistant.getPreviousId(tMsg.getRoute(),this.getUserId()));
						if (relais==MWACRouteAssistant.UNDEFINED_ID) relais=this.neighboorlist.getLinkToRepresentant(tMsg.getReceiver());
						if (relais==MWACNeighboorList.UNDEFINED_ID) 
							System.out.println("!!!!!! (2) Moi repr "+this.getUserId()+" ne trouve pas le précédent de "+interaction.frame.getSender()+" dans la route "+MWACRouteAssistant.routeToString(tMsg.getRoute())+" (pour un ROUTE REPLY)");
						else
						{
							if (!isCheckRouteReply)
								this.sendFrame(new MWACFrame(this.getUserId(),relais,new MWACMessage_RouteReply(tMsg.getSender(),tMsg.getReceiver(),tMsg.getIdRequest(),tMsg.getRoute())));
							else
								this.sendFrame(new MWACFrame(this.getUserId(),relais,new MWACMessage_CheckRouteReply(tMsg.getSender(),tMsg.getReceiver(),tMsg.getIdRequest(),tMsg.getRoute())));
						}
					}
				}
				else if(this.getRole()==MWACAgent.roleLINK)
				{

					if(tMsg.getReceiver()==this.getUserId())
					{
						if(isCheckRouteReply)
						{
							this.receivedCheckRoute((MWACMessage_CheckRouteReply) tMsg);
							return;
						}
					}

					//System.out.println("RECU PAR LINK"+this.getUserId()+" (CHECK) ROUTE REPLY "+tMsg.toString());
					int dest;
					if(this.neighboorlist.contains(tMsg.getReceiver()))
					{
						dest = tMsg.getReceiver();
						//System.out.println("!!!Le dest "+dest+" est mon voisin");
					}
					else if(MWACGroupAssistant.containsGroup(tMsg.getSender(), this.getGroups()))
					{
						if(!isCheckRouteReply)
							dest = MWACRouteAssistant.getLastId(tMsg.getRoute());
						else
							dest = MWACRouteAssistant.getPreviousId(tMsg.getRoute(), tMsg.getSender());
						//System.out.println("!!!Le dest "+dest+" est mon voisin representant (revoir pour le route request)");
					}
					else 
					{
						dest=(MWACRouteAssistant.getPreviousId(tMsg.getRoute(),interaction.frame.getSender()));
						//System.out.println("!!!Le dest "+dest+" est le precedent");
					}
					if (dest==MWACRouteAssistant.UNDEFINED_ID) 
						System.out.println("!!!!!! Moi link "+this.getUserId()+" ne trouve pas le précédent de "+interaction.frame.getSender()+" dans la route "+MWACRouteAssistant.routeToString(tMsg.getRoute())+" (pour un ROUTE REPLY)");
					else
					{
						if (!isCheckRouteReply)
							this.sendFrame(new MWACFrame(this.getUserId(),dest,new MWACMessage_RouteReply(tMsg)));
						else
							this.sendFrame(new MWACFrame(this.getUserId(),dest,new MWACMessage_CheckRouteReply((MWACMessage_CheckRouteReply) msg)));
					}
				}
				else
				{
					if(tMsg.getReceiver()==this.getUserId())
					{
						if(isCheckRouteReply)
						{
							this.receivedCheckRoute((MWACMessage_CheckRouteReply) tMsg);
						}
					}

				}
			}
			break;
		case MWACMessage.msgROUTED_DATA:
			if(interaction.frame.getReceiver()==this.getUserId())
			{
				MWACMessage_RoutedData tMsg = (MWACMessage_RoutedData) msg;

				if (tMsg.getReceiver()==this.getUserId())
				{
					this.receiveMessage(new MWACMessage_Data(tMsg.getSender(),tMsg.getReceiver(),tMsg.getMsg()));
				}
				else if(this.getRole()==MWACAgent.roleREPRESENTATIVE)
				{
					if (this.neighboorlist.contains(tMsg.getReceiver()))
					{
						//System.out.println("JE SUIS LE REPRESENTANT "+this.getId()+" DU RECEPTEUR ");
						this.sendFrame(new MWACFrame(this.getUserId(),tMsg.getReceiver(),new MWACMessage_Data(tMsg.getSender(),tMsg.getReceiver(),tMsg.getMsg())));
					}
					else
					{
						//System.out.println("RECU PAR repr "+this.getId()+" ROUTED DATA "+frm.toString());
						int relais = this.neighboorlist.getLinkToRepresentant(MWACRouteAssistant.getNextId(tMsg.getRoute(),this.getUserId()));
						//System.out.println("Le suivant de moi "+this.getId()+" dans "+MWACRouteAssistant.routeToString(tMsg.getRoute())+" est "+relais);
						if (relais==MWACNeighboorList.UNDEFINED_ID) 
							System.out.println("!!!!!! (3) Moi repr "+this.getUserId()+" ne trouve pas le suivant de "+interaction.frame.getSender()+" dans la route "+MWACRouteAssistant.routeToString(tMsg.getRoute())+" (pour un ROUTED DATA)");
						else
						{
							tMsg.setRoute(MWACRouteAssistant.removeHead(tMsg.getRoute()));
							this.sendFrame(new MWACFrame(this.getUserId(),relais,new MWACMessage_RoutedData(tMsg)));
						}
					}
				}
				else if(this.getRole()==MWACAgent.roleLINK)
				{

					//System.out.println("LINK "+this.getId()+" RECOIT ROUTED DATA "+frm.toString());
					int dest;
					//System.out.println("INFO "+frm.getSender()+"/"+MWACRouteAssistant.routeToString(this.getGroups()));
					dest = MWACRouteAssistant.getFirstId(tMsg.getRoute());
					//System.out.println("Envoyé par un de mes repr. Le prochain sera "+dest);

					if (dest==MWACNetworkPartialKnowledgeManager.UNDEFINED_ID)
						System.out.println("!!!!!! Moi link "+this.getUserId()+" ne trouve pas le suivant de "+interaction.frame.getSender()+" dans la route "+MWACRouteAssistant.routeToString(tMsg.getRoute())+" (pour un ROUTED DATA)");
					else
						this.sendFrame(new MWACFrame(this.getUserId(),dest,new MWACMessage_RoutedData(tMsg)));

				}
			}
			break;	

		case MWACMessage.msgPOSSIBLE_ORGANIZATIONAL_INCOHERENCE_NOTIFICATION:
			if (MWACAgent.ENABLE_ORGANIZATIONAL_INCOHERENCE_VERIFICATION)
				if(this.role==MWACAgent.roleREPRESENTATIVE)
				{
					MWACMessage_PossibleOrganizationalIncoherence tMsg = (MWACMessage_PossibleOrganizationalIncoherence) msg;
					//debug("L'agent REPRESENTANT "+this.getId()+" a recu une demande de verif de "+msg.getSender()+" concernant "+MWACRouteAssistant.routeToString(tMsg.getSuspectedGroups()));
					System.out.println("L'agent REPRESENTANT "+this.getUserId()+" a recu une demande de verif de "+msg.getSender()+" concernant "+MWACRouteAssistant.routeToString(tMsg.getSuspectedGroups()));
					int[] suspectedGroups = tMsg.getClonedSuspectedGroupsArray();
					for(int i=0;i<suspectedGroups.length;i++)
					{
						if(!this.organizationalIncoherenceManager.isSuspectedGroup(suspectedGroups[i]))
						{
							this.organizationalIncoherenceManager.add(tMsg.getSender(),suspectedGroups[i] );
							this.sendFrame(new MWACFrame(getUserId(),MWACFrame.BROADCAST_LINK,new MWACMessage_TTLRouteRequest(getUserId(),suspectedGroups[i],this.messageToTransmitQueue.getNextIdRequest(),(byte) MWACAgent.MAX_HOP_RESEARCH_ORGANIZATIONAL_INCOHERENCE)));
						}						
					}
				}
				else
				{
					// not concerned
				}
			break;

		case MWACMessage.msgCHECK_ROUTE_REQUEST:
			if(interaction.frame.getReceiver()==this.getUserId() || (interaction.frame.getReceiver()==MWACFrame.BROADCAST_REPRESENTATIVE && this.getRole()==MWACAgent.roleREPRESENTATIVE))
			{
				MWACMessage_CheckRouteRequest tMsg = (MWACMessage_CheckRouteRequest) msg;

				if (tMsg.getReceiver()==this.getUserId())
				{
					// Recu : il faut traiter!

					//System.out.println("Checkroute bien recu");
					this.sendFrame(new MWACFrame(this.getUserId(),interaction.frame.getSender(),new MWACMessage_CheckRouteReply(tMsg.getReceiver(),tMsg.getSender(),tMsg.getIdRequest(),tMsg.getRoute())));
				}
				else if(this.getRole()==MWACAgent.roleREPRESENTATIVE)
				{


					if (this.neighboorlist.contains(tMsg.getReceiver()))
					{
						//System.out.println("JE SUIS LE REPRESENTANT "+this.getId()+" DU RECEPTEUR ");
						this.sendFrame(new MWACFrame(this.getUserId(),tMsg.getReceiver(),new MWACMessage_CheckRouteRequest(tMsg)));
					}
					else if(this.neighboorlist.contains(tMsg.getSender()) && interaction.frame.getReceiver()==MWACFrame.BROADCAST_REPRESENTATIVE)
					{
						System.out.println("Je ("+this.getUserId()+")dois prendre en charge un check route pour "+interaction.frame.getSender());
						int[] route_start=new int[1]; route_start[0]=this.getUserId();
						int[] route_to_dest=this.networkPartialKnowledgeManager.getDataMessageRoute(MWACAgent.WORKSTATION_ID);
						if(route_to_dest!=null)
						{
							tMsg.setRoute(MWACRouteAssistant.add(route_start,route_to_dest));
							//System.out.println("J'envoie "+new MWACFrame(this.getUserId(),relais,new MWACMessage_CheckRouteRequest(tMsg)));
							//this.sendFrame(new MWACFrame(this.getUserId(),relais,new MWACMessage_CheckRouteRequest(tMsg)));
							int relais = this.neighboorlist.getLinkToRepresentant(MWACRouteAssistant.getNextId(tMsg.getRoute(),this.getUserId()));
							System.out.println("Le suivant de moi "+this.getUserId()+" dans "+MWACRouteAssistant.routeToString(tMsg.getRoute())+" est "+relais);
							if (relais==MWACNeighboorList.UNDEFINED_ID) 
								System.out.println("!!!!!! (3) Moi repr "+this.getUserId()+" ne trouve pas le suivant de "+interaction.frame.getSender()+" dans la route "+MWACRouteAssistant.routeToString(tMsg.getRoute())+" (pour un ROUTED DATA)");
							else
								this.sendFrame(new MWACFrame(this.getUserId(),relais,new MWACMessage_CheckRouteRequest(tMsg)));
						}
						else
						{
							// Je ne peux pas vérifier de route car je n'en connais pas
							System.out.println(">>>>> Moi repr "+this.getUserId()+" je n'ai encore jamais parlé avec "+tMsg.getReceiver()+"! Je ne peux donc pas aider "+interaction.frame.getSender()+" a vérifier si c'est une usurpation");

							this.sendFrame(new MWACFrame(this.getUserId(),interaction.frame.getSender(),new MWACMessage_CheckRouteReply(tMsg.getReceiver(),tMsg.getSender(),tMsg.getIdRequest(),new int[0]) ));
						}
					}
					else

					{
						//System.out.println("TRAITE PAR repr "+this.getUserId()+" CHECK ROUTE REQUEST "+interaction.frame.toString());
						int relais = this.neighboorlist.getLinkToRepresentant(MWACRouteAssistant.getNextId(tMsg.getRoute(),this.getUserId()));
						//System.out.println("Le suivant de moi "+this.getUserId()+" dans "+MWACRouteAssistant.routeToString(tMsg.getRoute())+" est "+relais);
						if (relais==MWACNeighboorList.UNDEFINED_ID) 
							System.out.println("!!!!!! (3) Moi repr "+this.getUserId()+" ne trouve pas le suivant de "+interaction.frame.getSender()+" dans la route "+MWACRouteAssistant.routeToString(tMsg.getRoute())+" (pour un ROUTED DATA)");
						else
							this.sendFrame(new MWACFrame(this.getUserId(),relais,new MWACMessage_CheckRouteRequest(tMsg)));
					}
				}
				else if(this.getRole()==MWACAgent.roleLINK)
				{

					// System.out.println("LINK "+this.getUserId()+" RECOIT CHECK ROUTE REQUEST "+interaction.frame.toString());
					int dest;

					//System.out.println("INFO "+frm.getSender()+"/"+MWACRouteAssistant.routeToString(this.getGroups()));
					if(!MWACRouteAssistant.contains(tMsg.getRoute(),interaction.frame.getSender()))
						// l'initiateur de la recherche est le représentant lui-même : il n'est donc psa dans la route!
						dest = tMsg.getRoute()[0];
					else
						dest = MWACRouteAssistant.getNextId(tMsg.getRoute(),interaction.frame.getSender());
					//System.out.println("Envoyé par un de mes repr. Le prochain sera "+dest);

					if (dest==MWACNetworkPartialKnowledgeManager.UNDEFINED_ID)
						System.out.println("!!!!!! Moi link "+this.getUserId()+" ne trouve pas le suivant de "+interaction.frame.getSender()+" dans la route "+MWACRouteAssistant.routeToString(tMsg.getRoute())+" (pour un ROUTED DATA)");
					else
						this.sendFrame(new MWACFrame(this.getUserId(),dest,new MWACMessage_CheckRouteRequest(tMsg)));

				}
			}
			break;	

		default:
			System.out.println("ERREUR!!!! NONE IMPLEMENTED MESSAGE "+msg.toString());
		}	
	}



	public MWACNeighboorList getNeighboorlist()
	{
		return this.neighboorlist;
	}

	/**
	 * If it is possible, send a message which is in a route waiting state
	 */
	public void tryToProcessWaitingSendedMessage()
	{
		MessageToSendItem item;
		ListIterator<MessageToSendItem> iter=this.messageToTransmitQueue.listIterator();
		int[] route;

		while(iter.hasNext())
		{
			item=iter.next();
			route=this.networkPartialKnowledgeManager.getDataMessageRoute(item.msg.getReceiver());
			if(route!=null) 
			{
				System.out.println("Repr "+this.getUserId()+" J'envoie le message routé!!!");
				this.sendFrame(new MWACFrame(this.getUserId(),this.neighboorlist.getLinkToRepresentant(MWACRouteAssistant.getFirstId(route)),new MWACMessage_RoutedData(item.msg,route)));
				iter.remove();
			}
		}
	}

	/**
	 * return the group identifier of this agent (the first one for a link agent)
	 * @return the group of the agent
	 */
	public int getGroup()
	{
		if(this.role==MWACAgent.roleREPRESENTATIVE)
			return this.getUserId();
		else
		{
			ArrayList<Integer> repr=this.neighboorlist.getRepresentativeIdentifiers();
			int nbRepr=repr.size();
			if(nbRepr==0)
			{
				switch(this.role)
				{
				case MWACAgent.roleSIMPLEMEMBER:
				case MWACAgent.roleLINK:
					System.out.println("<A"+this.getUserId()+","+MWACAgent.roleToString(this.role)+">!!!!! ERREUR de cohérence entre le role et la liste de voisins");
				case MWACAgent.roleNOTHING:
					// C'est normal pour eux
					return MWACAgent.groupNONE;
				}
			}
			else if (nbRepr==1)
			{
				switch(this.role)
				{
				case MWACAgent.roleSIMPLEMEMBER:
					return repr.get(0);
				case MWACAgent.roleLINK:
					System.out.println("<B"+this.getUserId()+","+MWACAgent.roleToString(this.role)+">!!!!! ERREUR de cohérence entre le role et la liste de voisins");
				case MWACAgent.roleNOTHING:
					return MWACAgent.groupNONE;
				}
			}
			else
			{
				if (this.role==MWACAgent.roleLINK)
					return repr.get(0);
				else
				{
					if (this.role!=MWACAgent.roleNOTHING) System.out.println("<C"+this.getUserId()+","+MWACAgent.roleToString(this.role)+">!!!!! ERREUR de cohérence entre le role et la liste de voisins");
					return MWACAgent.groupNONE;
				}
			}
		}
		System.out.println("<D"+this.getUserId()+","+MWACAgent.roleToString(this.role)+">!!!!! ERREUR de cohérence entre le role et la liste de voisins");
		return MWACAgent.groupNONE;
	}

	/**
	 * return the groups identifiers of this agent (interesting for link agent)
	 * @return the groups associated to the agent
	 */
	public int[] getGroups()
	{
		int[] res;

		if(this.getRole()!=MWACAgent.roleLINK)
		{
			res=new int[1];
			res[0]=this.getGroup();
		}
		else
		{
			ArrayList<Integer> repr=this.neighboorlist.getRepresentativeIdentifiers();
			int i=0;
			res=new int[repr.size()];
			ListIterator<Integer> iter=repr.listIterator();
			while(iter.hasNext()) res[i++]=iter.next();
		}
		return res;
	}

	/**
	 * extra waiting time depending of a specific role
	 * @param role the specified role
	 * @return the extra waiting time
	 */
	public int extraWaitTime(byte role)
	{
		if (this.role==this.roleSIMPLEMEMBER) return 3*MWACAgent.SLEEP_TIME_SLOT;
		return 0;
	}

	/** allows to an object to receive a message. Call by the simulator
	 * @param frame the received frame 
	 */
	public synchronized void receivedFrame(Frame frame)
	{
		super.receivedFrame(frame);
		this.receivedFrame((MWACFrame) frame);
	}

	/** allows to an object to receive a message
	 * @param frame the received ASTRO_Frame 
	 * Algo 3
	 */
	public synchronized void receivedFrame(MWACFrame frame)
	{
		if( (frame.getReceiver()==MWACFrame.BROADCAST) || (frame.getReceiver()==this.getUserId()) || ((frame.getReceiver()==MWACFrame.BROADCAST_LINK) && this.getRole()==this.roleLINK) || ((frame.getReceiver()==MWACFrame.BROADCAST_REPRESENTATIVE) && this.getRole()==this.roleREPRESENTATIVE)) this.receivedMessageQueue.push(frame);
	}

	/**
	 * compute the score of this agent
	 * @return the score of the agent
	 */
	private int score()
	{
		return (int)(this.pourcentOfAvailableEnergy()*this.pourcentOfAvailableEnergy()*this.neighboorlist.size());
	}

	/**
	 * change the role (notify the event, change the color)
	 * @param role the new role of the agent
	 */
	private void setRole(byte role)
	{
		if(this.role!=role)
		{
			this.role=role;
			super.notifyEvent(new RoleModificationEvent(getSystemId(),role));
			switch(role)
			{
			case MWACAgent.roleNOTHING: super.setColor(colorNOTHING); break;
			case MWACAgent.roleSIMPLEMEMBER:super.setColor(colorSIMPLEMEMBER); break;
			case MWACAgent.roleLINK:super.setColor(colorLINK); break;
			case MWACAgent.roleREPRESENTATIVE:super.setColor(colorREPRESENTATIVE); break;
			default: super.setColor(Color.BLACK);
			}
		}
	}

	/**
	 * A message bas been received by the final receiver. Generally this message is sended by a REPRESENTATIVE to one of its member
	 */
	public void receiveMessage(MWACMessage_Data msg)
	{
		super.notifyEvent(new ReceivedMessageEvent(this.getSystemId(),msg));
		System.out.println("\n"+this.getUserId()+" a recu le message "+((MWACMessage_Data)msg).getMsg());
	}


	public void receivedCheckRoute(MWACMessage_CheckRouteReply msg)
	{
		System.out.println(this.getUserId()+" RECOIT LA REPONSE A SON CONTROLE DE ROUTE "+msg);
	}


	/**
	 * send a message (reserved to a representative member)
	 * @param msg the message to send
	 */
	private void sendMessage(MWACMessage_Data msg)
	{
		if(this.neighboorlist.contains(msg.getReceiver()))
			this.sendFrame(new MWACFrame(getUserId(),msg.getReceiver(),msg));
		else
		{
			short idRequest=this.messageToTransmitQueue.add(msg);
			this.networkPartialKnowledgeManager.addRouteRequestAndReceiverAssociation(idRequest, msg.getReceiver());
			this.sendFrame(new MWACFrame(getUserId(),MWACFrame.BROADCAST_LINK,new MWACMessage_RouteRequest(getUserId(),msg.getReceiver(),idRequest)));
		}
	}


	/**
	 * send a message. This method can be called by all agents (REPRESENTATIVE, LINK, SIMPLE_MEMBER)
	 * @param s the string of the message
	 * @param receiver the receiver of this message
	 */
	public void sendMessage(int receiver,String s)
	{

		MWACMessage_Data msg = new MWACMessage_Data(this.getUserId(),receiver,s);

		if(this.neighboorlist.contains(receiver))
		{
			System.out.println("\n("+this.getUserId()+") Le destinataire est un de mes voisins");
			super.notifyEvent(new SendedMessageEvent(this.getSystemId(),(Message)msg.clone()));
			this.sendDataFrame(receiver,msg);
		}
		else
		{
			System.out.println("\n("+this.getUserId()+")Le destinataire N'est PAS un de mes voisins");
			switch(this.role)
			{
			case MWACAgent.roleNOTHING:
			case MWACAgent.roleSIMPLEMEMBER:
			case MWACAgent.roleLINK:
				ArrayList<Integer> lst=this.neighboorlist.getRepresentativeIdentifiers();
				if(lst==null) 
					super.notifyEvent(new MessageNotTransmittedEvent(this.getSystemId(),msg,"Representative neighboor not found"));
				else
				{
					super.notifyEvent(new SendedMessageEvent(this.getSystemId(),(Message)msg.clone()));
					this.sendDataFrame(lst.get(0),msg);
				}
				break;
			case MWACAgent.roleREPRESENTATIVE:
				System.out.println("\nREPRESENTANT "+this.getUserId());
				super.notifyEvent(new SendedMessageEvent(this.getSystemId(),(Message)msg.clone()));
				this.sendMessage(msg);
				break;
			}
		}
	}

	/**
	 * send a data frame. Used by non representative agent to give the data to their representative agent
	 * @param dest receiver of the frame (next hop)
	 * @param msg the MWAC data message
	 */
	private void sendDataFrame(int dest,MWACMessage_Data msg)
	{
		if(MWACAgent.DEBUG) debug(getUserId()+">>> Envoie d'un message de DONNEES");
		this.sendFrame(new MWACFrame(getUserId(),dest,msg));
	}

	/**
	 * send a conflict resolution message
	 * this message is send by a representative agent in conflict with one or more others representative agent
	 */
	private void sendConflictResolution() 
	{
		if(MWACAgent.DEBUG) debug(getUserId()+">>> Envoie par d'un message de RESOLUTION DE CONFLIT");
		this.sendFrame(new MWACFrame(getUserId(),MWACFrame.BROADCAST,new MWACMessage_ConflictResolution(getUserId(),MWACMessage.BROADCAST,this.score())));
	}

	/**
	 * send an possible organizational incoherence notification message
	 * @param v the suspected groups
	 */
	private void sendIncoherenceNotification(Vector<Integer> v)
	{
		int[] array = new int[v.size()];
		for(int i=0;i<v.size();i++) array[i]=v.get(i);

		if(MWACAgent.DEBUG) debug(">>> Envoie par d'un message de suspission d'incoherence organisationnelle     <"+getUserId()+","+MWACAgent.roleToString(this.role)+","+this.getGroup()+">");
		this.sendFrame(new MWACFrame(getUserId(),MWACFrame.BROADCAST,new MWACMessage_PossibleOrganizationalIncoherence(getUserId(),array)));

	}
	/**
	 * Procedure of verification of organizationnal incoherence
	 */
	private void incoherenceNotificationProcedure()
	{
		//System.out.println("* TEST INCOHERENCE (role="+MWACAgent.roleToString(this.getRole())+",id="+this.getId()+",groups="+MWACRouteAssistant.routeToString(this.getGroups())+").");
		Vector<Integer> v = this.neighboorlist.suspiciousNeighboorsGroups(this.getGroups());
		if (!v.isEmpty()) 
		{
			//debug("DETECTION POSSIBLE INCOHERENCE SIGNALEE PAR "+this.getId()+" "+v);
			System.out.println("\nDETECTION POSSIBLE INCOHERENCE SIGNALEE PAR <"+this.getUserId()+","+MWACAgent.roleToString(this.getRole())+","+MWACRouteAssistant.routeToString(this.getGroups())+"). Les groupes suspects sont "+v+"\n"+this.neighboorlist.toString());
			this.sendIncoherenceNotification(v);
		}
	}

	/**
	 * send a presentation message
	 */
	private void sendPresentation()
	{
		//Frame f=new Frame(getId(),dest,new ASTRO_Presentation(getId(),dest,role,group));
		if(MWACAgent.DEBUG) debug(">>> Envoie par d'un message de PRESENTATION     <"+getUserId()+","+MWACAgent.roleToString(this.role)+","+this.getGroup()+">");
		this.sendFrame(new MWACFrame(getUserId(),MWACFrame.BROADCAST,new MWACMessage_Presentation(getUserId(),this.role,this.getGroups())));
	}

	/**
	 * send an introduction message
	 */
	private void sendIntroduction()
	{
		if(MWACAgent.DEBUG) debug(">>> Envoie par d'un message d'INTRODUCTION      <"+getUserId()+","+MWACAgent.roleToString(this.role)+","+this.getGroup()+">    instance of rec?"+(this instanceof RecursiveAgent));
		this.sendFrame(new MWACFrame(super.getUserId(),MWACFrame.BROADCAST,new MWACMessage_Introduction(this.getUserId())));
	}

	/**
	 * send a message to request the close neighboors
	 */
	private void sendWhoAreMyNeighboors()
	{
		//Frame f=new Frame(getId(),dest,new ASTRO_Presentation(getId(),dest,role,group));
		if(MWACAgent.DEBUG) debug(">>> Envoie par d'un message de WHOAREMYNEIGHBOORS   <"+getUserId()+","+MWACAgent.roleToString(this.role)+","+this.getGroup()+">");
		this.sendFrame(new MWACFrame(getUserId(),MWACFrame.BROADCAST,new MWACMessage_WhoAreMyNeighboors(getUserId(),role,this.getGroups())));
	}

	/**
	 * return the role
	 * @return the role
	 */
	public byte getRole() 
	{
		return this.role;
	}





	/** verify that a route exists to join a specified agent
	 * @param id identfier of the agent which must be joined
	 */
	public void checkRouteTo(int id)
	{
		if(this.getRole()==MWACAgent.roleREPRESENTATIVE)
		{	
			int[] route=this.networkPartialKnowledgeManager.getDataMessageRoute(MWACAgent.WORKSTATION_ID);
			if (!(route==null || route.length==0)) this.checkRouteTo(id, route);
		}
		else
			this.checkRouteTo(id,new int[0]);
	}
	/** verify that a route exists to join a specified agent
	 * @param id identfier of the agent which must be joined
	 */
	public void checkRouteTo(int id,int[] route)
	{
		if(this.role==MWACAgent.roleREPRESENTATIVE)
		{
			int relais =-1;
			if(route.length>0) relais=this.neighboorlist.getLinkToRepresentant(route[0]);
			if(relais!=MWACRouteAssistant.UNDEFINED_ID) 
			{
				System.out.println("\nCHECK THE ROUTE LAUNCHED BY "+this.getUserId()+" (repr) TO "+id+". Route is "+MWACRouteAssistant.routeToString(route));
				this.sendFrame(new MWACFrame(super.getUserId(),relais,new MWACMessage_CheckRouteRequest(this.getUserId(),id,(short)1 /** identifiant de requete*/,route)));
				return;
			}

		}
		System.out.println("\nCHECK THE ROUTE LAUNCHED BY "+this.getUserId()+" (non repr) TO "+id+". Route is "+MWACRouteAssistant.routeToString(route));
		this.sendFrame(new MWACFrame(super.getUserId(),MWACFrame.BROADCAST_REPRESENTATIVE,new MWACMessage_CheckRouteRequest(this.getUserId(),id,(short)1 /** identifiant de requete*/,route)));
	}

	/** returns the string signature of the agent which will be viewable under the spy windows
	 * @return the string signature of the agent
	 */

	public String toSpyWindows()
	{
		String roleDependantText="";
		if(getRole()==MWACAgent.roleREPRESENTATIVE && this.messageToTransmitQueue!=null) roleDependantText="<BR><BR>"+this.messageToTransmitQueue.toHTML();
		if((getRole()==MWACAgent.roleREPRESENTATIVE || getRole()==MWACAgent.roleLINK) && this.alreadyProcessedRouteRequestManager!=null) roleDependantText+="<BR><BR>"+this.alreadyProcessedRouteRequestManager.toHTML();
		if(getRole()==MWACAgent.roleREPRESENTATIVE && this.networkPartialKnowledgeManager!=null) roleDependantText+="<BR><BR>"+this.networkPartialKnowledgeManager.toHTML();

		return "<HTML>"+"<B>Id</B>="+this.getUserId()+"    <B>Role</B>="+MWACAgent.roleToString(this.getRole())+"    <B>Group</B>="+MWACGroupAssistant.groupsToString(this.getGroups())+"    <B>Energy</B>="+String.format("%.2f",this.pourcentOfAvailableEnergy())+"    <B>Range</B>="+this.getRange()+"<BR><BR>"+this.neighboorlist.toHTML()+roleDependantText+"</HTML>";
	}


	/**
	 * print a debug message
	 * @param s the debug string 
	 */
	private void debug(String s)
	{
		System.out.println(new aDate().getTimeInMillis()+"   "+this.getUserId()+"\t"+s);
	}



	/**
	 * return the string representation of the role
	 * @param role the role 
	 * @return the string representation
	 */
	public static String roleToString(byte role)
	{
		switch(role)
		{
		case MWACAgent.roleREPRESENTATIVE	: return "ROLE_REPRESENTATIVE";
		case MWACAgent.roleLINK				: return "ROLE_LINK";
		case MWACAgent.roleSIMPLEMEMBER		: return "ROLE_SIMPLEMEMBER";
		case MWACAgent.roleNOTHING			: return "ROLE_NOTHING";
		}
		return "ROLE_UNDEFINED";
	}







	/**
	 * 
	 * @author JPeG
	 *
	 */
	protected class InteractionContext
	{
		public int debugPerdu;

		// Frame to compute
		public MWACFrame frame;

		// memorize if we have lost our representative role
		public boolean lostRepresentativeRole;	
		// memorize if we have previously lost our representative role
		public boolean hasPreviouslyLostRepresentativeRole;
		// memorize if there is a role conflict
		public boolean roleConfict;

		// memorize if it is necessary to send a message WhoAreMyNeighoors
		public boolean mustSendWhoAreMyNeighoors; 
		// memorize if it is necessary to send a message SendAPresentation
		public boolean mustSendAPresentation;	
		// reinitialization of the role
		public boolean reinitializationOfTheRole;
		/**
		 * default constructor
		 */
		public InteractionContext()
		{
			this.init();
		}

		/**
		 * Initialize all value to the default value
		 */
		public void init()
		{
			this.debugPerdu =-1;

			this.frame=null;

			this.mustSendWhoAreMyNeighoors=false; 
			this.mustSendAPresentation=false;	
			this.lostRepresentativeRole=false;	
			this.hasPreviouslyLostRepresentativeRole=false;
			this.roleConfict=false;
			this.reinitializationOfTheRole=false;
		}
	}







	private  String last ="";
	public String toStringASupprimer()
	{
		String now = "Agent #"+this.getUserId()+" , role="+this.getRole()+" :  voisinage="+this.neighboorlist.toString();
		if(now.equals(this.last)) 
			return "";
		else
		{
			this.last=now;
			return now;
		}
	}




}
