package simulation.environment.namedAttributeEnvironment;

/**
* interface to access (read & write) a value of a named attribute 
 * @author JPeG
 */
public interface NamedAttributeEnvironnementAccessInterface {

	
	/** attribute getter 
	 * @param attributeName name of the attribute
	 * @return the named attribute
	 */
	public NamedAttribute getAttribute(String attributeName) throws NamedAttributeNotFoundException;

	/** attribute setter 
	 * @param attributeName name of the attribute
	 * @param attribute attribute values
	 */
	public void setAttribute(String attributeName,NamedAttribute attribute) throws NamedAttributeNotFoundException;
}
