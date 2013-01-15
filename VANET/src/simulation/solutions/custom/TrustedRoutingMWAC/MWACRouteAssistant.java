package simulation.solutions.custom.TrustedRoutingMWAC;

import java.nio.ByteBuffer;

/**
 * MWAC route assistant
 * 
 * @author Jean-Paul Jamont
 */

public class MWACRouteAssistant {

	public static final int UNDEFINED_ID = -1;

	public static int[] add(int[] route, int[] routeToAdd) {
		int[] res = cloneRoute(route, route.length + routeToAdd.length);
		
		for (int i = 0; i < routeToAdd.length; i++)
			res[route.length + i] = routeToAdd[i];
		return res;
	}

	public static int[] add(int[] route, int id) {
		int[] res = cloneRoute(route, route.length + 1);
		res[route.length] = id;
		return res;
	}

	public static String routeToString(int[] route) {
		if (route == null || route.length == 0)
			return "<>";
		String res = "" + route[0];

		for (int i = 1; i < route.length; i++)
			res += "," + route[i];

		return res;
	}

	public static int[] stringToRoute(String routeStr) {
		String str[] = routeStr.split(" ");

		int route[] = new int[str.length];
		try {
			for (int i = 0; i < str.length; i++)
				route[i] = Integer.parseInt(str[i]);
		} catch (Exception e) {
			System.err.print("Route string incorrect: " + routeStr);
		}
		return route;
	}

	public static boolean contains(int[] route, int id) {
		for (int i = 0; i < route.length; i++)
			if (route[i] == id)
				return true;
		return false;
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

	public static int getPreviousId(int[] route, int id) {
		int i = 0;
		while (i < route.length && route[i] != id)
			i++;
		if (i == route.length || i == 0)
			return MWACRouteAssistant.UNDEFINED_ID;
		else
			return route[i - 1];
	}

	public static int getNextId(int[] route, int id) {
		int i = 0;
		while (i < route.length - 1 && route[i] != id)
			i++;
		if (i >= route.length - 1)
			return UNDEFINED_ID;
		else
			return route[i + 1];
	}

	public static int getFirstId(int[] route) {
		if (route.length < 1)
			return UNDEFINED_ID;
		else
			return route[0];
	}

	public static int[] subRoute(int[] route, int indexLastIncludedItem) {
		int[] res = new int[indexLastIncludedItem];
		for (int i = 0; i < indexLastIncludedItem; i++)
			res[i] = route[i];
		return res;
	}

	public static int getLastId(int[] route) {
		if (route.length < 1)
			return UNDEFINED_ID;
		else
			return route[route.length - 1];
	}

	public static ByteBuffer routeToByteBuffer(int[] route) {
		ByteBuffer bytes = ByteBuffer.allocate(4 * route.length);
		for (int i = 0; i < route.length; i++)
			bytes.putInt(route[i]);
		return bytes;
	}

	public static int[] removeHead(int[] route) {
		int[] res = new int[route.length - 1];
		for (int i = 1; i < route.length; i++)
			res[i - 1] = route[i];
		return res;
	}
	
	public static boolean equals(int[] route1,int[] route2)
	{
		if(route1.length!=route2.length) return false;
		for(int i=0;i<route1.length;i++) if(route1[i]!=route2[i]) return false;
		return true;
	}
}
