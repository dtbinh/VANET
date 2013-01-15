package simulation.solutions.custom.RecMAS.RecursiveAgent.Messages;

public class Label {

	public int value;
	
	public Label(int value)
	{
		this.value=value;
	}
	public String toString()
	{
		return "#"+this.value;
	}
}
