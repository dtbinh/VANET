package simulation.solutions.custom.VANETNetwork.Messages;

import java.nio.ByteBuffer;

import simulation.messages.Message;
import simulation.solutions.custom.VANETNetwork.Croisement;
import simulation.utils.IntegerPosition;

/**
 * @author Reykjanes
 *
 */
@SuppressWarnings("serial")
public class AgentsVANETMessage extends Message{
	
	//TODO verifier si je dis pas de conner*es
	/**
	 * Principaux attributs d'un message.
	 * 
	 * Un message est un objet stockant des informations, lors de l'envoi ces informations seront stockées dans une int[] normalisé	
	 */
	
	/**
	 * Pour certains messages, ni la position de l'envoyeur ni celle du receveur n'importent.
	 * FIXME remanier le constructeur ou en créer d'autres lorsqu'on s'en servira
	 */
	private IntegerPosition positionAgent;	
	
	private int senderID;
	private int receiverID;
	private int capaciteMessage;
	
	//Flemme de faire les accesseurs d'où -> public TODO: Quand j'ai le temps optimiser ce foutu code
	public int[] parcoursMessage;
	
	/**
	 * Utilisé dans les messages de feux rouges.
	 * Contient l'id de la voie (du Croisement qu'on trouve au bout de cette voie) qui est au vert. Une et une seule voie par Croisement peut être au vert.
	 */
	private int voieLibre;
	/**
	 * Constantes codant le type de l'agent
	 */
	
	//On référence tous les identifiants des agents	
	public static final int VOITURE=0;
	public static final int FEU_DE_SIGNALISATION=1;
	public static final int CROISEMENT=2;
			//TODO : verifier si ce champ est utile (un croisement, à première vue n'envoie pas de message tout seul, c'est le feu de signalisation par
			//l'intermediaire du croisement qui fait le boulot
	
			//TODO : Si on en a fini avec l'ajout de nouveaux champs, il faut essayer dès que possible de mettre des type byte au lieu d'int pour optimiser; 
	/**
	 * Attribut codant le type de message, celui-ci stocke les constantes ci-dessous
	 */
	private byte typeMessage;
	
	/**
	 * Constantes pouvant être stockées dans l'attribut typeMessage.
	 * Indiquent le "genre de message"
	 */
	public static final byte VOIE_LIBRE=0;//FIXME peut-etre à enlever si c'est inclus dans DIRE_QUI_PEUT_PASSER
	public static final byte ECHANGE_DE_POSITION=1;//FIXME trouver mieux que ce nom tout pourri
	public static final byte DIRE_QUI_PEUT_PASSER=2;
	public static final byte DIFFUSION_TRAJET=3;
	
	//Cet attribut correspond -par défaut- à la capaciteMessage, il sert en autre de TTL pour éviter une congestion. 
	public static final byte TTL_OPTIMAL=5;
	
	/**
	 * Constructeur de message.
	 * Il est possible d'avoir plusieurs constructeurs en fonction des besoins, ou sinon on peut passer tous les paramètres, 
	 * en mettant 0 ou null pour ceux qui ne servent pas dans les appels qui ne les utilisent pas
	 */
	//TODO: corriger ce constructeur et le faire générique !! On fera des "initVoieLibre" pour typer e remplir le message
	public AgentsVANETMessage(int sender, int receiver, byte type)
	{
		this.senderID=sender;
		this.receiverID=receiver;
		this.typeMessage=type;		
	}
	
