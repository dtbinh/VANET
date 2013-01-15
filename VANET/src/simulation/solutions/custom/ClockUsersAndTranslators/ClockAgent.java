package simulation.solutions.custom.ClockUsersAndTranslators;
import java.awt.Color;
import java.io.IOException;
import java.util.LinkedList;
import java.util.ListIterator;

import simulation.views.entity.imageInputBased.ImageFileBasedObjectView;
import simulation.entities.Agent;
import simulation.events.system.NotUnderstanbleRequestEvent;
import simulation.events.system.ReceivedMessageEvent;
import simulation.events.system.SendedMessageEvent;
import simulation.messages.Frame;
import simulation.messages.Message;
import simulation.messages.ObjectAbleToSendMessageInterface;
import simulation.multiagentSystem.MAS;
import simulation.solutions.custom.ClockUsersAndTranslators.messages.ClockUsersAndTranslatorsFrame;
import simulation.solutions.custom.ClockUsersAndTranslators.messages.ClockUsersAndTranslatorsMessage;
import simulation.utils.aDate;

/**
 * DSR agent
 * @author Jean-Paul Jamont
 */
public class ClockAgent extends Agent implements ObjectAbleToSendMessageInterface{


	private final static String PREY_SPRITE_FILENAME = "D:\\TestMASH\\Sprites\\clock.png";

	private ImageFileBasedObjectView view;

	private int typeOfAgent;

	/**
	 * parameterized constructor
	 * @param mas reference to the multiagent system
	 * @param id identifier of the agent
	 * @param energy energy level of the agent
	 * @param range transmission range of the agent
	 */
	public ClockAgent(MAS mas, Integer id, Float energy,  Integer range) {
		super(mas, id, range);
		try {
			this.view=new ImageFileBasedObjectView(ClockAgent.PREY_SPRITE_FILENAME);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Launched by the call of start method
	 */
	public void run()
	{
		ClockUsersAndTranslatorsFrame frm;
		ClockUsersAndTranslatorsMessage msg;

		System.out.println("Start clock agent #"+this.getUserId());

		// wait a little amount of time to allow the construction of others agents 
		try{Thread.sleep(500);}catch(Exception e){}
		this.setColor(Color.green);


		// the decision loop is processed while the simulator don't requiere to kill or stop this agent)
		while(!isKilling() && !isStopping())
		{
			// Pause
			try{Thread.sleep(SLEEP_TIME_SLOT);}catch(Exception e){};

			// Preparation of the others threads
			while(  ((isSuspending()) && (!isKilling() && !isStopping()))) try{Thread.sleep(SLEEP_TIME_SLOT);}catch(Exception e){};

		}

	}

	/**
	 * send a message
	 * @param receiver identifier of the receiver
	 * @param message message to send (a string)
	 */
	public void sendMessage(int receiver, String message)
	{
		System.out.println("ENVOIE D'UN MESSAGE PAR "+this.getUserId());
		this.sendFrame(new ClockUsersAndTranslatorsFrame(this.getUserId(),receiver,new ClockUsersAndTranslatorsMessage(this.getUserId(),receiver,message)));
	}


	/** allows to an object to receive a message
	 * @param frame the received frame 
	 */
	public synchronized void receivedFrame(Frame frame)
	{
		super.receivedFrame(frame);
		this.receivedFrame((ClockUsersAndTranslatorsFrame) frame);
	}

	/** allows to an object to receive a message
	 * @param frame the received DSR_Frame 
	 */
	public synchronized void receivedFrame(ClockUsersAndTranslatorsFrame frame)
	{
		if(frame.getReceiver()==this.getUserId() || frame.getReceiver()==Frame.BROADCAST)
		{
			if(frame.getClockUsersAndTranslatorsMessageInFrame().getStringMessage().equals("What time is it?"))
				this.sendFrame( new ClockUsersAndTranslatorsFrame(this.getUserId(),frame.getSender(),new ClockUsersAndTranslatorsMessage(this.getUserId(),frame.getSender(),"It is "+(new aDate()).getTime()+".")));
			else
				this.sendFrame( new ClockUsersAndTranslatorsFrame(this.getUserId(),frame.getSender(),new ClockUsersAndTranslatorsMessage(this.getUserId(),frame.getSender(),"Hey man, I speack english...")));
		}
	}

	/**
	 * 
	 */
	public String toSpyWindows()
	{

		String res= "<HTML>";

		res+="Agent #"+this.getUserId()+"<BR>";
		res+="role = <I>HORLOGE</I><BR>";
		return res+"</HTML>";
	}

}


