package simulation.messages.system.custom;

import java.nio.ByteBuffer;

import simulation.messages.system.NotUnderstantableSystemFrameException;
import simulation.messages.system.SystemFrame;

public class PerceivedAgentsInformationRequestSystemFrame extends SystemFrame{

	private final static byte FUNCTION_CODE = 0x01;
	private final static byte SUB_FUNCTION_CODE = 0x02;
	
	public int x;
	public int y;
	public int radius;
	
	
	public PerceivedAgentsInformationRequestSystemFrame( int x, int y, int radius) 
	{
		super(PerceivedAgentsInformationRequestSystemFrame.FUNCTION_CODE, PerceivedAgentsInformationRequestSystemFrame.SUB_FUNCTION_CODE);
		this.x=x;
		this.y=y;
		this.radius=radius;
	}
		
	public PerceivedAgentsInformationRequestSystemFrame(byte[] data) throws NotUnderstantableSystemFrameException {
		super(PerceivedAgentsInformationRequestSystemFrame.FUNCTION_CODE, PerceivedAgentsInformationRequestSystemFrame.SUB_FUNCTION_CODE);
		if(data.length!=4+4+4) throw new NotUnderstantableSystemFrameException(this.functionCode,this.subFunctionCode,"Data size error (size="+(data==null ? "null" : data.length)+"). Data are not constituted by 3 integers");

		ByteBuffer bBuffer = ByteBuffer.wrap(data);
		this.x = bBuffer.getInt();
		this.y = bBuffer.getInt();
		this.radius = bBuffer.getInt();
	}
	
	public byte[] toBytes()
	{
		return super.toBytes(ByteBuffer.allocate(4+4+4).putInt(this.x).putInt(this.y).putInt(this.radius).array());
	}
	
	public String toString()
	{
		return "Function code:"+Byte.toString(this.functionCode)+" Subfunction code:"+Byte.toString(this.subFunctionCode)+" : Information on agent in the environment circle portion (x="+this.x+",y="+y+",radius="+this.radius+") are requested";
	}
}
