package simulation.solutions.custom.MWACSocial;

import java.util.LinkedList;
import java.util.ListIterator;

import simulation.utils.aDate;

public class MWACSocialAlreadyProcessedRouteRequestManager {

	private static int ms_MAX_ROUTE_REQUEST_DELAY = 10000;

	private LinkedList<AlreadyProcessedRequestItem> list;

	public MWACSocialAlreadyProcessedRouteRequestManager()
	{
		this.list=new LinkedList<AlreadyProcessedRequestItem>();
	}

	public synchronized void verifyAges()
	{
		AlreadyProcessedRequestItem item;
		ListIterator<AlreadyProcessedRequestItem> iter=this.list.listIterator();
		aDate now = new aDate();
		while(iter.hasNext())
		{
			item=iter.next();
			if (now.differenceToMS(item.date)>MWACSocialAlreadyProcessedRouteRequestManager.ms_MAX_ROUTE_REQUEST_DELAY) iter.remove();
		}
	}

	
	public synchronized boolean isAlreadyProcessed(int sender,short request)
	{
		AlreadyProcessedRequestItem item;
		boolean finded = false;
		ListIterator<AlreadyProcessedRequestItem> iter=this.list.listIterator();
		aDate now = new aDate();
		while(iter.hasNext())
		{
			item=iter.next();
			if(!finded && ((item.sender==sender) && (item.idRouteRequest==request))) finded=true;
			if (now.differenceToMS(item.date)>MWACSocialAlreadyProcessedRouteRequestManager.ms_MAX_ROUTE_REQUEST_DELAY) iter.remove();
		}

		if(finded)
			return true;
		else
		{
			list.add(new AlreadyProcessedRequestItem(sender,request));
			return false;
		}
	}

	public String toHTML()
	{
		String res="<B>Table of already processed request</B> ("+this.list.size()+")<BR>";
		res+="<TABLE border=1>";
		res+="<TR><TD>Date</TD><TD>Sender</TD><TD>Route request id</TD></TR>";
		ListIterator<AlreadyProcessedRequestItem> iter = this.list.listIterator();
		while(iter.hasNext()) res+=iter.next().toHTML();
		res+="</TABLE>";
		return res;
	}
	private class AlreadyProcessedRequestItem
	{
		public int sender;
		public short idRouteRequest;
		public aDate date;

		public AlreadyProcessedRequestItem(int sender,short idRouteRequest)
		{
			this.sender=sender;
			this.idRouteRequest=idRouteRequest;
			this.date=new aDate();
		}
		
		public String toHTML()
		{
			return "<TR><TD>"+aDate.msToHHMMSSCCC(this.date.getTimeInMillis())+"</TD><TD>"+this.sender+"</TD><TD>"+this.idRouteRequest+"</TD></TR>";
		}
	}
	

}
