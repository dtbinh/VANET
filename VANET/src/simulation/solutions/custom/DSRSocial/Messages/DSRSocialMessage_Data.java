package simulation.solutions.custom.DSRSocial.Messages;

import java.nio.ByteBuffer;

import simulation.solutions.custom.DSR.Route.DSRRouteAssistant;


public class DSRSocialMessage_Data extends DSRSocialMessage{

	private String msg;
	int[] route;
	
//	public DSRMessage_Data(int sender,int receiver,DSRRoute route,String msg)
//	{
//		//super(sender,receiver,DSRMessage.DATA,route);
//		//this.msg=msg;
//	}
	
	public DSRSocialMessage_Data(int sender,int receiver,int[] route,String message)
	{
		super(sender,receiver,DSRSocialMessage.DATA);
		this.route=route;
		this.msg=message;
	}
	
	
	public DSRSocialMessage_Data(int sender,int receiver,byte[] data)
	{
		super(sender,receiver,DSRSocialMessage.DATA);

		ByteBuffer buf=ByteBuffer.wrap(data);
		short routeSize = buf.getShort();
		this.route=new int[routeSize];
		for(int i=0;i<routeSize;i++) route[i]=buf.getInt();
		this.msg="";
		while(buf.hasRemaining())
		{
			char c=(char)buf.get();
			msg+=c;
		}

	}
	
	public String getMessage()
	{
		return msg;
	}
	

	public int[] getRoute()
	{
		return this.route;
	}

	public void setRoute(int[] route)
	{
		this.route=route;
	}


	public  byte[] toByteSequence() 
	{
		char[] ch=msg.toCharArray();
		ByteBuffer buf=ByteBuffer.allocate(2 /* route size */+4*this.route.length+ch.length);
		buf.putShort((short)this.route.length);
		for(int i=0;i<this.route.length;i++) buf.putInt(this.route[i]);
		for(int i=0;i<ch.length;i++) buf.put((byte)(ch[i]));
		return super.toByteSequence(buf.array());
	}
	

	public String toString()
	{
		return "Data message instancied by "+this.getSender()+" to "+DSRSocialFrame.receiverIdToString(this.getReceiver())+" Route is "+DSRRouteAssistant.toString(this.route)+" Data is '"+this.getMessage()+"'";
	}


}
