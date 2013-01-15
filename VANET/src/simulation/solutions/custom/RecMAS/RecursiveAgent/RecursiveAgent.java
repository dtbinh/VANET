
/**
 * VERIFIER que les non elementary agent d'une groupe n'emagazine pas indefiniment les messages recu( cf runApplicativeAgentQuantum)
 * 
 * PROBLEME => Il faut quand on entend un REQUEST ACCEPT creer notre groupe d'abstraction
 *             Il faut revoir la màj des aggregatedAgents
 */

package simulation.solutions.custom.RecMAS.RecursiveAgent;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;
import java.util.Vector;

import simulation.battery.BatteryModel;
import simulation.entities.Object;
import simulation.events.system.RecMAS_AbstractionCreationEvent;
import simulation.events.system.RecMAS_AbstractionDecompositionEvent;
import simulation.events.system.RecMAS_AbstractionUpdateEvent;
import simulation.events.system.ReceivedFrameEvent;
import simulation.messages.Frame;
import simulation.multiagentSystem.MAS;
import simulation.solutions.custom.MWAC.MWACRouteAssistant;
import simulation.solutions.custom.RecMAS.MWAC.MWACAgent;
import simulation.solutions.custom.RecMAS.MWAC.TripletIdRoleGroup;
import simulation.solutions.custom.RecMAS.MWAC.Messages.MWACFrame;
import simulation.solutions.custom.RecMAS.MWAC.Messages.MWACMessage;
import simulation.solutions.custom.RecMAS.MWAC.Messages.MWACMessage_Presentation;
import simulation.solutions.custom.RecMAS.RecursiveAgent.Messages.*;
import simulation.utils.ImageToolkit;
import simulation.utils.Log;
import simulation.utils.aDate;
import simulation.views.InexistantLevelGraphicalViewException;
import simulation.views.entity.basic.BasicView;





public class RecursiveAgent extends MWACAgent implements Runnable,simulation.views.entity.RecursiveEntityViewInterface{

	private static String LOG_FILE_NAME = "D:\\RECMAS.HTML";
	private static int STOP_LOG = 50;
	public   int debugNB_OF_SLEEP = 0;
	public final static long TIME_TO_ANSWER_COMPOSE_JOIN = 1000;
	public final static long MAX_TIME_BEFORE_RELAUNCH_COMPOSE_JOIN = 5000;
	public static int MAX_NB_OF_LEVEL = 8;

	public final static int MAX_LABEL = 100;


	private RecMASAlreadyProcessedLabelManager labelizedMessageManager;
	private ReceivedRecMASMessageFIFOStack receivedRecMASMessages;

	public long joinRequested =-1;

	//private Timer periodicalComputationExecutionTimer;

	private RecursiveAgent previousLevelAgent;
	private RecursiveAgent nextLevelAgent;

	private RecursiveState recursiveState;

	private AggregatedAgentListManager aggregatedAgentList = null;



	public RecMASAlreadyProcessedLabelManager getLabelizedMessageManager()
	{
		if(this.labelizedMessageManager==null) 
			return this.previousLevelAgent.getLabelizedMessageManager();
		else
			return this.labelizedMessageManager;
	}


	public RecursiveAgent(MAS mas,Integer  id, Float energy,Integer  range)
	{
		super(mas,id,energy,range);

		this.previousLevelAgent=null;
		this.nextLevelAgent=null;
		this.aggregatedAgentList=null;
		this.recursiveState=new RecursiveState();
		this.labelizedMessageManager=new  RecMASAlreadyProcessedLabelManager(10000);
		this.receivedRecMASMessages=new ReceivedRecMASMessageFIFOStack();

		Log.open(LOG_FILE_NAME,true);
		
	}

	/** agent abstrait */
	public RecursiveAgent(Integer id, RecursiveAgent previousLevelAgent )
	{
		super(null,
				id,
				(previousLevelAgent.getApplicativeAgent().getBattery()!=null ? (float)previousLevelAgent.getApplicativeAgent().getBattery().getActualAmountOfEngergy() : 1.0f),
				previousLevelAgent.getApplicativeAgent().getRange());

		this.previousLevelAgent=previousLevelAgent;
		this.receivedRecMASMessages=new ReceivedRecMASMessageFIFOStack();
		this.nextLevelAgent=null;
		this.recursiveState=new RecursiveState();
		this.aggregatedAgentList=null;
		Log.println(this.idsToString()+" Creation de l'abstraction suite a une operation COMPOSE "+id+" de niveau "+(1+this.previousLevelAgent.getLevel()));
	}


	public int getElementaryId()
	{
		if(this.previousLevelAgent==null)
			return this.getUserId();
		else
			return this.previousLevelAgent.getElementaryId();
	}

	

	public void decompose()
	{
		this.decompose(false);
	}


	public void decompose(boolean myOwnRequest)
	{
		if(this.nextLevelAgent!=null)
		{
			if(this.nextLevelAgent.getUserId()==this.getUserId())
			{
				this.nextLevelAgent.decompose(false);
			}
			else
			{
				if(myOwnRequest) this.sendData(new RecMASMessage_DecomposeInform(this.getUserId(),this.getLevel()));
				this.nextLevelAgent=null;
				this.aggregatedAgentList=null;
			}
		}
		
		this.nextLevelAgent=null;
		this.MAX_NB_OF_LEVEL=1;
	}

	public void run()
	{
		if(this.getLevel()==0)
		{
			Log.println("Démarrage de l'agent recurssif "+this.getSystemId()+" de niveau "+this.getLevel(),this.getLevel());
			try{Thread.sleep(1000);}catch(Exception e){}


			Log.println("Démarrage de l'agent applicatif",this.getLevel());
			//this.periodicalComputationExecutionTimer.schedule(new PeriodicalProcessExecution(),0, PERIODICAL_COMPUTATION_EXECUTION_TIMER_INTERVAL);
			super.run();

		}
		else
		{
			Log.println("Démarrage de l'agent recurssif "+this.getSystemId()+" de niveau "+this.getLevel(),this.getLevel());
		}


	}

