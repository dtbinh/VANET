package simulation.battery;

public interface BatteryLevelReader {
	/**
	 * allows to know the  percentage of available energy
	 * @return the available energy in percent of the initial one
	 */
	public float pourcentOfAvailableEnergy();

	/**
	 * allows to know the actual amount of energy
	 * @return the actual amount of energy
	 */
	public double getActualAmountOfEngergy();

	/**
	 * allows to know the initial amount of energy
	 * @return the initial amount of energy
	 */
	public double getInitialAmountOfEngergy();

}
