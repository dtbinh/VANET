package simulation.solutions.custom.TrustedRoutingMWAC;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Random;

import simulation.entities.Agent;
import simulation.multiagentSystem.MAS;
import simulation.events.system.MessageNotTransmittedEvent;
import simulation.events.ProtocolBreakEvent;
import simulation.events.system.ReceivedMessageEvent;
import simulation.events.system.RoleModificationEvent;
import simulation.events.system.SendedMessageEvent;
import simulation.messages.Frame;
import simulation.messages.Message;
import simulation.solutions.custom.TrustedRoutingMWAC.Messages.MWACFrame;
import simulation.solutions.custom.TrustedRoutingMWAC.Messages.MWACMessage;
import simulation.solutions.custom.TrustedRoutingMWAC.Messages.MWACMessage_ConflictResolution;
import simulation.solutions.custom.TrustedRoutingMWAC.Messages.MWACMessage_Data;
import simulation.solutions.custom.TrustedRoutingMWAC.Messages.MWACMessage_Introduction;
import simulation.solutions.custom.TrustedRoutingMWAC.Messages.MWACMessage_Presentation;
import simulation.solutions.custom.TrustedRoutingMWAC.Messages.MWACMessage_RouteReply;
import simulation.solutions.custom.TrustedRoutingMWAC.Messages.MWACMessage_RouteRequest;
import simulation.solutions.custom.TrustedRoutingMWAC.Messages.MWACMessage_RoutedData;
import simulation.solutions.custom.TrustedRoutingMWAC.Messages.MWACMessage_WhoAreMyNeighboors;
import simulation.utils.aDate;

public class TrustedRoutingMWACAgent extends simulation.entities.Agent {

	private class InteractionContext
	{

		public MWACFrame frame;

		public boolean lostRepresentativeRole;	
		public boolean hasPreviouslyLostRepresentativeRole;
		public boolean roleConfict;

		public boolean mustSendWhoAreMyNeighoors; 
		public boolean mustSendAPresentation;	
		public boolean reinitializationOfTheRole;

		public InteractionContext(){
			this.init();
		}

		public void init(){
			this.frame = null;
			this.mustSendWhoAreMyNeighoors = false;
			this.mustSendAPresentation = false;
			this.lostRepresentativeRole = false;
			this.hasPreviouslyLostRepresentativeRole = false;
			this.roleConfict = false;
			this.reinitializationOfTheRole = false;
		}
	}
	
	/** used when the id for which we want the role is not in the neighbourhood **/
	public final static byte roleVOID = -1;
	/** no role */
	public final static byte roleNOTHING = 0;
	/** simple member role */
	public final static byte roleSIMPLEMEMBER = 1;
	/** link member role */
	public final static byte roleLINK = 2;
	/** representative member role */
	public final static byte roleREPRESENTATIVE = 3;
	/** color of no role members */
	public final static Color colorNOTHING = Color.LIGHT_GRAY;
	/** color of representative members */
	public final static Color colorREPRESENTATIVE = Color.RED;
	/** color of link members */
	public final static Color colorLINK = Color.GREEN;
	/** color of simple member */
	public final static Color colorSIMPLEMEMBER = Color.YELLOW;

	/** member of non group */
	private static int groupNONE = -1;

	/** role of the agent */
	private byte role;
	/** received message queue */
	private FrameFIFOStack receivedMessageQueue;
	/** message to send (if the agent is Representative */
	private MWACMessageFIFOStack messageToTransmitQueue;
	/** neighboorList */
	private TrustedRoutingMWACNeighboorList neighboorlist;
	/** to manage already processed route request */
	private MWACAlreadyProcessedRouteRequestManager alreadyProcessedRouteRequestManager;
	/** Memorize the energy level when the agent has been elected representative */
	private float pourcentOfAvailableEnergyWhenElectedRepresentative;


	/** MWAC route fragment manager */
	private MWACNetworkPartialKnowledgeManager networkPartialKnowledgeManager;
	
	
	/***************** TRUST VARIABLES *****************/
	
	private boolean useTrust = false;
	
	private static float TRUST_THRESHOLD		= 0.6f;
	private static float RADICAL_TRUST_DECREASE = 0.85f;
	
	private float decreaseTrustStep = 0.05f;
	
	
	private static long WATCH_TIME_DEFAULT 	= 5000; // ms
	private static long WATCH_TIME_MAX 		= 10000; // ms
	private static long WATCH_TIME_STEP		= 1000; // ms
	
	private WatchList watchList = new WatchList();
	
	private HashMap<Integer,Long> watchTimes = new HashMap<Integer, Long>();
	
	private HashMap<Integer, Integer> interactionCount = new HashMap<Integer, Integer>();
	
	/***************** END OF TRUST VARIABLES *****************/
	
	
	
	/********** MALICIOUS BEHAVIOUR VARIABLES **********/
	private boolean isMalicious = false;
	
	// used for both no forward and selective forward
	private boolean noForward = false;  
	private float percentDroppedMessages = 1.0f; // default: no fwd
	
	// modification of RREQ
	private boolean modifyRREQsource = false;
	private boolean modifyRREQdest = false;
	private boolean modifyRREQidreq = false;
	private boolean modifyRREQroute = false;
	
	// modification of RREP
	private boolean modifyRREPsource = false;
	private boolean modifyRREPdest = false;
	private boolean modifyRREPidreq = false;
	private boolean modifyRREProute = false;
	
	// modification or RData
	private boolean modifyRDataSource = false;
	private boolean modifyRDataDest = false;
	private boolean modifyRDataRoute = false;
	
	
	/********** END OF MALICIOUS BEHAVIOUR VARIABLES **********/
	
	public TrustedRoutingMWACAgent(MAS mas, Integer id, Float energy, Integer range) {
		super(mas, id, range);
		this.role = roleNOTHING;
		this.receivedMessageQueue = new FrameFIFOStack();
		this.neighboorlist = new TrustedRoutingMWACNeighboorList();
		this.messageToTransmitQueue = null;
		this.alreadyProcessedRouteRequestManager = null;
		this.networkPartialKnowledgeManager = null;
		this.pourcentOfAvailableEnergyWhenElectedRepresentative = 0;
	}
	
	
	/****************************
	 * TRUST MANAGEMENT METHODS *
	 ****************************/
	