	public  void receivedSystemMessage(RecMASMessage msg)
	{

		switch(msg.getType())
		{
		case  RecMASMessage.msgDECOMPOSE_REQUEST:
		{
			Log.println("DECOMPOSE_REQUEST recu par "+this.idsToString(this.getLevel()),this.getLevel());

			RecursiveAgent myAg = this.getRecurssiveAgent(msg.getLayer());
			RecMASMessage_DecomposeRequest tMsg = (RecMASMessage_DecomposeRequest) msg;
			if(tMsg.getSender()==myAg.getUserId())
			{
				System.err.println("\n"+this.idsToString()+" se décompose suite à une requête "+tMsg);
				myAg.previousLevelAgent.decompose();
			}
		}
		break;
		case  RecMASMessage.msgCOMPOSE_REQUEST:
		{
			RecursiveAgent myAg = this.getRecurssiveAgent(msg.getLayer());

			Log.println("COMPOSE_REQUEST recu par "+this.idsToString(this.getLevel()),this.getLevel());

			if(myAg.recursiveState.getStability() && myAg.role!=MWACAgent.roleREPRESENTATIVE && myAg.nextLevelAgent==null)
			{
				Log.println(myAg.idsToString(myAg.getLevel())+" acception la composition avec "+msg.getSender(),this.getLevel());
				RecMASMessage_ComposeAccept reply=new RecMASMessage_ComposeAccept(myAg.getUserId(),myAg.getLevel(),msg.getSender());
				myAg.sendData(reply);
				//MICHEL  Log.println(reply,myAg.getLevel());
				myAg.nextLevelAgent=new RecursiveAgent(msg.getSender(),myAg);
				myAg.aggregatedAgentList=new AggregatedAgentListManager(msg.getSender()); 
				Log.println(myAg.idsToString(myAg.getLevel())+" créer l'agent ABSTRAIT "+myAg.nextLevelAgent.toString(),myAg.getLevel());

				this.recursiveState.signalNeighboorModification();
			}
		}
		break;
		case RecMASMessage.msgCOMPOSE_UPDATE: 
		{
			RecursiveAgent myAg = this.getRecurssiveAgent(msg.getLayer());
			if(myAg.previousLevelAgent!=null && this.aggregatedAgentList!=null) 
			{
				if(msg.getSender()==myAg.getUserId())
				{
					Log.println(this.idsToString(myAg.getLevel())+" Traite un COMPOSE_UPDATE   "+msg,myAg.getLevel());
					if(myAg.aggregatedAgentList.process((RecMASMessage_ComposeUpdate)msg)) this.recursiveState.signalNeighboorModification();
				}
				else
				{
					if(MWACRouteAssistant.contains(((RecMASMessage_ComposeUpdate)msg).getAggregatedAgents(),this.getUserId()))
							{
							System.err.println("\n"+this.idsToString()+" Je suis considéré dans un groupe composé par erreur !!! "+msg);
							}
				}

			}
//			else if (myAg.previousLevelAgent==null && this.aggregatedAgentList==null && this.getLevel()==msg.getLayer())
//			{
//				Log.println(this.idsToString(myAg.getLevel())+" Traite un update*   "+msg,myAg.getLevel());
//				this.nextLevelAgent=new RecursiveAgent(msg.getSender(),myAg);
//				this.aggregatedAgentList=new AggregatedAgentListManager();
//				this.aggregatedAgentList.process((RecMASMessage_ComposeUpdate)msg);
//				Log.println(myAg.idsToString(myAg.getLevel())+" créer l'agent ABSTRAIT "+myAg.nextLevelAgent.toString(),myAg.getLevel());
//			}
			else
				;//MICHEL  Log.println(myAg.idsToString(myAg.getLevel())+" ("+(myAg==null)+","+(this.aggregatedAgentList==null)+","+this.getLevel()+""+(this.getLevel()==msg.getLayer()-1)+""+msg.getLayer()+") RECOIT UN UPDATE MAIS N'A PAS IMPLANTER LE NIVEAU "+msg.getLayer()+"! "+msg,myAg.getLevel());
		}
		break;
		case RecMASMessage.msgCOMPOSE_ACCEPT: 
		{
			RecursiveAgent myAg = this.getRecurssiveAgent(msg.getLayer());

			if( myAg.getRole()==MWACAgent.roleREPRESENTATIVE && ((RecMASMessage_ComposeAccept) msg).getSuperiorId()==myAg.getUserId())
			{
				Log.println(this.idsToString(this.getLevel())+"   traite un COMPOSE_ACCEPT "+msg,this.getLevel());
				try{


					if(myAg.aggregatedAgentList.add(msg.getSender())) this.recursiveState.signalNeighboorModification();
				}
				catch(NullPointerException e)
				{

					System.err.println("\n"+this.idsToString()+" traite "+msg);
					e.printStackTrace();
				}
			}
		}
		break;
		case RecMASMessage.msgCOMPOSE_JOIN:
		{
			RecursiveAgent myAg = this.getRecurssiveAgent(msg.getLayer());

			if(myAg.role==MWACAgent.roleREPRESENTATIVE) 
			{
				Log.println("COMPOSE_JOIN provenant de "+myAg.idsToString(this.getLevel())+" recu par représentant (id="+myAg.getUserId()+",level="+myAg.getLevel()+") previous JOIN state="+myAg.joinRequested+" ecart="+(aDate.getCurrentTimeInMS()-myAg.joinRequested),myAg.getLevel());
				if(!(myAg.joinRequested>0 && aDate.getCurrentTimeInMS()-myAg.joinRequested>RecursiveAgent.TIME_TO_ANSWER_COMPOSE_JOIN)) myAg.joinRequested=aDate.getCurrentTimeInMS();
			}
		}
		break;
		case RecMASMessage.msgAPPLICATIVE_MESSAGE:
		{
			RecMASMessage_msgApplicativeMessage tMsg = (RecMASMessage_msgApplicativeMessage) msg;
			//Log.println("\n(id="+this.getUserId()+",level="+this.getLevel()+") Réception d'un message de type APPLICATIF non implanté pour la couche "+this.getLevel()+"   "+msg.toString());
			int lvl=tMsg.getLayer();
			if(this.getAbstractedAgentIdentifier(lvl)==this.getElementaryId()) 
			{
				if(tMsg.getReceiver()==this.getElementaryId() || (tMsg.getReceiver()==RecMASMessage.BROADCAST ))
				{
					//MICHEL  Log.println("");
					Log.println(this.idsToString(this.getLevel())+" TRAITEMENT du message APPLICATIF recu "+tMsg.getData(),this.getLevel());
					this.receivedFrame(tMsg.getDataUnderFrameView());
					//MICHEL  Log.println(this.idsToString(this.getLevel())+" FIN TRAITEMENT du message APPLICATIF recu "+tMsg.getData(),this.getLevel());
				}
				else
				{
					//MICHEL  Log.println("");
					//MICHEL  Log.println(this.idsToString(this.getLevel())+" JE NE SUIS PAS LE DESTINATAIRE du message APPLICATIF"+tMsg.getData(),this.getLevel());
				}

			}

		}
		break;


		case RecMASMessage.msgINTERN_SYSTEM_MESSAGE_TRANSPORT:
		{
			RecMASMessage_msgInternSystemMessageTransport tMsg= (RecMASMessage_msgInternSystemMessageTransport) msg;

			if(tMsg.getLabel()>RecursiveAgent.STOP_LOG) Log.close();

			RecursiveAgent myAg = this.getRecurssiveAgent(tMsg.getLayer());




			if(msg.getSender()==this.getElementaryId())
				;//MICHEL  Log.println(this.idsToString()+"  ******* i0: JE SUIS L'EMETTEUR DE CETTE TRAME!!! "+tMsg);
			else
			{
				if(myAg==null) 
				{
					if(tMsg.getType()==RecMASMessage.msgCOMPOSE_UPDATE && MWACRouteAssistant.contains( ((RecMASMessage_ComposeUpdate) tMsg.getData() ).getAggregatedAgents(),this.getUserId()))
					{
						//MICHEL  Log.println(this.idsToString(this.getLevel())+" Je recois un message INTERNE contenant un UPDATE me concernant:  je construis l'abstraction nécéssaire");
						this.nextLevelAgent=new RecursiveAgent(msg.getSender(),myAg);
						this.aggregatedAgentList=new AggregatedAgentListManager();
						this.aggregatedAgentList.process((RecMASMessage_ComposeUpdate)msg);
						Log.println(this.idsToString(this.getLevel())+" créer l'agent     ABSTRAIT "+this.nextLevelAgent.toString(),this.getLevel());


						//System.err.println(this.idsToString(this.getLevel())+" Je recois un message INTERNE mais je n'ai pas encore construit le niveau d'abstraction nécéssaire pour le traiter    "+tMsg);
					}
					else
						;//MICHEL  Log.println(this.idsToString(this.getLevel())+" Je recois un message INTERNE mais je n'ai pas encore construit le niveau d'abstraction nécéssaire pour le traiter    "+tMsg);

				}
				else
				{
					//if(true || myAg.getRole()==MWACAgent.roleNOTHING) Log.println(this.idsToString()+" AVANT : "+this.getLabelizedMessageManager().toString()+" msg="+tMsg,Color.PINK);

					if(tMsg.getReceiver()==myAg.getUserId() && tMsg.getSender()==myAg.getUserId() && tMsg.getReceiver()==RecMASMessage.BROADCAST)
					{
						// Je ne fais rien de ce message
						//MICHEL  Log.println("");
						//MICHEL  Log.println(this.idsToString()+" ******* i4: CE MESSAGE INTERNE M'INDIFERE CAR PAS DIFF ET PAS DEST");

						//MICHEL  Log.println(this.idsToString(this.getLevel())+"   vient de recevoir une trame dont il n'est pas destinataire =>  Ce message ne me concerne pas  "+msg,this.getLevel());

					}
					else if(!this.labelizedMessageManager.isAlreadyProcessed(tMsg))
					{
						//if(true || myAg.getRole()==MWACAgent.roleNOTHING)  Log.println(this.idsToString()+" APRES i.e. non traite : "+this.getLabelizedMessageManager().toString(),Color.PINK);

						if(tMsg.getReceiver()==myAg.getUserId())
						{
							//MICHEL  Log.println("");
							//MICHEL  Log.println(this.idsToString()+"  ******* i1: JE SUIS ENTITE DESTINATAIRE DE LA TRAME INTERNE (remonte + repete)");

							Log.println(this.idsToString(this.getLevel())+") fait remonter une trame a son abstraction ("+myAg.getUserId()+","+myAg.getLevel()+") pour traitement  "+tMsg.getData()+"...",this.getLevel());
							myAg.receivedSystemMessage(tMsg.getData());
							Log.println(this.idsToString(this.getLevel())+" ... et je repete le message de transport interne ("+myAg.getUserId()+","+myAg.getLevel()+") pour traitement  "+tMsg.getData(),this.getLevel());
							this.sendData(msg);
						}
						else if(tMsg.getSender()==myAg.getUserId())
						{
							//MICHEL  Log.println("");
							//MICHEL  Log.println(this.idsToString()+"  ******* i2: JE SUIS ABSTRACTION DE L'EMETTEUR DE LA TRAME INTERNE (verifie si compose xxxx et je repete le message)");

							//MICHEL  Log.println(this.idsToString(this.getLevel())+"   vient de recevoir une trame dont il est en qq sorte l'emetteur "+msg,this.getLevel());

							if(tMsg.getData().getType()==RecMASMessage.msgCOMPOSE_ACCEPT )
							{
								if(myAg.nextLevelAgent==null)
								{
									RecMASMessage_ComposeAccept dta=(RecMASMessage_ComposeAccept) tMsg.getData();

									myAg.nextLevelAgent=new RecursiveAgent(dta.getSuperiorId(),myAg);
									Log.println(myAg.idsToString(myAg.getLevel())+"a entendu un COMPOSE ACCEPT qui l'implique et va donc créer l'agent ABSTRAIT "+myAg.nextLevelAgent.toString(),myAg.getLevel());
								}
							}
							else if(tMsg.getData().getType()==RecMASMessage.msgCOMPOSE_REQUEST )
							{
								if(myAg.nextLevelAgent==null)
								{
									RecMASMessage_ComposeRequest dta=(RecMASMessage_ComposeRequest) tMsg.getData();

									myAg.nextLevelAgent=new RecursiveAgent(dta.getSender(),myAg);
									Log.println(myAg.idsToString(myAg.getLevel())+"a entendu un COMPOSE REQUEST qui l'implique et va donc créer l'agent ABSTRAIT "+myAg.nextLevelAgent.toString(),myAg.getLevel());
								}

							}



							// je suis le destinataire
							//this.receivedSystemMessage(tMsg.getData());
							// Je le fais suivre pour les autre composants de mon groupe
							this.sendData(tMsg);
						}
						else if(tMsg.getReceiver()==RecMASMessage.BROADCAST)
						{
							//MICHEL  Log.println("");
							//MICHEL  Log.println(this.idsToString()+"  ******* i3: JE SUIS ENTITE VOISINE ET DONC DESTINATAIRE DU MESSAGE INTERNE (generation d'un message externe)");

							//MICHEL  Log.println(this.idsToString(this.getLevel())+"    nivconcerne=("+myAg.getUserId()+"<>"+tMsg.getReceiver()+","+myAg.getLevel()+")  vient de recevoir une trame dont il n'est pas destinataire =>   je suis un composant du groupe voisin de l'émeteur "+msg,this.getLevel());
							// je suis le destinataire
							myAg.receivedSystemMessage(tMsg.getData());

							// Je genere un message externe

							RecMASMessage_msgExternSystemMessageTransport newExternMsg = new RecMASMessage_msgExternSystemMessageTransport(tMsg,myAg.getUserId());
							if(!this.labelizedMessageManager.isAlreadyProcessed(newExternMsg)) this.sendData(newExternMsg);
						}	
						else
						{
							//MICHEL  System.err.println(this.idsToString()+"  ******* i99:  MESSAGE NE DEVANT JAMAIS S'AFFICHER ");
							//MICHEL  Log.println(this.idsToString(this.getLevel())+"  ******* i99:  MESSAGE NE DEVANT JAMAIS S'AFFICHER ");
						}

					}
					else
						;//MICHEL  Log.println(this.idsToString(this.getLevel())+"   vient de recevoir une trame INTERNE déja traitée (id="+tMsg.getSender()+",lvl="+tMsg.getLayer()+" - #"+ tMsg.getLabel()+")",this.getLevel());
				}
			}
		}
		break;
		case RecMASMessage.msgEXTERN_SYSTEM_MESSAGE_TRANSPORT:
		{
			RecMASMessage_msgExternSystemMessageTransport tMsg= (RecMASMessage_msgExternSystemMessageTransport) msg;

			if(tMsg.getLabel()>RecursiveAgent.STOP_LOG) Log.close();

			RecursiveAgent myAg = this.getRecurssiveAgent(tMsg.getLayer());

			if(msg.getSender()==this.getElementaryId())
			{
				//MICHEL  Log.println(this.idsToString()+"  ******* e0: JE SUIS L'EMETTEUR DE CETTE TRAME EXTERNE!!! "+tMsg);
			}
			else
			{
				//if(true || myAg.getRole()==MWACAgent.roleNOTHING) Log.println(this.idsToString()+" AVANT : "+this.getLabelizedMessageManager().toString()+" msg="+tMsg,Color.PINK);

				if(tMsg.getReceiver()==RecMASMessage.BROADCAST)
					System.err.println("????? External message BROADCASTED???");
				else if(myAg==null)
				{
					//MICHEL  Log.println("Je revois un message EXTERNE mais je n'ai pas encore construit le niveau d'abstraction nécéssaire pour le traiter "+msg);
					//System.err.println("Je revois un message EXTERNE mais je n'ai pas encore construit le niveau d'abstraction nécéssaire pour le traiter "+msg);
				}
				else if(tMsg.getReceiver()!=myAg.getUserId())
				{
					// Je ne fais rien de ce message
					//MICHEL  Log.println("");
					//MICHEL  Log.println(this.idsToString()+" ******* e2: CE MESSAGE EXTERNE M'INDIFERE CAR PAS DIFF ET PAS DEST");
					//MICHEL  Log.println(this.idsToString(this.getLevel())+"    nivconcerne=("+myAg.getUserId()+"<>"+tMsg.getReceiver()+","+myAg.getLevel()+") vient de recevoir une trame dont il n'est pas destinataire =>  Ce message ne me concerne pas  "+msg,this.getLevel());
				}
				else
				{
					if(!this.labelizedMessageManager.isAlreadyProcessed(tMsg))

					{
						//if(true || myAg.getRole()==MWACAgent.roleNOTHING)  Log.println(this.idsToString()+" APRES i.e. non traite : "+this.getLabelizedMessageManager().toString(),Color.PINK);

						//MICHEL  Log.println("");
						//MICHEL  Log.println(this.idsToString()+"  ******* e1: JE SUIS ENTITE DESTINATAIRE DE LA TRAME EXTERNE (remonte + repete)");

						Log.println(this.idsToString(this.getLevel())+") fait remonter une trame a son abstraction ("+myAg.getUserId()+","+myAg.getLevel()+") pour traitement  "+tMsg.getData()+"...",this.getLevel());
						myAg.receivedSystemMessage(tMsg.getData());
						Log.println(this.idsToString(this.getLevel())+" ... et je repete le message de transport interne ("+myAg.getUserId()+","+myAg.getLevel()+") pour traitement  "+tMsg.getData(),this.getLevel());
						this.sendData(msg);
					}
					else
						;//MICHEL  Log.println(this.idsToString(this.getLevel())+"   vient de recevoir une trame EXTERNE déja traitée (id="+tMsg.getSender()+",lvl="+tMsg.getLayer()+" - #"+ tMsg.getLabel()+")",this.getLevel());
				}


			}
		}
		break;
		}
	}



