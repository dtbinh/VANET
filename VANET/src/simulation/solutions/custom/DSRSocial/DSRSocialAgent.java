package simulation.solutions.custom.DSRSocial;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Vector;


import simulation.entities.Agent;
import simulation.events.system.ReceivedMessageEvent;
import simulation.messages.Frame;
import simulation.messages.Message;
import simulation.multiagentSystem.MAS;
import simulation.solutions.custom.DSRSocial.Messages.DSRSocialFrame;
import simulation.solutions.custom.DSRSocial.Messages.DSRSocialMessage;
import simulation.solutions.custom.DSRSocial.Messages.DSRSocialMessage_Data;
import simulation.solutions.custom.DSRSocial.Messages.DSRSocialMessage_RouteReply;
import simulation.solutions.custom.DSRSocial.Messages.DSRSocialMessage_RouteRequest;
import simulation.solutions.custom.DSRSocial.Messages.DSRSocialMessage_ServicesReply;
import simulation.solutions.custom.DSRSocial.Messages.DSRSocialMessage_ServicesRequest;
import simulation.solutions.custom.DSRSocial.Route.DSRSocialRouteAssistant;
import simulation.solutions.custom.DSRSocial.Route.EmptyRouteException;
import simulation.solutions.custom.DSRSocial.Route.NoNextRelayException;
import simulation.solutions.custom.DSRSocial.Route.NoPreviousRelayException;
import simulation.solutions.custom.DSRSocial.Route.UnknownIdentifierException;

public class DSRSocialAgent extends Agent {

	/** Initial TTL */
	private static byte INITIAL_TTL = 4;
	/** max ttl when services are searched */
	public static int MAX_TTL = 14;
	/** ttl step added when services are not found */
	public static int TTL_STEP = 2;
	/** time before launched a new services request when they are not found */
	public static int MAX_WAITING_TIME = 4000;


	private byte nextServiceRequestId;


	private Vector<Integer> services;
	private SearchedServices searchedServices;


	private short nextRequestIdentifier;
	private FrameFIFOStack receivedFrameQueue;
	private DSRSocialAlreadyProcessedRouteRequestManager alreadyProcessedRouteRequestManager;
	private DSRSocialAlreadyProcessedRouteRequestManager alreadyProcessedServicesRequestManager;
	private LinkedList<DSRSocialMessage_Data> waitingMessageToSend; 
	private MAS mas;

	private byte NUMBER_OF_MAX_HOP_AUTHORISED = 2;

	public DSRSocialAgent(MAS mas, Integer id, Float energy, Integer range) {
		super(mas, id, range);
		this.nextRequestIdentifier=0;
		this.receivedFrameQueue=new FrameFIFOStack();
		this.waitingMessageToSend=new LinkedList<DSRSocialMessage_Data>();
		this.alreadyProcessedRouteRequestManager=new DSRSocialAlreadyProcessedRouteRequestManager();
		this.mas=mas;
		this.nextServiceRequestId=0;
		this.searchedServices= new SearchedServices();
		this.services=new Vector<Integer>();
		this.alreadyProcessedServicesRequestManager=new DSRSocialAlreadyProcessedRouteRequestManager();
	}

