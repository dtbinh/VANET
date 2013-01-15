package simulation.solutions.custom.Home;
import java.awt.Color;
import java.io.IOException;

import simulation.views.entity.imageInputBased.ImageFileBasedObjectView;
import simulation.entities.Agent;
import simulation.events.system.NotUnderstanbleRequestEvent;
import simulation.events.system.ReceivedMessageEvent;
import simulation.events.system.SendedMessageEvent;
import simulation.messages.Frame;
import simulation.messages.Message;
import simulation.messages.ObjectAbleToSendMessageInterface;
import simulation.multiagentSystem.MAS;
import simulation.solutions.custom.Home.Messages.HomeAgentFrame;
import simulation.solutions.custom.Home.Messages.HomeAgentMessage;
import simulation.utils.aDate;
/**
 * DSR agent
 * @author Jean-Paul Jamont
 */
public class FloorAgent extends Agent implements ObjectAbleToSendMessageInterface{

float Tai_01,Tai_02,Tai_03,Tai_04,Tai_05,Tai_06,Tai_07;
float Tf_01,Tf_02,Tf_03,Tf_04,Tf_05,Tf_06,Tf_07;
float Tc_01,Tc_02,Tc_03,Tc_04,Tc_05,Tc_06,Tc_07;
float Twe_01,Twe_02,Twe_03,Twe_04,Twe_05,Twe_06,Twe_07;
private String name;	
public String message="";
public String msgto="";
HomeAgentFrame frm = null;
HomeAgentMessage msg;
	
	
	public FloorAgent(MAS mas, Integer id, Float energy,  Integer range) {
		super(mas, id, range);
		switch(id)
		{
		case 3: this.name="PLANCHER CHAUFFANT PIECE 01"; break;
		case 4: this.name="PLANCHER CHAUFFANT PIECE 02"; break;
		case 5: this.name="PLANCHER CHAUFFANT PIECE 06"; break;
		case 6: this.name="PLANCHER CHAUFFANT PIECE 07"; break;
		case 7: this.name="PLANCHER CHAUFFANT PIECE 03"; break;
		case 8: this.name="PLANCHER CHAUFFANT PIECE 04"; break;
		case 9: this.name="PLANCHER CHAUFFANT PIECE 05"; break;
		}
		try {
					this.setNativeView(new ImageFileBasedObjectView("E:\\traSPArent DATA\\traSPArent Soft\\TestMASH\\TestMASH\\Plancher.png"));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
			}
	
	public void run()
	{
		this.setRange(200);
		System.out.println("Démarrage de l'entité "+this.getUserId()+" utilisant DSR");
		try{Thread.sleep(500);}catch(Exception e){}
		while(!isKilling() && !isStopping())
		{
			try{Thread.sleep(SLEEP_TIME_SLOT);}catch(Exception e){};
			while(  ((isSuspending()) && (!isKilling() && !isStopping()))) try{Thread.sleep(SLEEP_TIME_SLOT);}catch(Exception e){};
			this.sleep(5000);
			System.out.println("zzzzzzzzzzzzzzzzzzzzzzz "+Tai_02*2);
			
		}
		
	}
	
	public void sendMessage(int receiver, String message)
	{
		System.out.println("ENVOIE D'UN MESSAGE PAR "+this.getUserId());
		this.sendFrame(new HomeAgentFrame(this.getUserId(),receiver,new HomeAgentMessage(this.getUserId(),receiver,message)));
	}


	/** allows to an object to receive a message
	 * @param frame the received frame 
	 */
	public synchronized void receivedFrame(Frame frame)
	{
		super.receivedFrame(frame);
		this.receivedFrame((HomeAgentFrame) frame);
	}

	/** allows to an object to receive a message  
	 * @param frame the received DSR_Frame 
	 */
	
	public synchronized void receivedFrame(HomeAgentFrame frame)
	{
		if(frame.getReceiver()==this.getUserId() || frame.getReceiver()==Frame.BROADCAST)
		{
			System.out.println("Agent #"+this.getSystemId()+" recoit "+frame.toString());
			message = frame.getAgentHorlogeMessageInFrame().getStringMessage();
			msgto = message;
		}
	}
	
	public float strToFloat(String s)
	{
	return Float.valueOf(s);
	}
	
	public String floatToStr(float f)
	{
		return String.valueOf(f);
	}

	public String toSpyWindows()
	{
		String res= "<HTML>";
		res+="Agent #"+this.getUserId()+"<BR>   name="+this.name+"</I><BR>";
		
		return res+"</HTML>";
	}
	
}