	private static boolean matches(MWACMessage msg1, MWACMessage msg2){
		if(msg1.getType() != msg2.getType()) 
			return false;
		else
			switch(msg1.getType()){
			case MWACMessage.msgROUTED_DATA:
				return (msg1.getSender() == msg2.getSender()) && (msg1.getReceiver() == msg2.getReceiver());				
			case MWACMessage.msgROUTE_REQUEST:
				MWACMessage_RouteRequest tMsg1 = (MWACMessage_RouteRequest)msg1;
				MWACMessage_RouteRequest tMsg2 = (MWACMessage_RouteRequest)msg2;
				return (msg1.getSender() == msg2.getSender()) && (msg1.getReceiver() == msg2.getReceiver()
						&& (tMsg1.getIdRequest() == tMsg2.getIdRequest()));
			case MWACMessage.msgROUTE_REPLY:
				MWACMessage_RouteReply tMsg3 = (MWACMessage_RouteReply)msg1;
				MWACMessage_RouteReply tMsg4 = (MWACMessage_RouteReply)msg2;
				return (msg1.getSender() == msg2.getSender()) && (msg1.getReceiver() == msg2.getReceiver()
						&& (tMsg3.getIdRequest() == tMsg4.getIdRequest()));
			default: // not interested
				return false;
			}		
	}
	
	public void useTrust(){
		useTrust = true;
	}
	
	public void dontUseTrust(){
		useTrust = false;
	}
	
	/*
	 * to remove
	 */
	
