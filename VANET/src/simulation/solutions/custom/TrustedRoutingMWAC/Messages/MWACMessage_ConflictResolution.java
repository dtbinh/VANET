package simulation.solutions.custom.TrustedRoutingMWAC.Messages;

import java.nio.ByteBuffer;

/**
 * MWAC message : resolution of a conflict
 * 
 * @author Jean-Paul Jamont
 */

@SuppressWarnings("serial")
public class MWACMessage_ConflictResolution extends MWACMessage {
	private int score;

	public MWACMessage_ConflictResolution(int sender, int receiver, byte[] data) {
		super(sender, receiver, MWACMessage.msgCONFLICT_RESOLUTION);
		ByteBuffer buf = ByteBuffer.wrap(data);
		this.score = buf.getInt();
	}

	public MWACMessage_ConflictResolution(int sender, int receiver, int score) {
		super(sender, receiver, MWACMessage.msgCONFLICT_RESOLUTION);
		this.score = score;
	}

	public byte[] toByteSequence() {
		return super.toByteSequence(ByteBuffer.allocate(5).putInt(score)
				.array());
	}

	public int getScore() {
		return this.score;
	}

	public String toString() {
		return "Conflict resolution message instancied by " + this.getSender()
				+ " to all Representative neighboors  (score=" + this.score
				+ ")";
	}
}
