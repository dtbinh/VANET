package simulation.solutions.custom.VANETNetwork.Messages;
import java.nio.ByteBuffer;

import javax.swing.SizeRequirements;

import simulation.messages.Frame;
import simulation.messages.Message;
import simulation.solutions.custom.VANETNetwork.Croisement;
import simulation.utils.IntegerPosition;
import sun.security.action.GetIntegerAction;
/**
 * @author Wyvern
 *
 */
@SuppressWarnings("serial")
public class FeuDeSignalisationMessages extends Message{
	
	//Informations nécessaire à l'utilisation d'un message
	private int senderID;
	private int receiverID;
	private int posX;
	private int posY;
	
	//TODO : Si on en a finit avec l'ajout de nouveaux champs, il faut essayer dès que possible mettre des type byte au lieu d'int pour optimiser; 
	//
	//Un champs d'une frame sera le type de message
	public int typeMessage;
	
	//On référence tout les types de message (liste provisoire)
	public static final int VOIE_LIBRE=0;
	
	
	public void SendMessageFeuDeSignalisation(int sender, int receiver, byte type,IntegerPosition pos, int typeMessage)
	{
		this.senderID=sender;
		this.receiverID=receiver;
		this.typeMessage=type;
		this.posX = pos.x;
		this.posY= pos.y;
	}
	
	
	public int getReceiver() 
	{
		return this.receiverID;
	}
	
	public int getSender() 
	{
		return senderID;
	}
	
	/**
	 * Méthode permettant de transcrire les information de l'objet FeuDeSignalisation 
	 * dans un tableau de Byte
	 */
	public byte[] toByteSequence()
	{
		//On prépare le messages, içi la taille 500 sera à réduire 
		//TODO : Trouver al fonction permettant d'avoir une taille adaptée 
		return (ByteBuffer.allocate(500).putInt(this.senderID).putInt(this.receiverID).putInt(this.posX).putInt(this.posY).putInt(this.typeMessage).array());
	
	}
	
	

}
