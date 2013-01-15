package
simulation.solutions.custom.DSR.Route;

/**
 * Model a route used by the DSR protocol
 * @author Jean-Paul Jamont
 */

/** this class help a designer to work with DSR route */
public class DSRRouteAssistant{


	/** add a new relay in the route
	 * example : (route=[11,12,13,14],relay=15) returns [11,12,13,14,15]
	 * @param route the processed route
	 * @param relay identifier of the last relay
	 * @return the route resulting from the addition of relay to the route 
	 */
	public static int[] add(int[] route,int relay)
	{
		int i=0;
		int[] newRoute = new int[1+route.length];
		for(i=0;i<route.length;i++) newRoute[i]=route[i];
		newRoute[i]=relay;
		return newRoute;
	}

	/** add a route in another one
	 * example : (route=[11,12,13,14],route=[21,22,23]) returns [11,12,13,14,21,22,23]
	 * @param route the 1st route 
	 * @param addedRoute the 2nd route
	 * @return the route resulting from the concatenation between the 1st route and the 2nd one
	 */
	public static int[] add(int[] route, int[] addedRoute)
	{
		int[] newRoute = new int[route.length+addedRoute.length];
		
		for(int i=0;i<route.length;i++) newRoute[i]=route[i];
		for(int i=0;i<addedRoute.length;i++) newRoute[route.length+i-1]=addedRoute[i];
		
		return newRoute;
	}

	/**
	 * get the last relay of a route 
	 * example : (route=[11,12,13,14]) returns 14
	 * @param route the processed route
	 * @return identifier of a last relay
	 * @throws EmptyRouteException
	 */
	public static int getLastRelay(int[] route) throws EmptyRouteException
	{
		if(route.length==0) 
			throw new EmptyRouteException();
		else
			return route[route.length-1];
	}

	/** allows to know if an identifier is contained in a route
	 * example: (route=[10,20,30],id=20) returns true
	 * example: (route=[10,20,30],id=21) returns false
	 * @param route the processed route
	 * @param id identifier of the searched relay
	 * @return true if the relay id is finded in the route, else false  
	 */
	public static boolean contains(int[] route,int id)
	{
		for(int i=0;i<route.length;i++) if(route[i]==id) return true;
		return false;
	}

	/**
	 * get the relay after another one according a specified route
	 * example : (route=[11,12,13,14],id=12) returns 13
	 * example : (route=[],id=1) returns EmptyRouteException
	 * example : (route=[11,12,13,14],id=15) returns UnknownIdentifierException
	 * example : (route=[11,12,13,14],id=14) returns NoNextRelayException
	 * @param route the processed route
	 * @param id identifier of the relay before the searched one
	 * @return identifier of the relay before the searched one
	 * @throws NoNextRelayException
	 * @throws UnknownIdentifierException
	 * @throws EmptyRouteException
	 */
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
	

	/**
	 * get the relay after another one according a specified route
	 * example : (route=[11,12,13,14],id=12) returns 11
	 * example : (route=[],id=1) returns EmptyRouteException
	 * example : (route=[11,12,13,14],id=15) returns UnknownIdentifierException
	 * example : (route=[11,12,13,14],id=11) returns NoPreviousRelayException
	 * @param route the processed route
	 * @param id identifier of the relay after the searched one
	 * @return identifier of the previous relay
	 * @throws EmptyRouteException
	 * @throws NoPreviousRelayException
	 * @throws UnknownIdentifierException
	 */
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


	
	/** reverse the route
	 * example : (route=[11,12,13,14]) returns [14,13,12,11]
	 * @param route the processed route
	 * @return the reversed route
	 */
	public static int[] reverse(int[] route)
	{
		int[] newRoute = new int[1+route.length];
		for(int i=0;i<route.length;i++) newRoute[route.length-1-i]=route[i];
		return newRoute;
	}


	
	/**
	 * clones a route
	 * @param route the route which must be cloned
	 * @return the cloned route
	 */
	 public static int[] clone(int[] route)  {
		   int[] newRoute=new int[route.length];
		   for(int i=0;i<route.length;i++) newRoute[i]=route[i];
		   return newRoute;
		  }

	 /**
		 * returns the string representation of the route
		 * @param route the processed route
		 * @return the string representation
		 */
		public static String toString(int[] route)
		{

			if(route.length==0) return "[]";

			String res="["+route[0];
			for(int i=1;i<route.length;i++) res+=","+route[i];
			return (res+"]");
		}

}
