package simulation.solutions.custom.ACO_MWAC;
import java.awt.Color;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Vector;

import simulation.battery.custom.LinearBatteryBasicModel;

import simulation.entities.Agent;
import simulation.events.*;
import simulation.events.system.MessageNotTransmittedEvent;
import simulation.events.system.ReceivedMessageEvent;
import simulation.events.system.RoleModificationEvent;
import simulation.events.system.SendedMessageEvent;
import simulation.messages.*;
import simulation.multiagentSystem.MAS;
import simulation.utils.aDate;
import simulation.solutions.custom.ACO_MWAC.Messages.*;
import simulation.solutions.custom.ACO_MWAC.AntAssistant.*;
import simulation.solutions.custom.MWAC.Messages.*;
import simulation.solutions.custom.MWAC.*;


public class ACO_MWAC_Agent extends Agent implements ObjectAbleToSendMessageInterface{

	private static final boolean ENABLE_ORGANIZATIONAL_INCOHERENCE_VERIFICATION = true;
	private static final boolean DEBUG = false;

	/** delay (in ms) before the detection of a possible organizational incoherence and its real notification */
	private static final int DELAY_BEFORE_ORGANIZATIONAL_INCOHERENCE_VERIFICATION = 15000;
	/** maximum distance authorized to not reorganize the organization  */
	private static final int MAX_HOP_RESEARCH_ORGANIZATIONAL_INCOHERENCE = 4;

	/** no role */
	public  final static byte roleNOTHING				= 0;
	/** simple member role */
	public  final static byte roleSIMPLEMEMBER			= 1;
	/** link member role */
	public  final static byte roleLINK					= 2;
	/** representative member role */
	public  final static byte roleREPRESENTATIVE		= 3;
	/** color of no role members */
	public  final static Color colorNOTHING 			= Color.LIGHT_GRAY;
	/** color of representative members */
	public  final static Color colorREPRESENTATIVE 		= Color.RED;
	/** color of link members */
	public  final static Color colorLINK 				= Color.GREEN;
	/** color of simple member */
	public  final static Color colorSIMPLEMEMBER 		= Color.YELLOW;

	/** member of non group */
	private static int groupNONE = -1;

	/** role of the agent */
	private byte role;
	/** received message queue */
	private FrameFIFOStack receivedMessageQueue;
	/** message to send (if the agent is Representative*/
	private MWACMessageFIFOStack messageToTransmitQueue;
	/** neighboorList */
	private ACO_MWACNeighboorList neighboorlist;
	/** to manage already processed route request */
	private MWACAlreadyProcessedRouteRequestManager alreadyProcessedRouteRequestManager;
	/** MWAC route fragment  manager */
	private MWACNetworkPartialKnowledgeManager networkPartialKnowledgeManager;
	/** MWAC incoherence possible detection manager */ 
	private MWACIncoherenceList organizationalIncoherenceManager;

	/** Memorize the energy level when the agent has been elected representative */
	private float pourcentOfAvailableEnergyWhenElectedRepresentative;

	
	//*************************************************************************************************************************************************************
	private boolean First_Initialisation_Message;
	private ACO_FrameFIFOStack receivedAntQueue;
	private ACO_MWAC_AntManager AntManager;
	private ACO_MWAC_AlreadyProcessedBackwardAnt AlreadyProcessedBackwardAnt;
	private ACO_MWAC_AlreadyProcessedForwardAnt AlreadyProcessedForwardAnt;
	private Routing_table RoutingTable;
	//*************************************************************************************************************************************************************

	/**
	 * Constructor
	 * @param mas the multiagent system
	 * @param id identifier of the agent
	 * @param energy energy of the agent
	 * @param range range of the agent
	 */
	public ACO_MWAC_Agent (MAS mas,Integer  id, Float energy,Integer  range)
	{
		super(mas,id,range);
		this.role=roleNOTHING;
		this.receivedMessageQueue=new FrameFIFOStack();
		this.neighboorlist=new ACO_MWACNeighboorList();
		this.messageToTransmitQueue=null;	//R
		this.alreadyProcessedRouteRequestManager=null;
		this.networkPartialKnowledgeManager=null;
		this.organizationalIncoherenceManager=null;
		this.pourcentOfAvailableEnergyWhenElectedRepresentative=0;

		//*********************************************************************************************************************************************************
		this.First_Initialisation_Message = false;
		this.receivedAntQueue = new ACO_FrameFIFOStack();
		this.AntManager = new ACO_MWAC_AntManager();
		this.AlreadyProcessedForwardAnt = new ACO_MWAC_AlreadyProcessedForwardAnt();
		this.AlreadyProcessedBackwardAnt = new ACO_MWAC_AlreadyProcessedBackwardAnt();
		this.RoutingTable = new Routing_table();
		//*********************************************************************************************************************************************************
	}

