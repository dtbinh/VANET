package simulation.solutions.custom.RecMAS.MWAC;

import java.util.LinkedList;
import java.util.ListIterator;

import simulation.solutions.custom.RecMAS.MWAC.Messages.MWACMessage_Data;
import simulation.utils.aDate;



/**
 *  to help the representative to manage sended messages  in this group
 *  only for representative agent
 * @author Jean-Paul Jamont
 */
public class MWACMessageFIFOStack
{


	/** identifier of the next route request */
	private short nextId=0;

	/** list of the managed message */
	private LinkedList<MessageToSendItem> msgList;

	/**
	 * default constructor
	 */
	public MWACMessageFIFOStack()
	{
		this.msgList = new LinkedList<MessageToSendItem>();
	}

	/**
	 * 
	 * @return
	 */
	public short getNextIdRequest()
	{
		return this.nextId++;
	}
	
	/**
	 * 
	 */
	public ListIterator<MessageToSendItem> listIterator()
	{
		return this.msgList.listIterator();
	}
	
	/**
	 * add a message in the list 
	 * @param msg the sended message 
	 * @return the associated route request identifier
	 */
	public short add(MWACMessage_Data msg)
	{
		short id = this.nextId++;
		this.msgList.add(new MessageToSendItem(id,msg));
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
		ListIterator<MessageToSendItem> iter=this.msgList.listIterator();
		MessageToSendItem msg;
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
		ListIterator<MessageToSendItem> iter=this.msgList.listIterator();
		str+="<TABLE border=1>";
		str+="<TR><TD>#</TD><TD>Description</TD></TR>";
		MessageToSendItem msg;
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
