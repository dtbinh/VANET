package simulation.solutions.custom.MWACSocial;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Vector;

import simulation.entities.Agent;
import simulation.events.system.*;
import simulation.messages.*;
import simulation.multiagentSystem.MAS;
import simulation.solutions.custom.MWACSocial.Messages.MWACSocialFrame;
import simulation.solutions.custom.MWACSocial.Messages.MWACSocialMessage;
import simulation.solutions.custom.MWACSocial.Messages.MWACSocialMessage_ConflictResolution;
import simulation.solutions.custom.MWACSocial.Messages.MWACSocialMessage_Data;
import simulation.solutions.custom.MWACSocial.Messages.MWACSocialMessage_Introduction;
import simulation.solutions.custom.MWACSocial.Messages.MWACSocialMessage_NeighboorhoodServicesReply;
import simulation.solutions.custom.MWACSocial.Messages.MWACSocialMessage_NeighboorhoodServicesRequest;
import simulation.solutions.custom.MWACSocial.Messages.MWACSocialMessage_Presentation;
import simulation.solutions.custom.MWACSocial.Messages.MWACSocialMessage_RouteReply;
import simulation.solutions.custom.MWACSocial.Messages.MWACSocialMessage_RouteRequest;
import simulation.solutions.custom.MWACSocial.Messages.MWACSocialMessage_RoutedData;
import simulation.solutions.custom.MWACSocial.Messages.MWACSocialMessage_ServicesReply;
import simulation.solutions.custom.MWACSocial.Messages.MWACSocialMessage_ServicesRequest;
import simulation.solutions.custom.MWACSocial.Messages.MWACSocialMessage_WhoAreMyNeighboors;
import simulation.utils.aDate;

/**
 * This class model our solution based on the MWAC model
 */
public class MWACSocialAgent extends Agent{



	/** Initial TTL */
	private static byte INITIAL_TTL = 2;
	
	/** max ttl when services are searched */
	public static int MAX_TTL = 7;

	/** ttl step added when services are not found */
	public static int TTL_STEP = 1;

	/** time before launched a new services request when they are not found */
	public static int MAX_WAITING_TIME = 4000;




	private static final boolean DEBUG = false;

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
	private SocialFrameFIFOStack receivedMessageQueue;
	/** message to send (if the agent is Representative*/
	private MWACSocialMessageFIFOStack messageToTransmitQueue;
	/** neighboorList */
	private MWACSocialNeighboorList neighboorlist;
	/** to manage already processed route request */
	private MWACSocialAlreadyProcessedRouteRequestManager alreadyProcessedRouteRequestManager;
	/** MWAC route fragment  manager */
	private MWACSocialNetworkPartialKnowledgeManager networkPartialKnowledgeManager;


	/** to manage already processed route request */
	private MWACSocialAlreadyProcessedRouteRequestManager alreadyProcessedServicesRequestManager;


	public long ageOfTheCurrentRole;

	private byte nextServiceRequestId;

	private Vector<Integer> services;
	private SearchedServices searchedServices;
	private NeighoorhoodServicesManager neighoorhoodServices;
	private MAS mas;

	/**
	 * Constructor
	 * @param mas the multiagent system
	 * @param id identifier of the agent
	 * @param energy energy of the agent
	 * @param range range of the agent
	 */
	public MWACSocialAgent(MAS mas,Integer  id, Float energy,Integer  range)
	{
		super(mas,id,range);
		this.role=roleNOTHING;
		this.receivedMessageQueue=new SocialFrameFIFOStack();
		this.neighboorlist=new MWACSocialNeighboorList();
		this.messageToTransmitQueue=null;	//R
		this.alreadyProcessedRouteRequestManager=null;
		this.alreadyProcessedServicesRequestManager=null;
		this.networkPartialKnowledgeManager=null;
		this.services=new Vector<Integer>();
		this.searchedServices=new SearchedServices();
		this.mas=mas;
		this.nextServiceRequestId=0;
		this.neighoorhoodServices=null;
	}

