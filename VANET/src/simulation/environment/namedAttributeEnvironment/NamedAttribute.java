package simulation.environment.namedAttributeEnvironment;

/**
 * Attribute of a data in a named environment
 * @author JPeG
 */
public class NamedAttribute {
	/** name of the attribute */
	protected String name;
	
	/** parametrized constructor*/
	public NamedAttribute(String name)
	{
		this.name=name;
	}

	/**
	 * return the name of the attribute
	 * @return the name
	 */
	public String getName()
	{
		return this.name;
	}
	
	/**
	 * set the name of the attribute
	 * @param value the name
	 */
	public void setName(String value)
	{
		this.name=value;
	}
}
