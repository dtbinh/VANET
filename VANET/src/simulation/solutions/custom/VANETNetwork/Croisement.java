package simulation.solutions.custom.VANETNetwork;


import simulation.entities.Agent;
import simulation.messages.Frame;
import simulation.messages.ObjectAbleToSendMessageInterface;
import simulation.multiagentSystem.MAS;
import simulation.solutions.custom.VANETNetwork.Messages.AgentsVANETFrame;
import simulation.solutions.custom.VANETNetwork.Messages.AgentsVANETMessage;

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
	 * Redéfinition de toString
	 */
	public String toString(){
		return "Croisement " + this.getUserId() + " ("+this.getPosition().x + "," + this.getPosition().y + ")";
	}

	@Override
	public void sendMessage(int receiver, String message) {
		// TODO Auto-generated method stub
		//TODO à finir
		//AgentsVANETMessage nouvMessage = new AgentsVANETMessae
		
	}
	
	/**
	 * Crée une voie à double sens reliant les deux croisements
	 * @param c1
	 * @param c2
	 */
	public static void relierCroisements(Croisement c1, Croisement c2){
		c1.ajouterCroisementAdjacent(c2);
		c2.ajouterCroisementAdjacent(c1);
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
	
	//TODO a supprimer
//	public void sendMessage() a supprimer
	//{
		/*
		try{ 	//Création message contenant les informations du feu de signalisation
				AgentsVANETMessage nouvMessageAWrapper = AgentsVANETMessage(AgentsVANETMessage.FEU_DE_SIGNALISATION,AgentsVANETMessage.VOITURE,AgentsVANETMessage.VOIE_LIBRE)
					
				//Transcription du message en byte[]
				byte[] transcriptionMessageAWrapper = nouvMessageAWrapper.toByteSequence();
			
				//Création d'une frame prête à être envoyée et intégration du byte[]
				AgentsVANETFrame nouvFrameAEnvoyer = AgentsVANETFrame(this.getUserId(),Frame.BROADCAST, nouvMessageAWrapper);
				this.sendMessage(nouvFrameAEnvoyer);}
				catch(Exception e) {}	
				
				
		*/
//	}
}
