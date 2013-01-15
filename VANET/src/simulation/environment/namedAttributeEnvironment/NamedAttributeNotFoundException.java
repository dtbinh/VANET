package simulation.environment.namedAttributeEnvironment;

import simulation.environment.AttributeNotFoundException;

/**
 * exception throwed when a nammed attribute is not found
 * @author JPeG
 *
 */
public class NamedAttributeNotFoundException extends AttributeNotFoundException{

	/** 
	 * default constructor
	 * @param attributeName name of the environment attribute
	 */
	public NamedAttributeNotFoundException(String attributeName)
	{
		super(attributeName);
	}

	/**
	 * return the string representation of this exception
	 */
	public String toString()
	{
		return "Attribute named "+this.attributeName+" not found in the environment";
	}
}
