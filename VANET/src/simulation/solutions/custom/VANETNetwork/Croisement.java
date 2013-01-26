package simulation.solutions.custom.VANETNetwork;
/**
 * La classe croisement sert de support aux feux de signalisation et leurs référencements permet de tisser la map.
 * @Author Reykjanes 
 */

import simulation.entities.Agent;
import simulation.messages.Frame;
import simulation.messages.ObjectAbleToSendMessageInterface;
import simulation.multiagentSystem.MAS;
import simulation.multiagentSystem.ObjectSystemIdentifier;
import simulation.solutions.custom.VANETNetwork.Messages.AgentsVANETFrame;
import simulation.solutions.custom.VANETNetwork.Messages.AgentsVANETMessage;

public class Croisement extends Agent implements ObjectAbleToSendMessageInterface{

	/** 
	 * Un croisement a un feu de signalisation s'occupant de référencer les directions possibles à partir de ce point 
	 * et de réguler le traffic 
	 */
	private FeuDeSignalisation feu;
	
	private final String DIRE_QUI_PEUT_PASSER = "DIRE_QUI_PEUT_PASSER";
	/**
	 * Constructeur par défaut appelé lors de la création du croisement (la création est gérée par MASH)
	 * Il faut considérer les paramètres comme invariants quoiqu'il arrive (tout comme le super constructeur et ses paramètres)
	 * sinon le constructeur n'est pas reconnu, et le croisement ne peut être créé.
	 * 
	 * Note: Il est possible de mettre des instructions supplémentaires après le super-constructeur.
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
	 * Nécessaire pour qu'une voiture puisse aller de ce Croisement à l'autre
	 * Ne fonctionne que dans un sens (pas de route depuis c vers this)
	 */
	public void ajouterCroisementAdjacent(Croisement c){
		this.feu.ajouterDirectionPossible(c);
	}
	 
	/**
	 * Redéfinition de toString
	 */
	public String toString(){
		return "Croisement " + this.getUserId() + " ("+this.getPosition().x + "," + this.getPosition().y + ")";
	}
	
	/**
	 * Fonction principale de "maintien" d'activité du croisement, permet d'appeler des fonctions, attention seulement à ne pas les rendre bloquantes.
	 * run est appelée automatiquement par MASH lors du lancement de la simulation
	 * Note: Le croisement disparaît lors de la fin de l'execution de run() 
	 */
	public void run()
	{
		// wait a little amount of time to allow the construction of others agents
		try{Thread.sleep(500);}catch(Exception e){}		
		while (true){
			try{Thread.sleep(100);}catch(Exception e){}		
			this.sendMessage(Frame.BROADCAST, DIRE_QUI_PEUT_PASSER);
		}
		
	}
	@Override
	public void sendMessage(int receiver, String message) {
		if (message.equals(DIRE_QUI_PEUT_PASSER))
			this.sendFrame(new AgentsVANETFrame(getUserId(), receiver, new AgentsVANETMessage(getUserId(),receiver, AgentsVANETMessage.DIRE_QUI_PEUT_PASSER,this.feu.getVoieLibre().getUserId())));
		//else if je cherche à envoyer un message de type T...
	}
	
	/**
	 * Crée une voie à double sens reliant les deux croisements (dont this)
	 * @param idAutreCroisement ...C'est pas assez clair ?
	 */
	public void relierCroisements(int idAutreCroisement){
		Croisement autreCroisement = (Croisement) this.getMAS().getSimulatedObject(new ObjectSystemIdentifier(idAutreCroisement)).getObject();
		this.ajouterCroisementAdjacent(autreCroisement);
		autreCroisement.ajouterCroisementAdjacent(this);
	}
	/**
	 * Crée une voie à sens unique, la paramètre étant le croisement où "la rue" à sens unique mène
	 * @param croisementDest un croisement
	 */
	public void ajouterVoieSensUnique(Croisement croisementDest) {
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
