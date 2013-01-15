package simulation.solutions.custom.TrustedMWAC;

import java.util.ArrayList;
import simulation.utils.aDate;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Vector;



/**
 * the neighboor list
 * @author Jean-Paul Jamont
 */
public class TrustedMWACNeighboorList
{
	/** to specify an undefined identifier */
	public final static int UNDEFINED_ID = -1;


	/** date of the last neighboor modification which can produce an organizationnal incoherence*/
	public long dateOfLastPossibleIncoherenceDetection;
	/** the list of neighboors */ 
	public LinkedList<TrustedTripletIdRoleGroup> neighboorList;

	/**
	 * default constructor
	 */
	public TrustedMWACNeighboorList()
	{
		this.neighboorList=new LinkedList<TrustedTripletIdRoleGroup>();
		dateOfLastPossibleIncoherenceDetection=aDate.getCurrentTimeInMS();
	}




	/**
	 * returns a HTML string representation the neighboor list
	 */
	public String toHTML()
	{
		String trust = "<FONT size=+1 color=green>TRUSTED</FONT>";
		if(!this.isConfident()) trust = "<FONT size=+1 color=red>DISTRUSTED</FONT>";
		
		String res="<B>Neighboor list</B> ("+neighboorList.size()+"neighboors / "+trust+")<BR>";

		res+="<TABLE border=1>";
		res+="<TR><TD>id</TD><TD>trust</TD><TD>role</TD><TD>group</TD></TR>";
		TrustedTripletIdRoleGroup triplet;
		ListIterator<TrustedTripletIdRoleGroup> iter = neighboorList.listIterator();
		while(iter.hasNext())
		{
			triplet=iter.next();
			res+="<TR><TD>"+triplet.id+"</TD><TD>"+triplet.trust+"</TD><TD>"+TrustedMWACAgent.roleToString(triplet.role)+"</TD><TD>"+MWACGroupAssistant.groupsToString(triplet.groups)+"</TD></TR>";
		}
		res+="</TABLE>";
		return res;
	}

	/**
	 * returns the identifier of a link agent which allows to communicate with a specific representative agent
	 * @param representativeId identifier of the specific reprensentative agent
	 * @return identifier of a link agent
	 */
	public int getLinkToRepresentant(int representativeId)
	{
		boolean trouve=false;
		TrustedTripletIdRoleGroup triplet=null;
		ListIterator<TrustedTripletIdRoleGroup> iter = neighboorList.listIterator();
		while(!trouve && iter.hasNext())
		{
			triplet=iter.next();
			if(triplet.role==TrustedMWACAgent.roleLINK) trouve=triplet.inGroup(representativeId);
		}

		if(trouve)
		{
			// on le deplace pour qu'il ne soit pas utilisé en priorité au prochain coup
			iter.remove();
			this.neighboorList.add(triplet);
			return triplet.id;
		}

		return UNDEFINED_ID;
	}

	/**
	 * returns the size of the neighboorlist (i.e. the number of neighboor)
	 * @return the number of neighboor)
	 */
	public int size()
	{
		return this.neighboorList.size();
	}

	/**
	 * add a neighboor in the list
	 * CAUTION: If the idenfier is already in the list, if lowest trust value is keeped
	 * @param id identifier of the neighboor
	 * @param role role of the neighboor
	 * @param group group idenifier of the neighboor
	 * @return true if the agent/object id was unknown
	 */
	public boolean put(int id,byte role,int group)
	{
		return this.put(id,1.0f,role,group);
	}
	/**
	 * add a neighboor in the list
	 * CAUTION: If the idenfier is already in the list, if lowest trust value is keeped
	 * @param id identifier of the neighboor
	 * @param confidence confidence to the identifier 
	 * @param role role of the neighboor
	 * @param group group idenifier of the neighboor
	 * @return true if the agent/object id was unknown
	 */
	public boolean put(int id,float confidence,byte role,int group)
	{
		int[] groups = new int[1];
		groups[0]=group;
		return this.put(id,confidence,role,groups);
	}	
	/**
	 * add a neighboor in the list
	 * CAUTION: If the idenfier is already in the list, if lowest trust value is keeped
	 * @param id identifier of the neighboor
	 * @param role role of the neighboor
	 * @param groups groups identifier of the neighboor
	 * @return true if the agent/object id was unknown
	 */
	public boolean put(int id,byte role,int[] groups)
	{
		return this.put(id,1.0f,role,groups);
	}
	/**
	 * add a neighboor in the list
	 * CAUTION: If the idenfier is already in the list, if lowest trust value is keeped
	 * @param id identifier of the neighboor
	 * @param role role of the neighboor
	 * @param groups groups identifier of the neighboor
	 * @return true if the agent/object id was unknown
	 */
	public boolean put(int id,float confidence,byte role,int[] groups)
	{
		if (!this.isKnowedGroups(groups,id)) dateOfLastPossibleIncoherenceDetection=aDate.getCurrentTimeInMS();

		boolean findedIdentifier=false;
		TrustedTripletIdRoleGroup tTrustedTriplet=null;
		ListIterator<TrustedTripletIdRoleGroup> iter = neighboorList.listIterator();
		while(!findedIdentifier && iter.hasNext()) findedIdentifier=((tTrustedTriplet=iter.next()).id==id);
		if(findedIdentifier)
		{
			iter.set(new TrustedTripletIdRoleGroup(id,Math.min(tTrustedTriplet.trust,confidence),role,groups));
		}
		else
			this.neighboorList.add(new TrustedTripletIdRoleGroup(id,confidence,role,groups));

		return !findedIdentifier;
	}

