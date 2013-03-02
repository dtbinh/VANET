package simulation.solutions.custom.VANETNetwork.Messages;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import simulation.messages.Message;
import simulation.solutions.custom.VANETNetwork.Croisement;

/**
 * Un message est un objet stockant des informations ; lors de l'envoi, ces informations seront stockées dans un int[] normalisé	
 * Cette classe gère les messages liés à VANET
 */
@SuppressWarnings("serial")
public class AgentsVANETMessage extends Message{
	
	//TODO (DONE) verifier si je dis pas de conner*es. / Oui, t'as dit pas mal de conneries
	//TODO corriger les conneries sus-mentionnées
	
	/**
	 * Constantes pouvant être stockées dans l'attribut typeMessage.
	 * Indiquent le "genre de message"
	 */
	public static final byte DIRE_QUI_PEUT_PASSER=1;
	public static final byte DIFFUSION_TRAJET=2;
	public static final byte INDIQUER_DIRECTION=3;// sert principalement à obtenir une référence vers le croisement indicateur au moment opportun, afin de pouvoir décider où aller ensuite
	
	/**
	 * Attribut codant le type de message, celui-ci stocke les constantes ci-dessus
	 */
	private byte typeMessage;
	
	private int senderID;
	private int receiverID;
	
	
	// TODO: Quand j'ai le temps optimiser ce foutu code (par exemple, faire des sous-classes de messageVANET qui héritent, et qui seront les seules à posséder leurs attributs particuliers)
	
/* ========== Attributs en rapport avec DIFFUSION_TRAJET ==========*/	
	/**
	 * Liste des id des croisements du parcours. Utilisé par DIFFUSION_TRAJET.
	 */
	public List<Integer>parcoursMessage;
	
	/**
	 * Time to live : nombre de sauts que le message est encore autorisé à faire. Décrémenté à chaque saut, on vérifiera si TTL > 0 avant de retransmettre.
	 */
	private int TTLMessage;
	
	/**
	 * Nombre de sauts qu'un message est autorisé à faire à sa création. Constante utilisée par les types de message qui se relaient (ex : DIFFUSION_TRAJET)
	 */
	public static final byte TTL_DEPART=4;
	
	
/* ========== Attributs en rapport avec DIRE_QUI_PEUT_PASSER ==========*/	
	/**
	 * Utilisé dans les messages de feux rouges.
	 * Contient l'id de la voie (du Croisement qu'on trouve au bout de cette voie) qui est au vert. Une et une seule voie par Croisement peut être au vert.
	 */
	private int voieLibre;
	
	
	/**
	 * Constructeur de message générique. Initialise les attributs à des valeurs nulles, sauf les 3 plus importants, qui sont donnés.
	 * Pour initialiser plus précisement un message, utilisez la méthode init qui correspond au type souhaité, juste après le new.
	 */
	public AgentsVANETMessage(int sender, int receiver, byte type)
	{
		this.senderID=sender;
		this.receiverID=receiver;
		this.typeMessage=type;	
		this.voieLibre = -1;
		this.TTLMessage = AgentsVANETMessage.TTL_DEPART;
		this.parcoursMessage = new LinkedList<Integer>(); 
	}
	
	/**
	 * Initialise le message en considérant qu'il s'agit d'un message de type DIFFUSION_TRAJET à retransmettre.
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
				res.putInt(iteratorParcours.next());//FIXME peut poser problème de ne pas faire les putInt sur la même ligne que le allocate ? (il semblerait pas, à faire confirmer par un expert)
			
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
	
	/*
	 * Liste des différents accesseurs en lecture des attributs d'un objet message
	 */
	
	public byte getTypeMessage() {
		return this.typeMessage;
	}	
	 
	public int getTTLMessage(){
		return this.TTLMessage;
	}
	
	/*
	 * Liste des accesseurs en écriture des attributs de l'objet message  
	 */	
	
	public void setVoieLibre(int nouvID) {
		this.voieLibre=nouvID;
	}
	
	public void setTTLMessage(int nouvTTL) {
		this.TTLMessage=nouvTTL;
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
