package simulation.events;

import java.util.ArrayList;
import java.util.HashMap;

import simulation.multiagentSystem.ObjectSystemIdentifier;


/**
 * interface which enable to notify a list of event
 * Supply services to notify an event
 * @author Jean-Paul Jamont
 */
public interface EventMapNotificationInterface {
	
	/**
	 * notify a list of events
	 * @param events the list of events
	 */
	public void notifyEvent(HashMap<ObjectSystemIdentifier,ArrayList<Event>> events);
}