	/**
	 * 
	 */
	public boolean isKnowedGroups(int[] groups,int ignoredAgent)
	{
		int i;
		Vector<Integer> v = new Vector<Integer>(groups.length);
		for(i=0;i<groups.length;i++) v.add(groups[i]);

		TrustedTripletIdRoleGroup t;
		ListIterator<TrustedTripletIdRoleGroup> iter = neighboorList.listIterator();
		while((!v.isEmpty()) && iter.hasNext())
		{
			t=iter.next();
			if(t.id!=ignoredAgent)
				for(i=0;i<v.size();i++) 
					if (t.inGroup(v.get(i))) 
					{
						v.remove(i);
						i--;
					}
		}

		return v.isEmpty();
	}

	/**
	 * removes the neighboor named 'id'
	 */
	public void remove(int id)
	{
		boolean trouve=false;
		ListIterator<TrustedTripletIdRoleGroup> iter = neighboorList.listIterator();
		while(!trouve && iter.hasNext()) trouve=(iter.next().id==id);
		if(trouve) iter.remove();
	}

	/**
	 * is the list contains the neighboor named 'id'
	 * @return true if the list contain an neighboor identified by 'id'
	 */
	public boolean contains(int id)
	{
		boolean trouve=false;
		TrustedTripletIdRoleGroup triplet = null;
		ListIterator<TrustedTripletIdRoleGroup> iter = neighboorList.listIterator();
		while(!trouve && iter.hasNext()) 
			{
			triplet = iter.next();
			trouve=(triplet.id==id && triplet.trust>0.75f);
			}
	
		return trouve;
	}

	/**
	 * return an item of the list according its index
	 * @return an item of the list
	 */
	public TrustedTripletIdRoleGroup get(int id)
	{
		TrustedTripletIdRoleGroup triplet; 
		ListIterator<TrustedTripletIdRoleGroup> iter = neighboorList.listIterator();
		while(iter.hasNext())
		{
			triplet=iter.next();
			if(triplet.id==id) return triplet.clone();
		}
		return null;
	}

	/**
	 * (Anca)
	 * The ids of the members in the neighborhood
	 * @return
	 */
	public int[] getMembers(){
		int[] members = new int[neighboorList.size()];
		
		int i = 0;
		for(TrustedTripletIdRoleGroup t : neighboorList)
			members[i++] = t.id;
		return members;
	}
	
	/**
	 * returns the number of representative agent in the list (i.e. in the neighboorhood of the agent)
	 * @return the number of reprentative agents
	 */
	public int getNbRepresentative()
	{
		int nb=0;
		ListIterator<TrustedTripletIdRoleGroup> iter = neighboorList.listIterator();
		while(iter.hasNext()) if(iter.next().role==TrustedMWACAgent.roleREPRESENTATIVE) nb++;
		return nb;
	}

	/**
	 * returns the identifiers of the representative agent in the list (i.e. in the neighboorhood of the agent)
	 * @return the set of agent identifiers
	 */
	public ArrayList<Integer> getRepresentativeIdentifiers()
	{
		TrustedTripletIdRoleGroup t;
		ArrayList<Integer> res = new ArrayList<Integer>() ;
		ListIterator<TrustedTripletIdRoleGroup> iter = neighboorList.listIterator();
		while(iter.hasNext())
		{
			t=iter.next();
			if(t.role==TrustedMWACAgent.roleREPRESENTATIVE) res.add(t.id);
		}
		return res;
	}

