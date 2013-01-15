package simulation.solutions.custom.MWACSocial;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * the neighboor list
 */
public class MWACSocialNeighboorList
{
	public final static int UNDEFINED_ID = -1;
	
	public LinkedList<SocialTripletIdRoleGroup> lst;

	public MWACSocialNeighboorList()
	{
		this.lst=new LinkedList<SocialTripletIdRoleGroup>();
	}


	public String toHTML()
	{
		String res="<B>Neighboor list</B> ("+lst.size()+")<BR>";

		res+="<TABLE border=1>";
		res+="<TR><TD>id</TD><TD>role</TD><TD>group</TD></TR>";
		SocialTripletIdRoleGroup triplet;
		ListIterator<SocialTripletIdRoleGroup> iter = lst.listIterator();
		while(iter.hasNext())
		{
			triplet=iter.next();
			res+="<TR><TD>"+triplet.id+"</TD><TD>"+MWACSocialAgent.roleToString(triplet.role)+"</TD><TD>"+MWACSocialGroupAssistant.groupsToString(triplet.groups)+"</TD></TR>";
		}
		res+="</TABLE>";
		return res;
	}

	public int getLinkToRepresentant(int representativeId)
	{
		boolean trouve=false;
		SocialTripletIdRoleGroup triplet=null;
		ListIterator<SocialTripletIdRoleGroup> iter = lst.listIterator();
		while(!trouve && iter.hasNext())
		{
			triplet=iter.next();
			if(triplet.role==MWACSocialAgent.roleLINK) trouve=triplet.inGroup(representativeId);
		}

		if(trouve)
		{
			// on le deplace pour qu'il ne soit pas utilisé en priorité au prochain coup
			iter.remove();
			this.lst.add(triplet);
			return triplet.id;
		}

		return UNDEFINED_ID;
	}

	public int size()
	{
		return this.lst.size();
	}

	/**
	 * 
	 * @param id
	 * @param role
	 * @param group
	 * @return true if the agent/object id was unknown
	 */
	public boolean put(int id,byte role,int group)
	{
		int[] groups = new int[1];
		groups[0]=group;
		return this.put(id,role,groups);
	}
	public boolean put(int id,byte role,int[] groups)
	{

		boolean trouve=false;
		ListIterator<SocialTripletIdRoleGroup> iter = lst.listIterator();
		while(!trouve && iter.hasNext()) trouve=(iter.next().id==id);
		if(trouve)
			iter.set(new SocialTripletIdRoleGroup(id,role,groups));
		else
			this.lst.add(new SocialTripletIdRoleGroup(id,role,groups));

		return !trouve;
	}

	public void remove(int id)
	{
		boolean trouve=false;
		ListIterator<SocialTripletIdRoleGroup> iter = lst.listIterator();
		while(!trouve && iter.hasNext()) trouve=(iter.next().id==id);
		if(trouve) iter.remove();
	}
	
	public boolean contains(int id)
	{
		boolean trouve=false;
		ListIterator<SocialTripletIdRoleGroup> iter = lst.listIterator();
		while(!trouve && iter.hasNext()) trouve=(iter.next().id==id);
		return trouve;
	}

	public SocialTripletIdRoleGroup get(int id)
	{
		SocialTripletIdRoleGroup triplet; 
		ListIterator<SocialTripletIdRoleGroup> iter = lst.listIterator();
		while(iter.hasNext())
		{
			triplet=iter.next();
			if(triplet.id==id) return triplet.clone();
		}
		return null;
	}

	public int getNbRepresentative()
	{
		int nb=0;
		ListIterator<SocialTripletIdRoleGroup> iter = lst.listIterator();
		while(iter.hasNext()) if(iter.next().role==MWACSocialAgent.roleREPRESENTATIVE) nb++;
		return nb;
	}

	public ArrayList<Integer> getRepresentativeIdentifiers()
	{
		SocialTripletIdRoleGroup t;
		ArrayList<Integer> res = new ArrayList<Integer>() ;
		ListIterator<SocialTripletIdRoleGroup> iter = lst.listIterator();
		while(iter.hasNext())
		{
			t=iter.next();
			if(t.role==MWACSocialAgent.roleREPRESENTATIVE) res.add(t.id);
		}
		return res;
	}

	public boolean isEmpty()
	{
		return this.lst.isEmpty();
	}

	public String toString()
	{
		return lst.toString();
	}
}