	public void initTrajetMessage(AgentsVANETMessage msg, Croisement derCrois,Croisement dest){
		
		msg.setCapaciteMessage(AgentsVANETMessage.TTL_OPTIMAL);
	
		for (int i=0; i < TTL_OPTIMAL; i++){
			parcoursMessage[i]=-1;}
		
		parcoursMessage[0]=dest.getUserId();
		parcoursMessage[1]=derCrois.getUserId();
		
		msg.setCapaciteMessage(TTL_OPTIMAL-2);		
	}
	/**
	 * Cette fonction prends en paramètre un msg, dans lequel on va rajouter un croisement pour construire le parcours inverse du message
	 *  /!\ Le boolean renvoyé indique si le message est fiable si il ne l'est pas alors il ne faut pas ré-emettre ce message car inutilisable /!\
	 *  On peut utiliser des itérateurs ou liste histoire d'alléger le code.  
	 */
	public boolean insérerInformationsTrajet(AgentsVANETMessage msg, Croisement dernierCroisement, Croisement croisementDestination)
	{
		//De base on considère le message comme erroné, ssi tout les test sont OK ont met RES à true;
		boolean res =false;
		int i=0;

		if(	(msg.typeMessage==AgentsVANETMessage.DIFFUSION_TRAJET) 
			&&
			((msg.getCapaciteMessage()-1) >= 0)
			){
				//On insère le nouveau croisement ssi le parcours est cohérent (A<->H<->B donnera A,H,B, si un croisement est manquant alors le parcours n'est pas fiable)
			while (	i<TTL_OPTIMAL
					&& parcoursMessage[i] != croisementDestination.getUserId() )				
			{i++;}
			//On peut rajouter un nouveau croiseùent
			if (i < TTL_OPTIMAL-1){
				parcoursMessage[i+1]=dernierCroisement.getUserId();
				res=true;
			}			
		}
		return res;
	}
	/**
	 * Liste des différents accesseurs en lecture des attributs d'un objet message
	 * @return
	 */
	
	public byte getTypeMessage() {
		return this.typeMessage;
	}	
	
	public IntegerPosition getPositionAgent() {
		return positionAgent;
	}
	
	public int getCapaciteMessage(){
		return this.capaciteMessage;
	}
	
	/**
	 * Liste des accesseurs en écriture des attributs de l'objet message  
	 * @return
	 */	
	
	public void setPositionAgent(IntegerPosition positionAgent) {
		this.positionAgent = positionAgent;
	}	
	
	/// FIXME (FIXED) Est-ce vraiment utile ? OUI !
	public void setTypeMessage(byte nouvTypeMessage) {
		this.typeMessage=nouvTypeMessage;
	}
	
	public void setCapaciteMessage(int nouvCapa){
		this.capaciteMessage=nouvCapa;
	}

	public void setVoieLibre(int nouvID){
		this.voieLibre=nouvID;
	}
	/**
	 * Méthode permettant de transcrire les informations de l'objet FeuDeSignalisation 
	 * dans un tableau de Byte
	 * 
	 * Fonction extrêmement importante, elle code en tableau de byte les attributs de l'objet message en vue d'une expédition.
	 * 
	 * NOTE: L'ordre est important et va induire un décodage lors de l'extraction des informations dans cet ordre précis
	 * @return le message sous forme de byte[]
	 */
	public byte[] toByteSequence()
	{
		//TODO : Trouver la fonction permettant d'avoir une taille adaptée 
		if(this.typeMessage==DIRE_QUI_PEUT_PASSER)
			return ByteBuffer.allocate(50).put(this.typeMessage).putInt(this.senderID).putInt(receiverID).putInt(this.voieLibre).array();

		else if (this.typeMessage==DIFFUSION_TRAJET){
			ByteBuffer res = ByteBuffer.allocate(100).put(this.typeMessage).putInt(this.senderID).putInt(receiverID).putInt(this.capaciteMessage);
			for (int i=0; i <TTL_OPTIMAL-this.capaciteMessage;i++)
				res.putInt(this.parcoursMessage[i]);
			
			return res.array();
		}
		//else if == ...
		else
		{
			System.out.println("PROBLEME : Un message ne possède pas un typeMessage cohérent");
			return null;
		}
	}
	
	/**
	 * Méthode inverse de toByteSequence : décortique un tableau de byte et en fait un Message
	 * L'ordre est important
	 * @param data
	 * @return le message extrait
	 */

	@Override
	public int getReceiver() {
		return this.receiverID;
	}
	
	@Override
	public int getSender() {
		return this.senderID;
	}
	
	public int getVoieLibre(){
		return this.voieLibre;
	}

}
