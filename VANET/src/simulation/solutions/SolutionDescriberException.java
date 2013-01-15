package simulation.solutions;

/**
 * This exception is throwed when your forget to give a SolutionDescriber to to solution manager
 * @author Jean-Paul Jamont
 */ 
public class SolutionDescriberException extends Exception
{
	/** default constructor */
	public SolutionDescriberException()
	{
		super();
	}

	/** string parametrized constructor */
	public SolutionDescriberException(String msg)
	{
		super(msg);
	}
}