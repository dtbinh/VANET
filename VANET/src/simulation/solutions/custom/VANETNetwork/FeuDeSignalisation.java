package simulation.solutions.custom.VANETNetwork;

import java.util.Vector;

import simulation.entities.Agent;
import simulation.messages.Frame;
import simulation.messages.ObjectAbleToSendMessageInterface;
import simulation.multiagentSystem.MAS;
import simulation.solutions.custom.PreyPredator.Messages.PreyPredatorFrame;
import simulation.solutions.custom.PreyPredator.Messages.PreyPredatorMessage;
import simulation.utils.IntegerPosition;
import simulation.views.entity.imageInputBased.ImageFileBasedObjectView;


/**
 * Classe FeuDeSignalisation
 * 
 * @author Wyvern
 *
 */
public  class FeuDeSignalisation extends Agent implements ObjectAbleToSendMessageInterface{
	
	private final static nativeEntityView AFFICHE_DEFAULT= basic;
	public FeuDeSignalisation(int x, int y)
	{
		//TODO Faire le constructue r de FeuDeSignalisation
		super(mas, y, y, nativeEntityView);
		FeuDeSignalisation nouvFeu = new FeuDeSignalisation(x, y);

		
	}
	
	

	@Override
	public void sendMessage(int receiver, String message) {
		// TODO Auto-generated method stub
		
	}
	
}
