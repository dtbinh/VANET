package simulation.solutions.custom.VANETNetwork;
/**
 * La classe croisement sert de support aux feux de signalisation et leurs r�f�rencements permet de tisser la map.
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
	 * Un croisement a un feu de signalisation s'occupant de r�f�rencer les directions possibles � partir de ce point 
	 * et de r�guler le traffic 
	 */
	private FeuDeSignalisation feu;
	
	/**
	 * R�f�rence vers la voiture qui est actuellement en train de traverser le Croisement (une seule � la fois)
	 */
	private Voiture voitureCourante;
	
	private final String DIRE_QUI_PEUT_PASSER = "DIRE_QUI_PEUT_PASSER";
	private final String INDIQUER_DIRECTION = "INDIQUER_DIRECTION";
	
	/**
	 * Constructeur par d�faut appel� lors de la cr�ation du croisement (la cr�ation est g�r�e par MASH)
	 * Il faut consid�rer les param�tres comme invariants quoiqu'il arrive (tout comme le super constructeur et ses param�tres)
	 * sinon le constructeur n'est pas reconnu, et le croisement ne peut �tre cr��.
	 * 
	 * Note: Il est possible de mettre des instructions suppl�mentaires apr�s le super-constructeur.
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
	 * N�cessaire pour qu'une voiture puisse aller de ce Croisement � l'autre
	 * Ne fonctionne que dans un sens (pas de route depuis c vers this)
	 */
	public void ajouterCroisementAdjacent(Croisement c){
		this.feu.ajouterDirectionPossible(c);
	}
	 
	/**
	 * Red�finition de toString
	 */
	public String toString(){
		return "Croisement " + this.getUserId() + " ("+this.getPosition().x + "," + this.getPosition().y + ")";
	}
	
	/**
	 * Fonction principale de "maintien" d'activit� du croisement, permet d'appeler des fonctions, attention seulement � ne pas les rendre bloquantes.
	 * run est appel�e automatiquement par MASH lors du lancement de la simulation
	 * Note: Le croisement dispara�t lors de la fin de l'execution de run() 
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
		
		//else if je cherche � envoyer un message de type T...
	}
	
	/**
	 * Cr�e une voie � double sens reliant les deux croisements (dont this)
	 * @param idAutreCroisement ...C'est pas assez clair ?
	 */
	public void relierCroisements(int idAutreCroisement){
		Croisement autreCroisement = idToCroisement(idAutreCroisement);
		this.ajouterCroisementAdjacent(autreCroisement);
		autreCroisement.ajouterCroisementAdjacent(this);
	}
	/**
	 * Cr�e une voie � sens unique, la param�tre �tant le croisement o� "la rue" � sens unique m�ne
	 * @param croisementDest un croisement
	 */
	public void ajouterVoieSensUnique(Croisement croisementDest) {
		this.ajouterCroisementAdjacent(croisementDest);
	}
	
	/**
	 * Renvoie le Croisement poss�dant l'id donn�
	 * @param id
	 * @return le Croisement (marcherait probablement aussi pour les autres agents)
	 */
	private Croisement idToCroisement(int id) {
		return (Croisement) this.getMAS().getSimulatedObject(new ObjectSystemIdentifier(id)).getObject();
	}
	/**
	 * Traite le cas d'une voiture au croisement : lui interdit de traverser le croisement s'il y a d�j� quelqu'un dessus,
	 * et lui donne la priorit� sinon. Le synchronized est ici tr�s important, peu importe le nombre de voitures qui demandent � ce qu'on
	 * s'occupe d'elles (= appellent cette m�thode), on les traitera une par une (pas de conflit sur la valeur de voitureCourante).
	 * @param voiture la Voiture qui veut passer sur le Croisement
	 */
	public synchronized void gererCirculation(Voiture voiture) {
		if (this.voitureCourante != null && this.voitureCourante.getUserId() != voiture.getUserId()) // Si quelqu'un d'autre que "moi" (voiture) est d�j� sur le carrefour
			voiture.setPeutBouger(false);
		
		else // il n'y a personne (ou alors j'�tais d�j� prioritaire, tant pis pour l'optimisation (on r�-�crit deux fois les m�mes choses))
		{
			this.voitureCourante = voiture;
			voiture.setPeutBouger(true);
		}	
	}
	
	/**
	 * Met � jour les donn�es de la voiture appelante afin de lui dire o� aller
	 * Si sa destinationFinale est adjacente, on lui indique le chemin (une �tape, c'est pas long, et c'est � coup s�r !)
	 * sinon, on lui indique une direction au hasard parmi ce qui reste (pas de demi-tour)
	 * @param voiture la Voiture qui a besoin d'indications
	 */
	public void indiquerDirectionAPrendre(Voiture voiture) {
		if (this.feu.contient(voiture.getDestinationFinale()))
			// on remplace l'ancien �ventuel parcours par "vazy c'est juste l�"
			voiture.indiquerDestinationFinaleAdjacente();
		else
			voiture.setEtapeDApres(this.directionAleatoireAutreQue(voiture.getDernierCroisementParcouru()));
	}
	
	private Croisement directionAleatoireAutreQue(Croisement croisementANePasRenvoyer) {
		return this.feu.directionAleatoireAutreQue(croisementANePasRenvoyer);
	}
	
	/**
	 * Permet de mod�liser le d�part de la voiture qui �tait sur le Croisement
	 * Remet voitureCourante � null, en v�rifiant toutefois que la voiture qui cherche � dire "je suis plus l�, consid�re qu'il n'y a plus personne"
	 * est bien celle qui se trouvait sur le Croisement (ce qui devrait toujours �tre le cas, hmm ?)
	 * @param idVoitureAuDepart l'id de la voiture
	 */
	public synchronized void quitterCroisement(int idVoitureAuDepart) {
		if (idVoitureAuDepart == this.voitureCourante.getUserId())
			this.voitureCourante = null;
		else
			System.out.println("\n"+"ERREUR !!!!!!!!!!!!!!!!!!!!!!!!!!!!!! Ici Croisement n�" + this.getUserId() + ", la voiture n�" + idVoitureAuDepart + "pr�tend me quitter, mais la voiture prioritaire �tait " + this.voitureCourante.getUserId() + "!!!!");
	}
	
	/**
	 * Renvoie vrai si le croisement "poss�de une voie" pour aller jusqu'� c
	 * @param c le croisement peut-�tre adjacent
	 * @return le bool�en ne tient pas compte d'�ventuelles voies � sens unique
	 */
	public boolean estAdjacentA(Croisement c) {
		return this.feu.contient(c);
	}
	
	public boolean equals(Croisement c){
		return this.getUserId() == c.getUserId();
	}
	
}
