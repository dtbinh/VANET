package simulation.statistics.criteria;

import simulation.journal.SimulationJournal;
import simulation.multiagentSystem.ObjectSystemIdentifier;
import simulation.statistics.AbstractDataBlock;
import simulation.statistics.system.SystemBasicMeasurement;

/** class used  to implement its own criterion of measure (system defined criterion)
 * @see UserCriterion,CriterionInterface
 * @author Jean-Paul Jamont
 */
public abstract class SystemCriterion implements CriterionInterface{
	/** returns a AbstractDataBlock which contain the data used to draw representation
	 * @param journal a reference to the event journal
	 * @return the AbstractDataBlock
	 */
	public abstract AbstractDataBlock getDataBlock(SystemBasicMeasurement measurementSystem);
	/** returns a AbstractDataBlock which contain the data used to draw representation
	 * @param journal a reference to the event journal
	 * @param id identifier of the specific object/agent (compute only events in relation with this object)
	 * @return the AbstractDataBlock
	 */
	public abstract AbstractDataBlock getDataBlock(SystemBasicMeasurement measurementSystem, int id);
}
