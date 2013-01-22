package simulation.solutions.custom.VANETNetwork;
/**
 * La classe croisement sert de support au feux de signalisation et leurs r�f�rencement permet de tisser la map.
 * @Author Reykjanes 
 */

import simulation.entities.Agent;
import simulation.messages.ObjectAbleToSendMessageInterface;
import simulation.multiagentSystem.MAS;

public class Croisement extends Agent implements ObjectAbleToSendMessageInterface{

	/** 
	 * Un croisement a un feu de signalisation s'occupant de r�f�rencer les directions possibles et de r�guler le traffic 
	 */
	private FeuDeSignalisation feu;
	
	
	/**
	 * Constructeur par d�faut appell� lors de la cr�ation du croisement (la cr�ation est g�r�e par MASH)
	 * Il faut consid�rer les param�tres comme invariant quoiqu'il arrive (tout comme le super constructeur et ses param�tre)
	 * 	sinon le constructeur n'est pas reconnu, et le croisement ne peut �tre cr��e.
	 * 
	 *  Note: Il est possible de mettre des instructions suppl�mentaire apr�s le super-constructeur.
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
	 * Red�finition de toString
	 */
	public String toString(){
		return "Croisement " + this.getUserId() + " ("+this.getPosition().x + "," + this.getPosition().y + ")";
	}
	
	/**
	 * Fonction principale de "maintient" d'activit� du croisement, permet d'appeller des fonctions, attention seulement � ne pas les rendre bloquantes.
	 * 
	 * Note: Le croisement dispara�t lors de la fin de l'execution de run() 
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
		//TODO � finir
		//AgentsVANETMessage nouvMessage = new AgentsVANETMessae
		
	}
	
	/**
	 * Cr�e une voie � double sens reliant les deux croisements
	 * @param c1 premier croisement
	 * @param c2 deuxi�me croisement
	 */
	public static void relierCroisements(Croisement c1, Croisement c2){
		c1.ajouterCroisementAdjacent(c2);
		c2.ajouterCroisementAdjacent(c1);
	}
	/**
	 * Cr�e une voie � sens unique, la param�tre �tant le croisement o� "la rue" � sens unique m�ne
	 * @param croisementDest un croisement
	 */
	public void ajouterVoieSensUnique(Croisement croisementDest)
	{
		this.ajouterCroisementAdjacent(croisementDest);
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
