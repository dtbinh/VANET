package simulation.solutions.custom.DSDV;

import simulation.entities.Agent;
import simulation.events.system.MessageNotTransmittedEvent;
import simulation.events.system.ReceivedMessageEvent;
import simulation.events.system.SendedMessageEvent;
import simulation.messages.*;
import simulation.multiagentSystem.MAS;
import simulation.solutions.custom.DSDV.Messages.DSDV_Frame;
import simulation.solutions.custom.DSDV.Messages.DSDV_Message;
import simulation.solutions.custom.DSDV.Messages.DSDV_Message_Acknowledgement;
import simulation.solutions.custom.DSDV.Messages.DSDV_Message_Data;
import simulation.solutions.custom.DSDV.Messages.DSDV_Message_Introduction;
import simulation.solutions.custom.DSDV.Messages.DSDV_Message_Update;


/**
 * l'agent DSDV
 * @author Yacine LATTAB & Ouerdia SLAOUTI
 */
public class DSDVAgent extends Agent implements ObjectAbleToSendMessageInterface{

	// liste des trames non encore traitée
	private FrameFIFOStack receivedFrameQueue;

	// la table de routage de l'agent DSDV
	private Routing_table table;	

	//le Manager de l'agent DSDV
	private DSDV_Manager Manager;

	/**
	 * constructeur pour l'initialisation des attribut de l'agent DSDV
	 * @param mas multi agent system
	 * @param id l'identité de l'agent
	 * @param energy le niveau d'energie initial de l'agent
	 * @param range la portée de transmission de l'agent
	 */
	public DSDVAgent(MAS mas, Integer id, Float energy, Integer range) {

		super(mas, id, range);

		this.table=new Routing_table();
		this.Manager = new DSDV_Manager(id);	
		this.receivedFrameQueue=new FrameFIFOStack();
	}

	/**
	 * Launched by the call of start method
	 */
	public void run()
	{
		DSDV_Frame frame;
		DSDV_Message msg;

		System.out.println("Démarrage de l'entité "+this.getUserId()+" utilisant DSDV");

		// wait a little amount of time to allow the construction of others agents 
		try{Thread.sleep(500);}catch(Exception e){}

		//envoi d'un message d'introduction (ce message est 
		this.sendFrame(new DSDV_Frame(this.getUserId(), DSDV_Frame.BROADCAST, new DSDV_Message_Introduction(this.getUserId(), DSDV_Message.BROADCAST, this.Manager.get_SN())));


		// the decision loop is processed while the simulator don't require to kill or stop this agent)
		while(!isKilling() && !isStopping())
		{		
			// Pause
			try{Thread.sleep(SLEEP_TIME_SLOT);}catch(Exception e){};

			// Preparation of the others threads
			while(((isSuspending()) && (!isKilling() && !isStopping()))) try{Thread.sleep(SLEEP_TIME_SLOT);}catch(Exception e){};

			
			//si mise à jour est à vrai, diffusion d'une nouvelle mise à jour
			if (this.Manager.RoundOfTableUpdate == true) {
				
				//si mon niveau d'energie atteint un certain seuil, positionner
				if( this.getBattery().getActualAmountOfEngergy() <= this.Manager.EnergyMin)	this.Manager.Stop_Chrono();
				
				//remettre RoundOfTableUpdate à faux, pour qu'il n'y aura pas d'envoie de mise à jour jusqu'au prochain tour
				this.Manager.RoundOfTableUpdate = false;
				
				//mettre à jour son entrée de sa table de routage et diffuser cette entrée
				this.sendFrame(new DSDV_Frame(this.getUserId(), DSDV_Frame.BROADCAST, new DSDV_Message_Update(this.getUserId(), DSDV_Message.BROADCAST, new Entry_routing_table(this.getUserId(), 0, this.getUserId(), this.Manager.get_SN()))));
			}


			// is the agent has a message which wait to be processed
			if(!this.receivedFrameQueue.isEmpty())
			{
				// for each frame...
				while(!this.receivedFrameQueue.isEmpty())
				{
					// the received frame
					frame=this.receivedFrameQueue.pop();

					// the message contained in this received frame
					msg=DSDV_Message.createMessage(frame.getData());

					// what's the type of received message?
					switch(msg.getType()){


					//the received frame is an Introduction message
					case DSDV_Message.msgINTRODUCTION:
					{
						// The message is specialized
						DSDV_Message_Introduction tMsg = (DSDV_Message_Introduction)msg;

						//try to update my routing table
						this.table.Update(tMsg);

						//if updating my routing table, I send an updating message	
						if (this.table.get_last_Entry()!=null) 
							this.sendFrame(new DSDV_Frame(this.getUserId(),DSDV_Frame.BROADCAST,new DSDV_Message_Update(this.getUserId(), DSDV_Message_Update.BROADCAST, this.table.get_last_Entry())));
					}break;



					// The received frame is an Update Message
					case DSDV_Message.msgUPDATE:
					{
						// The message is specialized
						DSDV_Message_Update tMsg = (DSDV_Message_Update)msg;

						//try to update my routing table
						this.table.Update(tMsg);

						//if updating my routing table, I send an updating message	
						if (this.table.get_last_Entry()!=null) 
							this.sendFrame(new DSDV_Frame(this.getUserId(),DSDV_Frame.BROADCAST,new DSDV_Message_Update(this.getUserId(), DSDV_Message_Update.BROADCAST, this.table.get_last_Entry())));
					
					}break;


					// The received frame is the DATA
					case DSDV_Message.msgDATA:
					{
						//Am i concerned by the data?
						if(frame.getReceiver()==this.getUserId())
						{		
							// The message is specialized
							DSDV_Message_Data tMsg = (DSDV_Message_Data)msg;
							
							// Am i the receiver of the data?
							if(tMsg.getReceiver()==this.getUserId())
							{
								// Notify the well reception of the message
								this.notifyEvent(new ReceivedMessageEvent(this.getSystemId(),(DSDV_Message_Data) tMsg.clone()));

								// send an acknowledgment message
								this.sendFrame(new DSDV_Frame(this.getUserId(),table.get_Next_relay(tMsg.getSender()), new DSDV_Message_Acknowledgement (tMsg)));
							}
							else
							{
								// I am a relay, i send this message								
								this.sendFrame(new DSDV_Frame(this.getUserId(),table.get_Next_relay(tMsg.getReceiver()),tMsg.toByteSequence()));
							}
						}	
					}
					break;


					// The received frame is an ACKNOWLEDGEMENT
					case DSDV_Message.msgACKNOWLEDGEMENT:
					{
						//Am i concerned by the ACKNOWLEDGEMENT?
						if(frame.getReceiver()==this.getUserId())
						{
							// The message is specialized
							DSDV_Message_Acknowledgement tMsg = (DSDV_Message_Acknowledgement)msg;
							
							// if i am not the receiver of the acknowlegdement, I relay it 
							if(tMsg.getReceiver()!=this.getUserId())
								this.sendFrame(new DSDV_Frame(this.getUserId(),table.get_Next_relay(tMsg.getReceiver()),tMsg.toByteSequence()));
						}	
					}
					break;
					default: System.out.println("Not understanted message ("+msg.getType()+")!");
					}
				}
			}
		}
	}

