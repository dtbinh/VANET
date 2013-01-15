/**
 * 
 */
package simulation.solutions.custom.TrustedRoutingMWAC;

import java.util.ArrayList;

/**
 * @author Anca
 *
 */
public class WatchList {

	ArrayList<WatchEntry> entries = new ArrayList<WatchEntry>();
	
	public WatchList(){
		super();
	}
	
	public synchronized boolean addEntry(WatchEntry we){
		return entries.add(we);
	}
	
	public synchronized boolean removeEntry(WatchEntry we){
		return entries.remove(we);
	}
	
	public synchronized ArrayList<WatchEntry> getSubset(int nodeId){
		ArrayList<WatchEntry> subset = new ArrayList<WatchEntry>();
		
		for(WatchEntry we : entries)
			if(we.getNodeId() == nodeId)
				subset.add(we);
		
		return subset;
	}
	
	public String toHTML(){
		String res = "<b>Watch list </b> (" + entries.size() + ")<br>";

		res += "<table border=1>";
		res += "<tr><td>id</td><td>interaction #</td><td> exp date </td><td>message</td></tr>";
		for(WatchEntry we : entries)
			res += we.toString();
		res+= "</table>";
		
		return res;
	}
}
