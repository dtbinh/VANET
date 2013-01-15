package simulation.messages.system;

public class NotUnderstantableSystemFrameException extends Exception{

	private int functionCode;
	private int subFunctionCode;
	private String err;

	public NotUnderstantableSystemFrameException(int functionCode, int subFunctionCode, String err)
	{
		this.functionCode=functionCode;
		this.subFunctionCode=subFunctionCode;
		this.err=err;
	}

	public String toString()
	{
		return "In system frame (function="+this.functionCode+",subfunction="+this.subFunctionCode+") , "+err;
	}
}