	/**  Launched by the call of start method*/
	public void run()
	{
		System.out.println("Démarrage du MWACAgent "+this.getUserId());
		String servicesPresentation="Supplied services :";
		this.ageOfTheCurrentRole=0;
		try{
			// Lecture depuis un fichier
			BufferedReader in = new BufferedReader(new FileReader("D:\\SCE\\"+this.getUserId()+".services"));
			String s = new String();
			while((s = in.readLine()) != null) 
				if (!this.services.contains(new Integer(s)))
				{
					this.services.add(new Integer(s));
					servicesPresentation+=new Integer(s)+" ";
				}
			in.close();

		} catch(IOException e) {System.out.println("ERREUR D'OUVERTURE DES FICHIER "+this.getUserId());}


		try{
			// Lecture depuis un fichier
			BufferedReader in = new BufferedReader(new FileReader("D:\\SCE\\"+this.getUserId()+".rec"));
			String s = new String();
			while((s = in.readLine()) != null) 
			{
				ServiceSearchItem ssi=new ServiceSearchItem(s);
				this.searchedServices.add(ssi);
			}
			in.close();
			servicesPresentation+="\n\nSearched services: "+this.searchedServices.toString();

		} catch(IOException e) {System.out.println("ERREUR D'OUVERTURE DES FICHIER "+this.getUserId());}


		System.out.println(servicesPresentation); servicesPresentation=null;

		try{Thread.sleep(500);}catch(Exception e){}

		if (MWACSocialAgent.DEBUG) debug("Envoie un message d'introduction");
		sendIntroduction();
		try{Thread.sleep(2*SLEEP_TIME_SLOT);}catch(Exception e){};


		MWACSocialMessage msg; MWACSocialFrame frm;	boolean mustSendWhoAreMyNeighoors=false; boolean mustSendAPresentation=false;	boolean lostRepresentativeRole=false;	boolean hasPreviouslyLostRepresentativeRole=false;
		// MWAC LAYER
		while(!isKilling() && !isStopping())
		{
			// Pause
			try{Thread.sleep(SLEEP_TIME_SLOT+this.extraWaitTime(this.role));}catch(Exception e){};

			// Preparation of the others threads
			while(  ((isSuspending()) && (!isKilling() && !isStopping()))) try{Thread.sleep(SLEEP_TIME_SLOT);}catch(Exception e){};

			while( this.receivedMessageQueue.isEmpty() && (this.role!=MWACSocialAgent.roleNOTHING)) 
			{
				try
				{
					Thread.sleep(SLEEP_TIME_SLOT);
				}
				catch(Exception e){};

				this.verifySearchedServices();


				//if(this.getRole()==MWACSocialAgent.roleREPRESENTATIVE) System.out.println("<"+this.getUserId()+","+this.ageOfTheCurrentRole+","+(this.mas.elapsedSimulationTime()-this.ageOfTheCurrentRole)+">");
				if(this.getRole()==MWACSocialAgent.roleREPRESENTATIVE && this.ageOfTheCurrentRole!=-1)
					if(this.mas.elapsedSimulationTime()-this.ageOfTheCurrentRole>2000) 
					{
						this.updateNeighboorServicesList();
						this.ageOfTheCurrentRole=-1;
					}
			}

			// Process the waiting messages
			int debugPerdu=-1;
			mustSendWhoAreMyNeighoors=false; mustSendAPresentation=false; hasPreviouslyLostRepresentativeRole=lostRepresentativeRole; lostRepresentativeRole=false;


			while((frm=this.receivedMessageQueue.pop())!=null) 
			{
				msg=MWACSocialMessage.createMessage(frm.getData());

				switch(msg.getType())
				{
				case MWACSocialMessage.msgINTRODUCTION:
				{
					this.neighboorlist.put(msg.getSender(),MWACSocialAgent.roleNOTHING, MWACSocialAgent.groupNONE);
					mustSendWhoAreMyNeighoors=true;
				}
				break;
				case MWACSocialMessage.msgWHO_ARE_MY_NEIGHBOORS:
				case MWACSocialMessage.msgPRESENTATION:
				{

					this.neighboorlist.put(msg.getSender(),((MWACSocialMessage_Presentation)msg).getRole(),  ((MWACSocialMessage_Presentation)msg).getClonedGroupArray());
					mustSendAPresentation=   mustSendAPresentation
					|| (msg.getType()==MWACSocialMessage.msgWHO_ARE_MY_NEIGHBOORS) 
					|| this.neighboorlist.put(msg.getSender(),((MWACSocialMessage_Presentation)msg).getRole(), ((MWACSocialMessage_Presentation)msg).getClonedGroupArray());
					this.ageOfTheCurrentRole=this.mas.elapsedSimulationTime();	// To update services list in neighboorhood
				}
				break;
				case MWACSocialMessage.msgCONFLICT_RESOLUTION:
				{
					this.neighboorlist.put(msg.getSender(), MWACSocialAgent.roleREPRESENTATIVE, msg.getSender());
					if(this.role==MWACSocialAgent.roleREPRESENTATIVE && !lostRepresentativeRole)
					{
						int scoreInMsg = ((MWACSocialMessage_ConflictResolution)msg).getScore();
						if((scoreInMsg>this.score()) || ((scoreInMsg==this.score()) && this.getUserId()<msg.getSender()))
						{
							if(MWACSocialAgent.DEBUG) debug("*PERDU* contre "+msg.getSender());
							lostRepresentativeRole=true;
							mustSendAPresentation=true;
							debugPerdu=msg.getSender();
						}
						else
						{
							if(MWACSocialAgent.DEBUG) debug("*GAGNE* contre "+msg.getSender());
							this.neighboorlist.put(msg.getSender(),this.roleNOTHING,this.groupNONE);
							if(MWACSocialAgent.DEBUG) debug("Je montre que je gagne mon conflit");
							this.sendConflictResolution();
							mustSendAPresentation=false;
						}
					}
					else
					{
						// I have already lost my reprentative role against with another agent
						if (lostRepresentativeRole) if(MWACSocialAgent.DEBUG) debug("*DEJA PERDU* contre "+debugPerdu+" bien avant "+msg.getSender()+"    role="+this.role+"  lost="+lostRepresentativeRole);
						//this.neighboorlist.put(msg.getSender(),this.roleNOTHING,this.groupNONE);
						mustSendAPresentation=true;
					}
				}
				break;
				case MWACSocialMessage.msgDATA:
					if(frm.getReceiver()==this.getUserId())
					{
						MWACSocialMessage_Data tMsg = (MWACSocialMessage_Data) msg;

						if(msg.getReceiver()==this.getUserId()) 
							this.receiveMessage(tMsg);
						else if(this.role==MWACSocialAgent.roleREPRESENTATIVE)
						{
							// Je suis le representant de l'émetteur (mon membre envoit directement le message)
							this.sendMessage(tMsg);
						}
					}
					break;
				case MWACSocialMessage.msgROUTE_REQUEST:
				{
					// Je suis représentant
					MWACSocialMessage_RouteRequest tMsg = (MWACSocialMessage_RouteRequest) msg;
					if(this.getRole()==MWACSocialAgent.roleREPRESENTATIVE)
					{
						SocialTripletIdRoleGroup triplet=this.neighboorlist.get(tMsg.getReceiver());
						if (triplet!=null || tMsg.getReceiver()==this.getUserId())
						{
							if(!alreadyProcessedRouteRequestManager.isAlreadyProcessed(tMsg.getSender(), tMsg.getIdRequest()))
							{
								// Est-til un voisin?
								System.out.println("REPRESENTANT #"+this.getUserId()+" VA REPONDRE PAR UN ROUTE REPLY A LA RECHERCHE");
								int relais = this.neighboorlist.getLinkToRepresentant(MWACSocialRouteAssistant.getLastId(tMsg.getRoute()));
								if (relais==MWACSocialRouteAssistant.UNDEFINED_ID) relais=this.neighboorlist.getLinkToRepresentant(tMsg.getSender());
								if (relais==MWACSocialNeighboorList.UNDEFINED_ID) System.out.println("!!!!!! Moi repr "+this.getUserId()+" ne trouve pas le précédent de "+frm.getSender()+" dans la route "+MWACSocialRouteAssistant.integerArrayToString(tMsg.getRoute())+" (pour un ROUTE REPLY)");
								//System.out.println("Le relais pour "+MWACRouteAssistant.getLastId(tMsg.getRoute())+" est "+relais);
								this.sendFrame(new MWACSocialFrame(this.getUserId(),relais,new MWACSocialMessage_RouteReply(this.getUserId(),tMsg.getSender(),tMsg.getIdRequest(),tMsg.getRoute())));
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
								tMsg.setRoute(MWACSocialRouteAssistant.add(tMsg.getRoute(),this.getUserId()));



								if(MWACSocialMessage.USE_TTL)
								{
									if(tMsg.getTTL()>0)
									{
										tMsg.decreaseTTL();
										this.sendFrame(new MWACSocialFrame(this.getUserId(),MWACSocialFrame.BROADCAST_LINK,tMsg));
									}
								}
								else
									this.sendFrame(new MWACSocialFrame(this.getUserId(),MWACSocialFrame.BROADCAST_LINK,tMsg));



							}
							else
							{
								// Already processed
							}
						}
					}
					else if(this.getRole()==MWACSocialAgent.roleLINK)
					{
						if( (!alreadyProcessedRouteRequestManager.isAlreadyProcessed(tMsg.getSender(), tMsg.getIdRequest())) && (tMsg.getSender()!=this.getUserId()))
						{
							//System.out.println("LINK #"+this.getUserId()+" VA FAIRE SUIVRE LA RECHERCHE");
							this.sendFrame(new MWACSocialFrame(this.getUserId(),MWACSocialFrame.BROADCAST_REPRESENTATIVE, tMsg));
						}
						else
						{
							//System.out.println("LINK #"+this.getUserId()+" NE VA PAS REPONDRE A LA RECHERCHE CAR DEJA TRAITE");
						}
					}
				}
				break;	
				case MWACSocialMessage.msgROUTE_REPLY:
					if(frm.getReceiver()==this.getUserId())
					{

						MWACSocialMessage_RouteReply tMsg = (MWACSocialMessage_RouteReply) msg;

						if(this.getRole()==MWACSocialAgent.roleREPRESENTATIVE)
						{
							if(tMsg.getReceiver()==this.getUserId())
							{
								//System.out.println("repr "+this.getUserId()+" J'ai eu mon route reply!");
								int[] route = MWACSocialRouteAssistant.cloneRoute(tMsg.getRoute(), 1+tMsg.getRoute().length);
								route[route.length-1]=tMsg.getSender();
								this.networkPartialKnowledgeManager.addRoute(route);
								int member=this.networkPartialKnowledgeManager.getRouteRequestAssociatedReceiver(tMsg.getIdRequest());
								this.networkPartialKnowledgeManager.addIdGroupAssociation(MWACSocialRouteAssistant.getLastId(route), member);
								this.tryToProcessWaitingSendedMessage();
							}
							else
							{
								//System.out.println("RECU PAR repr "+this.getUserId()+" ROUTE REPLY "+tMsg.toString());
								int relais = this.neighboorlist.getLinkToRepresentant(MWACSocialRouteAssistant.getPreviousId(tMsg.getRoute(),this.getUserId()));
								if (relais==MWACSocialRouteAssistant.UNDEFINED_ID) relais=this.neighboorlist.getLinkToRepresentant(tMsg.getReceiver());
								if (relais==MWACSocialNeighboorList.UNDEFINED_ID) 
									System.out.println("!!!!!! Moi repr "+this.getUserId()+" ne trouve pas le précédent de "+frm.getSender()+" dans la route "+MWACSocialRouteAssistant.integerArrayToString(tMsg.getRoute())+" (pour un ROUTE REPLY)");
								else
									this.sendFrame(new MWACSocialFrame(this.getUserId(),relais,new MWACSocialMessage_RouteReply(tMsg.getSender(),tMsg.getReceiver(),tMsg.getIdRequest(),tMsg.getRoute())));
							}
						}
						else if(this.getRole()==MWACSocialAgent.roleLINK)
						{

							//System.out.println("RECU PAR LINK"+this.getUserId()+" ROUTE REPLY "+tMsg.toString());
							int dest;
							if(this.neighboorlist.contains(tMsg.getReceiver()))
							{
								dest = tMsg.getReceiver();
								//System.out.println("Le dest "+dest+" est mon voisin");
							}
							else if(MWACSocialGroupAssistant.containsGroup(tMsg.getSender(), this.getGroups()))
							{
								dest = MWACSocialRouteAssistant.getLastId(tMsg.getRoute());
								//System.out.println("Le dest "+dest+" est mon voisin representant");
							}
							else 
							{
								dest=(MWACSocialRouteAssistant.getPreviousId(tMsg.getRoute(),frm.getSender()));
								//System.out.println("Le dest "+dest+" est le precedent");
							}
							if (dest==MWACSocialRouteAssistant.UNDEFINED_ID) 
								System.out.println("!!!!!! Moi link "+this.getUserId()+" ne trouve pas le précédent de "+frm.getSender()+" dans la route "+MWACSocialRouteAssistant.integerArrayToString(tMsg.getRoute())+" (pour un ROUTE REPLY)");
							else
								this.sendFrame(new MWACSocialFrame(this.getUserId(),dest,new MWACSocialMessage_RouteReply(tMsg)));

						}
					}
					break;
				case MWACSocialMessage.msgROUTED_DATA:
					if(frm.getReceiver()==this.getUserId())
					{
						MWACSocialMessage_RoutedData tMsg = (MWACSocialMessage_RoutedData) msg;

						if (tMsg.getReceiver()==this.getUserId())
						{
							this.receiveMessage(new MWACSocialMessage_Data(tMsg.getSender(),tMsg.getReceiver(),tMsg.getMsg()));
						}
						else if(this.getRole()==MWACSocialAgent.roleREPRESENTATIVE)
						{
							if (this.neighboorlist.contains(tMsg.getReceiver()))
							{
								//System.out.println("JE SUIS LE REPRESENTANT "+this.getUserId()+" DU RECEPTEUR ");
								this.sendFrame(new MWACSocialFrame(this.getUserId(),tMsg.getReceiver(),new MWACSocialMessage_Data(tMsg.getSender(),tMsg.getReceiver(),tMsg.getMsg())));
							}
							else
							{
								//System.out.println("RECU PAR repr "+this.getUserId()+" ROUTED DATA "+frm.toString());
								int relais = this.neighboorlist.getLinkToRepresentant(MWACSocialRouteAssistant.getNextId(tMsg.getRoute(),this.getUserId()));
								//System.out.println("Le suivant de moi "+this.getUserId()+" dans "+MWACRouteAssistant.routeToString(tMsg.getRoute())+" est "+relais);
								if (relais==MWACSocialNeighboorList.UNDEFINED_ID) 
									System.out.println("!!!!!! Moi repr "+this.getUserId()+" ne trouve pas le suivant de "+frm.getSender()+" dans la route "+MWACSocialRouteAssistant.integerArrayToString(tMsg.getRoute())+" (pour un ROUTED DATA)");
								else
								{
									tMsg.setRoute(MWACSocialRouteAssistant.removeHead(tMsg.getRoute()));
									this.sendFrame(new MWACSocialFrame(this.getUserId(),relais,new MWACSocialMessage_RoutedData(tMsg)));
								}
							}
						}
						else if(this.getRole()==MWACSocialAgent.roleLINK)
						{

							//System.out.println("LINK "+this.getUserId()+" RECOIT ROUTED DATA "+frm.toString());
							int dest;
							//System.out.println("INFO "+frm.getSender()+"/"+MWACRouteAssistant.routeToString(this.getGroups()));
							dest = MWACSocialRouteAssistant.getFirstId(tMsg.getRoute());
							//System.out.println("Envoyé par un de mes repr. Le prochain sera "+dest);

							if (dest==MWACSocialNetworkPartialKnowledgeManager.UNDEFINED_ID)
								System.out.println("!!!!!! Moi link "+this.getUserId()+" ne trouve pas le suivant de "+frm.getSender()+" dans la route "+MWACSocialRouteAssistant.integerArrayToString(tMsg.getRoute())+" (pour un ROUTED DATA)");
							else
								this.sendFrame(new MWACSocialFrame(this.getUserId(),dest,new MWACSocialMessage_RoutedData(tMsg)));

						}
					}
					break;	

				case MWACSocialMessage.msgNEIGHBOORHOOD_SERVICES_REQUEST:
				{
					MWACSocialMessage_NeighboorhoodServicesRequest tMsg=(MWACSocialMessage_NeighboorhoodServicesRequest) msg;
					this.sendFrame(new MWACSocialFrame(this.getUserId(),tMsg.getSender(),new MWACSocialMessage_NeighboorhoodServicesReply(this.getUserId(),tMsg.getSender(),this.services)));
				}
				break;
				case MWACSocialMessage.msgNEIGHBOORHOOD_SERVICES_REPLY:
					if(this.getRole()==MWACSocialAgent.roleREPRESENTATIVE)
					{
						MWACSocialMessage_NeighboorhoodServicesReply tMsg=(MWACSocialMessage_NeighboorhoodServicesReply) msg;
						this.neighoorhoodServices.addServices(tMsg.getSender(), tMsg.getServices());
					}
					break;	
				case MWACSocialMessage.msgSERVICES_REQUEST:
				{
					MWACSocialMessage_ServicesRequest tMsg=(MWACSocialMessage_ServicesRequest) msg;

					if(this.getRole()==MWACSocialAgent.roleREPRESENTATIVE)
					{
						// est ce une requete d'un de mes membres
						if(tMsg.getReceiver()==this.getUserId() && tMsg.getSender()!=this.getUserId())
						{
							// Je dois faire cette recherche pour mon membre
							System.out.println("Le représentant "+this.getUserId()+" assume la recherche pour "+tMsg.getSender());
							this.searchedServices.add(new ServiceSearchItem(this.mas.elapsedSimulationTime(),tMsg.getServices()));
						}
						else
						{

							if( (!alreadyProcessedServicesRequestManager.isAlreadyProcessed(tMsg.getSender(), tMsg.getIdRequest())) && (tMsg.getSender()!=this.getUserId()))
							{
								Vector<Integer> searchedServ = MWACSocialRouteAssistant.integerArrayToVector(tMsg.getServices());
								Vector<Integer> founded = new Vector<Integer>();

								ListIterator<Integer> iter = searchedServ.listIterator();
								Integer item;
								while(iter.hasNext())
								{
									item=iter.next();
									if(this.services.contains(item) || this.neighoorhoodServices.availableService(item))
									{
										founded.add(item);
										iter.remove();
									}
								}

								if(!founded.isEmpty())
								{
									int relais = this.neighboorlist.getLinkToRepresentant(MWACSocialRouteAssistant.getLastId(tMsg.getRoute()));
									if (relais==MWACSocialRouteAssistant.UNDEFINED_ID) relais=this.neighboorlist.getLinkToRepresentant(tMsg.getSender());
									if (relais==MWACSocialNeighboorList.UNDEFINED_ID) System.out.println("!!!!!! Moi repr "+this.getUserId()+" ne trouve pas le précédent de "+frm.getSender()+" dans la route "+MWACSocialRouteAssistant.integerArrayToString(tMsg.getRoute())+" (pour un ROUTE REPLY)");

									System.out.println("\nAg "+this.getUserId()+" send its founded services   (MWACSocialMessage_ServicesReply)");
									this.sendFrame(new MWACSocialFrame(this.getUserId(),relais,new MWACSocialMessage_ServicesReply(this.getUserId(),tMsg.getSender(),tMsg.getIdRequest(),MWACSocialRouteAssistant.vectorToIntegerArray(founded),tMsg.getRoute())));
								}

								if(!searchedServ.isEmpty() && (tMsg.getTTL()>0))
								{
									tMsg.setServices(MWACSocialRouteAssistant.vectorToIntegerArray(searchedServ));
									tMsg.setRoute(MWACSocialRouteAssistant.add(tMsg.getRoute(),this.getUserId()));

									tMsg.decreaseTTL();
									tMsg.setServices(MWACSocialRouteAssistant.vectorToIntegerArray(searchedServ));

									System.out.println("\nAg "+this.getUserId()+" folow the services request)  "+MWACSocialRouteAssistant.integerArrayToString(tMsg.getServices()));
									this.sendFrame(new MWACSocialFrame(this.getUserId(),MWACSocialFrame.BROADCAST_LINK,tMsg));
								}

							}
						}
					}
					else if(this.getRole()==MWACSocialAgent.roleLINK)
					{
						if( (!alreadyProcessedRouteRequestManager.isAlreadyProcessed(tMsg.getSender(), tMsg.getIdRequest())) && (tMsg.getSender()!=this.getUserId()) && (frm.getReceiver()!=MWACSocialFrame.BROADCAST_REPRESENTATIVE))
						{	
							// Si les destinataires sont les liaisons, je fais suivre au representant
							this.sendFrame(new MWACSocialFrame(this.getUserId(),MWACSocialFrame.BROADCAST_REPRESENTATIVE,tMsg));
						}
					}
				}
				break;
				case MWACSocialMessage.msgSERVICES_REPLY:
				{
					if(frm.getReceiver()==this.getUserId())
					{

						MWACSocialMessage_ServicesReply tMsg = (MWACSocialMessage_ServicesReply) msg;

						if(this.getRole()==MWACSocialAgent.roleREPRESENTATIVE)
						{
							if(tMsg.getReceiver()==this.getUserId())
							{
								// Je met à jour mes services
								this.verifySearchedServices(tMsg.getServices());
							}
							else
							{
								//System.out.println("RECU PAR repr "+this.getUserId()+" ROUTE REPLY "+tMsg.toString());
								int relais = this.neighboorlist.getLinkToRepresentant(MWACSocialRouteAssistant.getPreviousId(tMsg.getRoute(),this.getUserId()));
								if (relais==MWACSocialRouteAssistant.UNDEFINED_ID) relais=this.neighboorlist.getLinkToRepresentant(tMsg.getReceiver());
								if (relais==MWACSocialNeighboorList.UNDEFINED_ID) 
									System.out.println("!!!!!! Moi repr "+this.getUserId()+" ne trouve pas le précédent de "+frm.getSender()+" dans la route "+MWACSocialRouteAssistant.integerArrayToString(tMsg.getRoute())+" (pour un ROUTE REPLY)");
								else
									this.sendFrame(new MWACSocialFrame(this.getUserId(),relais,new MWACSocialMessage_ServicesReply(tMsg.getSender(),tMsg.getReceiver(),tMsg.getIdRequest(),tMsg.getServices(),tMsg.getRoute())));
							}
						}
						else if(this.getRole()==MWACSocialAgent.roleLINK)
						{

							//System.out.println("RECU PAR LINK"+this.getUserId()+" ROUTE REPLY "+tMsg.toString());
							int dest;
							if(this.neighboorlist.contains(tMsg.getReceiver()))
							{
								dest = tMsg.getReceiver();
								//System.out.println("Le dest "+dest+" est mon voisin");
							}
							else if(MWACSocialGroupAssistant.containsGroup(tMsg.getSender(), this.getGroups()))
							{
								dest = MWACSocialRouteAssistant.getLastId(tMsg.getRoute());
								//System.out.println("Le dest "+dest+" est mon voisin representant");
							}
							else 
							{
								dest=(MWACSocialRouteAssistant.getPreviousId(tMsg.getRoute(),frm.getSender()));
								//System.out.println("Le dest "+dest+" est le precedent");
							}
							if (dest==MWACSocialRouteAssistant.UNDEFINED_ID) 
								System.out.println("!!!!!! Moi link "+this.getUserId()+" ne trouve pas le précédent de "+frm.getSender()+" dans la route "+MWACSocialRouteAssistant.integerArrayToString(tMsg.getRoute())+" (pour un ROUTE REPLY)");
							else
								this.sendFrame(new MWACSocialFrame(this.getUserId(),dest,new MWACSocialMessage_ServicesReply(tMsg)));

						}
					}
				}
				break;
			
				default:
					System.out.println("ERREUR!!!! NONE IMPLEMENTED MESSAGE "+msg.toString());
				}				
			}




			// Verify the role

			// No neighboors
			byte newRole=this.role;	boolean conflict=false;

			if(this.neighboorlist.isEmpty()) 
				newRole=MWACSocialAgent.roleNOTHING;
			else
			{
				switch(this.role)
				{
				case MWACSocialAgent.roleNOTHING:
				case MWACSocialAgent.roleSIMPLEMEMBER:
				{
					int n = this.neighboorlist.getNbRepresentative();
					if(n==0) 
						newRole=this.roleREPRESENTATIVE;
					else if(n==1) 
					{
						newRole=this.roleSIMPLEMEMBER;
						if(hasPreviouslyLostRepresentativeRole) newRole=this.role;
					}
					else newRole=this.roleLINK;



				}
				break;
				case MWACSocialAgent.roleLINK:
				{
					int n = this.neighboorlist.getNbRepresentative();
					if(n==0) newRole=this.roleREPRESENTATIVE;
					else if(n==1) newRole=this.roleSIMPLEMEMBER;
					else newRole=this.roleLINK;
				}
				break;
				case MWACSocialAgent.roleREPRESENTATIVE:
				{
					if(hasPreviouslyLostRepresentativeRole||lostRepresentativeRole)
						newRole=MWACSocialAgent.roleNOTHING;
					else
					{
						if(this.neighboorlist.getNbRepresentative()>0) conflict=true;
						newRole=MWACSocialAgent.roleREPRESENTATIVE;
					}
				}
				break;
				}
			}



			// Y a t'il un conflit
			if (this.role!=newRole)
			{
				this.ageOfTheCurrentRole=this.mas.elapsedSimulationTime();

				if(MWACSocialAgent.DEBUG) debug("Je change de role "+MWACSocialAgent.roleToString(this.role)+" => "+MWACSocialAgent.roleToString(newRole)+" lost="+lostRepresentativeRole+"  prevLost="+hasPreviouslyLostRepresentativeRole);
				this.setRole(newRole);
				switch(newRole)
				{
				case MWACSocialAgent.roleREPRESENTATIVE:
					this.alreadyProcessedRouteRequestManager=new MWACSocialAlreadyProcessedRouteRequestManager();
					this.alreadyProcessedServicesRequestManager=new MWACSocialAlreadyProcessedRouteRequestManager();
					this.messageToTransmitQueue=new MWACSocialMessageFIFOStack();
					this.networkPartialKnowledgeManager=new MWACSocialNetworkPartialKnowledgeManager();
					this.neighoorhoodServices=new NeighoorhoodServicesManager();
					mustSendWhoAreMyNeighoors=true;
					break;
				case MWACSocialAgent.roleLINK:
					this.alreadyProcessedServicesRequestManager=new MWACSocialAlreadyProcessedRouteRequestManager();
					this.alreadyProcessedRouteRequestManager=new MWACSocialAlreadyProcessedRouteRequestManager();
					this.networkPartialKnowledgeManager=null;
					this.messageToTransmitQueue=null;
					mustSendAPresentation=true;
					break;
				case MWACSocialAgent.roleNOTHING:
					this.alreadyProcessedRouteRequestManager=null;
					this.alreadyProcessedServicesRequestManager=null;
					this.networkPartialKnowledgeManager=null;
					this.messageToTransmitQueue=null;
					this.sendIntroduction();
					mustSendAPresentation=false;
					break;
				case MWACSocialAgent.roleSIMPLEMEMBER:
					this.alreadyProcessedServicesRequestManager=null;
					this.alreadyProcessedRouteRequestManager=null;
					this.networkPartialKnowledgeManager=null;
					this.messageToTransmitQueue=null;
					mustSendAPresentation=true;
					break;
				}
				if (MWACSocialAgent.DEBUG && newRole==MWACSocialAgent.roleREPRESENTATIVE) this.debug("Je deviens ROLE_REPRESENTANT");
			}



			if(conflict)
			{
				if(MWACSocialAgent.DEBUG) debug("Je suis en conflit avec "+this.neighboorlist.getRepresentativeIdentifiers());
				this.sendConflictResolution();
			}
			else if(mustSendWhoAreMyNeighoors)
				this.sendWhoAreMyNeighboors();
			else if(mustSendAPresentation)
				this.sendPresentation();



			// Forget the message and the frame
			frm=null; msg=null;

			// Must be freezed?
			try{Thread.sleep(SLEEP_TIME_SLOT);}catch(Exception e){}
		}

		// AGENT SPECIFIC TASK
		System.out.println("Fin du thread "+this.getUserId());
	}


