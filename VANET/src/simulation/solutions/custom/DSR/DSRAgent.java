package simulation.solutions.custom.DSR;
import java.util.LinkedList;
import java.util.ListIterator;

import simulation.entities.Agent;
import simulation.events.system.ReceivedMessageEvent;
import simulation.events.system.SendedMessageEvent;
import simulation.messages.Frame;
import simulation.messages.Message;
import simulation.messages.ObjectAbleToSendMessageInterface;
import simulation.multiagentSystem.MAS;
import simulation.solutions.custom.DSR.Messages.DSRFrame;
import simulation.solutions.custom.DSR.Messages.DSRMessage;
import simulation.solutions.custom.DSR.Messages.DSRMessage_Data;
import simulation.solutions.custom.DSR.Messages.DSRMessage_RouteReply;
import simulation.solutions.custom.DSR.Messages.DSRMessage_RouteRequest;
import simulation.solutions.custom.DSR.Route.DSRRouteAssistant;
import simulation.solutions.custom.DSR.Route.EmptyRouteException;
import simulation.solutions.custom.DSR.Route.NoNextRelayException;
import simulation.solutions.custom.DSR.Route.NoPreviousRelayException;
import simulation.solutions.custom.DSR.Route.UnknownIdentifierException;

/**
 * DSR agent
 * @author Jean-Paul Jamont
 */
public class DSRAgent extends Agent implements ObjectAbleToSendMessageInterface{

	/** identifier of the next route request */
	private short nextRequestIdentifier;
	/** a stack to store received frame (enable a delayed processing) */
	private FrameFIFOStack receivedFrameQueue;
	/** manager of already processed route request (all processed route request identifier are stored to prevent multiple route request responding*/
	private DSRAlreadyProcessedRouteRequestManager alreadyProcessedRouteRequestManager;
	/** message which wait to be sended (because there is no finded route between the sender and the receiver)*/
	private LinkedList<DSRMessage_Data> waitingMessageToSend; 

	/** number of maximum authorised hop */
	private byte NUMBER_OF_MAX_HOP_AUTHORISED = 16;

	/**
	 * parameterized constructor
	 * @param mas reference to the multiagent system
	 * @param id identifier of the agent
	 * @param energy energy level of the agent
	 * @param range transmission range of the agent
	 */
	public DSRAgent(MAS mas, Integer id, Float energy,  Integer range) {
		super(mas, id, range);
		this.nextRequestIdentifier=0;
		this.receivedFrameQueue=new FrameFIFOStack();
		this.waitingMessageToSend=new LinkedList<DSRMessage_Data>();
		this.alreadyProcessedRouteRequestManager=new DSRAlreadyProcessedRouteRequestManager();
	}