	/**  Launched by the call of start method*/
	public void run()
	{
		DSRSocialFrame frm;
		DSRSocialMessage msg;

		System.out.println("Démarrage de l'entité "+this.getUserId()+" utilisant DSR");




		String servicesPresentation="Supplied services :";

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

		while(!isKilling() && !isStopping())
		{
			// Pause
			try{
				Thread.sleep(SLEEP_TIME_SLOT);
				this.verifySearchedServices();
			}catch(Exception e){};

			// Preparation of the others threads
			while(  ((isSuspending()) && (!isKilling() && !isStopping()))) try{Thread.sleep(SLEEP_TIME_SLOT);}catch(Exception e){};

			// As-t'on un message a traiter?
			if(!this.receivedFrameQueue.isEmpty())
			{
				while(!this.receivedFrameQueue.isEmpty())
				{
					frm=this.receivedFrameQueue.pop();

					msg=DSRSocialMessage.createMessage(frm.getData());

					switch(msg.getType())
					{
					case DSRSocialMessage.ROUTE_REQUEST:
					{
						DSRSocialMessage_RouteRequest tMsg = (DSRSocialMessage_RouteRequest)msg;
						if(msg.getReceiver()==this.getUserId()) 
						{
							if(!this.alreadyProcessedRouteRequestManager.isAlreadyProcessed(tMsg.getSender(),tMsg.getRequestIdentifier()))
							{
								// Return a route reply

								int relay=0;
								try 
								{
									relay=DSRSocialRouteAssistant.getLastRelay(tMsg.getRoute());
								}
								catch (EmptyRouteException e) 
								{
									relay=tMsg.getSender();
								}
								this.sendFrame(new DSRSocialFrame(this.getUserId(),relay,new DSRSocialMessage_RouteReply(tMsg)));
							}
							else
							{
								// Already replied
							}
						}
						else
						{
							// Have I already answer to a such research?
							if(!this.alreadyProcessedRouteRequestManager.isAlreadyProcessed(tMsg.getSender(),tMsg.getRequestIdentifier()) && (tMsg.getSender()!=this.getUserId()))
							{
								int[] route =DSRSocialRouteAssistant.add(tMsg.getRoute(),this.getUserId());
								if(DSRSocialMessage.TTL_VERSION_OF_DSR)
								{if(tMsg.getTTL()>0) this.sendFrame(new DSRSocialFrame(this.getUserId(),DSRSocialFrame.BROADCAST,new DSRSocialMessage_RouteRequest(tMsg.getSender(),tMsg.getReceiver(),tMsg.getRequestIdentifier(),(byte)(tMsg.getTTL()-1),route)));}
								else
									this.sendFrame(new DSRSocialFrame(this.getUserId(),DSRSocialFrame.BROADCAST,new DSRSocialMessage_RouteRequest(tMsg.getSender(),tMsg.getReceiver(),tMsg.getRequestIdentifier(),route)));
							}
							else
							{
								// DO nothing
							}
						}
					}
					break;
					case DSRSocialMessage.ROUTE_REPLY:
					{
						DSRSocialMessage_RouteReply tMsg = (DSRSocialMessage_RouteReply)msg;

						if(tMsg.getReceiver()==this.getUserId())
						{
							// I am the final receiver of the ROUTE-REPLY
							// I send my message along this route
							int receiver=tMsg.getSender();
							DSRSocialMessage_Data item;
							int[] route= tMsg.getRoute();
							ListIterator<DSRSocialMessage_Data> iter = this.waitingMessageToSend.listIterator();
							while(iter.hasNext())
							{
								item=iter.next();
								if(item.getReceiver()==receiver)
								{
									item.setRoute(route);
									//this.notifyEvent(new SendedMessageEvent(this.getUserId(),(Message)item.clone()));
									this.sendFrame(new DSRSocialFrame(this.getUserId(),frm.getSender(),item));
									iter.remove();
								}
							}
						}
						else if(DSRSocialRouteAssistant.contains(tMsg.getRoute(),this.getUserId()))
						{
							// I must transmit the message
							try {
								this.sendFrame(new DSRSocialFrame(this.getUserId(),DSRSocialRouteAssistant.previous(tMsg.getRoute(),this.getUserId()),tMsg));
							} catch (EmptyRouteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (NoPreviousRelayException e) {
								// TODO Auto-generated catch block
								this.sendFrame(new DSRSocialFrame(this.getUserId(),tMsg.getReceiver(),tMsg));
							} catch (UnknownIdentifierException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						else
						{
							System.out.println("FORGOTTEN ROUTE REPLY");
						}
					}
					break;
					case DSRSocialMessage.DATA:
					{
						DSRSocialMessage_Data tMsg = (DSRSocialMessage_Data)msg;
						if(tMsg.getReceiver()==this.getUserId())
						{
							System.out.println("RECU "+tMsg.getMessage());
							// Notify la bonne reception de l'évenement
							this.notifyEvent(new ReceivedMessageEvent(this.getSystemId(),(DSRSocialMessage_Data) tMsg.clone()));
						}
						else
						{
							// Dois-je retransmettre le message?
							if(DSRSocialRouteAssistant.contains(tMsg.getRoute(),this.getUserId()))
							{
								try {
									this.sendFrame(new DSRSocialFrame(this.getUserId(),DSRSocialRouteAssistant.next(tMsg.getRoute(),this.getUserId()),(DSRSocialMessage_Data)tMsg.clone()));
								} catch (NoNextRelayException e) {
									// TODO Auto-generated catch block
									this.sendFrame(new DSRSocialFrame(this.getUserId(),tMsg.getReceiver(),(DSRSocialMessage_Data)tMsg.clone()));
								} catch (UnknownIdentifierException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (EmptyRouteException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					}
					break;
					case DSRSocialMessage.SERVICES_REQUEST:
					{
						DSRSocialMessage_ServicesRequest tMsg=(DSRSocialMessage_ServicesRequest) msg;

						if( (!alreadyProcessedServicesRequestManager.isAlreadyProcessed(tMsg.getSender(), tMsg.getIdRequest())) && (tMsg.getSender()!=this.getUserId()))
						{
							Vector<Integer> searchedServ = DSRSocialRouteAssistant.integerArrayToVector(tMsg.getServices());
							Vector<Integer> founded = new Vector<Integer>();

							ListIterator<Integer> iter = searchedServ.listIterator();
							Integer item;
							while(iter.hasNext())
							{
								item=iter.next();
								if(this.services.contains(item))
								{
									founded.add(item);
									iter.remove();
								}
							}

							if(!founded.isEmpty())
							{
								int relais =DSRSocialRouteAssistant.getLastId(tMsg.getRoute());
								if (relais==DSRSocialRouteAssistant.UNDEFINED_ID) 
									{
										System.out.println(this.getUserId()+" est VOISIN de "+tMsg.getSender()+" ?");
										relais=tMsg.getSender();
									}
								if(relais<1) System.out.println("(1) ERREURRRRR dans la recherche de  relais   Route="+DSRSocialRouteAssistant.integerArrayToString(tMsg.getRoute())+"  Relais="+relais);

								System.out.println("\nAg "+this.getUserId()+" send its founded services   (DSRSocialMessage_ServicesReply)");
								this.sendFrame(new DSRSocialFrame(this.getUserId(),relais,new DSRSocialMessage_ServicesReply(this.getUserId(),tMsg.getSender(),tMsg.getIdRequest(),DSRSocialRouteAssistant.vectorToIntegerArray(founded),tMsg.getRoute())));
							}

							if(!searchedServ.isEmpty() && (tMsg.getTTL()>0))
							{
								tMsg.setServices(DSRSocialRouteAssistant.vectorToIntegerArray(searchedServ));
								tMsg.setRoute(DSRSocialRouteAssistant.add(tMsg.getRoute(),this.getUserId()));

								tMsg.decreaseTTL();
								tMsg.setServices(DSRSocialRouteAssistant.vectorToIntegerArray(searchedServ));

								System.out.println("\nAg "+this.getUserId()+" folow the services request)  "+DSRSocialRouteAssistant.integerArrayToString(tMsg.getServices()));
								this.sendFrame(new DSRSocialFrame(this.getUserId(),DSRSocialFrame.BROADCAST,tMsg));
							}

						}
					}
					break;
					case DSRSocialMessage.SERVICES_REPLY:
					{
						if(frm.getReceiver()==this.getUserId())
						{

							DSRSocialMessage_ServicesReply tMsg = (DSRSocialMessage_ServicesReply) msg;

								if(tMsg.getReceiver()==this.getUserId())
								{
									// Je met à jour mes services
									this.verifySearchedServices(tMsg.getServices());
								}
								else
								{
									//System.out.println("RECU PAR repr "+this.getUserId()+" ROUTE REPLY "+tMsg.toString());
									int relais = DSRSocialRouteAssistant.getPreviousId(tMsg.getRoute(),this.getUserId());
									if (relais==DSRSocialRouteAssistant.UNDEFINED_ID) 
									{
										System.out.println(this.getUserId()+" est VOISIN de "+tMsg.getSender()+" ?");
										relais=tMsg.getReceiver();
									}
									if(relais<1) System.out.println("(2) ERREURRRRR dans la recherche de  relais de "+this.getUserId()+"  Route="+DSRSocialRouteAssistant.integerArrayToString(tMsg.getRoute())+"  Relais="+relais+"    "+tMsg.toString());
									this.sendFrame(new DSRSocialFrame(this.getUserId(),relais,new DSRSocialMessage_ServicesReply(tMsg.getSender(),tMsg.getReceiver(),tMsg.getIdRequest(),tMsg.getServices(),tMsg.getRoute())));
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
		this.waitingMessageToSend.add(new DSRSocialMessage_Data(this.getUserId(),receiver,new int[0],message));
		if(DSRSocialMessage.TTL_VERSION_OF_DSR)
			this.sendFrame(new DSRSocialFrame(this.getUserId(),DSRSocialFrame.BROADCAST,new DSRSocialMessage_RouteRequest(this.getUserId(),receiver,this.nextRequestIdentifier++,this.NUMBER_OF_MAX_HOP_AUTHORISED,new int[0])));
		else
			this.sendFrame(new DSRSocialFrame(this.getUserId(),DSRSocialFrame.BROADCAST,new DSRSocialMessage_RouteRequest(this.getUserId(),receiver,this.nextRequestIdentifier++,new int[0])));
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
		this.receivedFrame((DSRSocialFrame) frame);
	}

	/** allows to an object to receive a message
	 * @param frame the received DSR_Frame 
	 */
	public synchronized void receivedFrame(DSRSocialFrame frame)
	{
		if( (frame.getReceiver()==DSRSocialFrame.BROADCAST) || (frame.getReceiver()==this.getUserId()) ) 
			this.receivedFrameQueue.push(frame);
	}

	public String toSpyWindows()
	{

		String res= "<HTML>";

		res+="Agent #"+this.getUserId()+"<BR>Num prochaine requete:"+this.nextRequestIdentifier+"<BR>";
		res+="<B>Received frames queue</B>:<BR>"+receivedFrameQueue.toString()+"<BR><BR>";

		res+="<B>Already processed frame</B>:<BR>"+this.alreadyProcessedRouteRequestManager.toHTML();

		res+="<B>Waiting messages to send</B> ("+this.waitingMessageToSend.size()+")<BR>";
		res+="<TABLE border=1>";
		res+="<TR><TD>Message</TD></TR>";
		ListIterator<DSRSocialMessage_Data> iter = this.waitingMessageToSend.listIterator();
		while(iter.hasNext()) res+="<TR><TD>"+iter.next().toString()+"</TD></TR>";
		res+="</TABLE>";

		return res+"</HTML>";



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
				Vector<Integer> v = DSRSocialRouteAssistant.integerArrayToVector(item.searchedServices());
				ListIterator<Integer> iterSearchedServices=v.listIterator();
				Integer search;
				while(iterSearchedServices.hasNext())
				{
					search=iterSearchedServices.next();
					if(this.services.contains(search) || DSRSocialRouteAssistant.contains(services, search)) 
					{
						System.out.println("\nL'agent "+this.getUserId()+" connait le service "+search);
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


				Vector<Integer> v = DSRSocialRouteAssistant.integerArrayToVector(item.searchedServices());
				ListIterator<Integer> iterSearchedServices=v.listIterator();
				Integer search;
				while(iterSearchedServices.hasNext())
				{
					search=iterSearchedServices.next();
					if(this.services.contains(search)) 
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
						System.out.println("\nRecherche initiée par le représentant "+this.getUserId()+" services:"+DSRSocialRouteAssistant.integerArrayToString(item.searchedServices()));
						item.TTL=this.INITIAL_TTL;
						item.nbRequest++;
						item.serviceRequestDate=this.mas.elapsedSimulationTime();
						item.requestIdentifier=this.nextServiceRequestId++;
						this.sendFrame(new DSRSocialFrame(this.getUserId(),DSRSocialFrame.BROADCAST,new DSRSocialMessage_ServicesRequest(this.getUserId(),DSRSocialMessage.BROADCAST,item.requestIdentifier,item.TTL,item.searchedServices(),new int[0])));
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
							System.out.println("\nRelance de recherche initiée (TTL="+item.TTL+") par le représentant "+this.getUserId()+" services:"+DSRSocialRouteAssistant.integerArrayToString(item.searchedServices()));
							item.TTL+=TTL_STEP;
							item.nbRequest++;
							item.serviceRequestDate=this.mas.elapsedSimulationTime();
							item.requestIdentifier=this.nextServiceRequestId++;
							this.sendFrame(new DSRSocialFrame(this.getUserId(),DSRSocialFrame.BROADCAST,new DSRSocialMessage_ServicesRequest(this.getUserId(),DSRSocialMessage.BROADCAST,item.requestIdentifier,item.TTL,item.searchedServices(),new int[0])));
						}
					}

				}
			}
		}
		return true;
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


}


