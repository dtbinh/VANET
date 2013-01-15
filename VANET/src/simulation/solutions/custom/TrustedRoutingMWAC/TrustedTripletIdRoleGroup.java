package simulation.solutions.custom.TrustedRoutingMWAC;


/**
 * model a triplet id/role/group (item of the neighboor list)
 * 
 * @author Jean-Paul Jamont
 */
public class TrustedTripletIdRoleGroup implements Cloneable {
	public byte role;
	public int[] groups;
	public int id;
	public float trust;

	public TrustedTripletIdRoleGroup(int id, byte role, int group, float trust) {
		this.id = id;
		this.role = role;
		this.groups = new int[1];
		this.groups[0] = group;
		this.trust = trust;
	}
	
	public TrustedTripletIdRoleGroup(int id, byte role, int group) {
		this.id = id;
		this.role = role;
		this.groups = new int[1];
		this.groups[0] = group;
		this.trust = 1.0f;
	}
	
	public TrustedTripletIdRoleGroup(int id, byte role, int[] groups, float trust) {
		
		this.trust = trust;
		this.id = id;
		this.role = role;
		this.groups = groups;
		
	}

	public TrustedTripletIdRoleGroup(int id, byte role, int[] groups) {
		this(id,role,groups,1.0f);		
	}

	public TrustedTripletIdRoleGroup clone() {

		try {
			return (TrustedTripletIdRoleGroup) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}

	}

	public static int[] cloneRoute(int[] route) {
		return MWACRouteAssistant.cloneRoute(route, route.length);
	}

	public static int[] cloneRoute(int[] route, int newDimension) {
		int[] res = new int[newDimension];
		for (int i = 0; i < route.length; i++)
			res[i] = route[i];
		return res;
	}

	public boolean inGroup(int id) {
		return MWACGroupAssistant.containsGroup(id, this.groups);
	}

	public String toString() {
		return "<" + this.id + "," + TrustedRoutingMWACAgent.roleToString(this.role) + ","
				+ MWACGroupAssistant.groupsToString(this.groups) + "," + trust + ">";
	}
}