	/** allows to an object to receive a message
	 * @param frame the received MWAC_Frame 
	 */
	public synchronized void receivedFrame(MWACFrame frame)
	{
		RecMASMessage msg = RecMASMessage.createMessage(frame.getData());
		if(msg==null)
			super.receivedFrame(frame);
		else
		{

			String frmSignature = this.idsToString()+"  Frame de "+frame.getSender()+" to "+frame.getReceiver()+" received :";
			if(frame.getSender()!=this.getUserId())
				if( (frame.getReceiver()==MWACFrame.BROADCAST) || (frame.getReceiver()==this.getUserId()) || ((frame.getReceiver()==MWACFrame.BROADCAST_LINK) && this.getRole()==this.roleLINK) || ((frame.getReceiver()==MWACFrame.BROADCAST_REPRESENTATIVE) && this.getRole()==this.roleREPRESENTATIVE))
				{
					frmSignature+="Accepted frame    "+msg;
					//MICHEL  Log.println(frmSignature,this.getLevel());
					this.receivedRecMASMessages.push(msg);

					//this.receivedSystemMessage(msg);
					frmSignature="";
				}
				else
					frmSignature += "Rejected frame (2)   "+msg;
			else
				frmSignature += "Rejected frame (1)  "+msg;

			if(!frmSignature.isEmpty()) ;//MICHEL  Log.println(frmSignature,this.getLevel());
		}
	}


