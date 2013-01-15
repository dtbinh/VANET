package simulation.battery.custom;

/*
 * Created on 15 juin 2009
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
import simulation.battery.*;
import simulation.events.EventMapNotificationInterface;
import simulation.events.EventNotificationInterface;
import simulation.multiagentSystem.ObjectSystemIdentifier;

/**
 * model the behavior of a battery
 * @author Jean-Paul Jamont
 */
public class LinearBatteryBasicModel extends BatteryModel
{
	/** state depending consumption */
	/** these values are inspired from SIMULATING NETWORKS OF WIRELESS SENSORS by Sung Park, Andreas Savvides and Mani B. Srivastava */
	/** consumption during OFF state */
	public final static double STATE_OFF_CONSUMPTION 		= 0.000006;
	/** consumption during IDLE state */
	public final static double STATE_IDLE_CONSUMPTION 		= 0.002900;
	/** consumption during SENDING state */
	public final static double STATE_SENDING_CONSUMPTION 	= 0.008000;
	/** consumption during RECEIVING state */
	public final static double STATE_RECEIVING_CONSUMPTION 	= 0.007000;
	/** consumption during SENSING state */
	public final static double STATE_SENSING_CONSUMPTION 	= 0.010000;
	/** consumption during SLEEP state */
	public final static double STATE_SLEEP_CONSUMPTION 		= 0.000100; /*adapted to underline communication consumption*/

	/**
	 * 
	 * @param mas
	 * @param owner
	 * @param intialAmountOfEnergy
	 * @param actualQuantityOfEnergy
	 */
	public LinearBatteryBasicModel(EventNotificationInterface mas,ObjectSystemIdentifier owner, double intialAmountOfEnergy,double actualQuantityOfEnergy) 
	{
		super(mas,owner,intialAmountOfEnergy, actualQuantityOfEnergy);
	}

	/**
	 * withdraw an amount of energy
	 * @param state state where was the object/agent during the elapsed time
	 * @param elapsed_time_in_ms elapsed time (in state state) which allow to compute the consumption of energy
	 */
	public void withdrawEnergyConsumption(int state, int elapsed_time_in_ms) 
	{
		switch(state)
		{
		case BatteryModel.STATE_OFF:
			this.consume((elapsed_time_in_ms*STATE_OFF_CONSUMPTION)/1000);
			break;
		case BatteryModel.STATE_IDLE:
			this.consume((elapsed_time_in_ms*STATE_IDLE_CONSUMPTION)/1000);
			break;
		case BatteryModel.STATE_SENDING:
			this.consume((elapsed_time_in_ms*STATE_SENDING_CONSUMPTION)/1000);
			break;
		case BatteryModel.STATE_RECEIVING:
			this.consume((elapsed_time_in_ms*STATE_RECEIVING_CONSUMPTION)/1000);
			break;
		case BatteryModel.STATE_SENSING:
			this.consume((elapsed_time_in_ms*STATE_SENSING_CONSUMPTION)/1000);
			break;
		case BatteryModel.STATE_SLEEP:
			this.consume((elapsed_time_in_ms*STATE_SLEEP_CONSUMPTION)/1000);
			break;
		}
	}

}
