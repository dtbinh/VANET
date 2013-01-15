package simulation.solutions.custom.TrustedRoutingMWAC.Messages;

import java.nio.ByteBuffer;

import simulation.messages.Message;

@SuppressWarnings("serial")
public class MWACMessage extends Message {

	public final static byte BROADCAST = -1;

	public static boolean USE_TTL = false;

	/** Message sended at the agent birth */
	public final static byte msgINTRODUCTION = 1;
	/**
	 * An agent use this message to present him to neighbor... No response
	 * required
	 */
	public final static byte msgPRESENTATION = 2;
	/** A representative agent manage its role conflict by sending its score */
	public final static byte msgCONFLICT_RESOLUTION = 3;
	/** Like presentation but require a response from surrounding neighbors */
	public final static byte msgWHO_ARE_MY_NEIGHBOORS = 4;
	/** Data message */
	public final static byte msgDATA = 5;
	/** Route request */
	public final static byte msgROUTE_REQUEST = 6;
	/** Route request */
	public final static byte msgTTL_ROUTE_REQUEST = 7;
	/** Route reply */
	public final static byte msgROUTE_REPLY = 8;
	/** Routed data */
	public final static byte msgROUTED_DATA = 9;


	private int receiver;
	private int sender;
	private byte typeMsg;

	public MWACMessage() {
	}

	public MWACMessage(int sender, int receiver, byte typeMsg) {
		this.sender = sender;
		this.receiver = receiver;
		this.typeMsg = typeMsg;
	}

	/**
	 * returns the volume of the message
	 * 
	 * @return the volume of the message (bytes)
	 */
	public static MWACMessage createMessage(byte[] msg) {

		ByteBuffer buf = ByteBuffer.wrap(msg);
		int sender = buf.getInt();
		int receiver = buf.getInt();
		byte typeMsg = buf.get();
		byte[] data = new byte[msg.length - 9];
		for (int i = 0; i < msg.length - 9; i++)
			data[i] = buf.get();

		switch (typeMsg) {
		case MWACMessage.msgINTRODUCTION:
			return new MWACMessage_Introduction(sender, receiver);
		case MWACMessage.msgPRESENTATION:
			return new MWACMessage_Presentation(sender, receiver, data);
		case MWACMessage.msgCONFLICT_RESOLUTION:
			return new MWACMessage_ConflictResolution(sender, receiver, data);
		case MWACMessage.msgWHO_ARE_MY_NEIGHBOORS:
			return new MWACMessage_WhoAreMyNeighboors(sender, receiver, data);
		case MWACMessage.msgDATA:
			return new MWACMessage_Data(sender, receiver, data);
		case MWACMessage.msgROUTE_REQUEST:
			return new MWACMessage_RouteRequest(sender, receiver, data);
		case MWACMessage.msgTTL_ROUTE_REQUEST:
			return new MWACMessage_RouteRequest(sender, receiver, data);
		case MWACMessage.msgROUTE_REPLY:
			return new MWACMessage_RouteReply(sender, receiver, data);
		case MWACMessage.msgROUTED_DATA:
			return new MWACMessage_RoutedData(sender, receiver, data);
		}
		System.out.println("Unknown MWAC message");
		return null;
	}

	public void setType(byte type) {
		this.typeMsg = type;
	}

	public int getType() {
		return this.typeMsg;
	}

	public int getReceiver() {
		return this.receiver;
	}

	public int getSender() {
		return this.sender;
	}

	@Override
	public byte[] toByteSequence() {
		return this.toByteSequence(null);
	}

	public byte[] toByteSequence(byte[] data) {

		if (data == null)
			return ByteBuffer.allocate(8 + 1).putInt(this.sender).putInt(this.receiver).put(this.typeMsg).array();
		else {
			return ByteBuffer.allocate(8 + 1 + data.length).putInt(this.sender).putInt(this.receiver).put(this.typeMsg).put(data).array();
		}
	}

	public String toString() {
		return "MWAC message of " + this.sender;
	}

	public static String debugByteArray(byte[] data) {
		String result = "";
		if (data != null)
			for (int i = 0; i < data.length; i++)
				result += Integer.toString((data[i] & 0xff) + 0x100, 16).substring(1) + " ";
		else
			result = "null";
		return ("[" + result + "]");
	}

	public static void bytesCopy(byte[] dest, byte[] src) {
		for (int i = 0; i < src.length; i++)
			dest[i] = src[i];
	}

	public static byte[] cloneBytes(byte ori[]) {
		byte[] res = new byte[ori.length];
		MWACMessage.bytesCopy(res, ori);
		return res;
	}

}
