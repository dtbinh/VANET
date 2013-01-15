

package simulation.events;


/**
 * Interface to which allow to catch an event
 * @author Jean-Paul Jamont
 */
public interface EventNotificationInterface {
	
	/**
	 * to notify an event 
	 * @param evt the notified event
	 */
	public void notifyEvent(Event evt);
}
