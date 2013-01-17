package simulation.solutions.custom.VANETNetwork;

import java.util.List;



/**
 * Classe FeuDeSignalisation
 * Un Croisement poss�de un FeuDeSignalisation, qui g�re ceux qui peuvent passer
 * @author Wyvern
 *
 */
public class FeuDeSignalisation{
	
	private List<Croisement> directionsPossibles;
	/**
	 * Correspond � la voie qui est au vert. 
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
	 * Cr�e un thread qui change r�guli�rement la voie autoris�e.
	 * TODO mettre les m�thodes qui utilisent voieLibre en synchronized
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
	 * @param x l'indice de la direction demand�e (0 mini)
	 * @return la r�f�rence du croisement demand� ou null si x n'est pas un indice valide
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
