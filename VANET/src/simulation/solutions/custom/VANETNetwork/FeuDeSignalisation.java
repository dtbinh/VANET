package simulation.solutions.custom.VANETNetwork;

import java.util.List;



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
	 */
	private Croisement voieLibre; 
	
	public FeuDeSignalisation(List<Croisement> list) {
		this.voieLibre = null;
		this.directionsPossibles = list;
		
		if (this.directionsPossibles.size() > 0)
			this.voieLibre = this.directionsPossibles.get(0);
	}
	
	



	/**
	 * Crée un thread qui change régulièrement la voie autorisée.
	 * TODO mettre les méthodes qui utilisent voieLibre en synchronized
	 */
	private void changerFeuRegulierement(){
		
	}
	
	private synchronized void setVoieLibre(Croisement voie) {
		this.voieLibre = voie;
	}
	 
	/**
	 * @return the directionsPossibles
	 */
	public List<Croisement> getDirectionsPossibles() {
		return this.directionsPossibles;
	}

	/**
	 * 
	 * @param x l'indice de la direction demandée (0 mini)
	 * @return la référence du croisement demandé ou null si x n'est pas un indice valide
	 */
	public Croisement getDirectionPossible(int x) {
		if (x > 0 && x < this.directionsPossibles.size())
			return this.directionsPossibles.get(x);
		else
			return null;
	}

	/**
	 * @param directionsPossibles the directionsPossibles to set
	 */
	public void setDirectionsPossibles(List<Croisement> directionsPossibles) {
		this.directionsPossibles = directionsPossibles;
	}
	
	public synchronized Croisement getVoieLibre() {
		return this.voieLibre;
	}
	
}
