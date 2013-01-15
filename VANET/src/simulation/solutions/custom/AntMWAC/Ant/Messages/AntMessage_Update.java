package simulation.solutions.custom.AntMWAC.Ant.Messages;

import java.nio.ByteBuffer;
import simulation.solutions.custom.AntMWAC.MWAC.Messages.MWACFrame;
import simulation.solutions.custom.AntMWAC.MWAC.Messages.MWACMessage;

/**
 ce message est envoyé par un capteur pour que ses voisins mettent a  jour son niveau d'énergie
 **/

@SuppressWarnings("serial")
public class AntMessage_Update extends MWACMessage{
	
	
	private float EnergyLevel;

	/******************************************************************************************/
	public AntMessage_Update (int sender, int receiver, byte [] data){
		
		super(sender, receiver,MWACMessage.msgUPDATE);
		
		ByteBuffer buf=ByteBuffer.wrap(data);
		
		this.EnergyLevel = buf.getFloat();
	}
	
	/******************************************************************************************/
	public AntMessage_Update (int sender, int receiver, float EnergyLevel){
		
		super(sender,receiver, MWACMessage.msgUPDATE);
		
		this.EnergyLevel = EnergyLevel;
	}
	/******************************************************************************************/
	
	public float get_Energy_Level(){
		return this.EnergyLevel;
	}
	/******************************************************************************************/
	
	public  byte[] toByteSequence() 
	{
		ByteBuffer buf = ByteBuffer.allocate(4);
		
		buf.putFloat(this.EnergyLevel);
		
		return super.toByteSequence(buf.array());
	}

	/******************************************************************************************/
	public String toString()
	{
		return "AntMWAC_Update_Message instancied by "+
		this.getSender()+" to "+MWACFrame.receiverIdToString(this.getReceiver())+
		", Energy Level is "+this.get_Energy_Level();
	}

}