	public void tryToProcessWaitingSendedMessage()
	{
		SocialMessageToSendItem item;
		ListIterator<SocialMessageToSendItem> iter=this.messageToTransmitQueue.listIterator();
		int[] route;

		while(iter.hasNext())
		{
			item=iter.next();
			route=this.networkPartialKnowledgeManager.getDataMessageRoute(item.msg.getReceiver());
			if(route!=null) 
			{
				System.out.println("Repr "+this.getUserId()+" J'envoie le message routé!!!");
				this.sendFrame(new MWACSocialFrame(this.getUserId(),this.neighboorlist.getLinkToRepresentant(MWACSocialRouteAssistant.getFirstId(route)),new MWACSocialMessage_RoutedData(item.msg,route)));
			}
		}
	}

	/**
	 * return the group identifier of this agent (the first one for a link agent)
	 * @return the group of the agent
	 */
	public int getGroup()
	{
		if(this.role==MWACSocialAgent.roleREPRESENTATIVE)
			return this.getUserId();
		else
		{
			ArrayList<Integer> repr=this.neighboorlist.getRepresentativeIdentifiers();
			int nbRepr=repr.size();
			if(nbRepr==0)
			{
				switch(this.role)
				{
				case MWACSocialAgent.roleSIMPLEMEMBER:
				case MWACSocialAgent.roleLINK:
					System.out.println("<A"+this.getUserId()+","+MWACSocialAgent.roleToString(this.role)+">!!!!! ERREUR de cohérence entre le role et la liste de voisins");
				case MWACSocialAgent.roleNOTHING:
					// C'est normal pour eux
					return MWACSocialAgent.groupNONE;
				}
			}
			else if (nbRepr==1)
			{
				switch(this.role)
				{
				case MWACSocialAgent.roleSIMPLEMEMBER:
					return repr.get(0);
				case MWACSocialAgent.roleLINK:
					System.out.println("<B"+this.getUserId()+","+MWACSocialAgent.roleToString(this.role)+">!!!!! ERREUR de cohérence entre le role et la liste de voisins");
				case MWACSocialAgent.roleNOTHING:
					return MWACSocialAgent.groupNONE;
				}
			}
			else
			{
				if (this.role==MWACSocialAgent.roleLINK)
					return repr.get(0);
				else
				{
					if (this.role!=MWACSocialAgent.roleNOTHING) System.out.println("<C"+this.getUserId()+","+MWACSocialAgent.roleToString(this.role)+">!!!!! ERREUR de cohérence entre le role et la liste de voisins");
					return MWACSocialAgent.groupNONE;
				}
			}
		}
		System.out.println("<D"+this.getUserId()+","+MWACSocialAgent.roleToString(this.role)+">!!!!! ERREUR de cohérence entre le role et la liste de voisins");
		return MWACSocialAgent.groupNONE;
	}

