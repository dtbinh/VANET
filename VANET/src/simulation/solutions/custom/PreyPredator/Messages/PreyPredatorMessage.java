package simulation.solutions.custom.PreyPredator.Messages;

import java.nio.ByteBuffer;

import simulation.messages.Frame;
import simulation.messages.Message;
import simulation.utils.IntegerPosition;

public class PreyPredatorMessage extends Message{

	private static final long serialVersionUID = 1L;
	private int senderID;
	private int receiverID;
	private IntegerPosition pos;
	
	public byte type;
	
	public static final byte HOWl=0;
	public static final byte BLEAT=1;
	public static final byte BITE=2;
	
	public PreyPredatorMessage(int sender, int receiver, byte type,IntegerPosition pos){
		senderID=sender;
		this.receiverID=receiver;
		this.type=type;
		this.pos = pos;
	}
	
	public int getReceiver() {return receiverID;}
	public int getSender() {return senderID;}
	
	public IntegerPosition getPosition(){ return pos;}
	
	public byte[] toByteSequence(){
		if(type==BITE)
			return ByteBuffer.allocate(19).put(type).putInt(this.senderID).putInt(receiverID).putInt(0).putInt(0).array();
		else
			return ByteBuffer.allocate(19).put(type).putInt(this.senderID).putInt(receiverID).putInt(pos.x).putInt(pos.y).array();
	}
}
