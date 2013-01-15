package simulation.scenario;

/** exception raised when a variable is not found (i.e. the scenario contain a method applied to a not defined agent
 * @author Jean-Paul Jamont
 */
public class VariableNotFoundException extends Exception{
	
	/** name of the not founded variable */
	private String varName;
	
	/** parametrized constructor 
	 * @param varName name of the not founded variable*/
	public VariableNotFoundException(String varName)
	{
		this.varName=varName;
	}
	
	/**
	 * returns a string representation of this exception
	 @return the string representation
	 */
	public String toString()
	{
		return this.getClass().getName()+": Variable/Object/Agent "+varName+" not found!";
	}
}
