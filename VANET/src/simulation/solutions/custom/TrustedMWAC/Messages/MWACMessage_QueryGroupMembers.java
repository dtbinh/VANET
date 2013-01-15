/**
 * 
 */
package simulation.solutions.custom.TrustedMWAC.Messages;


/**
 * @author Anca
 *
 */
@SuppressWarnings("serial")
public class MWACMessage_QueryGroupMembers extends MWACMessage {

	public MWACMessage_QueryGroupMembers(int sender, int receiver){
		super(sender,receiver, MWACMessage.msgQUERY_GROUP_MEMBERS);
	}
	
	public  byte[] toByteSequence() {
		return super.toByteSequence();
	}

	public String toString()	{
		return "Query group members message instancied by " + this.getSender()
				+ " to " + this.getReceiver();
	}
}
