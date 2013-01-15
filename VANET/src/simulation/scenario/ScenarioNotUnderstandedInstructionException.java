package simulation.scenario;

/** exception raised when an instruction is not understanted 
 * @author Jean-Paul Jamont
 */
public class ScenarioNotUnderstandedInstructionException extends Exception{

	/** the not understanded line */
	public String line;
	/** an extra possible comment */
	public String additionalMessage;

	/** parametrized constructor 
	 * @param line the line processed when the exception has been raised */
	public ScenarioNotUnderstandedInstructionException(String line)
	{
		this(line,"");
	}
	/** parametrized constructor
	 * @param line the line processed when the exception has been raised 
	 * @param additionalMessage an extra additionnal comment
	 */
	public ScenarioNotUnderstandedInstructionException(String line,String additionalMessage)
	{
		this.line=line;
		this.additionalMessage=additionalMessage;
	}

	/**
	 * returns a string representation of this exception
	 @return the string representation
	 */
	public String toString()
	{
		if (!this.line.isEmpty())
		{
			if(this.additionalMessage.isEmpty())
				return this.getClass().getName()+": '"+this.line+"' not understanded\n"+super.toString();
			else
				return this.getClass().getName()+": '"+this.line+"' not understanded\n"+super.toString()+"\n"+this.additionalMessage;
		}
		else
		{
			return this.getClass().getName()+": "+this.additionalMessage;
		}
	}
}



