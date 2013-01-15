package simulation.scenario;

import simulation.entities.Object;
/** allows to reference an object able to add a new entity (agent/object) in the simulator 
 * @author Jean-Paul Jamont
 */
public interface InterfaceEntityAddition {
	/**
	 * add an entity in the simulation
	 * @param id entity identifier
	 * @param x absisse of the entity position
	 * @param y ordinate of the entity position
	 * @param ag the new object/agent
	 */
	public void scenarioEntityAddition(int id,int x,int y,Object ag);
}