	/**
	 * return the groups identifiers of this agent (interesting for link agent)
	 * @return the groups associated to the agent
	 */
	public int[] getGroups()
	{
		int[] res;

		if(this.getRole()!=MWACSocialAgent.roleLINK)
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
		if (this.role==this.roleSIMPLEMEMBER) return 4*MWACSocialAgent.SLEEP_TIME_SLOT;
		return 0;
	}

	/** allows to an object to receive a message
	 * @param frame the received frame 
	 */
	public synchronized void receivedFrame(Frame frame)
	{
		super.receivedFrame(frame);
		this.receivedFrame((MWACSocialFrame) frame);
	}
	/** allows to an object to receive a message
	 * @param frame the received ASTRO_Frame 
	 */
	public synchronized void receivedFrame(MWACSocialFrame frame)
	{
		if( (frame.getReceiver()==MWACSocialFrame.BROADCAST) || (frame.getReceiver()==this.getUserId()) || ((frame.getReceiver()==MWACSocialFrame.BROADCAST_LINK) && this.getRole()==this.roleLINK) || ((frame.getReceiver()==MWACSocialFrame.BROADCAST_REPRESENTATIVE) && this.getRole()==this.roleREPRESENTATIVE)) 
			this.receivedMessageQueue.push(frame);
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
			super.notifyEvent(new RoleModificationEvent(this.getSystemId(),role));
			switch(role)
			{
			case MWACSocialAgent.roleNOTHING: super.setColor(colorNOTHING); break;
			case MWACSocialAgent.roleSIMPLEMEMBER:super.setColor(colorSIMPLEMEMBER); break;
			case MWACSocialAgent.roleLINK:super.setColor(colorLINK); break;
			case MWACSocialAgent.roleREPRESENTATIVE:super.setColor(colorREPRESENTATIVE); break;
			default: super.setColor(Color.BLACK);
			}
		}
	}

	/**
	 * 
	 */
	public void receiveMessage(MWACSocialMessage_Data msg)
	{
		super.notifyEvent(new ReceivedMessageEvent(this.getSystemId(),msg));
		System.out.println(this.getUserId()+" a recu le message "+((MWACSocialMessage_Data)msg).getMsg());
	}


	/**
	 * send a message (reserved to a representative member)
	 * @param msg the message to send
	 */
	private void sendMessage(MWACSocialMessage_Data msg)
	{
		if(this.neighboorlist.contains(msg.getReceiver()))
			super.sendFrame(new MWACSocialFrame(this.getUserId(),msg.getReceiver(),msg));
		else
		{
			short idRequest=this.messageToTransmitQueue.add(msg);
			this.networkPartialKnowledgeManager.addRouteRequestAndReceiverAssociation(idRequest, msg.getReceiver());
			super.sendFrame(new MWACSocialFrame(this.getUserId(),MWACSocialFrame.BROADCAST_LINK,new MWACSocialMessage_RouteRequest(this.getUserId(),msg.getReceiver(),idRequest,MWACSocialAgent.INITIAL_TTL)));
		}
	}


	/**
	 * send a message
	 * @param s the string of the message
	 * @param receiver the receiver of this message
	 */
	public void sendMessage(int receiver,String s)
	{

		MWACSocialMessage_Data msg = new MWACSocialMessage_Data(this.getUserId(),receiver,s);

		if(this.neighboorlist.contains(receiver))
		{
			super.notifyEvent(new SendedMessageEvent(this.getSystemId(),(Message)msg.clone()));
			this.sendDataFrame(receiver,msg);
		}
		else
		{
			switch(this.role)
			{
			case MWACSocialAgent.roleNOTHING:
			case MWACSocialAgent.roleSIMPLEMEMBER:
			case MWACSocialAgent.roleLINK:
				ArrayList<Integer> lst=this.neighboorlist.getRepresentativeIdentifiers();
				if(lst==null) 
					super.notifyEvent(new MessageNotTransmittedEvent(this.getSystemId(),msg,"Representative neighboor not found"));
				else
				{
					super.notifyEvent(new SendedMessageEvent(this.getSystemId(),(Message)msg.clone()));
					this.sendDataFrame(lst.get(0),msg);
				}
				break;
			case MWACSocialAgent.roleREPRESENTATIVE:
				System.out.println("REPRESENTANT "+this.getUserId());
				super.notifyEvent(new SendedMessageEvent(this.getSystemId(),(Message)msg.clone()));
				this.sendMessage(msg);
				break;
			}
		}
	}

	/**
	 * send a data frame
	 * @param dest receiver of the frame (next hop)
	 * @param msg the ASTRO data message
	 */
	private void sendDataFrame(int dest,MWACSocialMessage_Data msg)
	{
		if(MWACSocialAgent.DEBUG) debug(this.getUserId()+">>> Envoie d'un message de DONNEES");
		super.sendFrame(new MWACSocialFrame(this.getUserId(),dest,msg));
	}

	/**
	 * send a conflict resolution message
	 */
	private void sendConflictResolution() 
	{
		if(MWACSocialAgent.DEBUG) debug(this.getUserId()+">>> Envoie par d'un message de RESOLUTION DE CONFLIT");
		super.sendFrame(new MWACSocialFrame(this.getUserId(),MWACSocialFrame.BROADCAST,new MWACSocialMessage_ConflictResolution(this.getUserId(),MWACSocialMessage.BROADCAST,this.score())));
	}

	/**
	 * send a presentation message
	 */
	private void sendPresentation()
	{
		//Frame f=new Frame(getId(),dest,new ASTRO_Presentation(getId(),dest,role,group));
		if(MWACSocialAgent.DEBUG) debug(">>> Envoie par d'un message de PRESENTATION     <"+this.getUserId()+","+MWACSocialAgent.roleToString(this.role)+","+this.getGroup()+">");
		super.sendFrame(new MWACSocialFrame(this.getUserId(),MWACSocialFrame.BROADCAST,new MWACSocialMessage_Presentation(this.getUserId(),this.role,this.getGroups())));
	}

	/**
	 * send an introduction message
	 */
	private void sendIntroduction()
	{
		if(MWACSocialAgent.DEBUG) debug(">>> Envoie par d'un message d'INTRODUCTION      <"+this.getUserId()+","+MWACSocialAgent.roleToString(this.role)+","+this.getGroup()+">");
		super.sendFrame(new MWACSocialFrame(super.getUserId(),MWACSocialFrame.BROADCAST,new MWACSocialMessage_Introduction(this.getUserId())));
	}

	/**
	 * send a message to request the close neighboors
	 */
	private void sendWhoAreMyNeighboors()
	{
		//Frame f=new Frame(getId(),dest,new ASTRO_Presentation(getId(),dest,role,group));
		if(MWACSocialAgent.DEBUG) debug(">>> Envoie par d'un message de WHOAREMYNEIGHBOORS   <"+this.getUserId()+","+MWACSocialAgent.roleToString(this.role)+","+this.getGroup()+">");
		super.sendFrame(new MWACSocialFrame(this.getUserId(),MWACSocialFrame.BROADCAST,new MWACSocialMessage_WhoAreMyNeighboors(this.getUserId(),role,this.getGroups())));
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
		String res="";
		String roleDependantText="";
		if(getRole()==MWACSocialAgent.roleREPRESENTATIVE ) roleDependantText+="<BR><BR>"+this.neighoorhoodServices.toHTML();
		if(getRole()==MWACSocialAgent.roleREPRESENTATIVE && this.messageToTransmitQueue!=null) roleDependantText+="<BR><BR>"+this.messageToTransmitQueue.toHTML();
		if((getRole()==MWACSocialAgent.roleREPRESENTATIVE || getRole()==MWACSocialAgent.roleLINK) && this.alreadyProcessedRouteRequestManager!=null) roleDependantText+="<BR><BR>"+this.alreadyProcessedRouteRequestManager.toHTML();
		if(getRole()==MWACSocialAgent.roleREPRESENTATIVE && this.networkPartialKnowledgeManager!=null) roleDependantText+="<BR><BR>"+this.networkPartialKnowledgeManager.toHTML();
		res="<HTML>"+"<B>Id</B>="+this.getUserId()+"    <B>Role</B>="+MWACSocialAgent.roleToString(this.getRole())+"    <B>Group</B>="+MWACSocialGroupAssistant.groupsToString(this.getGroups())+"    <B>Energy</B>="+this.getBattery().getActualAmountOfEngergy()+"    <B>Range</B>="+this.getRange()+"<BR><BR>";
		res+=("<B>Supplied services </B>");
		for(int i=0;i<this.services.size();i++) res+=(""+this.services.get(i)+" ");
		res+="<BR><B>Searched services: </B><BR>";
		res+=this.searchedServices.toHTML();
		res+=("<BR><BR>"+this.neighboorlist.toHTML()+roleDependantText+"</HTML>");
		return res;
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
		case MWACSocialAgent.roleREPRESENTATIVE	: return "ROLE_REPRESENTATIVE";
		case MWACSocialAgent.roleLINK				: return "ROLE_LINK";
		case MWACSocialAgent.roleSIMPLEMEMBER		: return "ROLE_SIMPLEMEMBER";
		case MWACSocialAgent.roleNOTHING			: return "ROLE_NOTHING";
		}
		return "ROLE_UNDEFINED";
	}

	public boolean verifySearchedServices(int[] services)
	{
		if (this.searchedServices.isEmpty()) return false;

		long date=this.mas.elapsedSimulationTime();
		long diff;

		ServiceSearchItem item;
		ListIterator<ServiceSearchItem> iter = this.searchedServices.searchedServices.listIterator();

		while(iter.hasNext())
		{
			item=iter.next();
			diff=item.scheduledDate-date;
			if(diff<0)
			{
				Vector<Integer> v = MWACSocialRouteAssistant.integerArrayToVector(item.searchedServices());
				ListIterator<Integer> iterSearchedServices=v.listIterator();
				Integer search;
				while(iterSearchedServices.hasNext())
				{
					search=iterSearchedServices.next();
					if(this.services.contains(search) || this.neighoorhoodServices.availableService(search) || MWACSocialRouteAssistant.contains(services, search)) 
					{
						System.out.println("\nLe représentant "+this.getUserId()+" connait déjà le service "+search);
						iterSearchedServices.remove();
					}
				}
				item.services=v;

				if(v.isEmpty())
				{
					System.out.println("\nRequete de "+this.getUserId()+" satisfaite");
					iter.remove();
				}

			}
		}
		return true;
	}

	public boolean verifySearchedServices()
	{
		if (this.searchedServices.isEmpty()) return false;

		long date=this.mas.elapsedSimulationTime();
		long diff;

		ServiceSearchItem item;
		ListIterator<ServiceSearchItem> iter = this.searchedServices.searchedServices.listIterator();

		while(iter.hasNext())
		{
			item=iter.next();
			diff=item.scheduledDate-date;
			if(diff<0)
			{

				if(this.role!=MWACSocialAgent.roleREPRESENTATIVE)
				{
					ArrayList<Integer> lst=this.neighboorlist.getRepresentativeIdentifiers();
					if(lst==null) 
						System.out.println("****************RECHERCHE DE SERVICE IMPOSSIBLE **************************");
					else
					{

						Vector<Integer> v = MWACSocialRouteAssistant.integerArrayToVector(item.searchedServices());
						ListIterator<Integer> iterSearchedServices=v.listIterator();
						Integer search;
						while(iterSearchedServices.hasNext())
						{
							search=iterSearchedServices.next();
							if(this.services.contains(search)) 
							{
								System.out.println("\nLe non représentant  "+this.getUserId()+" connait déjà le service "+search);
								iterSearchedServices.remove();
							}
						}
						item.services=v;
						if(v.size()>0)
						{
							System.out.println("\nLe non représentant "+this.getUserId()+" recherche des services. Il le signal a son représentant "+lst.get(0)+" services:"+MWACSocialRouteAssistant.integerArrayToString(item.searchedServices()));
							this.sendFrame(new MWACSocialFrame(this.getUserId(),lst.get(0),new MWACSocialMessage_ServicesRequest(this.getUserId(),lst.get(0),item.requestIdentifier,item.TTL,item.searchedServices(),new int[0])));
						}
						iter.remove();
					}
				}
				else 
				{
					Vector<Integer> v = MWACSocialRouteAssistant.integerArrayToVector(item.searchedServices());
					ListIterator<Integer> iterSearchedServices=v.listIterator();
					Integer search;
					while(iterSearchedServices.hasNext())
					{
						search=iterSearchedServices.next();
						if(this.services.contains(search) || this.neighoorhoodServices.availableService(search)) 
						{
							System.out.println("\nLe représentant "+this.getUserId()+" connait déjà le service "+search);
							iterSearchedServices.remove();
						}

					}
					item.services=v;

					if(v.isEmpty())
					{
						System.out.println("\nRequete de "+this.getUserId()+" satisfaite");
						iter.remove();
					}
					else
					{
						// Je suis représentant
						if(item.nbRequest==0)
						{
							System.out.println("\nRecherche initiée par le représentant "+this.getUserId()+" services:"+MWACSocialRouteAssistant.integerArrayToString(item.searchedServices()));
							item.TTL=this.INITIAL_TTL;
							item.nbRequest++;
							item.serviceRequestDate=this.mas.elapsedSimulationTime();
							item.requestIdentifier=this.nextServiceRequestId++;
							this.sendFrame(new MWACSocialFrame(this.getUserId(),MWACSocialFrame.BROADCAST_LINK,new MWACSocialMessage_ServicesRequest(this.getUserId(),MWACSocialMessage.BROADCAST,item.requestIdentifier,item.TTL,item.searchedServices(),new int[0])));
						}
						else if(date-item.serviceRequestDate>MAX_WAITING_TIME)
						{
							if(item.TTL>=MAX_TTL) 
							{
								System.out.println("\nArret de recherche pour cause de TTL ("+item.TTL+") initiée par le représentant "+this.getUserId());
								iter.remove();
							}
							else
							{
								System.out.println("\nRelance de recherche initiée (TTL="+item.TTL+") par le représentant "+this.getUserId()+" services:"+MWACSocialRouteAssistant.integerArrayToString(item.searchedServices()));
								item.TTL+=TTL_STEP;
								item.nbRequest++;
								item.serviceRequestDate=this.mas.elapsedSimulationTime();
								item.requestIdentifier=this.nextServiceRequestId++;
								this.sendFrame(new MWACSocialFrame(this.getUserId(),MWACSocialFrame.BROADCAST_LINK,new MWACSocialMessage_ServicesRequest(this.getUserId(),MWACSocialMessage.BROADCAST,item.requestIdentifier,item.TTL,item.searchedServices(),new int[0])));
							}
						}
					}
				}
			}
		}
		return true;
	}


	public void updateNeighboorServicesList()
	{
		System.out.println("UPDATE SERVICES LIST BY "+this.getUserId());
		this.neighoorhoodServices=new NeighoorhoodServicesManager();
		this.sendFrame(new MWACSocialFrame(this.getUserId(),MWACSocialFrame.BROADCAST,new MWACSocialMessage_NeighboorhoodServicesRequest(this.getUserId(),MWACSocialMessage.BROADCAST)));
	}


	private class SearchedServices
	{
		public Vector<ServiceSearchItem> searchedServices;

		public SearchedServices()
		{
			this.searchedServices=new Vector<ServiceSearchItem>();
		}

		public boolean isEmpty()
		{
			return this.searchedServices.isEmpty();
		}
		public void add(ServiceSearchItem item)
		{
			this.searchedServices.add(item);
		}

		public String toHTML()
		{
			String str="";

			str+="<TABLE border=1>";
			str+="<TR><TD>Date</TD><TD>Num request</TD><TD>Request id</TD><TD>TTL</TD><TD>Searched services</TD></TR>";

			ServiceSearchItem item;
			ListIterator<ServiceSearchItem> iter = this.searchedServices.listIterator();
			while(iter.hasNext())
			{
				item=iter.next();
				str+="<TR><TD>"+item.scheduledDate+"</TD><TD>"+item.nbRequest+"</TD><TD>"+item.requestIdentifier+"</TD><TD>"+item.TTL+"</TD><TD>"+item.servicesToString()+"</TD></TR>";
			}
			str+="</TABLE>";

			return str;
		}

		public String toString()
		{
			String res="";
			if(this.searchedServices.isEmpty())
				res="No services";
			else
				for(int i=0;i<this.searchedServices.size();i++) res+=this.searchedServices.get(i).toString()+"\n";


			return res;
		}
	}

	private class ServiceSearchItem
	{
		public long scheduledDate;
		public Vector<Integer> services;

		public long serviceRequestDate;
		public byte requestIdentifier;
		public byte TTL;
		public byte nbRequest;

		public ServiceSearchItem(String res)
		{

			String[] l = res.split("\t");
			this.scheduledDate=new Long(l[0]);
			this.services=new Vector<Integer>();
			String[] s = l[1].split(",");

			for(int i=0;i<s.length;i++) if(!this.services.contains(new Integer(s[i]))) this.services.add(new Integer(s[i]));
			//System.out.println(">>>>> ["+s.length+"] "+res+"\n>>>>> "+this.toString());

			this.serviceRequestDate=0;
			this.nbRequest=0;

		}

		public ServiceSearchItem(long date,int[] services)
		{
			this(date,new Vector<Integer>() );
			for(int i=0;i<services.length;i++) this.services.add(services[i]);
		}
		public ServiceSearchItem(long date,Vector<Integer> services)
		{
			this.scheduledDate=date;
			this.services=services;

			this.serviceRequestDate=0;
		}

		public String toString()
		{
			String res="At "+this.scheduledDate+"ms, ";
			if(this.services.isEmpty()) 
				res+="no services";
			else
			{
				res+=" searched services :"+this.services.get(0);
				for(int i=1;i<this.services.size();i++) res+=","+this.services.get(i);
			}
			return res;
		}

		public int[] searchedServices()
		{
			int[] res = new int[this.services.size()];
			for(int i=0;i<this.services.size();i++) res[i]=this.services.get(i);
			return res;
		}

		public String servicesToString()
		{

			if(this.services.isEmpty()) 
				return "no services";
			else
			{
				String res=""+this.services.get(0);
				for(int i=1;i<this.services.size();i++) res+=","+this.services.get(i);
				return res;
			}

		}


	}



	private class NeighoorhoodServicesManager
	{
		public Vector<NeighoorhoodServicesItem> services;

		public NeighoorhoodServicesManager()
		{
			this.services=new Vector<NeighoorhoodServicesItem>();
		}

		public boolean availableService(int service)
		{

			ListIterator<NeighoorhoodServicesItem> iter = this.services.listIterator();
			NeighoorhoodServicesItem item;
			boolean trouve=false;;
			while(!trouve && iter.hasNext()) trouve=iter.next().services.contains(service);
			return trouve;
		}

		public void addServices(int neighboor,int[] services)
		{
			for(int i=0;i<services.length;i++) this.addService(neighboor, services[i]);
		}
		public void addService(int neighboor,int service)
		{
			ListIterator<NeighoorhoodServicesItem> iter = this.services.listIterator();
			NeighoorhoodServicesItem item;
			boolean fini=false;;
			while(!fini && iter.hasNext())
			{
				item=iter.next();
				if(item.neighboorIdentifier==neighboor)
				{
					item.services.add(service);
					fini=true;
				}
			}
			if(!fini)
			{
				NeighoorhoodServicesItem newItem = new NeighoorhoodServicesItem(neighboor);
				newItem.services.add(service);
				this.services.add(newItem);
			}
		}


		public String toHTML()
		{
			String str="<B>Services in my group</B>";

			str+="<TABLE border=1>";
			str+="<TR><TD>Neighboor</TD><TD>Services</TD></TR>";

			ListIterator<NeighoorhoodServicesItem> iter = this.services.listIterator();
			NeighoorhoodServicesItem item;
			while(iter.hasNext())
			{
				item=iter.next();
				str+="<TR><TD>"+item.neighboorIdentifier+"</TD><TD>";
				for(int i=0;i<item.services.size();i++) str+=""+item.services.get(i)+" "; 
				str+="</TD></TR>";
			}
			str+="</TABLE>";

			return str;
		}

		private class NeighoorhoodServicesItem
		{
			public int neighboorIdentifier;
			public Vector<Integer> services;

			public NeighoorhoodServicesItem(int neighboor)
			{
				this.neighboorIdentifier=neighboor;
				this.services=new Vector<Integer>();
			}
		}
	}






}