	public void sendData(RecMASMessage msg)
	{
		if(this.getLevel()==0)
		{
			//MICHEL  Log.println(this.idsToString(this.getLevel())+"  Envoie de RecMASMessage  "+msg,this.getLevel());
			super.sendFrame(new MWACFrame(this.getUserId(),MWACFrame.BROADCAST,msg.toByteSequence()));
			if(msg.getType()==RecMASMessage.msgINTERN_SYSTEM_MESSAGE_TRANSPORT) this.labelizedMessageManager.isAlreadyProcessed(msg);	// Pour ne pas traiter soi-même cette trame

			// Le générateur d'un message interne doit lui même l'envoyer en tant qu'externe (au cas ou un voisin soit prêt de lui... A optimiser pus tard en utilisant un bit de ce qui sera la génaralisation des deux

		}
		else
		{
			switch(msg.getType())
			{
			case RecMASMessage.msgDECOMPOSE_REQUEST:
			case RecMASMessage.msgDECOMPOSE_INFORM:
			case RecMASMessage.msgCOMPOSE_REQUEST: 
			case RecMASMessage.msgCOMPOSE_ACCEPT:
			case RecMASMessage.msgCOMPOSE_JOIN: 
			case RecMASMessage.msgCOMPOSE_UPDATE:
			{
				//if( (msg.getLayer()==0) || this.getAbstractedAgentIdentifier(msg.getLayer())==this.getElementaryId()) 
				//this.getRecurssiveAgent(msg.getLayer()).receivedSystemMessage(msg);
				RecMASMessage_msgInternSystemMessageTransport tsm=new RecMASMessage_msgInternSystemMessageTransport(msg.getSender(),msg.getReceiver(),msg.getLayer(),this.getLabelizedMessageManager().getNextAvailableLabel(), msg);
				this.getRecurssiveAgent(0).sendData(tsm);
				if(msg.getType()==RecMASMessage.msgDECOMPOSE_REQUEST) System.err.println("\n"+this.idsToString()+" envoie "+tsm);
				break;
			}
			case RecMASMessage.msgAPPLICATIVE_MESSAGE:
			{
				RecMASMessage_msgApplicativeMessage tMsg = (RecMASMessage_msgApplicativeMessage) msg;
				//Log.println("\n(id="+this.getUserId()+",level="+this.getLevel()+") Transport de messages de type APPLICATIF non implanté pour la couche "+this.getLevel()+"   "+msg.toString());
				//this.receivedFrame(tMsg.getData());
				RecMASMessage_msgInternSystemMessageTransport tsm=new RecMASMessage_msgInternSystemMessageTransport(tMsg.getSender(),tMsg.getReceiver(),msg.getLayer(),this.getLabelizedMessageManager().getNextAvailableLabel(),(RecMASMessage_msgApplicativeMessage) msg);
				Log.println("In "+this.idsToString(this.getLevel())+" :Transport d'un message applicatif ["+msg.toString()+"] => ["+tsm.toString()+"]",this.getLevel());
				if(tsm.getLabel()<RecursiveAgent.MAX_LABEL) 
					this.getRecurssiveAgent(0).sendData(tsm);
				else
					System.err.println("TRUNCATED LABEL "+this.idsToString());
			}
			break;
			case RecMASMessage.msgINTERN_SYSTEM_MESSAGE_TRANSPORT:
			{
				/*
				int[] newReceivers = this.aggregatedAgentList.array();	
				if(newReceivers.length==0) 
				{
					newReceivers=new int[1];
					newReceivers[0]=RecMASMessage.BROADCAST;
				}

				RecMASMessage_msgInternalAgentsTransportSystemMessage tsm=new RecMASMessage_msgInternalAgentsTransportSystemMessage(msg.getSender(),this.getLevel(),new Label(this.nextLabelForInternalTransportMessage++),newReceivers,(RecMASMessage_msgInternalAgentsTransportSystemMessage) msg);
				//Log.println("In (id="+this.getUserId()+",lvl="+this.getLevel()+") : ["+tMsg.toString()+"] => ["+tsm.toString()+"]");

				if(this.getLevel()==0) 
					this.sendData(tsm);
				else
					this.previousLevelAgent.sendData(tsm);
				 */

				this.getRecurssiveAgent(0).sendData(msg);

			}
			break;

			default:

				//MICHEL  Log.println("Reception de messages "+msg+" non pris en compte",this.getLevel());
				System.err.println("!!!! MESSAGE NON TRAITE SANS SENDDATA "+msg);

			}
		}
	}

