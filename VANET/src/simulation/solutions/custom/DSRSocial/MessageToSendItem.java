package simulation.solutions.custom.DSRSocial;

import simulation.solutions.custom.DSRSocial.Messages.DSRSocialMessage_Data;
import simulation.utils.aDate;


/**
 * an encapsuled message 
 */
public class MessageToSendItem
{
	/** date of the message order */
	public aDate date;
	/** id of the msg */
	public short id;
	/** msg to send */
	public DSRSocialMessage_Data msg;

	/**
	 * parametrized constructor
	 * @param id identifier of the associated route request
	 * @param msg the sended message 
	 */
	public MessageToSendItem(short id, DSRSocialMessage_Data msg)
	{
		this.id=id;
		this.msg=msg;
		this.date=new aDate();
	}


	public String toHTML()
	{	
		return "<TR><TD>"+date+"</TD><TD>"+id+"</TD><TD>"+msg.toString()+"</TD></TR>";
	}

	
	/**
	 * return the message representation 
	 * @return the message string representation
	 */
	public String toString()
	{
		return msg.toString();
	}
}