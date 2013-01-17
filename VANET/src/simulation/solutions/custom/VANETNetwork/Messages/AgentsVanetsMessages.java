package simulation.solutions.custom.VANETNetwork.Messages;


import simulation.utils.IntegerPosition;
/**
 * Classe définissant les frames utilisée lors de la communications entre agents
 * @author Wyvern
 *
 */
public class AgentsVanetsMessages {
	
	private int senderID;
	private int receiverID;
	private IntegerPosition positionAgent;
	
	public byte senderType;
	
	public static final byte VOITURE=0;
	public static final byte FEU_DE_SIGNALISATION=1;	
	public static final byte CROISEMENT=2;
	
	public AgentsVanetsMessages(int sender, int receiver, byte senderType, IntegerPosition senderPosition)
	{
		this.senderID=sender;
		this.receiverID=receiver;
		this.positionAgent=senderPosition;
		this.senderType= senderType;
	}
	
	public int getReceiver()
	{
		return this.receiverID;
	}
	
	public int getSender()
	{
		return this.senderID;
	}
	
	
	public IntegerPosition getPositionAgent() {
		return positionAgent;
	}

	public void setPositionAgent(IntegerPosition positionAgent) {
		this.positionAgent = positionAgent;
	}
}