	public void sendFrame(MWACFrame frame)
	{
		RecMASMessage_msgApplicativeMessage tam = new RecMASMessage_msgApplicativeMessage(this.getUserId(),(frame.getReceiver()<0 ? RecMASMessage.BROADCAST : frame.getReceiver()),this.getLevel(),frame.getMessage());
		Log.println(this.idsToString(this.getLevel())+" envoie une trame applicative "+frame.getMessage(),this.getLevel());
		this.sendData(tam);

	}

	public void sendFrame(Frame frame)
	{
		if(this.getLevel()==0)
		{
			super.sendFrame(frame);
		}
		else
		{
			if(frame instanceof MWACFrame) 
				this.sendFrame((MWACFrame)frame);
			else
				;//MICHEL  Log.println("REFUSE DE TRANSMETTRE UNE Frame",this.getLevel());
		}
	}


	public void processMessage(InteractionContext interaction)
	{

		// Message extraction from the frame
		MWACMessage msg=MWACMessage.createMessage(interaction.frame.getData());

		int id=-1;
		byte role=MWACAgent.roleNOTHING;
		int[] group=new int[0];

		// compute depending the type of message
		switch(msg.getType())
		{
		case MWACMessage.msgINTRODUCTION:
			id=msg.getSender();
			role=MWACAgent.roleNOTHING;
			group=new int[1];group[0]=MWACAgent.groupNONE;
			break;
		case MWACMessage.msgWHO_ARE_MY_NEIGHBOORS:
		case MWACMessage.msgPRESENTATION:
			id=msg.getSender();
			role=((MWACMessage_Presentation)msg).getRole();
			group=((MWACMessage_Presentation)msg).getClonedGroupArray();
			break;
		case MWACMessage.msgCONFLICT_RESOLUTION:
			id=msg.getSender();
			role=MWACAgent.roleREPRESENTATIVE;
			group=new int[1];group[0]=id;
			break;			
		}

		if(id!=-1)
		{
			TripletIdRoleGroup triplet=this.neighboorlist.get(id);

			if((triplet==null)||(triplet.role!=role)||(group.length!=triplet.groups.length))
				this.recursiveState.signalNeighboorModification();
			else
				for(int i=0;i<group.length;i++) if(!triplet.inGroup(group[i])) this.recursiveState.signalNeighboorModification();
		}

		super.processMessage(interaction);

	}
	public MWACAgent getApplicativeAgent()
	{
		return this;
	}


