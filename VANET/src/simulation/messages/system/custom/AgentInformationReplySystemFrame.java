package simulation.messages.system.custom;

import java.nio.ByteBuffer;

import simulation.messages.system.NotUnderstantableSystemFrameException;
import simulation.messages.system.SystemFrame;

public class AgentInformationReplySystemFrame extends SystemFrame{
	private final static byte FUNCTION_CODE = 0x02;
	private final static byte SUB_FUNCTION_CODE = 0x01;
	
	private AgentInformation agentInformation;
	
	public AgentInformationReplySystemFrame( int id, int x, int y, byte role) {
		super(AgentInformationReplySystemFrame.FUNCTION_CODE, AgentInformationReplySystemFrame.SUB_FUNCTION_CODE);
		// TODO Auto-generated constructor stub
		this.agentInformation=new AgentInformation(id,x,y,role);
	}
	
	public AgentInformationReplySystemFrame( byte[] data) throws NotUnderstantableSystemFrameException {
		super(AgentInformationReplySystemFrame.FUNCTION_CODE, AgentInformationReplySystemFrame.SUB_FUNCTION_CODE);
		if(data.length!=4+4+4+1) throw new NotUnderstantableSystemFrameException(this.FUNCTION_CODE,this.SUB_FUNCTION_CODE,"Data size error (size="+(data==null ? "null" : data.length)+"). Data are not constituted by 3 integers and 1 byte");

		this.agentInformation=new AgentInformation(data);
	}
	
	public AgentInformation getAgentInformation()
	{
		return this.agentInformation;
	}
	public byte[] toBytes()
	{
		return  super.toBytes(this.agentInformation.toBytes());
	}
	
	public String toString()
	{
		return "Function code:"+Byte.toString(this.FUNCTION_CODE)+" Subfunction code:"+Byte.toString(this.SUB_FUNCTION_CODE)+" : "+this.agentInformation.toString();
	}

}
