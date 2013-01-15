package simulation.solutions.custom.PreyPredatorShepherd;
import java.awt.Color;
import java.io.File;
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
import simulation.multiagentSystem.SimulatedObject;
import simulation.utils.DoublePosition;
import simulation.utils.GeomVector;
import simulation.utils.IntegerPosition;
import simulation.utils.aDate;

/**
 * DSR agent
 * @author Jean-Paul Jamont
 */
public class ShepherdAgent extends Agent implements ObjectAbleToSendMessageInterface{


	public final static double DEFAULT_VISION_RADIUS = 100;

	private final static String PREY_SPRITE_FILENAME = "D:\\TestMASH\\Sprites\\shepherd.gif";



	private MotionManagement motionManagement;

	/**
	 * parameterized constructor
	 * @param mas reference to the multiagent system
	 * @param id identifier of the agent
	 * @param energy energy level of the agent
	 * @param range transmission range of the agent
	 */
	public ShepherdAgent(MAS mas, Integer id, Float energy,  Integer range) {
		super(mas, id, range);
		try {
			this.setNativeView(new ImageFileBasedObjectView(ShepherdAgent.PREY_SPRITE_FILENAME));
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

		System.out.println("Démarrage de l'entité PreyAgent "+this.getUserId());
		this.motionManagement=new MotionManagement(this.getPosition());
		this.motionManagement.setSpeed(1+(Math.random()*5));

		// wait a little amount of time to allow the construction of others agents 
		try{Thread.sleep(5000);}catch(Exception e){}

		this.setColor(Color.GREEN);



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
	 * Launched by the call of start method
	 */
	public LinkedList<SimulatedObject> getPerceivedAgent(double visionRadius)
	{
		LinkedList<SimulatedObject> res = new LinkedList<SimulatedObject>();
		SimulatedObject[] tab=this.getMASObjectArray();
		for(int i=0;i<tab.length;i++)
			if(tab[i].getPosition().inCircleArea(this.getPosition(), visionRadius)) res.add(tab[i]);
		return res;
	}


	/**
	 * send a message
	 * @param receiver identifier of the receiver
	 * @param message message to send (a string)
	 */
	public void sendMessage(int receiver, String message)
	{
		System.out.println("ENVOIE D'UN MESSAGE PAR "+this.getUserId());
	}


	/** allows to an object to receive a message
	 * @param frame the received frame 
	 */
	public synchronized void receivedFrame(Frame frame)
	{
		super.receivedFrame(frame);
	}


	/**
	 * 
	 */
	public String toSpyWindows()
	{

		String res= "<HTML>";

		res+="Agent #"+this.getUserId()+"<BR>";
		res+="role = <I>PREY</I><BR>";



		return res+"</HTML>";
	}

	private class MotionManagement
	{
		private aDate lastMovmentIteration =null;
		private DoublePosition position;
		private double speed;	// speed in unit by second
		private GeomVector direction;


		public MotionManagement(IntegerPosition p)
		{
			this.position=new DoublePosition(p);
			this.speed=1.0f;
			this.lastMovmentIteration=new aDate();
			this.direction=new GeomVector();
		}

		/** take direction to the specified point */
		public void setDirection(IntegerPosition p)
		{
			this.setDirection(new GeomVector(p.x-this.position.x,p.y-this.position.y));
		}

		/**
		 * Set the direction according the vector parameters
		 * @param x 
		 * @param y
		 */
		public void setDirection(GeomVector v)
		{
			this.direction=v;
			v.normalize();
		}

		public void setSpeed(double speed)
		{
			this.speed=speed;
		}
		public void move()
		{
			long timeOfMovement=new aDate().differenceToMS(lastMovmentIteration);
			this.lastMovmentIteration=new aDate();

			if(this.direction.x!=0 && this.direction.y!=0)
			{
				//String cr="[#"+getUserId()+"] Position = "+this.position.toString()+"   Direction = "+this.direction.toString()+"  Time span = "+timeOfMovement+"    =>   ";
				this.position.x=this.position.x+this.direction.x*speed*timeOfMovement/1000.0;
				this.position.y=this.position.y+this.direction.y*speed*timeOfMovement/1000.0;
				//cr+=this.position.toString();
				//System.out.println(cr);
				setPosition(new IntegerPosition((int)this.position.x,(int)this.position.y));
			}
		}
	}

}


