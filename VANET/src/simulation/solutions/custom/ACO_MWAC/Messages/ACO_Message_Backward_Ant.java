package simulation.solutions.custom.ACO_MWAC.Messages;

import java.nio.ByteBuffer;

import simulation.solutions.custom.ACO_MWAC.AntAssistant.*;

/**
 * la fourmi Backward permettant d'acquitter une données et de mettre à jour la pheromone
 * @author Yacine LATTAB & Ouerdia SLAOUTI
 */

@SuppressWarnings("serial")
public class ACO_Message_Backward_Ant extends ACO_Message {

	//l'identité de la fourmi Forward
	private short AntId;

	//la longueur de la route empruntée par la fourmi (pour la mise à jour de la pheromone)
	private int length;

	//la memoire de la fourmi Backward
	private int [] memory;

	/**
	 * initialisation
	 * @param sender le destinateur de la fourmi backwrd
	 * @param receiver le destinataire de la fourmi backward
	 * @param data (identité de la fourmi + la memoire)
	 */
	public ACO_Message_Backward_Ant (int sender, int receiver, byte [] data) {

		super ( sender, receiver, ACO_Message.msgBACKWARD_ANT);

		ByteBuffer buf = ByteBuffer.wrap(data);

		//recuperer l'identité de la fourmi
		this.AntId = buf.getShort();

		//calculer la longueur du chemin
		this.length = buf.getInt();

		//recuperer la momoire
		this.memory = new int [(int) ((buf.capacity()-6)/4)]; 
		for (int i=0; i<this.memory.length; i++)   this.memory[i] = buf.getInt();
	}

	
	/**
	 * constructeur pour initliser une fourmi Backward à partir d'une fourmi Backward
	 */
	public ACO_Message_Backward_Ant (int sender, int receiver, Short AntId, int length, int [] memory) {

		super ( sender, receiver, ACO_Message.msgBACKWARD_ANT);

		//initialiser l'identité d'une fourmi
		this.AntId = AntId;

		//initialiser la longueur du chemin
		this.length = length;

		//initialiser la momoire de la fourmi
		this.memory = new int [memory.length]; 
		for (int i=0; i<memory.length; i++)     this.memory [i] = memory [i];
	}

	/**
	 * initialiser la fourmi Backward à partir de la fourmi Forward
	 * @param fourmi Forward_Ant
	 */
	public ACO_Message_Backward_Ant ( ACO_Message_Forward_Ant msg) {

		super (msg.getReceiver(), msg.getSender(), ACO_Message.msgBACKWARD_ANT);

		this.AntId = msg.get_AntId();
		this.length = msg.get_length();

		this.memory = new int [msg.get_Memory().length]; 
		this.memory = msg.get_Memory();

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

	/**
	 * permet d'obtenir la representation de la fourmi en bytes 
	 * @return la representation en bytes 
	 */
	public  byte[] toByteSequence() 
	{
		ByteBuffer buf = ByteBuffer.allocate( 2 + 4* (1+this.memory.length));

		buf.putShort(this.AntId);
		buf.putInt(this.length);

		for(int i=0;i<this.memory.length;i++) buf.putInt(this.memory[i]);

		return super.toByteSequence(buf.array());
	}


	/**
	 * permet d'obtenir la representation (en String) de ce message
	 * @return la representation en String
	 */
	public String toString(){

		return "Backward_Ant #"+get_AntId()+ " instancied by "+getSender()+" to "+getReceiver()+ ", Hop_Count is "+ get_length() +", Route is " + Ant_Route_Assistant.toString(get_Memory());
	}


}
