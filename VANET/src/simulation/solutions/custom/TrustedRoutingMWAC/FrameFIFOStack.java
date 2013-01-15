package simulation.solutions.custom.TrustedRoutingMWAC;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

import simulation.solutions.custom.TrustedRoutingMWAC.Messages.*;


/**
 * a FIFO stack of frame (used to memorize received frame)
 * 
 * @author Jean-Paul Jamont
 */
public class FrameFIFOStack {
	private LinkedList<MWACFrame> stack;


	public FrameFIFOStack() {
		this.stack = new LinkedList<MWACFrame>();
	}

	public synchronized void push(MWACFrame f) {
		this.stack.add(f);
	}

	public synchronized MWACFrame pop() {
		try {
			MWACFrame res = this.stack.getFirst();
			this.stack.removeFirst();
			return res;

		} catch (Exception e) {
			return null;
		}
	}

	public ArrayList<TrustedTripletIdRoleGroup> removeConflictResolutionMessageInQueue() {
		ArrayList<TrustedTripletIdRoleGroup> lst = new ArrayList<TrustedTripletIdRoleGroup>();
		ListIterator<MWACFrame> iter = this.stack.listIterator();
		MWACMessage msg;
		while (iter.hasNext()) {
			msg = MWACMessage.createMessage(iter.next().getData());
			if (msg.getType() == MWACMessage.msgCONFLICT_RESOLUTION)
				lst.add(new TrustedTripletIdRoleGroup(msg.getSender(),	TrustedRoutingMWACAgent.roleREPRESENTATIVE, msg.getSender()));
		}
		return lst;

	}

	public boolean isEmpty() {
		return (this.stack.size() == 0);
	}

	public synchronized void removeAll() {
		this.stack.clear();
	}

}