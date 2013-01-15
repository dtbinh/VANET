package simulation.solutions.custom.MWACSocial;

import java.util.LinkedList;
import java.util.ListIterator;

import simulation.solutions.custom.MWAC.Messages.MWACMessage_Data;
import simulation.solutions.custom.MWACSocial.Messages.MWACSocialMessage_Data;
import simulation.utils.aDate;



/**
 *  to help the representative to manage sended messages  in this group
 *  only for representative agent
 */
public class MWACSocialMessageFIFOStack
{


	/** identifier of the next route request */
	private short nextId=0;

	/** list of the managed message */
	private LinkedList<SocialMessageToSendItem> msgList;

	/**
	 * default constructor
	 */
	public MWACSocialMessageFIFOStack()
	{
		this.msgList = new LinkedList<SocialMessageToSendItem>();
	}

	/**
	 * 
	 */
	public ListIterator<SocialMessageToSendItem> listIterator()
	{
		return this.msgList.listIterator();
	}
	
	/**
	 * add a message in the list 
	 * @param msg the sended message 
	 * @return the associated route request identifier
	 */
	public short add(MWACSocialMessage_Data msg)
	{
		short id = this.nextId++;
		this.msgList.add(new SocialMessageToSendItem(id,msg));
		return id;
	}

	/**
	 * is the list empty?
	 * @return true of false
	 */
	public boolean isEmpty()
	{
		return (this.msgList.size()==0);
	}

	/**
	 * remove all item of the list
	 */
	public void removeAll()
	{
		this.msgList.clear();
	}

	/**
	 * return the string representation of the object
	 * @return the string representation
	 */
	public String toString()
	{
		String str="";
		ListIterator<SocialMessageToSendItem> iter=this.msgList.listIterator();
		SocialMessageToSendItem msg;
		while(iter.hasNext())
		{
			msg=iter.next();
			str+=msg.toString()+"\n";
		}
		return str;
	}

	/**
	 * return an HTML string representation of the object (to the spy window)
	 * @return the HTML representation
	 */
	public String toHTML()
	{
		int i=1;
		String str="<B>Message to send</B> ("+this.msgList.size()+")<BR>";
		ListIterator<SocialMessageToSendItem> iter=this.msgList.listIterator();
		str+="<TABLE border=1>";
		str+="<TR><TD>#</TD><TD>Description</TD></TR>";
		SocialMessageToSendItem msg;
		while(iter.hasNext())
		{
			msg=iter.next();
			str+="<TR><TD>"+i+"</TD><TD>"+msg.toString()+"</TD></TR>";
			i++;
		}
		str+="</TABLE>";
		return str;
	}



	
}
