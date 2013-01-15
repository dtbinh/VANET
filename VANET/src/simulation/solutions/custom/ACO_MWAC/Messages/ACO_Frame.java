package simulation.solutions.custom.ACO_MWAC.Messages;


import java.nio.ByteBuffer;
import simulation.messages.Frame;


/**
 * ACO_MWAC specialized frame 
 * @author Yacine LATTAB & Ouerdia SLAOUTI
 */
@SuppressWarnings("serial")
public class ACO_Frame extends Frame{
	
	public static String receiverIdToString(int id)	{
		switch(id){
			case BROADCAST: 	return "ALL_CLOSE_AGENT";
			default: 			return ""+id;
		}
	}
	
	public static ACO_Frame create_ACO_Frame(byte[] frame)
	{
		ByteBuffer buf=ByteBuffer.wrap(frame);
		int sender=buf.getInt();
		int receiver=buf.getInt();
		byte[] data = new byte[frame.length-8];
		for(int i=0;i<frame.length-8;i++) data[i]=buf.get();
		
		return new ACO_Frame(sender,receiver,data);
	}
	
	
	public  ACO_Frame(int sender, int receiver, ACO_Message msg){
		super(sender,receiver,msg);
	}
	

	public  ACO_Frame(int sender,int receiver,byte[] msg){
		super(sender,receiver,msg);
	}
	
	
	//renvoi vrai si la trame est de type ACO, false sinon
	public static synchronized Boolean IsAnAcoFrame (Frame frame){
		
		ByteBuffer buf=ByteBuffer.wrap(frame.toByteSequence());
		
		//extraire les quatres premiers champs
		for (int i=0; i<4; i++) buf.getInt();
		
		// get the type of the message
		byte typeMsg=buf.get();
		
		if (typeMsg > 10)  return true; 
		else return false;
	}
	
	
	public String toString()
	{	
		int receiver = super.getReceiver();
		if (receiver==-1)
			return "Frame from "+super.getSender()+" to all surrounding neighboors. Message is "+ACO_Message.createMessage(super.getData()).toString();
		else
			try
		{
				return "Frame from "+super.getSender()+" to "+super.getReceiver()+". Message is  "+ACO_Message.createMessage(super.getData()).toString();
		}
		catch(Exception e)
		{
			System.out.println("SENDER="+super.getSender()+"  DEST="+super.getReceiver());
			System.out.println("MESSAGE="+super.getData());
			e.printStackTrace();
			System.out.flush();
			System.exit(-1);
			return null;
		}
	}

}
