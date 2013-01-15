package simulation.solutions.custom.AntMWAC.MWAC;

import simulation.solutions.custom.AntMWAC.MWAC.Messages.MWACMessage;



/**
 * model a triplet id/role/group (item of the neighboor list)
 * @author Jean-Paul Jamont
 */
public class TripletIdRoleGroup implements Cloneable
{
	public byte role;
	public int[] groups;
	public int id;

	public TripletIdRoleGroup(int id,byte role,int group)
	{
		this.id=id;	this.role=role;	
		this.groups = new int[1];
		this.groups[0]=group;
	}
	public TripletIdRoleGroup(int id,byte role,int[] groups)
	{
		this.id=id;	this.role=role;	this.groups=groups;
	}

	public TripletIdRoleGroup clone()
	{

		try 
		{
			return (TripletIdRoleGroup) super.clone();
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
		return "<"+this.id+","+AntMWACAgent.roleToString(this.role)+","+MWACGroupAssistant.groupsToString(this.groups)+">";
	}
}	
