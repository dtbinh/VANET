package simulation.solutions.custom.ACO_MWAC;

import java.util.LinkedList;


import simulation.solutions.custom.ACO_MWAC.Messages.ACO_Frame;


/**
 * a FIFO stack of frame (used to memorize received frame)
 * @author Yacine LATTAB & Ouerdia Slaouti
 */
public class ACO_FrameFIFOStack
{
	private LinkedList<ACO_Frame> stack;

	/*
	 * Constructor
	 */
	public ACO_FrameFIFOStack()
	{
		this.stack = new LinkedList<ACO_Frame>();
	}


	//empile une trame
	public synchronized void push(ACO_Frame f)
	{
		//if(f.getReceiver()==Frame.BROADCAST || f.getReceiver()==getId()) 
		this.stack.add(f);
	}

	//depile une trame
	public synchronized ACO_Frame pop()
	{
		try
		{
			ACO_Frame res = this.stack.getFirst();
			this.stack.removeFirst();
			return res;

		}
		catch (Exception e)
		{
			return null;
		}
	}

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
}