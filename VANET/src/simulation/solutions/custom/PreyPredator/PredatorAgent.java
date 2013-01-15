package simulation.solutions.custom.PreyPredator;

import java.util.Vector;

import simulation.entities.Agent;
import simulation.messages.Frame;
import simulation.messages.ObjectAbleToSendMessageInterface;
import simulation.multiagentSystem.MAS;
import simulation.solutions.custom.PreyPredator.Messages.PreyPredatorFrame;
import simulation.solutions.custom.PreyPredator.Messages.PreyPredatorMessage;
import simulation.utils.IntegerPosition;
import simulation.views.entity.imageInputBased.ImageFileBasedObjectView;
			 
public class PredatorAgent extends Agent implements ObjectAbleToSendMessageInterface{

	//Image location
	private final static String SPRITE_FILENAME = "D:\\TestMASH\\Sprites\\predator.png";
	private ImageFileBasedObjectView view;
	
	//Messages
	private final String HOWL="HOWL";
	private final String BITE="BITE";
	
	private int speed=2;
	private int live=5000;
	private Vector<Prey> presadddds;
	
	
	
	
	public PredatorAgent(MAS mas, int id, Integer range) {
		this(mas, id, (float)1,range);	
	}
	
	public PredatorAgent(MAS mas, Integer id, Float energy,Integer range) {
		super(mas, id, range);
		presadddds = new Vector<PredatorAgent.Prey>();
		try{
			view = new ImageFileBasedObjectView(SPRITE_FILENAME);
		}catch(Exception e){System.out.println("fail image");}
	}
	
	/**
	 * Launched by the call of start method
	 */
	public void run()
	{
		// wait a little amount of time to allow the construction of others agents 
		try{Thread.sleep(500);}catch(Exception e){}

		// the decision loop is processed while the simulator don't requiere to kill or stop this agent)
		while(!isKilling() && !isStopping())
		{
			// Pause
			try{
				Thread.sleep(SLEEP_TIME_SLOT);
				//there any prey?
				if(0<presadddds.size()){
					int menor =0;
					//Look for the neardest
					for(int i=1;i<presadddds.size();i++){
						if(presadddds.get(i).distancia <presadddds.get(menor).distancia){
							menor=i;
						}
					}
					//Can i bit it?
					if(presadddds.get(menor).distancia < speed){
						sendMessage(presadddds.get(menor).id,BITE);
						System.out.println("J'ai mangé =) " +  getUserId());
						kill();
					//Get close
					}else{
						//Direction
						float grados = presadddds.get(menor).grados;
						int x = (int)(speed* Math.cos(degreesToRadian(grados)));
						int y = (int)(speed* Math.sin(degreesToRadian(grados)));
						//move
						setPosition(this.getPosition().x + x, this.getPosition().y + y);
						
						//m i dead?
						if(live--==0){
							System.out.println("je suis mort" + getUserId());
							kill();
						}
						//now where are they?
						for(int i=0;i<presadddds.size();i++){
							presadddds.get(i).update();
						}
					}
				}
				//Howl!!!
				this.sendMessage(Frame.BROADCAST,HOWL);
			}catch(Exception e){
				
			};
			// Preparation of the others threads
			while(((isSuspending()) && (!isKilling() && !isStopping()))){ 
				try{
					Thread.sleep(SLEEP_TIME_SLOT);
				}catch(Exception e){};
			}
		}
		
	}
	
	@Override
	public void sendMessage(int receiver, String message) {
		if(message.equals(HOWL))
			this.sendFrame(new PreyPredatorFrame(getUserId(), receiver, new PreyPredatorMessage(getUserId(),receiver,PreyPredatorMessage.HOWl,getPosition())));
		else if(message.equals(BITE))
			this.sendFrame(new PreyPredatorFrame(getUserId(), receiver, new PreyPredatorMessage(getUserId(),receiver,PreyPredatorMessage.BITE,getPosition())));
	}
	
	public synchronized void receivedFrame(Frame frame){
		if((frame.getReceiver()==Frame.BROADCAST) || (frame.getReceiver()==this.getUserId()) ){
			PreyPredatorMessage msg = (PreyPredatorMessage) frame.getMessage();
			if(msg.type==PreyPredatorMessage.BLEAT){
				int senderID=frame.getSender();
				int i=0;
				for(;i<presadddds.size();i++){
					if(presadddds.get(i).id==senderID){
						presadddds.get(i).setPosicion(msg.getPosition());
						break;
					}
				}
				if(i==presadddds.size()){
					presadddds.add(new Prey(senderID,msg.getPosition()));
				}
			}
		}
	}
	
	private float degreesToRadian(float degree){
		return (float)(degree*(Math.PI/180));
	}
	
	private float radianToDegree(float radian){
		return (float)(radian*(180/Math.PI));
	}
	
	@Override
	public ImageFileBasedObjectView getView() {
		return view;
	}
	
	
	
	
	private class Prey{
		
		int id;
		float grados;
		int distancia;
		IntegerPosition pos;
		
		public Prey(int id, IntegerPosition posicionRef){
			pos= posicionRef.clone();
			int x= pos.x-getPosition().x;
			int y= pos.y-getPosition().y;
			
			this.id=id;
			distancia = (int)Math.sqrt((x*x)+(y*y));
			
			if(x!=0){
				grados = radianToDegree((float)Math.atan(y/x));
				if(x<0)
					grados+=180;
			}else{
				if(0<y){
					grados = 90;
				}else{
					grados = -90;
				}
			}
		}
		
		public void setPosicion(IntegerPosition posicionRef){
			pos= posicionRef.clone();
			int x= pos.x-getPosition().x;
			int y= pos.y-getPosition().y;
			
			distancia = (int)Math.sqrt((x*x)+(y*y));
			if(x!=0){
				grados = radianToDegree((float)Math.atan(y/x));
				if(x<0)
					grados+=180;
			}else{
				if(0<y)
					grados = 90;
				else
					grados = -90;
			}
		}
		
		public void update(){
			int x= pos.x-getPosition().x;
			int y= pos.y-getPosition().y;
			
			distancia = (int)Math.sqrt((x*x)+(y*y));
			if(x!=0){
				grados = radianToDegree((float)Math.atan(y/x));
				if(x<0)
					grados+=180;
			}else{
				if(0<y)
					grados = 90;
				else
					grados = -90;
			}
		}
		
		
		/*public void print(){
			System.out.println("Presa");
			int x= pos.x-getPosition().x;
			int y= pos.y-getPosition().y;
			System.out.println("Distancia= " + distancia);
			System.out.println("Grados= " + grados);
			System.out.println("X= " + x);
			System.out.println("Y= " + y);
		}*/
	}
}
