package simulation.messages.system.custom;

import java.nio.ByteBuffer;

import simulation.messages.system.NotUnderstantableSystemFrameException;
import simulation.messages.system.SystemFrame;
import simulation.utils.BytesArray;

public class AgentInformationRequestSystemFrame extends SystemFrame {

	private final static byte FUNCTION_CODE = 0x01;
	private final static byte SUB_FUNCTION_CODE = 0x01;
	
	private int concernedAgentId;

	public AgentInformationRequestSystemFrame(int id) 
	{
		super(AgentInformationRequestSystemFrame.FUNCTION_CODE, AgentInformationRequestSystemFrame.SUB_FUNCTION_CODE);
		this.concernedAgentId=id;
	}

	public AgentInformationRequestSystemFrame(byte[] data) throws NotUnderstantableSystemFrameException {
		super(AgentInformationRequestSystemFrame.FUNCTION_CODE, AgentInformationRequestSystemFrame.SUB_FUNCTION_CODE);
		if(data.length!=4) throw new NotUnderstantableSystemFrameException(this.FUNCTION_CODE,this.SUB_FUNCTION_CODE,"Data size error (size="+(data==null ? "null" : data.length)+"). Data are not constituted by 1 integer");
		this.concernedAgentId = ByteBuffer.wrap(data).getInt();
		// TODO Auto-generated constructor stub
	}

	public int getConcernedAgentId()
	{
		return this.concernedAgentId;
	}
	
	public byte[] toBytes()
	{
		return super.toBytes(ByteBuffer.allocate(4).putInt(this.concernedAgentId).array());
	}
	
	public String toString()
	{
		return "Function code:"+Byte.toString(this.FUNCTION_CODE)+" Subfunction code:"+Byte.toString(this.SUB_FUNCTION_CODE)+" : Information on agent #"+this.concernedAgentId+" are requested";
	}

}
