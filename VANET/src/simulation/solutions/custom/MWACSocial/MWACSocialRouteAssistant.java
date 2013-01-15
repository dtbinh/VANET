package simulation.solutions.custom.MWACSocial;

import java.nio.ByteBuffer;
import java.util.Vector;

import simulation.solutions.custom.MWAC.Messages.MWACMessage;

public class MWACSocialRouteAssistant {

	public static final int UNDEFINED_ID = -1;

	public static int[] add(int[] route,int[] routeToAdd)
	{
		int[] res = MWACSocialRouteAssistant.cloneRoute(route,route.length+routeToAdd.length);
		for(int i=0;i<routeToAdd.length;i++) res[route.length+i]=routeToAdd[i];
		return res;
	}	

	public static int[] add(int[] route,int id)
	{
		int[] res = MWACSocialRouteAssistant.cloneRoute(route,route.length+1);
		res[route.length]=id;
		return res;
	}

	
	public static int[] vectorToIntegerArray(Vector<Integer> route)
	{
		if (route==null || route.size()==0) return new int[0];
		int[] v = new int[route.size()];
		for(int i=0;i<route.size();i++) v[i]=route.get(i);
		return v;
	}

	public static Vector<Integer> integerArrayToVector(int[] route)
	{
		if (route==null || route.length==0) return new Vector<Integer>();
		Vector<Integer> v = new Vector<Integer>();
		for(int i=0;i<route.length;i++) v.add(route[i]);

		return v;
	}

	
	public static String integerArrayToString(int[] route)
	{
		if (route==null || route.length==0) return "<>";
		String res = ""+route[0];

		for(int i=1;i<route.length;i++) res+=","+route[i];

		return res;
	}

	public static boolean contains(int[] route,int id)
	{
		for(int i=0;i<route.length;i++) if(route[i]==id) return true;
		return false;
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


	public static int getPreviousId(int[] route,int id)
	{
		int i=0;
		while(i<route.length && route[i]!=id) i++;
		if(i==route.length || i==0) 
			return MWACSocialRouteAssistant.UNDEFINED_ID;
		else
			return route[i-1];
	}	

	public static int getNextId(int[] route,int id)
	{
		int i=0;
		while(i<route.length-1 && route[i]!=id) i++;
		if(i>=route.length-1) 
			return MWACSocialRouteAssistant.UNDEFINED_ID;
		else
			return route[i+1];
	}	

	public static int getFirstId(int[] route)
	{
		if(route.length<1) 
			return MWACSocialRouteAssistant.UNDEFINED_ID;
		else
			return route[0];
	}	

	public static int getLastId(int[] route)
	{
		if(route.length<1) 
			return MWACSocialRouteAssistant.UNDEFINED_ID;
		else
			return route[route.length-1];
	}
	public static int[] subRoute(int[] route,int indexLastIncludedItem)
	{
		int[] res=new int[indexLastIncludedItem];
		for(int i=0;i<indexLastIncludedItem;i++) res[i]=route[i];
		return res;
	}


	public static ByteBuffer routeToByteBuffer(int[] route)
	{
		ByteBuffer bytes=ByteBuffer.allocate(4*route.length);
		for(int i=0;i<route.length;i++) bytes.putInt(route[i]);
		return bytes;
	}

	public static int[] removeHead(int[] route)
	{
		int[] res = new int[route.length-1];
		for(int i=1;i<route.length;i++) res[i-1]=route[i];
		return res;
	}
}
