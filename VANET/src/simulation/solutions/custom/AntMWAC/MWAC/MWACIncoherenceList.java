package simulation.solutions.custom.AntMWAC.MWAC;

import java.util.LinkedList;
import java.util.ListIterator;

import simulation.utils.aDate;

/**
 * Allow to manage notification of suspected organizational incoherence
 * @author JPeG
 *
 */
public class MWACIncoherenceList {

	/** maximum time authorised to try to find a route to the suspected group */
	public final static int MAX_AUTHORISED_TIME_TO_CONTACT_SUSPICIOUS_GROUP = 5000;
	/** possible returned result when we try to find a real inconsistency*/
	public final static int NO_REAL_INCOHERENCE = -1;

	/** the list which regroup all suspected incoherence */
	public LinkedList<MWACIncoherenceListItem> incoherenceList;

	/**
	 * default constructor
	 */
	public MWACIncoherenceList()
	{
		this.incoherenceList=new LinkedList<MWACIncoherenceListItem>();
	}

	/**
	 * allows to know if a group is suspected to be at the origin of an inconsistancy
	 * @param group the inspected group
	 * @return true if the group is already suspected
	 */
	public boolean isSuspectedGroup(int group)
	{
		for(int i=0;i<this.incoherenceList.size();i++)
			if(this.incoherenceList.get(i).suspiciousGroup==group) return true;
		return false;
	}

	/**
	 * throwsProblem
	 */
	public int existsProblem()
	{
		//if(!this.incoherenceList.isEmpty()) System.out.println("*********\r\n"+this.toString()+"*********\n");

		ListIterator<MWACIncoherenceListItem> iter=this.incoherenceList.listIterator();
		MWACIncoherenceListItem item;

		while(iter.hasNext())
		{
			item=iter.next();
			if ((new aDate(item.date)).differenceToMS(new aDate())>MWACIncoherenceList.MAX_AUTHORISED_TIME_TO_CONTACT_SUSPICIOUS_GROUP) 
			{
				//System.out.println("Suppression de "+item.toString());
				//int res = item.detectorAgents[0];
				int res = item.suspiciousGroup;
				iter.remove();
				return res;
			}
		}

		return MWACIncoherenceList.NO_REAL_INCOHERENCE;
	}

	/**
	 * allow to know is the incoherence list is empty... or not!
	 * @return true if the incoherence list is empty
	 */
	public boolean isEmpty()
	{
		return this.incoherenceList.isEmpty();
	}

	/**
	 * to consider a group as not suspicious
	 * @param group the group that is not suspicious
	 */
	public void contactedGroup(int group)
	{
		ListIterator<MWACIncoherenceListItem> iter=this.incoherenceList.listIterator();
		MWACIncoherenceListItem item;

		while(iter.hasNext())
		{
			item=iter.next();
			if (item.suspiciousGroup==group) 
			{
				iter.remove();
				return;
			}
		}
	}

	/** add a suspected incoherence */
	//	public synchronized void add(int detectorAgent,int[] suspectedGroups)
	//	{
	//		MWACIncoherenceListItem item;
	//		ListIterator<MWACIncoherenceListItem> iter;
	//		boolean finded;
	//
	//		// For all suspected group
	//		for(int i=0;i<suspectedGroups.length;i++)
	//		{
	//			finded=false;
	//			iter = incoherenceList.listIterator();
	//
	//			// look if the suspected group is already suspected 
	//			while(!finded && iter.hasNext())
	//			{
	//				item=iter.next();
	//				// The suspected group already exists
	//				if (item.suspiciousGroup==suspectedGroups[i])
	//				{
	//					item.add(detectorAgent);
	//					finded=true;
	//				}
	//			}
	//			// The suspected group don't exists 
	//			if(!finded) this.incoherenceList.add(new MWACIncoherenceListItem(suspectedGroups[i],detectorAgent));
	//
	//		}
	//	}
	public synchronized void add(int detectorAgent,int suspectedGroup)
	{
		MWACIncoherenceListItem item;
		ListIterator<MWACIncoherenceListItem> iter;
		boolean finded=false;

		// For all suspected group
		iter = incoherenceList.listIterator();

		// look if the suspected group is already suspected 
		while(!finded && iter.hasNext())
		{
			item=iter.next();
			// The suspected group already exists
			if (item.suspiciousGroup==suspectedGroup)
			{
				item.add(detectorAgent);
				finded=true;
			}
		}
		// The suspected group don't exists 
		if(!finded) this.incoherenceList.add(new MWACIncoherenceListItem(suspectedGroup,detectorAgent));
	}

	/**
	 * returns the string signature of the incoherence list
	 * @return the string signature
	 */
	public String toString()
	{
		String res="";
		for(int i=0;i<this.incoherenceList.size();i++) res+=this.incoherenceList.get(i).toString()+"\r\n";
		return res;
	}

	/** item of a MWAC incoherence list */
	private class MWACIncoherenceListItem
	{
		/** suspicious group */
		public int suspiciousGroup;
		/** list of agents detectors */
		public int[] detectorAgents;
		/** date of the begin of the search */
		public long date;

		public MWACIncoherenceListItem(int suspiciousGroup, int detectorAgent)
		{
			this.suspiciousGroup=suspiciousGroup;
			this.detectorAgents=new int[1];
			this.detectorAgents[0]=detectorAgent;
			this.date=aDate.getCurrentTimeInMS();
		}

		/**
		 * allows to knows if an detector agent is already contained in the list of detector
		 * @param detectorAgent identifier of the searched detector agent
		 * @return true if the agent is finded in the list of detectors
		 */
		public boolean contains(int detectorAgent)
		{
			return MWACRouteAssistant.contains(this.detectorAgents, detectorAgent);
		}

		/**
		 * Add a detector agent in a list of detector agents
		 * @param detectorAgent identifier of the detector agent
		 */
		public void add(int detectorAgent)
		{
			if (!contains(detectorAgent)) MWACRouteAssistant.add(detectorAgents, detectorAgent);
		}

		/**
		 * returns the string signature of an item
		 * @returns string signature of the item
		 */
		public String toString()
		{
			return "Suspected group:"+this.suspiciousGroup+" Detectors:"+MWACRouteAssistant.routeToString(this.detectorAgents)+" Date:"+aDate.msToHHMMSS(this.date);
		}

	}

}
