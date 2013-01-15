package simulation.solutions.custom.RecMAS.RecursiveAgent;

import simulation.solutions.custom.RecMAS.MWAC.MWACAgent;
import simulation.utils.aDate;

public class RecursiveState {

	
	public static int MIN_STABILITY_DELAY_IN_MS = 12000;
	
	private int FComplexity;
	private int FSatisfaction;
	private int FPerturbation;

	
	public boolean inComposition;

	
	private long lastNeighboorModification;
	
	
	public RecursiveState()
	{
		this.lastNeighboorModification=aDate.getCurrentTimeInMS()+2000;
	}
	
	public void signalNeighboorModification()
	{
		this.lastNeighboorModification=aDate.getCurrentTimeInMS();
		this.inComposition=false;
	}
	
	public boolean getStability()
	{
		if(this.lastNeighboorModification==-1) return false;
		return (aDate.getCurrentTimeInMS()-this.lastNeighboorModification)>RecursiveState.MIN_STABILITY_DELAY_IN_MS;
	}
	
	public String toHTML()
	{
		return "Reccursive state : [FComplexity:"+this.FComplexity+"  FSatisfaction:"+this.FSatisfaction+"  FPerturbation:"+this.FPerturbation+"  FStability:"+(Math.min(100.0,100.0*(aDate.getCurrentTimeInMS()-this.lastNeighboorModification)/RecursiveState.MIN_STABILITY_DELAY_IN_MS))+"%]";
	}
	
}
