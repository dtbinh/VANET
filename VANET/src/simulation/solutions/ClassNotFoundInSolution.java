package simulation.solutions;

public class ClassNotFoundInSolution extends Exception{

	private String className;
	
	public ClassNotFoundInSolution(String className)
	{
		this.className = className;
	}
	public String toString()
	{
		return "Class "+className+" not found in the specified Solution";
	}
}
