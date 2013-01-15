package simulation.solutions.custom.DSDV.Messages;

import java.nio.ByteBuffer;

/**
 * le message DSDV qui permet de transporter une donn�e 
 * @author Yacine LATTAB & Ouerdia SLAOUTI
 */
@SuppressWarnings("serial")
public class DSDV_Message_Data extends DSDV_Message{

	// la donn�e embarqu�e dans le message de donn�es
	private String msg;

	/**
	 * constructeur pour l'initialisation du message de donn�es
	 * @param sender le destinateur 
	 * @param receiver le destinataire
	 * @param msg la donn�e en String
	 */
	public DSDV_Message_Data(int sender,int receiver,String msg)
	{
		super(sender,receiver,DSDV_Message.msgDATA);
		this.msg=msg;
	}
	
	/**
	 * constructeur pour l'initialisation (reconstitution) du message de donn�es
	 * @param sender le destinateur 
	 * @param receiver le destinataire
	 * @param data la donn�e en Bytes
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
	 * retourne la donn�e transport�e pa le message de donn�es 
	 * @return la donn�e elle meme
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
