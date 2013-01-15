package simulation.solutions.custom.ACO_MWAC.AntAssistant;

import simulation.solutions.custom.MWAC.MWACRouteAssistant;


/**
 * Modele de route utilisée par les fourmis
 * @author Yacine LATTAB & Ouerdia SLAOUTI
 */

public class Ant_Route_Assistant {

	//indique que le prochain relais n'existe pas
	public static int NotExist = -1;


	public static int[] add(int[] route,int id)
	{
		int[] res = new int[route.length + 1];
		for(int i=0;i<route.length;i++) res[i]=route[i];
		res[route.length]=id;
		return res;
	}

	public static boolean contains(int[] route,int id)
	{
		for(int i=0;i<route.length;i++) if(route[i]==id) return true;
		return false;
	}
	
	public static int getFirstId(int[] route)
	{
		if(route.length<1) 
			return NotExist;
		else
			return route[0];
	}	
	
	public static int getLastId(int[] route)
	{
		if(route.length<1) 
			return NotExist;
		else
			return route[route.length-1];
	}
	
	public static int[] removeLastId(int[] route)
	{
		if(route.length<1){
			return route;
		}
		else{
			int[] res = new int[route.length-1];
			for(int i=0;i<res.length;i++) res[i]=route[i];
			return res;
		}
	}
	
	public synchronized static String toString(int [] route)
	{
		if (route==null || route.length==0) return "<>";
		String res="<"+route[0];
		for(int i=1;i<route.length;i++) res+=","+route[i];
		return (res+">");
	}	
}
