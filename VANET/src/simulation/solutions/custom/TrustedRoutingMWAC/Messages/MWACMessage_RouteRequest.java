package simulation.solutions.custom.TrustedRoutingMWAC.Messages;

import java.nio.ByteBuffer;
import simulation.solutions.custom.TrustedRoutingMWAC.MWACRouteAssistant;

/**
 * MWAC message : route request (sended by a representative agent)
 * 
 * @author Jean-Paul Jamont
 */

@SuppressWarnings("serial")
public class MWACMessage_RouteRequest extends MWACMessage {
	protected int[] route;
	protected short idRequest;

	public MWACMessage_RouteRequest(int sender, int receiver, short idRequest,	int[] route) {
		super(sender, receiver, MWACMessage.msgROUTE_REQUEST);
		this.idRequest = idRequest;
		this.route = route;
	}

	public MWACMessage_RouteRequest(int sender, int receiver, byte[] data) {
		super(sender, receiver, MWACMessage.msgROUTE_REQUEST);
		ByteBuffer buf = ByteBuffer.wrap(data);

		int capacity = (buf.capacity() - 2) / 4;
		if (capacity < 0)
			capacity = 0;
		this.idRequest = buf.getShort();

		this.route = new int[capacity];
		for (int i = 0; i < capacity; i++)
			this.route[i] = buf.getInt();
	}

	public MWACMessage_RouteRequest(int sender, int receiver, short idRequest) {
		this(sender, receiver, idRequest, new int[0]);
	}

	public short getIdRequest() {
		return this.idRequest;
	}

	public int[] getRoute() {
		return this.route;
	}

	public void setRoute(int[] route) {
		this.route = route;
	}

	public byte[] toByteSequence() {
		ByteBuffer res;
		res = ByteBuffer.allocate(2 + 4 * this.route.length);
		res.putShort(this.idRequest);
		for (int i = 0; i < route.length; i++)
			res.putInt(route[i]);
		return super.toByteSequence(res.array());
	}

	public String toString() {
		return "Route request #" + this.idRequest + " instancied by "
				+ this.getSender() + " to "
				+ MWACFrame.receiverIdToString(this.getReceiver())
				+ " Route is " + MWACRouteAssistant.routeToString(this.route);
	}
}
