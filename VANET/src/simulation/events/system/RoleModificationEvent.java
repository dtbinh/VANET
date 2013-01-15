package simulation.events.system;

import simulation.events.Event;
import simulation.multiagentSystem.ObjectSystemIdentifier;
import simulation.solutions.custom.MWAC.MWACAgent;


/** event notified when a role is modified 
 * @author Jean-Paul Jamont
 */
public final class RoleModificationEvent extends Event{

	/** the new role */
	private byte role;

	/** basic constructor 
	 * @param raiser identifier of the event raiser
	 * @param role the new role
	 */
	public RoleModificationEvent(ObjectSystemIdentifier raiser,byte role)
	{
		super(raiser);
		this.role=role;
	}

	public byte getRole()
	{
		return this.role;
	}

	/** Returns a string representation of the object 
	 * @return a string representation of the event
	 */
public String toString()
	{
		return "Object #"+getRaiser()+": Role become "+MWACAgent.roleToString(this.role);
	}


}
