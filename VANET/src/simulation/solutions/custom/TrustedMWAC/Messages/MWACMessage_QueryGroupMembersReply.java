/**
 * 
 */
package simulation.solutions.custom.TrustedMWAC.Messages;

import java.nio.ByteBuffer;


/**
 * @author Anca
 *
 */
@SuppressWarnings("serial")
public class MWACMessage_QueryGroupMembersReply extends MWACMessage {

	int[] members;
	
	public MWACMessage_QueryGroupMembersReply(int sender, int receiver, byte[] data){
		super(sender, receiver, MWACMessage.msgQUERY_GROUP_MEMBERS_REPLY);
		
		ByteBuffer buff = ByteBuffer.wrap(data);
		int count = data.length / 4;
		
		members = new int[count];
		
		for(int i = 0; i < count; i++)
			members[i] = buff.getInt();
	}
	
	public MWACMessage_QueryGroupMembersReply(int sender, int receiver, int[] members){
		super(sender, receiver, MWACMessage.msgQUERY_GROUP_MEMBERS_REPLY);
		this.members = members;
	}
	
	public byte[] toByteSequence(){
		ByteBuffer buf = ByteBuffer.allocate(4 * members.length);
		
		for (int i = 0; i < members.length; i++)
			buf.putInt(members[i]);
		return super.toByteSequence(buf.array());
	}
	
	public int[] getMembers(){
		return members;
	}
}
