package simulation.solutions.custom.MWACSocial.Messages;

import java.nio.ByteBuffer;

public class MWACSocialMessage_ConflictResolution extends MWACSocialMessage
{
	private int score;


	public MWACSocialMessage_ConflictResolution(int sender,int receiver,byte[] data)
	{
		super(sender,receiver,MWACSocialMessage.msgCONFLICT_RESOLUTION);
		ByteBuffer buf=ByteBuffer.wrap(data);
		this.score=buf.getInt();
	}

	public MWACSocialMessage_ConflictResolution(int sender,int receiver,int score)
	{
		super(sender,receiver,MWACSocialMessage.msgCONFLICT_RESOLUTION);
		this.score=score;
	}


	public  byte[] toByteSequence() 
	{
		return super.toByteSequence(ByteBuffer.allocate(5).putInt(score).array());
	}


	public int getScore()
	{
		return this.score;
	}

	public String toString()
	{
		return "Conflict resolution message instancied by "+this.getSender()+" to all Representative neighboors  (score="+this.score+")";
	}
}
