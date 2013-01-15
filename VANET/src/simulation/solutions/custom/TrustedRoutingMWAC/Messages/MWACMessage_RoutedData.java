package simulation.solutions.custom.TrustedRoutingMWAC.Messages;

import java.nio.ByteBuffer;

import simulation.solutions.custom.TrustedRoutingMWAC.MWACRouteAssistant;

/**
 * MWAC message : routed data (data + route : used to transport data between two
 * representative agents)
 * 
 * @author Jean-Paul Jamont
 */
@SuppressWarnings("serial")
public class MWACMessage_RoutedData extends MWACMessage {

	protected String msg;
	int[] route;

	public MWACMessage_RoutedData(MWACMessage_RoutedData msg) {
		this(msg.getSender(), msg.getReceiver(), MWACRouteAssistant.cloneRoute(msg.getRoute()), msg.getMsg());
	}

	public MWACMessage_RoutedData(MWACMessage_Data msg, int[] route) {
		this(msg.getSender(), msg.getReceiver(), route, msg.getMsg());
	}

	public MWACMessage_RoutedData(int sender, int receiver, byte[] data) {
		super(sender, receiver, MWACMessage.msgROUTED_DATA);
		ByteBuffer buf = ByteBuffer.wrap(data);
		short routeSize = buf.getShort();
		this.route = new int[routeSize];
		for (int i = 0; i < routeSize; i++)
			route[i] = buf.getInt();
		this.msg = "";
		while (buf.hasRemaining()) {
			char c = (char) buf.get();
			msg += c;
		}
	}

	public MWACMessage_RoutedData(int sender, int receiver, int[] route,
			String msg) {
		super(sender, receiver, MWACMessage.msgROUTED_DATA);
		this.route = route;
		this.msg = msg;
	}

	public byte[] toByteSequence() {
		char[] ch = msg.toCharArray();
		ByteBuffer buf = ByteBuffer.allocate(2 /* route size */+ 4
				* this.route.length + ch.length);
		buf.putShort((short) this.route.length);
		for (int i = 0; i < this.route.length; i++)
			buf.putInt(this.route[i]);
		for (int i = 0; i < ch.length; i++)
			buf.put((byte) (ch[i]));
		return super.toByteSequence(buf.array());
	}

	public String getMsg() {
		return this.msg;
	}

	public int[] getRoute() {
		return this.route;
	}

	public void setRoute(int[] route) {
		this.route = route;
	}

	public String toString() {
		return "Data Routed Message from " + this.getSender() + " to "
				+ this.getReceiver() + ".  [msg=" + this.msg + ",route="
				+ MWACRouteAssistant.routeToString(this.route) + "]";
	}
}
