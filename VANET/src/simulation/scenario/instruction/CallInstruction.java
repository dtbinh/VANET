package simulation.scenario.instruction;

import java.util.Vector;

/** an instruction which is a method call to an object/agent
 * @author Jean-Paul Jamont
 */
public class CallInstruction extends Instruction{

	/** method name */
	public String method;
	/** method parameters*/
	public Vector<Parameter>  param;


	/**
	 * Default constructor
	 * @param entity the entity where the method is applied
	 * @param method the method name
	 * @param param the parameter list applied to the method
	 */
	public CallInstruction(String entity,String method, Vector<Parameter>  param)
	{
		super(entity);
		this.method=method;
		this.param=param;
	}


	/**
	 * returns a string representation of the instruction
	 @return the string representation
	 */
	public String toString()
	{
		return "Call instruction on object "+this.entity+" of the method "+this.method+" with the parameter list:\r\n"+this.param;
	}
}