	/**
	 * Launched by the call of start method
	 */
	public void run()
	{
		DSRFrame frm;
		DSRMessage msg;

		System.out.println("Démarrage de l'entité "+this.getUserId()+" utilisant DSR");

		// wait a little amount of time to allow the construction of others agents 
		try{Thread.sleep(500);}catch(Exception e){}

		// the decision loop is processed while the simulator don't requiere to kill or stop this agent)
		while(!isKilling() && !isStopping())
		{
			// Pause
			try{Thread.sleep(SLEEP_TIME_SLOT);}catch(Exception e){};

			// Preparation of the others threads
			while(  ((isSuspending()) && (!isKilling() && !isStopping()))) try{Thread.sleep(SLEEP_TIME_SLOT);}catch(Exception e){};

			// is the agent has a message which wait to be processed
			if(!this.receivedFrameQueue.isEmpty())
			{
				// for each frame...
				while(!this.receivedFrameQueue.isEmpty())
				{
					// the received frame
					frm=this.receivedFrameQueue.pop();

					// the message contained in this received frame
					msg=DSRMessage.createMessage(frm.getData());

					// what's the type of received frame?
					switch(msg.getType())
					{
					// The received frame is a route request
					case DSRMessage.ROUTE_REQUEST:
					{
						// The message is specialized
						DSRMessage_RouteRequest tMsg = (DSRMessage_RouteRequest)msg;
						// Am i the searched host?
						if(msg.getReceiver()==this.getUserId()) 
						{
							// Is the route request already replied?
							if(!this.alreadyProcessedRouteRequestManager.isAlreadyProcessed(tMsg.getSender(),tMsg.getRequestIdentifier()))
							{
								// NO : Return a route reply
								int relay=0;
								try 
								{
									// Who is the next relay between me and the sender of the route request
									relay=DSRRouteAssistant.getLastRelay(tMsg.getRoute());
								}
								catch (EmptyRouteException e) 
								{
									// the sender is a neighboor
									relay=tMsg.getSender();
								}

								// send the route reply
								this.sendFrame(new DSRFrame(this.getUserId(),relay,new DSRMessage_RouteReply(tMsg)));
							}
							else
							{
								// YES: Already replied, ignore the route request
							}
						}
						else
						{
							// Have I already answer to a such research?
							if(!this.alreadyProcessedRouteRequestManager.isAlreadyProcessed(tMsg.getSender(),tMsg.getRequestIdentifier()) && (tMsg.getSender()!=this.getUserId()))
							{
								// NO : I transmit the route request to my all neighboors
								int[] route =DSRRouteAssistant.add(tMsg.getRoute(),this.getUserId());
								if(DSRMessage.TTL_VERSION_OF_DSR)
								{if(tMsg.getTTL()>0) this.sendFrame(new DSRFrame(this.getUserId(),DSRFrame.BROADCAST,new DSRMessage_RouteRequest(tMsg.getSender(),tMsg.getReceiver(),tMsg.getRequestIdentifier(),(byte)(tMsg.getTTL()-1),route)));}
								else
									this.sendFrame(new DSRFrame(this.getUserId(),DSRFrame.BROADCAST,new DSRMessage_RouteRequest(tMsg.getSender(),tMsg.getReceiver(),tMsg.getRequestIdentifier(),route)));
							}
							else
							{
								// YES: I do nothing
							}
						}
					}
					break;

					// The received frame is a route reply
					case DSRMessage.ROUTE_REPLY:
					{
						// The message is specialized
						DSRMessage_RouteReply tMsg = (DSRMessage_RouteReply)msg;

						// Am i the final receiver of the route reply (i.e. sender of the answered route request)?
						if(tMsg.getReceiver()==this.getUserId())
						{
							// YES : I send my message along this route
							int receiver=tMsg.getSender();
							DSRMessage_Data item;
							int[] route= tMsg.getRoute();
							ListIterator<DSRMessage_Data> iter = this.waitingMessageToSend.listIterator();
							while(iter.hasNext())
							{
								item=iter.next();
								if(item.getReceiver()==receiver)
								{
									item.setRoute(route);
									//this.notifyEvent(new SendedMessageEvent(this.getId(),(Message)item.clone()));
									this.sendFrame(new DSRFrame(this.getUserId(),frm.getSender(),item));
									iter.remove();
								}
							}
						}
						else if(DSRRouteAssistant.contains(tMsg.getRoute(),this.getUserId()))
						{
							// NO but I am a relay
							// I must transmit the message
							try {
								this.sendFrame(new DSRFrame(this.getUserId(),DSRRouteAssistant.previous(tMsg.getRoute(),this.getUserId()),tMsg));
							} catch (EmptyRouteException e) {
								e.printStackTrace();
							} catch (NoPreviousRelayException e) {
								this.sendFrame(new DSRFrame(this.getUserId(),tMsg.getReceiver(),tMsg));
							} catch (UnknownIdentifierException e) {
								e.printStackTrace();
							}
						}
						else
						{
							// I am not the final receiver and not a relay!!! 
							System.out.println("FORGOTTEN ROUTE REPLY");
						}
					}
					break;
					// The received frame is a transported data
					case DSRMessage.DATA:
					{
						// The message is specialized
						DSRMessage_Data tMsg = (DSRMessage_Data)msg;
						
						// Am i the receiver of the data?
						if(tMsg.getReceiver()==this.getUserId())
						{
							// YES
							System.out.println("RECU "+tMsg.getMessage());
							// Notify the well reception of the message
							this.notifyEvent(new ReceivedMessageEvent(this.getSystemId(),(DSRMessage_Data) tMsg.clone()));
						}
						else
						{
							// Am i a relay of the transported data? 
							if(DSRRouteAssistant.contains(tMsg.getRoute(),this.getUserId()))
							{
								// YES
								try {
									// I transmit the data to the next hop
									this.sendFrame(new DSRFrame(this.getUserId(),DSRRouteAssistant.next(tMsg.getRoute(),this.getUserId()),(DSRMessage_Data)tMsg.clone()));
								} catch (NoNextRelayException e) {
									this.sendFrame(new DSRFrame(this.getUserId(),tMsg.getReceiver(),(DSRMessage_Data)tMsg.clone()));
								} catch (UnknownIdentifierException e) {
									e.printStackTrace();
								} catch (EmptyRouteException e) {
									e.printStackTrace();
								}
							}
						}
					}
					break;
					default:
						System.out.println("Not understanted message ("+msg.getType()+")!");
					}
				}
			}

		}
	}

