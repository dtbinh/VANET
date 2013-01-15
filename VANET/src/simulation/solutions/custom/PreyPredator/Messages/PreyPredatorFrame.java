package simulation.solutions.custom.PreyPredator.Messages;

import java.nio.ByteBuffer;

import simulation.messages.Frame;
import simulation.messages.Message;
import simulation.solutions.custom.DSR.Messages.DSRFrame;
import simulation.solutions.custom.DSR.Messages.DSRMessage;
import simulation.utils.IntegerPosition;

public class PreyPredatorFrame extends Frame {
	
	public PreyPredatorFrame(int sender, int receiver,byte[] data){
		super(sender,receiver,data);
	}
	
	public PreyPredatorFrame(int sender, int receiver,Message msg){
		super(sender,receiver,msg);
	}
	
	public Message getMessage(){
		ByteBuffer buffer = ByteBuffer.wrap(this.getData());
		byte type= buffer.get();
		if(type == PreyPredatorMessage.BITE)
			return new PreyPredatorMessage(buffer.getInt(), buffer.getInt(), type, null);
		else if(type == PreyPredatorMessage.BLEAT)
			return new PreyPredatorMessage(buffer.getInt(), buffer.getInt(), type, new IntegerPosition(buffer.getInt(), buffer.getInt()));
		else if(type == PreyPredatorMessage.HOWl)
			return new PreyPredatorMessage(buffer.getInt(), buffer.getInt(), type, new IntegerPosition(buffer.getInt(), buffer.getInt()));
		System.out.println("Error in the encapsulation of the frame data");
		return null;
	}
}
