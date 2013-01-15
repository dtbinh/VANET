package simulation.solutions;

import java.util.ListIterator;
import java.util.Vector;

import simulation.entities.Agent;
import simulation.messages.Frame;
import simulation.messages.Message;


/**
 * This class allow to link a solution with the simulator
 * @author Jean-Paul Jamont
 */ 
public class Solution
{

	/** Solution describer */
	SolutionDescriber solutionDescriber;

	/** the agents classes */
	private Vector<Class> agents;
	/** the object classes */
	private Vector<Class> objects;
	/** the agents classes */
	private Class frame;

	/** 

	/**
	 * Suscribe your solution.
	 * When the simulation start, your extended classes will replace the defaut classes
	 * @param solutionDescriber the solution description
	 * @param agents a vector of the different classes of the different type of agents 
	 * @param objects a vector of the different classes of the different type of objects 
	 * @param frame Class of the frame which allow to interpret received bytes from real world agents
		 */
	public Solution(SolutionDescriber solutionDescriber, Vector<Class> agents, Vector<Class> objects, Class frame) throws SolutionException,SolutionDescriberException
	{
		this.solutionDescriber=solutionDescriber;
		if (solutionDescriber==null) throw(new SolutionDescriberException());

		if (agents==null) this.agents=new Vector<Class>(); else this.agents=agents;
		if (objects==null) this.objects=new Vector<Class>(); else this.objects=objects;
		this.frame=frame;
		}

	
	/**
	 * Suscribe your solution.
	 * When the simulation start, your extended classes will replace the defaut classes
	 * @param solutionDescriber the solution description
	 * @param agents a vector of the different classes of the different type of agents 
	 * @param objects a vector of the different classes of the different type of objects 
	 */
	public Solution(SolutionDescriber solutionDescriber, Vector<Class> agents, Vector<Class> objects) throws SolutionException,SolutionDescriberException
	{
		this(solutionDescriber,agents,objects,null);
	}

	
	/**
	 * Suscribe your solution.
	 * When the simulation start, your extended classes will replace the defaut classes
	 * @param solutionDescriber the solution description
	 * @param agents a vector of the different classes of the different type of agents 
	 */
	public Solution(SolutionDescriber solutionDescriber, Vector<Class> agents) throws SolutionException,SolutionDescriberException
	{
		this(solutionDescriber,agents,null);
	}

	/**
	 * Suscribe your solution.
	 * When the simulation start, your extended classes will replace the defaut classes
	 * @param solutionDescriber the solution description
	 * @param agents the class of the alone type of agent of your solution
	 * @param frame Class of the frame which allow to interpret received bytes from real world agents
	 */
	public Solution(SolutionDescriber solutionDescriber, Class agent, Class baseFrame) throws SolutionException,SolutionDescriberException
	{
		this(solutionDescriber,null,null,baseFrame);
		this.agents.add(agent);
	}
	
	/**
	 * Suscribe your solution.
	 * When the simulation start, your extended classes will replace the defaut classes
	 * @param solutionDescriber the solution description
	 * @param agents the class of the alone type of agent of your solution
	 */
	public Solution(SolutionDescriber solutionDescriber, Class agent) throws SolutionException,SolutionDescriberException
	{
		this(solutionDescriber,null,null,null);
		this.agents.add(agent);
	}
	/*
	 * This method is called before the construction of the environment (it is the 1st method call, before run the scenario)
	 */
	public void init_pre(){}

	/*
	 * This method is called after the construction of the environment (before the dynamic part of the scenario)
	 */
	public void init_post(){}


	/** Get the authors' name of the solution
	 * @return the authors' names 
	 */
	public String getAuthor()
	{
		return this.solutionDescriber.getAuthor();
	}

	/** Get the implementation's name of the solution
	 * @return the implementation's name 
	 */
	public String getImplementationName()
	{
		return this.solutionDescriber.getImplementationName();
	}

	/** Get the version of the solution
	 * @return the version of the solution
	 */
	public String getVersion()
	{
		return this.solutionDescriber.getVersion();
	}



	/**
	 * Returns a String object representing the specified integer.
	 * @return The string representation
	 */
	public String toString()
	{

		return solutionDescriber.toString()+"\t"+this.agentListToString()+"\t"+this.objectListToString();
	}


	/** 
	 * returns the agent Class associated to solution base frame. 
	 * This method is only use when bytes are received by an attached real world agent or object.
	 * @param className string representation of the class
	 * @return the Class associated to the class string representation
	 * @throws ClassNotFoundException
	 */
	public Class getBaseFrame()
	{
		return this.frame;
	}

	/** 
	 * returns the agent Class associated to the class string representation
	 * @param className string representation of the class
	 * @return the Class associated to the class string representation
	 * @throws ClassNotFoundException
	 */
	public Class getTheAgentClass(int agentClassId) throws ClassNotFoundInSolution
	{
		if (agentClassId>this.agents.size()) throw new ClassNotFoundInSolution("agent class identified by int#"+agentClassId);
		return this.agents.get((agentClassId<=0 ? 0 : agentClassId-1));
	}
	/** 
	 * returns the agent Class associated to the class string representation
	 * @param className string representation of the class
	 * @return the Class associated to the class string representation
	 * @throws ClassNotFoundException
	 */
	public Class getTheAgentClass(String agentClassName) throws ClassNotFoundInSolution
	{
		System.out.println("Recherche de la classe "+agentClassName);
		Class cl;
		ListIterator<Class> iter = this.agents.listIterator();
		while(iter.hasNext())
		{
			cl=iter.next();
			if (agentClassName.equals(cl.getSimpleName())) 
			{
				System.out.println("On a trouvé "+cl.getSimpleName());
				return cl;
			}
		}
		throw new ClassNotFoundInSolution(agentClassName);
	}

	/** 
	 * returns the agent Class associated to the class string representation
	 * @param className string representation of the class
	 * @return the Class associated to the class string representation
	 * @throws ClassNotFoundException
	 */
	public Class getTheObjectClass(int objectClassId) throws ClassNotFoundInSolution
	{
		if (objectClassId>this.agents.size()) new ClassNotFoundInSolution("object class identified by int#"+objectClassId);
		return this.agents.get(objectClassId<=0 ? 0 : objectClassId-1);
	}
	/** 
	 * returns the agent Class associated to the class string representation
	 * @param className string representation of the class
	 * @return the Class associated to the class string representation
	 * @throws ClassNotFoundException
	 */
	public Class getTheObjectClass(String className) throws ClassNotFoundInSolution
	{
		Class cl;	
		ListIterator<Class> iter =  this.objects.listIterator();
		while(iter.hasNext())
		{
			cl=iter.next();
			if (className.equals(cl.getSimpleName())) return cl;
		}

		throw new ClassNotFoundInSolution(className);
	}

	public String agentListToString() {
		String res="";
		if(this.agents.size()==0)
			return "null";
		else
			res+=this.agents.get(0).getSimpleName();
		for(int i=1;i<this.agents.size();i++) res+=", "+this.agents.get(i).getSimpleName();
		return res;
	}

	public String objectListToString() {
		String res="";
		if(this.objects.size()==0)
			return "null";
		else
			res+=this.objects.get(0).getSimpleName();
		for(int i=1;i<this.objects.size();i++) res+=", "+this.objects.get(i).getSimpleName();
		return res;
	}

}