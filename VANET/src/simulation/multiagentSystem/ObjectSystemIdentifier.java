package simulation.multiagentSystem;

import java.io.Serializable;

/**
 * System identifier of the object
 * @author Jean-Paul Jamont
 */
public class ObjectSystemIdentifier implements Serializable {

	/** initial system identifier */
	public final static int INITIAL_SYSTEM_IDENTIFIER = 1;
	 
	/** identifier associated to an identifier error */
	public final static int BAD_SYSTEM_IDENTIFIER = -1;
	
	/** system identifier */
	private int id;
	
	/** Identifier of the object */
	private static int next_given_system_id = ObjectSystemIdentifier.INITIAL_SYSTEM_IDENTIFIER;
	
	/**
	 * Default constructor
	 */
	public ObjectSystemIdentifier()
	{
		this.id=next_given_system_id++;
	}
	
	/**
	 * 	Integer identifier Parametrized constructor 
	 */
	public ObjectSystemIdentifier(int sys_id)
	{
		this.id=sys_id;
	}

	/**
	 * returns the system identifier
	 * @return the system identifier
	 */
	public int getId()
	{
		return this.id;
	}
	
	/**
	 * is an identifier equals to another
	 * @param sys_id the compared system identifier 
	 * @return true if the two identifier are the same
	 */
	public boolean equals(ObjectSystemIdentifier sys_id)
	{
		return (this.id==sys_id.getId());
	}
	
	/**
	 * is an identifier equals to another
	 * @param sys_id the compared system identifier 
	 * @return true if the two identifier are the same
	 */
	public boolean equivalent(int int_sys_id)
	{
		return (this.id==int_sys_id);
	}
	
	/**
	 * is an identifier equals to another
	 * @param sys_id the compared system identifier 
	 * @return returns (this.id - sys_id.id). A null value signifies the identifiers are the same, a positive value signifies the parameter identifier is lower than the object identifier, a negative value else. 
	 * 
	 */
	public int compare(ObjectSystemIdentifier sys_id)
	{
			return (this.id-sys_id.getId());
	}
	
	/**
	 * returns the object signature
	 * @return the string signature. 
	 * 
	 */
	public String toString()
	{
			return ""+this.id;
	}
	
	/**
	 * Initialize the numerotation of the system id
	 */
	public static void init()
	{
		System.out.println("Initialization of the system id counter");
		ObjectSystemIdentifier.next_given_system_id=ObjectSystemIdentifier.INITIAL_SYSTEM_IDENTIFIER;
	}
	/**
	 * is an identifier equals to another
	 * @param sys_id the compared system identifier 
	 * @return true if the two identifier are the same
	 */
//	public ObjectSystemIdentifier clone()
//	{
//		return new ObjectSystemIdentifier(this.id);
//	}
}
