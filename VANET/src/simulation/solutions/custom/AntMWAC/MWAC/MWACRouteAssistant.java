package simulation.solutions.custom.AntMWAC.MWAC;

import java.nio.ByteBuffer;

import simulation.solutions.custom.AntMWAC.MWAC.Messages.MWACMessage;
/**
 * MWAC route assistant 
 * @author Jean-Paul Jamont
 */

public class MWACRouteAssistant {

	public static final int UNDEFINED_ID = -1;

	public static int[] add(int[] route,int[] routeToAdd)
	{
		int[] res = MWACRouteAssistant.cloneRoute(route,route.length+routeToAdd.length);
		for(int i=0;i<routeToAdd.length;i++) res[route.length+i]=routeToAdd[i];
		return res;
	}	

	public static int[] add(int[] route,int id)
	{
		int[] res = MWACRouteAssistant.cloneRoute(route,route.length+1);
		res[route.length]=id;
		return res;
	}

	public static String routeToString(int[] route)
	{
		if (route==null || route.length==0) return "<>";
		String res = ""+route[0];

		for(int i=1;i<route.length;i++) res+=","+route[i];

		return res;
	}

	public  static int[] removeLastId(int[] route)
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
	
	
	public static boolean contains(int[] route,int id)
	{
		for(int i=0;i<route.length;i++) if(route[i]==id) return true;
		return false;
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


	public static int getPreviousId(int[] route,int id)
	{
		int i=0;
		while(i<route.length && route[i]!=id) i++;
		if(i==route.length || i==0) 
			return MWACRouteAssistant.UNDEFINED_ID;
		else
			return route[i-1];
	}	

	public static int getNextId(int[] route,int id)
	{
		int i=0;
		while(i<route.length-1 && route[i]!=id) i++;
		if(i>=route.length-1) 
			return MWACRouteAssistant.UNDEFINED_ID;
		else
			return route[i+1];
	}	

	public static int getFirstId(int[] route)
	{
		if(route.length<1) 
			return MWACRouteAssistant.UNDEFINED_ID;
		else
			return route[0];
	}	

	public static int[] subRoute(int[] route,int indexLastIncludedItem)
	{
		int[] res=new int[indexLastIncludedItem];
		for(int i=0;i<indexLastIncludedItem;i++) res[i]=route[i];
		return res;
	}

	public static int getLastId(int[] route)
	{
		if(route.length<1) 
			return MWACRouteAssistant.UNDEFINED_ID;
		else
			return route[route.length-1];
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
	
	
	/*****************************************************************************/
	/**@author tenboukti ahlem && khorchi karima
	 *  allow to check if route exists among the used routes (All_routes)
	 */
	public static  boolean existeRoute(int []All_routes,int []route)
	{
    int length=All_routes[0];   
    int i=1;
    int []route_test;
    boolean exist=false;
	while ( ! exist && i<All_routes.length)
	{ route_test=new int[length];
		for (int j=0; j<length;j++)
		{
			route_test[j]=All_routes[i];
			//System.out.println("l'elemnt " +route_test[j]); 
			
			i++;
		}
	
		exist=Routes_equals(route_test,route);
		if (!exist) 
			{ if (i<All_routes.length)	
				{
				length=All_routes[i];
				i++;
				}
			}
				        
	        
	
			}
	return exist;
	}
	
	/**********************************************************************************/
	public static  boolean Routes_equals(int []route1,int []route2)
	{
	boolean equal=true;
	int j=0;
	if( route1.length!=route2.length) equal=false;
	else{  
	while (equal && j<route1.length )
	{
		if ( route1[j]!=route2[j]) equal=false;
		else j++;
	}
	
	}
		return equal;
	}
	
	/*****************************************************************************/	
	/*public static  boolean Routes_equals (int []route1,int []route2)
	{
	boolean equal=false;
	int j=0;
	  
	while ( j<route1.length )
	{  int i=0;
		while ( i<route2.length)
	  { if ( route1[j]==route2[i]) 
		  equal=true;
		 
	     else  equal =false ;
	    i++; 
	   }
	j++;}
    
	 return equal;
	}
	*/
}
