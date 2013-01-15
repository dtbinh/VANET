package simulation.solutions.custom.TrustedMWAC;
import java.awt.Color;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import simulation.entities.Agent;
import simulation.events.system.MessageNotTransmittedEvent;
import simulation.events.system.ReceivedMessageEvent;
import simulation.events.system.RoleModificationEvent;
import simulation.events.system.SendedMessageEvent;
import simulation.events.system.UsurpationDetectionEvent;
import simulation.messages.Frame;
import simulation.messages.Message;
import simulation.messages.ObjectAbleToSendMessageInterface;
import simulation.multiagentSystem.MAS;
import simulation.solutions.custom.TrustedMWAC.Messages.MWACFrame;
import simulation.solutions.custom.TrustedMWAC.Messages.MWACMessage;
import simulation.solutions.custom.TrustedMWAC.Messages.MWACMessage_CheckRouteReply;
import simulation.solutions.custom.TrustedMWAC.Messages.MWACMessage_CheckRouteRequest;
import simulation.solutions.custom.TrustedMWAC.Messages.MWACMessage_ConflictResolution;
import simulation.solutions.custom.TrustedMWAC.Messages.MWACMessage_Data;
import simulation.solutions.custom.TrustedMWAC.Messages.MWACMessage_Introduction;
import simulation.solutions.custom.TrustedMWAC.Messages.MWACMessage_PossibleOrganizationalIncoherence;
import simulation.solutions.custom.TrustedMWAC.Messages.MWACMessage_Presentation;
import simulation.solutions.custom.TrustedMWAC.Messages.MWACMessage_QueryGroupMembers;
import simulation.solutions.custom.TrustedMWAC.Messages.MWACMessage_QueryGroupMembersReply;
import simulation.solutions.custom.TrustedMWAC.Messages.MWACMessage_RouteReply;
import simulation.solutions.custom.TrustedMWAC.Messages.MWACMessage_RouteRequest;
import simulation.solutions.custom.TrustedMWAC.Messages.MWACMessage_RoutedData;
import simulation.solutions.custom.TrustedMWAC.Messages.MWACMessage_TTLRouteRequest;
import simulation.solutions.custom.TrustedMWAC.Messages.MWACMessage_WhoAreMyNeighboors;
import simulation.utils.aDate;

/* ALGORITHM 2 of the EUMAS paper
 *
 * public boolean isConfidentNeighboorhood()
 * voir aussi TrustedMWACNeighboorList -> isConfident();
 * voir aussi TrustManager -> TETA1
 */

/* ALGORITHM 3 of EUMAS paper
 * public synchronized void receivedFrame(MWACFrame frame)
 */

/**
 * This class model our solution based on the MWAC model
 * @author Jean-Paul Jamont
 */
public class TrustedMWACAgent extends Agent implements ObjectAbleToSendMessageInterface{

	private static final int QUERY_MEMBERS_REPLY_TIMEOUT = 5000;

	/** Define the workstation id */
	private static int WORKSTATION_ID = 1;

	/** enables organizational incoherence verification */
	private static final boolean ENABLE_ORGANIZATIONAL_INCOHERENCE_VERIFICATION = false;
	/** enables debug print out */
	private static final boolean DEBUG = true;

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
	/** backup_mode role */
	public  final static byte roleBACKUP_MODE			=4;
	/** color of no role members */
	public  final static Color colorNOTHING 			= Color.LIGHT_GRAY;
	/** color of representative members */
	public  final static Color colorREPRESENTATIVE 		= Color.RED;
	/** color of link members */
	public  final static Color colorLINK 				= Color.GREEN;
	/** color of simple member */
	public  final static Color colorSIMPLEMEMBER 		= Color.YELLOW;
	/** color of simple member */
	public  final static Color colorBACKUPMODE 			= Color.PINK;




	/** member of non group */
	private static int groupNONE = -1;

	/** role of the agent */
	private byte role;
	/** received message queue */
	private FrameFIFOStack receivedMessageQueue;
	/** message to send (if the agent is Representative*/
	private MWACMessageFIFOStack messageToTransmitQueue;
	/** neighboorList */
	private TrustedMWACNeighboorList neighboorlist;
	/** to manage already processed route request */
	private MWACAlreadyProcessedRouteRequestManager alreadyProcessedRouteRequestManager;
	/** MWAC route fragment  manager */
	private MWACNetworkPartialKnowledgeManager networkPartialKnowledgeManager;
	/** MWAC incoherence possible detection manager */ 
	private MWACIncoherenceList organizationalIncoherenceManager;
	/** MWAC trust manager*/ 
	private TrustManager trustManager;



	private boolean isUsurper = false;
	private boolean mustUsurpsWorkstationID = false;
	private boolean mustUsurpsNeighboorID = false;

	// (Anca)
	private boolean mustUsurpGroups = false; // does not include all groups in the presentation message
	private boolean mustUsurpLink = false; // claims it is a link to a new group

	// (Anca)
	private int[] claimedGroups;
	private byte claimedRole;


	// (Anca) -- id suspected to be link usurpator
	private int suspectedId = -1;

	// (Anca) -- list of replies used for checking if the suspectedId is malicious 
	private List<Boolean> queryMemberReplies = new LinkedList<Boolean>();

	// (Anca)
	private aDate possibleLinkUsurpationDate = null;

	private int realId;


	/** Memorize the energy level when the agent has been elected representative */
	private float pourcentOfAvailableEnergyWhenElectedRepresentative;

	/**
	 * Constructor
	 * @param mas the multiagent system
	 * @param id identifier of the agent
	 * @param energy energy of the agent
	 * @param range range of the agent
	 */
	public TrustedMWACAgent(MAS mas,Integer  id, Float energy,Integer  range)
	{
		super(mas,id,range);
		this.role=roleNOTHING;
		this.receivedMessageQueue=new FrameFIFOStack();
		this.neighboorlist=new TrustedMWACNeighboorList();
		this.messageToTransmitQueue=null;	//R
		this.alreadyProcessedRouteRequestManager=null;
		this.networkPartialKnowledgeManager=null;
		this.organizationalIncoherenceManager=null;
		this.pourcentOfAvailableEnergyWhenElectedRepresentative=0;
		this.trustManager=new TrustManager(this);
		this.realId = id;

	}

