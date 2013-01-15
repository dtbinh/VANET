package simulation.multiagentSystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import simulation.multiagentSystem.ObjectSystemIdentifier;
import simulation.multiagentSystem.ObjectUserIdentifier;
import simulation.utils.IntegerPosition;


/** Manage the list of object in the MAS */
public class ObjectListManager implements Cloneable{



	/** the collection of object */
	private List<ObjectAndItsNeighboorhood> itemList;
	/** to increase efficiency of research */
	private HashMap<Integer,ObjectSystemIdentifier> int_to_ObjectSystemIdentifier_Map;


	/**
	 * Default constructor
	 */
	public ObjectListManager()
	{
		this.itemList=Collections.synchronizedList(new LinkedList<ObjectAndItsNeighboorhood>());
		int_to_ObjectSystemIdentifier_Map = new HashMap<Integer,ObjectSystemIdentifier>(); 
	}

	/**
	 * return the list of objects/neighboorhood
	 * @return a reference to the list
	 */
	public synchronized Object[] getClonedList()
	{
		Object[] res=null;
		synchronized(this.itemList) 
		{
		 res = this.itemList.toArray();
		}
		return  res;
	}

	/**
	 * add a new simulated object in the list
	 * @param obj the new simulated object
	 */
	public synchronized  void add(SimulatedObject obj)
	{
		synchronized(this.itemList) 
		{
			this.itemList.add(new ObjectAndItsNeighboorhood(obj));
		}
	}

	/**
	 * add a new simulated object in the list
	 * @param obj the new simulated object
	 */
	public synchronized void add(SimulatedObject obj,HashSet<ObjectSystemIdentifier> neighboorhood)
	{
		synchronized(this.itemList) 
		{
			this.itemList.add(new ObjectAndItsNeighboorhood(obj,neighboorhood));
			this.int_to_ObjectSystemIdentifier_Map.put(obj.getObject().getSystemId().getId(),obj.getObject().getSystemId());
		}
	}

	/**
	 * remove an object of the list
	 * @param sys_id system identifier of the object
	 */
	public synchronized void remove(ObjectSystemIdentifier sys_id)
	{
		synchronized(this.itemList) 
		{

			ListIterator<ObjectAndItsNeighboorhood> iter = this.itemList.listIterator();
			while(iter.hasNext())
			{
				if (iter.next().getSimulatedObject().getObject().getSystemId().equals(sys_id))
				{
					iter.remove();
					int_to_ObjectSystemIdentifier_Map.remove(sys_id.getId());
				}
			}
		}
	}


	/**
	 * return the searched object 
	 * @param sys_id system identifier of the object
	 * @return
	 */
	public  synchronized int getIndexOfAnObjectSystemIdentifier(ObjectSystemIdentifier sys_id)
	{
		ListIterator<ObjectAndItsNeighboorhood> iter=null;



		try
		{ 
			iter=this.itemList.listIterator(sys_id.getId()-1);// Often, sys_id is at the position sys_id
		}
		catch(Exception e)
		{
			iter=this.itemList.listIterator(this.size()-1);
		}

		ObjectAndItsNeighboorhood s;
		if (iter.hasNext()) 
			s=iter.next(); 
		else
			s=iter.previous();

		int compare = sys_id.compare(s.getSimulatedObject().getObject().getSystemId());

		if (compare==0)
			return iter.nextIndex()-1;
		else if (compare<0) 
		{
			while(iter.hasPrevious())
			{
				s=iter.previous();
				if (sys_id.equals(s.getSimulatedObject().getObject().getSystemId())) 
					return 1+iter.previousIndex();
			}
		}
		else
		{	
			while(iter.hasNext())
			{
				s=iter.next();
				if (sys_id.equals(s.getSimulatedObject().getObject().getSystemId())) 
					return iter.nextIndex()-1;
			}
		}

		System.out.println("!!!!!!!!!!! IMPOSSIBLE D'IDENTIFIER L'INDEX DE "+sys_id);
		return -1;


	}


	public  synchronized ObjectAndItsNeighboorhood get(IntegerPosition p,int radius)
	{
		ObjectAndItsNeighboorhood s;
		ListIterator<ObjectAndItsNeighboorhood> iter=this.itemList.listIterator();

		while(iter.hasNext())
		{
			s=iter.next();
			if(s.getSimulatedObject().getPosition().inCircleArea(p, radius)) return s;
		}

		return null;
	}

	/**
	 * return the searched object 
	 * @param sys_id system identifier of the object
	 * @return
	 */
	public synchronized ObjectAndItsNeighboorhood get(ObjectSystemIdentifier sys_id)
	{
		ListIterator<ObjectAndItsNeighboorhood> iter=null;


		try
		{ 
			iter=this.itemList.listIterator(sys_id.getId()-1);// Often, sys_id is at the position sys_id
		}
		catch(Exception e)
		{
			iter=this.itemList.listIterator(this.size()-1);
		}

		ObjectAndItsNeighboorhood s;
		if (iter.hasNext()) 
			s=iter.next(); 
		else if(iter.hasPrevious())
			s=iter.previous();
		else
		{
			System.out.println("\n!!!!!!!!!!! ObjectListManager IMPOSSIBLE DE TROUVER L'AGENT "+sys_id);
			return null;
		}

		int compare = sys_id.compare(s.getSimulatedObject().getObject().getSystemId());

		if (compare==0) 
			return s;
		else if (compare<0) 
		{
			while(iter.hasPrevious())
			{
				s=iter.previous();
				if (sys_id.equals(s.getSimulatedObject().getObject().getSystemId())) 
					return s;
			}
		}
		else
		{	
			while(iter.hasNext())
			{
				s=iter.next();
				if (sys_id.equals(s.getSimulatedObject().getObject().getSystemId())) 
					return s;
			}
		}

		System.out.println("!!!!!!!!!!! ObjectListManager IMPOSSIBLE DE TROUVER L'AGENT "+sys_id);
		return null;
	}

	/**
	 * returns the 'int' associated system_identifier 
	 */
	public ObjectSystemIdentifier getObjectSystemIdentifier(int int_sys_id)
	{
		return this.int_to_ObjectSystemIdentifier_Map.get(int_sys_id);
	}

	/**
	 * return the number of object int the list
	 * @return
	 */
	public synchronized int size()
	{
		return  itemList.size();
	}

	/**
	 * remove all item of the list
	 */
	public synchronized void clear()
	{

			this.itemList.clear();
	
	}


	/**
	 * returns the string signature
	 * @return the string signature
	 */
	public synchronized  String toString()
	{

		String str="";
		ListIterator<ObjectAndItsNeighboorhood> iter=null;

			while(iter.hasNext()) str+=iter.next().toString()+"\n";
	
		return str;
	}

	/**
	 * Clone method
	 */
	public synchronized ObjectListManager clone() 
	{
		ObjectListManager res = null;


			try 

			{
				res = (ObjectListManager) super.clone();
			} 
			catch(CloneNotSupportedException e)
			{
				e.printStackTrace(System.err);
			}
		

		return res;
	}
}
