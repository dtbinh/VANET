package simulation.entities;

import java.awt.Color;
import java.lang.Thread;

import simulation.multiagentSystem.AgentThreadGroup;
import simulation.multiagentSystem.MAS;
import simulation.views.entity.EntityViewInterface;

/** 
 * Agent definition. We consider an agent like an autonomous behaviored object 
 * @author Jean-Paul Jamont
 */
public class Agent extends Object implements Runnable{

	/** The slot time used when an agent wait an extern event or sleep*/
	public static final int SLEEP_TIME_SLOT = 50;
	/** The agent must be killed -Thread implementation-*/
	private boolean kill;
	/** Allows to define the set of agent thread to simplify the implementation of suspend mode...*/
	private AgentThreadGroup agentThreadGroup;

	/** Builds the agent and its mininal state
	 * @param mas the multiagent system which includes this agent
	 * @param id the agent identifier
	 * @param energy energy level of the agent
	 * @param range range of the agent communication modules */ 
	public Agent(MAS mas,int id, Integer range)
	{
		super(mas,id,range);
		this.agentThreadGroup = null;
		this.kill=false;
	}
	
	public Agent(MAS mas,int id, Integer range,EntityViewInterface entityView)
	{
		this(mas,id,range);
		this.setNativeView(entityView);
	}


	/**  Launched by the call of start method*/
	public void run()
	{

		//System.out.println("Démarrage du thread "+this.id);

		// while the simulator don't requiere the agent death
		while(!this.kill && !this.agentThreadGroup.isStoped())
		{
			// Preparation of the others threads
			while( (this.agentThreadGroup.isSuspended()) && (!this.kill && !this.agentThreadGroup.isStoped())) try{Thread.sleep(SLEEP_TIME_SLOT);}catch(Exception e){};

			// Must be freezed?
			try{Thread.sleep(SLEEP_TIME_SLOT);}catch(Exception e){}
			//mas.notifyEvent(new SpyStateEvent(this.id,this.energy,this.range));
		}

		//System.out.println("Fin du thread "+this.id);
	}

	/** Allows to kill the agent*/
	public void kill()
	{
		this.kill=true;
	}

	/** Allows to know the agent kill is required*/
	public boolean isKilling()
	{
		return this.kill;
	}

	/** Allows to know the agent stop is required*/
	public boolean isStopping()
	{
		return this.agentThreadGroup.isStoped();
	}

	/** Allows to kill the agent*/
	public boolean isSuspending()
	{
		return this.agentThreadGroup.isSuspended();
	}



	/** sets the agent thread group
	 * @param agentThreadGroup the group of agent thread
	 */
	public void setAgentThreadGroup(AgentThreadGroup agentThreadGroup)
	{
		this.agentThreadGroup=agentThreadGroup;
	}

	/** returns the string signature of the agent which will be viewable under the spy windows
	 * @return the string signature of the agent
	 */
	public String toSpyWindows()
	{
		return "<HTML>"+"Agent #"+this.getUserId()+"<BR>  Energy="+String.format("%.2f",this.pourcentOfAvailableEnergy())+"<BR>  range="+this.getRange()+"</HTML>";
	}


	/** returns the string signature of the agent
	 * @return the string signature of the agent
	 */
	public String toString()
	{
		return "Agent #"+this.getUserId()+":  energy="+String.format("%.2f",this.pourcentOfAvailableEnergy())+"  range="+this.getRange();
	}
	

}
