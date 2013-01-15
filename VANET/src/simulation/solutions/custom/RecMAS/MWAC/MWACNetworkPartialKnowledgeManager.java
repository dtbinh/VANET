package simulation.solutions.custom.RecMAS.MWAC;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Vector;

/**
 * Network partial knowledge manager 
 * Manage the route fragment and the agent/group well known association
 * @author Jean-Paul Jamont
 */
public class MWACNetworkPartialKnowledgeManager {

	public static int UNDEFINED_ID = -1;

	private Vector<GroupAssociation> idGroupAssociation;
	private Vector<int[]> routes;
	private Vector<RouteRequestAndReceiverAssociationItem> routeRequestAndReceiverAssociation;

	public MWACNetworkPartialKnowledgeManager()
	{
		this.idGroupAssociation=new Vector<GroupAssociation>();
		this.routes=new Vector<int[]>();
		this.routeRequestAndReceiverAssociation=new Vector<RouteRequestAndReceiverAssociationItem>();
	}

	public int[] getDataMessageRoute(int dest)
	{
		boolean find=false;
		int[] route=null;
		int repr=group(dest);
		if (repr==MWACNetworkPartialKnowledgeManager.UNDEFINED_ID) return null;
		System.out.println(repr+" est le représentant du message a envoyer");
		ListIterator<int[]> iter=this.routes.listIterator();
		while(!find && iter.hasNext()) 
		{
			route=iter.next();
			find=MWACRouteAssistant.contains(route, repr);
		}

		if(find) 
		{
			System.out.println("!J'ai trouvé une route "+MWACRouteAssistant.routeToString(route));
			find=false;
			int i=0;
			//System.out.println("Je vais rentré dans la boucle");
			while(!find && i<route.length) find=(route[i++]==repr);
			//System.out.println("Je sors de la boucle");
			route= MWACRouteAssistant.subRoute(route,i);
			//System.out.println("Je fais la nouvelle route");
			//System.out.println("La route est devenue "+MWACRouteAssistant.routeToString(route));
			System.out.flush();
			return route;
		}
		else
		{
			
			System.out.println("Je N'ai PAS trouvé une route");
			return null;
		}

	}

	public int getRouteRequestAssociatedReceiver(int routeRequestId)
	{
		RouteRequestAndReceiverAssociationItem item;
		ListIterator<RouteRequestAndReceiverAssociationItem> iter =  routeRequestAndReceiverAssociation.listIterator();

		while(iter.hasNext())
		{
			item=iter.next();
			if(item.routeRequest==routeRequestId) 
				{
				int sender = item.sender;
				iter.remove();
				return sender;
				}
		}
		
		return MWACNetworkPartialKnowledgeManager.UNDEFINED_ID;
	}

	public void addRouteRequestAndReceiverAssociation(int routeRequestId,int senderOfTheMessage)
	{
		this.routeRequestAndReceiverAssociation.add(new RouteRequestAndReceiverAssociationItem(routeRequestId,senderOfTheMessage));
	}

	public void addRoute(int[] route)
	{
		this.routes.add(route);
	}

	public void addIdGroupAssociation(int idRepresentative, int member)
	{
		boolean find=false;
		GroupAssociation grp;
		ListIterator<GroupAssociation> iter = this.idGroupAssociation.listIterator();

		while(!find && iter.hasNext()) 
		{
			grp=iter.next();
			find=(grp.representative==idRepresentative);
			if(find) grp.add(member);
		}

		if(!find) this.idGroupAssociation.add(new GroupAssociation(idRepresentative,member));

	}

	public int group(int id)
	{
		GroupAssociation item;
		ListIterator<GroupAssociation> iter = this.idGroupAssociation.listIterator();

		while(iter.hasNext())
		{
			item=iter.next();
			if(item.contains(id)) return item.representative;
		}

		return MWACNetworkPartialKnowledgeManager.UNDEFINED_ID;
	}


	public String toHTML()
	{
		String res="";

		res="<B>Group association</B> ("+this.idGroupAssociation.size()+")<BR>";
		res+="<TABLE border=1>";
		res+="<TR><TD>Group</TD><TD>Members</TD></TR>";
		for(int i=0;i<this.idGroupAssociation.size();i++) res+=this.idGroupAssociation.get(i).toHTML();
		res+="</TABLE>";

		res+="<BR>";

		res+="<B>Route fragment</B> ("+this.routes.size()+")<BR>";
		res+="<TABLE border=1>";
		for(int i=0;i<this.routes.size();i++) res+="<TR><TD>"+MWACRouteAssistant.routeToString(this.routes.get(i))+"</TD></TR>";
		res+="</TABLE>";

		res+="<BR>";

		res+="<B>Association(Route request/sender)</B> ("+this.routes.size()+")<BR>";
		res+="<TABLE border=1>";
		res+="<TR><TD>Route request</TD><TD>Sender</TD></TR>";
		for(int i=0;i<this.routeRequestAndReceiverAssociation.size();i++) res+=this.routeRequestAndReceiverAssociation.get(i).toHTML();
		res+="</TABLE>";

		return res;
	}



	private class GroupAssociation
	{
		public int representative;
		public int[] members;

		public GroupAssociation(int idRepresentative, int member)
		{
			this.representative=idRepresentative;
			this.members=new int[1];
			this.members[0]=member;
		}

		public void add(int member)
		{
			this.members=MWACGroupAssistant.cloneGroupArray(this.members,1+this.members.length);
			this.members[this.members.length-1]=member;
		}

		public boolean contains(int id)
		{
			if(this.representative==id) return true;
			for(int i=0;i<this.members.length;i++) if(this.members[i]==id) return true;
			return false;
		}

		public String toHTML()
		{
			return "<TR><TD>"+this.representative+"</TD><TD>"+MWACGroupAssistant.groupsToString(this.members)+"</TD></TR>";
		}
	}



	public class RouteRequestAndReceiverAssociationItem
	{
		public int routeRequest;
		public int sender;

		public RouteRequestAndReceiverAssociationItem(int routeRequest,int sender)
		{
			this.routeRequest=routeRequest;
			this.sender=sender;
		}

		public String toHTML()
		{
			return "<TR><TD>"+this.routeRequest+"</TD><TD>"+this.sender+"</TD><TR>";
		}
	}

}