	public int getLevel()
	{
		if (this.previousLevelAgent==null)
			return 0;
		else 
			return 1+this.previousLevelAgent.getLevel();
	}

	public RecursiveAgent getPreviousLevelAgent()
	{
		return this.previousLevelAgent;
	}

	public RecursiveAgent getNextLevelAgent()
	{
		return this.nextLevelAgent;
	}


	public String toHTML()
	{
		String res="";
		//res+="<A NAME=\"LEVEL"+this.getLevel()+"\">Reccursive agent (id="+this.getUserId()+",level="+this.getLevel()+")</A><BR>";

		//res+="<h2 id=test> bla bla bla</h2>";
		res+="<h2>Reccursive agent (id="+this.getUserId()+",level="+this.getLevel()+"):</h2><BR>";
		res+=this.recursiveState.toHTML()+"<BR>";
		res+=this.aggregatedAgentList.toHTML()+"<BR><BR>";
		res+="Applicative agent:<BR>"+super.toSpyWindows();
		res+="<BR>----------------------------------------<BR>";
		return res;
	}

	/** returns the string signature of the agent which will be viewable under the spy windows
	 * @return the string signature of the agent
	 */
	public String toSpyWindows()
	{
		String res="<HTML>"+"Elementary Agent #"+this.getUserId()+"<BR>----------------------------------------<BR>";
		//res+="<a href=#test>Aller vers oui alors écoute moi (ancre2)</a>";

		RecursiveAgent ag = this;

		while(ag!=null)
		{
			res+=ag.toHTML();
			ag=ag.nextLevelAgent;
		}

		res+="</HTML>";

		return res;
	}

	public String idToString()
	{
		return this.idsToString(this.getLevel());
	}

	public int getAbstractionConstructionDuration()
	{
		return this.aggregatedAgentList.constructionDuration();
	}

	private class AggregatedAgentListManager extends TreeSet<Integer>
	{

		public final static long STABILITY_TIME_QUANTUM = 500;

		private long dateOfCreation_1;
		private long dateOfCreation_2;
		private long dateOfLastModification;


		public int constructionDuration()
		{
			return (int) (this.dateOfCreation_2-this.dateOfCreation_1);
		}
		public AggregatedAgentListManager()
		{
			this.dateOfCreation_1=aDate.getCurrentTimeInMS();
			this.dateOfCreation_2=this.dateOfCreation_1;
			this.dateOfLastModification=-1;
		}

		public AggregatedAgentListManager(int i)
		{
			this();
			super.add(i);
		}

		public void broadcasted() {
			// TODO Auto-generated method stub
			this.dateOfLastModification=-1;
		}

		/** return false if no modification occurs */
		public boolean process(RecMASMessage_ComposeUpdate msg)
		{
			boolean res = false;
			int[] lstMembers = msg.getAggregatedAgents();
			for(int i=0;i<lstMembers.length;i++) res=res||this.add(lstMembers[i]);

			if(!res && this.size()==lstMembers.length) return false;

			Iterator<Integer> iter = this.iterator();
			while(iter.hasNext())
				if (!MWACRouteAssistant.contains(lstMembers, iter.next())) iter.remove();

			this.dateOfLastModification=aDate.getCurrentTimeInMS();

			if(res) dateOfCreation_2=aDate.getCurrentTimeInMS();

			return true;
		}

		public boolean add(int i)
		{
			if (super.add(new Integer(i))) {this.dateOfLastModification=aDate.getCurrentTimeInMS();this.dateOfCreation_2=this.dateOfLastModification; return true;}
			return false;
		}

		public long elapsedTimeSinceLastModification()
		{
			if(this.isEmpty() || this.dateOfLastModification==-1) return 0;
			return aDate.getCurrentTimeInMS()-this.dateOfLastModification;
		}

