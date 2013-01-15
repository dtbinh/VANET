package simulation.scenario.instruction;

import java.util.Vector;

/** an instruction which give born to an object/agent 
 * @author Jean-Paul Jamont
 */
public class CreationInstruction extends Instruction{
	
	/** class name of the object */
	public String className;
	/** parameters list of the associated constructor */
	public Vector<Parameter>  param;
	
	
	/** default constructor
	 * @param entity entity name
	 * @param className class name
	 * @param param parameter to the constructor of the entity
	 */
	public CreationInstruction(String entity,String className, Vector<Parameter>  param)
	{
		super(entity);
		this.className=className;
		this.param=param;
	}

	/**
	 * returns a string representation of the instruction
	 @return the string representation
	 */
	public String toString()
	{
		return "Instruction de creation de la variable "+this.entity+" de classe "+this.className+" et dont les paramétres sont:\r\n"+this.param;
	}
}
