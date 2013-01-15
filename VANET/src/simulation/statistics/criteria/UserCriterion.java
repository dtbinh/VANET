package simulation.statistics.criteria;

import simulation.journal.SimulationJournal;
import simulation.multiagentSystem.ObjectSystemIdentifier;
import simulation.statistics.AbstractDataBlock;

/** class used by an user to implement its own criterion of measure
 * @see SystemCriterion,CriterionInterface 
 * @author Jean-Paul Jamont
 */
public abstract class UserCriterion implements CriterionInterface{

	/** returns a AbstractDataBlock which contain the data used to draw representation
	 * @param journal a reference to the event journal
	 * @return the AbstractDataBlock
	 */
	public abstract AbstractDataBlock getDataBlock(SimulationJournal journal);

	/** returns a AbstractDataBlock which contain the data used to draw representation
	 * @param journal a reference to the event journal
	 * @param id identifier of the specific object/agent (compute only events in relation with this object)
	 * @return the AbstractDataBlock
	 */
	public abstract AbstractDataBlock getDataBlock(SimulationJournal journal,int id);
}
