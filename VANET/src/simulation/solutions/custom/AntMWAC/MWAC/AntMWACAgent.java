package simulation.solutions.custom.AntMWAC.MWAC;
import java.awt.Color;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Vector;
import java.util.Random;
import simulation.battery.BatteryModel;
import simulation.battery.custom.LinearBatteryBasicModel;
import simulation.entities.Agent;
import simulation.entities.Object;
import simulation.events.system.MessageNotTransmittedEvent;

import simulation.events.system.EnvoiFourmisEvent;
import simulation.events.system.EnvoiMWACEvent;
import simulation.events.system.PositionEvent;
import simulation.events.system.ReceivedAntEvent;
import simulation.events.system.ReceivedDataEvent;
import simulation.events.system.ReceivedMessageEvent;
import simulation.events.system.RoleModificationEvent;
import simulation.events.system.SendedAntEvent;
import simulation.events.system.SendedDataEvent;
import simulation.events.system.SendedMessageEvent;
import simulation.events.system.SolicitsAgentEvent;

import simulation.messages.*;
import simulation.multiagentSystem.MAS;
import simulation.solutions.custom.AntMWAC.MWAC.Messages.*;
import simulation.utils.aDate;
import  simulation.solutions.custom.AntMWAC.Ant.*;
import simulation.solutions.custom.AntMWAC.Ant.Messages.AntMessage_Backward;
import simulation.solutions.custom.AntMWAC.Ant.Messages.AntMessage_Forward;
import simulation.solutions.custom.AntMWAC.Ant.Messages.AntMessage_Initialisation;
import simulation.solutions.custom.AntMWAC.Ant.Messages.AntMessage_Update;

import simulation.solutions.custom.AntMWAC.MWAC.MWACGroupAssistant;
import simulation.solutions.custom.AntMWAC.MWAC.MWACNeighboorList;
import simulation.solutions.custom.AntMWAC.MWAC.MWACNetworkPartialKnowledgeManager;
import simulation.solutions.custom.AntMWAC.MWAC.MWACRouteAssistant;
import simulation.solutions.custom.AntMWAC.MWAC.TripletIdRoleGroup;
import simulation.solutions.custom.AntMWAC.MWAC.Messages.MWACFrame;
import simulation.solutions.custom.AntMWAC.MWAC.Messages.MWACMessage;
import simulation.solutions.custom.AntMWAC.MWAC.Messages.MWACMessage_CheckRouteReply;
import simulation.solutions.custom.AntMWAC.MWAC.Messages.MWACMessage_CheckRouteRequest;
import simulation.solutions.custom.AntMWAC.MWAC.Messages.MWACMessage_ConflictResolution;
import simulation.solutions.custom.AntMWAC.MWAC.Messages.MWACMessage_Data;
import simulation.solutions.custom.AntMWAC.MWAC.Messages.MWACMessage_PossibleOrganizationalIncoherence;
import simulation.solutions.custom.AntMWAC.MWAC.Messages.MWACMessage_Presentation;
import simulation.solutions.custom.AntMWAC.MWAC.Messages.MWACMessage_RouteReply;
import simulation.solutions.custom.AntMWAC.MWAC.Messages.MWACMessage_RouteRequest;
import simulation.solutions.custom.AntMWAC.MWAC.Messages.MWACMessage_RoutedData;
import simulation.solutions.custom.AntMWAC.MWAC.Messages.MWACMessage_TTLRouteRequest;






/********************************************************************************************/
public class AntMWACAgent extends Agent implements ObjectAbleToSendMessageInterface{

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
	private static int groupNONE = -1;
	/** role of the agent */
	private byte role;
	/** received message queue */
	private FrameFIFOStack receivedMessageQueue;
	/** message to send (if the agent is Representative)*/
	private MWACMessageFIFOStack messageToTransmitQueue;
	/** neighboorList */
	private MWACNeighboorList neighboorlist;
	
	private MWACAlreadyProcessedRouteRequestManager alreadyProcessedRouteRequestManager;
	
	private MWACNetworkPartialKnowledgeManager networkPartialKnowledgeManager;
	
	private MWACIncoherenceList organizationalIncoherenceManager;

     

    public  boolean mustsendwithAnts = true;
	

	


	// (Anca)
	private boolean isUsurper = false;
	private boolean mustUsurpsWorkstationID = false;
	private boolean mustUsurpsNeighboorID = false;
	private boolean mustUsurpGroups = false; // does not include all groups in the presentation message
	private boolean mustUsurpLink = false; // claims it is a link to a new group
	private int realId;
	// (Anca)
	
	private int[] claimedGroups;
	private byte claimedRole;


	// (Anca) -- id suspected to be link usurpator
	private int suspectedId = -1;
	private aDate possibleLinkUsurpationDate = null;
	
	
	/** Memorize the energy level when the agent has been elected representative */
	private float pourcentOfAvailableEnergyWhenElectedRepresentative;
	
	
	/****les fourmis************************/
	
	private AntManager gestionaire_fourmis;
	private Pheromone_Table table_pheromone ;
	private ReceivedRoutes routeAnt;
	private ReceivedRoutes routeMWAC;
	private table_taux_occupation taux_occupation ;
	public  int cpt ; 
    public boolean notification_position;
	/********************************************************************************************/
	public AntMWACAgent(MAS mas,Integer  id, Float energy,Integer  range)
	{   
		super(mas,id,range);
		
		this.role=roleNOTHING;
		this.receivedMessageQueue=new FrameFIFOStack();
		this.neighboorlist=new MWACNeighboorList();
		this.messageToTransmitQueue=null;	
		this.alreadyProcessedRouteRequestManager=null;
		this.networkPartialKnowledgeManager=null;
		this.organizationalIncoherenceManager=null;
		this.pourcentOfAvailableEnergyWhenElectedRepresentative=0;
		this.realId = id;
		this.cpt=0;
		LinearBatteryBasicModel	batterie = new LinearBatteryBasicModel(mas,this.getSystemId(),1000,1000);
		this.setBattery(batterie); //creer une batterie lineaire  pour l'agent 
		
		
	    /**** les fourmis************/
		this.gestionaire_fourmis = new AntManager (); 
		this.table_pheromone =   new Pheromone_Table(); 
		this.routeAnt=new ReceivedRoutes();
		this.routeMWAC=new ReceivedRoutes();
		this.taux_occupation =new  table_taux_occupation();
        this.notification_position =false;
		/*** demarrage des chrono*******************************/
		this.gestionaire_fourmis.Timer_RoundOfEnergyUpdate.start(); 
		this.gestionaire_fourmis.Timer_RoundOfEvaporation.start();
		this.gestionaire_fourmis.Timer_RoundOfPrevent.start();
		this.gestionaire_fourmis.Timer_RoundOfTauxOccupation.start();
		
		
	}

