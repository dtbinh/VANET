package simulation.scenario;

/** exception raised when a method is not found in a class 
 * @author Jean-Paul Jamont
 */
public class MethodNotFoundException extends Exception{

	/** the name of the not found method */
	private String methodName;
	/** class where the method has been searched */
	private String className;
	/** list of the type of parameters */
	private String paramTypeList;

	/** parametrized constructor 
	 * @param className name of the class which must contain the method
	 * @param methodName name of the not founded method
	 * */
	public MethodNotFoundException(String className,String methodName,String paramTypeList)
	{
		this.methodName=methodName;
		this.className=className;
		this.paramTypeList=paramTypeList;
	}

	/**
	 * returns a string representation of this exception
	 @return the string representation
	 */
	public String toString()
	{
		return this.getClass().getName()+": Method "+methodName+" with the param list "+paramTypeList+" not found in class "+className;
	}
}
