package simulation.solutions.custom.AntMWAC.Ant.Messages;

import java.nio.ByteBuffer;

import simulation.solutions.custom.AntMWAC.MWAC.Messages.MWACFrame;
import simulation.solutions.custom.AntMWAC.MWAC.Messages.MWACMessage;


@SuppressWarnings("serial")
public class AntMessage_Initialisation extends MWACMessage{
		
	
	public  float EnergyLevel; //le niveau d'energie 
	public  int HopCount;      //le nombre de sauts qui separe l'expediteur de la station de base 
	
	/******************************************************************************/
	
	public AntMessage_Initialisation (int sender, int receiver, byte [] data){
		
		super(sender,receiver,MWACMessage.msgINITIALIZATION);
		
		ByteBuffer buf=ByteBuffer.wrap(data);
		this.EnergyLevel = buf.getFloat();
		this.HopCount = buf.getInt();
	}
	
	
	/******************************************************************************************/
	
	public AntMessage_Initialisation (int sender, int receiver, float EnergyLevel, int HopCount){
		
		super(sender, receiver, MWACMessage.msgINITIALIZATION);

		this.EnergyLevel = EnergyLevel;
		this.HopCount = HopCount;
	}
	
	/******************************************************************************************/
	
	public float get_EnergyLevel(){
		return this.EnergyLevel;
	}
	/******************************************************************************************/
	public int get_HopCount(){
		return this.HopCount;
	}

	
	
	/******************************************************************************************/
	
	public  byte[] toByteSequence() 
	{
		ByteBuffer buf = ByteBuffer.allocate(8);  
		
		buf.putFloat(this.EnergyLevel);
		buf.putInt(this.HopCount);
		
		return super.toByteSequence(buf.array());
	}

	
	/******************************************************************************************/
	public String toString()
	{
		return "AntMWAC_Initialization_Message instancied by "+
		getSender()+" to "+MWACFrame.receiverIdToString(getReceiver())+ ", Energy is "+
		get_EnergyLevel()+", Hop Count is "+get_HopCount();
	}

}