		public int[] array()
		{
			int i=0;
			int [] res = new int[this.size()];
			Iterator<Integer> iter = this.iterator();
			while(iter.hasNext()) res[i++]=iter.next();
			return res;
		}

		public String toHTML()
		{
			return "Aggregated agents : "+this.toString();
		}

	}


	private String idsToString()
	{
		return this.idsToString(-1);
	}

	private String idsToString(int activeLevel)
	{
		RecursiveAgent ag = this.getRecurssiveAgent(0);

		String res="(";
		while(ag!=null)
		{
			res+="("+(activeLevel==ag.getLevel() ? "*" : "")+"lvl="+ag.getLevel()+",id="+ag.getUserId()+",role="+(ag.getRole()==MWACAgent.roleNOTHING ? "N" : MWACAgent.roleToString(ag.getRole()).charAt(0))+(activeLevel==ag.getLevel() ? "*" : "")+(activeLevel==ag.getLevel() ? "*" : "")+")";
			ag=ag.nextLevelAgent;
		}
		return res;
	}


	// remettre en private
	public RecursiveAgent getRecurssiveAgent(int layer)
	{
		RecursiveAgent ag = this;

		while(ag.previousLevelAgent!=null) ag=ag.previousLevelAgent;

		int i=0;
		while(ag!=null && i<layer)
		{
			ag=ag.nextLevelAgent;
			i++;
		}
		return ag;
	}

	@Override
	public BufferedImage graphicalView(double zoom, BasicView view,	boolean noText, int layer) throws InexistantLevelGraphicalViewException {
		// TODO Auto-generated method stub
		int role = this.getRole(layer);

		if(role==-1 || this.getAbstractedAgentIdentifier(layer)!=this.getAbstractedAgentIdentifier(0))
		{
			// this agent is not the agent which must to appear to resume its layer agent view
			throw new InexistantLevelGraphicalViewException(layer,ImageToolkit.CreateTransparencyBufferedImage(1,1));
		}
		else
		{
			//Log.println("Entity #"+this.getElementarId()+": layer #"+layer+"  role="+role);
			// TODO Auto-generated method stub
			view.setText(""+this.getSystemId());
			view.setBackgroundColor(this.roleToColor(role));
			view.setTextColor(Color.BLACK);
			return view.graphicalView(zoom,noText);
		}
	}

	@Override
	public BufferedImage graphicalView(double zoom, BasicView view, int layer) throws InexistantLevelGraphicalViewException{
		// TODO Auto-generated method stub
		return this.graphicalView(zoom, view, false,layer);
	}


	public int getAbstractedAgentIdentifier(int layer) {
		if (layer==this.getLevel())
			return this.getUserId();
		else if(this.nextLevelAgent!=null)
			return this.nextLevelAgent.getAbstractedAgentIdentifier(layer);
		else 
			return -1;
	}
	@Override
	public int getRole(int layer) {
		// TODO Auto-generated method stub
		try{
			return this.getRecurssiveAgent(layer).getRole();
		}
		catch(java.lang.NullPointerException e)
		{
			return -1;
		}
	}

	@Override
	public  Vector<Integer> getViewableLinkedNeighboorsUserIdentifier(int level)
	{
		if(this.getLevel()==level)
		{
			LinkedList<TripletIdRoleGroup> neighboorhood = getNeighboorlist().neighboorList;
			Vector<Integer> res = new Vector<Integer>(neighboorhood.size());
			for(int index=0;index<neighboorhood.size();index++) res.add(neighboorhood.get(index).id);
			return res;
		}
		else if(this.nextLevelAgent!=null)
			return this.nextLevelAgent.getViewableLinkedNeighboorsUserIdentifier(level);
		else
			return null;

	}
	private Color roleToColor(int role)
	{
		switch(role)
		{
		case MWACAgent.roleNOTHING: 		return MWACAgent.colorNOTHING;
		case MWACAgent.roleSIMPLEMEMBER:	return MWACAgent.colorSIMPLEMEMBER;
		case MWACAgent.roleLINK:			return MWACAgent.colorLINK;
		case MWACAgent.roleREPRESENTATIVE:	return MWACAgent.colorREPRESENTATIVE; 
		default: 							return Color.white;
		}
	}


	public void runApplicativeAgentQuantum()
	{
		//Log.println(this.getUserId()+" donne la main a sa couche "+this.getLevel());
		if(this.getUserId()==this.getElementaryId()) 
		{
			if( this.receivedMessageQueue.isEmpty() && (!interaction.lostRepresentativeRole) && (!interaction.reinitializationOfTheRole) )
			{
				this.waitMessages();
			}
			else
				this.processInteractions();

			//	if(this.nextLevelAgent!=null && this.getLevel()<RecursiveAgent.MAX_NB_OF_LEVEL) this.nextLevelAgent.sleep(1);
		}

	}


	public void processApplicativeTasksExecution()
	{
		//this.runApplicativeAgentQuantum();

		// Dans tous les cas on donnbe la main aux abstractions
		if(this.getLevel()==0)
		{
			//if(this.getUserId()==1) Log.println(this.getUserId()+" passe dans quantum ");
			RecursiveAgent ag=this.nextLevelAgent;
			while(ag!=null)
			{
				ag.sleep(0);
				ag.runApplicativeAgentQuantum();
				ag=ag.nextLevelAgent;
			}
		}

	}



