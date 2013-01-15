package simulation.scenario.instruction;

/** models a parameter of the scenario 
 * @author Jean-Paul Jamont
 */
public class Parameter {
	
	/** integer parameter  */
	public static final int INTEGER = 0;
	/** double parameter  */
	public static final int DOUBLE = 1;
	/** string parameter  */
	public static final int STRING = 2;
	/** reference parameter  */
	public static final int REFERENCE = 3;



	/** value of the parameter */
	public Object value;
	/** type of the parameter */
	public int type;

	/** default constructor
	 * @param type type of the parameter
	 * @param value value of the parameter
	 */
	public Parameter(int type, Object value)
	{
		this.type=type;
		this.value=value;
	}

	/**
	 * returns a string representation of the parameter
	 @return the string representation
	 */
	public String toString()
	{
		String type;
		switch(this.type)
		{
		case INTEGER: type="Integer"; break;
		case DOUBLE: type="Double";break;
		case STRING: type="String";break;
		case REFERENCE: type ="Reference";break;
		default : type="ERREUR";
		}
		if(this.value==null)
			return ("Type= void ");
		else
			return ("Type= ("+type+") "+type+"  Value="+value.toString());
	}
}
