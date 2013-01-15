package simulation.solutions.custom.AntMWAC.Ant.Messages;

import java.nio.ByteBuffer;
import simulation.solutions.custom.AntMWAC.MWAC.MWACRouteAssistant;
import simulation.solutions.custom.AntMWAC.MWAC.Messages.MWACMessage;

@SuppressWarnings("serial")
public class AntMessage_Forward extends MWACMessage {
	
	private short AntId;    //id de la fourmi 
	private int longueur  ; //la longueur de la route parcourue par  la fourmi
	private int [] route;   // la route de la fourmi 
	public String msg;     // le msg  que transporte  la fourmi 
	int nbHoup;            //le nombre de sauts 
	
/**************************************************************************************/	

	public AntMessage_Forward  (int sender, int receiver, byte [] data){
		
		super (sender, receiver, MWACMessage.msgFORWARD_ANT);
		ByteBuffer buf = ByteBuffer.wrap(data);
		this.AntId = buf.getShort();
		this.longueur =buf.getInt();
		this.nbHoup=buf.getInt();
		this.route = new int [this.longueur]; 
		for (int i=0; i<this.longueur; i++)	this.route[i]=buf.getInt();
	  
		//recuperer le message 
		this.msg="";  char c;
		while(buf.hasRemaining())
		{
			c=(char)buf.get();
			msg+=c; 
		}
	}
	
/**************************************************************************************/	
	
	public AntMessage_Forward  (int sender, int receiver, short AntId, int [] memory, String msg,int nbhoup){
		
		super (sender, receiver, MWACMessage.msgFORWARD_ANT);
		
		this.AntId = AntId;
		this.longueur = 0;
		this.nbHoup=nbhoup;
		this.route = new int [memory.length];
		for (int i=0;i< this.route.length;i++)	this.route [i] = memory [i];
		this.msg = msg;
	}
	/**************************************************************************************/	
	public Short get_AntId(){
		return this.AntId;
	}
	
	/**************************************************************************************/	
	public int[] get_route(){
		return this.route;
	}
	/**************************************************************************************/	
	public String get_msg(){
		return this.msg; 
	}
		
	/**************************************************************************************/
	public int get_length()
	{
		return this.longueur;
	
	}
	/****************************************************************************************/
	public int get_nbhoup(){
		return this.nbHoup;
		
	}
	
	/*************************************************************************************/
	public int   set_nbhoup(int n){   
		this.nbHoup=n;
		return nbHoup;
	}
	/****************************************************************************************/
	/**
	 * permet d'obtenir la representation de la fourmi en bytes 
	 * @return la representation en bytes 
	 */
	public  byte[] toByteSequence() 
	{
		char[] ch=msg.toCharArray();
		ByteBuffer buf = ByteBuffer.allocate( 10 + (4 * this.route.length) + ch.length);
		
		buf.putShort(this.AntId);
		buf.putInt(this.longueur);
		buf.putInt(nbHoup);
		for(int i=0;i<this.route.length;i++) buf.putInt(this.route[i]);
		for(int i=0; i < ch.length; i++) buf.put((byte)(ch[i]));
		
		return super.toByteSequence(buf.array());
	}

	/***************************************************************************************/

	public void set_route(int []chemin)
	{
		this.route= chemin;
	}
	/**************************************************************************************/	
	public void set_length(int l)
	{
		this.longueur =l;
	}
	
	/**************************************************************************************/
	/**
	 * permet d'obtenir la representation de la fourmi en String 
	 * @return la representation en String
	 */
	public String toString(){
		
		return "Forward_Ant #"+this.get_AntId()+ " instancied by "+this.getSender()+" to "+this.getReceiver()+ ", Route is " +MWACRouteAssistant.routeToString(this.get_route())+ " Data is "+this.msg;
	}

	
		
		

}
