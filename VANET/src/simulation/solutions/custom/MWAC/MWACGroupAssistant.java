package simulation.solutions.custom.MWAC;

/**
 * method to assist the designer of the MWAC solution
 * @author Jean-Paul Jamont
 */
public class MWACGroupAssistant {

	
	public static boolean containsGroup(int id,int[] groups)
	{
	for(int i=0;i<groups.length;i++) if(groups[i]==id) return true;
	return false;
	}

	public static int[] cloneGroupArray(int[] route)
	{
		return MWACRouteAssistant.cloneRoute(route, route.length);
	}
	
	public static int[] cloneGroupArray(int[] route,int newDimension)
	{
		int[] res = new int[newDimension];
		for(int i=0;i<route.length;i++) res[i]=route[i];
		return res;
	}
	
	public static String groupsToString(int[] group)
	{
		switch(group.length)
		{
		case 0: return "ERROR";
		case 1:
			if (group[0]==-1)
				return "NO_GROUP";
			else
				return ""+group[0];
		default:
			String res=""+group[0];
			for(int i=1;i<group.length;i++) res+=","+group[i];
			return res;
		}
	}
	
	
}
