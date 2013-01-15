package simulation.solutions.custom.RecMAS.RecursiveAgent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import simulation.solutions.custom.RecMAS.RecursiveAgent.Messages.RecMASMessage;




/**
 * a FIFO stack of frame (used to memorize received frame)
 * @author Jean-Paul Jamont
 */
public class SendedRecMASMessageFIFOStack
{
	private LinkedList<RecMASMessage> stack;

	/*
	 * Constructor
	 */
	public SendedRecMASMessageFIFOStack()
	{
		this.stack = new LinkedList<RecMASMessage>();
	}


	// Méthode push
	//  empile un objet
	public synchronized void push(RecMASMessage f)
	{
		Iterator<RecMASMessage> iter = this.stack.iterator();
		int sender = f.getSender();
		int receiver = f.getReceiver();
		int type = f.getType();
		
		this.stack.add(f);
	}

	

	// Méthode pop
	//   dépile un objet
	public synchronized RecMASMessage pop()
	{
		try
		{
			RecMASMessage res = this.stack.getFirst();
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
		String res="Number of waiting message : "+this.stack.size()+"\n";
		Iterator<RecMASMessage> iter = this.stack.iterator();

		while(iter.hasNext())
			res+=iter.next().toString()+"\n";

		return res;

	}


	public int size() {
		// TODO Auto-generated method stub
		return this.stack.size();
	}

	//		public ListIterator<ASTRO_Message> listIterator()
	//		{
	//			return this.stack.listIterator();
	//		}
}