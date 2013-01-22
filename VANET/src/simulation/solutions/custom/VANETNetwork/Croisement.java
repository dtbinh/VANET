package simulation.solutions.custom.VANETNetwork;
/**
 * La classe croisement sert de support au feux de signalisation et leurs référencement permet de tisser la map.
 * @Author Reykjanes 
 */

import simulation.entities.Agent;
import simulation.messages.ObjectAbleToSendMessageInterface;
import simulation.multiagentSystem.MAS;

public class Croisement extends Agent implements ObjectAbleToSendMessageInterface{

	/** 
	 * Un croisement a un feu de signalisation s'occupant de référencer les directions possibles et de réguler le traffic 
	 */
	private FeuDeSignalisation feu;
	
	
	/**
	 * Constructeur par défaut appellé lors de la création du croisement (la création est gérée par MASH)
	 * Il faut considérer les paramètres comme invariant quoiqu'il arrive (tout comme le super constructeur et ses paramètre)
	 * 	sinon le constructeur n'est pas reconnu, et le croisement ne peut être créée.
	 * 
	 *  Note: Il est possible de mettre des instructions supplémentaire après le super-constructeur.
	 * @param mas
	 * @param id
	 * @param energy
	 * @param range
	 */
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
	 * Redéfinition de toString
	 */
	public String toString(){
		return "Croisement " + this.getUserId() + " ("+this.getPosition().x + "," + this.getPosition().y + ")";
	}
	
	/**
	 * Fonction principale de "maintient" d'activité du croisement, permet d'appeller des fonctions, attention seulement à ne pas les rendre bloquantes.
	 * 
	 * Note: Le croisement disparaît lors de la fin de l'execution de run() 
	 */
	public void run()
	{
		// wait a little amount of time to allow the construction of others agents
		try{Thread.sleep(500);}catch(Exception e){}		
		while (true){
			try{Thread.sleep(5);}catch(Exception e){}		
		}
		
	}
	@Override
	public void sendMessage(int receiver, String message) {
		// TODO Auto-generated method stub
		//TODO à finir
		//AgentsVANETMessage nouvMessage = new AgentsVANETMessae
		
	}
	
	/**
	 * Crée une voie à double sens reliant les deux croisements
	 * @param c1 premier croisement
	 * @param c2 deuxième croisement
	 */
	public static void relierCroisements(Croisement c1, Croisement c2){
		c1.ajouterCroisementAdjacent(c2);
		c2.ajouterCroisementAdjacent(c1);
	}
	/**
	 * Crée une voie à sens unique, la paramètre étant le croisement où "la rue" à sens unique mène
	 * @param croisementDest un croisement
	 */
	public void ajouterVoieSensUnique(Croisement croisementDest)
	{
		this.ajouterCroisementAdjacent(croisementDest);
	}
	
	/**
	 * renvoie vrai si le croisement "possède une voie" pour aller jusqu'à c
	 * @param c le croisement peut-être adjacent
	 * @return le booléen ne tient pas compte d'éventuelles voies à sens unique
	 */
	public boolean estAdjacentA(Croisement c) {
		return this.feu.contient(c);
	}
	
	public boolean equals(Croisement c){ //TODO réfléchir : est-ce suffisant ?
		return this.getUserId() == c.getUserId();
	}
	
}
