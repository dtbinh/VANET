package simulation.solutions.custom.VANETNetwork.Messages;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import simulation.messages.Message;
import simulation.solutions.custom.VANETNetwork.Croisement;
import simulation.utils.IntegerPosition;

/**
 * Un message est un objet stockant des informations ; lors de l'envoi, ces informations seront stockées dans un int[] normalisé	
 * Cette classe gère les messages liés à VANET
 */
@SuppressWarnings("serial")
public class AgentsVANETMessage extends Message{
	
	//TODO (DONE) verifier si je dis pas de conner*es. / Oui, t'as dit pas mal de conneries
	//TODO corriger les conneries sus-mentionnées
	
	/**
	 * Pour certains messages, ni la position de l'envoyeur ni celle du receveur n'importent. 
	 * FIXME Posons carrément la question, est-ce que parfois, ON S'EN SERT ? On fait du VANET, quand même. Je ne vois qu'un seul endroit où on se sert des Positions, c'est lors du déplacement. Faut non plus tout recopier bêtement de PreyPredator.
	 */
	private IntegerPosition positionAgent;	
	
	private int senderID;
	private int receiverID;
	
	/**
	 * Time to live : nombre de sauts que le message est encore autorisé à faire. Décrémenté à chaque saut, on vérifiera si TTL > 0 avant de retransmettre.
	 */
	private int TTLMessage;
	//TODO : bien séparer les attributs qui ne sont utilisés que pour un type de message des autres
	
	//Flemme de faire les accesseurs d'où -> public TODO: Quand j'ai le temps optimiser ce foutu code (par exemple, faire des sous-classes de messageVANET qui héritent, et qui seront les seules à posséder leurs atributs particuliers)
	/**
	 * Liste des id des croisements du parcours. Utilisé par DIFFUSION_TRAJET.
	 */
	public List<Integer>parcoursMessage;
	
	/**
	 * Utilisé dans les messages de feux rouges.
	 * Contient l'id de la voie (du Croisement qu'on trouve au bout de cette voie) qui est au vert. Une et une seule voie par Croisement peut être au vert.
	 */
	private int voieLibre;
	
	/**
	 * Constantes codant le type de l'agent
	 */
	
	//On référence tous les identifiants des agents	
	//FIXME ... et on s'en sert quand de ces trucs là ?
	public static final int VOITURE=0;
	public static final int FEU_DE_SIGNALISATION=1;
	public static final int CROISEMENT=2;
			//TODO : verifier si ce champ est utile (un croisement, à première vue n'envoie pas de message tout seul, c'est le feu de signalisation par
			//l'intermediaire du croisement qui fait le boulot
	
	/**
	 * Attribut codant le type de message, celui-ci stocke les constantes ci-dessous
	 */
	private byte typeMessage;
	
	/**
	 * Constantes pouvant être stockées dans l'attribut typeMessage.
	 * Indiquent le "genre de message"
	 */
	public static final byte VOIE_LIBRE=0;//FIXME peut-etre à enlever si c'est inclus dans DIRE_QUI_PEUT_PASSER
	public static final byte ECHANGE_DE_POSITION=1;//FIXME FIXED trouver mieux que ce nom tout pourri : Non je le trouve cool ! / RE : échange de quelles positions ? un croisement et une voiture ? je suppose que c'est plutôt 2 voitures, auquel cas la voiture de devant ne voit pas sa place "échangée" avec l'autre, i.e 5 mètres derrière... personne ne recule dans un dépassement... si c'est pas un dépassement, ben je vois pas à quoi sert ce message. MEILLEUR NOM, B*RDEL !
	public static final byte DIRE_QUI_PEUT_PASSER=2;
	public static final byte DIFFUSION_TRAJET=3;
	public static final byte INDIQUER_DIRECTION=4;// sert principalement à obtenir une référence vers le croisement indicateur au moment opportun, afin de pouvoir décider où aller ensuite
	
	/**
	 * Nombre de sauts qu'un message est autorisé à faire à sa création. Constante utilisée par les types de message qui se relaient (ex : DIFFUSION_TRAJET)
	 */
	public static final byte TTL_DEPART=5;
	
	/**
	 * Constructeur de message générique. Initialise les attributs à des valeurs nulles, sauf les 3 plus importants, qui sont donnés.
	 * Pour initialiser plus précisement un message, utilisez la méthode init qui correspond au type souhaité, juste après le new.
	 */
	public AgentsVANETMessage(int sender, int receiver, byte type)
	{
		this.senderID=sender;
		this.receiverID=receiver;
		this.typeMessage=type;	
		this.positionAgent = null;
		this.voieLibre = -1;
		this.TTLMessage = AgentsVANETMessage.TTL_DEPART;
		this.parcoursMessage = new LinkedList<Integer>(); 
	}
	