	public void printNeighbourhood(){
		try {
			PrintWriter pw = new PrintWriter(new File("C:\\Users\\Anca\\Desktop\\Logs\\test" + getUserId() +" .html"));
			pw.println(neighboorlist.toHTML());
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	/*
	 * MISC
	 */
	
	
	public void printWatchList(){
		try {
			PrintWriter pw = new PrintWriter(new File("C:\\Users\\Anca\\Desktop\\Logs\\watch_" + getUserId() + ".html"));
			pw.println(watchList.toHTML());
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	/** 
	 * 
	 * @param id
	 * @param msg
	 * @param date
	 */
	private void addToWatchList(int id, MWACMessage msg, aDate date) {
		Integer ir = interactionCount.get(id);
		Long wt = watchTimes.get(id);

		if (ir == null) {
			ir = 0;
		}
		if (wt == null) {
			wt = WATCH_TIME_DEFAULT;
		}

		ir++;
		interactionCount.put(id, ir);
		watchTimes.put(id, wt);
		
		date.add(GregorianCalendar.MILLISECOND, wt.intValue());

		watchList.addEntry(new WatchEntry(id, ir, msg, date));
	}
	
	/************************************
	 * END OF TRUST MANANGEMENT METHODS *
	 ************************************/
	

	
	/*******************************
	 * MALICIOUS BEHAVIOUR METHODS *
	 *******************************/
	
	public void sendFabricatedData(int source, int destination, int receiver, String msg){
		isMalicious = true;
		sendDataFrame(receiver, new MWACMessage_Data(source, destination, msg));
	}
	
	public void sendFabricatedRoutedData(int source, int destination, int receiver, String routeStr){		
		isMalicious = true;
		String[] rStr = routeStr.split(" ");
	
		int[] route;
		if (routeStr.isEmpty())
			route = new int[0];
		else {
			route = new int[rStr.length];
			for (int i = 0; i < rStr.length; i++)
				route[i] = Integer.parseInt(rStr[i]);
		}
		
		MWACMessage_Data msg = new MWACMessage_Data(source, destination, "phony");
		sendFrame(new MWACFrame(getUserId(), receiver, new MWACMessage_RoutedData(msg,route)));
	}
	
	public void sendFabricatedRouteRequest(int source, int destination, int reqId, String pRouteStr){
		isMalicious = true;
		int[] route;
		if(pRouteStr.isEmpty())
			route = new int[0];
		else {
			String[] rStr = pRouteStr.split(" ");

			route = new int[rStr.length];
			for (int i = 0; i < rStr.length; i++)
				route[i] = Integer.parseInt(rStr[i]);
		}
		int r = (role == roleREPRESENTATIVE) ? MWACFrame.BROADCAST_LINK : MWACFrame.BROADCAST_REPRESENTATIVE;
		sendFrame(new MWACFrame(getUserId(), r, new MWACMessage_RouteRequest(source, destination,(short)reqId, route)));
	}
	
	public void sendFabricatedRouteReply(int source, int destination, int receiver, int reqId, String routeStr){
		isMalicious = true;
		
		int[] route;
		if (routeStr.isEmpty())
			route = new int[0];
		else {
			String[] rStr = routeStr.split(" ");

			route = new int[rStr.length];
			for (int i = 0; i < rStr.length; i++)
				route[i] = Integer.parseInt(rStr[i]);
		}
		sendFrame(new MWACFrame(getUserId(), receiver, new MWACMessage_RouteReply(source, destination,(short)reqId, route)));
	}
	

	private boolean drop(){
		return Math.random() < percentDroppedMessages; 
	}
	
	private int generateFakeIdentifier(){
		Random random = new Random();
		
		return -(10 + random.nextInt(90));
	}
	
	public void becomeNoFwd(float percentage){
		if(!isMalicious){
			isMalicious = true;
			useTrust = false;
		}		
		noForward = true;
		percentDroppedMessages = percentage;
	}

	public void becomeModifRREQ(int source, int dest, int idreq, int route){
		if(!isMalicious){
			isMalicious = true;
			useTrust = false;
			noForward = false;
		}
		
		if(source == 1) modifyRREQsource = true;
		if(dest == 1) modifyRREQdest = true;
		if(idreq == 1) modifyRREQidreq = true;
		if(route == 1) modifyRREQroute = true;
	}
	

	public void becomeModifRREP(int source, int dest, int idreq, int route){
		if(!isMalicious){
			isMalicious = true;
			useTrust = false;
			noForward = false;
		}
		
		if(source == 1) modifyRREPsource = true;
		if(dest == 1) modifyRREPdest = true;
		if(idreq == 1) modifyRREPidreq = true;
		if(route == 1) modifyRREProute = true;
	}
	
	public void becomeModifRData(int source, int dest, int route){
		if(!isMalicious){
			isMalicious = true;
			useTrust = false;
			noForward = false;
		}
		
		if(source == 1) modifyRDataSource = true;
		if(dest == 1) modifyRDataDest = true;
		if(route == 1) modifyRDataRoute = true;
	}
	
	/**************************************
	 * END OF MALICIOUS BEHAVIOUR METHODS *
	 **************************************/
	

	/** Launched by the call of start method */
	public void run() {
		System.out.println("Demarrage du TrustedRoutingMWACAgent " + getUserId());

		sleep(500);
		
		sendIntroduction();

		sleep(2 * SLEEP_TIME_SLOT);

		// Context of the interaction (messages process)
		InteractionContext interaction = new InteractionContext();

		// MWAC LAYER
		while (!isKilling() && !isStopping()) {
			// Pause
			sleep(SLEEP_TIME_SLOT + extraWaitTime(role));

			// Preparation of the others threads
			while (((isSuspending()) && (!isKilling() && !isStopping())))
				try {
					Thread.sleep(SLEEP_TIME_SLOT);
				} catch (Exception e) {
				}
			;

			// Attente de messages
			while (this.receivedMessageQueue.isEmpty()&& (!interaction.lostRepresentativeRole)	&& (!interaction.reinitializationOfTheRole)) {

				// Is it the time to leave its representative mandat?
				if (pourcentOfAvailableEnergy() < (pourcentOfAvailableEnergyWhenElectedRepresentative / 2.0))
					interaction.lostRepresentativeRole = true;

				sleep(SLEEP_TIME_SLOT);

			}

			// Process waiting messages (if we have received some messages)
			while ((interaction.frame = receivedMessageQueue.pop()) != null)
				if (!interaction.lostRepresentativeRole)
					processMessage(interaction);

			if (interaction.reinitializationOfTheRole) {
				this.neighboorlist = new TrustedRoutingMWACNeighboorList();
			}

			computeRoleDecision(interaction);

			if (interaction.roleConfict) {
				sendConflictResolution();
			} else if (interaction.mustSendWhoAreMyNeighoors)
				sendWhoAreMyNeighboors();
			else if (interaction.mustSendAPresentation)
				sendPresentation();

			interaction.init();

			sleep(SLEEP_TIME_SLOT);

		}

		// AGENT SPECIFIC TASK
		System.out.println("Fin du thread " + getUserId());
	}


	
	public void computeRoleDecision(InteractionContext interaction) {

		// No neighboors
		byte newRole = role;

		if (this.neighboorlist.isEmpty())
			newRole = roleNOTHING;
		else {
			switch (this.role) {
			case roleNOTHING:
			case roleSIMPLEMEMBER: {
				int n = neighboorlist.getNbRepresentative();
				if (n == 0)
					newRole = roleREPRESENTATIVE;
				else if (n == 1) {
					newRole = roleSIMPLEMEMBER;
					if (interaction.hasPreviouslyLostRepresentativeRole)
						newRole = role;
				} else
					newRole = roleLINK;
			}
				break;
			case roleLINK: {
				int n = neighboorlist.getNbRepresentative();
				if (n == 0)
					newRole = roleREPRESENTATIVE;
				else if (n == 1)
					newRole = roleSIMPLEMEMBER;
				else
					newRole = roleLINK;
			}
				break;
			case roleREPRESENTATIVE: 
				if (interaction.lostRepresentativeRole	|| interaction.hasPreviouslyLostRepresentativeRole) {
					newRole = roleNOTHING;
					interaction.roleConfict = false; // Il n'y a plus de conflit
				} else {
					if (neighboorlist.getNbRepresentative() > 0)
						interaction.roleConfict = true;
					newRole = roleREPRESENTATIVE;
				}		
				break;
			}
		}

		// Initialize data and
		if (role != newRole) {
			
			setRole(newRole);
			
			switch (newRole) {
			case roleREPRESENTATIVE:
				pourcentOfAvailableEnergyWhenElectedRepresentative = pourcentOfAvailableEnergy();
				alreadyProcessedRouteRequestManager = new MWACAlreadyProcessedRouteRequestManager();
				messageToTransmitQueue = new MWACMessageFIFOStack();
				networkPartialKnowledgeManager = new MWACNetworkPartialKnowledgeManager();
				interaction.mustSendWhoAreMyNeighoors = true;
				break;
			case roleLINK:
				pourcentOfAvailableEnergyWhenElectedRepresentative = 0;
				alreadyProcessedRouteRequestManager = new MWACAlreadyProcessedRouteRequestManager();
				networkPartialKnowledgeManager = null;
				messageToTransmitQueue = null;
				interaction.mustSendAPresentation = true;
				break;
			case roleNOTHING:
				pourcentOfAvailableEnergyWhenElectedRepresentative = 0;
				alreadyProcessedRouteRequestManager = null;
				networkPartialKnowledgeManager = null;
				messageToTransmitQueue = null;
				neighboorlist = new TrustedRoutingMWACNeighboorList();
				interaction.mustSendAPresentation = false;
				sendIntroduction();
				sleep(250); 								
				break;
			case roleSIMPLEMEMBER:
				alreadyProcessedRouteRequestManager = null;
				networkPartialKnowledgeManager = null;
				messageToTransmitQueue = null;
				interaction.mustSendAPresentation = true;
				break;
			}			
		}
	}
	
	private void handleIntroduction(MWACMessage_Introduction msg, InteractionContext interaction) {
		neighboorlist.put(msg.getSender(),roleNOTHING, groupNONE);
		interaction.mustSendWhoAreMyNeighoors = true;
	}
	
	private void handlePresentation(MWACMessage_Presentation msg, InteractionContext interaction){
		neighboorlist.put(msg.getSender(), msg.getRole(), msg.getClonedGroupArray());
		
		interaction.mustSendAPresentation = interaction.mustSendAPresentation
				|| (msg.getType() == MWACMessage.msgWHO_ARE_MY_NEIGHBOORS)
				|| neighboorlist.put(msg.getSender(), msg.getRole(), msg.getClonedGroupArray());

	}

	private void handleConflictResolution(MWACMessage_ConflictResolution msg, InteractionContext interaction) {

		neighboorlist.put(msg.getSender(), roleREPRESENTATIVE, msg.getSender());

		if (role == roleREPRESENTATIVE && !interaction.lostRepresentativeRole) {
			int scoreInMsg = msg.getScore();
			if ((scoreInMsg > score()) || ((scoreInMsg == score()) && getUserId() < msg.getSender())) {
				interaction.lostRepresentativeRole = true;
				interaction.mustSendAPresentation = true;			

			} else {
				neighboorlist.put(msg.getSender(), roleNOTHING, groupNONE);
				sendConflictResolution();
				interaction.mustSendAPresentation = false;
			}
		} else
			interaction.mustSendAPresentation = true;
	}
	
	/**
	 * 
	 * @param msg
	 * @param interaction
	 */
	private void handleData(MWACMessage_Data msg, InteractionContext interaction) {
		
		int sender, receiver, source, destination;
		
		sender = interaction.frame.getSender();
		receiver = interaction.frame.getReceiver();
		
		source = msg.getSender();
		destination = msg.getReceiver();
		
		if (receiver == getUserId()) {

			/** WITH TRUST **/
			if(useTrust){
				if (destination == this.getUserId()) {
					if (source != sender && neighboorlist.getRole(source) != roleVOID){
						System.out.println("1 AUTHORIZATION ERROR: " + source + " could have sent directly to " + destination);
						notifyEvent(new ProtocolBreakEvent(this.getSystemId()));
						neighboorlist.distrustCompletely(sender);
					}
					else
						receiveMessage(msg);
				} else {
					switch (role) {
					case roleSIMPLEMEMBER:
						System.out.println("2 AUTHORIZATION ERROR: Simple member " + receiver + " is not allowed to relay messages");
						this.notifyEvent(new ProtocolBreakEvent(this.getSystemId()));
						neighboorlist.distrustCompletely(sender);
						break;
					case roleLINK:
						System.out.println("3 AUTHORIZATION ERROR: Link " + receiver + " is not allowed to relay data messages");
						notifyEvent(new ProtocolBreakEvent(this.getSystemId()));
						neighboorlist.distrustCompletely(sender);
						break;
					case roleREPRESENTATIVE:
						if(sender != source){
							System.out.println("4 AUTHORIZATION ERROR: Simple/Link " + sender + " is not allowed to relay messages from other nodes (" +  source + ")");
							notifyEvent(new ProtocolBreakEvent(this.getSystemId()));
							neighboorlist.distrustCompletely(sender);
						}
						else
							sendMessage(msg);						
						break;
					}
				}
			/** WITHOUT TRUST **/
			}else{
				if(destination == getUserId())
					receiveMessage(msg);
				
				else if (role == roleREPRESENTATIVE){
					if(noForward){
						if(!drop())
							sendMessage(msg);
					}else
						sendMessage(msg);
				}
			}
		}
	}

	/**
	 * 
	 * @param msg
	 * @param interaction
	 */
	private void handleRouteRequest(MWACMessage_RouteRequest msg, InteractionContext interaction){

		int sender = interaction.frame.getSender();
		int receiver = interaction.frame.getReceiver(); 
		int source = msg.getSender();
		int destination = msg.getReceiver();
		
		if (role == roleREPRESENTATIVE) {

			if (useTrust) {
				if (neighboorlist.getRole(sender) == roleSIMPLEMEMBER) {
					System.out.println("1 AUTHORIZATION ERROR: Simple members are not allowed to send RREQ ");
					notifyEvent(new ProtocolBreakEvent(this.getSystemId()));
					neighboorlist.distrustCompletely(sender);
					return;
				}
				
				if(neighboorlist.getRole(source) != roleVOID && neighboorlist.getRole(source)!= roleREPRESENTATIVE){
					System.out.println("2 AUTHORIZATION ERROR: Only representatives issue RREQ");
					notifyEvent(new ProtocolBreakEvent(this.getSystemId()));
					neighboorlist.distrustCompletely(sender);
					return;
				}		
			}
			
			TrustedTripletIdRoleGroup triplet = neighboorlist.get(destination);
			
			if (triplet != null || destination == this.getUserId()) {
				
				if (!alreadyProcessedRouteRequestManager.isAlreadyProcessed(source, msg.getIdRequest())) {

					System.out.println("REPRESENTANT #" + this.getUserId()	+ " VA REPONDRE PAR UN ROUTE REPLY A LA RECHERCHE");

					int relais = neighboorlist.getLinkToRepresentant(MWACRouteAssistant.getLastId(msg.getRoute()));

					if (relais == MWACRouteAssistant.UNDEFINED_ID)
						relais = neighboorlist.getLinkToRepresentant(source);

					if (relais == TrustedRoutingMWACNeighboorList.UNDEFINED_ID)
						System.out.println("!!!!!! (1) Moi repr " + this.getUserId() + " ne trouve pas le précédent de "
										+ sender + " dans la route " + MWACRouteAssistant.routeToString(msg.getRoute())
										+ " (pour un ROUTE REPLY)");

					MWACMessage_RouteReply reply = new MWACMessage_RouteReply(getUserId(), source, msg.getIdRequest(), msg.getRoute());
					
					if (noForward) { // MALICIOUS BEHAVIOUR: NO/SEL FWD						
						if(!drop())
							sendFrame(new MWACFrame(getUserId(), relais, reply));
					}else
						sendFrame(new MWACFrame(getUserId(), relais, reply));;
					
					
					// MONITOR THE RELAY
					if(useTrust){
						addToWatchList(relais, reply, new aDate());
					}
				} else {
					// Already answer by sending a route reply
				}
			} else {
				if ((!alreadyProcessedRouteRequestManager.isAlreadyProcessed(source, msg.getIdRequest()))&& (source != getUserId())) { // On fait suivre
					
					msg.setRoute(MWACRouteAssistant.add(msg.getRoute(),getUserId()));
					
			
					int s = msg.getSender();
					int r = msg.getReceiver();
					int idR = msg.getIdRequest();
					int[] rt = msg.getRoute();

					// MALICIOUS BEHAVIOUR: MODIFICATION OF RREQ
					if(modifyRREQsource)
						s = generateFakeIdentifier();								
					if(modifyRREQdest)
						r = generateFakeIdentifier();
					if(modifyRREQidreq)
						idR = generateFakeIdentifier();
					if(modifyRREQroute){
						if(rt.length == 0)
							rt = new int[1];
						rt[rt.length - 1] = generateFakeIdentifier();						
					}
					
					msg = new MWACMessage_RouteRequest(s,r,(short)idR,rt);
					
					if(noForward){ // MALICIOUS BEHAVIOUR: NO/SEL FWD
						if(!drop())
							sendFrame(new MWACFrame(getUserId(), MWACFrame.BROADCAST_LINK, msg));
					}else
						sendFrame(new MWACFrame(getUserId(), MWACFrame.BROADCAST_LINK, msg));
					
					// MONITOR THE RELAY(S)
					if(useTrust){
						ArrayList<Integer> links = neighboorlist.getLinkIdentifiers();
						
						aDate expDate = new aDate();
						
						for(Integer l : links){
							addToWatchList(l, msg, expDate);
						}
					}

				} else {
					// Already processed
				}
			}
			
		} else if (role == roleLINK) {
			if ((!alreadyProcessedRouteRequestManager.isAlreadyProcessed(source, msg.getIdRequest())) && (msg.getSender() != getUserId())
					&& (receiver != MWACFrame.BROADCAST_REPRESENTATIVE)) {
				
				int s = msg.getSender();
				int r = msg.getReceiver();
				int idR = msg.getIdRequest();
				int[] rt = msg.getRoute();

				// MALICIOUS BEHAVIOUR: MODIFICATION OF RREQ
				if(modifyRREQsource)
					s = generateFakeIdentifier();								
				if(modifyRREQdest)
					r = generateFakeIdentifier();
				if(modifyRREQidreq)
					idR = generateFakeIdentifier();
				if(modifyRREQroute){
					if(rt.length == 0)
						rt = new int[1];
					rt[rt.length - 1] = generateFakeIdentifier();						
				}
				
				msg = new MWACMessage_RouteRequest(s,r,(short)idR,rt);
				
				if (noForward) { // MALICIOUS BEHAVIOUR: NO/SEL FORWARD
					if (!drop())
						sendFrame(new MWACFrame(getUserId(), MWACFrame.BROADCAST_REPRESENTATIVE, msg));
				} else
					sendFrame(new MWACFrame(getUserId(), MWACFrame.BROADCAST_REPRESENTATIVE, msg));
				
				// TODO ?? Monitoring may be inconclusive 
				// if(useTrust)
				// i can monitor REPRESENTATIVEs, but they may have responded to
				// the request before, which is why they don't forward my
				// message; therefore, i shouldn't decrease trust ??
				
			} else {
				// nothing
			}
			
			
		} else if(role == roleSIMPLEMEMBER)
			if(useTrust){
				System.out.println("3 AUTHORIZATION ERROR: Simple members are not supposed to receive RREQ ");
				notifyEvent(new ProtocolBreakEvent(this.getSystemId()));
				neighboorlist.distrustCompletely(sender);				
			}
		}
	


	
	/**
	 * 
	 * @param msg
	 * @param interaction
	 */
	private void handleRouteReply(MWACMessage_RouteReply msg, InteractionContext interaction) {
		
		int sender, receiver, source, destination;
		
		sender = interaction.frame.getSender();
		receiver = interaction.frame.getReceiver();
		
		source = msg.getSender();
		destination = msg.getReceiver();
		
		if (receiver == getUserId()) {		
			
			if (useTrust) {
				if (neighboorlist.getRole(sender) == roleSIMPLEMEMBER) {
					System.out.println("1 AUTHORIZATION ERROR: Simple members are not allowed to send RREP ");
					notifyEvent(new ProtocolBreakEvent(this.getSystemId()));
					neighboorlist.distrustCompletely(sender);
					return;
				}
				
				if(neighboorlist.getRole(source) != roleVOID && neighboorlist.getRole(source)!= roleREPRESENTATIVE){
					System.out.println("2 AUTHORIZATION ERROR: Only representatives issue RREP");
					notifyEvent(new ProtocolBreakEvent(this.getSystemId()));
					neighboorlist.distrustCompletely(sender);
					return;
				}
			}
			
			switch(role){
			case roleSIMPLEMEMBER:
				if(useTrust){
					System.out.println("3 AUTHORIZATION ERROR: Simple members are not supposed to receive RREP ");
					notifyEvent(new ProtocolBreakEvent(this.getSystemId()));
					neighboorlist.distrustCompletely(sender);
				}
				break;
			case roleLINK:
				int dest;
				if (neighboorlist.contains(destination)) {					
					dest = msg.getReceiver();
				} else if (MWACGroupAssistant.containsGroup(source, getGroups())) {
						dest = MWACRouteAssistant.getLastId(msg.getRoute());
				} else {
					dest = (MWACRouteAssistant.getPreviousId(msg.getRoute(),sender));
				}
				if (dest == MWACRouteAssistant.UNDEFINED_ID)
					System.out.println("!!!!!! Moi link " + getUserId() + " ne trouve pas le précédent de "	+ sender 
							+ " dans la route "	+ MWACRouteAssistant.routeToString(msg.getRoute()) + " (pour un ROUTE REPLY)");
				else {
					
					int s = msg.getSender();
					int r = msg.getReceiver();
					int idR = msg.getIdRequest();
					int[] rt = msg.getRoute();
					
					if(modifyRREPsource)
						s = generateFakeIdentifier();								
					if(modifyRREPdest)
						r = generateFakeIdentifier();
					if(modifyRREPidreq)
						idR = generateFakeIdentifier();
					if(modifyRREProute){
						if(rt.length == 0){
							rt = new int[1];
							rt[0] = generateFakeIdentifier();
						}
						int[] route = MWACRouteAssistant.cloneRoute(rt, rt.length + 1);
						for (int i = 0; i < rt.length; i++)
							route[i + 1] = rt[i];
						route[0] = generateFakeIdentifier();
						rt = route;
					}
					
					msg = new MWACMessage_RouteReply(s,r,(short)idR,rt);
					
					if (noForward) { // MALICIOUS BEHAVIOUR: NO/SEL FORWARD
						if (!drop())
							sendFrame(new MWACFrame(getUserId(), dest,	new MWACMessage_RouteReply(msg)));
					} else 
						sendFrame(new MWACFrame(getUserId(), dest,	new MWACMessage_RouteReply(msg)));
				
					
					if(useTrust)
						addToWatchList(dest, msg, new aDate());
				}
				break;
			case roleREPRESENTATIVE:
				if (destination == getUserId()) {

					int[] route = MWACRouteAssistant.cloneRoute(msg.getRoute(), 1 + msg.getRoute().length);
					
					route[route.length - 1] = msg.getSender();
					
					networkPartialKnowledgeManager.addRoute(route);
					
					int member = networkPartialKnowledgeManager.getRouteRequestAssociatedReceiver(msg.getIdRequest());
					
					networkPartialKnowledgeManager.addIdGroupAssociation(MWACRouteAssistant.getLastId(route), member);
										
					tryToProcessWaitingSendedMessage();					
				} else {					

					int relais = neighboorlist.getLinkToRepresentant(MWACRouteAssistant.getPreviousId(msg.getRoute(),getUserId()));

					if (relais == MWACRouteAssistant.UNDEFINED_ID)
						relais = neighboorlist.getLinkToRepresentant(msg.getReceiver());

					if (relais == TrustedRoutingMWACNeighboorList.UNDEFINED_ID)
						System.out.println("!!!!!! (2) Moi repr " + getUserId() + " ne trouve pas le précédent de "	+ sender
								+ " dans la route "	+ MWACRouteAssistant.routeToString(msg.getRoute()) + " (pour un ROUTE REPLY)");
					else {
										
						int s = msg.getSender();
						int r = msg.getReceiver();
						int idR = msg.getIdRequest();
						int[] rt = msg.getRoute();
						
						// MALICIOUS BEHAVIOUR: MODIFY ROUTE REPLY
						if(modifyRREPsource)
							s = generateFakeIdentifier();								
						if(modifyRREPdest)
							r = generateFakeIdentifier();
						if(modifyRREPidreq)
							idR = generateFakeIdentifier();
						if(modifyRREProute){
							if(rt.length == 0){
								rt = new int[1];
								rt[0] = generateFakeIdentifier();
							}
							int[] route = MWACRouteAssistant.cloneRoute(rt, rt.length + 1);
							for (int i = 0; i < rt.length; i++)
								route[i + 1] = rt[i];
							route[0] = generateFakeIdentifier();
							rt = route;
						}
						
						MWACMessage_RouteReply rrep = new MWACMessage_RouteReply(s,r,(short)idR,rt);
						
											
						if(noForward){	// MALICIOUS BEHAVIOUR: NO/SEL FORWARD
							if(!drop())
								sendFrame(new MWACFrame(getUserId(), relais, rrep));							
						}else
							sendFrame(new MWACFrame(getUserId(), relais, rrep));
						
										
						if(useTrust)
							addToWatchList(relais, rrep, new aDate());
						}
					}
				break;
			}
		}
	}
	
	/**
	 * 
	 * @param msg
	 * @param interaction
	 */
	private void handleRoutedData(MWACMessage_RoutedData msg, InteractionContext interaction){
		
		int sender = interaction.frame.getSender();
		int receiver = interaction.frame.getReceiver();
		int source = msg.getSender();
		int destination = msg.getReceiver();
		
		if(receiver == getUserId())	{					
			
			if (destination == getUserId()){
			
				/** WITH TRUST **/
				if(useTrust){
					if(role == roleSIMPLEMEMBER){
						System.out.println("1 AUTHORIZATION ERROR: Simple members (" + getUserId() + ") are not supposed to receive RData");
						notifyEvent(new ProtocolBreakEvent(this.getSystemId()));
						neighboorlist.distrustCompletely(sender);
					}
					else 
						receiveMessage(new MWACMessage_Data(source,destination,msg.getMsg()));
				/** WITHOUT TRUST **/
				}else
					receiveMessage(new MWACMessage_Data(source,destination,msg.getMsg()));
			}
			else 
			{
				switch(role){
				case roleSIMPLEMEMBER: 
					if(useTrust){
						System.out.println("2 AUTHORIZATION ERROR: Simple members (" + getUserId() + ") are not supposed to receive/relay RData");
						notifyEvent(new ProtocolBreakEvent(this.getSystemId()));
						neighboorlist.distrustCompletely(sender);
					}
					break;
				case roleLINK: 
					
					if(useTrust){
						if(neighboorlist.getRole(sender) != roleREPRESENTATIVE)	{
							System.out.println("3 AUTHORIZATION ERROR: Only representatives are allowed to send RData to links (" +  sender + " is not rep)");
							notifyEvent(new ProtocolBreakEvent(this.getSystemId()));
							neighboorlist.distrustCompletely(sender);
							break;
						}
						
					/*	if(neighboorlist.getRole(source) != roleVOID && neighboorlist.getRole(source) != roleREPRESENTATIVE){
							System.out.println("4 AUTHORIZATION ERROR: Simple/link agents are not supposed to issue RData ");
							neighboorlist.distrustCompletely(sender);
							break;
						}
					*/	
					}
					
					int dest;

					dest = MWACRouteAssistant.getFirstId(msg.getRoute());	
					
					if (dest == MWACNetworkPartialKnowledgeManager.UNDEFINED_ID)
						System.out.println("!!!!!! Moi link " + getUserId()	+ " ne trouve pas le suivant de " + sender 										
										+ " dans la route "	+ MWACRouteAssistant.routeToString(msg.getRoute()) + " (pour un ROUTED DATA)");
					else{
						
						int s = msg.getSender();
						int r = msg.getReceiver();
						int[] rt = msg.getRoute();
						
						if(modifyRDataSource)
							s = generateFakeIdentifier();
						if(modifyRDataDest)
							r = generateFakeIdentifier();
						if(modifyRDataRoute){
							if(rt.length == 0){
								rt = new int[1];
								rt[0] = generateFakeIdentifier();
							}
							int[] route = MWACRouteAssistant.cloneRoute(rt, rt.length + 1);
							for (int i = 0; i < rt.length; i++)
								route[i + 1] = rt[i];
							route[0] = generateFakeIdentifier();
							rt = route;
						}
						
						msg = new MWACMessage_RoutedData(new MWACMessage_Data(s,r,msg.getMsg()),rt);
						
						if (noForward) { // MALICIOUS BEHAVIOUR: NO/SEL FORWARD
							if(!drop())
								sendFrame(new MWACFrame(getUserId(),dest,msg)); //new MWACMessage_RoutedData(msg)));
						} else
							sendFrame(new MWACFrame(getUserId(),dest, msg)); //new MWACMessage_RoutedData(msg)));
						
						if(useTrust)
							addToWatchList(dest, msg, new aDate());
					}
					break;
					
				case roleREPRESENTATIVE: 
					if(useTrust){
						if(neighboorlist.getRole(sender) != roleLINK){
							System.out.println("5 AUTHORIZATION ERROR: Only links are allowed to relay RData to representatives ");
							notifyEvent(new ProtocolBreakEvent(this.getSystemId()));
							neighboorlist.distrustCompletely(sender);
							break;
						}
						
						if(neighboorlist.getRole(source) != roleVOID){
							System.out.println("6 AUTHORIZATION ERROR: Simple/link agents are not allowed to issue RData ");
							notifyEvent(new ProtocolBreakEvent(this.getSystemId()));
							neighboorlist.distrustCompletely(sender);
							break;
						}

					}
					
					if (neighboorlist.contains(destination))	{
						if(noForward){ // MALICIOUS BEHAVIOUR: NO/SEL FORWARD
							if(!drop())
								sendFrame(new MWACFrame(getUserId(),destination,new MWACMessage_Data(source,destination,msg.getMsg())));
						}else
							sendFrame(new MWACFrame(getUserId(),destination,new MWACMessage_Data(source,destination,msg.getMsg())));
					}
					else
					{
						int relais = neighboorlist.getLinkToRepresentant(MWACRouteAssistant.getNextId(msg.getRoute(),getUserId()));

						if (relais == TrustedRoutingMWACNeighboorList.UNDEFINED_ID) 
							System.out.println("!!!!!! (3) Moi repr " + getUserId() + " ne trouve pas le suivant de "
											+ sender	+ " dans la route "	+ MWACRouteAssistant.routeToString(msg.getRoute())	+ " (pour un ROUTED DATA)");
						else {
							msg.setRoute(MWACRouteAssistant.removeHead(msg.getRoute()));
							
							int s = msg.getSender();
							int r = msg.getReceiver();
							int[] rt = msg.getRoute();
							
							if(modifyRDataSource)
								s = generateFakeIdentifier();
							if(modifyRDataDest)
								r = generateFakeIdentifier();
							if(modifyRDataRoute){
								if(rt.length == 0){
									rt = new int[1];
									rt[0] = generateFakeIdentifier();
								}
								int[] route = MWACRouteAssistant.cloneRoute(rt, rt.length + 1);
								for (int i = 0; i < rt.length; i++)
									route[i + 1] = rt[i];
								route[0] = generateFakeIdentifier();
								rt = route;
							}
							
							msg = new MWACMessage_RoutedData(new MWACMessage_Data(s,r,msg.getMsg()),rt);
							
							if (noForward) { // MALICIOUS BEHAVIOUR: NO/SEL FORWARD
								if (!drop())
									sendFrame(new MWACFrame(getUserId(), relais, new MWACMessage_RoutedData(msg)));
							} else
									sendFrame(new MWACFrame(getUserId(),relais,new MWACMessage_RoutedData(msg)));
							
							if(useTrust){
								addToWatchList(relais, msg, new aDate());
							}
						}
					}
					break;
				}
			}
				
				
		
		}

	}
	
	public void processMessage(InteractionContext interaction) {

		MWACMessage msg = MWACMessage.createMessage(interaction.frame.getData());

		switch (msg.getType()) {
		
		case MWACMessage.msgINTRODUCTION:			
			handleIntroduction((MWACMessage_Introduction)msg,interaction);
			break;
		case MWACMessage.msgWHO_ARE_MY_NEIGHBOORS:
		case MWACMessage.msgPRESENTATION:
			handlePresentation((MWACMessage_Presentation)msg, interaction);
			break;
		case MWACMessage.msgCONFLICT_RESOLUTION:
			handleConflictResolution((MWACMessage_ConflictResolution)msg,interaction);
			break;
		case MWACMessage.msgDATA:
			handleData((MWACMessage_Data)msg, interaction);			
			break;
		case MWACMessage.msgROUTE_REQUEST:
			handleRouteRequest((MWACMessage_RouteRequest)msg, interaction);
			break;
		case MWACMessage.msgROUTE_REPLY:
			handleRouteReply((MWACMessage_RouteReply)msg, interaction);
			break;
		case MWACMessage.msgROUTED_DATA:
			handleRoutedData((MWACMessage_RoutedData)msg, interaction);
			break;
		default:
			System.out.println("ERROR!!!! NOT IMPLEMENTED MESSAGE "	+ msg.toString());
			break;
			
		}
	}
	

	
	/**
	 * send a data frame. Used by non representative agent to give the data to their representative agent
	 * @param dest receiver of the frame (next hop)
	 * @param msg the MWAC data message
	 */
	private void sendDataFrame(int dest,MWACMessage_Data msg){	
		super.sendFrame(new MWACFrame(getUserId(),dest,msg));
	}
	
	/**
	 * send a message (reserved to a representative member)
	 * @param msg the message to send
	 */
	private void sendMessage(MWACMessage_Data msg)
	{
		if(this.neighboorlist.contains(msg.getReceiver()))
			sendFrame(new MWACFrame(getUserId(),msg.getReceiver(),msg));
		else
		{
			short idRequest=this.messageToTransmitQueue.add(msg);
			this.networkPartialKnowledgeManager.addRouteRequestAndReceiverAssociation(idRequest, msg.getReceiver());
			MWACMessage_RouteRequest request = new MWACMessage_RouteRequest(getUserId(),msg.getReceiver(),idRequest);
			sendFrame(new MWACFrame(getUserId(),MWACFrame.BROADCAST_LINK,request));
			
			if (useTrust) {
				aDate expDate = new aDate();
				ArrayList<Integer> links = neighboorlist.getLinkIdentifiers();
				for (Integer l : links)
					addToWatchList(l, request, expDate);
			}
	}
	}
	/**
	 * send a message. This method can be called by all agents (REPRESENTATIVE,
	 * LINK, SIMPLE_MEMBER)
	 * 
	 * @param s
	 *            the string of the message
	 * @param receiver
	 *            the receiver of this message
	 */
	public void sendMessage(int receiver, String s) {

		MWACMessage_Data msg = new MWACMessage_Data(this.getUserId(), receiver,	s);

		if (this.neighboorlist.contains(receiver)) {
			System.out.println("\n(" + this.getUserId()	+ ") Le destinataire est un de mes voisins");
			super.notifyEvent(new SendedMessageEvent(this.getSystemId(), (Message) msg.clone()));
			sendDataFrame(receiver, msg);
		} else {
			System.out.println("\n(" + this.getUserId() + ")Le destinataire N'est PAS un de mes voisins");
			switch (this.role) {
			case roleNOTHING:
			case roleSIMPLEMEMBER:
			case roleLINK:
				ArrayList<Integer> lst = neighboorlist.getRepresentativeIdentifiers();
				if (lst == null)
					super.notifyEvent(new MessageNotTransmittedEvent(this.getSystemId(), msg,	"Representative neighboor not found"));
				else {
					super.notifyEvent(new SendedMessageEvent(this.getSystemId(), (Message) msg.clone()));
					sendDataFrame(lst.get(0), msg);
				}
				break;
			case roleREPRESENTATIVE:
				System.out.println("\nREPRESENTANT " + this.getUserId());
				super.notifyEvent(new SendedMessageEvent(this.getSystemId(), (Message) msg.clone()));
				sendMessage(msg);
				break;
			}
		}
	}
	
	/**
	 * send an introduction message
	 */
	private void sendIntroduction() {
		super.sendFrame(new MWACFrame(super.getUserId(), MWACFrame.BROADCAST, new MWACMessage_Introduction(this.getUserId())));
	}

	/**
	 * send a presentation message
	 */
	private void sendPresentation() {
		super.sendFrame(new MWACFrame(getUserId(),MWACFrame.BROADCAST,new MWACMessage_Presentation(getUserId(),this.role,this.getGroups())));
	}
	/**
	 * send a message to request the close neighboors
	 */
	private void sendWhoAreMyNeighboors() {
		super.sendFrame(new MWACFrame(getUserId(), MWACFrame.BROADCAST,
				new MWACMessage_WhoAreMyNeighboors(getUserId(), role, getGroups())));
	}
	
	/**
	 * send a conflict resolution message
	 * this message is send by a representative agent in conflict with one or more others representative agent
	 */
	private void sendConflictResolution() 
	{
		super.sendFrame(new MWACFrame(getUserId(),MWACFrame.BROADCAST,new MWACMessage_ConflictResolution(getUserId(),MWACMessage.BROADCAST,this.score())));
	}
	
	/** allows to an object to receive a message. Call by the simulator
	 * @param frame the received frame 
	 */
	public synchronized void receivedFrame(Frame frame)
	{
		super.receivedFrame(frame);
		
		TrustedTripletIdRoleGroup triplet = neighboorlist.get(frame.getSender());
		if(triplet != null){
			if(triplet.trust > TRUST_THRESHOLD){
				this.receivedFrame((MWACFrame) frame);		
			}
		}else
			this.receivedFrame((MWACFrame) frame);
				
	}

	/** allows to an object to receive a message
	 * @param frame the received frame
	 */
	public synchronized void receivedFrame(MWACFrame frame) {
		
		/************** PROMISCUOUS MODE WATCHING **************/
		
		if(useTrust){
			
			MWACMessage msg = MWACMessage.createMessage(frame.getData());
			
			if (msg.getType() == MWACMessage.msgROUTE_REPLY
					|| msg.getType() == MWACMessage.msgROUTE_REQUEST
					|| msg.getType() == MWACMessage.msgROUTED_DATA) {
			
				if(neighboorlist.get(frame.getSender()).trust < TRUST_THRESHOLD){
					return; // ignore frames from distrusted neighbours
				} else {
	
					int sender = frame.getSender();
	
					// watchlist filtered by sender
					ArrayList<WatchEntry> subset = watchList.getSubset(sender);
					
					aDate now = new aDate();
					
					WatchEntry toRemove = null;
					
					for(WatchEntry we : subset){
						if(now.differenceToMS(we.getExpireDate()) > 0){ // still watching
							if(matches(we.getMessage(), MWACMessage.createMessage(frame.getData()))){
								toRemove = we;
							}else{ 
								// no forwarded yet (there is still time)
							}						
						} else { // no forward (time has expired)						
							
							long crtWT = watchTimes.get(frame.getSender()); // curent waiting time 
							
							if(crtWT < WATCH_TIME_MAX){
								watchTimes.put(frame.getSender(), crtWT + WATCH_TIME_STEP);
								neighboorlist.decreaseTrustBy(sender, we.getInteractionNo() * decreaseTrustStep);							
							} else{
								neighboorlist.decreaseTrustBy(sender, RADICAL_TRUST_DECREASE);
							}
							toRemove = we;
						}
					}
					if(toRemove != null)
						watchList.removeEntry(toRemove);			
				}			
			}
		}
		
		if ((frame.getReceiver() == MWACFrame.BROADCAST) || (frame.getReceiver() == this.getUserId())
				|| ((frame.getReceiver() == MWACFrame.BROADCAST_LINK) && role == roleLINK)
				|| ((frame.getReceiver() == MWACFrame.BROADCAST_REPRESENTATIVE) && role == roleREPRESENTATIVE)){
			receivedMessageQueue.push(frame);
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
	/**
	 * If it is possible, send a message which is in a route waiting state
	 */
	public void tryToProcessWaitingSendedMessage() {
		MessageToSendItem item;
		ListIterator<MessageToSendItem> iter = messageToTransmitQueue.listIterator();
		int[] route;

		while (iter.hasNext()) {
			item = iter.next();
			route = networkPartialKnowledgeManager.getDataMessageRoute(item.msg.getReceiver());
			if (route != null) {
				System.out.println("Repr " + this.getUserId() + " J'envoie le message routé!!!");
				
				MWACMessage_RoutedData msgRData = new MWACMessage_RoutedData(item.msg, route);
				int link = neighboorlist.getLinkToRepresentant(MWACRouteAssistant.getFirstId(route));
				
				if(noForward){ // MALICIOUS BEHAVIOUR: NO/SEL FORWARD
					if(!drop())
						sendFrame(new MWACFrame(getUserId(), link, msgRData));
				}else
					sendFrame(new MWACFrame(getUserId(), link, msgRData));
				
				/** monitor fwd message */
				if(useTrust)
					addToWatchList(link, msgRData, new aDate());
				
				iter.remove();
			}
		}
	}

	/**
	 * return the group identifier of this agent (the first one for a link
	 * agent)
	 * 
	 * @return the group of the agent
	 */
	public int getGroup() {
		if (role == roleREPRESENTATIVE)
			return this.getUserId();
		else {
			ArrayList<Integer> repr = this.neighboorlist.getRepresentativeIdentifiers();
			int nbRepr = repr.size();
			if (nbRepr == 0) {
				switch (this.role) {
				case roleSIMPLEMEMBER:
				case roleLINK:
					System.out.println("<A" + this.getUserId()+ ","	+ roleToString(this.role)
									+ ">!!!!! ERREUR de cohérence entre le role et la liste de voisins");
				case roleNOTHING:
					// C'est normal pour eux
					return groupNONE;
				}
			} else if (nbRepr == 1) {
				switch (this.role) {
				case roleSIMPLEMEMBER:
					return repr.get(0);
				case roleLINK:
					System.out.println("<B"
									+ this.getUserId()
									+ ","
									+ roleToString(this.role)
									+ ">!!!!! ERREUR de cohérence entre le role et la liste de voisins");
				case roleNOTHING:
					return groupNONE;
				}
			} else {
				if (role == roleLINK)
					return repr.get(0);
				else {
					if (role != roleNOTHING)
						System.out.println("<C" + this.getUserId() + ","+ roleToString(this.role)
										+ ">!!!!! ERREUR de cohérence entre le role et la liste de voisins");
					return groupNONE;
				}
			}
		}
		System.out.println("<D" + this.getUserId() + "," + roleToString(this.role)
						+ ">!!!!! ERREUR de cohérence entre le role et la liste de voisins");
		return groupNONE;
	}

	/**
	 * return the groups identifiers of this agent (interesting for link agent)
	 * 
	 * @return the groups associated to the agent
	 */
	public int[] getGroups() {
		int[] res;

		if (role != roleLINK) {
			res = new int[1];
			res[0] = this.getGroup();
		} else {
			ArrayList<Integer> repr = this.neighboorlist.getRepresentativeIdentifiers();
			int i = 0;
			res = new int[repr.size()];
			ListIterator<Integer> iter = repr.listIterator();
			while (iter.hasNext())
				res[i++] = iter.next();
		}
		return res;
	}
	
	/**
	 * change the role (notify the event, change the color)
	 * @param role the new role of the agent
	 */
	private void setRole(byte role) {
		if (this.role != role) {
			this.role = role;
			super.notifyEvent(new RoleModificationEvent(getSystemId(), role));
			switch (role) {
			case roleNOTHING:
				super.setColor(colorNOTHING);
				break;
			case roleSIMPLEMEMBER:
				super.setColor(colorSIMPLEMEMBER);
				break;
			case roleLINK:
				super.setColor(colorLINK);
				break;
			case roleREPRESENTATIVE:
				super.setColor(colorREPRESENTATIVE);
				break;
			default:
				super.setColor(Color.BLACK);
			}
		}
	}
	
	public static String roleToString(byte role) {
		switch (role) {
		case TrustedRoutingMWACAgent.roleREPRESENTATIVE:
			return "ROLE_REPRESENTATIVE";
		case TrustedRoutingMWACAgent.roleLINK:
			return "ROLE_LINK";
		case TrustedRoutingMWACAgent.roleSIMPLEMEMBER:
			return "ROLE_SIMPLEMEMBER";
		case TrustedRoutingMWACAgent.roleNOTHING:
			return "ROLE_NOTHING";
		}
		return "ROLE_UNDEFINED";
	}
	
	private int score(){
		return (int)(this.pourcentOfAvailableEnergy()*this.pourcentOfAvailableEnergy()*this.neighboorlist.size());
	}
	
	/**
	 * extra waiting time depending of a specific role
	 * 
	 * @param role
	 *            the specified role
	 * @return the extra waiting time
	 */
	public int extraWaitTime(byte role) {
		if (this.role == roleSIMPLEMEMBER)
			return 3 * SLEEP_TIME_SLOT;
		return 0;
	}

}
