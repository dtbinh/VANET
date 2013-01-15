package simulation.messages.system;

public class NotSystemFrameException extends Exception{

	private String err;
	public NotSystemFrameException(String err)
	{
		this.err=err;
	}

	public String toString()
	{
		return err;
	}
}