	/**
	 * Initialise le message en considérant qu'il s'agit d'un message de type DIFFUSION_TRAJET.
	 * On en fait une copie de msg, et on rajoute croisARajouter à this.parcoursMessage (en début de liste)
	 * @param msg le message de type DIFFUSION_TRAJET reçu par la voiture, qui servira de modèle pour initialiser this.
	 * @param croisARajouter si msg.parcoursMessage vaut {B,C,D} et que la voiture appelante est sur la voie AB (croisARajouter vaut donc A), alors
	 * this.parcoursMessage vaudra {A,B,C,D} à la fin de cette méthode (la liste utilise le sens "d'où, vers où").
	 */
	public void initTrajetMessage(AgentsVANETMessage msg, Croisement croisARajouter) {
		if (this.getTypeMessage() == AgentsVANETMessage.DIFFUSION_TRAJET)
		{
			this.parcoursMessage = new LinkedList<Integer>(); // On réinitialise (des fois qu'un idiot ait rajouté à la main des Croisements entre le new et cette méthode)
			this.parcoursMessage.add(croisARajouter.getUserId());
			this.parcoursMessage.addAll(msg.parcoursMessage);
			
			this.setTTLMessage(msg.getTTLMessage() - 1); // le même TTL qu'avant, -1 car on a fait un saut.
		}
		else
			System.out.println("ERREUR : on cherche à initialiser un message de type DIFFUSION_TRAJET qui n'a pas été déclaré comme tel");
	}
	
	/*
	 * Liste des différents accesseurs en lecture des attributs d'un objet message
	 */
	
	public byte getTypeMessage() {
		return this.typeMessage;
	}	
	
	public IntegerPosition getPositionAgent() {
		return positionAgent;
	}
	 
	public int getTTLMessage(){
		return this.TTLMessage;
	}
	
	/*
	 * Liste des accesseurs en écriture des attributs de l'objet message  
	 */	
	
	public void setPositionAgent(IntegerPosition positionAgent) {
		this.positionAgent = positionAgent;
	}	
	
	// FIXME (FIXED) Est-ce vraiment utile ? OUI !
	public void setTypeMessage(byte nouvTypeMessage) {
		this.typeMessage=nouvTypeMessage;
	}
	
	public void setVoieLibre(int nouvID) {
		this.voieLibre=nouvID;
	}
	
	public void setTTLMessage(int nouvTTL) {
		this.TTLMessage=nouvTTL;
	}
	/**
	 * Méthode permettant de transcrire les informations du Message 
	 * dans un tableau de Byte
	 * 
	 * Fonction extrêmement importante, elle code en tableau de byte les attributs de l'objet message en vue d'une expédition.
	 * 
	 * NOTE: L'ordre est important et va induire un décodage lors de l'extraction des informations dans cet ordre précis
	 * On mettra toujours en premier le type de message afin de faciliter le décodage
	 * @return le message sous forme de byte[]
	 */
	public byte[] toByteSequence()
	{
		//TODO : Trouver un moyen d'avoir une taille adaptée pour le allocate
		if(this.typeMessage==DIRE_QUI_PEUT_PASSER)
			return ByteBuffer.allocate(50).put(this.typeMessage).putInt(this.senderID).putInt(receiverID).putInt(this.voieLibre).array();

		else if (this.typeMessage==DIFFUSION_TRAJET){
			ByteBuffer res = ByteBuffer.allocate(100).put(this.typeMessage).putInt(this.senderID).putInt(receiverID).putInt(this.TTLMessage);
				
			Iterator <Integer> iteratorParcours = this.parcoursMessage.iterator();
			
			res.putInt(this.parcoursMessage.size());
			while (iteratorParcours.hasNext())
				res.putInt(iteratorParcours.next());//FIXME peut poser problème de ne pas faire les putInt sur la même ligne que le allocate ?
			
			return res.array();
		}
		else if (this.typeMessage==INDIQUER_DIRECTION) 
			return ByteBuffer.allocate(50).put(this.typeMessage).putInt(this.senderID).putInt(receiverID).array();
		
		//else if == ...
		else
		{
			System.out.println("PROBLEME : Un message ne possède pas un typeMessage cohérent");
			return null;
		}
	}

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