	/**  Launched by the call of start method*/
	public void run()
	{

		System.out.println("Démarrage de ACO_MWAC_Agent "+this.getUserId());
		
		try{Thread.sleep(500);}catch(Exception e){}
		
		if (DEBUG) debug("Envoie un message d'introduction");
		sendIntroduction();
		try{Thread.sleep(2*SLEEP_TIME_SLOT);}catch(Exception e){};
		
		// Context of the interaction (messages process)
		InteractionContext interaction = new InteractionContext();
		
		//*********************************************************************************************************************************************************
		//si je suis la station de base, je demarre l'etape d'initialisation des tables de routage après la creation de tous les agents
		if(this.getUserId() == 	Configuration.base_station) {
			try{Thread.sleep(Configuration.Waiting_Time_for_creating_All_Agents * 1000);}catch(Exception e){}
			this.AntManager.Best_Hop = 0; 
			this.sendFrame(new ACO_Frame(this.getUserId(), ACO_Frame.BROADCAST, new ACO_Message_Initialization(this.getUserId(), ACO_Message.BROADCAST, (float) this.getBattery().getActualAmountOfEngergy(), this.AntManager.Best_Hop)));
		}
		//*********************************************************************************************************************************************************	
			
		// MWAC LAYER
		while(!isKilling() && !isStopping())
		{
			// Pause
			//try{Thread.sleep(SLEEP_TIME_SLOT+this.extraWaitTime(this.role));}catch(Exception e){};
			this.sleep(SLEEP_TIME_SLOT+this.extraWaitTime(this.role));

			// Preparation of the others threads
			while( ((isSuspending()) && (!isKilling() && !isStopping()))) try{Thread.sleep(SLEEP_TIME_SLOT);}catch(Exception e){};

			// Attente de messages
			while( this.receivedMessageQueue.isEmpty() && (!interaction.lostRepresentativeRole)) 
			{	
				//********************************************************************************************************************************************************
				//energy update duration ? //verifier que c'est un agent non simple membre pour envoyer les mises à jour de l'energie
				if((this.AntManager.IsRoundOfEnergyUpdate()) && (this.getRole()!=roleSIMPLEMEMBER) && (this.getUserId()!=Configuration.base_station)) { this.sendFrame(new ACO_Frame(this.getUserId(), ACO_Frame.BROADCAST, new ACO_Message_Update(this.getUserId(), ACO_Message.BROADCAST, (float) this.getBattery().getActualAmountOfEngergy()))); }
				
				//evaporation duration ?    //verifier que c'est un agent non simple membre pour faire l'evaporation et reinitialiser les "Prevent_Destination"
				if ((AntManager.IsRoundOfEvaporation()) && (this.getRole()!=roleSIMPLEMEMBER) && (this.getUserId()!=Configuration.base_station)) { this.RoutingTable.pheromone_evaporation();  this.RoutingTable.Reinit_PreventDestination_table(this.AntManager.Best_Hop);}		

				//s'il y a des messages de type ACO les traiter 
				if (!this.receivedAntQueue.isEmpty()) { this.process_ACO_Message(this.receivedAntQueue.pop()); }
				//********************************************************************************************************************************************************

				if (ENABLE_ORGANIZATIONAL_INCOHERENCE_VERIFICATION)
				{
					// All agents look if there are detected a possible incoherence
					if (this.neighboorlist.dateOfLastPossibleIncoherenceDetection>0)
						if ( (new aDate()).differenceToMS(new aDate(this.neighboorlist.dateOfLastPossibleIncoherenceDetection))>DELAY_BEFORE_ORGANIZATIONAL_INCOHERENCE_VERIFICATION) 
						{
							// On signale le problème à notre représentant
							this.incoherenceNotificationProcedure();	
							this.neighboorlist.dateOfLastPossibleIncoherenceDetection=0;
						}

					// A representative agent manage suspected inconsistency 
					if (this.role==roleREPRESENTATIVE)
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

				//try{Thread.sleep(SLEEP_TIME_SLOT);}catch(Exception e){};
				this.sleep(SLEEP_TIME_SLOT);
			}

			// Process waiting messages (if we have received some messages)
			while((interaction.frame=this.receivedMessageQueue.pop())!=null) if (!interaction.lostRepresentativeRole) this.processMessage(interaction);

			// Verify the role
			this.computeRoleDecision(interaction);

			// Action associated to the decision to take into account the  interaction/organization modification
			if(interaction.roleConfict)
			{
				if(DEBUG) debug("Je suis en conflit avec "+this.neighboorlist.getRepresentativeIdentifiers());
				this.sendConflictResolution();
			}
			else if(interaction.mustSendWhoAreMyNeighoors)
				this.sendWhoAreMyNeighboors();
			else if(interaction.mustSendAPresentation)
				this.sendPresentation();

			interaction.init();
			
			// Must be freezed?
			try{Thread.sleep(SLEEP_TIME_SLOT);}catch(Exception e){}
			
			//********************************************************************************************************************************************************
			//s'il y a des messages de type ACO, les traiter 
			if (!this.receivedAntQueue.isEmpty()) { this.process_ACO_Message(this.receivedAntQueue.pop()); }
			//********************************************************************************************************************************************************
		}

		// AGENT SPECIFIC TASK
		System.out.println("Fin du thread "+this.getUserId());
	}
	

	//*****************************************************************************************************************************************************************
	private void process_ACO_Message(ACO_Frame frame){

		ACO_Message msg = ACO_Message.createMessage(frame.getData());

		switch(msg.getType())
		{		
		// The received frame is an energy level
		case ACO_Message.msgINITIALIZATION :
		{
			//the message is specialised
			ACO_Message_Initialization tMsg = (ACO_Message_Initialization)msg;

			//si c'est le premier message reçu, le rediffuser;
			if (this.AntManager.Best_Hop > tMsg.get_HopCount()){
				this.AntManager.Best_Hop = tMsg.get_HopCount();
				this.sendFrame(new ACO_Frame (this.getUserId(), ACO_Frame.BROADCAST, new ACO_Message_Initialization(this.getUserId(), ACO_Message.BROADCAST, (float) this.getBattery().getActualAmountOfEngergy(),this.AntManager.Best_Hop+1)));
			}
			//dans tout les cas, je met à jour ma table de routage
			this.RoutingTable.Initialise_Entry_Of_RoutingTable(tMsg);

		}break;


		// The received frame is an energy level update
		case ACO_Message.msgUPDATE :
		{
			// The message is specialized
			ACO_Message_Update tMsg = (ACO_Message_Update)msg;

			//mettre à jour les tables de routage
			this.RoutingTable.Update_EnergyLevel(tMsg);

		}break;


		// The received frame is an Ant Forward
		case ACO_Message.msgFORWARD_ANT:
		{		
			//Am i concerned by the data?
			if(frame.getReceiver()==this.getUserId())
			{	
				//si je reçois la premiere fourmi, j'active les deux chronometre 
				if (this.First_Initialisation_Message == false) {this.First_Initialisation_Message = true; this.AntManager.Timer_RoundOfEnergyUpdate.start(); this.AntManager.Timer_RoundOfEvaporation.start();}
				
				// The message is specialized
				ACO_Message_Forward_Ant fourmi = (ACO_Message_Forward_Ant)msg;

				// si je suis le destinataire de ce message ?
				if(fourmi.getReceiver() == this.getUserId())
				{
					fourmi = this.AntManager.Add_Relay(fourmi, frame.getSender()); 				//ajouter le relais qui me l'a envoyée
					this.AlreadyProcessedForwardAnt.isAlreadyProcessed(fourmi);					//affichage
					this.notifyEvent(new ReceivedMessageEvent(this.getSystemId(), (ACO_Message_Forward_Ant)fourmi.clone()));

					ACO_Message_Backward_Ant fourmi_back = new ACO_Message_Backward_Ant(fourmi); 			//instancier une fourmi back
					this.AlreadyProcessedBackwardAnt.isAlreadyProcessed(fourmi_back);						//affichage
					fourmi_back = this.AntManager.Delete_Last_Relay(fourmi_back);							//supprimer le dernier relais de la fourmi
					this.sendFrame(new ACO_Frame (this.getUserId(), frame.getSender(), fourmi_back));	//renvoyer la fourmi
				}

				//sinon je suis un relais
				else
				{
					//voisin direct de la station de base ? l'envoyer directement vers la station de base
					if (this.RoutingTable.find_Entry(fourmi.getReceiver()) != null )
					{
						fourmi = this.AntManager.Add_Relay(fourmi, frame.getSender()); 				//ajouter le relais qui me l'a envoyée
						this.AlreadyProcessedForwardAnt.isAlreadyProcessed(fourmi);					//affichage
						this.sendFrame(new ACO_Frame(this.getUserId(), fourmi.getReceiver(), fourmi));	//envoi de la fourmi
					}

					//sinon, je passe par un autre agent intermediaire
					else {

						//je verfie si ce n'est pas un retour arriere (si le dernier element de la memoire est moi-meme)
						if ( this.getUserId() == Ant_Route_Assistant.getLastId(fourmi.get_Memory())) {
							this.RoutingTable.find_Entry(frame.getSender()).set_PreventDestination(1);  	//je marque la destination indesirable
							fourmi = this.AntManager.Delete_Last_Relay((ACO_Message_Forward_Ant)fourmi);	//je me supprime de la memoire de la fourmi
							
							System.out.println("---------------------------------------------------------------------------------- nombre de retour arriere : "+(++Configuration.come_back));
						}
						else {
							fourmi = this.AntManager.Add_Relay(fourmi, frame.getSender()); 					//ajouter le relais qui me l'a envoyée
						}

						//affichage
						this.AlreadyProcessedForwardAnt.isAlreadyProcessed(fourmi);							//affichage

						//pick up the next agent
						int next_agent = this.RoutingTable.get_Next_Agent(this.RoutingTable,this.neighboorlist.listOfRoutingAgent(this.getUserId(), this.getRole(), fourmi.get_Memory()), fourmi.get_Memory()); 

						//si un relais existe, lui envoyer la fourmi 
						if (next_agent != this.RoutingTable.NotExist) this.sendFrame(new ACO_Frame(this.getUserId(), next_agent, fourmi));


						// pas d'autre agent intermediaire, effectuer un retour arriere
						else {

							//si je suis l'expediteur, et je ne trouve pas de prochains relais, alors impossible de la transmettre
							if (fourmi.getSender() == this.getUserId()) super.notifyEvent(new MessageNotTransmittedEvent(this.getSystemId(), fourmi, "No Path Found"));

							//sinon, je ne suis pas l'exepediteur, je la passe à mon predecesseur
							else {
								int previous = Ant_Route_Assistant.getLastId(fourmi.get_Memory());
								System.out.println("retour ariere de "+ this.getUserId() + "   vers  " + previous);
								this.sendFrame(new ACO_Frame(this.getUserId(), previous, fourmi));
							}
						}
					}
				}									
			}
		}break;


		// The received frame is an Ant Backward
		case ACO_Message.msgBACKWARD_ANT:
		{					
			//Am i concerned by the data?
			if(frame.getReceiver()==this.getUserId())
			{	
				// The message is specialized
				ACO_Message_Backward_Ant fourmi = (ACO_Message_Backward_Ant)msg;

				//ajouter à la liste
				this.AlreadyProcessedBackwardAnt.isAlreadyProcessed(fourmi);

				//renforce path (renforcer la connexion en deposant de la pheromone addiotionnelle)
				this.RoutingTable.renforce_path(frame.getSender(),fourmi.get_length());

				//si je ne suis pas le destinataire de ce message, je le passe donc à mon predecesseur
				if(fourmi.getReceiver()!=this.getUserId()) {
					int previous = Ant_Route_Assistant.getLastId(fourmi.get_Memory());
					fourmi = this.AntManager.Delete_Last_Relay(fourmi);
					this.sendFrame(new ACO_Frame(this.getUserId(), previous , fourmi));
				}
			}
		}
		break;
		//fin du switch
		} 
	}
	//*****************************************************************************************************************************************************************

	/**
	 * chose a role and decide of action if necessary
	 * @param interaction
	 */
	public void computeRoleDecision(InteractionContext interaction)
	{

		// No neighboors
		byte newRole=this.role;	

		if(this.neighboorlist.isEmpty()) 
			newRole=roleNOTHING;
		else
		{
			switch(this.role)
			{
			case roleNOTHING:
			case roleSIMPLEMEMBER:
			{
				int n = this.neighboorlist.getNbRepresentative();
				if(n==0) 
					newRole=roleREPRESENTATIVE;
				else if(n==1) 
				{
					newRole=roleSIMPLEMEMBER;
					if(interaction.hasPreviouslyLostRepresentativeRole) newRole=this.role;
				}
				else newRole=roleLINK;
			}
			break;
			case roleLINK:
			{
				int n = this.neighboorlist.getNbRepresentative();
				if(n==0) newRole=roleREPRESENTATIVE;
				else if(n==1) newRole=roleSIMPLEMEMBER;
				else newRole=roleLINK;
			}
			break;
			case roleREPRESENTATIVE:
			{
				if(interaction.lostRepresentativeRole||interaction.hasPreviouslyLostRepresentativeRole)
				{
					newRole=roleNOTHING;
					interaction.roleConfict=false;	// Il n'y a plus de conflit
				}
				else
				{
					if(this.neighboorlist.getNbRepresentative()>0) interaction.roleConfict=true;
					newRole=roleREPRESENTATIVE;
				}
			}
			break;
			}
		}



		// Initialize data and 
		if (this.role!=newRole)
		{
			if(DEBUG) debug("Je change de role "+roleToString(this.role)+" => "+roleToString(newRole)+" lost="+interaction.lostRepresentativeRole+"  prevLost="+interaction.hasPreviouslyLostRepresentativeRole);
			this.setRole(newRole);
			switch(newRole)
			{
			case roleREPRESENTATIVE:
				this.pourcentOfAvailableEnergyWhenElectedRepresentative=this.pourcentOfAvailableEnergy();
				this.alreadyProcessedRouteRequestManager=new MWACAlreadyProcessedRouteRequestManager();
				this.messageToTransmitQueue=new MWACMessageFIFOStack();
				this.networkPartialKnowledgeManager=new MWACNetworkPartialKnowledgeManager();
				this.organizationalIncoherenceManager=new MWACIncoherenceList();
				interaction.mustSendWhoAreMyNeighoors=true;
				break;
			case roleLINK:
				this.pourcentOfAvailableEnergyWhenElectedRepresentative=0;
				this.alreadyProcessedRouteRequestManager=new MWACAlreadyProcessedRouteRequestManager();
				this.networkPartialKnowledgeManager=null;
				this.messageToTransmitQueue=null;
				this.organizationalIncoherenceManager=null;
				interaction.mustSendAPresentation=true;
				break;
			case roleNOTHING:
				this.pourcentOfAvailableEnergyWhenElectedRepresentative=0;
				this.alreadyProcessedRouteRequestManager=null;
				this.networkPartialKnowledgeManager=null;
				this.messageToTransmitQueue=null;
				this.organizationalIncoherenceManager=null;
				this.neighboorlist=new ACO_MWACNeighboorList();
				interaction.mustSendAPresentation=false;
				this.sendIntroduction();
				this.sleep(250);	// A priori grosse modification... On prend le temps de laisser les choses se stabiliser
				break;
			case roleSIMPLEMEMBER:
				this.alreadyProcessedRouteRequestManager=null;
				this.networkPartialKnowledgeManager=null;
				this.messageToTransmitQueue=null;
				this.organizationalIncoherenceManager=null;
				interaction.mustSendAPresentation=true;
				break;
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
			this.neighboorlist.put(msg.getSender(),roleNOTHING, groupNONE);
			interaction.mustSendWhoAreMyNeighoors=true;
		}
		break;
		case MWACMessage.msgWHO_ARE_MY_NEIGHBOORS:
		case MWACMessage.msgPRESENTATION:
		{
			this.neighboorlist.put(msg.getSender(),((MWACMessage_Presentation)msg).getRole(),  ((MWACMessage_Presentation)msg).getClonedGroupArray());
			interaction.mustSendAPresentation=   interaction.mustSendAPresentation
			|| (msg.getType()==MWACMessage.msgWHO_ARE_MY_NEIGHBOORS) 
			|| this.neighboorlist.put(msg.getSender(),((MWACMessage_Presentation)msg).getRole(), ((MWACMessage_Presentation)msg).getClonedGroupArray());
		}
		break;
		case MWACMessage.msgCONFLICT_RESOLUTION:
		{
			this.neighboorlist.put(msg.getSender(), roleREPRESENTATIVE, msg.getSender());
			if(this.role==roleREPRESENTATIVE && !interaction.lostRepresentativeRole)
			{
				int scoreInMsg = ((MWACMessage_ConflictResolution)msg).getScore();
				if((scoreInMsg>this.score()) || ((scoreInMsg==this.score()) && this.getUserId()<msg.getSender()))
				{
					if(DEBUG) debug("*PERDU* contre "+msg.getSender());
					interaction.lostRepresentativeRole=true;
					interaction.mustSendAPresentation=true;
					interaction.debugPerdu=msg.getSender();
				}
				else
				{
					if(DEBUG) debug("*GAGNE* contre "+msg.getSender());
					this.neighboorlist.put(msg.getSender(),roleNOTHING,groupNONE);
					if(DEBUG) debug("Je montre que je gagne mon conflit");
					this.sendConflictResolution();
					interaction.mustSendAPresentation=false;
				}
			}
			else
			{
				// I have already lost my reprentative role against with another agent
				if (interaction.lostRepresentativeRole) if(DEBUG) debug("*DEJA PERDU* contre "+interaction.debugPerdu+" bien avant "+msg.getSender()+"    role="+this.role+"  lost="+interaction.lostRepresentativeRole);
				//this.neighboorlist.put(msg.getSender(),this.roleNOTHING,this.groupNONE);
				interaction.mustSendAPresentation=true;
			}
		}
		break;
		case MWACMessage.msgTTL_ROUTE_REQUEST:
		case MWACMessage.msgROUTE_REQUEST:
		{
			// Je suis représentant
			MWACMessage_RouteRequest tMsg = (MWACMessage_RouteRequest) msg;
			if(this.getRole()==roleREPRESENTATIVE)
			{
				TripletIdRoleGroup triplet=this.neighboorlist.get(tMsg.getReceiver());
				if (triplet!=null || tMsg.getReceiver()==this.getUserId())
				{
					if(!alreadyProcessedRouteRequestManager.isAlreadyProcessed(tMsg.getSender(), tMsg.getIdRequest()))
					{
						// Est-til un voisin?
						System.out.println("REPRESENTANT #"+this.getUserId()+" VA REPONDRE PAR UN ROUTE REPLY A LA RECHERCHE");
						int relais = this.neighboorlist.getLinkToRepresentant(MWACRouteAssistant.getLastId(tMsg.getRoute()));
						if (relais==MWACRouteAssistant.UNDEFINED_ID) relais=this.neighboorlist.getLinkToRepresentant(tMsg.getSender());
						if (relais==MWACNeighboorList.UNDEFINED_ID) System.out.println("!!!!!! (1) Moi repr "+this.getUserId()+" ne trouve pas le précédent de "+interaction.frame.getSender()+" dans la route "+MWACRouteAssistant.routeToString(tMsg.getRoute())+" (pour un ROUTE REPLY)");
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
			else if(this.getRole()==roleLINK)
			{
				if( (!alreadyProcessedRouteRequestManager.isAlreadyProcessed(tMsg.getSender(), tMsg.getIdRequest())) && (tMsg.getSender()!=this.getUserId()) && (interaction.frame.getReceiver()!=MWACFrame.BROADCAST_REPRESENTATIVE))
				{
					//System.out.println("LINK #"+this.getUserId()+" VA FAIRE SUIVRE LA RECHERCHE");
					this.sendFrame(new MWACFrame(this.getUserId(),MWACFrame.BROADCAST_REPRESENTATIVE, tMsg));
				}
				else
				{
					//System.out.println("LINK #"+this.getUserId()+" NE VA PAS REPONDRE A LA RECHERCHE CAR DEJA TRAITE");
				}
			}
		}
		break;	
		case MWACMessage.msgROUTE_REPLY:
			if(interaction.frame.getReceiver()==this.getUserId())
			{

				MWACMessage_RouteReply tMsg = (MWACMessage_RouteReply) msg;

				if(this.getRole()==roleREPRESENTATIVE)
				{
					if(tMsg.getReceiver()==this.getUserId())
					{
						//System.out.println("repr "+this.getUserId()+" J'ai eu mon route reply!");
						int[] route = MWACRouteAssistant.cloneRoute(tMsg.getRoute(), 1+tMsg.getRoute().length);
						route[route.length-1]=tMsg.getSender();
						this.networkPartialKnowledgeManager.addRoute(route);
						int member=this.networkPartialKnowledgeManager.getRouteRequestAssociatedReceiver(tMsg.getIdRequest());
						this.networkPartialKnowledgeManager.addIdGroupAssociation(MWACRouteAssistant.getLastId(route), member);
						this.tryToProcessWaitingSendedMessage();

						System.out.println("Retour de "+tMsg.getSender());
						this.organizationalIncoherenceManager.contactedGroup(tMsg.getSender());
					}
					else
					{
						//System.out.println("RECU PAR repr "+this.getUserId()+" ROUTE REPLY "+tMsg.toString());
						int relais = this.neighboorlist.getLinkToRepresentant(MWACRouteAssistant.getPreviousId(tMsg.getRoute(),this.getUserId()));
						if (relais==MWACRouteAssistant.UNDEFINED_ID) relais=this.neighboorlist.getLinkToRepresentant(tMsg.getReceiver());
						if (relais==MWACNeighboorList.UNDEFINED_ID) 
							System.out.println("!!!!!! (2) Moi repr "+this.getUserId()+" ne trouve pas le précédent de "+interaction.frame.getSender()+" dans la route "+MWACRouteAssistant.routeToString(tMsg.getRoute())+" (pour un ROUTE REPLY)");
						else
							this.sendFrame(new MWACFrame(this.getUserId(),relais,new MWACMessage_RouteReply(tMsg.getSender(),tMsg.getReceiver(),tMsg.getIdRequest(),tMsg.getRoute())));
					}
				}
				else if(this.getRole()==roleLINK)
				{

					//System.out.println("RECU PAR LINK"+this.getUserId()+" ROUTE REPLY "+tMsg.toString());
					int dest;
					if(this.neighboorlist.contains(tMsg.getReceiver()))
					{
						dest = tMsg.getReceiver();
						//System.out.println("Le dest "+dest+" est mon voisin");
					}
					else if(MWACGroupAssistant.containsGroup(tMsg.getSender(), this.getGroups()))
					{
						dest = MWACRouteAssistant.getLastId(tMsg.getRoute());
						//System.out.println("Le dest "+dest+" est mon voisin representant");
					}
					else 
					{
						dest=(MWACRouteAssistant.getPreviousId(tMsg.getRoute(),interaction.frame.getSender()));
						//System.out.println("Le dest "+dest+" est le precedent");
					}
					if (dest==MWACRouteAssistant.UNDEFINED_ID) 
						System.out.println("!!!!!! Moi link "+this.getUserId()+" ne trouve pas le précédent de "+interaction.frame.getSender()+" dans la route "+MWACRouteAssistant.routeToString(tMsg.getRoute())+" (pour un ROUTE REPLY)");
					else
						this.sendFrame(new MWACFrame(this.getUserId(),dest,new MWACMessage_RouteReply(tMsg)));

				}
			}
			break;
		case MWACMessage.msgPOSSIBLE_ORGANIZATIONAL_INCOHERENCE_NOTIFICATION:
			if (ENABLE_ORGANIZATIONAL_INCOHERENCE_VERIFICATION)
				if(this.role==roleREPRESENTATIVE)
				{
					MWACMessage_PossibleOrganizationalIncoherence tMsg = (MWACMessage_PossibleOrganizationalIncoherence) msg;
					//debug("L'agent REPRESENTANT "+this.getUserId()+" a recu une demande de verif de "+msg.getSender()+" concernant "+MWACRouteAssistant.routeToString(tMsg.getSuspectedGroups()));
					System.out.println("L'agent REPRESENTANT "+this.getUserId()+" a recu une demande de verif de "+msg.getSender()+" concernant "+MWACRouteAssistant.routeToString(tMsg.getSuspectedGroups()));
					int[] suspectedGroups = tMsg.getClonedSuspectedGroupsArray();
					for(int i=0;i<suspectedGroups.length;i++)
					{
						if(!this.organizationalIncoherenceManager.isSuspectedGroup(suspectedGroups[i]))
						{
							this.organizationalIncoherenceManager.add(tMsg.getSender(),suspectedGroups[i] );
							super.sendFrame(new MWACFrame(getUserId(),MWACFrame.BROADCAST_LINK,new MWACMessage_TTLRouteRequest(getUserId(),suspectedGroups[i],this.messageToTransmitQueue.getNextIdRequest(),(byte) MAX_HOP_RESEARCH_ORGANIZATIONAL_INCOHERENCE)));
						}						
					}
				}
				else
				{
					// not concerned
				}

			break;

		default:
			System.out.println("ERREUR!!!! NONE IMPLEMENTED MESSAGE "+msg.toString());
		}	
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
		if(this.role==roleREPRESENTATIVE)
			return this.getUserId();
		else
		{
			ArrayList<Integer> repr=this.neighboorlist.getRepresentativeIdentifiers();
			int nbRepr=repr.size();
			if(nbRepr==0)
			{
				switch(this.role)
				{
				case roleSIMPLEMEMBER:
				case roleLINK:
					System.out.println("<A"+this.getUserId()+","+roleToString(this.role)+">!!!!! ERREUR de cohérence entre le role et la liste de voisins");
				case roleNOTHING:
					// C'est normal pour eux
					return groupNONE;
				}
			}
			else if (nbRepr==1)
			{
				switch(this.role)
				{
				case roleSIMPLEMEMBER:
					return repr.get(0);
				case roleLINK:
					System.out.println("<B"+this.getUserId()+","+roleToString(this.role)+">!!!!! ERREUR de cohérence entre le role et la liste de voisins");
				case roleNOTHING:
					return groupNONE;
				}
			}
			else
			{
				if (this.role==roleLINK)
					return repr.get(0);
				else
				{
					if (this.role!=roleNOTHING) System.out.println("<C"+this.getUserId()+","+roleToString(this.role)+">!!!!! ERREUR de cohérence entre le role et la liste de voisins");
					return groupNONE;
				}
			}
		}
		System.out.println("<D"+this.getUserId()+","+roleToString(this.role)+">!!!!! ERREUR de cohérence entre le role et la liste de voisins");
		return groupNONE;
	}

	/**
	 * return the groups identifiers of this agent (interesting for link agent)
	 * @return the groups associated to the agent
	 */
	public int[] getGroups()
	{
		int[] res;

		if(this.getRole()!=roleLINK)
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
		if (this.role==roleSIMPLEMEMBER) return 3*SLEEP_TIME_SLOT;
		return 0;
	}


	//*************************************************************************************************************************************************************
	//receive Frame (ACO or MWAC)
	public synchronized void receivedFrame(Frame frame)	{

		if(ACO_Frame.IsAnAcoFrame(frame)) 
			this.received_ACO_Frame((ACO_Frame)frame);
		else 
			this.receivedFrame((MWACFrame) frame);

		//mise à jour du niveau d'energie estimé
		try{ this.RoutingTable.Update_Estimate_EnergyLevel(frame.getSender(), this.Estimate_Energy_Consumption());} catch (Exception e){}
	}

	//receive ACO Frame
	public synchronized void received_ACO_Frame(ACO_Frame frame){
		if((frame.getReceiver()==ACO_Frame.BROADCAST) || (frame.getReceiver()==this.getUserId())) {
			super.receivedFrame((Frame)frame);
			this.receivedAntQueue.push(frame);
		}
	}

	//receive MWAC Frame
	public synchronized void receivedFrame(MWACFrame frame){
		if( (frame.getReceiver()==MWACFrame.BROADCAST) || (frame.getReceiver()==this.getUserId()) || ((frame.getReceiver()==MWACFrame.BROADCAST_LINK) && this.getRole()==roleLINK) || ((frame.getReceiver()==MWACFrame.BROADCAST_REPRESENTATIVE) && this.getRole()==roleREPRESENTATIVE)) {
			super.receivedFrame((Frame)frame);
			this.receivedMessageQueue.push(frame);
		}
	}

	//estimation de la consommation d'energie    //taille moyenne d'une trame ACO_MWAC dans le fichier de configuration Configuration.jAava
	public float Estimate_Energy_Consumption(){
			return (float) (((LinearBatteryBasicModel.STATE_SENDING_CONSUMPTION + LinearBatteryBasicModel.STATE_RECEIVING_CONSUMPTION)* simulation.entities.Object.ms_BYTERATE *(Configuration.NetWork_Size/2)) / 1000);
	}
	//*************************************************************************************************************************************************************

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
			case roleNOTHING: super.setColor(colorNOTHING); break;
			case roleSIMPLEMEMBER:super.setColor(colorSIMPLEMEMBER); break;
			case roleLINK:super.setColor(colorLINK); break;
			case roleREPRESENTATIVE:super.setColor(colorREPRESENTATIVE); break;
			default: super.setColor(Color.BLACK);
			}
		}
	}

	/**
	 * send a message. This method can be called by all agents (REPRESENTATIVE, LINK, SIMPLE_MEMBER)
	 * @param s the string of the message
	 * @param receiver the receiver of this message
	 */
	public void sendMessage(int receiver, String s)
	{
		//*******************************************************************************************************************************************************
		//generer une fourmi
		ACO_Message_Forward_Ant fourmi = (ACO_Message_Forward_Ant) this.AntManager.new_Forward_Ant(this.getUserId(), receiver, s);
		
		//quand j'envoie la premiere fourmi, j'active les deux chronometre 
		if (this.First_Initialisation_Message == false) {this.First_Initialisation_Message = true; this.AntManager.Timer_RoundOfEnergyUpdate.start(); this.AntManager.Timer_RoundOfEvaporation.start();}

		//ajouter la fourmi à la liste des fourmis deja traitée
		this.AlreadyProcessedForwardAnt.isAlreadyProcessed(fourmi);

		//si je suis un voisin direct de la station de base
		if (this.RoutingTable.find_Entry(fourmi.getReceiver()) != null ){
			this.notifyEvent(new SendedMessageEvent(this.getSystemId(), (ACO_Message_Forward_Ant)fourmi.clone()));
			this.sendFrame(new ACO_Frame (this.getUserId(), receiver, fourmi));
		}
		else {

			int next_agent = this.RoutingTable.get_Next_Agent(this.RoutingTable, this.neighboorlist.listOfRoutingAgent(this.role), new int [0]);

			if (next_agent != this.RoutingTable.NotExist){
				this.notifyEvent(new SendedMessageEvent(this.getSystemId(), (ACO_Message_Forward_Ant)fourmi.clone()));
				this.sendFrame(new ACO_Frame(this.getUserId(), next_agent, fourmi));
			}
			else {
				System.out.println("pas de chemin possible pour transmettre le message" + " __ à partir du noeud source");
				super.notifyEvent(new MessageNotTransmittedEvent(this.getSystemId(), fourmi, "No Path Found"));	
			}
		}
		//********************************************************************************************************************************************************
	}


	/**
	 * send a conflict resolution message
	 * this message is send by a representative agent in conflict with one or more others representative agent
	 */
	private void sendConflictResolution() 
	{
		if(DEBUG) debug(getUserId()+">>> Envoie par d'un message de RESOLUTION DE CONFLIT");
		super.sendFrame(new MWACFrame(getUserId(),MWACFrame.BROADCAST,new MWACMessage_ConflictResolution(getUserId(),MWACMessage.BROADCAST,this.score())));
	}

	/**
	 * Procedure of verification of organizationnal incoherence
	 */
	private void incoherenceNotificationProcedure()
	{
		//System.out.println("* TEST INCOHERENCE (role="+MWACAgent.roleToString(this.getRole())+",id="+this.getUserId()+",groups="+MWACRouteAssistant.routeToString(this.getGroups())+").");
		Vector<Integer> v = this.neighboorlist.suspiciousNeighboorsGroups(this.getGroups());
		if (!v.isEmpty()) 
		{
			//debug("DETECTION POSSIBLE INCOHERENCE SIGNALEE PAR "+this.getUserId()+" "+v);
			System.out.println("\nDETECTION POSSIBLE INCOHERENCE SIGNALEE PAR <"+this.getUserId()+","+roleToString(this.getRole())+","+MWACRouteAssistant.routeToString(this.getGroups())+"). Les groupes suspects sont "+v+"\n"+this.neighboorlist.toString());
			this.sendIncoherenceNotification(v);
		}
	}

	/**
	 * send an possible organizational incoherence notification message
	 * @param v the suspected groups
	 */
	private void sendIncoherenceNotification(Vector<Integer> v)
	{
		int[] array = new int[v.size()];
		for(int i=0;i<v.size();i++) array[i]=v.get(i);

		if(DEBUG) debug(">>> Envoie par d'un message de suspission d'incoherence organisationnelle     <"+getUserId()+","+roleToString(this.role)+","+this.getGroup()+">");
		super.sendFrame(new MWACFrame(getUserId(),MWACFrame.BROADCAST,new MWACMessage_PossibleOrganizationalIncoherence(getUserId(),array)));

	}
	/**
	 * send a presentation message
	 */
	private void sendPresentation()
	{
		//Frame f=new Frame(getUserId(),dest,new ASTRO_Presentation(getUserId(),dest,role,group));
		if(DEBUG) debug(">>> Envoie par d'un message de PRESENTATION     <"+getUserId()+","+roleToString(this.role)+","+this.getGroup()+">");
		super.sendFrame(new MWACFrame(getUserId(),MWACFrame.BROADCAST,new MWACMessage_Presentation(getUserId(),this.role,this.getGroups())));
	}

	/**
	 * send an introduction message
	 */
	private void sendIntroduction()
	{
		if(DEBUG) debug(">>> Envoie par d'un message d'INTRODUCTION      <"+getUserId()+","+roleToString(this.role)+","+this.getGroup()+">");
		super.sendFrame(new MWACFrame(super.getUserId(),MWACFrame.BROADCAST,new MWACMessage_Introduction(this.getUserId())));
	}

	/**
	 * send a message to request the close neighboors
	 */
	private void sendWhoAreMyNeighboors()
	{
		//Frame f=new Frame(getUserId(),dest,new ASTRO_Presentation(getUserId(),dest,role,group));
		if(DEBUG) debug(">>> Envoie par d'un message de WHOAREMYNEIGHBOORS   <"+getUserId()+","+roleToString(this.role)+","+this.getGroup()+">");
		super.sendFrame(new MWACFrame(getUserId(),MWACFrame.BROADCAST,new MWACMessage_WhoAreMyNeighboors(getUserId(),role,this.getGroups())));
	}



	/**
	 * return the role
	 * @return the role
	 */
	public byte getRole() 
	{
		return this.role;
	}




	/** returns the string signature of the agent which will be viewable under the spy windows
	 * @return the string signature of the agent
	 */

	public String toSpyWindows()
	{
		//*********************************************************************************************************************************************************
		String ACO_AFFICHAGE="<BR>" + "<BR>"+this.RoutingTable.toHTML()+ "<BR>"+ this.AlreadyProcessedForwardAnt.toHTML()+ "<BR>"+ this.AlreadyProcessedBackwardAnt.toHTML();

		return "<HTML>"+"<B>Id</B>="+this.getUserId()+"    <B>Role</B>="+roleToString(this.getRole())+"    <B>Group</B>="+MWACGroupAssistant.groupsToString(this.getGroups())+"    <B>Energy</B>="+String.format("%.2f",this.getBattery().getActualAmountOfEngergy())+"    <B>Range</B>="+this.getRange()+"<BR><BR>"+this.neighboorlist.toHTML()+ACO_AFFICHAGE+"</HTML>";
		//**********************************************************************************************************************************************************
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
		case roleREPRESENTATIVE	: return "ROLE_REPRESENTATIVE";
		case roleLINK				: return "ROLE_LINK";
		case roleSIMPLEMEMBER		: return "ROLE_SIMPLEMEMBER";
		case roleNOTHING			: return "ROLE_NOTHING";
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
		}
	}






}
