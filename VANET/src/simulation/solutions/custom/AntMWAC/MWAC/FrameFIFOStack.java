package simulation.solutions.custom.AntMWAC.MWAC;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;



import simulation.solutions.custom.AntMWAC.MWAC.Messages.MWACFrame;
import simulation.solutions.custom.AntMWAC.MWAC.Messages.MWACMessage;

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


	// M�thode push
	//  empile un objet
	public synchronized void push(MWACFrame f)
	{
		//if(f.getReceiver()==Frame.BROADCAST || f.getReceiver()==getId()) 
		this.stack.add(f);
	}

	// M�thode pop
	//   d�pile un objet
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
			if(msg.getType()==MWACMessage.msgCONFLICT_RESOLUTION) lst.add(new TripletIdRoleGroup(msg.getSender(),AntMWACAgent.roleREPRESENTATIVE,msg.getSender()));
		}
		return lst;

	}

	// M�thode isEmpty
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