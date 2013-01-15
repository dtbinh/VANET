package simulation.multiagentSystem;

import simulation.entities.Agent;
import simulation.utils.IntegerPosition;

/*
 * Created on 15 juin 2009
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * the simulated agent 
 * @author Jean-Paul Jamont
 */
public class SimulatedAgent extends SimulatedObject {

	/** the agent associated thread */
	private Thread thAgent = null;

	/** Constructs a simulated agent
	 * @param obj the object
	 * @param pos its associated position
	 */
	public SimulatedAgent(Agent obj, IntegerPosition pos)
	{
		super(obj,pos);

	}

	/** Constructs a simulated object
	 * @param obj the object
	 * @param x absissa of the simulated agent
	 * @param x ordinate of the simulated agent
	 */
	public SimulatedAgent(Agent obj, int x, int y)
	{
		super(obj,new IntegerPosition(x,y));

	}

	/** Create the thread associated to the agent and associate this thread to the specified thead group
	 * @param threadGroup the group of thread which must include the new thread
	 */
	public void createThread(AgentThreadGroup threadGroup)
	{
		this.thAgent = new Thread((Agent)object);
		this.thAgent.setName("Ag#"+this.getObject().getSystemId());

		((Agent) object).setAgentThreadGroup(threadGroup);
		threadGroup.add(this.thAgent);
	}

	/** returns the string signature of the simulated agent
	 * @return the string signature of the simulated agent
	 */
	public String toString()
	{
		return "Simulated agent "+this.object.getUserId()+" at position "+this.position.toString();
	}

}
