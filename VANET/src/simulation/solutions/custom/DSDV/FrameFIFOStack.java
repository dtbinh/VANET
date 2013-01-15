package simulation.solutions.custom.DSDV;
import java.util.LinkedList;
import java.util.ListIterator;

import simulation.solutions.custom.DSDV.Messages.DSDV_Frame;

/**
 * une liste FIFO pour memoriser les trames reçues par un agent ( traitement en mode asynchrone)
 * @author Yacine LATTAB & Ouerdia SLAOUTI
 */

public class FrameFIFOStack {
	
	//la liste des trame DSDV non encore traitée
	private LinkedList<DSDV_Frame> stack;

	/**
	 * constructeur pour l'initialisation de la file d'attente des trames reçues
	 */
	public FrameFIFOStack()
	{
		this.stack = new LinkedList<DSDV_Frame>();
	}
	
	/**
	 * enfiler une nouvelle trame DSDV dans la file d'attente
	 * @param frame trame à enfiler
	 */
	public synchronized void push(DSDV_Frame frame)
	{
		this.stack.add(frame);
	}
	
	/**
	 * retirer, de la file d'attente, une trame DSDV 
	 * @return trame DSDV
	 */
	public synchronized DSDV_Frame pop()
	{
		try
		{
			DSDV_Frame res = this.stack.getFirst();
			this.stack.removeFirst();
			return res;
		}
		catch (Exception e)
		{
			return null;
		}
	}

	/**
	 * indique si la liste des trames reçues est vide ou non 
	 * @return true ou false
	 */
	public boolean isEmpty()
	{
		return (this.stack.size()==0);
	}

	/**
	 * pour supprimer (reinitialiser) la liste des trames reçues 
	 * @return true ou false
	 */
	public synchronized void removeAll()
	{
		this.stack.clear();
	}
	
	/**
	 * ertourne de la liste en String 
	 * @return la representation en String
	 */
	public String toString()
	{
		String res="";
		ListIterator<DSDV_Frame> iter=this.stack.listIterator();
		while(iter.hasNext()) res+=iter.next().toString()+"<BR>";
		return res;
	}
}