	/**  Launched by the call of start method*/
	public void run()
	{

		//this.trustManager.setManagedAgent(this);
		System.out.println("Démarrage du MWACAgent "+this.getUserId());

		try{Thread.sleep(2500);}catch(Exception e){}

		if (TrustedMWACAgent.DEBUG) debug("Envoie un message d'introduction");
		sendIntroduction();
		try{Thread.sleep(2*SLEEP_TIME_SLOT);}catch(Exception e){};

		// Context of the interaction (messages process)
		InteractionContext interaction = new InteractionContext();

		// MWAC LAYER
		while(!isKilling() && !isStopping())
		{


			// Pause
			//try{Thread.sleep(SLEEP_TIME_SLOT+this.extraWaitTime(this.role));}catch(Exception e){};
			this.sleep(SLEEP_TIME_SLOT+this.extraWaitTime(this.role));

			// Preparation of the others threads
			while(  ((isSuspending()) && (!isKilling() && !isStopping()))) try{Thread.sleep(SLEEP_TIME_SLOT);}catch(Exception e){};




			// Attente de messages
			while( this.receivedMessageQueue.isEmpty() && (!interaction.lostRepresentativeRole) && (!interaction.reinitializationOfTheRole)) 
			{
				if (TrustedMWACAgent.ENABLE_ORGANIZATIONAL_INCOHERENCE_VERIFICATION)
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
					if (this.role==TrustedMWACAgent.roleREPRESENTATIVE)
					{
						int idOfAProblematicGroup = this.organizationalIncoherenceManager.existsProblem();
						if (idOfAProblematicGroup!=MWACIncoherenceList.NO_REAL_INCOHERENCE)
						{
							if (this.getUserId()>idOfAProblematicGroup)
							{
								System.out.println("REAL PROBLEM catch by "+this.getUserId()+" with group "+idOfAProblematicGroup+"\n"+this.organizationalIncoherenceManager.toString());

								// leave its role
								interaction.lostRepresentativeRole=true;
							}
							else
								System.out.println("IGNORE REAL PROBLEM catch by "+this.getUserId()+" with group "+idOfAProblematicGroup+"\n"+this.organizationalIncoherenceManager.toString());
						}
					}
				}


				// Is it the time to leave its representative mandat?
				if (this.pourcentOfAvailableEnergy()<(this.pourcentOfAvailableEnergyWhenElectedRepresentative/2.0)) interaction.lostRepresentativeRole=true;


				// Malicious behavior
				//this.usurpsCounter++;
				//if(this.usurpsCounter==400 && this.getUserId()==101) this.sendMessage(1,"COUCOU DUDULE");
				//if(this.usurpsCounter==500 && this.getUserId()==270) this.checkRouteTo(1);

				/** (Anca) **/					
				if (this.mustUsurpsWorkstationID || this.mustUsurpsNeighboorID || this.mustUsurpGroups || this.mustUsurpLink)
				{
					if( !this.isUsurper)
					{
						if (this.mustUsurpsWorkstationID) 
						{
							// we must adopt a malicious behavior
							this.isUsurper=true;

							this.setUserId(TrustedMWACAgent.WORKSTATION_ID);														
							interaction.reinitializationOfTheRole = true;

							System.out.println("\n--------> #"+this.realId+" USURPE LA STATION DE TRAVAIL  #" + this.getUserId());
						}
						else if(this.mustUsurpsNeighboorID)
						{
							if(this.neighboorlist.size()>0)
							{
								// we must adopt a malicious behavior
								this.isUsurper=true;

								this.setUserId(this.neighboorlist.neighboorList.get(0).id);															
								interaction.reinitializationOfTheRole = true;


								System.out.println("\n--------> #"+this.realId+" USURPE SON VOISIN  #" + this.getUserId());
							}
							else
							{
								System.out.println("\n--------> #"+this.realId+" NE PEUT PAS USURPER UN VOISIN CAR IL N EN A PAS");
							}
						}
						/**
						 * (Anca) The case of GROUP USURPATION
						 */
						else if(this.mustUsurpGroups)
						{
							if(!this.neighboorlist.isEmpty())
							{
								this.isUsurper = true;
								//interaction.mustSendAPresentation = true;
								sendPresentation();

								System.out.println("\n----------> #" + this.getUserId() + " OMITS THE GROUP: " + this.getGroups()[0]);
							}
							else
							{ 
								System.out.println("\n----------> #" + this.getUserId() + "Can't usurp groups, he doesn't belong to any groups...");
							}
							/**
							 * (Anca) The case of LINK to NEW GROUP USURPATION	
							 */							
						} else if (this.mustUsurpLink){

							if(!this.neighboorlist.isEmpty() && role == roleLINK){
								this.isUsurper = true;

								possibleLinkUsurpationDate = new aDate();
								sendPresentation();
								System.out.println("\n----------> #" + this.getUserId() + " LINK TO A NEW GROUP" );
							}
							else{
								System.out.println("\n----------> #" + this.getUserId() + " CAN'T harm the network. No neighbors...");
							}
						} 
						/**/

					}
				}
				else if(this.isUsurper)
				{
					// we must stop the malicious behavior
					this.isUsurper=false;
					this.setUserId(this.realId);
					interaction.reinitializationOfTheRole=true;
					System.out.println("\n--------> #"+this.realId+" ARRETE SON USURPATION #"+this.getUserId());
				}


				//try{Thread.sleep(SLEEP_TIME_SLOT);}catch(Exception e){};
				this.sleep(SLEEP_TIME_SLOT);
			}





			// Process waiting messages (if we have received some messages)
			while((interaction.frame=this.receivedMessageQueue.pop())!=null) if (!interaction.lostRepresentativeRole) this.processMessage(interaction);

			if(interaction.reinitializationOfTheRole)
			{
				//this.setRole(this.roleNOTHING);
				this.neighboorlist=new TrustedMWACNeighboorList();
			}

			// Verify the role
			this.computeRoleDecision(interaction);


			/************************************** 
			 *	(Anca) Treat LINK USURPATION CASE  
			 */

			if (suspectedId > 0 && possibleLinkUsurpationDate != null) {

				float confidence = 1.0f;
				if( neighboorlist.get(suspectedId) != null)
					confidence = neighboorlist.get(suspectedId).trust;


				aDate now = new aDate();
				if (now.differenceToMS(possibleLinkUsurpationDate) > QUERY_MEMBERS_REPLY_TIMEOUT) {
					if (!queryMemberReplies.isEmpty()) {

						// inconsistent replies
						if (queryMemberReplies.contains(false)	&& queryMemberReplies.contains(true))
							confidence -= TrustManager.ALPHA;

						// all replies stating that suspectedId is NOT in the group
						else if (queryMemberReplies.contains(false)	&& !queryMemberReplies.contains(true))
							confidence -= TrustManager.BETA;
					}

					neighboorlist.put(suspectedId, confidence, roleNOTHING, groupNONE);

					// reinitialization
					possibleLinkUsurpationDate = null;
					queryMemberReplies.clear();
					suspectedId = -1;

				}
			}
			/** (/Anca) link usurpation **************/

			// Action associated to the decision to take into account the  interaction/organization modification
			if(interaction.roleConfict)
			{
				if(TrustedMWACAgent.DEBUG) debug("Je suis en conflit avec "+this.neighboorlist.getRepresentativeIdentifiers());
				this.sendConflictResolution();
			}
			else if(interaction.mustSendWhoAreMyNeighoors)
				this.sendWhoAreMyNeighboors();
			else if(interaction.mustSendAPresentation)
				this.sendPresentation();


			interaction.init();

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
			newRole=TrustedMWACAgent.roleNOTHING;
		else
		{

			if (!this.isConfidentNeighboorhood())
			{

			}
			else
				switch(this.role)
				{
				case TrustedMWACAgent.roleBACKUP_MODE:
					// here the neighboorhood is trusted : we change our role
				case TrustedMWACAgent.roleNOTHING:
				case TrustedMWACAgent.roleSIMPLEMEMBER:
				{
					int n = this.neighboorlist.getNbRepresentative();
					if(n==0) 
						newRole=TrustedMWACAgent.roleREPRESENTATIVE;
					else if(n==1) 
					{
						newRole=TrustedMWACAgent.roleSIMPLEMEMBER;
						if(interaction.hasPreviouslyLostRepresentativeRole) newRole=this.role;
					}
					else newRole=TrustedMWACAgent.roleLINK;
				}
				break; 
				case TrustedMWACAgent.roleLINK:
				{
					int n = this.neighboorlist.getNbRepresentative();
					if(n==0) newRole=TrustedMWACAgent.roleREPRESENTATIVE;
					else if(n==1) newRole=TrustedMWACAgent.roleSIMPLEMEMBER;
					else newRole=TrustedMWACAgent.roleLINK;
				}
				break;
				case TrustedMWACAgent.roleREPRESENTATIVE:
				{
					if(interaction.lostRepresentativeRole||interaction.hasPreviouslyLostRepresentativeRole)
					{
						newRole=TrustedMWACAgent.roleNOTHING;
						interaction.roleConfict=false;	// Il n'y a plus de conflit
					}
					else
					{
						if(this.neighboorlist.getNbRepresentative()>0) interaction.roleConfict=true;
						newRole=TrustedMWACAgent.roleREPRESENTATIVE;
					}
				}
				break;
				}
		}



		// Initialize data and 
		if (this.role!=newRole)
		{
			if(TrustedMWACAgent.DEBUG) debug("Je change de role "+TrustedMWACAgent.roleToString(this.role)+" => "+TrustedMWACAgent.roleToString(newRole)+" lost="+interaction.lostRepresentativeRole+"  prevLost="+interaction.hasPreviouslyLostRepresentativeRole);
			this.setRole(newRole);
			switch(newRole)
			{
			case TrustedMWACAgent.roleREPRESENTATIVE:
				this.pourcentOfAvailableEnergyWhenElectedRepresentative=this.pourcentOfAvailableEnergy();
				this.alreadyProcessedRouteRequestManager=new MWACAlreadyProcessedRouteRequestManager();
				this.messageToTransmitQueue=new MWACMessageFIFOStack();
				this.networkPartialKnowledgeManager=new MWACNetworkPartialKnowledgeManager();
				this.organizationalIncoherenceManager=new MWACIncoherenceList();
				interaction.mustSendWhoAreMyNeighoors=true;
				break;
			case TrustedMWACAgent.roleLINK:
				this.pourcentOfAvailableEnergyWhenElectedRepresentative=0;
				this.alreadyProcessedRouteRequestManager=new MWACAlreadyProcessedRouteRequestManager();
				this.networkPartialKnowledgeManager=null;
				this.messageToTransmitQueue=null;
				this.organizationalIncoherenceManager=null;
				interaction.mustSendAPresentation=true;
				break;
			case TrustedMWACAgent.roleNOTHING:
				this.pourcentOfAvailableEnergyWhenElectedRepresentative=0;
				this.alreadyProcessedRouteRequestManager=null;
				this.networkPartialKnowledgeManager=null;
				this.messageToTransmitQueue=null;
				this.organizationalIncoherenceManager=null;
				this.neighboorlist=new TrustedMWACNeighboorList();
				interaction.mustSendAPresentation=false;
				this.sendIntroduction();
				this.sleep(250);	// A priori grosse modification... On prend le temps de laisser les choses se stabiliser
				break;
			case TrustedMWACAgent.roleBACKUP_MODE:
				// we have the same structure than a representative agent
			case TrustedMWACAgent.roleSIMPLEMEMBER:
				this.alreadyProcessedRouteRequestManager=null;
				this.networkPartialKnowledgeManager=null;
				this.messageToTransmitQueue=null;
				this.organizationalIncoherenceManager=null;
				interaction.mustSendAPresentation=true;
				break;
			}
			if (TrustedMWACAgent.DEBUG) {
				if (newRole==TrustedMWACAgent.roleREPRESENTATIVE)
					this.debug("Je deviens ROLE_REPRESENTANT");
			}
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
			float confidence = 1.0f;

			// (Anca) Trust value should be updated from the current value, if it exists 
			if(neighboorlist.contains(msg.getSender()))
				confidence = neighboorlist.get(msg.getSender()).trust;


			if(msg.getSender()==TrustedMWACAgent.WORKSTATION_ID && this.neighboorlist.isStable(5*1000))
			{
				System.out.println("\nxxxxxxxx ("+this.getUserId()+") Un message d'introduction envoyé par la workstation alors que la config était stable... louche! ... Je vérifie!");

				// (Anca)
				//confidence=TrustManager.TETA1;
				if(confidence > TrustManager.ETA)
					confidence -= TrustManager.ETA;
				else
					confidence = 0.0f;

				this.checkRouteTo(TrustedMWACAgent.WORKSTATION_ID);
			}

			this.neighboorlist.put(msg.getSender(),confidence,TrustedMWACAgent.roleNOTHING, TrustedMWACAgent.groupNONE);
			interaction.mustSendWhoAreMyNeighoors=true;
		}
		break;
		case MWACMessage.msgWHO_ARE_MY_NEIGHBOORS:
		case MWACMessage.msgPRESENTATION:
		{
			float confidence = 1.0f;

			// (Anca) Trust value should be updated from the current value, if it exists in the neighbor list 
			if(neighboorlist.contains(msg.getSender()))
				confidence = neighboorlist.get(msg.getSender()).trust;

			if(msg.getSender() == TrustedMWACAgent.WORKSTATION_ID)
			{
				if(this.getRole()!=TrustedMWACAgent.roleREPRESENTATIVE && this.neighboorlist.isStable(5*1000))
				{
					System.out.println("\n!!!!!!!!!!!! ("+this.getUserId()+") Un message de PRESENTATION envoyé par la workstation alors que la config était stable... louche! ... Je vérifie!");

					// (Anca)
					//confidence=TrustManager.TETA1;
					if(confidence > TrustManager.ETA)
						confidence -= TrustManager.ETA;
					else
						confidence = 0.0f;

					this.checkRouteTo(TrustedMWACAgent.WORKSTATION_ID);
				}
				else if(this.getRole() == TrustedMWACAgent.roleREPRESENTATIVE  && (msg.getType() == MWACMessage.msgWHO_ARE_MY_NEIGHBOORS) && (this.networkPartialKnowledgeManager.getDataMessageRoute(TrustedMWACAgent.WORKSTATION_ID)!=null))
				{
					System.out.println("Route de "+this.getUserId()+" a "+msg.getSender()+" est " + MWACRouteAssistant.routeToString(this.networkPartialKnowledgeManager.getDataMessageRoute(TrustedMWACAgent.WORKSTATION_ID)));
					if(this.networkPartialKnowledgeManager.getDataMessageRoute(TrustedMWACAgent.WORKSTATION_ID).length>1)
					{
						confidence = TrustManager.TETA1-0.01f;
						this.notifyEvent(new UsurpationDetectionEvent(this.getSystemId()));
						this.checkRouteTo(TrustedMWACAgent.WORKSTATION_ID);
					}
				}
			} 

			/**
			 * (Anca) 1. GROUP USURPATION 
			 */

			if (!checkMyGroup((MWACMessage_Presentation) msg)) {
				if (this.role == roleREPRESENTATIVE && neighboorlist.isStable(5 * 1000)) {


					System.out.println(getUserId() + ": My group is not included in this presentation message, though I am the REPRESENTATIVE agent of the group and config is stable " + MWACGroupAssistant.groupsToString(((MWACMessage_Presentation)msg).getGroups()));
					confidence = 0.0f; // usurpation detected

					this.notifyEvent(new UsurpationDetectionEvent(this.getSystemId()));
				}
			}			

			/**
			 * (Anca) 2. LINK USURPATION
			 */

			int[] unknownGroups = getUnknownGroups((MWACMessage_Presentation)msg);
			if(unknownGroups != null){

				// only for the first unknown group
				if(neighboorlist.isStable(5 * 1000)){
					System.out.println(getUserId() + ": I don't know this group: " + unknownGroups[0]);

					// tries to check if the group actually exists
					sendQueryForMembers(unknownGroups[0]); 
				}

			}else{
				// OK: NO NEW GROUPS IN THIS MESSAGE
			} /** (/Anca) */

			this.neighboorlist.put(msg.getSender(),confidence,((MWACMessage_Presentation)msg).getRole(),  ((MWACMessage_Presentation)msg).getClonedGroupArray());

			interaction.mustSendAPresentation = interaction.mustSendAPresentation	|| (msg.getType()==MWACMessage.msgWHO_ARE_MY_NEIGHBOORS)
			|| this.neighboorlist.put(msg.getSender(),((MWACMessage_Presentation)msg).getRole(), ((MWACMessage_Presentation)msg).getClonedGroupArray());


		}
		break;
		case MWACMessage.msgCONFLICT_RESOLUTION:
		{
			this.neighboorlist.put(msg.getSender(), TrustedMWACAgent.roleREPRESENTATIVE, msg.getSender());
			if(this.role==TrustedMWACAgent.roleREPRESENTATIVE && !interaction.lostRepresentativeRole)
			{
				int scoreInMsg = ((MWACMessage_ConflictResolution)msg).getScore();
				if((scoreInMsg>this.score()) || ((scoreInMsg==this.score()) && this.getUserId()<msg.getSender()))
				{
					if(TrustedMWACAgent.DEBUG) debug("*PERDU* contre "+msg.getSender());
					interaction.lostRepresentativeRole=true;
					interaction.mustSendAPresentation=true;
					interaction.debugPerdu=msg.getSender();
				}
				else
				{
					if(TrustedMWACAgent.DEBUG) debug("*GAGNE* contre "+msg.getSender());
					this.neighboorlist.put(msg.getSender(),TrustedMWACAgent.roleNOTHING,TrustedMWACAgent.groupNONE);
					if(TrustedMWACAgent.DEBUG) debug("Je montre que je gagne mon conflit");
					this.sendConflictResolution();
					interaction.mustSendAPresentation=false;
				}
			}
			else
			{
				// I have already lost my reprentative role against with another agent
				if (interaction.lostRepresentativeRole) if(TrustedMWACAgent.DEBUG) debug("*DEJA PERDU* contre "+interaction.debugPerdu+" bien avant "+msg.getSender()+"    role="+this.role+"  lost="+interaction.lostRepresentativeRole);
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
				else if(this.role==TrustedMWACAgent.roleREPRESENTATIVE)
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
			if(this.getRole()==TrustedMWACAgent.roleREPRESENTATIVE)
			{
				TrustedTripletIdRoleGroup triplet=this.neighboorlist.get(tMsg.getReceiver());
				if (triplet!=null || tMsg.getReceiver()==this.getUserId())
				{
					if(!alreadyProcessedRouteRequestManager.isAlreadyProcessed(tMsg.getSender(), tMsg.getIdRequest()))
					{
						// Est-til un voisin?
						System.out.println("REPRESENTANT #"+this.getUserId()+" VA REPONDRE PAR UN ROUTE REPLY A LA RECHERCHE");
						int relais = this.neighboorlist.getLinkToRepresentant(MWACRouteAssistant.getLastId(tMsg.getRoute()));
						if (relais==MWACRouteAssistant.UNDEFINED_ID) relais=this.neighboorlist.getLinkToRepresentant(tMsg.getSender());
						if (relais==TrustedMWACNeighboorList.UNDEFINED_ID) System.out.println("!!!!!! (1) Moi repr "+this.getUserId()+" ne trouve pas le précédent de "+interaction.frame.getSender()+" dans la route "+MWACRouteAssistant.routeToString(tMsg.getRoute())+" (pour un ROUTE REPLY)");
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
			else if(this.getRole()==TrustedMWACAgent.roleLINK)
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

				if(this.getRole()==TrustedMWACAgent.roleREPRESENTATIVE)
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
						if (relais==TrustedMWACNeighboorList.UNDEFINED_ID) 
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
				else if(this.getRole()==TrustedMWACAgent.roleLINK)
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
				else if(this.getRole()==TrustedMWACAgent.roleREPRESENTATIVE)
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
						if (relais==TrustedMWACNeighboorList.UNDEFINED_ID) 
							System.out.println("!!!!!! (3) Moi repr "+this.getUserId()+" ne trouve pas le suivant de "+interaction.frame.getSender()+" dans la route "+MWACRouteAssistant.routeToString(tMsg.getRoute())+" (pour un ROUTED DATA)");
						else
						{
							tMsg.setRoute(MWACRouteAssistant.removeHead(tMsg.getRoute()));
							this.sendFrame(new MWACFrame(this.getUserId(),relais,new MWACMessage_RoutedData(tMsg)));
						}
					}
				}
				else if(this.getRole()==TrustedMWACAgent.roleLINK)
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
			if (TrustedMWACAgent.ENABLE_ORGANIZATIONAL_INCOHERENCE_VERIFICATION)
				if(this.role==TrustedMWACAgent.roleREPRESENTATIVE)
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
							super.sendFrame(new MWACFrame(getUserId(),MWACFrame.BROADCAST_LINK,new MWACMessage_TTLRouteRequest(getUserId(),suspectedGroups[i],this.messageToTransmitQueue.getNextIdRequest(),(byte) TrustedMWACAgent.MAX_HOP_RESEARCH_ORGANIZATIONAL_INCOHERENCE)));
						}						
					}
				}
				else
				{
					// not concerned
				}
			break;

		case MWACMessage.msgCHECK_ROUTE_REQUEST:
			if(interaction.frame.getReceiver()==this.getUserId() || (interaction.frame.getReceiver()==MWACFrame.BROADCAST_REPRESENTATIVE && this.getRole()==TrustedMWACAgent.roleREPRESENTATIVE))
			{
				MWACMessage_CheckRouteRequest tMsg = (MWACMessage_CheckRouteRequest) msg;

				if (tMsg.getReceiver()==this.getUserId() && !this.isUsurper)
				{
					// Recu : il faut traiter!

					//System.out.println("Checkroute bien recu");
					this.sendFrame(new MWACFrame(this.getUserId(),interaction.frame.getSender(),new MWACMessage_CheckRouteReply(tMsg.getReceiver(),tMsg.getSender(),tMsg.getIdRequest(),tMsg.getRoute())));
				}
				else if(this.getRole()==TrustedMWACAgent.roleREPRESENTATIVE)
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
						int[] route_to_dest=this.networkPartialKnowledgeManager.getDataMessageRoute(TrustedMWACAgent.WORKSTATION_ID);
						if(route_to_dest!=null)
						{
							tMsg.setRoute(MWACRouteAssistant.add(route_start,route_to_dest));
							//System.out.println("J'envoie "+new MWACFrame(this.getUserId(),relais,new MWACMessage_CheckRouteRequest(tMsg)));
							//this.sendFrame(new MWACFrame(this.getUserId(),relais,new MWACMessage_CheckRouteRequest(tMsg)));
							int relais = this.neighboorlist.getLinkToRepresentant(MWACRouteAssistant.getNextId(tMsg.getRoute(),this.getUserId()));
							System.out.println("Le suivant de moi "+this.getUserId()+" dans "+MWACRouteAssistant.routeToString(tMsg.getRoute())+" est "+relais);
							if (relais==TrustedMWACNeighboorList.UNDEFINED_ID) 
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
						if (relais==TrustedMWACNeighboorList.UNDEFINED_ID) 
							System.out.println("!!!!!! (3) Moi repr "+this.getUserId()+" ne trouve pas le suivant de "+interaction.frame.getSender()+" dans la route "+MWACRouteAssistant.routeToString(tMsg.getRoute())+" (pour un ROUTED DATA)");
						else
							this.sendFrame(new MWACFrame(this.getUserId(),relais,new MWACMessage_CheckRouteRequest(tMsg)));
					}
				}
				else if(this.getRole()==TrustedMWACAgent.roleLINK)
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

			/** (Anca) */ 
		case MWACMessage.msgQUERY_GROUP_MEMBERS: 

			if(interaction.frame.getReceiver()==this.getUserId()){
				System.out.println(getUserId() + " I have received a query ");				
				if(!isUsurper){
					sendQueryForMembersReply(interaction.frame.getSender());
				}
			}else if(this.role == roleREPRESENTATIVE && !isUsurper){
				MWACMessage_QueryGroupMembers tMsg = (MWACMessage_QueryGroupMembers)msg;
				this.sendQueryForMembers(tMsg.getReceiver());
			}
			break;
		case MWACMessage.msgQUERY_GROUP_MEMBERS_REPLY: // TODO

			MWACMessage_QueryGroupMembersReply tMsg = (MWACMessage_QueryGroupMembersReply)msg;	


			if(interaction.frame.getReceiver() == this.getUserId()){

				if(tMsg.getSender() != suspectedId){
					int[] members = tMsg.getMembers();

					// add reply stating if the suspectedId is in the group
					queryMemberReplies.add(!MWACGroupAssistant.containsGroup(suspectedId, members));
				}
			}else if(role == roleREPRESENTATIVE && !isUsurper){
				// relay REPLY
			}

			break;
			/** (/Anca) */
		default:
			System.out.println("ERREUR!!!! NONE IMPLEMENTED MESSAGE "+msg.toString());
		}	
	}


	/**
	 * (Anca) Returns the groups that were not known before receiving the message
	 * @param msg presentation message
	 * @return array of unknown groups
	 */
	private int[] getUnknownGroups(MWACMessage_Presentation msg) {

		LinkedList<TrustedTripletIdRoleGroup> neighb = neighboorlist.neighboorList;
		Set<Integer> knownGroupsSet = new HashSet<Integer>();

		int[] msgGroups = msg.getClonedGroupArray();
		List<Integer> unknownGroups = new LinkedList<Integer>(); 

		for(TrustedTripletIdRoleGroup triplet : neighb)
			for(int i = 0; i < triplet.groups.length; i++)
				knownGroupsSet.add(triplet.groups[i]);

		for(int i = 0; i < msgGroups.length; i++)
			if(!knownGroupsSet.contains(msgGroups[i]))
				unknownGroups.add(msgGroups[i]);

		if (!unknownGroups.isEmpty()) {
			int[] unk = new int[unknownGroups.size()];
			for (int i = 0; i < unknownGroups.size(); i++)
				unk[i] = unknownGroups.get(i);

			return unk;
		}
		return null;
	}

	/**
	 * (Anca) 
	 * Checks that the agent's group is included in the presentation message
	 * (used by the REPRESENTATIVE agents, to detect usurpation of groups)
	 * 
	 * @param pMsg
	 * @return true if the group is included in the message, false otherwise
	 */
	private boolean checkMyGroup(MWACMessage_Presentation pMsg) {
		int msgGroups[] = pMsg.getClonedGroupArray();
		int myGroup  = getGroup();

		return MWACGroupAssistant.containsGroup(myGroup, msgGroups);			
	} /** (/Anca)*/

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
		if(this.role==TrustedMWACAgent.roleREPRESENTATIVE)
			return this.getUserId();
		else
		{
			ArrayList<Integer> repr=this.neighboorlist.getRepresentativeIdentifiers();
			int nbRepr=repr.size();
			if(nbRepr==0)
			{
				switch(this.role)
				{
				case TrustedMWACAgent.roleBACKUP_MODE:
					// Il faudra en reparler : est-ce qu'on considére qu'on appartient encore a u ngroupe
					// Ici je considere que tant pis on ne pourra plus communiquer
					System.out.println(this.getUserId()+","+TrustedMWACAgent.roleToString(this.role)+"Je refuse d etre representant du coup je suis isoler du reste du réseaux");
					return TrustedMWACAgent.groupNONE;
				case TrustedMWACAgent.roleSIMPLEMEMBER:
				case TrustedMWACAgent.roleLINK:
					System.out.println("<A"+this.getUserId()+","+TrustedMWACAgent.roleToString(this.role)+">!!!!! ERREUR de cohérence entre le role et la liste de voisins");
				case TrustedMWACAgent.roleNOTHING:
					// C'est normal pour eux
					return TrustedMWACAgent.groupNONE;
				}
			}
			else if (nbRepr==1)
			{
				switch(this.role)
				{
				case TrustedMWACAgent.roleBACKUP_MODE:
					// the same than a representative agent
				case TrustedMWACAgent.roleSIMPLEMEMBER:
					return repr.get(0);
				case TrustedMWACAgent.roleLINK:
					System.out.println("<B"+this.getUserId()+","+TrustedMWACAgent.roleToString(this.role)+">!!!!! ERREUR de cohérence entre le role et la liste de voisins");
				case TrustedMWACAgent.roleNOTHING:
					return TrustedMWACAgent.groupNONE;
				}
			}
			else
			{
				if (this.role==TrustedMWACAgent.roleLINK)
					return repr.get(0);
				else if(this.role==TrustedMWACAgent.roleBACKUP_MODE)
					// Même si j'ai plusieurs voisins representant je ne me considere pas comme Link Agent
					// ici il faudrait en fait choisir le representant avec le plus grand indice de confiance
					// on en reparle
					return repr.get(0);
				else
				{
					if (this.role!=TrustedMWACAgent.roleNOTHING) System.out.println("<C"+this.getUserId()+","+TrustedMWACAgent.roleToString(this.role)+">!!!!! ERREUR de cohérence entre le role et la liste de voisins");
					return TrustedMWACAgent.groupNONE;
				}
			}
		}
		System.out.println("<D"+this.getUserId()+","+TrustedMWACAgent.roleToString(this.role)+">!!!!! ERREUR de cohérence entre le role et la liste de voisins");
		return TrustedMWACAgent.groupNONE;
	}

	/**
	 * return the groups identifiers of this agent (interesting for link agent)
	 * @return the groups associated to the agent
	 */
	public int[] getGroups()
	{
		int[] res;

		if(this.getRole()!=TrustedMWACAgent.roleLINK && !(this.getRole()!=TrustedMWACAgent.roleBACKUP_MODE && this.neighboorlist.getNbRepresentative()>1))
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
		if (this.role==TrustedMWACAgent.roleSIMPLEMEMBER) return 3*TrustedMWACAgent.SLEEP_TIME_SLOT;
		if (this.role==TrustedMWACAgent.roleBACKUP_MODE) return 5*TrustedMWACAgent.SLEEP_TIME_SLOT;
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
		if(frame.getSender() == this.getUserId()) 
		{
			this.notifyEvent(new UsurpationDetectionEvent(this.getSystemId()));

			// (Anca)
			this.neighboorlist.put(frame.getSender(),0.0f,TrustedMWACAgent.roleNOTHING, TrustedMWACAgent.groupNONE);

			//this.setRole(roleSIMPLEMEMBER); 		--> it destabilizes the network 

			trustManager.myIdHasBeenUsurped();
		}
		if(this.isUsurper) System.out.println("RECEPTION par l'usurpateur "+this.realId+" de "+(frame.getReceiver()==this.getUserId())+" :"+frame.toString());

		if( (frame.getReceiver()==MWACFrame.BROADCAST) || (frame.getReceiver()==this.getUserId()) || ((frame.getReceiver()==MWACFrame.BROADCAST_LINK) && this.getRole()==TrustedMWACAgent.roleLINK) || ((frame.getReceiver()==MWACFrame.BROADCAST_REPRESENTATIVE) && this.getRole()==TrustedMWACAgent.roleREPRESENTATIVE)) this.receivedMessageQueue.push(frame);
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
			case TrustedMWACAgent.roleBACKUP_MODE: super.setColor(TrustedMWACAgent.colorBACKUPMODE); break;
			case TrustedMWACAgent.roleNOTHING: super.setColor(TrustedMWACAgent.colorNOTHING); break;
			case TrustedMWACAgent.roleSIMPLEMEMBER:super.setColor(TrustedMWACAgent.colorSIMPLEMEMBER); break;
			case TrustedMWACAgent.roleLINK:super.setColor(TrustedMWACAgent.colorLINK); break;
			case TrustedMWACAgent.roleREPRESENTATIVE:super.setColor(TrustedMWACAgent.colorREPRESENTATIVE); break;
			default: super.setColor(Color.white);
			}
		}
	}

	/**
	 * A message bas been received by the final receiver. Generally this message is sended by a REPRESENTATIVE to one of its member
	 */
	public void receiveMessage(MWACMessage_Data msg)
	{
		super.notifyEvent(new ReceivedMessageEvent(this.getSystemId(),msg));
		if(this.isUsurper)
			System.out.println("\nUSURPATEUR "+this.realId+" a USURPE le message "+((MWACMessage_Data)msg).getMsg());
		else
			System.out.println("\n"+this.getUserId()+" a recu le message "+((MWACMessage_Data)msg).getMsg());
	}


	public void receivedCheckRoute(MWACMessage_CheckRouteReply msg)
	{
		System.out.println(this.getUserId()+" RECOIT LA REPONSE A SON CONTROLE DE ROUTE "+msg);
		if(msg.getRoute().length==0)
		{
			System.out.println("Moi "+this.getUserId()+", je ne peux pas conclure sur l'évetuelle usurpation de "+msg.getSender());
		}
		else if(!this.neighboorlist.get(msg.getSender()).inGroup(MWACRouteAssistant.getLastId(msg.getRoute())))
		{
			System.out.println("Moi "+this.getUserId()+", je déclare que "+msg.getSender()+" est un USURPATEUR");
			if (this.neighboorlist.contains(msg.getSender())) 
			{
				float confidence=0;
				this.neighboorlist.put(msg.getSender(),confidence,TrustedMWACAgent.roleNOTHING, TrustedMWACAgent.groupNONE);
			}
		}
		else
		{
			System.out.println("Le test précédent était if(this.neighboorlist.get(msg.getSender()).inGroup(MWACRouteAssistant.getLastId(msg.getRoute()))     msg.getSender()="+msg.getSender()+"   MWACRouteAssistant.getLastId(msg.getRoute())="+MWACRouteAssistant.getLastId(msg.getRoute())+"    route="+msg.getRoute());
			System.out.println("Moi "+this.getUserId()+", je pense que "+msg.getSender()+" n'est pas un usurpateur");

		}
	}

	/**
	 * send a message (reserved to a representative member)
	 * @param msg the message to send
	 */
	private void sendMessage(MWACMessage_Data msg)
	{
		if(this.neighboorlist.contains(msg.getReceiver()))
			super.sendFrame(new MWACFrame(getUserId(),msg.getReceiver(),msg));
		else
		{
			short idRequest=this.messageToTransmitQueue.add(msg);
			this.networkPartialKnowledgeManager.addRouteRequestAndReceiverAssociation(idRequest, msg.getReceiver());
			super.sendFrame(new MWACFrame(getUserId(),MWACFrame.BROADCAST_LINK,new MWACMessage_RouteRequest(getUserId(),msg.getReceiver(),idRequest)));
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
			case TrustedMWACAgent.roleBACKUP_MODE:
				// Je considere qu'en BACKUP_MODE on se comporte comme un simple membre
				// ATTENTION : IL EST POSSIBLE DE N'AVOIR AUCUN REPRESENTANT AUQUEL CAS ON EST COINCE
				if(this.neighboorlist.getNbRepresentative()==0)
				{
					System.out.println(this.getUserId()+" est en BACKUP MODE et ne peut pas envoyer de message car je n'ai pas de reprentant");
					return;
				}
				// Si j'ai des representant je me comporte comme un simple membre
			case TrustedMWACAgent.roleNOTHING:
			case TrustedMWACAgent.roleSIMPLEMEMBER:
			case TrustedMWACAgent.roleLINK:
				ArrayList<Integer> lst=this.neighboorlist.getRepresentativeIdentifiers();
				if(lst==null) 
					super.notifyEvent(new MessageNotTransmittedEvent(this.getSystemId(),msg,"Representative neighboor not found"));
				else
				{
					super.notifyEvent(new SendedMessageEvent(this.getSystemId(),(Message)msg.clone()));
					this.sendDataFrame(lst.get(0),msg);
				}
				break;
			case TrustedMWACAgent.roleREPRESENTATIVE:
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
		if(TrustedMWACAgent.DEBUG) debug(getUserId()+">>> Envoie d'un message de DONNEES");
		super.sendFrame(new MWACFrame(getUserId(),dest,msg));
	}

	/**
	 * send a conflict resolution message
	 * this message is send by a representative agent in conflict with one or more others representative agent
	 */
	private void sendConflictResolution() 
	{
		if(TrustedMWACAgent.DEBUG) debug(getUserId()+">>> Envoie par d'un message de RESOLUTION DE CONFLIT");
		super.sendFrame(new MWACFrame(getUserId(),MWACFrame.BROADCAST,new MWACMessage_ConflictResolution(getUserId(),MWACMessage.BROADCAST,this.score())));
	}

	/**
	 * (Anca)
	 * send a query for members of a group
	 * @param newGroupRep
	 */
	private void sendQueryForMembers(int newGroupRep){
		if(DEBUG) debug(getUserId() + ">>>> Sending query for members to " + newGroupRep);
		super.sendFrame(new MWACFrame(getUserId(), MWACFrame.BROADCAST, new MWACMessage_QueryGroupMembers(getUserId(), newGroupRep)));
	}

	/**
	 * Sends a reply (for member queries) containing the members of its group
	 * @param dest
	 */
	private void sendQueryForMembersReply(int dest){
		if(DEBUG) debug(getUserId() + ">>>> Sending query for members REPLY to " + dest);

		int[] members = neighboorlist.getMembers();

		ByteBuffer buf = ByteBuffer.allocate(4 * members.length);

		for(int i = 0; i < members.length; i++)
			buf.putInt(members[i]);
		super.sendFrame(new MWACFrame(getUserId(), MWACFrame.BROADCAST, new MWACMessage_QueryGroupMembersReply(getUserId(), dest, buf.array())));
	}

	/**
	 * send an possible organizational incoherence notification message
	 * @param v the suspected groups
	 */
	private void sendIncoherenceNotification(Vector<Integer> v)
	{
		// Je pense qu'il ne faut pas qu'un backup mode cherche a detecter des incoherences... a voir
		if (this.role!=TrustedMWACAgent.roleBACKUP_MODE)
		{
			int[] array = new int[v.size()];
			for(int i=0;i<v.size();i++) array[i]=v.get(i);

			if(TrustedMWACAgent.DEBUG) debug(">>> Envoie par d'un message de suspission d'incoherence organisationnelle     <"+getUserId()+","+TrustedMWACAgent.roleToString(this.role)+","+this.getGroup()+">");
			super.sendFrame(new MWACFrame(getUserId(),MWACFrame.BROADCAST,new MWACMessage_PossibleOrganizationalIncoherence(getUserId(),array)));
		}
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
			System.out.println("\nDETECTION POSSIBLE INCOHERENCE SIGNALEE PAR <"+this.getUserId()+","+TrustedMWACAgent.roleToString(this.getRole())+","+MWACRouteAssistant.routeToString(this.getGroups())+"). Les groupes suspects sont "+v+"\n"+this.neighboorlist.toString());
			this.sendIncoherenceNotification(v);
		}
	}

	/**
	 * send a presentation message
	 */
	private void sendPresentation() 
	{
		//Frame f=new Frame(getId(),dest,new ASTRO_Presentation(getId(),dest,role,group));


		if(TrustedMWACAgent.DEBUG) debug(">>> Envoie par d'un message de PRESENTATION     <"+getUserId()+","+TrustedMWACAgent.roleToString(this.role)+","+this.getGroup()+">");

		/**
		 *  (Anca) The case of group usurpation
		 */
		if(this.mustUsurpGroups)
		{
			if(this.role != roleLINK){ // he only has one group, he invents a new group value
				claimedGroups = this.getGroups();
				if(claimedGroups != null)
					claimedGroups[0]++; // forge group id (this falls into the case of new group link)
			} 
			else 
			{
				// remove the first group 				
				// (TODO later - pick a random group to remove)

				int[] groups = this.getGroups();
				claimedGroups = new int[groups.length - 1];
				for(int i = 1; i < groups.length; i++)
					claimedGroups[i - 1] = groups[i];
			}		

			/**
			 *  (Anca) The case of link to a new group usurpation
			 */
		} else if (this.mustUsurpLink){

			// ID should stay the same (?)

			// Forge role of this node into roleLINK (no matter what it's role was)		
			claimedRole = roleLINK;

			// all the previous groups plus a new group (that may not exist)			
			claimedGroups = MWACGroupAssistant.cloneGroupArray(getGroups(), getGroups().length + 1);

			// value of the new group -- random 50

			claimedGroups[getGroups().length] = 1 + (new Random()).nextInt(10);//1234; 

		} else {
			claimedGroups = this.getGroups();
			claimedRole = this.role;
		} /** (/Anca) */

		if(this.isUsurper)
			System.out.println("USURPER #" + realId + " sending out presentation: " + getUserId() + ", " + TrustedMWACAgent.roleToString(claimedRole) + ", " + MWACGroupAssistant.groupsToString(claimedGroups));
		super.sendFrame(new MWACFrame(getUserId(),MWACFrame.BROADCAST,new MWACMessage_Presentation(getUserId(), claimedRole, claimedGroups)));		
	}

	/**
	 * send an introduction message
	 */
	private void sendIntroduction() 
	{
		if(TrustedMWACAgent.DEBUG) debug(">>> Envoie par d'un message d'INTRODUCTION      <"+getUserId()+","+TrustedMWACAgent.roleToString(this.role)+","+this.getGroup()+">");

		super.sendFrame(new MWACFrame(super.getUserId(),MWACFrame.BROADCAST,new MWACMessage_Introduction(this.getUserId())));

	}

	/**
	 * send a message to request the close neighboors
	 */
	private void sendWhoAreMyNeighboors()
	{
		//Frame f=new Frame(getId(),dest,new ASTRO_Presentation(getId(),dest,role,group));
		if(TrustedMWACAgent.DEBUG) debug(">>> Envoie par d'un message de WHOAREMYNEIGHBOORS   <"+getUserId()+","+TrustedMWACAgent.roleToString(this.role)+","+this.getGroup()+">");

		/**
		 *  (Anca) The case of group usurpation
		 */
		if(this.mustUsurpGroups)
		{
			if(this.role != roleLINK){ // he only has one group, he invents a new group value
				claimedGroups = this.getGroups();
				if(claimedGroups != null)
					claimedGroups[0]++; // forge group id (this falls into the case of new group link)
			} else {
				// remove the first group 				
				// (TODO later - pick a random group to remove) 

				int[] groups = this.getGroups();
				claimedGroups = new int[groups.length - 1];
				for(int i = 1; i < groups.length; i++)
					claimedGroups[i - 1] = groups[i];
			}		

			/**
			 *  (Anca) The case of link to a new group usurpation
			 */
		} else if (this.mustUsurpLink){

			// ID should stay the same (?)

			// Forge role of this node into roleLINK (no matter what it's role was)		
			claimedRole = roleLINK;

			// all the previous groups plus a new group (that may not exist)			
			claimedGroups = MWACGroupAssistant.cloneGroupArray(getGroups(), getGroups().length + 1);

			// value of the new group (unlikely)
			claimedGroups[getGroups().length] = 124; 

		} else {
			claimedGroups = this.getGroups();
			claimedRole = this.role;
		} /** (/Anca) */

		super.sendFrame(new MWACFrame(getUserId(),MWACFrame.BROADCAST,new MWACMessage_WhoAreMyNeighboors(getUserId(),claimedRole, claimedGroups)));
	}

	/**
	 * return the role
	 * @return the role
	 */
	public byte getRole() 
	{
		return this.role;
	}


	/**
	 * returns true if the agent think to be in a confident neighboorhood
	 * ALGORITHM 2 of the EUMAS paper
	 */
	public boolean isConfidentNeighboorhood()
	{
		return this.neighboorlist.isConfident();
	}

	/** agent become an usurper of the workstation
	 */
	public void becomeWorkstationUsurper()
	{
		this.mustUsurpsWorkstationID=true;
	}

	/** agent become an usurper of a neighboor
	 */
	public void becomeNeighboorUsurper()
	{
		this.mustUsurpsNeighboorID=true;
	}

	/**
	 * (Anca) Agent becomes usurper of groups 
	 * - does not include all groups in the message 
	 */
	public void becomeGroupUsurper(){
		this.mustUsurpGroups = true;
	}

	/**
	 * (Anca) Agent becomes usurper of link
	 * - claims he has a link to a new group
	 */
	public void becomeLinkUsurper(){
		this.mustUsurpLink = true;
	}

	/** verify that a route exists to join a specified agent
	 * @param id identfier of the agent which must be joined
	 */
	public void checkRouteTo(int id)
	{
		// Reflechir au backup_mode
		if(this.getRole()==TrustedMWACAgent.roleREPRESENTATIVE)
		{	
			int[] route=this.networkPartialKnowledgeManager.getDataMessageRoute(TrustedMWACAgent.WORKSTATION_ID);
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
		// Reflechir au backup_mode
		if(this.role==TrustedMWACAgent.roleREPRESENTATIVE)
		{
			int relais =-1;
			if(route.length>0) relais=this.neighboorlist.getLinkToRepresentant(route[0]);
			if(relais!=MWACRouteAssistant.UNDEFINED_ID) 
			{
				System.out.println("\nCHECK THE ROUTE LAUNCHED BY "+this.getUserId()+" (repr) TO "+id+". Route is "+MWACRouteAssistant.routeToString(route));
				super.sendFrame(new MWACFrame(super.getUserId(),relais,new MWACMessage_CheckRouteRequest(this.getUserId(),id,(short)1 /** identifiant de requete*/,route)));
				return;
			}

		}
		System.out.println("\nCHECK THE ROUTE LAUNCHED BY "+this.getUserId()+" (non repr) TO "+id+". Route is "+MWACRouteAssistant.routeToString(route));
		super.sendFrame(new MWACFrame(super.getUserId(),MWACFrame.BROADCAST_REPRESENTATIVE,new MWACMessage_CheckRouteRequest(this.getUserId(),id,(short)1 /** identifiant de requete*/,route)));
	}

	/** returns the string signature of the agent which will be viewable under the spy windows
	 * @return the string signature of the agent
	 */

	public String toSpyWindows()
	{
		String roleDependantText="";
		if(getRole()==TrustedMWACAgent.roleREPRESENTATIVE && this.messageToTransmitQueue!=null) roleDependantText="<BR><BR>"+this.messageToTransmitQueue.toHTML();
		if((getRole()==TrustedMWACAgent.roleREPRESENTATIVE || getRole()==TrustedMWACAgent.roleLINK) && this.alreadyProcessedRouteRequestManager!=null) roleDependantText+="<BR><BR>"+this.alreadyProcessedRouteRequestManager.toHTML();
		if(getRole()==TrustedMWACAgent.roleREPRESENTATIVE && this.networkPartialKnowledgeManager!=null) roleDependantText+="<BR><BR>"+this.networkPartialKnowledgeManager.toHTML();

		return "<HTML>"+"<B>Id</B>="+this.getUserId()+"    <B>Role</B>="+TrustedMWACAgent.roleToString(this.getRole())+"    <B>Group</B>="+MWACGroupAssistant.groupsToString(this.getGroups())+"    <B>Energy</B>="+String.format("%.2f",this.pourcentOfAvailableEnergy())+"    <B>Range</B>="+this.getRange()+"<BR><BR>"+this.trustManager.toHTML()+"<BR><BR>"+this.neighboorlist.toHTML()+roleDependantText+"</HTML>";
	}

	/**
	 * (Anca) -- debug
	 */
	public void printInfo(){
		System.out.println(toSpyWindows());
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
		case TrustedMWACAgent.roleREPRESENTATIVE	: return "ROLE_REPRESENTATIVE";
		case TrustedMWACAgent.roleLINK				: return "ROLE_LINK";
		case TrustedMWACAgent.roleSIMPLEMEMBER		: return "ROLE_SIMPLEMEMBER";
		case TrustedMWACAgent.roleNOTHING			: return "ROLE_NOTHING";
		case TrustedMWACAgent.roleBACKUP_MODE		: return "BACKUP_MODE";
		}
		return "ROLE_UNDEFINED";
	}







	/**
	 * 
	 * @author JPeG
	 *
	 */
	private class InteractionContext
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






}
