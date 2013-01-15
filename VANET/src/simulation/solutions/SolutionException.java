package simulation.solutions;

/**
 * This exception is throwed when a robot implementation not extend the class <i>Robot</i>
 * In a solution, you must put a robot behaviour in a class which extends <i>Robot</i>
 * @author Jean-Paul Jamont
 */ 
public class SolutionException extends Exception
{
	/** default constructor */
	public SolutionException()
	{
		super();
	}

	/** string parametrized constructor */
	public SolutionException(String msg)
	{
		super(msg);
	}
}