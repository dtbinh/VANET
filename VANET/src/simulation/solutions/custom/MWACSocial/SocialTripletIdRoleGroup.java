package simulation.solutions.custom.MWACSocial;





/**
 * model a triplet id/role/group (item of the neighboor list)
 */
public class SocialTripletIdRoleGroup implements Cloneable
{
	public byte role;
	public int[] groups;
	public int id;

	public SocialTripletIdRoleGroup(int id,byte role,int group)
	{
		this.id=id;	this.role=role;	
		this.groups = new int[1];
		this.groups[0]=group;
	}
	public SocialTripletIdRoleGroup(int id,byte role,int[] groups)
	{
		this.id=id;	this.role=role;	this.groups=groups;
	}

	public SocialTripletIdRoleGroup clone()
	{

		try 
		{
			return (SocialTripletIdRoleGroup) super.clone();
		} 
		catch (CloneNotSupportedException e) 
		{
			return null;
		}

	}
	

	public static int[] cloneRoute(int[] route)
	{
		return MWACSocialRouteAssistant.cloneRoute(route, route.length);
	}
	
	public static int[] cloneRoute(int[] route,int newDimension)
	{
		int[] res = new int[newDimension];
		for(int i=0;i<route.length;i++) res[i]=route[i];
		return res;
	}
	
	public boolean inGroup(int id)
	{
		return MWACSocialGroupAssistant.containsGroup(id,this.groups);
	}
	

	public String toString()
	{
		return "<"+this.id+","+MWACSocialAgent.roleToString(this.role)+","+MWACSocialGroupAssistant.groupsToString(this.groups)+">";
	}
}	
