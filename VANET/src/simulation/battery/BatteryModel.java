package simulation.battery;

/*
 * Created on 15 juin 2009
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
import simulation.events.EventMapNotificationInterface;
import simulation.events.EventNotificationInterface;
import simulation.events.system.EnergyModificationEvent;
import simulation.multiagentSystem.ObjectSystemIdentifier;

/**
 * model the behavior of a battery
 * @author Jean-Paul Jamont
 */
public abstract class BatteryModel implements BatteryLevelReader {

	/** allows to discretize the event energy modification */
	protected final static float ENERGY_PERCENT_MODIFICATION_NOTIFICATION = 0.5f;
	/** memorize pourcent of the last notification */
	protected float availablePourcentOfEnergyAtLastNotification;


	/** To signal battery related event */
	private  EventNotificationInterface mas;
	/** Identifier of the owner */
	private ObjectSystemIdentifier owner;
	/** initial amount of stored energy */
	private double initialAmountOfEnergy;
	/** actual amount of stored energy */
	private double actualAmountOfEnergy;


	/** states to take into account */
	public final static int STATE_OFF = 0;
	public final static int STATE_IDLE = 1;
	public final static int STATE_SENDING = 2;
	public final static int STATE_RECEIVING = 3;
	public final static int STATE_SENSING = 4;
	public final static int STATE_SLEEP = 5;

	/**
	 * default constructor
	 * @param mas the interface for event notification
	 * @param owner the owner of the modeled battery 
	 * @param initialAmountOfEnergy the initial amount of energy (J)
	 */
	public BatteryModel(EventNotificationInterface mas, ObjectSystemIdentifier owner, double initialAmountOfEnergy)
	{
		this(mas,owner,initialAmountOfEnergy,initialAmountOfEnergy);
		this.availablePourcentOfEnergyAtLastNotification=0;
	}

	/**
	 * constructor used when the battery is not full at is creation
	 * @param mas the interface for event notification
	 * @param owner the owner of the modeled battery 
	 * @param initialAmountOfEnergy the initial amount of energy (J)
	 * @param actualAmountOfEnergy the actual amount of energy (J)
	 */
	public BatteryModel(EventNotificationInterface mas,ObjectSystemIdentifier owner,double initialAmountOfEnergy, double actualAmountOfEnergy)
	{
		this.mas=mas;
		this.owner=owner;
		this.actualAmountOfEnergy=actualAmountOfEnergy;
		this.initialAmountOfEnergy=initialAmountOfEnergy;
		mas.notifyEvent(new EnergyModificationEvent(owner,this.initialAmountOfEnergy,this.actualAmountOfEnergy));
	}

	/**
	 * returns the owner identifier
	 * @return identifier of the owner
	 */
	public ObjectSystemIdentifier getOwner()
	{
		return this.owner;
	}

	/**
	 * allows to know the initial amount of energy
	 * @return the initial amount of energy
	 */
	public double getInitialAmountOfEngergy()
	{
		return this.initialAmountOfEnergy;
	}

	/**
	 * allows to know the actual amount of energy
	 * @return the actual amount of energy
	 */
	public double getActualAmountOfEngergy()
	{
		return this.actualAmountOfEnergy;
	}

	/**
	 * set the initial amount of energy
	 * @param energy the initial amount of energy
	 */
	protected void setInitialAmountOfEnergy(double energy)
	{
		this.initialAmountOfEnergy=energy;
	}

	/**
	 * set the actual amount of energy
	 * @param energy the actual amount of energy
	 */
	protected void setActualAmountOfEnergy(double energy)
	{
		if(energy>=0)
			this.actualAmountOfEnergy=energy;
		else
			this.actualAmountOfEnergy=0;

		if(Math.abs(this.availablePourcentOfEnergyAtLastNotification-this.pourcentOfAvailableEnergy())>BatteryModel.ENERGY_PERCENT_MODIFICATION_NOTIFICATION) 
		{
			this.availablePourcentOfEnergyAtLastNotification=this.pourcentOfAvailableEnergy();
			mas.notifyEvent(new EnergyModificationEvent(this.owner,this.initialAmountOfEnergy,this.actualAmountOfEnergy));
		}
	}

	/**
	 * consume (withdraw) an amount of energy
	 * @param energy the actual amount of energy
	 */
	protected void consume(double energy)
	{
		this.setActualAmountOfEnergy(this.getActualAmountOfEngergy()-energy);
	}


	/**
	 * 
	 * @return the available energy in pourcent of the initial one
	 */
	public float pourcentOfAvailableEnergy()
	{
		return 100*this.availableEnergyRate();
	}

	/**
	 * 
	 * @return the available energy in pourcent of the initial one
	 */
	public float availableEnergyRate()
	{
		return (float) (this.actualAmountOfEnergy/this.initialAmountOfEnergy);
	}

	/**
	 * withdraw an amount of energy
	 * @param state state where was the object/agent during the elapsed time
	 * @param elapsed_time_in_ms elapsed time (in state state) which allow to compute the consumption of energy
	 */
	public abstract void withdrawEnergyConsumption(int state,int elapsed_time_in_ms);


	/**
	 * is the battery empty ?
	 * @return true if the battery is empty
	 */
	public boolean isEmpty()
	{
		return (this.actualAmountOfEnergy==0);
	}
}
