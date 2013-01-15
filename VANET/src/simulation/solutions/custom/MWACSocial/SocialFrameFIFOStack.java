package simulation.solutions.custom.MWACSocial;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;



import simulation.solutions.custom.MWAC.Messages.MWACFrame;
import simulation.solutions.custom.MWAC.Messages.MWACMessage;
import simulation.solutions.custom.MWACSocial.Messages.MWACSocialFrame;

/**
 * a FIFO stack of frame (used to memorize received frame)
 */
public class SocialFrameFIFOStack
{
	private LinkedList<MWACSocialFrame> stack;

	/*
	 * Constructor
	 */
	public SocialFrameFIFOStack()
	{
		this.stack = new LinkedList<MWACSocialFrame>();
	}


	// Méthode push
	//  empile un objet
	public synchronized void push(MWACSocialFrame f)
	{
		//if(f.getReceiver()==Frame.BROADCAST || f.getReceiver()==getId()) 
		this.stack.add(f);
	}

	// Méthode pop
	//   dépile un objet
	public synchronized MWACSocialFrame pop()
	{
		try
		{
			MWACSocialFrame res = this.stack.getFirst();
			this.stack.removeFirst();
			return res;

		}
		catch (Exception e)
		{
			return null;
		}
	}

	public ArrayList<SocialTripletIdRoleGroup> removeConflictResolutionMessageInQueue()
	{
		ArrayList<SocialTripletIdRoleGroup> lst = new ArrayList<SocialTripletIdRoleGroup>();
		ListIterator<MWACSocialFrame> iter=this.stack.listIterator();
		MWACMessage msg;
		while(iter.hasNext())
		{
			msg=MWACMessage.createMessage(iter.next().getData());
			if(msg.getType()==MWACMessage.msgCONFLICT_RESOLUTION) lst.add(new SocialTripletIdRoleGroup(msg.getSender(),MWACSocialAgent.roleREPRESENTATIVE,msg.getSender()));
		}
		return lst;

	}

	// Méthode isEmpty
	//  informe si la pile est vide ou non
	public boolean isEmpty()
	{
		return (this.stack.size()==0);
	}

	// Methode removeAll
	public synchronized void removeAll()
	{
		this.stack.clear();
	}

	//		public ListIterator<ASTRO_Message> listIterator()
	//		{
	//			return this.stack.listIterator();
	//		}
}