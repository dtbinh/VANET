package simulation.solutions.custom.RecMAS.RecursiveAgent;

import java.awt.Color;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import simulation.solutions.custom.RecMAS.RecursiveAgent.Messages.Label;
import simulation.solutions.custom.RecMAS.RecursiveAgent.Messages.RecMASMessage;
import simulation.solutions.custom.RecMAS.RecursiveAgent.Messages.RecMASMessage_msgExternSystemMessageTransport;
import simulation.solutions.custom.RecMAS.RecursiveAgent.Messages.RecMASMessage_msgInternSystemMessageTransport;
import simulation.utils.Log;
import simulation.utils.aDate;

/**
 * Already processed ROUTE_REQUEST manager 
 * @author Jean-Paul Jamont
 */
public class RecMASAlreadyProcessedLabelManager {

	private static byte INTERNAL_LIST = 0x00;
	private static byte EXTERNAL_LIST = 0x01;


	private  int ms_LABEL_MEMORIZATION_DELAY = 15000;

	public int nextLabelForInternalTransportMessage = 0;

	private LinkedList<AlreadyProcessedRequestItem> internSystemMessagelist;
	private LinkedList<AlreadyProcessedRequestItem> externSystemMessagelist;


	public RecMASAlreadyProcessedLabelManager()
	{
		this.internSystemMessagelist=new LinkedList<AlreadyProcessedRequestItem>();
		this.externSystemMessagelist=new LinkedList<AlreadyProcessedRequestItem>();
	}
	public RecMASAlreadyProcessedLabelManager(int delay_in_ms)
	{
		this();
		this.ms_LABEL_MEMORIZATION_DELAY=delay_in_ms;
	}

	public synchronized Label getNextAvailableLabel()
	{
		return new Label(this.nextLabelForInternalTransportMessage++);
	}

	public synchronized void verifyAges()
	{
		AlreadyProcessedRequestItem item;

		for(int indexList=0;indexList<2;indexList++)
		{
			ListIterator<AlreadyProcessedRequestItem> iter=null;
			if(indexList==0)
				this.internSystemMessagelist.listIterator();
			else
				this.externSystemMessagelist.listIterator();

			long now = aDate.getCurrentTimeInMS();
			while(iter.hasNext())
			{
				item=iter.next();
				if (now-item.date>this.ms_LABEL_MEMORIZATION_DELAY) iter.remove();
			}
		}
	}



	public synchronized boolean isAlreadyProcessed(RecMASMessage msg)
	{
		boolean isIntern = (msg instanceof RecMASMessage_msgInternSystemMessageTransport);
		long now = new aDate().getTimeInMillis();

		
		AlreadyProcessedRequestItem item;
		boolean finded = false;
		ListIterator<AlreadyProcessedRequestItem> iter=null;

		int msgSender = msg.getSender();
		int msgReceiver = msg.getReceiver();
		int msgLabel = (isIntern? ((RecMASMessage_msgInternSystemMessageTransport) msg).getLabel() : ((RecMASMessage_msgExternSystemMessageTransport) msg).getLabel()); 
	
		
		if(isIntern) 
			iter=this.internSystemMessagelist.listIterator(); 
		else 
			iter=this.externSystemMessagelist.listIterator();
		
		while(iter.hasNext())
		{
			item=iter.next();
			if(!finded && ((item.sender==msgSender) && (item.label==msgLabel))) 
			{
				//MICHEL  Log.println("isAlreadyProcessed("+msg+") renvoie VRAI car pas une instance de SystemMessageTransportAlreadyProcessedRequestItem",Color.PINK);
				item.date=aDate.getCurrentTimeInMS();
				finded=true;
			/*
			  	if(item instanceof SystemMessageTransportAlreadyProcessedRequestItem)
			 
				{
					SystemMessageTransportAlreadyProcessedRequestItem tItem = (SystemMessageTransportAlreadyProcessedRequestItem) item;
					if(tItem.dest==RecMASMessage.BROADCAST || tItem.dest==destId)
					{
						Log.println("isAlreadyProcessed("+msg+") renvoie VRAI car trouve ("+tItem.dest+"=="+RecMASMessage.BROADCAST+" || "+tItem.dest+"=="+destId+")",Color.PINK);
						tItem.date=aDate.getCurrentTimeInMS();
						finded=true;
					}
					else
					{
						Log.println("isAlreadyProcessed("+msg+") renvoie FAUX car trouve ("+tItem.dest+"=="+RecMASMessage.BROADCAST+" || "+tItem.dest+"=="+destId+")",Color.PINK);
						iter.remove();
						list.add(new SystemMessageTransportAlreadyProcessedRequestItem(sender,label,destId));
						return false;
					}
				}
				else
				{
					Log.println("isAlreadyProcessed("+msg+") renvoie VRAI car pas une instance de SystemMessageTransportAlreadyProcessedRequestItem",Color.PINK);
					item.date=aDate.getCurrentTimeInMS();
					finded=true;
				}
				*/

			}
			if (now-item.date>this.ms_LABEL_MEMORIZATION_DELAY) iter.remove();
		}

		if(finded)
			return true;
		else
		{
			//this.list.add(new SystemMessageTransportAlreadyProcessedRequestItem(sender,label,receiver));
			if(isIntern)
				this.internSystemMessagelist.add(new AlreadyProcessedRequestItem(msgSender,msgLabel));
			else
				this.externSystemMessagelist.add(new AlreadyProcessedRequestItem(msgSender,msgLabel));		
			return false;
		}
	}

