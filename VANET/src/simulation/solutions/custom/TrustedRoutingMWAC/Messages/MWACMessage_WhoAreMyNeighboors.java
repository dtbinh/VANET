package simulation.solutions.custom.TrustedRoutingMWAC.Messages;

import simulation.solutions.custom.TrustedRoutingMWAC.MWACGroupAssistant;

/**
 * MWAC message : message sended to verify or discovers its neighboorhood
 * 
 * @author Jean-Paul Jamont
 */
@SuppressWarnings("serial")
public class MWACMessage_WhoAreMyNeighboors extends MWACMessage_Presentation {

	public MWACMessage_WhoAreMyNeighboors(int sender, int receiver, byte[] data) {
		super(sender, receiver, data);
	}

	public MWACMessage_WhoAreMyNeighboors(int sender, byte role, int[] groups) {
		super(sender, role, groups);
	}

	public String toString() {
		return "WhoAreMyNeighboors message instancied by " + this.getSender()
				+ " : [role=" + this.role + ",group="
				+ MWACGroupAssistant.groupsToString(this.getGroups()) + "]";
	}
}
