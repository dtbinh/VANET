package simulation.solutions.custom.DSDV.Messages;

import java.nio.ByteBuffer;

/**
 * le message DSDV qui permet de transporter une donnée 
 * @author Yacine LATTAB & Ouerdia SLAOUTI
 */
@SuppressWarnings("serial")
public class DSDV_Message_Data extends DSDV_Message{

	// la donnée embarquée dans le message de données
	private String msg;

	/**
	 * constructeur pour l'initialisation du message de données
	 * @param sender le destinateur 
	 * @param receiver le destinataire
	 * @param msg la donnée en String
	 */
	public DSDV_Message_Data(int sender,int receiver,String msg)
	{
		super(sender,receiver,DSDV_Message.msgDATA);
		this.msg=msg;
	}
	
	/**
	 * constructeur pour l'initialisation (reconstitution) du message de données
	 * @param sender le destinateur 
	 * @param receiver le destinataire
	 * @param data la donnée en Bytes
	 */
	public DSDV_Message_Data(int sender,int receiver,byte[] data)
	{
		super(sender,receiver,DSDV_Message.msgDATA);

		ByteBuffer buf=ByteBuffer.wrap(data);
		this.msg="";
		char c;
		while(buf.hasRemaining())
		{
			c=(char)buf.get();
			msg+=c;
		}
	}

	/**
	 * retourne la donnée transportée pa le message de données 
	 * @return la donnée elle meme
	 **/
	public String getMsg()
	{
		return msg;
	}

	/**
	 * retourne la representation du message en une suite de bytes  
	 * @return representation en bytes
	 **/
	public  byte[] toByteSequence() 
	{
		char[] ch=msg.toCharArray();
		ByteBuffer buf=ByteBuffer.allocate(ch.length);
		for(int i=0;i<ch.length;i++) buf.put((byte)(ch[i]));
		
		return super.toByteSequence(buf.array());
	}

	/**
	 * retourne la representation du message en String  
	 * @return representation en String 
	 **/
	public String toString()
	{
		return "Data Message instancied by "+this.getSender()+" to "+this.getReceiver()+" Data is '"+this.getMsg()+"'";
	}
}
