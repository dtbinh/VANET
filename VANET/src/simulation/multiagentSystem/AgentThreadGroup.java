

package simulation.multiagentSystem;

import java.util.ArrayList;
import java.util.ListIterator;


/** 
 * Allows to manage the agent threads through a group 
 * @author Jean-Paul Jamont
 */
public class AgentThreadGroup{

	/** this attribute allows to kill all the threads in the group*/
	private boolean kill;
	/** this attribute allows to suspend all the threads in the group*/
	private boolean isSuspended;
	/** the list of threads*/
	private ArrayList<Thread> threadList;
	/** has been started/activated */
	private boolean activated;

	/** Constuctor of the agent thread group*/
	public AgentThreadGroup()
	{
		this.threadList=new ArrayList<Thread>();
		this.isSuspended = false;
		this.kill=false;
		this.activated=false;
	}

	/** suspends the threads of the group */
	public void suspend()
	{
		this.isSuspended = true;
	}

	/** resumes the threads of the group */
	public void resume()
	{
		this.isSuspended = false;
	}

	/** stops the threads of the group */
	public void stop()
	{
		this.kill=true;
	}

	/** starts the threads of the group */
	public void start()
	{
		this.activated=true;
		Thread item;

		// Agent sleep during the start of other threads
		this.suspend();

		// Agent iterator
		ListIterator<Thread> iter = threadList.listIterator();

		// Quels sont les agents mis en jeux?
		while (iter.hasNext())
		{
			item=iter.next();
			item.start();
		}

		// On peut démarrer
		this.resume();
	}

	/** adds a thread in the group 
	 * @param t a thread to add in the group
	 */
	public void add(Thread t)
	{	this.threadList.add(t);
	if (this.activated) t.start();
	}

	/** allows to know if the threads are "stopped"
	 * @return true if the thread are stopped
	 */
	public boolean isStoped()
	{
		return this.kill;
	}

	/** allows to know if the threads are "suspended"
	 * @return true if the thread are suspended
	 */
	public boolean isSuspended()
	{
		return this.isSuspended;
	}
}
