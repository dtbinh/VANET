package simulation.environment;

/**
 * exception thowed when a attribute is not found in the environment model
 * @author JPeG
 */
public class AttributeNotFoundException extends Exception{

	/** name of the attribute */
	protected String attributeName;

	/**
	 * default constructor
	 * @param attributeName name of the attribute
	 */
	public AttributeNotFoundException(String attributeName)
	{
		this.attributeName = attributeName;
	}

	/**
	 * string representation of the exception
	 */
	public String toString()
	{
		return "Attribute named "+this.attributeName+" not found in the environment";
	}
}
