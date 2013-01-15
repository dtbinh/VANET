package simulation.solutions.custom.MWAC;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;



import simulation.solutions.custom.MWAC.Messages.MWACFrame;
import simulation.solutions.custom.MWAC.Messages.MWACMessage;

/**
 * a FIFO stack of frame (used to memorize received frame)
 * @author Jean-Paul Jamont
 */
public class FrameFIFOStack
{
	private LinkedList<MWACFrame> stack;

	/*
	 * Constructor
	 */
	public FrameFIFOStack()
	{
		this.stack = new LinkedList<MWACFrame>();
	}


	// Méthode push
	//  empile un objet
	public synchronized void push(MWACFrame f)
	{
		//if(f.getReceiver()==Frame.BROADCAST || f.getReceiver()==getId()) 
		this.stack.add(f);
	}

	// Méthode pop
	//   dépile un objet
	public synchronized MWACFrame pop()
	{
		try
		{
			MWACFrame res = this.stack.getFirst();
			this.stack.removeFirst();
			return res;

		}
		catch (Exception e)
		{
			return null;
		}
	}

	public ArrayList<TripletIdRoleGroup> removeConflictResolutionMessageInQueue()
	{
		ArrayList<TripletIdRoleGroup> lst = new ArrayList<TripletIdRoleGroup>();
		ListIterator<MWACFrame> iter=this.stack.listIterator();
		MWACMessage msg;
		while(iter.hasNext())
		{
			msg=MWACMessage.createMessage(iter.next().getData());
			if(msg.getType()==MWACMessage.msgCONFLICT_RESOLUTION) lst.add(new TripletIdRoleGroup(msg.getSender(),MWACAgent.roleREPRESENTATIVE,msg.getSender()));
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