	/********************************************************************************************/
	public void run()
	{

	
		System.out.println("Demarrage du AntMWACAgent "+this.getUserId());
		
       
		try{Thread.sleep(500);}catch(Exception e){}

		if (AntMWACAgent.DEBUG) debug("Envoie un message d'introduction");
		sendIntroduction();
		try{Thread.sleep(2*SLEEP_TIME_SLOT);}catch(Exception e){};
        
		InteractionContext interaction = new InteractionContext();
		if (!this.notification_position)
		{super.notifyEvent(new PositionEvent(this.getSystemId(),this.getPosition(),this.getMAS()));
		this.notification_position =true;
		}
		while(!isKilling() && !isStopping())
		{
		this.sleep(SLEEP_TIME_SLOT+this.extraWaitTime(this.role));
		
        while(  ((isSuspending()) && (!isKilling() && !isStopping())))  
        	try{Thread.sleep(SLEEP_TIME_SLOT);} catch(Exception e){};
        	
        while( this.receivedMessageQueue.isEmpty() && (!interaction.lostRepresentativeRole) && (!interaction.reinitializationOfTheRole)) 
		{
			
		    if (AntMWACAgent.ENABLE_ORGANIZATIONAL_INCOHERENCE_VERIFICATION)
		  {
			if (this.neighboorlist.dateOfLastPossibleIncoherenceDetection>0)
			if ( (new aDate()).differenceToMS(new aDate(this.neighboorlist.dateOfLastPossibleIncoherenceDetection))>DELAY_BEFORE_ORGANIZATIONAL_INCOHERENCE_VERIFICATION) 
			{ // We reported the problem to our representative
				this.incoherenceNotificationProcedure();	
				this.neighboorlist.dateOfLastPossibleIncoherenceDetection=0;
			}

			if (this.role==AntMWACAgent.roleREPRESENTATIVE)
			{
             int idOfAProblematicGroup = this.organizationalIncoherenceManager.existsProblem();
			 if (idOfAProblematicGroup!=MWACIncoherenceList.NO_REAL_INCOHERENCE)
			     {
				if (this.getUserId()>idOfAProblematicGroup)
					{
					System.out.println("REAL PROBLEM catch by "+this.getUserId()+" with group "+idOfAProblematicGroup+"\n"+this.organizationalIncoherenceManager.toString());

					interaction.lostRepresentativeRole=true;
					}
				else
					System.out.println("IGNORE REAL PROBLEM catch by "+this.getUserId()+" with group "+idOfAProblematicGroup+"\n"+this.organizationalIncoherenceManager.toString());
					}
			 }
				}

			if (this.pourcentOfAvailableEnergy()<(this.pourcentOfAvailableEnergyWhenElectedRepresentative/2.0))
				interaction.lostRepresentativeRole=true;
            
			
			
			/********************************************************************/
			if (this.gestionaire_fourmis.IsRoundOfEnergyUpdate())
				{
				this.sendFrame(new MWACFrame(this.getUserId(),MWACFrame.BROADCAST,new AntMessage_Update(this.getUserId(),MWACMessage.BROADCAST,(float)this.getBattery().getActualAmountOfEngergy())));
				Ant_Operations.estimate_energy_receiving(this.table_pheromone,new MWACFrame(this.getUserId(),MWACFrame.BROADCAST,new AntMessage_Update(this.getUserId(),MWACMessage.BROADCAST,(float)this.getBattery().getActualAmountOfEngergy())));
				this.gestionaire_fourmis.Timer_RoundOfEnergyUpdate.start(); 
				}
			
			
			if (this.gestionaire_fourmis.IsRoundOfPrevent())
				{for (int j=0;j<this.table_pheromone.tableau.size();j++)
					
				{
					 this.table_pheromone.get_Entry_At(j).set_PreventDestination(0);
				}
				   this.gestionaire_fourmis.Timer_RoundOfPrevent.start(); 
				}
			if (this.gestionaire_fourmis.IsRoundOfEvaporation())
				{for (int j=0;j<this.table_pheromone.tableau.size();j++)
					
				{
					float pheromone_initial=this.table_pheromone.get_Entry_At(j).get_PheromoneValue();
					float pheromone_update=pheromone_initial-0.001f;	
					//System.out.println(this.getUserId()+"je vais faire une MAJ de la valeur de oheromone qui était "+pheromone_initial+" à "+pheromone_update);
					if (pheromone_update<0.01)
						{
						   float nb =this.table_pheromone.get_Entry_At(j).get_hopcount()+1;
						   System.out.println(this.getUserId()+" le nbr de saut est "+nb+" et le rapp "+ 1/nb);
						this.table_pheromone.get_Entry_At(j).set_PheromoneValue(1/nb);
				     
					      
						}else  this.table_pheromone.get_Entry_At(j).set_PheromoneValue(pheromone_update);
				}
				 
				this.gestionaire_fourmis.Timer_RoundOfEvaporation.start();
				}
			
			
			if (this.gestionaire_fourmis.IsRoundOfTauxOccupation()){
				for (int j=0;j<this.taux_occupation.table.size();j++)
				{
					this.taux_occupation.get_Entry_At(j).set_taux(0);
				}
				this.gestionaire_fourmis.Timer_RoundOfTauxOccupation.start();
			}
            /******************************************************************/
			/** (Anca) **/					
			if (this.mustUsurpsWorkstationID || this.mustUsurpsNeighboorID || this.mustUsurpGroups || this.mustUsurpLink)
			{
				if( !this.isUsurper)
				{
					if (this.mustUsurpsWorkstationID) 
					{
						// we must adopt a malicious behavior
						this.isUsurper=true;

						this.setUserId(AntMWACAgent.WORKSTATION_ID);														
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


			/****************traitement de message recus**************************************************************************/
        
	 while((interaction.frame=this.receivedMessageQueue.pop())!=null)  if (!interaction.lostRepresentativeRole)  this.processMessage(interaction);
	 
		 
			/******************************************************************************************/
			
			if(interaction.reinitializationOfTheRole)
			{
			this.neighboorlist=new MWACNeighboorList(); //reinitialiser la  table de pheromone
			}
		
			this.computeRoleDecision(interaction);

			if(interaction.roleConfict)
			{
			if(AntMWACAgent.DEBUG) debug("Je suis en conflit avec "+ this.neighboorlist.getRepresentativeIdentifiers());
			this.sendConflictResolution();
			}
			else if(interaction.mustSendWhoAreMyNeighoors)  this.sendWhoAreMyNeighboors();
			else if(interaction.mustSendAPresentation)  this.sendPresentation();

			interaction.init();

			try{Thread.sleep(SLEEP_TIME_SLOT);}catch(Exception e){}
		}

		System.out.println("Fin du thread "+this.getUserId());
	}

	/********************************************************************************************/
	
	public void computeRoleDecision(InteractionContext interaction)
	{
		byte newRole=this.role;	

		if(this.neighboorlist.isEmpty())  newRole=AntMWACAgent.roleNOTHING;
		else
		{
			switch(this.role)
			{
			case AntMWACAgent.roleNOTHING:
			case AntMWACAgent.roleSIMPLEMEMBER:
			{
				int n = this.neighboorlist.getNbRepresentative();
				if(n==0)  newRole=this.roleREPRESENTATIVE;
				else if(n==1) 
				{
					newRole=this.roleSIMPLEMEMBER;
					if(interaction.hasPreviouslyLostRepresentativeRole) newRole=this.role;
				}
				else newRole=this.roleLINK;
			}
			break;
			case AntMWACAgent.roleLINK:
			{
				int n = this.neighboorlist.getNbRepresentative();
				if(n==0) newRole=this.roleREPRESENTATIVE;
				else if(n==1) newRole=this.roleSIMPLEMEMBER;
				else newRole=this.roleLINK;
			}
			break;
			case AntMWACAgent.roleREPRESENTATIVE:
			{
				if(interaction.lostRepresentativeRole||interaction.hasPreviouslyLostRepresentativeRole)
				{
					newRole=AntMWACAgent.roleNOTHING;
					interaction.roleConfict=false;	// Il n'y a plus de conflit
				}
				else
				{
					if(this.neighboorlist.getNbRepresentative()>0) interaction.roleConfict=true;
					newRole=AntMWACAgent.roleREPRESENTATIVE;
				}
			}
			break;
			}
		}

		
		if (this.role!=newRole)
		{
			if(AntMWACAgent.DEBUG) debug("Je change de role "+AntMWACAgent.roleToString(this.role)+" => "+AntMWACAgent.roleToString(newRole)+" lost="+interaction.lostRepresentativeRole+"  prevLost="+interaction.hasPreviouslyLostRepresentativeRole);
			this.setRole(newRole);
			switch(newRole)
			{
			case AntMWACAgent.roleREPRESENTATIVE:
				this.pourcentOfAvailableEnergyWhenElectedRepresentative=this.pourcentOfAvailableEnergy();
				this.alreadyProcessedRouteRequestManager=new MWACAlreadyProcessedRouteRequestManager();
				this.messageToTransmitQueue=new MWACMessageFIFOStack();
				this.networkPartialKnowledgeManager=new MWACNetworkPartialKnowledgeManager();
				this.organizationalIncoherenceManager=new MWACIncoherenceList();
				interaction.mustSendWhoAreMyNeighoors=true;
				break;
			case AntMWACAgent.roleLINK:
				this.pourcentOfAvailableEnergyWhenElectedRepresentative=0;
				this.alreadyProcessedRouteRequestManager=new MWACAlreadyProcessedRouteRequestManager();
				this.networkPartialKnowledgeManager=null;
				this.messageToTransmitQueue=null;
				this.organizationalIncoherenceManager=null;
				interaction.mustSendAPresentation=true;
				break;
			case AntMWACAgent.roleNOTHING:
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
			case AntMWACAgent.roleSIMPLEMEMBER:
				this.alreadyProcessedRouteRequestManager=null;
				this.networkPartialKnowledgeManager=null;
				this.messageToTransmitQueue=null;
				this.organizationalIncoherenceManager=null;
				interaction.mustSendAPresentation=true;
				break;
			}
			if ((AntMWACAgent.DEBUG) && newRole==AntMWACAgent.roleREPRESENTATIVE)  this.debug("Je deviens ROLE_REPRESENTANT");
		}


	}

	/********************************************************************************************/
	public void processMessage(InteractionContext interaction)
	{	
		super.notifyEvent(new SolicitsAgentEvent(this.getSystemId(),this.cpt));
		MWACFrame frame = interaction.frame;  // Message extraction from the frame
		MWACMessage msg = MWACMessage.createMessage(interaction.frame.getData());
		Ant_Operations.estimate_energy_sending(frame.getSender(),this.table_pheromone,frame);
		
	
		switch(msg.getType())  
		{
		case MWACMessage.msgINITIALIZATION:
		
			AntMessage_Initialisation msg_init = (AntMessage_Initialisation) msg ;//specifier le msg 
			
			TripletIdRoleGroup voisin = this.neighboorlist.get(msg_init.getSender());
			
		 /*if (
				(this.role == AntMWACAgent.roleREPRESENTATIVE  &&  this.getUserId()!=WORKSTATION_ID   && voisin!= null&& voisin.role ==AntMWACAgent.roleLINK)
						
			||(this.role == AntMWACAgent.roleLINK  &&  this.getUserId()!=WORKSTATION_ID   && voisin!= null && voisin.role ==AntMWACAgent.roleREPRESENTATIVE)
				
			||((this.role == AntMWACAgent.roleREPRESENTATIVE ||this.role == AntMWACAgent.roleLINK )&& msg_init.getSender()==AntMWACAgent.WORKSTATION_ID) )
		 {*/
			 
			 
		    
	/***************************************************************************************************/
			
//		    Entry_pheromone_table emetteur =  this.table_pheromone.find_Entry(msg.getSender());
//			if  (  emetteur.get_hopcount() >= msg_init.get_HopCount())// &(this.getUserId()!= AntMWACAgent.WORKSTATION_ID))
//			{  
//			emetteur.set_hopcount(msg_init.get_HopCount());
//			emetteur.set_EnergyLevel(msg_init.get_EnergyLevel());
//			//this.table_pheromone.Insert(entree);
//			MWACFrame trame = new MWACFrame (this.getUserId(), MWACFrame.BROADCAST, 
//			new AntMessage_Initialisation(this.getUserId(), MWACMessage.BROADCAST, (float) this.getBattery().getActualAmountOfEngergy(),msg_init.get_HopCount()+1));
//			this.sendFrame(trame);
//			Ant_Operations.estimate_energy_receiving( this.table_pheromone, trame);
//			
//			
//			}
				  
		//}
			/***************************************************************************************************/
			 Entry_pheromone_table  entree = new Entry_pheromone_table (msg_init.getSender(),msg_init.get_EnergyLevel(),Ant_Operations.calcul_pheromone(msg_init.getSender(),msg_init.get_HopCount() ,voisin.role), msg_init.get_HopCount());
			    
			    
			    Entry_pheromone_table emetteur =  this.table_pheromone.find_Entry(msg.getSender());
				if  ( (emetteur == null || emetteur.get_hopcount() > entree.get_hopcount()) && (this.getUserId()!= AntMWACAgent.WORKSTATION_ID))
				{  this.table_pheromone.Insert(entree);
				MWACFrame trame = new MWACFrame (this.getUserId(), MWACFrame.BROADCAST, 
				new AntMessage_Initialisation(this.getUserId(), MWACMessage.BROADCAST, (float) this.getBattery().getActualAmountOfEngergy(),msg_init.get_HopCount()+1));
				this.sendFrame(trame);
				Ant_Operations.estimate_energy_receiving( this.table_pheromone, trame);
			
			}
		
		break;
		case MWACMessage.msgFORWARD_ANT:
	      AntMessage_Forward fourmi_forward = (AntMessage_Forward)msg; //specifier le msg 
	      
			if( frame.getReceiver() == this.getUserId())
			{   
				this.cpt=cpt+1; 
				super.notifyEvent(new SolicitsAgentEvent(this.getSystemId(),this.cpt));
				if(fourmi_forward.getReceiver() == this.getUserId()) // si je suis le destinataire de cette fourmis 
		         {  		
				int [] chemin =MWACRouteAssistant.cloneRoute(fourmi_forward.get_route(), fourmi_forward.get_route().length+1);
				chemin[chemin.length-1]=frame.getSender();
				fourmi_forward.set_route(chemin);
				fourmi_forward.set_length(chemin.length);
				fourmi_forward.set_nbhoup(fourmi_forward.get_nbhoup()+1);
				
				this.routeAnt.put(fourmi_forward.getSender(),fourmi_forward.get_route().clone());
				super.notifyEvent(new ReceivedMessageEvent(this.getSystemId(),msg));
				super.notifyEvent(new ReceivedAntEvent(this.getSystemId(),fourmi_forward));
			    AntMessage_Backward fourmi_back = new AntMessage_Backward(fourmi_forward); 	 //instancier une fourmi backward 		
			
			    if (MWACRouteAssistant.getLastId(fourmi_back.get_route())!= this.table_pheromone.NotExist)	
			    	{Entree_taux_occupation ligne1 =new Entree_taux_occupation(MWACRouteAssistant.getLastId(fourmi_back.get_route()) ,1);
				     this.taux_occupation.Insert(ligne1);
			    	 this.sendFrame(new MWACFrame (this.getUserId(),MWACRouteAssistant.getLastId(fourmi_back.get_route()) , fourmi_back));	
			    	
			    	
			    	Ant_Operations.estimate_energy_receiving(this.table_pheromone,new MWACFrame (this.getUserId(),MWACRouteAssistant.getLastId(fourmi_back.get_route()) , fourmi_back) );
			    	}
			    else   	
			super.notifyEvent(new MessageNotTransmittedEvent(this.getSystemId(),fourmi_back,"la fBack  n'a pas de prochain relai"));
		} 

				
	else 
		{
					
		// le destinataire est un voisin direct 
		if (this.table_pheromone.find_Entry(fourmi_forward.getReceiver()) != null )
			{  
			int [] chemin =MWACRouteAssistant.cloneRoute(fourmi_forward.get_route(), fourmi_forward.get_route().length+1);
			chemin[chemin.length-1]=frame.getSender();
			fourmi_forward.set_route(chemin);
			fourmi_forward.set_length(chemin.length);
			
			fourmi_forward.set_nbhoup(fourmi_forward.get_nbhoup()+1);
			Entree_taux_occupation ligne1 =new Entree_taux_occupation(fourmi_forward.getReceiver() ,1);
		    this.taux_occupation.Insert(ligne1);
			this.sendFrame(new MWACFrame(this.getUserId(), fourmi_forward.getReceiver(), fourmi_forward));
			super.notifyEvent(new EnvoiFourmisEvent(this.getSystemId(),this.getUserId(),fourmi_forward.getReceiver()));
			Ant_Operations.estimate_energy_receiving(this.table_pheromone,new MWACFrame(this.getUserId(), fourmi_forward.getReceiver(), fourmi_forward));
			
			}
					
		else {  //sinon je suis un intermédiare  

				//je verfie si ce n'est pas un retour arriere (si le dernier element de la memoire est moi-meme)
						
			if (this.getUserId() == MWACRouteAssistant.getLastId(fourmi_forward.get_route().clone()) )
			{
				/// c'est un retour arrrière 
				
				//je me supprime de la route
				fourmi_forward.set_route(MWACRouteAssistant.removeLastId(fourmi_forward.get_route().clone()));
				fourmi_forward.set_length(fourmi_forward.get_route().length);
				fourmi_forward.set_nbhoup(fourmi_forward.get_nbhoup()+1);
				
				System.out.println("retour ariere  vers"+ this.getUserId());
				//if (this.role != AntMWACAgent.roleNOTHING && this.role!=AntMWACAgent.roleSIMPLEMEMBER)
			    this.table_pheromone.find_Entry(frame.getSender()).set_PreventDestination(1);  	
		
									
			}
			else  {
				int [] chemin =MWACRouteAssistant.cloneRoute(fourmi_forward.get_route(), fourmi_forward.get_route().length+1);
				chemin[chemin.length-1]=frame.getSender();
				fourmi_forward.set_route(chemin);
				fourmi_forward.set_length(chemin.length);
				fourmi_forward.set_nbhoup(fourmi_forward.get_nbhoup()+1);
				
			}  // ce n'est pas un retour arière je calcule le nextAgent
				
			int next_agent = Ant_Operations.Next_Agent(this.table_pheromone,fourmi_forward.get_route().clone(),taux_occupation); 
               
			if (next_agent != this.table_pheromone.NotExist) {
				Entree_taux_occupation ligne1 =new Entree_taux_occupation(next_agent ,1);
			    this.taux_occupation.Insert(ligne1);
				this.sendFrame(new MWACFrame(this.getUserId(), next_agent, fourmi_forward));
				super.notifyEvent(new EnvoiFourmisEvent(this.getSystemId(),this.getUserId(),next_agent));
		        Ant_Operations.estimate_energy_receiving(this.table_pheromone,new MWACFrame(this.getUserId(), next_agent, fourmi_forward));
			}
			
		    else { // le nextAgent=-1

							// (à revoir ) si je suis l'expediteur, et je ne trouve pas de prochains relais, alors impossible de la transmettre
				if (fourmi_forward.getSender() == this.getUserId()) super.notifyEvent(new MessageNotTransmittedEvent(this.getSystemId(), fourmi_forward, " pas de chemin vers la destination"));

				else 
				{
					
				int previous = MWACRouteAssistant.getLastId(fourmi_forward.get_route().clone());
		        
			  //  System.out.println("retour ariere de "+ this.getUserId() + "   vers  " + previous);
			    if  (previous != this.table_pheromone.NotExist) 
			    	{this.sendFrame(new MWACFrame(this.getUserId(), previous,fourmi_forward));
			    	Entree_taux_occupation ligne1 =new Entree_taux_occupation(previous ,1);
				    this.taux_occupation.Insert(ligne1);
			    	Ant_Operations.estimate_energy_receiving(this.table_pheromone,new MWACFrame(this.getUserId(), previous,fourmi_forward) );
			    	}
			   // else super.notifyEvent(new MessageNotTransmittedEvent(this.getSystemId(),fourmi_forward,"il n'a pas de previous"));		
				}
				}
		          }
				}									
			
			}
				
				
		break;
		
		case MWACMessage.msgBACKWARD_ANT:
		
		     
			if (frame.getReceiver()==this.getUserId())
			{	
		     
			    AntMessage_Backward fourmi_backward = (AntMessage_Backward)msg;	// on specialise le message 			
                
				// mis à jour de la valeur de pheromone (en meme temps le renforcement et l'evaporation)
			Ant_Operations.MAJ_pheromone(frame.getSender(),this.table_pheromone,fourmi_backward.get_length_route());

				//si je ne suis pas le destinataire de ce message, je le passe donc à mon predecesseur
		  if(fourmi_backward.getReceiver()!=this.getUserId()) {
					
		     
			 fourmi_backward.set_route(MWACRouteAssistant.removeLastId(fourmi_backward.get_route()));
			 
		     int previous = MWACRouteAssistant.getLastId(fourmi_backward.get_route());
		     Entree_taux_occupation ligne =new Entree_taux_occupation(previous ,1);
			 this.taux_occupation.Insert(ligne);
		    this.sendFrame(new MWACFrame(this.getUserId(), previous ,fourmi_backward));
		     Ant_Operations.estimate_energy_receiving(this.table_pheromone,new MWACFrame(this.getUserId(), previous ,fourmi_backward));
				}
		  else 
		       {
				//super.notifyEvent(new ReceivedMessageEvent(this.getSystemId(), (Message)fourmi_backward.clone()));
				}
			}						
			
		
		break;
		
		case MWACMessage.msgUPDATE:
			//on specialise le mesg 
			AntMessage_Update  update_msg = (AntMessage_Update)msg;

            Entry_pheromone_table e  =this.table_pheromone.find_Entry(frame.getSender());
            if (e != null)  e.set_EnergyLevel(update_msg.get_Energy_Level());
			
		break;
		/*****************************************************************************************/
		
		case MWACMessage.msgINTRODUCTION:
		{
			this.neighboorlist.put(msg.getSender(),AntMWACAgent.roleNOTHING, AntMWACAgent.groupNONE);
			interaction.mustSendWhoAreMyNeighoors=true;
		}
		break;
		case MWACMessage.msgWHO_ARE_MY_NEIGHBOORS:
		case MWACMessage.msgPRESENTATION:
		{
			float confidence = 1;
            float valeur_pheromone =0.01f;
            byte role = ((MWACMessage_Presentation)msg).getRole();
            switch (role ){
            case AntMWACAgent.roleLINK : 
            	valeur_pheromone  = Parametres.delta_liaison;
            	break;
            	
            case  AntMWACAgent.roleREPRESENTATIVE :
            	valeur_pheromone = Parametres.delta_representant;
            	break;
            case  AntMWACAgent.roleSIMPLEMEMBER :
            	valeur_pheromone = Parametres.delta_simple_membre;
            	break;
            
            }
            
            int nb_saut=1000;

            float energy = (float) ((int)(Object.ms_BYTERATE*((double)frame.getVolume())) * LinearBatteryBasicModel.STATE_SENDING_CONSUMPTION)/1000;
            int moyen=this.table_pheromone.mean_hopcount(this.table_pheromone);
            if (moyen!=0 && moyen!=1000) { 
            nb_saut=moyen;
            // on calcule la valeur de pheromone en fonction de nombre de sauts
            valeur_pheromone=Ant_Operations.calcul_pheromone(msg.getSender(),nb_saut,role);
            
            }
           	            
			Entry_pheromone_table  entree_ph = new Entry_pheromone_table (msg.getSender(),2000 -energy,valeur_pheromone,nb_saut);
			Entry_pheromone_table elt  =  this.table_pheromone.find_Entry(msg.getSender());
			
			if (elt ==null) this.table_pheromone.Insert(entree_ph);
			else System.out.println("l'élément existe ");
			
			
			this.neighboorlist.put( msg.getSender(),((MWACMessage_Presentation)msg).getRole(),  ((MWACMessage_Presentation)msg).getClonedGroupArray());
			interaction.mustSendAPresentation=   interaction.mustSendAPresentation || 
			(msg.getType()==MWACMessage.msgWHO_ARE_MY_NEIGHBOORS) ||
			this.neighboorlist.put(msg.getSender(),((MWACMessage_Presentation)msg).getRole(), ((MWACMessage_Presentation)msg).getClonedGroupArray());
            

		}
		break;
		
		case MWACMessage.msgCONFLICT_RESOLUTION:
		{
			this.neighboorlist.put(msg.getSender(), AntMWACAgent.roleREPRESENTATIVE, msg.getSender());
			if(this.role==AntMWACAgent.roleREPRESENTATIVE && !interaction.lostRepresentativeRole)
			{
				int scoreInMsg = ((MWACMessage_ConflictResolution)msg).getScore();
				if((scoreInMsg>this.score()) || ((scoreInMsg==this.score()) && this.getUserId()<msg.getSender()))
				{
					if(AntMWACAgent.DEBUG) debug("*PERDU* contre "+msg.getSender());
					interaction.lostRepresentativeRole=true;
					interaction.mustSendAPresentation=true;
					interaction.debugPerdu=msg.getSender();
				}
				else
				{
					if(AntMWACAgent.DEBUG) debug("*GAGNE* contre "+msg.getSender());
					this.neighboorlist.put(msg.getSender(),this.roleNOTHING,this.groupNONE);
					if(AntMWACAgent.DEBUG) debug("Je montre que je gagne mon conflit");
					this.sendConflictResolution(); 
					interaction.mustSendAPresentation=false;
				}
			}
			else
			{
				// I have already lost my reprentative role against with another agent
				if (interaction.lostRepresentativeRole) if(AntMWACAgent.DEBUG) debug("*DEJA PERDU* contre "+interaction.debugPerdu+" bien avant "+msg.getSender()+"    role="+this.role+"  lost="+interaction.lostRepresentativeRole);
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
				{   
					//super.notifyEvent(new ReceivedDataEvent(this.getSystemId(), (MWACMessage_Data)tMsg.clone()));
					this.receiveMessage(tMsg);
				
					} 
				else if(this.role==AntMWACAgent.roleREPRESENTATIVE)
				{
			     // Je suis le representant de l'?metteur (mon membre envoit directement le message)
				  this.sendMessage(tMsg);
				}
			}
			break;
		case MWACMessage.msgTTL_ROUTE_REQUEST:
		case MWACMessage.msgROUTE_REQUEST:
		{  
			// Je suis représentant
			MWACMessage_RouteRequest tMsg = (MWACMessage_RouteRequest) msg;
			if(this.getRole()==AntMWACAgent.roleREPRESENTATIVE)
			{
				TripletIdRoleGroup triplet=this.neighboorlist.get(tMsg.getReceiver());
				if (triplet!=null || tMsg.getReceiver()==this.getUserId())
				{ System.out.println("--->tMsg.getReceiver() est "+tMsg.getReceiver());
					if(!alreadyProcessedRouteRequestManager.isAlreadyProcessed(tMsg.getSender(), tMsg.getIdRequest()))
					{
						// Est-til un voisin?
						System.out.println("REPRESENTANT #"+this.getUserId()+" VA REPONDRE PAR UN ROUTE REPLY A LA RECHERCHE");
						//tMsg.setRoute(MWACRouteAssistant.add(tMsg.getRoute(),this.getUserId()));
						int relais = this.neighboorlist.getLinkToRepresentant(MWACRouteAssistant.getLastId(tMsg.getRoute()));
						if (relais==MWACRouteAssistant.UNDEFINED_ID) relais=this.neighboorlist.getLinkToRepresentant(tMsg.getSender());
						if (relais==MWACNeighboorList.UNDEFINED_ID) System.out.println("!!!!!! (1) Moi repr "+this.getUserId()+" ne trouve pas le précédent de "+interaction.frame.getSender()+" dans la route "+MWACRouteAssistant.routeToString(tMsg.getRoute())+" (pour un ROUTE REPLY)");
						//System.out.println("Le relais pour "+MWACRouteAssistant.getLastId(tMsg.getRoute())+" est "+relais);
						Entree_taux_occupation entry =new Entree_taux_occupation(relais,1);
						this.taux_occupation.Insert(entry);
						

						this.sendFrame(new MWACFrame(this.getUserId(),relais,new MWACMessage_RouteReply(this.getUserId(),tMsg.getSender(),tMsg.getIdRequest(),tMsg.getRoute())));
					Ant_Operations.estimate_energy_receiving(this.table_pheromone, new MWACFrame(this.getUserId(),relais,new MWACMessage_RouteReply(this.getUserId(),tMsg.getSender(),tMsg.getIdRequest(),tMsg.getRoute())));}
					else
					{
						// Already answer by sending a route reply
					}
				}
				else
				{
					if( (!alreadyProcessedRouteRequestManager.isAlreadyProcessed(tMsg.getSender(), tMsg.getIdRequest())) && (tMsg.getSender()!=this.getUserId()))
					{	// On fait suivre
						tMsg.setRoute(MWACRouteAssistant.add(tMsg.getRoute().clone(),this.getUserId()));



						if(msg.getType()==MWACMessage.msgTTL_ROUTE_REQUEST)
						{
							if(((MWACMessage_TTLRouteRequest) tMsg).getTTL()>0)
							{
								((MWACMessage_TTLRouteRequest) tMsg).decreaseTTL();
								
								this.sendFrame(new MWACFrame(this.getUserId(),MWACFrame.BROADCAST_LINK,(MWACMessage_TTLRouteRequest) tMsg));
								Ant_Operations.estimate_energy_receiving(this.table_pheromone,new MWACFrame(this.getUserId(),MWACFrame.BROADCAST_LINK,(MWACMessage_TTLRouteRequest) tMsg));
							}
						}
						else
							{this.sendFrame(new MWACFrame(this.getUserId(),MWACFrame.BROADCAST_LINK,tMsg));
							Ant_Operations.estimate_energy_receiving(this.table_pheromone,new MWACFrame(this.getUserId(),MWACFrame.BROADCAST_LINK,tMsg));
							}
					}
					else
					{
						// Already processed
					}
				}
			}
			else if(this.getRole()==AntMWACAgent.roleLINK)
			{
				if( (!alreadyProcessedRouteRequestManager.isAlreadyProcessed(tMsg.getSender(), tMsg.getIdRequest())) && (tMsg.getSender()!=this.getUserId()) && (interaction.frame.getReceiver()!=MWACFrame.BROADCAST_REPRESENTATIVE))
				{
					//System.out.println("LINK #"+this.getId()+" VA FAIRE SUIVRE LA RECHERCHE");
					this.sendFrame(new MWACFrame(this.getUserId(),MWACFrame.BROADCAST_REPRESENTATIVE, tMsg));
					Ant_Operations.estimate_energy_receiving(this.table_pheromone,new MWACFrame(this.getUserId(),MWACFrame.BROADCAST_REPRESENTATIVE, tMsg));
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

				if(this.getRole()==AntMWACAgent.roleREPRESENTATIVE)
				{   
					if(tMsg.getReceiver()==this.getUserId())
					{       
						if(isCheckRouteReply)
						{
							this.receivedCheckRoute((MWACMessage_CheckRouteReply) tMsg);
						}
                        
						int[] route = MWACRouteAssistant.cloneRoute(tMsg.getRoute(), 1+tMsg.getRoute().length);
						route[route.length-1]=tMsg.getSender();
						//this.routeMWAC.put(tMsg.getSender(),route.clone());
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
							
							Entree_taux_occupation entry =new Entree_taux_occupation(tMsg.getReceiver(),1);
							this.taux_occupation.Insert(entry);
							// System.out.println("je transmet le CHECK route reply a un de mes membres");
							this.sendFrame(new MWACFrame(this.getUserId(),tMsg.getReceiver(),new MWACMessage_CheckRouteReply(tMsg.getSender(),tMsg.getReceiver(),tMsg.getIdRequest(),tMsg.getRoute())));
							Ant_Operations.estimate_energy_receiving(this.table_pheromone,new MWACFrame(this.getUserId(),tMsg.getReceiver(),new MWACMessage_CheckRouteReply(tMsg.getSender(),tMsg.getReceiver(),tMsg.getIdRequest(),tMsg.getRoute())));
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
								{
								
								Entree_taux_occupation entry =new Entree_taux_occupation(relais,1);
								this.taux_occupation.Insert(entry);
								this.sendFrame(new MWACFrame(this.getUserId(),relais,new MWACMessage_RouteReply(tMsg.getSender(),tMsg.getReceiver(),tMsg.getIdRequest(),tMsg.getRoute())));
								Ant_Operations.estimate_energy_receiving(this.table_pheromone,new MWACFrame(this.getUserId(),relais,new MWACMessage_RouteReply(tMsg.getSender(),tMsg.getReceiver(),tMsg.getIdRequest(),tMsg.getRoute())));}
							else
								{
								Entree_taux_occupation entry =new Entree_taux_occupation(relais,1);
								this.taux_occupation.Insert(entry);
								this.sendFrame(new MWACFrame(this.getUserId(),relais,new MWACMessage_CheckRouteReply(tMsg.getSender(),tMsg.getReceiver(),tMsg.getIdRequest(),tMsg.getRoute())));
								Ant_Operations.estimate_energy_receiving(this.table_pheromone,new MWACFrame(this.getUserId(),relais,new MWACMessage_CheckRouteReply(tMsg.getSender(),tMsg.getReceiver(),tMsg.getIdRequest(),tMsg.getRoute())));
								}
						}
					}
				}
				else if(this.getRole()==AntMWACAgent.roleLINK)
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
							{
							Entree_taux_occupation entry =new Entree_taux_occupation(dest,1);
							this.taux_occupation.Insert(entry);
							this.sendFrame(new MWACFrame(this.getUserId(),dest,new MWACMessage_RouteReply(tMsg)));
                             Ant_Operations.estimate_energy_receiving(this.table_pheromone,new MWACFrame(this.getUserId(),dest,new MWACMessage_RouteReply(tMsg)) );}
							else
							{Entree_taux_occupation entry =new Entree_taux_occupation(dest,1);
							this.taux_occupation.Insert(entry);
							this.sendFrame(new MWACFrame(this.getUserId(),dest,new MWACMessage_CheckRouteReply((MWACMessage_CheckRouteReply) msg)));
							 Ant_Operations.estimate_energy_receiving(this.table_pheromone,new MWACFrame(this.getUserId(),dest,new MWACMessage_CheckRouteReply((MWACMessage_CheckRouteReply) msg)));
							}
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
			{   this.cpt = cpt+1;  
			   super.notifyEvent(new SolicitsAgentEvent(this.getSystemId(),this.cpt));
				MWACMessage_RoutedData tMsg = (MWACMessage_RoutedData) msg;
                
				if (tMsg.getReceiver()==this.getUserId())
				{
					this.receiveMessage(new MWACMessage_Data(tMsg.getSender(),tMsg.getReceiver(),tMsg.getMsg()));
					//System.out.println("j'insere la route "+tMsg.getRoute().clone().toString());
					routeMWAC.put(tMsg.getSender(),tMsg.getRoute().clone());
				}
				else if(this.getRole()==AntMWACAgent.roleREPRESENTATIVE)
				{ 
					if (this.neighboorlist.contains(tMsg.getReceiver()))
					{   Entree_taux_occupation ligne1 =new Entree_taux_occupation( tMsg.getReceiver(),1);
		                this.taux_occupation.Insert(ligne1);
		                this.sendFrame(new MWACFrame(this.getUserId(),tMsg.getReceiver(),tMsg));
						//this.sendFrame(new MWACFrame(this.getUserId(),tMsg.getReceiver(),new MWACMessage_Data(tMsg.getSender(),tMsg.getReceiver(),tMsg.getMsg())));
						super.notifyEvent(new EnvoiMWACEvent(this.getSystemId(),this.getUserId(),tMsg.getReceiver()));
						Ant_Operations.estimate_energy_receiving(this.table_pheromone,new MWACFrame(this.getUserId(),tMsg.getReceiver(),tMsg));
					}
					else
					{
						
						int relais = this.neighboorlist.getLinkToRepresentant(MWACRouteAssistant.getNextId(tMsg.getRoute(),this.getUserId()));
						
						if (relais==MWACNeighboorList.UNDEFINED_ID) 
							System.out.println("!!!!!! (3) Moi repr "+this.getUserId()+" ne trouve pas le suivant de "+interaction.frame.getSender()+" dans la route "+MWACRouteAssistant.routeToString(tMsg.getRoute())+" (pour un ROUTED DATA)");
						else
						{   System.out.println ("moi l'agent"+ this.getUserId()+"je trouve le relai"+relais);
							//tMsg.setRoute(MWACRouteAssistant.removeHead(tMsg.getRoute()));
							 Entree_taux_occupation ligne1 =new Entree_taux_occupation(relais ,1);
						     this.taux_occupation.Insert(ligne1);
							this.sendFrame(new MWACFrame(this.getUserId(),relais,new MWACMessage_RoutedData(tMsg)));
							super.notifyEvent(new EnvoiMWACEvent(this.getSystemId(),this.getUserId(),relais));
							Ant_Operations.estimate_energy_receiving(this.table_pheromone,new MWACFrame(this.getUserId(),relais,new MWACMessage_RoutedData(tMsg)));}
					}
				}
				else if(this.getRole()==AntMWACAgent.roleLINK)
				{

					
					int dest;
					
					
					dest =MWACRouteAssistant.getNextId(tMsg.getRoute(),frame.getSender());
					
					//dest = MWACRouteAssistant.getFirstId(tMsg.getRoute());
					//System.out.println("Envoyé par un de mes repr. Le prochain sera "+dest);

					if (dest==MWACNetworkPartialKnowledgeManager.UNDEFINED_ID)
						System.out.println("!!!!!! Moi link "+this.getUserId()+" ne trouve pas le suivant de "+interaction.frame.getSender()+" dans la route "+MWACRouteAssistant.routeToString(tMsg.getRoute())+" (pour un ROUTED DATA)");
					else
						{ System.out.println("moi l'agent"+this.getUserId()+"je trouve le relai"+dest);
						Entree_taux_occupation ligne1 =new Entree_taux_occupation(dest ,1);
					    this.taux_occupation.Insert(ligne1);
						this.sendFrame(new MWACFrame(this.getUserId(),dest,new MWACMessage_RoutedData(tMsg)));
						super.notifyEvent(new EnvoiMWACEvent(this.getSystemId(),this.getUserId(),dest));
						Ant_Operations.estimate_energy_receiving(this.table_pheromone,new MWACFrame(this.getUserId(),dest,new MWACMessage_RoutedData(tMsg)) );
						}
				}
			}
			break;	       

		case MWACMessage.msgPOSSIBLE_ORGANIZATIONAL_INCOHERENCE_NOTIFICATION:
			if (AntMWACAgent.ENABLE_ORGANIZATIONAL_INCOHERENCE_VERIFICATION)
				if(this.role==AntMWACAgent.roleREPRESENTATIVE)
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
							MWACFrame trame =new MWACFrame(getUserId(),MWACFrame.BROADCAST_LINK,
									new MWACMessage_TTLRouteRequest(getUserId(),suspectedGroups[i],
											this.messageToTransmitQueue.getNextIdRequest(),
											(byte) AntMWACAgent.MAX_HOP_RESEARCH_ORGANIZATIONAL_INCOHERENCE));
							super.sendFrame(trame);
							 	
							Ant_Operations.estimate_energy_receiving(this.table_pheromone,trame);
						}						
					}
				}
				else
				{
					// not concerned
				}
			break;

		case MWACMessage.msgCHECK_ROUTE_REQUEST:
			if(interaction.frame.getReceiver()==this.getUserId() || (interaction.frame.getReceiver()==MWACFrame.BROADCAST_REPRESENTATIVE && this.getRole()==AntMWACAgent.roleREPRESENTATIVE))
			{
				MWACMessage_CheckRouteRequest tMsg = (MWACMessage_CheckRouteRequest) msg;

				if (tMsg.getReceiver()==this.getUserId() && !this.isUsurper)
				{
					// Recu : il faut traiter!

					//System.out.println("Checkroute bien recu");
					
					this.sendFrame(new MWACFrame(this.getUserId(),interaction.frame.getSender(),new MWACMessage_CheckRouteReply(tMsg.getReceiver(),tMsg.getSender(),tMsg.getIdRequest(),tMsg.getRoute())));
					Entree_taux_occupation entry = new Entree_taux_occupation(interaction.frame.getSender(),1);
					this.taux_occupation.Insert(entry);
					Ant_Operations.estimate_energy_receiving(this.table_pheromone,new MWACFrame(this.getUserId(),interaction.frame.getSender(),new MWACMessage_CheckRouteReply(tMsg.getReceiver(),tMsg.getSender(),tMsg.getIdRequest(),tMsg.getRoute())));
					
				}
				else if(this.getRole()==AntMWACAgent.roleREPRESENTATIVE)
				{


					if (this.neighboorlist.contains(tMsg.getReceiver()))
					{    
						
						Entree_taux_occupation entry = new Entree_taux_occupation(tMsg.getReceiver(),1);
						this.taux_occupation.Insert(entry);
						//System.out.println("JE SUIS LE REPRESENTANT "+this.getId()+" DU RECEPTEUR ");
						this.sendFrame(new MWACFrame(this.getUserId(),tMsg.getReceiver(),new MWACMessage_CheckRouteRequest(tMsg)));
					    
					    Ant_Operations.estimate_energy_receiving(this.table_pheromone, new MWACFrame(this.getUserId(),tMsg.getReceiver(),new MWACMessage_CheckRouteRequest(tMsg)));
					}
					else if(this.neighboorlist.contains(tMsg.getSender()) && interaction.frame.getReceiver()==MWACFrame.BROADCAST_REPRESENTATIVE)
					{
						System.out.println("Je ("+this.getUserId()+")dois prendre en charge un check route pour "+interaction.frame.getSender());
						int[] route_start=new int[1]; route_start[0]=this.getUserId();
						int[] route_to_dest=this.networkPartialKnowledgeManager.getDataMessageRoute(AntMWACAgent.WORKSTATION_ID);
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
								{
								Entree_taux_occupation entry = new Entree_taux_occupation(relais,1);
								this.taux_occupation.Insert(entry);
								this.sendFrame(new MWACFrame(this.getUserId(),relais,new MWACMessage_CheckRouteRequest(tMsg)));
								
								Ant_Operations.estimate_energy_receiving(this.table_pheromone ,new MWACFrame(this.getUserId(),relais,new MWACMessage_CheckRouteRequest(tMsg)));
								}
							}
						else
						{    
							// Je ne peux pas v?rifier de route car je n'en connais pas
							System.out.println(">>>>> Moi repr "+this.getUserId()+" je n'ai encore jamais parl? avec "+tMsg.getReceiver()+"! Je ne peux donc pas aider "+interaction.frame.getSender()+" a v?rifier si c'est une usurpation");
							Entree_taux_occupation entry = new Entree_taux_occupation(interaction.frame.getSender(),1);
							this.taux_occupation.Insert(entry);
							this.sendFrame(new MWACFrame(this.getUserId(),interaction.frame.getSender(),new MWACMessage_CheckRouteReply(tMsg.getReceiver(),tMsg.getSender(),tMsg.getIdRequest(),new int[0]) ));
							
						    Ant_Operations.estimate_energy_receiving(this.table_pheromone ,new MWACFrame(this.getUserId(),interaction.frame.getSender(),new MWACMessage_CheckRouteReply(tMsg.getReceiver(),tMsg.getSender(),tMsg.getIdRequest(),new int[0]) ) );
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
							{Entree_taux_occupation entry = new Entree_taux_occupation(relais,1);
							this.taux_occupation.Insert(entry);
							this.sendFrame(new MWACFrame(this.getUserId(),relais,new MWACMessage_CheckRouteRequest(tMsg)));
						 
						     Ant_Operations.estimate_energy_receiving(this.table_pheromone ,new MWACFrame(this.getUserId(),relais,new MWACMessage_CheckRouteRequest(tMsg)) );
							}
					}
				}
				else if(this.getRole()==AntMWACAgent.roleLINK)
				{

					
					int dest;


					if(!MWACRouteAssistant.contains(tMsg.getRoute(),interaction.frame.getSender()))
						// l'initiateur de la recherche est le repr?sentant lui-m?me : il n'est donc psa dans la route!
						dest = tMsg.getRoute()[0];
					else
						dest = MWACRouteAssistant.getNextId(tMsg.getRoute(),interaction.frame.getSender());
					//System.out.println("Envoy? par un de mes repr. Le prochain sera "+dest);

					if (dest==MWACNetworkPartialKnowledgeManager.UNDEFINED_ID)
						System.out.println("!!!!!! Moi link "+this.getUserId()+" ne trouve pas le suivant de "+interaction.frame.getSender()+" dans la route "+MWACRouteAssistant.routeToString(tMsg.getRoute())+" (pour un ROUTED DATA)");
					else
					   { Entree_taux_occupation entry = new Entree_taux_occupation(dest,1);
						 this.taux_occupation.Insert(entry);
						 this.sendFrame(new MWACFrame(this.getUserId(),dest,new MWACMessage_CheckRouteRequest(tMsg)));
						 
					     Ant_Operations.estimate_energy_receiving(this.table_pheromone, new MWACFrame(this.getUserId(),dest,new MWACMessage_CheckRouteRequest(tMsg)) );
						}
                
				}
			}
			break;	

		default:
			
	
			System.out.println("ERREUR!!!! NONE IMPLEMENTED MESSAGE "+msg.toString());
		}	
	}

	/********************************************************************************************/
	/** envoi des messages routedData*/
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
				Entree_taux_occupation entry = new Entree_taux_occupation(this.neighboorlist.getLinkToRepresentant(MWACRouteAssistant.getNextId(route,this.getUserId())),1);
				this.taux_occupation.Insert(entry);
				this.sendFrame(new MWACFrame(this.getUserId(),this.neighboorlist.getLinkToRepresentant(MWACRouteAssistant.getNextId(route,this.getUserId())),new MWACMessage_RoutedData(item.msg,route)));
				super.notifyEvent(new EnvoiMWACEvent(this.getSystemId(),this.getUserId(),this.neighboorlist.getLinkToRepresentant(MWACRouteAssistant.getNextId(route,this.getUserId()))));
				Ant_Operations.estimate_energy_receiving(this.table_pheromone,new MWACFrame(this.getUserId(),this.neighboorlist.getLinkToRepresentant(MWACRouteAssistant.getFirstId(route)),new MWACMessage_RoutedData(item.msg,route)));
				iter.remove();
			}
		}
	}

	/********************************************************************************************/
	public int getGroup()
	{
		if(this.role==AntMWACAgent.roleREPRESENTATIVE)
			return this.getUserId();
		else
		{
			ArrayList<Integer> repr=this.neighboorlist.getRepresentativeIdentifiers();
			int nbRepr=repr.size();
			if(nbRepr==0)
			{
				switch(this.role)
				{
				case AntMWACAgent.roleSIMPLEMEMBER:
				case AntMWACAgent.roleLINK:
					System.out.println("<A"+this.getUserId()+","+AntMWACAgent.roleToString(this.role)+">!!!!! ERREUR de cohï¿½rence entre le role et la liste de voisins");
				case AntMWACAgent.roleNOTHING:
					// C'est normal pour eux
					return AntMWACAgent.groupNONE;
				}
			}
			else if (nbRepr==1)
			{
				switch(this.role)
				{
				case AntMWACAgent.roleSIMPLEMEMBER:
					return repr.get(0);
				case AntMWACAgent.roleLINK:
					System.out.println("<B"+this.getUserId()+","+AntMWACAgent.roleToString(this.role)+">!!!!! ERREUR de cohï¿½rence entre le role et la liste de voisins");
				case AntMWACAgent.roleNOTHING:
					return AntMWACAgent.groupNONE;
				}
			}
			else
			{
				if (this.role==AntMWACAgent.roleLINK)
					return repr.get(0);
				else
				{
					if (this.role!=AntMWACAgent.roleNOTHING) System.out.println("<C"+this.getUserId()+","+AntMWACAgent.roleToString(this.role)+">!!!!! ERREUR de cohï¿½rence entre le role et la liste de voisins");
					return AntMWACAgent.groupNONE;
				}
			}
		}
		System.out.println("<D"+this.getUserId()+","+AntMWACAgent.roleToString(this.role)+">!!!!! ERREUR de cohï¿½rence entre le role et la liste de voisins");
		return AntMWACAgent.groupNONE;
	}
	/********************************************************************************************/
	/********************************************************************************************/
	public int[] getGroups()
	{
		int[] res;

		if(this.getRole()!=AntMWACAgent.roleLINK)
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
	/********************************************************************************************/
	/********************************************************************************************/
	public int extraWaitTime(byte role)
	{
		if (this.role==this.roleSIMPLEMEMBER) return 3*AntMWACAgent.SLEEP_TIME_SLOT;
		return 0;
	}
	/********************************************************************************************/
	/********************************************************************************************/
	public synchronized void receivedFrame(Frame frame)
	{
		super.receivedFrame(frame);
		this.receivedFrame((MWACFrame) frame);
	}
	/********************************************************************************************/
	/********************************************************************************************/
	public synchronized void receivedFrame(MWACFrame frame)
	{
		if( (frame.getReceiver()==MWACFrame.BROADCAST) || (frame.getReceiver()==this.getUserId()) ||
				((frame.getReceiver()==MWACFrame.BROADCAST_LINK) && this.getRole()==this.roleLINK) ||
				((frame.getReceiver()==MWACFrame.BROADCAST_REPRESENTATIVE) && this.getRole()==this.roleREPRESENTATIVE)) this.receivedMessageQueue.push(frame);
	}
	/********************************************************************************************/
	/********************************************************************************************/
	private int score()
	{
		return (int)(this.pourcentOfAvailableEnergy()*this.pourcentOfAvailableEnergy()*this.neighboorlist.size());
	}

	/********************************************************************************************/
	private void setRole(byte role)
	{
		if(this.role!=role)
		{
			this.role=role;
			super.notifyEvent(new RoleModificationEvent(getSystemId(),role));
			switch(role)
			{
			case AntMWACAgent.roleNOTHING: super.setColor(colorNOTHING); break;
			case AntMWACAgent.roleSIMPLEMEMBER:super.setColor(colorSIMPLEMEMBER); break;
			case AntMWACAgent.roleLINK:super.setColor(colorLINK); break;
			case AntMWACAgent.roleREPRESENTATIVE:super.setColor(colorREPRESENTATIVE); break;
			default: super.setColor(Color.BLACK);
			}
		}
	}

	/********************************************************************************************/
	/*********************************************************************************************/
	public void receiveMessage(MWACMessage_Data msg)  //ok 
	{
		super.notifyEvent(new ReceivedMessageEvent(this.getSystemId(),msg));
		super.notifyEvent(new ReceivedDataEvent(this.getSystemId(),msg));
		if(this.isUsurper)
			System.out.println("\nUSURPATEUR "+this.realId+" a USURPE le message "+((MWACMessage_Data)msg).getMsg());
		else
			System.out.println("\n"+this.getUserId()+" a recu le message "+((MWACMessage_Data)msg).getMsg());
	}
	/********************************************************************************************/
	public void receivedCheckRoute(MWACMessage_CheckRouteReply msg)
	{
		System.out.println(this.getUserId()+" RECOIT LA REPONSE A SON CONTROLE DE ROUTE "+msg);
	}
	
	
	/********************************************************************************************/
	private void sendMessage(MWACMessage_Data msg)
	{
		if(this.neighboorlist.contains(msg.getReceiver()))
			{Entree_taux_occupation ligne1 =new Entree_taux_occupation(msg.getReceiver() ,1);
		    this.taux_occupation.Insert(ligne1);
			super.sendFrame(new MWACFrame(getUserId(),msg.getReceiver(),msg));
			Ant_Operations.estimate_energy_receiving(this.table_pheromone,new MWACFrame(getUserId(),msg.getReceiver(),msg)); }
		else
		{
			short idRequest=this.messageToTransmitQueue.add(msg);
			this.networkPartialKnowledgeManager.addRouteRequestAndReceiverAssociation(idRequest, msg.getReceiver());
			
			int []route =new int [1];
			route[0]=this.getUserId(); 
			super.sendFrame(new MWACFrame(getUserId(),MWACFrame.BROADCAST_LINK,new MWACMessage_RouteRequest(getUserId(),msg.getReceiver(),idRequest,route)));
		    Ant_Operations.estimate_energy_receiving(this.table_pheromone,new MWACFrame(getUserId(),MWACFrame.BROADCAST_LINK,new MWACMessage_RouteRequest(getUserId(),msg.getReceiver(),idRequest)));
		}
	}

	/********************************************************************************************/
	public void sendMessage(int receiver,String s)
	{   
		if(this.neighboorlist.contains(receiver))    //envoi un msg de donnees direct a la dst 
		{   MWACMessage_Data msg = new MWACMessage_Data(this.getUserId(),receiver,s);
			System.out.println("\n("+this.getUserId()+" Le destinataire est un de mes voisins");
			super.notifyEvent(new SendedMessageEvent(this.getSystemId(),(Message)msg.clone()));
			super.notifyEvent(new SendedDataEvent(this.getSystemId(),(MWACMessage_Data)msg.clone()));
			Entree_taux_occupation ligne1 =new Entree_taux_occupation(receiver ,1);
		    this.taux_occupation.Insert(ligne1);
			this.sendFrame(new MWACFrame(this.getUserId(),receiver,msg));
			
			Ant_Operations.estimate_energy_receiving(this.table_pheromone,new MWACFrame(this.getUserId(),receiver,msg));
		}
		else 
		{  if (!this.mustsendwithAnts)  //envoi par de donnees par les routes MWAC 
			
		   {
			MWACMessage_Data msg = new MWACMessage_Data(this.getUserId(),receiver,s);
			System.out.println("\n("+this.getUserId()+")Le destinataire N'est PAS un de mes voisins");
			switch(this.role)
			{
			case AntMWACAgent.roleNOTHING:
			case AntMWACAgent.roleSIMPLEMEMBER:
			case AntMWACAgent.roleLINK:
				ArrayList<Integer> lst=this.neighboorlist.getRepresentativeIdentifiers();
				if(lst==null) 
					super.notifyEvent(new MessageNotTransmittedEvent(this.getSystemId(),msg,"Representative neighboor not found"));
				else
				{
					super.notifyEvent(new SendedMessageEvent(this.getSystemId(),(Message)msg.clone()));
					super.notifyEvent(new SendedDataEvent(this.getSystemId(),(MWACMessage_Data)msg.clone()));
					this.sendDataFrame(lst.get(0),msg);
					Entree_taux_occupation ligne1 =new Entree_taux_occupation( lst.get(0),1);
				    this.taux_occupation.Insert(ligne1);
				}
				break;
			case AntMWACAgent.roleREPRESENTATIVE:
				System.out.println("\nREPRESENTANT "+this.getUserId());
				super.notifyEvent(new SendedMessageEvent(this.getSystemId(),(Message)msg.clone()));
				super.notifyEvent(new SendedDataEvent(this.getSystemId(),(MWACMessage_Data)msg.clone()));
				this.sendMessage(msg);
				break;
			}
		   }
		else {
			  
			AntMessage_Forward msg = new AntMessage_Forward
			(this.getUserId(),receiver,this.gestionaire_fourmis.get_new_AntId(),new int [0],s,0);
		    // System.out.println( "la fourmis est :" +msg.toString());
			System.out.println("\n("+this.getUserId()+")Le destinataire N'est PAS un de mes voisins");
			
			if (Ant_Operations.Next_Agent(this.table_pheromone,msg.get_route(),taux_occupation)!=-1) 
			   {
				Entree_taux_occupation ligne1 =new Entree_taux_occupation( Ant_Operations.Next_Agent(this.table_pheromone,msg.get_route(),taux_occupation),1);
			    this.taux_occupation.Insert(ligne1);
				this.sendFrame(new MWACFrame(this.getUserId(),Ant_Operations.Next_Agent(this.table_pheromone,msg.get_route(),taux_occupation),msg));
				super.notifyEvent(new EnvoiFourmisEvent(this.getSystemId(),this.getUserId(),Ant_Operations.Next_Agent(this.table_pheromone,msg.get_route(),taux_occupation)));
				super.notifyEvent(new SendedMessageEvent(this.getSystemId(),(Message)msg.clone()));
				super.notifyEvent(new SendedAntEvent(this.getSystemId(),(AntMessage_Forward)msg.clone()));
				
			    Ant_Operations.estimate_energy_receiving(this.table_pheromone,new MWACFrame(this.getUserId(),Ant_Operations.Next_Agent(this.table_pheromone,msg.get_route(),taux_occupation),msg));
			   }
			 else  super.notifyEvent(new MessageNotTransmittedEvent(this.getSystemId(),msg,"il n'a pas de prochain relai"));
			/******************************************************************************************/
			  // que les agents represenatnts et de liaisons qui participent au routage
			  
		/*switch(this.role)
		     {
			case roleNOTHING:
			case roleSIMPLEMEMBER:
			 ArrayList<Integer> lst=this.neighboorlist.getRepresentativeIdentifiers();
			if(lst==null) super.notifyEvent(new MessageNotTransmittedEvent(this.getSystemId(),msg,"Representative neighboor not found"));
			else
				{
			      
					super.notifyEvent(new SendedMessageEvent(this.getSystemId(),(Message)msg.clone()));
					super.notifyEvent(new SendedAntEvent(this.getSystemId(),(AntMessage_Forward)msg.clone()));
					Entree_taux_occupation ligne1 =new Entree_taux_occupation(lst.get(0),1);
			        this.taux_occupation.Insert(ligne1);
					this.sendFrame(new MWACFrame(this.getUserId(),lst.get(0),msg));
				    Ant_Operations.estimate_energy_receiving(this.table_pheromone,new MWACFrame(this.getUserId(),lst.get(0),msg ));
					
				}
			
			break;
			case roleREPRESENTATIVE:
			case roleLINK:
			 
			  if (Ant_Operations.Next_Agent(this.table_pheromone,msg.get_route(),taux_occupation)!=-1) 
			   {
				
				super.notifyEvent(new SendedMessageEvent(this.getSystemId(),(Message)msg.clone()));
				super.notifyEvent(new SendedAntEvent(this.getSystemId(),(AntMessage_Forward)msg.clone()));
			    Entree_taux_occupation ligne1 =new Entree_taux_occupation(Ant_Operations.Next_Agent(this.table_pheromone,msg.get_route(),taux_occupation),1);
			    this.taux_occupation.Insert(ligne1);
				this.sendFrame(new MWACFrame(this.getUserId(),Ant_Operations.Next_Agent(this.table_pheromone,msg.get_route()),msg));
				Ant_Operations.estimate_energy_receiving(this.table_pheromone,new MWACFrame(this.getUserId(),Ant_Operations.Next_Agent(this.table_pheromone,msg.get_route(),taux_occupation),msg) );
			   }
			 else  super.notifyEvent(new MessageNotTransmittedEvent(this.getSystemId(),msg,"il n'a pas de prochain relai"));
			
			break;
		     }*/
		  /********************************************************************************/
		}
			
	
				}
		}
	


	/********************************************************************************************/
	private void sendDataFrame(int dest,MWACMessage msg)
	{
		if(AntMWACAgent.DEBUG) debug(getUserId()+">>> Envoie d'un message de DONNEES");
		super.sendFrame(new MWACFrame(getUserId(),dest,msg));
		
	     Ant_Operations.estimate_energy_receiving(this.table_pheromone,new MWACFrame(getUserId(),dest,msg) );
	}

	/********************************************************************************************/
	private void sendConflictResolution() 
	{
		if(AntMWACAgent.DEBUG) debug(getUserId()+">>> Envoie par d'un message de RESOLUTION DE CONFLIT");
		super.sendFrame(new MWACFrame(getUserId(),MWACFrame.BROADCAST,new MWACMessage_ConflictResolution(getUserId(),MWACMessage.BROADCAST,this.score())));
		 
	    Ant_Operations.estimate_energy_receiving(this.table_pheromone,new MWACFrame(getUserId(),MWACFrame.BROADCAST,new MWACMessage_ConflictResolution(getUserId(),MWACMessage.BROADCAST,this.score())));
		
	}

	/********************************************************************************************/
	private void sendIncoherenceNotification(Vector<Integer> v)
	{
		int[] array = new int[v.size()];
		for(int i=0;i<v.size();i++) array[i]=v.get(i);

		if(AntMWACAgent.DEBUG) debug(">>> Envoie par d'un message de suspission d'incoherence organisationnelle     <"+getUserId()+","+AntMWACAgent.roleToString(this.role)+","+this.getGroup()+">");
		super.sendFrame(new MWACFrame(getUserId(),MWACFrame.BROADCAST,new MWACMessage_PossibleOrganizationalIncoherence(getUserId(),array)));
        
		
		Ant_Operations.estimate_energy_receiving( this.table_pheromone,new MWACFrame(getUserId(),MWACFrame.BROADCAST,new MWACMessage_PossibleOrganizationalIncoherence(getUserId(),array)));
	}
	/********************************************************************************************/
	private void incoherenceNotificationProcedure()
	{
		//System.out.println("* TEST INCOHERENCE (role="+MWACAgent.roleToString(this.getRole())+",id="+this.getId()+",groups="+MWACRouteAssistant.routeToString(this.getGroups())+").");
		Vector<Integer> v = this.neighboorlist.suspiciousNeighboorsGroups(this.getGroups());
		if (!v.isEmpty()) 
		{
			//debug("DETECTION POSSIBLE INCOHERENCE SIGNALEE PAR "+this.getId()+" "+v);
			System.out.println("\nDETECTION POSSIBLE INCOHERENCE SIGNALEE PAR <"+this.getUserId()+","+AntMWACAgent.roleToString(this.getRole())+","+MWACRouteAssistant.routeToString(this.getGroups())+"). Les groupes suspects sont "+v+"\n"+this.neighboorlist.toString());
			this.sendIncoherenceNotification(v);
			
		}
	}
	/********************************************************************************************/
	/*private void sendPresentation()
	{
		//Frame f=new Frame(getId(),dest,new ASTRO_Presentation(getId(),dest,role,group));
		if(AntMWACAgent.DEBUG) debug(">>> Envoie par d'un message de PRESENTATION     <"+getUserId()+","+AntMWACAgent.roleToString(this.role)+","+this.getGroup()+">");
		super.sendFrame(new MWACFrame(getUserId(),MWACFrame.BROADCAST,new MWACMessage_Presentation(getUserId(),this.role,this.getGroups())));
	   
		Ant_Operations.estimate_energy_receiving(this.table_pheromone,new MWACFrame(getUserId(),MWACFrame.BROADCAST,new MWACMessage_Presentation(getUserId(),this.role,this.getGroups())));
	}
*/
	
	
	private void sendPresentation() 
	{
		//Frame f=new Frame(getId(),dest,new ASTRO_Presentation(getId(),dest,role,group));


		if(AntMWACAgent.DEBUG) debug(">>> Envoie par d'un message de PRESENTATION     <"+getUserId()+","+AntMWACAgent.roleToString(this.role)+","+this.getGroup()+">");

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

			claimedGroups[getGroups().length] =  1 + (new Random()).nextInt(50);//1234; 

		} else {
			claimedGroups = this.getGroups();
			claimedRole = this.role;
		} /** (/Anca) */

		if(this.isUsurper) System.out.println("USURPER #" + realId + " sending out presentation: " + getUserId() + ", " +AntMWACAgent.roleToString(claimedRole) + ", " + MWACGroupAssistant.groupsToString(claimedGroups));
		super.sendFrame(new MWACFrame(getUserId(),MWACFrame.BROADCAST,new MWACMessage_Presentation(getUserId(), claimedRole, claimedGroups)));		
	    Ant_Operations.estimate_energy_receiving(this.table_pheromone, new MWACFrame(getUserId(),MWACFrame.BROADCAST,new MWACMessage_Presentation(getUserId(), claimedRole, claimedGroups)));		
	}
	/********************************************************************************************/
	private void sendIntroduction()
	{
		if(AntMWACAgent.DEBUG) debug(">>> Envoie par d'un message d'INTRODUCTION      <"+getUserId()+","+AntMWACAgent.roleToString(this.role)+","+this.getGroup()+">");
		super.sendFrame(new MWACFrame(super.getUserId(),MWACFrame.BROADCAST,new MWACMessage_Introduction(this.getUserId())));
	    
		Ant_Operations.estimate_energy_receiving( this.table_pheromone,new MWACFrame(super.getUserId(),MWACFrame.BROADCAST,new MWACMessage_Introduction(this.getUserId())));
	}

	/********************************************************************************************/
	/*private void sendWhoAreMyNeighboors()
	{
		//Frame f=new Frame(getId(),dest,new ASTRO_Presentation(getId(),dest,role,group));
		if(AntMWACAgent.DEBUG) debug(">>> Envoie par d'un message de WHOAREMYNEIGHBOORS   <"+getUserId()+","+AntMWACAgent.roleToString(this.role)+","+this.getGroup()+">");
		super.sendFrame(new MWACFrame(getUserId(),MWACFrame.BROADCAST,new MWACMessage_WhoAreMyNeighboors(getUserId(),role,this.getGroups())));
		
		Ant_Operations.estimate_energy_receiving(this.table_pheromone,new MWACFrame(getUserId(),MWACFrame.BROADCAST,new MWACMessage_WhoAreMyNeighboors(getUserId(),role,this.getGroups())));
	}
*/
	
	private void sendWhoAreMyNeighboors()
	{
		//Frame f=new Frame(getId(),dest,new ASTRO_Presentation(getId(),dest,role,group));
		if(AntMWACAgent.DEBUG) debug(">>> Envoie par d'un message de WHOAREMYNEIGHBOORS   <"+getUserId()+","+AntMWACAgent.roleToString(this.role)+","+this.getGroup()+">");

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
	    Ant_Operations.estimate_energy_receiving(this.table_pheromone,new MWACFrame(getUserId(),MWACFrame.BROADCAST,new MWACMessage_WhoAreMyNeighboors(getUserId(),claimedRole, claimedGroups)));
	
	}
	/********************************************************************************************/
	public byte getRole() 
	{
		return this.role;
	}



	/********************************************************************************************/
	public void becomeWorkstationUsurper()
	{
		this.mustUsurpsWorkstationID=true;
	}

	/********************************************************************************************/
	public void becomeNeighboorUsurper()
	{
		this.mustUsurpsNeighboorID=true;
	}


	/********************************************************************************************/
	public void checkRouteTo(int id)
	{
		if(this.getRole()==AntMWACAgent.roleREPRESENTATIVE)
		{	
			int[] route=this.networkPartialKnowledgeManager.getDataMessageRoute(AntMWACAgent.WORKSTATION_ID);
			if (!(route==null || route.length==0)) this.checkRouteTo(id, route);
		}
		else
			this.checkRouteTo(id,new int[0]);
	}
	/********************************************************************************************/
	
	public void checkRouteTo(int id,int[] route)
	{
		if(this.role==AntMWACAgent.roleREPRESENTATIVE)
		{
			int relais =-1;
			if(route.length>0) relais=this.neighboorlist.getLinkToRepresentant(route[0]);
			if(relais!=MWACRouteAssistant.UNDEFINED_ID) 
			{   Entree_taux_occupation entry =new Entree_taux_occupation(relais,1);
			    this.taux_occupation.Insert(entry);
				System.out.println("\nCHECK THE ROUTE LAUNCHED BY "+this.getUserId()+" (repr) TO "+id+". Route is "+MWACRouteAssistant.routeToString(route));
				super.sendFrame(new MWACFrame(super.getUserId(),relais,new MWACMessage_CheckRouteRequest(this.getUserId(),id,(short)1 /** identifiant de requete*/,route)));
				
				Ant_Operations.estimate_energy_receiving(this.table_pheromone,new MWACFrame(super.getUserId(),relais,new MWACMessage_CheckRouteRequest(this.getUserId(),id,(short)1 /** identifiant de requete*/,route)));
				return;
			}
			
		}
		System.out.println("\nCHECK THE ROUTE LAUNCHED BY "+this.getUserId()+" (non repr) TO "+id+". Route is "+MWACRouteAssistant.routeToString(route));
		super.sendFrame(new MWACFrame(super.getUserId(),MWACFrame.BROADCAST_REPRESENTATIVE,new MWACMessage_CheckRouteRequest(this.getUserId(),id,(short)1 /** identifiant de requete*/,route)));
		
		
	}

	/********************************************************************************************/

	public String toSpyWindows()
	{
	  
	String Ant_AFFICHAGE="<BR>" + "<BR>"+this.table_pheromone.toHTML()+ "<BR>";
	String roleDependantText="";
	String RouteofAntUsed=""; 
	String RouteofMWACtUsed="";
	if(getRole()==AntMWACAgent.roleREPRESENTATIVE && this.messageToTransmitQueue!=null) roleDependantText="<BR><BR>"+this.messageToTransmitQueue.toHTML();
	if((getRole()==AntMWACAgent.roleREPRESENTATIVE || getRole()==AntMWACAgent.roleLINK) && this.alreadyProcessedRouteRequestManager!=null) roleDependantText+="<BR><BR>"+this.alreadyProcessedRouteRequestManager.toHTML();
	if(getRole()==AntMWACAgent.roleREPRESENTATIVE && this.networkPartialKnowledgeManager!=null) roleDependantText+="<BR><BR>"+this.networkPartialKnowledgeManager.toHTML();
    if (this.getUserId()==WORKSTATION_ID ) {RouteofAntUsed +=this.routeAnt.toHTML(); RouteofMWACtUsed+=this.routeMWAC.toHTMLMWAC(); }  
	return "<HTML>"+"<B>Id</B>="+this.getUserId()+" <B>Role</B>="+AntMWACAgent.roleToString(this.getRole())+"    <B>Group</B>="+MWACGroupAssistant.groupsToString(this.getGroups())+"    <B>Energy</B>="+String.format("%.2f",this.pourcentOfAvailableEnergy())+"<B>%</B>"+"    <B>Range</B>="+this.getRange()+"<BR><BR>"+this.neighboorlist.toHTML()+roleDependantText+RouteofMWACtUsed+
	RouteofAntUsed+Ant_AFFICHAGE+"</HTML>";
	}

	/********************************************************************************************/
	private void debug(String s)
	{
		System.out.println(new aDate().getTimeInMillis()+"   "+this.getUserId()+"\t"+s);
	}

	/********************************************************************************************/
	public static String roleToString(byte role)
	{
		switch(role)
		{
		case AntMWACAgent.roleREPRESENTATIVE	: return "ROLE_REPRESENTATIVE";
		case AntMWACAgent.roleLINK				: return "ROLE_LINK";
		case AntMWACAgent.roleSIMPLEMEMBER		: return "ROLE_SIMPLEMEMBER";
		case AntMWACAgent.roleNOTHING			: return "ROLE_NOTHING";
		}
		return "ROLE_UNDEFINED";
	}

	/********************************************************************************************/
	private class InteractionContext
	{
		public int debugPerdu;
		public MWACFrame frame;

		public boolean lostRepresentativeRole;	

		public boolean hasPreviouslyLostRepresentativeRole;
		
		public boolean roleConfict;

		
		public boolean mustSendWhoAreMyNeighoors; 
		
		public boolean mustSendAPresentation;	
		
		public boolean reinitializationOfTheRole;
		
		public InteractionContext()
		{
			this.init();
		}

		/********************************************************************************************/
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
	/******************************************************************************/
	public void becomeGroupUsurper(){
		this.mustUsurpGroups = true;
	}

	/******************************************************************************/
	public void becomeLinkUsurper(){
		this.mustUsurpLink = true;
	}

	/********************************************************************************************/
	public void initialisation_workstation()
	{  //if (this.mustsendwithAnts)
		MWACFrame trame =new MWACFrame(this.getUserId(), MWACFrame.BROADCAST,
				new AntMessage_Initialisation(this.getUserId(), MWACMessage.BROADCAST, (float) this.getBattery().getActualAmountOfEngergy(),0));
	this.sendFrame(trame);
	Ant_Operations.estimate_energy_receiving(this.table_pheromone,trame);
	
	}
	
	/********************************************************************************************/
	public void envoi_par_fourmis()
	{
		this.mustsendwithAnts =true;
	}
	/********************************************************************************************/
	public void envoi_par_MWAC()
	{
		this.mustsendwithAnts =false;
	}
}
