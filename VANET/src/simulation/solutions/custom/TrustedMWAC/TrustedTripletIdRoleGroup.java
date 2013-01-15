package simulation.solutions.custom.TrustedMWAC;

import simulation.solutions.custom.TrustedMWAC.Messages.MWACMessage;



/**
 * model a triplet id/role/group (item of the neighboor list)
 * @author Jean-Paul Jamont
 */
public class TrustedTripletIdRoleGroup implements Cloneable
{
	public byte role;
	public int[] groups;
	public int id;
	public float trust;

	public TrustedTripletIdRoleGroup(int id,byte role,int group)
	{
		this(id,1.0f,role,group);
	}
	public TrustedTripletIdRoleGroup(int id,float trust,byte role,int group)
	{
		this.id=id;	this.trust=trust; this.role=role;	
		this.groups = new int[1];
		this.groups[0]=group;
	}
	public TrustedTripletIdRoleGroup(int id,byte role,int[] groups)
	{
		this(id,1.0f,role,groups);
	}
	public TrustedTripletIdRoleGroup(int id,float trust,byte role,int[] groups)
	{
		this.id=id;	this.trust=trust;	this.role=role;	this.groups=groups;
	}

	public TrustedTripletIdRoleGroup clone()
	{

		try 
		{
			return (TrustedTripletIdRoleGroup) super.clone();
		} 
		catch (CloneNotSupportedException e) 
		{
			return null;
		}

	}
	

	public static int[] cloneRoute(int[] route)
	{
		return MWACRouteAssistant.cloneRoute(route, route.length);
	}
	
	public static int[] cloneRoute(int[] route,int newDimension)
	{
		int[] res = new int[newDimension];
		for(int i=0;i<route.length;i++) res[i]=route[i];
		return res;
	}
	
	public boolean inGroup(int id)
	{
		return MWACGroupAssistant.containsGroup(id,this.groups);
	}
	

	public String toString()
	{
		return "<"+this.id+","+this.trust+","+TrustedMWACAgent.roleToString(this.role)+","+MWACGroupAssistant.groupsToString(this.groups)+">";
	}
}	
