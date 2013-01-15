package simulation.solutions.custom.DSR;

import java.util.LinkedList;
import java.util.ListIterator;

import simulation.utils.aDate;

/**
 * manager of already processed route request (for a DSR agent) 
 * Aim: remember all processed frame to not answer more than one time to a route request
 * @author Jean-Paul Jamont
 */
public class DSRAlreadyProcessedRouteRequestManager {

	/** after this delay we consider that the route request cannot be yet received*/
	private static int ms_MAX_ROUTE_REQUEST_DELAY = 10000;
	/** the list of already processed request (AlreadyProcessedRequestItem is a private class )*/
	private LinkedList<AlreadyProcessedRequestItem> list;

	/**
	 * default constructor 
	 */
	public DSRAlreadyProcessedRouteRequestManager()
	{
		this.list=new LinkedList<AlreadyProcessedRequestItem>();
	}

	/**
	 * remove old processed route request
	 */
	public synchronized void verifyAges()
	{
		AlreadyProcessedRequestItem item;
		ListIterator<AlreadyProcessedRequestItem> iter=this.list.listIterator();
		aDate now = new aDate();
		while(iter.hasNext())
		{
			item=iter.next();
			if (now.differenceToMS(item.date)>DSRAlreadyProcessedRouteRequestManager.ms_MAX_ROUTE_REQUEST_DELAY) iter.remove();
		}
	}
	
	/**
	 * allow to know if a route request has already been processed.
	 * a route request is identified by the t-uple (sender,request)
	 * @param sender
	 * @param request
	 * @return
	 */
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
			if (now.differenceToMS(item.date)>DSRAlreadyProcessedRouteRequestManager.ms_MAX_ROUTE_REQUEST_DELAY) iter.remove();
		}

		if(finded)
			return true;
		else
		{
			list.add(new AlreadyProcessedRequestItem(sender,request));
			return false;
		}
	}

	/**
	 * returns a HTML string representation to display this class under a array representation in a spy window
	 * @return a HTML string representation of this object
	 */
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
	
	
	/**
	 * model a already processed route request
	 * @author Jean-Paul Jamont
	 *
	 */
	private class AlreadyProcessedRequestItem
	{
		/** identifier of the sender of the request */
		public int sender;
		/** identifier of the route request */
		public short idRouteRequest;
		/** date of the route request treatment */
		public aDate date;

		/** 
		 * Parameterized constructor
		 * @param sender identifier of the sender of the request
		 * @param idRouteRequest identifier of the route request
		 */
		public AlreadyProcessedRequestItem(int sender,short idRouteRequest)
		{
			this.sender=sender;
			this.idRouteRequest=idRouteRequest;
			this.date=new aDate();
		}
		
		/**
		 * returns a HTML string representation of this item
		 * @return a HTML string representation 
		 */
		public String toHTML()
		{
			return "<TR><TD>"+aDate.msToHHMMSSCCC(this.date.getTimeInMillis())+"</TD><TD>"+this.sender+"</TD><TD>"+this.idRouteRequest+"</TD></TR>";
		}
	}
	

}
