package simulation.solutions.custom.DSR;

import java.util.LinkedList;
import java.util.ListIterator;

import simulation.solutions.custom.DSR.Messages.DSRFrame;


/**
 * a FIFO stack of frame (used to memorize received frame). Allow to process frame after their reception.
 * @author Jean-Paul Jamont
 */
public class FrameFIFOStack
{
	/** list of the received frame */
	private LinkedList<DSRFrame> stack;

	/**
	 * default constructor
	 */
	public FrameFIFOStack()
	{
		this.stack = new LinkedList<DSRFrame>();
	}


	
	/**
	 * push a DSR frame in the stack (store a value)
	 * @param f the DSR frame
	 */
	public synchronized void push(DSRFrame f)
	{
		this.stack.add(f);
	}

	/**
	 * remove a value from the stack
	 * @return a DSR frame
	 */
	public synchronized DSRFrame pop()
	{
		try
		{
			DSRFrame res = this.stack.getFirst();
			this.stack.removeFirst();
			return res;
		}
		catch (Exception e)
		{
			return null;
		}
	}

	/**
	 * is the stack empty?
	 * @return true if the stack is empty, false else
	 */
	public boolean isEmpty()
	{
		return (this.stack.size()==0);
	}

	/**
	 * remove all frame from the stack
	 */
	public synchronized void removeAll()
	{
		this.stack.clear();
	}
	
	/**
	 * returns a string representation of the stack
	 * @return the string representation
	 */
	public String toString()
	{
		String res="";
		ListIterator<DSRFrame> iter=this.stack.listIterator();
		while(iter.hasNext()) res+=iter.next().toString()+"<BR>";
		return res;
	}
}