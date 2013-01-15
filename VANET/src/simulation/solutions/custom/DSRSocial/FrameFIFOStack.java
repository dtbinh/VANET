package simulation.solutions.custom.DSRSocial;

import java.util.LinkedList;
import java.util.ListIterator;

import simulation.solutions.custom.DSRSocial.Messages.DSRSocialFrame;



/**
 * a FIFO stack of frame (used to memorize received frame)
 */
public class FrameFIFOStack
{
	private LinkedList<DSRSocialFrame> stack;

	/*
	 * Constructor
	 */
	public FrameFIFOStack()
	{
		this.stack = new LinkedList<DSRSocialFrame>();
	}


	// Méthode push
	//  empile un objet
	public synchronized void push(DSRSocialFrame f)
	{
		//if(f.getReceiver()==Frame.BROADCAST || f.getReceiver()==getId()) 
		this.stack.add(f);
	}

	// Méthode pop
	//   dépile un objet
	public synchronized DSRSocialFrame pop()
	{
		try
		{
			DSRSocialFrame res = this.stack.getFirst();
			this.stack.removeFirst();
			return res;

		}
		catch (Exception e)
		{
			return null;
		}
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
	
	public String toString()
	{
		String res="";
		ListIterator<DSRSocialFrame> iter=this.stack.listIterator();
		while(iter.hasNext()) res+=iter.next().toString()+"<BR>";
		return res;
	}
}