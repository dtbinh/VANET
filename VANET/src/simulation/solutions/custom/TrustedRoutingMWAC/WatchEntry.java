/**
 * 
 */
package simulation.solutions.custom.TrustedRoutingMWAC;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import simulation.solutions.custom.TrustedRoutingMWAC.Messages.MWACMessage;
import simulation.utils.aDate;

/**
 * @author Anca
 *
 */
public class WatchEntry {
	
	int nodeId; 
	int interactionNo;
	MWACMessage message;
	aDate expireDate;
	
	public WatchEntry(int nodeId, int interactionNo, MWACMessage message,aDate expireDate) {
		super();
		this.nodeId = nodeId;
		this.interactionNo = interactionNo;
		this.message = message;
		this.expireDate = expireDate;
	}

	public int getNodeId() {
		return nodeId;
	}

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}

	public int getInteractionNo() {
		return interactionNo;
	}

	public void setInteractionNo(int interactionNo) {
		this.interactionNo = interactionNo;
	}

	public MWACMessage getMessage() {
		return message;
	}

	public void setMessage(MWACMessage message) {
		this.message = message;
	}

	public aDate getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(aDate expireDate) {
		this.expireDate = expireDate;
	}

	@Override
	public String toString() {
		String DATE_FORMAT = "hh:mm:ss:SSS";
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

		String dateStr = sdf.format(expireDate.getTime());

		return "<tr><td>" + nodeId + "</td><td>" + interactionNo + "</td><td>" + dateStr + "</td><td>" + message.toString() + "</td></tr>"; 
				
	}
	
	
	
	
}
