package simulation.scenario;

/** exception raised when the time associated to a line of the scenario has a bad format 
 * @author Jean-Paul Jamont
 */
public class ScenarioBadTimeFormatException extends Exception{

	/** the line which contain the bad time format*/
	String line;

	/** parametrized constructor 
	 * @param line the line processed when the exception has been raised */
	public ScenarioBadTimeFormatException(String line)
	{
		this.line=line;
	}

	/**
	 * returns a string representation of this exception
	 @return the string representation
	 */
	public String toString()
	{
		return "Bad time format in line '"+line+"'";
	}
}
