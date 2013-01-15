package simulation.solutions.custom.PreyPredator;

import java.awt.Image;
import java.awt.image.ImageFilter;
import java.util.Vector;

import simulation.entities.Agent;
import simulation.messages.Frame;
import simulation.messages.ObjectAbleToSendMessageInterface;
import simulation.multiagentSystem.MAS;
import simulation.solutions.custom.PreyPredator.Messages.PreyPredatorFrame;
import simulation.solutions.custom.PreyPredator.Messages.PreyPredatorMessage;
import simulation.utils.ImageToolkit;
import simulation.utils.IntegerPosition;
import simulation.views.entity.imageInputBased.ImageFileBasedObjectView;
			 
public class PreyAgent extends Agent implements ObjectAbleToSendMessageInterface{

	
	private int speed=2;
	private Vector<Predator> depredadores;
	
	
	////Animation variables
	private boolean isMoving=false;
	private int animationInterval=0; 
	private final static String SPRITE_FILENAME = "D:\\TestMASH\\Sprites\\prey.png";
	private final static String SPRITE2_FILENAME = "D:\\TestMASH\\Sprites\\prey.png";
	private final static String SPRITE3_FILENAME = "D:\\TestMASH\\Sprites\\prey.png";
	private ImageFileBasedObjectView view1;
	private ImageFileBasedObjectView view2;
	private ImageFileBasedObjectView view3;
	private ImageFileBasedObjectView view;
	
	public PreyAgent(MAS mas, int id, Integer range) {
		this(mas, id, (float)1,range);
	}
	
	public PreyAgent(MAS mas, Integer id, Float energy,Integer range) {
		super(mas, id, range);
		depredadores = new Vector<Predator>();
		try{
			view =view1 = new ImageFileBasedObjectView(SPRITE_FILENAME);
			view2 = new ImageFileBasedObjectView(SPRITE2_FILENAME);
			view3 = new ImageFileBasedObjectView(SPRITE3_FILENAME);
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
		int counter=0;
		while(!isKilling() && !isStopping())
		{
			// Pause
			try{
				Thread.sleep(SLEEP_TIME_SLOT);
				if(0<depredadores.size()){
					
					///Get the 2 nerdest predators
					int[] neardest = new int[]{0,0};
					for(int i=0;i<depredadores.size();i++){
						if(depredadores.get(i).distancia < depredadores.get(neardest[0]).distancia){
							neardest[1]=neardest[0];
							neardest[0]=i;
						}
					}
					
					//If the neardest predator is to closes
					if(depredadores.get(neardest[0]).distancia < 50){
						isMoving=true;
						//Were is bether to scape?
						float grados = 
								(((
									depredadores.get(neardest[0]).grados+((depredadores.get(neardest[0]).grados<0)?360:0) 
									+ 
									depredadores.get(neardest[1]).grados+((depredadores.get(neardest[1]).grados<0)?360:0)
								)
								/2)+180)%360;
						int x = (int)(speed* Math.cos(degreesToRadian(grados)));
						int y = (int)(speed* Math.sin(degreesToRadian(grados)));
						//Move
						setPosition(this.getPosition().x + x, this.getPosition().y + y);
						//Updape
						for(int i=0;i<depredadores.size();i++){
							depredadores.get(i).update();
							//Dont worry about to far predadors
							if(50 < depredadores.get(i).distancia)
								depredadores.remove(i--);
						}
					}else
						isMoving=false;
				}else
					isMoving=false;
				animation();
			}catch(Exception e){};

			while(((isSuspending()) && (!isKilling() && !isStopping()))){ 
				// Preparation of the others threads
				try{
					Thread.sleep(SLEEP_TIME_SLOT);
				}catch(Exception e){
					System.out.println(e.toString());
				}
			}
			this.sendMessage(Frame.BROADCAST,"Beeee");
			
		}
	}
	
	private void animation(){
		if(isMoving){
			if(animationInterval++==7){
				if(view!=view2)
					view=view2;
				else
					view=view1;
				animationInterval=0;
				setPosition(getPosition());
			}
		}else{
			if(view!=view1){
				view=view1;
				setPosition(getPosition());
			}
		}
	}

	@Override
	public void sendMessage(int receiver, String message) {
		this.sendFrame(new PreyPredatorFrame(getUserId(), receiver, new PreyPredatorMessage(getUserId(),receiver,PreyPredatorMessage.BLEAT,getPosition())));
	}
	
	public synchronized void receivedFrame(Frame frame){
		if((frame.getReceiver()==Frame.BROADCAST) || (frame.getReceiver()==this.getUserId())){
			PreyPredatorMessage msg = (PreyPredatorMessage) frame.getMessage();
			if(msg.type==PreyPredatorMessage.HOWl){
				int senderID=frame.getSender();
				int i=0;
				for(;i<depredadores.size();i++){
					if(depredadores.get(i).id==senderID){
						depredadores.get(i).setPosicion(msg.getPosition());						
						break;
					}
				}
				if(i==depredadores.size()){
					depredadores.add(new Predator(senderID,msg.getPosition()));
				}
			}else if(msg.type==PreyPredatorMessage.BITE){
				System.out.println("Je suis mort " + getUserId());
				kill();
			}
		}
	}
	
	
	private float degreesToRadian(float degree){
		return (float)(degree*(Math.PI/180));
	}	

	
	@Override
	public ImageFileBasedObjectView getView() {
		return view;
	}
	
	
	private float radianToDegree(float radian){
		return (float)(radian*(180/Math.PI));
	}
	
	private class Predator{
		
		int id;
		float grados;
		int distancia;
		IntegerPosition pos;
		
		public Predator(int id, IntegerPosition posicionRef){
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
			System.out.println("Depredador");
			int x= pos.x-getPosition().x;
			int y= pos.y-getPosition().y;
			System.out.println("Distancia= " + distancia);
			System.out.println("Grados= " + grados);
			System.out.println("X= " + x);
			System.out.println("Y= " + y);
			
		}*/
		
	}
	
	
	
	
}
