package simulation.events.system;

import simulation.events.Event;
import simulation.multiagentSystem.ObjectSystemIdentifier;






/** event raised at each energy modification 
 * @author Jean-Paul Jamont
 */
public final class EnergyModificationEvent extends Event{

	/** the new energy level */
	private double actualEnergyAmount ;
	private double initialEnergyAmount;

	/** basic constructor 
	 * @param raiser identifier of the event raiser
	 * @param energyLevel the new energy level
	 */
	public EnergyModificationEvent(ObjectSystemIdentifier raiser,double initialEnergyAmount,double actualEnergyAmount)
	{
		super(raiser);
		this.actualEnergyAmount=actualEnergyAmount;
		this.initialEnergyAmount=initialEnergyAmount;
	}

	public float getPourcentOfAvailableEnergy()
	{
		return 100*getAvailableEnergyLevel();
	}
	public float getAvailableEnergyLevel()
	{
		return (float) (this.actualEnergyAmount/this.initialEnergyAmount);
	}
	public double getActualEnergyAmount()
	{
		return this.actualEnergyAmount;
	}
	
	public double getInitialEnergyAmount()
	{
		return this.initialEnergyAmount;
	}
	
	
	/** Returns a string representation of the object 
	 * @return a string representation of the event
	 */
	public String toString()
	{
		return "Object #"+getRaiser()+": Energy level is "+this.getPourcentOfAvailableEnergy()+"  -  Energy amount is "+this.actualEnergyAmount+"/"+this.initialEnergyAmount;
	}

}
