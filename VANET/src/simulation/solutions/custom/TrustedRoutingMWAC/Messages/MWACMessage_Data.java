package simulation.solutions.custom.TrustedRoutingMWAC.Messages;

import java.nio.ByteBuffer;

/**
 * MWAC message : transport of data
 * 
 * @author Jean-Paul Jamont
 */

@SuppressWarnings("serial")
public class MWACMessage_Data extends MWACMessage {
	protected String msg;

	public MWACMessage_Data(int sender, int receiver, byte[] data) {
		super(sender, receiver, MWACMessage.msgDATA);
		ByteBuffer buf = ByteBuffer.wrap(data);
		this.msg = "";
		while (buf.hasRemaining()) {
			char c = (char) buf.get();
			msg += c;
		}
	}

	public MWACMessage_Data(int sender, int receiver, String msg) {
		super(sender, receiver, MWACMessage.msgDATA);
		this.msg = msg;
	}

	public byte[] toByteSequence() {
		char[] ch = msg.toCharArray();
		ByteBuffer buf = ByteBuffer.allocate(ch.length);
		for (int i = 0; i < ch.length; i++)
			buf.put((byte) (ch[i]));
		return super.toByteSequence(buf.array());
	}

	public String getMsg() {
		return this.msg;
	}

	public String toString() {
		return "Data Message from " + this.getSender() + " to "
				+ this.getReceiver() + ".  [msg=" + this.msg + "]";
	}
}
