package simulation.scenario;
import simulation.entities.Object;

/** an item of the entity (object/agent) dictionnary 
 * @author Jean-Paul Jamont
 */
public class EntitiesDictionnaryItem {
	
	/** id of the entity (in the simulator) */
	public int id;
	/** name of the entity (in the scenario) */
	public String name;
	/** reference to the object */
	public Object obj;

	/** default constructor
	 * @param id id of the entity (in the simulator)
	 * @param name name of the entity (in the scenario)
	 * @param obj reference to the object 
	 */
	public EntitiesDictionnaryItem(int id,String name, Object obj)
	{
		this.id=id;
		this.name=name;
		this.obj=obj;
	}
}
