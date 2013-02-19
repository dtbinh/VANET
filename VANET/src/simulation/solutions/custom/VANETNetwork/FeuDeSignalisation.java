package simulation.solutions.custom.VANETNetwork;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;


/**
 * Classe FeuDeSignalisation
 * Un Croisement possède un FeuDeSignalisation, qui gère ceux qui peuvent passer et vers où
 * @author Wyvern
 *
 */
public class FeuDeSignalisation{
	
	/**
	 * Attribut servant de références pour l'orientation des voitures (une sorte de panneau de signalisation)
	 */
	private List<Croisement> directionsPossibles;
	
	/**
	 * Correspond à la voie qui est au vert. 
	 * Seules les voitures venant de voieLibre peuvent passer
	 * TODO toujours mettre les méthodes qui utilisent voieLibre en synchronized
	 */
	private Croisement voieLibre; 
	
	/**
	 * Constructeur et instanciateur d'un feu
	 * @param list des croisements adjacents
	 */
	public FeuDeSignalisation(List<Croisement> list) {
		this.voieLibre = null;
		this.directionsPossibles = list;
		
		if (this.directionsPossibles.size() > 0)
			this.voieLibre = this.directionsPossibles.get(0);
		this.changerFeuRegulierement();
	}
	
	/**
	 * Constructeur par défaut
	 */
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
	/**
	 * Accesseur sécurisé en lecture permettant de récupérer l'attribut VoieLibre
	 * @return un croisement
	 */
	public synchronized Croisement getVoieLibre() {
		return this.voieLibre;
	}
	/**
	 * Accesseur sécurisé en écriture permettant de modifier l'attribut VoieLibre
	 * @param voie la nouvelle directions possible
	 */
	public synchronized void setVoieLibre(Croisement voie) {
		this.voieLibre = voie;
	}
	 
	/** 
	 * Accesseur en lecture des directions possibles
	 * @return the directionsPossibles
	 */
	public List<Croisement> getDirectionsPossibles() {
		return this.directionsPossibles;
	}

	/**
	 * Accesseur en ecriture des directions possibles
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
	 * @param c un objet croisement
	 * @return un booléen
	 */
	public boolean contient(Croisement c) {
		return this.directionsPossibles.contains(c);
	}
	
	
	public Croisement directionAleatoireAutreQue(Croisement croisementANePasRenvoyer) { 
		Random r = new Random();

		Croisement res = this.directionsPossibles.get(r.nextInt(this.directionsPossibles.size()));
		while (res.equals(croisementANePasRenvoyer))
			res = this.directionsPossibles.get(r.nextInt(this.directionsPossibles.size()));
		return res;
	}
	/**
	 * Ajoute une direction en plus
	 * @param directionPossible
	 */
	public void ajouterDirectionPossible(Croisement directionPossible)
	{
		this.directionsPossibles.add(directionPossible);
		
		//Et on décide que la voie libre courante est directionPossible si elle n'était pas initialisée 
		//(normal, car il n'y avait aucun Croisement dans la liste lors du constructeur)
		if (this.voieLibre == null)
			this.voieLibre = directionPossible;
	}
	
	
}