	/** 
	 * returns the list of identifiers of neighbors groups which let's think that there is an organizational incoherence
	 * @param group group of the agent which is the owner of this neighboor list
	 * @returns a list of suspicious groups
	 */
	public Vector<Integer> suspiciousNeighboorsGroups(int[] ownerGroupIdentifier)
	{
		//String TEMPTEMPTEMPTEMP="";

		Vector<Integer> lst = new Vector<Integer>();
		Vector<Integer> ok = new Vector<Integer>();
		TrustedTripletIdRoleGroup triplet;
		ListIterator<TrustedTripletIdRoleGroup> iter;

		int i=0;;
		int t[];

		// construct the list of identifier of neighboor group
		iter = neighboorList.listIterator();
		while(iter.hasNext())
		{
			triplet=iter.next();
			t = triplet.groups;
			for(i=0;i<t.length;i++) 
				if (!MWACRouteAssistant.contains(ownerGroupIdentifier, t[i]) && (!lst.contains(t[i]))) lst.add(t[i]);
		}

		//TEMPTEMPTEMPTEMP="EXISTING GROUPS: ";
		//for(i=0;i<lst.size();i++) TEMPTEMPTEMPTEMP+=(lst.get(i)+" ");
		//TEMPTEMPTEMPTEMP+="\nTRIPLETS:\n";

		// remove identifier of well known group
		// * group identifier which are contained by a link agent which allow to contact the owner group 
		// * group identifier of a neighboor representative agent 
		LinkedList<TrustedTripletIdRoleGroup> copyNeighboorList = (LinkedList<TrustedTripletIdRoleGroup>) this.neighboorList.clone();
		iter = copyNeighboorList.listIterator();
		while(iter.hasNext())
		{
			triplet=iter.next();
			if (triplet.role==TrustedMWACAgent.roleREPRESENTATIVE)
			{
				lst.removeElement(triplet.id);
				iter.remove();
			}
			else if (triplet.role==TrustedMWACAgent.roleLINK)
			{
				t = triplet.groups;

				i=0;
				while(i<t.length && (!MWACRouteAssistant.contains(ownerGroupIdentifier, t[i]))) i++;


				if (i<t.length) 
				{
					for(i=0;i<t.length;i++)
					{
						lst.removeElement(t[i]);
						if(!ok.contains(t[i]) && (!MWACRouteAssistant.contains(ownerGroupIdentifier, t[i]))) ok.add(t[i]);
					}
					//		TEMPTEMPTEMPTEMP+="(1)"+triplet+"\n";
					iter.remove();
				}
			}
		}

		// remove identifier of well known group by an other agent
		if(!ok.isEmpty())
		{
			iter = copyNeighboorList.listIterator();
			while(iter.hasNext())
			{
				triplet=iter.next();
				if (triplet.role==TrustedMWACAgent.roleLINK)
				{
					t = triplet.groups;

					i=0;
					while(i<t.length && (!ok.contains(t[i]))) i++;


					if (i<t.length) 
					{
						for(i=0;i<t.length;i++)
						{
							lst.removeElement(t[i]);
						}
						//		TEMPTEMPTEMPTEMP+="(2)"+triplet+"\n";
						iter.remove();
					}
					//				else
					//					TEMPTEMPTEMPTEMP+="(-)"+triplet+"\n";
				}
			}
		}

		//	if (lst.size()>0) 
		//	{
			//	TEMPTEMPTEMPTEMP+="\n\nRESULTAT DES SUSPECTS=";
			//		for(i=0;i<lst.size();i++) TEMPTEMPTEMPTEMP+=(lst.get(i)+" ");
			//	System.out.println(TEMPTEMPTEMPTEMP);
		//	}
		return lst;
	}

	/**
	 * Is the neighbors list empty?
	 * @return true if the list is empty
	 */
	public boolean isEmpty()
	{
		return this.neighboorList.isEmpty();
	}

	/** is the neighboorhood confident?
	 * @return true if the neighboorhood can be considered as confident
	 */
	public boolean isConfident()
	{
		ListIterator<TrustedTripletIdRoleGroup> iter = this.neighboorList.listIterator();
		
		while(iter.hasNext())
			if (iter.next().trust < TrustManager.TETA1) return false;
		
		return true;
	}

	/** is the neighboorhood has known a modification in the recent past?
	 * @param delay the span of time which has required
	 * @return true if the neighboorhood has known a modification  during the last <i>delay</i> ms
	 */
	public boolean isStable(int delay)
	{
		return (aDate.getCurrentTimeInMS()-dateOfLastPossibleIncoherenceDetection)>delay;
	}

	/**
	 * (Anca)
	 * Trust recovery algorithm (Algorithm 1 / p. 8 - PAAMS article)
	 */
	public void trustRecovery(){
		for(TrustedTripletIdRoleGroup triplet : neighboorList){
			triplet.trust = (1 - TrustManager.LAMBDA) * triplet.trust + TrustManager.LAMBDA;
		}
	}
	
	/**
	 * returns the string signature of the neighboor list
	 * @return the string signature
	 */
	public String toString()
	{
		return neighboorList.toString();
	}
}
