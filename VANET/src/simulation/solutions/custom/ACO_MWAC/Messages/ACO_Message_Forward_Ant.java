package simulation.solutions.custom.ACO_MWAC.Messages;

import java.nio.ByteBuffer;

import simulation.solutions.custom.ACO_MWAC.AntAssistant.Ant_Route_Assistant;

/**
* le message represenatant la fourmi forward dont le role est de transporter un message de données
* @author Yacine LATTAB & Ouerdia SLAOUTI
*/

@SuppressWarnings("serial")
public class ACO_Message_Forward_Ant extends ACO_Message {
	
	//l'identité de la fourmi Forward
	private short AntId;
	
	//taille de la memoire de la fourmi Forward
	private int length;
	
	//la memoire de la fourmi Forward
	private int [] memory;
	
	//la données effectivement transportées
	public String msg;
	
	
	/**
	 * constructeur 
	 * @param sender le destinateur 
	 * @param receiver le destinataire
	 * @param data données en une suite de Byte
	 */
	public ACO_Message_Forward_Ant  (int sender, int receiver, byte [] data){
		
		super (sender, receiver, ACO_Message.msgFORWARD_ANT);
		
		ByteBuffer buf = ByteBuffer.wrap(data);

		this.AntId = buf.getShort();
		this.length = buf.getInt();

		this.memory = new int [this.length]; 
		for (int i=0; i<this.length; i++)	this.memory[i]=buf.getInt();
		
		//recuperer la donnée (le message de données)
		this.msg="";  char c;
		while(buf.hasRemaining())
		{
			c=(char)buf.get();
			msg+=c; 
		}
	}
	
	
	/**
	 * construcreur
	 * @param sender le destinateur
	 * @param receiver le destinataire
	 * @param AntId l'identité de la fourmi
	 * @param memory la route suivie par la fourmi
	 * @param msg la donnée effective
	 */
	public ACO_Message_Forward_Ant  (int sender, int receiver, short AntId, int [] memory, String msg){
		
		super (sender, receiver, ACO_Message.msgFORWARD_ANT);
		
		this.AntId = AntId;
		this.length = memory.length;
		
		this.memory = new int [this.length];
		for (int i=0;i< this.length;i++)	this.memory [i] = memory [i];
		
		this.msg = msg;
	}
	
	public Short get_AntId(){
		return this.AntId;
	}
	public int get_length(){
		return this.length;
	}
	public int[] get_Memory(){
		return this.memory;
	}
	public String get_msg(){
		return this.msg; 
	}
		
	
	/**
	 * permet d'obtenir la representation de la fourmi en bytes 
	 * @return la representation en bytes 
	 */
	public  byte[] toByteSequence() 
	{
		char[] ch=msg.toCharArray();
		ByteBuffer buf = ByteBuffer.allocate( 6 + (4 * this.memory.length) + ch.length);
		
		buf.putShort(this.AntId);
		buf.putInt(this.length);
		for(int i=0;i<this.memory.length;i++) buf.putInt(this.memory[i]);
		for(int i=0; i < ch.length; i++) buf.put((byte)(ch[i]));
		
		return super.toByteSequence(buf.array());
	}

	/**
	 * permet d'obtenir la representation de la fourmi(en String) 
	 * @return la representation en String
	 */
	public String toString(){
	
		return "Forward_Ant #"+get_AntId()+ " instancied by "+getSender()+" to "+getReceiver()+ ", Route is " +Ant_Route_Assistant.toString(get_Memory())+ " Data is "+this.msg;
	}
		
		

}
