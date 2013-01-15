package simulation.events.user;
import simulation.events.Event;
import simulation.multiagentSystem.ObjectSystemIdentifier;

/** event notified for Recursive Agent
 * @author Ha Hoang
 */
public class  RecursiveAgentEvent extends Event{
	public final static byte ReceivedMessage = 1;	
	public final static byte SendedMessage = 2;	
	
	// Id and level of receiver recursive agent 
	private byte typeEvent;
	private int id;
	private int level;
	
	/** basic constructor 
	 * @param raiser identifier of the event raiser
	 * @param msg the received message
	 */
	public RecursiveAgentEvent(ObjectSystemIdentifier raiser,int id, int level,byte typeEvent)
	{
		super(raiser);
		this.setTypeEvent(typeEvent);;
		this.setId(id);
		this.setLevel(level);
	}
	
	public static RecursiveAgentEvent createRecursiveAgentEvent(ObjectSystemIdentifier raiser,RecursiveAgentEvent oldEvent)
	{

		byte typeEv=oldEvent.getTypeEvent();
		switch(typeEv)
		{	
		case RecursiveAgentEvent.ReceivedMessage: return new ReceivedRecursiveMessageEvent(raiser, oldEvent.getId(),oldEvent.getLevel(),((ReceivedRecursiveMessageEvent)oldEvent).getMessage()); 
		case RecursiveAgentEvent.SendedMessage: return new SendedRecursiveMessageEvent(raiser, oldEvent.getId(),oldEvent.getLevel(),((SendedRecursiveMessageEvent)oldEvent).getMessage());
		}
		System.out.println("Unknown recursive message");
		return null;
	}
	

	
	/** Returns a string representation of the object 
	 * @return a string representation of the event
	 */
public String toString()
	{
		return "RecursiveAgent ("+this.getId()+","+this.getLevel()+")" ;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getLevel() {
		return level;
	}





	public void setTypeEvent(byte typeEvent) {
		this.typeEvent = typeEvent;
	}





	public byte getTypeEvent() {
		return typeEvent;
	}


}
