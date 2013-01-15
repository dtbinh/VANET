package simulation.battery.custom;


/*
 * Created on 15 juin 2009
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
import simulation.battery.BatteryModel;
import simulation.events.EventNotificationInterface;
import simulation.multiagentSystem.ObjectSystemIdentifier;

/**
 * model the behavior of a battery
 * @author Jean-Paul Jamont
 */
public class InfiniteBatteryModel extends BatteryModel
{

	/**
	 * default constructor
	 * @param mas the interface for event notification
	 * @param owner the owner of the modeled battery 
	 * @param initialAmountOfEnergy the initial amount of energy (J)
	 */
	public InfiniteBatteryModel(EventNotificationInterface mas,ObjectSystemIdentifier owner,double intialAmountOfEnergy,double actualQuantityOfEnergy) 
	{
		super(mas,owner,intialAmountOfEnergy, actualQuantityOfEnergy);
	}

	
	 /** 
	  * there is no consumption (the amount of energy cannot be consumed)
	  * @param state not used
	  * @param elapsed_time_in_ms not used
	  */
	public void withdrawEnergyConsumption(int state, int elapsed_time_in_ms) 
	{
		// No consumption
	}

}