	/**
	 * send a message
	 * @param receiver identifier of the receiver
	 * @param message message to send (a string)
	 */
	public void sendMessage(int receiver, String message)
	{
		this.waitingMessageToSend.add(new DSRMessage_Data(this.getUserId(),receiver,new int[0],message));
		if(DSRMessage.TTL_VERSION_OF_DSR)
			this.sendFrame(new DSRFrame(this.getUserId(),DSRFrame.BROADCAST,new DSRMessage_RouteRequest(this.getUserId(),receiver,this.nextRequestIdentifier++,this.NUMBER_OF_MAX_HOP_AUTHORISED,new int[0])));
		else
			this.sendFrame(new DSRFrame(this.getUserId(),DSRFrame.BROADCAST,new DSRMessage_RouteRequest(this.getUserId(),receiver,this.nextRequestIdentifier++,new int[0])));
		// Est-ce qu'on connait une route vers le destinataire?
		// OUI : on envoit la frame
		// NON : on envoit un route request (si pas déjà en cours)
	}


	/** allows to an object to receive a message
	 * @param frame the received frame 
	 */
	public synchronized void receivedFrame(Frame frame)
	{
		super.receivedFrame(frame);
		this.receivedFrame((DSRFrame) frame);
	}

	/** allows to an object to receive a message
	 * @param frame the received DSR_Frame 
	 */
	public synchronized void receivedFrame(DSRFrame frame)
	{
		if( (frame.getReceiver()==DSRFrame.BROADCAST) || (frame.getReceiver()==this.getUserId()) ) 
			this.receivedFrameQueue.push(frame);
	}

	/**
	 * 
	 */
	public String toSpyWindows()
	{

		String res= "<HTML>";

		res+="Agent #"+this.getUserId()+"<BR>Num prochaine requete:"+this.nextRequestIdentifier+"<BR>";
		res+="<B>Received frames queue</B>:<BR>"+receivedFrameQueue.toString()+"<BR><BR>";

		res+="<B>Already processed frame</B>:<BR>"+this.alreadyProcessedRouteRequestManager.toHTML();

		res+="<B>Waiting messages to send</B> ("+this.waitingMessageToSend.size()+")<BR>";
		res+="<TABLE border=1>";
		res+="<TR><TD>Message</TD></TR>";
		ListIterator<DSRMessage_Data> iter = this.waitingMessageToSend.listIterator();
		while(iter.hasNext()) res+="<TR><TD>"+iter.next().toString()+"</TD></TR>";
		res+="</TABLE>";

		return res+"</HTML>";
	}

}


