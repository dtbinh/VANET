package simulation.environment.namedAttributeEnvironment;

/**
 * exception throwed when a XML tag is not found in a XML flow 
 * @author JPeG
 *
 */
public class XMLTagNotFound extends Exception {

	/** the not found tag */
	private String tag;

	/**
	 * default constructor
	 * @param tag the not found tag
	 */
	public XMLTagNotFound(String tag)
	{
		this.tag=tag;
	}

	/**
	 * returns the not found tag
	 * @return the not found tag
	 */
	public String getTag()
	{
		return this.tag;
	}

	/**
	 * returns this object under a string representation
	 * @return the string representation
	 */
	public String toString()
	{
		return "Attribute <"+this.tag+"> not found";
	}
}