	/**
	 * envoyer un message
	 * @param message message à envoyer (a DSDV_Message)
	 */
	public void sendMessage(int receiver, String s)
	{
		//si le destinataire figure dans la table de routage de l'emetteur et son sequence number est pair
		if ((table.search(receiver)!= table.NotExist) && (table.clone().get(table.search(receiver)).get_SequenceNumber() % 2 ==0))
		{				
			// Notify the sending of the message containing data 
			this.notifyEvent(new SendedMessageEvent(this.getSystemId(),new DSDV_Message_Data(this.getUserId(),receiver,s)));

			// I send the message embedded in the DSDV_Frame
			this.sendFrame(new DSDV_Frame(this.getUserId(),table.get_Next_relay(receiver),new DSDV_Message_Data(this.getUserId(),receiver,s)));		
		}
		//sinon, le destinataire ne figure pas dans ma table de routage ou son numero de sequence est impair
		else {
			this.notifyEvent(new MessageNotTransmittedEvent(this.getSystemId(), new DSDV_Message_Data(this.getUserId(),receiver,s), "No Path Found"));
		}
	}

	/** allows to an object to receive a message
	 * @param frame the received frame 
	 */
	public synchronized void receivedFrame(Frame frame)
	{
		super.receivedFrame(frame);
		this.receivedFrame((DSDV_Frame) frame);
	}

	/** allows to an object to receive a message
	 * @param frame the received DSDV_Frame 
	 */
	public synchronized void receivedFrame(DSDV_Frame frame)
	{
		if( (frame.getReceiver()==frame.BROADCAST) || (frame.getReceiver()==this.getUserId()) ) 
			this.receivedFrameQueue.push(frame);
	}
	/**
	 * permet l'affichage des attributs de l'agent 
	 */
	public String toSpyWindows()
	{

		String res= "<HTML>";

		res+="Agent #"+this.getUserId()+" SN "+ this.Manager.get_SN()+" Energy "+(((int)(this.getBattery().getActualAmountOfEngergy()*100))/100)+" Range "+this.getRange()+"<BR>"+"<BR>";
		res+="<B>Table de routage</B><BR>";

		res+="<TABLE border=1>";
		res+="<TR><TD><B>Destination</B></TD><TD><B>Nombre de sauts</B></TD><TD><B>Prochain noeud</B></TD><TD><B>Sequence Number</B></TD></TR>"; 
		for (int i=0;i<this.table.clone().size();i++)
			res+="<TR><TD>"+this.table.clone().get(i).get_Destination()+"</TD><TD>"+this.table.clone().get(i).get_Hops()+"</TD><TD>"+this.table.clone().get(i).get_Next()+"</TD><TD># "+this.table.clone().get(i).get_SequenceNumber()+"</TD></TR>";
		res+="</TABLE>";

		return res+"</HTML>";

	}

}