	/*
	public synchronized boolean isAlreadyProcessed(int sender,int label,int receiver,int destId)
	{
		AlreadyProcessedRequestItem item;
		boolean finded = false;
		ListIterator<AlreadyProcessedRequestItem> iter=this.list.listIterator();
		long now = new aDate().getTimeInMillis();
		while(iter.hasNext())
		{
			item=iter.next();
			if(!finded && ((item.sender==sender) && (item.label==label))) 
			{
				if(item instanceof SystemMessageTransportAlreadyProcessedRequestItem)
				{
					SystemMessageTransportAlreadyProcessedRequestItem tItem = (SystemMessageTransportAlreadyProcessedRequestItem) item;
					if(tItem.dest==RecMASMessage.BROADCAST || tItem.dest==destId)
					{
						Log.println("isAlreadyProcessed(sender="+sender+",label="+label+",receiver="+receiver+",destId="+destId+") renvoie VRAI car trouve ("+tItem.dest+"=="+RecMASMessage.BROADCAST+" || "+tItem.dest+"=="+destId+")",Color.PINK);
						tItem.date=aDate.getCurrentTimeInMS();
						finded=true;
					}
					else
					{
						Log.println("isAlreadyProcessed(sender="+sender+",label="+label+",receiver="+receiver+",destId="+destId+") renvoie FAUX car trouve ("+tItem.dest+"=="+RecMASMessage.BROADCAST+" || "+tItem.dest+"=="+destId+")",Color.PINK);
						iter.remove();
						list.add(new SystemMessageTransportAlreadyProcessedRequestItem(sender,label,destId));
						return false;
					}
				}
				else
				{
					Log.println("isAlreadyProcessed(sender="+sender+",label="+label+",receiver="+receiver+",destId="+destId+") renvoie VRAI car pas une instance de SystemMessageTransportAlreadyProcessedRequestItem",Color.PINK);
					item.date=aDate.getCurrentTimeInMS();
					finded=true;
				}

			}
			if (now-item.date>this.ms_LABEL_MEMORIZATION_DELAY) iter.remove();
		}

		if(finded)
			return true;
		else
		{
			this.list.add(new SystemMessageTransportAlreadyProcessedRequestItem(sender,label,receiver));
			return false;
		}
	}
	 */

	/*
	 * public synchronized boolean isAlreadyProcessed(int sender,int label)

	{
		AlreadyProcessedRequestItem item;
		boolean finded = false;
		ListIterator<AlreadyProcessedRequestItem> iter=this.list.listIterator();
		long now = aDate.getCurrentTimeInMS();
		while(iter.hasNext())
		{
			item=iter.next();
			if(!finded && ((item.sender==sender) && (item.label==label))) finded=true;
			if (now-item.date>this.ms_LABEL_MEMORIZATION_DELAY) iter.remove();
		}

		if(finded)
			return true;
		else
		{
			list.add(new AlreadyProcessedRequestItem(sender,label));
			return false;
		}
	}
	 */
	public String toHTML()
	{
		ListIterator<AlreadyProcessedRequestItem> iter =null;
		String res="<B>Table of already processed labeled internal and external transport message</B><BR>";
		res+="<TABLE border=1>";
		res+="<TR><TD>Date</TD><TD>Sender</TD><TD>Internal transport message label</TD></TR>";
		iter = this.internSystemMessagelist.listIterator();
		while(iter.hasNext()) res+=iter.next().toHTML();
		res+="</TABLE>";

		res+="<TABLE border=1>";
		res+="<TR><TD>Date</TD><TD>Sender</TD><TD>External transport message label</TD></TR>";
		iter = this.externSystemMessagelist.listIterator();
		while(iter.hasNext()) res+=iter.next().toHTML();
		res+="</TABLE>";


		return res;
	}

	public String toString()
	{
		Iterator<AlreadyProcessedRequestItem> iter = null;
		String res="(Internal ("+this.internSystemMessagelist.size()+") : ";
		iter=this.internSystemMessagelist.iterator();
		while(iter.hasNext()) res+=iter.next().toString();
		res+="     External  ("+this.externSystemMessagelist.size()+") : ";
		iter=this.externSystemMessagelist.iterator();
		while(iter.hasNext()) res+=iter.next().toString();
		return res+")";
	}




	private class AlreadyProcessedRequestItem 
	{
		public int sender;
		public int label;
		public long date;


		public AlreadyProcessedRequestItem(int sender,int label)
		{
			this.sender=sender;
			this.label=label;
			this.date=aDate.getCurrentTimeInMS();
		}

		public String toHTML()
		{
			return "<TR><TD>"+aDate.msToHHMMSSCCC(this.date)+"</TD><TD>"+this.sender+"</TD><TD>"+this.label+"</TD></TR>";
		}

		public String toString()
		{
			return new String("(sender="+this.sender+",label="+this.label+",date="+this.date+")");
		}
	}



	private class SystemMessageTransportAlreadyProcessedRequestItem extends AlreadyProcessedRequestItem {

		public int dest;



		public SystemMessageTransportAlreadyProcessedRequestItem(int sender,int label,int dest)
		{
			super(sender,label);
			this.dest=dest;
		}

		public String toString()
		{
			return "(sender="+this.sender+",dest="+this.dest+",label="+this.label+",date="+this.date+")";
		}
	}

}
