package simulation.solutions.custom.VANETNetwork;

import java.util.LinkedList;
import java.util.List;

import simulation.entities.Agent;
import simulation.messages.ObjectAbleToSendMessageInterface;
import simulation.solutions.custom.PreyPredator.Messages.PreyPredatorFrame;
import simulation.solutions.custom.PreyPredator.Messages.PreyPredatorMessage;
import simulation.solutions.custom.VANETNetwork.Messages.AgentsVANETFrame;
import simulation.solutions.custom.VANETNetwork.Messages.AgentsVANETMessage;



/**
 * Classe FeuDeSignalisation
 * Un Croisement possède un FeuDeSignalisation, qui gère ceux qui peuvent passer
 * @author Wyvern
 *
 */
public class FeuDeSignalisation{
	
	private List<Croisement> directionsPossibles;
	/**
	 * Correspond à la voie qui est au vert. 
	 * Seules les voitures venant de voieLibre peuvent passer
	 * TODO toujours mettre les méthodes qui utilisent voieLibre en synchronized
	 */
	private Croisement voieLibre; 
	
	public FeuDeSignalisation(List<Croisement> list) {
		this.voieLibre = null;
		this.directionsPossibles = list;
		
		if (this.directionsPossibles.size() > 0)
			this.voieLibre = this.directionsPossibles.get(0);
		this.changerFeuRegulierement();
	}
	
	public FeuDeSignalisation() {
		this.voieLibre = null;
		this.directionsPossibles = new LinkedList<Croisement>();
		this.changerFeuRegulierement();
	}



	/**
	 * Crée un thread qui change régulièrement la voie autorisée.
	 */
	private void changerFeuRegulierement(){
		new Thread(new ChangerVoieAutorisee(this)).start();
	}
	
	public synchronized Croisement getVoieLibre() {
		return this.voieLibre;
	}
	
	public synchronized void setVoieLibre(Croisement voie) {
		this.voieLibre = voie;
	}
	 
	/**
	 * @return the directionsPossibles
	 */
	public List<Croisement> getDirectionsPossibles() {
		return this.directionsPossibles;
	}

	/**
	 * @param directionsPossibles the directionsPossibles to set
	 */
	public void setDirectionsPossibles(List<Croisement> directionsPossibles) {
		this.directionsPossibles = directionsPossibles;
	}
	
	/**
	 * Obtention d'une des directions du Feu par indice
	 * @param x l'indice de la direction demandée (0 mini)
	 * @return la référence du croisement demandé ou null si x n'est pas un indice valide
	 */
	public Croisement getDirectionPossible(int x) {
		if (x >= 0 && x < this.directionsPossibles.size())
			return this.directionsPossibles.get(x);
		else
			return null;
	}

	/**
	 * renvoie true si directionsPossibles contient le croisement c et false sinon
	 * @param c
	 * @return
	 */
	public boolean contient(Croisement c) {
		return this.directionsPossibles.contains(c);
	}
	
	/**
	 * Ajoute une direction en plus
	 * @param directionPossible
	 */
	public void ajouterDirectionPossible(Croisement directionPossible)
	{
		this.directionsPossibles.add(directionPossible);
	}
	
	
}
