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
import simulation.utils.aDate;
/**
 * DSR agent
 * @author Jean-Paul Jamont
 */
public class RoomAgent extends Agent  implements ObjectAbleToSendMessageInterface{
	
	float Tai_01,Tai_02,Tai_03,Tai_04,Tai_05,Tai_06,Tai_07;
	float Tf_01,Tf_02,Tf_03,Tf_04,Tf_05,Tf_06,Tf_07;
	float Tc_01,Tc_02,Tc_03,Tc_04,Tc_05,Tc_06,Tc_07;
	float Twe_01,Twe_02,Twe_03,Twe_04,Twe_05,Twe_06,Twe_07;
	private String name;

	
	public RoomAgent(MAS mas, Integer id, Float energy,  Integer range) {
		
	super(mas, id, range);
	switch(id)
	{
	case 14: this.name="PIECE 01"; break;
	case 15: this.name="PIECE 02"; break;
	case 16: this.name="PIECE 06"; break;
	case 17: this.name="PIECE 07"; break;
	case 18: this.name="PIECE 03"; break;
	case 19: this.name="PIECE 04"; break;
	case 20: this.name="PIECE 05"; break;
	}	

	try {
		this.setNativeView(new ImageFileBasedObjectView("E:\\traSPArent DATA\\traSPArent Soft\\TestMASH\\TestMASH\\Piece.png"));
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}

	@Override
	public void sendMessage(int receiver, String message) {
		// TODO Auto-generated method stub
		
	}
}
