package simulation.solutions.custom.TrustedRoutingMWAC;

import java.util.ArrayList;

import simulation.utils.aDate;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Vector;


public class TrustedRoutingMWACNeighboorList {
	/** to specify an undefined identifier */
	public final static int UNDEFINED_ID = -1;

	/**
	 * date of the last neighboor modification which can produce an
	 * organizationnal incoherence
	 */
	public long dateOfLastPossibleIncoherenceDetection;
	/** the list of neighboors */
	public LinkedList<TrustedTripletIdRoleGroup> neighboorList;

	/**
	 * default constructor
	 */
	public TrustedRoutingMWACNeighboorList() {
		this.neighboorList = new LinkedList<TrustedTripletIdRoleGroup>();
		dateOfLastPossibleIncoherenceDetection = aDate.getCurrentTimeInMS();
	}

	/**
	 * is the list contains the neighboor named 'id'
	 * 
	 * @return true if the list contain an neighboor identified by 'id'
	 */
	public boolean contains(int id) {
		boolean trouve = false;
		ListIterator<TrustedTripletIdRoleGroup> iter = neighboorList.listIterator();
		while (!trouve && iter.hasNext())
			trouve = (iter.next().id == id);
		return trouve;
	}

	
	/**
	 * Set trust of id to 0.0f
	 * @param id
	 * @return true if operation was successful
	 */
	public boolean distrustCompletely(int id){
		TrustedTripletIdRoleGroup triplet = get(id);
		if(triplet != null)
			return put(id, triplet.role, triplet.groups, 0.0f);
		return false;
	}
	
	/**
	 * Decrease trust of id by the specified amount
	 * @param id
	 * @param amount
	 * @return true if operation was sucessful
	 */
	public boolean decreaseTrustBy(int id, float amount){
		TrustedTripletIdRoleGroup triplet = get(id);
		if(triplet != null){
			float trust = 0.0f;
			if(triplet.trust > 0.0f)
				 trust = triplet.trust - amount;
			
			return put(id, triplet.role, triplet.groups, trust);
		}
		return false;
	}
	
	/**
	 * return an item of the list according its index
	 * 
	 * @return an item of the list
	 */
	public TrustedTripletIdRoleGroup get(int id) {
		TrustedTripletIdRoleGroup triplet;
		ListIterator<TrustedTripletIdRoleGroup> iter = neighboorList.listIterator();
		while (iter.hasNext()) {
			triplet = iter.next();
			if (triplet.id == id)
				return triplet.clone();
		}
		return null;
	}

	/**
	 * returns the identifier of a link agent which allows to communicate with a
	 * specific representative agent
	 * 
	 * @param representativeId
	 *            identifier of the specific reprensentative agent
	 * @return identifier of a link agent
	 */
	public int getLinkToRepresentant(int representativeId) {
		boolean trouve = false;
		TrustedTripletIdRoleGroup triplet = null;
		ListIterator<TrustedTripletIdRoleGroup> iter = neighboorList.listIterator();
		
		while (!trouve && iter.hasNext()) {
			triplet = iter.next();
			if (triplet.role == TrustedRoutingMWACAgent.roleLINK)
				trouve = triplet.inGroup(representativeId);
		}

		if (trouve) {
			// on le deplace pour qu'il ne soit pas utilisé en priorité au
			// prochain coup
			iter.remove();
			this.neighboorList.add(triplet);
			return triplet.id;
		}

		return UNDEFINED_ID;
	}

	/**
	 * returns the number of representative agent in the list (i.e. in the
	 * neighboorhood of the agent)
	 * 
	 * @return the number of reprentative agents
	 */
	public int getNbRepresentative() {
		int nb = 0;
		ListIterator<TrustedTripletIdRoleGroup> iter = neighboorList.listIterator();
		while (iter.hasNext())
			if (iter.next().role == TrustedRoutingMWACAgent.roleREPRESENTATIVE)
				nb++;
		return nb;
	}

