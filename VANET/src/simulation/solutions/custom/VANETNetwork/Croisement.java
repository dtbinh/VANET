package simulation.solutions.custom.VANETNetwork;


import simulation.entities.Agent;
import simulation.messages.ObjectAbleToSendMessageInterface;
import simulation.multiagentSystem.MAS;

public class Croisement extends Agent implements ObjectAbleToSendMessageInterface{

	private FeuDeSignalisation feu;
	
	
	public Croisement (MAS mas, Integer id, Float energy,Integer range){
		super(mas, id, range);

		this.feu = new FeuDeSignalisation();
	}	
	
	/**
	 * Modifie le FeuDeSignalisation du croisement pour y ajouter "une voie adjacente"
	 */
	public void ajouterCroisementAdjacent(Croisement croisement){
		this.feu.ajouterDirectionPossible(croisement);
	}
	 
	/**
	 * Red�finition de toString
	 */
	public String toString(){
		return "Croisement " + this.getUserId() + " ("+this.getPosition().x + "," + this.getPosition().y + ")";
	}

	@Override
	public void sendMessage(int receiver, String message) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Cr�e une voie � double sens reliant les deux croisements
	 * @param c1
	 * @param c2
	 */
	public static void relierCroisements(Croisement c1, Croisement c2){
		c1.ajouterCroisementAdjacent(c2);
		c2.ajouterCroisementAdjacent(c1);
	}
	
	/**
	 * renvoie vrai si le croisement "poss�de une voie" pour aller jusqu'� c
	 * @param c le croisement peut-�tre adjacent
	 * @return le bool�en ne tient pas compte d'�ventuelles voies � sens unique
	 */
	public boolean estAdjacentA(Croisement c) {
		return this.feu.contient(c);
	}
	
	public boolean equals(Croisement c){ //TODO r�fl�chir : est-ce suffisant ?
		return this.getUserId() == c.getUserId();
	}
}
