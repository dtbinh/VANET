package simulation.messages.system.custom;

import java.nio.ByteBuffer;
import java.util.Vector;

import simulation.messages.system.NotUnderstantableSystemFrameException;
import simulation.messages.system.SystemFrame;

public class PerceivedAgentsInformationReplySystemFrame extends SystemFrame{
	
	private final static byte FUNCTION_CODE = 0x02;
	private final static byte SUB_FUNCTION_CODE = 0x02;
	
	private Vector<AgentInformation> agentsInformation;
	
	public PerceivedAgentsInformationReplySystemFrame() {
		super(PerceivedAgentsInformationReplySystemFrame.FUNCTION_CODE, PerceivedAgentsInformationReplySystemFrame.SUB_FUNCTION_CODE);
		this.agentsInformation=new Vector<AgentInformation>();

	}
	public PerceivedAgentsInformationReplySystemFrame( byte[] data) throws NotUnderstantableSystemFrameException 
	{
		this();
		if(data.length%(4+4+4+1)!=0) throw new NotUnderstantableSystemFrameException(this.functionCode,this.subFunctionCode,"Data size error (size="+(data==null ? "null" : data.length)+"). Data are not constituted by packet of 13 bytes (4 integers + 1 byte)");
		ByteBuffer bBuffer = ByteBuffer.wrap(data);
		for(int i=0;i<data.length/(4+4+4+1);i++) this.add(bBuffer.getInt(),bBuffer.getInt(),bBuffer.getInt(),bBuffer.get());
	}
	public void add(AgentInformation agInfo)
	{
		this.agentsInformation.add(agInfo);
	}
	public void add(int id, int x, int y, byte role)
	{
		this.add(new AgentInformation(id,x,y,role));
	}
	public AgentInformation getAgentInformation(int index)
	{
		return this.agentsInformation.get(index);
	}
	public int getNumberOfAgentInformation()
	{
		return this.agentsInformation.size();
	}

	public byte[] toBytes()
	{
		ByteBuffer bBuffer = ByteBuffer.allocate((4+4+4+1)*this.agentsInformation.size());
		for(int i=0;i<this.agentsInformation.size();i++) bBuffer.put(this.agentsInformation.get(i).toBytes());
		return super.toBytes(bBuffer.array());
	}

	public String toString()
	{
		String res = "Function code:"+Byte.toString(this.functionCode)+" Subfunction code:"+Byte.toString(this.subFunctionCode)+". Informations on agents in the perception area are : \n";
		if(this.agentsInformation.size()==0)
			res+="        No agents";
		else
			for(int i=0;i<this.agentsInformation.size();i++) res+="        "+this.agentsInformation.get(i).toString()+"\n";
		return res;
	}
}
