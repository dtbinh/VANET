package simulation.solutions.custom.DSDV.Messages;

import java.nio.ByteBuffer;
import simulation.messages.Frame;

/**
 * specifie la trame de l'agent DSDV 
 * @author Yacine LATTAB & Ouerdia SLAOUTI
 */
@SuppressWarnings("serial")
public class DSDV_Frame extends Frame{

	public static String receiverIdToString(int id)
	{
		if (id==DSDV_Frame.BROADCAST)
			return "ALL_ITS_NEIGHBOURS";
		else
			return ""+id;
	}
	
	/**
	 * constructeur pour l'initialisation de la trame DSDV
	 * @param sender le destinateur de la trame DSDV
	 * @param receiver le destinataire de la trame DSDV
	 * @param msg le message DSDV à embarquer dans la trame
	 */
	public DSDV_Frame(int sender,int receiver,DSDV_Message msg)
	{
		super(sender,receiver,msg);
	}
	
	/**
	 * constructeur pour l'initialisation de la trame DSDV
	 * @param sender le destinateur de la trame DSDV
	 * @param receiver le destinataire de la trame DSDV
	 * @param msg le message DSDV en une suite de bytes 
	 */
	public DSDV_Frame(int sender,int receiver,byte[] msg)
	{
		super(sender,receiver,msg);
	}

	/**
	 * reconstitue la trame DSDV à partir d'une suite de bytes 
	 * @param frame suite de bytes
	 * @return la trame DSDV reconstruite
	 */
	public static DSDV_Frame create_DSDV_Frame(byte[] frame)
	{
		ByteBuffer buf=ByteBuffer.wrap(frame);
		int sender=buf.getInt();
		int receiver=buf.getInt();
		byte[] data = new byte[frame.length-8];
		for(int i=0;i<frame.length-8;i++) data[i]=buf.get();
		
		return new DSDV_Frame(sender,receiver,data);
	}
	
	/**
	 * retourne la representation de la trame en String
	 * @return representation en String 
	 */
	public String toString()
	{	
		int receiver = super.getReceiver();
		if (receiver==-1)
			return "Frame from "+super.getSender()+" to all surrounding neighboors. Message is "+DSDV_Message.createMessage(super.getData()).toString();
		else
			try {
				return "Frame from "+super.getSender()+" to "+super.getReceiver()+". Message is  "+DSDV_Message.createMessage(super.getData()).toString();
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
