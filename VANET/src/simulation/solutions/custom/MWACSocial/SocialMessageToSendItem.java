package simulation.solutions.custom.MWACSocial;

import simulation.solutions.custom.MWAC.Messages.MWACMessage_Data;
import simulation.solutions.custom.MWACSocial.Messages.MWACSocialMessage_Data;
import simulation.utils.aDate;


/**
 * an encapsuled message 
 */
public class SocialMessageToSendItem
{
	/** date of the message order */
	public aDate date;
	/** id of the msg */
	public short id;
	/** msg to send */
	public MWACSocialMessage_Data msg;

	/**
	 * parametrized constructor
	 * @param id identifier of the associated route request
	 * @param msg the sended message 
	 */
	public SocialMessageToSendItem(short id, MWACSocialMessage_Data msg)
	{
		this.id=id;
		this.msg=msg;
		this.date=new aDate();
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