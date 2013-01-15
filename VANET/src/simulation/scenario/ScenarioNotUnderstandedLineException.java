package simulation.scenario;

/** exception raised when the scenario player cannot understand a line of the scenario 
 * @author Jean-Paul Jamont
 */
public class ScenarioNotUnderstandedLineException extends Exception{

	/** the not understanted line*/
	public String line;
	/** message to help the scenario designer */
	public String msg;

	/** parametrized constructor 
	 * @param line the line processed when the exception has been raised */
	public ScenarioNotUnderstandedLineException(String line,String msg)
	{
		this.line=line;
		this.msg=msg;
	}

	/**
	 * returns a string representation of this exception
	 @return the string representation
	 */
	public String toString()
	{
		return this.getClass().getName()+": '"+this.line+"' not understanded because "+msg+"\n"+super.toString();
	}
}


