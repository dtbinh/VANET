package simulation.solutions.custom.VANETNetwork;


import simulation.entities.Agent;
import simulation.messages.ObjectAbleToSendMessageInterface;
import simulation.multiagentSystem.MAS;
import simulation.utils.IntegerPosition;

public class Croisement extends Agent implements ObjectAbleToSendMessageInterface{
	private String name;
	private FeuDeSignalisation feu;
	
	private static Map map = new Map(); 
	
	public Croisement (MAS mas, Integer id, Float energy,Integer range, String name){
		super(mas, id, range);
		this.name = name;
		this.feu = new FeuDeSignalisation(Croisement.map.listCroisAdjacents(this.name));
	}
	
	 
	public String getName(){
		return this.name;
	}
	/**
	 * Redéfinition de toString
	 */
	public String toString(){
		return this.name + " ("+this.pos.x + "," + this.pos.y + ")";
	}

	@Override
	public void sendMessage(int receiver, String message) {
		// TODO Auto-generated method stub
		
	}
}
