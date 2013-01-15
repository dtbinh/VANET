package simulation.solutions.custom.AntMWAC.Ant.Messages;

import java.nio.ByteBuffer;

import simulation.solutions.custom.AntMWAC.MWAC.MWACRouteAssistant;
import simulation.solutions.custom.AntMWAC.MWAC.Messages.MWACMessage;


@SuppressWarnings("serial")
public class AntMessage_Backward extends MWACMessage {

	private short AntId;   //id de la fourmi 
	private int [] route ;  // la route de la fourmi 
	private int longueur;

	/******************************************************************************/
	
	public AntMessage_Backward (int sender, int receiver, byte [] data) {

		super ( sender, receiver, MWACMessage.msgBACKWARD_ANT);

		ByteBuffer buf = ByteBuffer.wrap(data);
		this.AntId = buf.getShort();
		this.longueur=buf.getInt();
		this.route = new int [(int) ((buf.capacity()-6)/4)]; 
		for (int i=0; i<this.route.length; i++)   this.route[i] = buf.getInt();
	}

	/******************************************************************************/
	
	
	public AntMessage_Backward (int sender, int receiver, Short AntId,  int [] route,int leng) {

		super ( sender, receiver, MWACMessage.msgBACKWARD_ANT);
		this.AntId = AntId;
		this.longueur=leng;
		this.route = new int [route.length]; 
		for (int i=0; i<route.length; i++)     this.route [i] = route [i];
	}
	/******************************************************************************/
	
	/**
	 * initialiser la fourmi Backward à partir de la fourmi Forward
	 */
	public AntMessage_Backward ( AntMessage_Forward msg) {

		super (msg.getReceiver(), msg.getSender(), MWACMessage.msgBACKWARD_ANT);
		this.AntId = msg.get_AntId();
		this.route = new int [msg.get_route().length]; 
		this.route = msg.get_route();
		this.longueur=msg.get_route().length;

	}

	/******************************************************************************/
	public Short get_AntId(){
		return this.AntId;
	}
	
	
	
	/******************************************************************************/
	public int[] get_route(){
		return this.route;
	}

	/******************************************************************************/
	public int get_length_route()
	{
		return this.longueur;
	}
	/******************************************************************************/
	
	public void set_route(int []chemin)
	{
		this.route= chemin;
	}
	/*****************************************************************************/
	/**
	 * permet d'obtenir la representation de la fourmi en bytes 
	 * @return la representation en bytes 
	 */
	public  byte[] toByteSequence() 
	{
		ByteBuffer buf = ByteBuffer.allocate( 6 + (4*this.route.length));

		buf.putShort(this.AntId);
		buf.putInt(this.longueur);
	
		for(int i=0;i<this.route.length;i++) buf.putInt(this.route[i]);

		return super.toByteSequence(buf.array());
	}

	/**************************************************************************************/	
	/**
	 * permet d'obtenir la representation (en String) de ce message
	 * @return la representation en String
	 */
	public String toString(){

		return "Backward_Ant #"+get_AntId()+ " instancied by "+getSender()+" to "
		+getReceiver()+ ", Route is " + MWACRouteAssistant.routeToString(get_route());
	}


}