	public void sleep(int delay)
	{

		super.sleep(delay);

		while(!this.receivedRecMASMessages.isEmpty())	this.receivedSystemMessage(this.receivedRecMASMessages.pop());

		//if(this.getRole()==MWACAgent.roleREPRESENTATIVE) Log.println("\nRepresentant "+this.getUserId()+" ENTRE DANS "+(++debugNB_OF_SLEEP)+" sleep");


		if(this.getUserId()==this.getElementaryId() && this.getRole()!=MWACAgent.roleREPRESENTATIVE  && this.nextLevelAgent!=null && this.nextLevelAgent.getUserId()==this.getElementaryId())
		{
			System.err.print("\n"+this.idsToString()+" vient de perdre mon role de representant : il se décompose et en demande autant à ceux qui sont aggrégés "+this.aggregatedAgentList.toString());
			this.sendData(new RecMASMessage_DecomposeRequest(this.getUserId(),1+this.getLevel()));
			this.decompose();
			this.notifyEvent(new RecMAS_AbstractionDecompositionEvent(this.getSystemId(),this.getUserId(),1+this.getLevel()));
			this.aggregatedAgentList=null;
		}

		if(this.getUserId()==this.getElementaryId() && this.getRole()==MWACAgent.roleREPRESENTATIVE )	///this.getRole()==MWACAgent.roleREPRESENTATIVE
		{

			// On va répondre a un voisin qui souhaite se composer
			/*
			if(this.joinRequested!=-1) 
				Log.println("\n!!!Représentant (id="+this.getUserId()+",level="+this.getLevel()+")    date="+ aDate.getCurrentTimeInMS()+"   date_requete="+this.joinRequested+"   ecart="+(aDate.getCurrentTimeInMS()-this.joinRequested)+"  ecart attendu="+RecursiveAgent.TIME_TO_ANSWER_COMPOSE_JOIN);
			else
				;//Log.println("\n????-1??? Représentant (id="+this.getUserId()+",level="+this.getLevel()+")    date="+ aDate.getCurrentTimeInMS()+"   date_requete="+this.joinRequested+"   ecart="+(aDate.getCurrentTimeInMS()-this.joinRequested)+"  ecart attendu="+RecursiveAgent.TIME_TO_ANSWER_COMPOSE_JOIN);
			 */

			if( this.joinRequested>0 && aDate.getCurrentTimeInMS()-this.joinRequested>RecursiveAgent.TIME_TO_ANSWER_COMPOSE_JOIN)
			{
				if(this.getUserId()!=this.getElementaryId() && this.getRole()!=MWACAgent.roleREPRESENTATIVE)
				{
					System.err.println("\n!!!!! "+idsToString()+"   "+MWACAgent.roleToString(this.getRole()));
				}
				else
				{
					Log.println(this.idsToString(this.getLevel())+"   Stability : "+this.recursiveState.getStability()+"  => je veux créer une abstraction ",this.getLevel());
					RecMASMessage_ComposeRequest request = new RecMASMessage_ComposeRequest(this.getUserId(),this.getLevel());
					//MICHEL  Log.println(request,this.getLevel());
					this.sendData(request);

					this.joinRequested=-1;
				}
			}


			// On déclenche une demande de composition
			if(this.aggregatedAgentList==null && !this.recursiveState.inComposition && this.recursiveState.getStability() && this.nextLevelAgent==null && this.getLevel()<RecursiveAgent.MAX_NB_OF_LEVEL)
			{
				Log.println(this.idsToString(this.getLevel())+" est prêt à lancer une composition",this.getLevel());
				RecMASMessage_ComposeRequest request = new RecMASMessage_ComposeRequest(this.getUserId(),this.getLevel());
				this.sendData(request);
				this.aggregatedAgentList=new AggregatedAgentListManager(this.getUserId());
				this.nextLevelAgent=new RecursiveAgent(this.getUserId(),this);
				//MICHEL  Log.println(this.idsToString()+" a envoyer une requete "+request,this.getLevel());
				this.recursiveState.inComposition=true;

				this.notifyEvent(new RecMAS_AbstractionCreationEvent(this.getSystemId(),this.getUserId(),this.getLevel()));
			}



			// On diffuse une màj de la liste des agents agrégés
			if(this.aggregatedAgentList!=null && this.aggregatedAgentList.elapsedTimeSinceLastModification()>Math.min(AggregatedAgentListManager.STABILITY_TIME_QUANTUM*(1+this.getLevel()),AggregatedAgentListManager.STABILITY_TIME_QUANTUM*5))
			{
				RecMASMessage_ComposeUpdate update = new RecMASMessage_ComposeUpdate(this.getUserId(),this.getLevel(),this.role,this.aggregatedAgentList.array());
				Log.println(update,this.getLevel());
				this.sendData(update);
				this.aggregatedAgentList.broadcasted();
				//this.recursiveState.signalNeighboorModification();
				this.notifyEvent(new RecMAS_AbstractionUpdateEvent(this.getSystemId(),this.getUserId(),this.getLevel(),this.getRole(),this.aggregatedAgentList.array()));
			}
			else
			{
				//Log.println("NOT "+this.aggregatedAgentList.elapsedTimeSinceLastModification()+" > "+Math.min(AggregatedAgentListManager.STABILITY_TIME_QUANTUM*(1+this.getLevel()),AggregatedAgentListManager.STABILITY_TIME_QUANTUM*5));
			}


		}
		else
		{


			if(aDate.getCurrentTimeInMS()-this.joinRequested>RecursiveAgent.MAX_TIME_BEFORE_RELAUNCH_COMPOSE_JOIN) this.joinRequested=-1;

			// On est pas représentant
			if((!this.neighboorlist.isEmpty()) && this.recursiveState.getStability() && this.nextLevelAgent==null && this.getLevel()<RecursiveAgent.MAX_NB_OF_LEVEL && this.joinRequested==-1 && this.getUserId()==this.getElementaryId())
			{
				RecMASMessage_ComposeJoin join = new RecMASMessage_ComposeJoin(this.getUserId(),this.getLevel());
				//Log.println("Demande l'envoie de "+join);
				this.sendData(join);
				this.joinRequested=aDate.getCurrentTimeInMS();
			}


		}


		if(this.getUserId()==this.getElementaryId()) 
			this.processApplicativeTasksExecution();
		else
		{
			// optimisation simulateur pour eviter surcharge traitement
			this.receivedMessageQueue.removeAll();
		}

	}

	/*
	private String aeffacer="";

	class PeriodicalProcessExecution extends TimerTask {

		public void run() {
			processApplicativeTasksExecution();
		}
	}
	 */

	public void decompositionNow()
	{
		System.out.println(this.idsToString()+" DECOMPOSITION");
	}

	public void decomposeAll()
	{
		System.out.println("Decomposition de "+this);
		this.getRecurssiveAgent(0).decompose(true);
	}


}
