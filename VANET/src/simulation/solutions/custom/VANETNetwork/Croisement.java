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
	
	/**
	 * Référence vers la voiture qui est actuellement en train de traverser le Croisement (une seule à la fois)
	 */
	private Voiture voitureCourante;
	
	private final String DIRE_QUI_PEUT_PASSER = "DIRE_QUI_PEUT_PASSER";
	private final String INDIQUER_DIRECTION = "INDIQUER_DIRECTION";
	
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

		this.voitureCourante = null;
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
			this.sendMessage(Frame.BROADCAST, INDIQUER_DIRECTION);
		}
	}
	@Override
	public void sendMessage(int receiver, String message) {
		if (message.equals(DIRE_QUI_PEUT_PASSER))
		{
			AgentsVANETMessage msg = new AgentsVANETMessage(getUserId(),receiver, AgentsVANETMessage.DIRE_QUI_PEUT_PASSER);
			msg.setVoieLibre(this.feu.getVoieLibre().getUserId());
			this.sendFrame(new AgentsVANETFrame(getUserId(), receiver, msg));
		}
		else if (message.equals(INDIQUER_DIRECTION))
			this.sendFrame(new AgentsVANETFrame(getUserId(), receiver, new AgentsVANETMessage(getUserId(),receiver, AgentsVANETMessage.INDIQUER_DIRECTION)));
		
		//else if je cherche à envoyer un message de type T...
	}
	
	/**
	 * Crée une voie à double sens reliant les deux croisements (dont this)
	 * @param idAutreCroisement ...C'est pas assez clair ?
	 */
	public void relierCroisements(int idAutreCroisement){
		Croisement autreCroisement = idToCroisement(idAutreCroisement);
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
	 * Renvoie le Croisement possédant l'id donné
	 * @param id
	 * @return le Croisement (marcherait probablement aussi pour les autres agents)
	 */
	private Croisement idToCroisement(int id) {
		return (Croisement) this.getMAS().getSimulatedObject(new ObjectSystemIdentifier(id)).getObject();
	}
	/**
	 * Traite le cas d'une voiture au croisement : lui interdit de traverser le croisement s'il y a déjà quelqu'un dessus,
	 * et lui donne la priorité sinon. Le synchronized est ici très important, peu importe le nombre de voitures qui demandent à ce qu'on
	 * s'occupe d'elles (= appellent cette méthode), on les traitera une par une (pas de conflit sur la valeur de voitureCourante).
	 * @param voiture la Voiture qui veut passer sur le Croisement
	 */
	public synchronized void gererCirculation(Voiture voiture) {
		if (this.voitureCourante != null && this.voitureCourante.getUserId() != voiture.getUserId()) // Si quelqu'un d'autre que "moi" (voiture) est déjà sur le carrefour
			voiture.setPeutBouger(false);
		
		else // il n'y a personne (ou alors j'étais déjà prioritaire, tant pis pour l'optimisation (on ré-écrit deux fois les mêmes choses))
		{
			this.voitureCourante = voiture;
			voiture.setPeutBouger(true);
		}	
	}
	
	/**
	 * Met à jour les données de la voiture appelante afin de lui dire où aller
	 * Si sa destinationFinale est adjacente, on lui indique le chemin (une étape, c'est pas long, et c'est à coup sûr !)
	 * sinon, on lui indique une direction au hasard parmi ce qui reste (pas de demi-tour)
	 * @param voiture la Voiture qui a besoin d'indications
	 */
	public void indiquerDirectionAPrendre(Voiture voiture) {
		if (this.feu.contient(voiture.getDestinationFinale()))
			// on remplace l'ancien éventuel parcours par "vazy c'est juste là"
			voiture.indiquerDestinationFinaleAdjacente();
		else
			voiture.setEtapeDApres(this.directionAleatoireAutreQue(voiture.getDernierCroisementParcouru()));
	}
	
	private Croisement directionAleatoireAutreQue(Croisement croisementANePasRenvoyer) {
		return this.feu.directionAleatoireAutreQue(croisementANePasRenvoyer);
	}
	
	/**
	 * Permet de modéliser le départ de la voiture qui était sur le Croisement
	 * Remet voitureCourante à null, en vérifiant toutefois que la voiture qui cherche à dire "je suis plus là, considère qu'il n'y a plus personne"
	 * est bien celle qui se trouvait sur le Croisement (ce qui devrait toujours être le cas, hmm ?)
	 * @param idVoitureAuDepart l'id de la voiture
	 */
	public synchronized void quitterCroisement(int idVoitureAuDepart) {
		if (idVoitureAuDepart == this.voitureCourante.getUserId())
			this.voitureCourante = null;
		else
			System.out.println("\n"+"ERREUR !!!!!!!!!!!!!!!!!!!!!!!!!!!!!! Ici Croisement n°" + this.getUserId() + ", la voiture n°" + idVoitureAuDepart + "prétend me quitter, mais la voiture prioritaire était " + this.voitureCourante.getUserId() + "!!!!");
	}
	
	/**
	 * Renvoie vrai si le croisement "possède une voie" pour aller jusqu'à c
	 * @param c le croisement peut-être adjacent
	 * @return le booléen ne tient pas compte d'éventuelles voies à sens unique
	 */
	public boolean estAdjacentA(Croisement c) {
		return this.feu.contient(c);
	}
	
	public boolean equals(Croisement c){
		return this.getUserId() == c.getUserId();
	}
	
}
