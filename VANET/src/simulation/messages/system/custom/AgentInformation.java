package simulation.messages.system.custom;

import java.nio.ByteBuffer;

public class AgentInformation {

	public int id;
	public int x;
	public int y;
	public byte role;
	
	public AgentInformation(byte[] data)
	{
	ByteBuffer bBuffer = ByteBuffer.wrap(data);
	this.id = bBuffer.getInt();
	this.x = bBuffer.getInt();
	this.y = bBuffer.getInt();
	this.role = bBuffer.get();
	}
	
	public AgentInformation(int id,int x, int y,byte role)
	{
		this.id=id;
		this.x=x;
		this.y=y;
		this.role=role;
	}
	
	public byte[] toBytes()
	{
		return ByteBuffer.allocate(4+4+4+1).putInt(id).putInt(x).putInt(y).put(role).array();
	}
	
	public String toString()
	{
		return "Agent #"+this.id+" is in (x="+this.x+",y="+this.y+"). Its role is "+this.role;
	}
}