	/**
	 * returns the identifiers of the representative agent in the list (i.e. in
	 * the neighboorhood of the agent)
	 * 
	 * @return the set of agent identifiers
	 */
	public ArrayList<Integer> getRepresentativeIdentifiers() {
		TrustedTripletIdRoleGroup t;
		ArrayList<Integer> res = new ArrayList<Integer>();
		ListIterator<TrustedTripletIdRoleGroup> iter = neighboorList.listIterator();
		while (iter.hasNext()) {
			t = iter.next();
			if (t.role == TrustedRoutingMWACAgent.roleREPRESENTATIVE)
				res.add(t.id);
		}
		return res;
	}

	/**
	 * 
	 * @return the set of agent identifiers
	 */
	public ArrayList<Integer> getLinkIdentifiers() {
		TrustedTripletIdRoleGroup t;
		ArrayList<Integer> res = new ArrayList<Integer>();
		ListIterator<TrustedTripletIdRoleGroup> iter = neighboorList.listIterator();
		while (iter.hasNext()) {
			t = iter.next();
			if (t.role == TrustedRoutingMWACAgent.roleLINK)
				res.add(t.id);
		}
		return res;
	}
	
	
	public byte getRole(int id){
		TrustedTripletIdRoleGroup nb = get(id);
		if(nb != null)
			return nb.role;
		else
			return TrustedRoutingMWACAgent.roleVOID;
	}

	
	
	/**
	 * Is the neighbors list empty?
	 * 
	 * @return true if the list is empty
	 */
	public boolean isEmpty() {
		return this.neighboorList.isEmpty();
	}

	/**
	 * 
	 */
	public boolean isKnowedGroups(int[] groups, int ignoredAgent) {
		int i;
		Vector<Integer> v = new Vector<Integer>(groups.length);
		for (i = 0; i < groups.length; i++)
			v.add(groups[i]);

		TrustedTripletIdRoleGroup t;
		ListIterator<TrustedTripletIdRoleGroup> iter = neighboorList.listIterator();
		while ((!v.isEmpty()) && iter.hasNext()) {
			t = iter.next();
			if (t.id != ignoredAgent)
				for (i = 0; i < v.size(); i++)
					if (t.inGroup(v.get(i))) {
						v.remove(i);
						i--;
					}
		}

		return v.isEmpty();
	}

	public boolean put(int id, byte role, int group, float trust) {
		int[] groups = new int[1];
		groups[0] = group;
		return this.put(id, role, groups, trust);
	}
	
	public boolean put(int id, byte role, int group) {
		return put(id,role,group,1.0f);
	}

	public boolean put(int id, byte role, int[] groups, float trust) {
		
		boolean findedIdentifier = false;
		ListIterator<TrustedTripletIdRoleGroup> iter = neighboorList.listIterator();
		while (!findedIdentifier && iter.hasNext())
			findedIdentifier = (iter.next().id == id);
		if (findedIdentifier)
			iter.set(new TrustedTripletIdRoleGroup(id, role, groups, trust));
		else
			this.neighboorList.add(new TrustedTripletIdRoleGroup(id, role, groups, trust));

		return !findedIdentifier;
	}
	
	public boolean put(int id, byte role, int[] groups){
		return put(id,role,groups,1.0f);
	}

	public void remove(int id) {
		boolean trouve = false;
		ListIterator<TrustedTripletIdRoleGroup> iter = neighboorList.listIterator();
		while (!trouve && iter.hasNext())
			trouve = (iter.next().id == id);
		if (trouve)
			iter.remove();
	}

	public int size() {
		return neighboorList.size();
	}

	public String toHTML() {
		String res = "<B>Neighboor list</B> (" + neighboorList.size() + ")<BR>";

		res += "<TABLE border=1>";
		res += "<TR><TD>id</TD><TD>trust</TD><TD>role</TD><TD>group</TD></TR>";
		TrustedTripletIdRoleGroup triplet;
		ListIterator<TrustedTripletIdRoleGroup> iter = neighboorList.listIterator();
		while (iter.hasNext()) {
			triplet = iter.next();
			res += "<TR><TD>" + triplet.id + "</TD><TD>" + triplet.trust + 
			"</TD><TD>"	+ TrustedRoutingMWACAgent.roleToString(triplet.role) + "</TD><TD>"
			+ MWACGroupAssistant.groupsToString(triplet.groups)	+ "</TD></TR>";
		}
		res += "</TABLE>";
		return res;
	}


	public String toString() {
		return neighboorList.toString();
	}
}
