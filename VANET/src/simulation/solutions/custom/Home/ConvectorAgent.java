package simulation.solutions.custom.Home;
import java.awt.Color;
import java.io.IOException;

import simulation.views.entity.imageInputBased.ImageFileBasedObjectView;
import simulation.entities.Agent;
import simulation.environment.AttributeNotFoundException;
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
public class ConvectorAgent extends Agent implements ObjectAbleToSendMessageInterface{

float Tai_01,Tai_02,Tai_03,Tai_04,Tai_05,Tai_06,Tai_07;
float Tf_01,Tf_02,Tf_03,Tf_04,Tf_05,Tf_06,Tf_07;
float Tc_01,Tc_02,Tc_03,Tc_04,Tc_05,Tc_06,Tc_07;
float Twe_01,Twe_02,Twe_03,Twe_04,Twe_05,Twe_06,Twe_07;
private String name;
public String message="";
public String msgto="";
HomeAgentFrame frm = null;
HomeAgentMessage msg;
	
	
	public ConvectorAgent(MAS mas, Integer id, Float energy,  Integer range) {
		super(mas, id, range);
		switch(id)
		{
		case 1: this.name="CONVECTEUR 1"; break;
		case 2: this.name="CONVECTEUR 2"; break;
		}		
		try {
				this.setNativeView(new ImageFileBasedObjectView("E:\\traSPArent DATA\\traSPArent Soft\\TestMASH\\TestMASH\\Convecteur.png"));
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
			
			if (this.getUserId()==1)
			{
				Object obj=null;
				try {
					obj = this.getEnvironmentAttribute("Tai_01");
					Tai_01=(Float) obj;
					System.out.println("Tai_01 mesuré par #1 : "+Tai_01);
					//this.setEnvironmentAttribute("Tai_01",1234);
					//System.out.println("The new value of Tai_01 is : "+Tai_01);
					this.sendMessage(2, this.floatToStr(Tai_01));
				}
				catch (AttributeNotFoundException e) 
				{
					// TODO Auto-generated catch block
					System.out.println(e);
				}
			}


			else if (this.getUserId()==2 && !msgto.isEmpty())
			{

				Tai_01 = this.strToFloat(msgto);
				System.out.println("Agent #2 a recu "+Tai_01+" et la transforme en "+(Tai_01*2));
				try {
					this.setEnvironmentAttribute("Tai_01", Tai_01*2);
				} 
				catch (AttributeNotFoundException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				msgto="";
			}
			/*
			if (this.getUserId()==1){
				Tai_06 = (Float) this.getEnvironmentAttribute("Tai_06");
				Tf_06 = (Float) this.getEnvironmentAttribute("Tf_06");
				Tc_06 = (Float) this.getEnvironmentAttribute("Tc_06");
				Twe_06 = (Float) this.getEnvironmentAttribute("Twe_06");
				System.out.println("Tai_06 : "+Tai_06);
				
				//System.out.println("The new value of Tai_06 is : "+Tai_06);
				this.sendMessage(2, this.floatToStr(Tai_06));
			}
			if (this.getUserId()==2 && !msgto.isEmpty()){
				Tai_07 = (Float) this.getEnvironmentAttribute("Tai_07");
				Tf_07 = (Float) this.getEnvironmentAttribute("Tf_07");
				Tc_07 = (Float) this.getEnvironmentAttribute("Tc_07");
				Twe_07 = (Float) this.getEnvironmentAttribute("Twe_07");
				Tai_07 = this.strToFloat(msgto);
				System.out.println("zzzzzzzzzzzzzzzzzzzzzzz "+Tai_07*2);
				this.setEnvironmentAttribute("Tai_01",4444);
				msgto = "";
			}
			*/
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
		res+="Agent #"+this.getUserId()+"<BR> name="+this.name+"<BR>";
		if (this.getUserId()==1){
			res+="used variables : Tai_06 = "+Tai_06+", Twe_06 = "+Twe_06+", Tf_06 = "+Tf_06+", Tc_06 = "+Tc_06+"<BR>";
		}
		if (this.getUserId()==2){
			res+="used variables : Tai_07 = "+Tai_07+", Twe_07 = "+Twe_07+", Tf_07 = "+Tf_07+", Tc_07 = "+Tc_07+"<BR>";
			res+=""+message+"<BR>";
		}
		return res+"</HTML>";
	}
	
}
