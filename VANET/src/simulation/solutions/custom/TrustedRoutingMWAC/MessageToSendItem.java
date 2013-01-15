package simulation.solutions.custom.TrustedRoutingMWAC;

import simulation.solutions.custom.TrustedRoutingMWAC.Messages.MWACMessage_Data;
import simulation.utils.aDate;

/**
 * an encapsuled message
 * 
 * @author Jean-Paul Jamont
 */
public class MessageToSendItem {
	/** date of the message order */
	public aDate date;
	/** id of the msg */
	public short id;
	/** msg to send */
	public MWACMessage_Data msg;

	/**
	 * parametrized constructor
	 * 
	 * @param id
	 *            identifier of the associated route request
	 * @param msg
	 *            the sended message
	 */
	public MessageToSendItem(short id, MWACMessage_Data msg) {
		this.id = id;
		this.msg = msg;
		this.date = new aDate();
	}

	/**
	 * return the message representation
	 * 
	 * @return the message string representation
	 */
	public String toString() {
		return msg.toString();
	}
}