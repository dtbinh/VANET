package simulation.solutions.custom.DSRSocial.Route;

import java.util.Vector;

/**
 * Model a route used by the DSR protocol
 * @author JPeG
 *
 */
public class DSRSocialRouteAssistant{
	public static int UNDEFINED_ID = -1;
	
	public static int getLastRelay(int[] route) throws EmptyRouteException
	{
		if(route.length==0) 
			throw new EmptyRouteException();
		else
			return route[route.length-1];
	}
	
	/** add a new relay in the route
	 * @param relay identifier of 
	 */
	public static int[] add(int[] route,int relay)
	{
		int i=0;
		int[] newRoute = new int[1+route.length];
		for(i=0;i<route.length;i++) newRoute[i]=route[i];
		newRoute[i]=relay;
		return newRoute;
	}

	public static int[] add(int[] route, int[] addedRoute)
	{
		int[] newRoute = new int[route.length+addedRoute.length];
		
		for(int i=0;i<route.length;i++) newRoute[i]=route[i];
		for(int i=0;i<addedRoute.length;i++) newRoute[route.length+i-1]=addedRoute[i];
		
		return newRoute;
	}
	
	/** reverse the route
	 */
	public static int[] reverse(int[] route)
	{
		int i=0;
		int[] newRoute = new int[1+route.length];
		for(i=0;i<route.length;i++) newRoute[route.length-1-i]=route[i];
		return newRoute;
	}

	
		
	/**
	 * return the string representation of the route
	 * @return the string representation
	 */
	public static String toString(int[] route)
	{

		if(route.length==0) return "[]";

		String res="["+route[0];
		for(int i=1;i<route.length;i++) res+=","+route[i];
		return (res+"]");
	}
	
	
	
	public static boolean contains(int[] route,int id)
	{
		for(int i=0;i<route.length;i++) if(route[i]==id) return true;
		return false;
	}
	
	public static int previous(int[] route,int id) throws EmptyRouteException,NoPreviousRelayException,UnknownIdentifierException
	{
		
		if (route.length==0) throw new EmptyRouteException();
		
		for(int i=0;i<route.length;i++)
		{
			if(route[i]==id)
			{
				if (i==0) 
					throw new NoPreviousRelayException();
				else 
					return route[i-1];
			}
		}
		
		throw new UnknownIdentifierException();
	}

	public static int next(int[] route,int id) throws NoNextRelayException, UnknownIdentifierException, EmptyRouteException 
	{
		
		if (route.length==0) throw new EmptyRouteException();
		
		for(int i=0;i<route.length;i++)
		{
			if(route[i]==id)
			{
				if (i==route.length-1) 
					throw new NoNextRelayException();
				else 
					return route[i+1];
			}
		}
		
		throw new UnknownIdentifierException();
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
	public static int getPreviousId(int[] route,int id)
	{
		int i=0;
		while(i<route.length && route[i]!=id) i++;
		if(i==route.length || i==0) 
			return DSRSocialRouteAssistant.UNDEFINED_ID;
		else
			return route[i-1];
	}	

	public static int getNextId(int[] route,int id)
	{
		int i=0;
		while(i<route.length-1 && route[i]!=id) i++;
		if(i>=route.length-1) 
			return DSRSocialRouteAssistant.UNDEFINED_ID;
		else
			return route[i+1];
	}	

	public static int getFirstId(int[] route)
	{
		if(route.length<1) 
			return DSRSocialRouteAssistant.UNDEFINED_ID;
		else
			return route[0];
	}	

	public static int getLastId(int[] route)
	{
		if(route.length<1) 
			return DSRSocialRouteAssistant.UNDEFINED_ID;
		else
			return route[route.length-1];
	}
	/**
	 * 
	 */
	 public static int[] clone(int[] route)  {
		   int[] newRoute=new int[route.length];
		   for(int i=0;i<route.length;i++) newRoute[i]=route[i];
		   return newRoute;
		  }
	
}
