package simulation.scenario;

/** exception raised when the scenario is finished : there no other instruction 
 * @author Jean-Paul Jamont
 */
public class FinishedScenarioException extends Exception{


	/** default constructor */
	public FinishedScenarioException(){}

	/**
	 * returns a string representation of this exception
	 @return the string representation
	 */
	public String toString()
	{
		return this.getClass().getName()+": Finished scenario!";
	}
}
