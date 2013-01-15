package simulation.solutions.custom;

import simulation.solutions.*;

/**
 * This class allow to link a solution with ImplementedSolution.ini .
 * @author Jean-Paul Jamont
 */ 
public abstract class SolutionItem
{

	/** the solution */
	private Solution soluce;

	/** contructs the SolutionItem */
	public SolutionItem()
	{
		this.soluce=null;
	}

	/** set the solution 
	 * @param soluce the solution
	 */
	public void setSolution(Solution soluce)
	{
		this.soluce=soluce;
	}

	/** 
	 * returns the solution 
	 *	@return the solution
	 */
	public Solution getSolution() throws SolutionException
	{
		if (this.soluce==null) throw new SolutionException();
		return this.soluce;
	}

}