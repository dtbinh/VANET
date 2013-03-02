package simulation.solutions.custom.VANETNetwork.Messages;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import simulation.messages.Message;
import simulation.solutions.custom.VANETNetwork.Croisement;

/**
 * Un message est un objet stockant des informations ; lors de l'envoi, ces informations seront stock�es dans un int[] normalis�	
 * Cette classe g�re les messages li�s � VANET
 */
@SuppressWarnings("serial")
public class AgentsVANETMessage extends Message{
	
	//TODO (DONE) verifier si je dis pas de conner*es. / Oui, t'as dit pas mal de conneries
	//TODO corriger les conneries sus-mentionn�es
	
	/**
	 * Constantes pouvant �tre stock�es dans l'attribut typeMessage.
	 * Indiquent le "genre de message"
	 */
	public static final byte DIRE_QUI_PEUT_PASSER=1;
	public static final byte DIFFUSION_TRAJET=2;
	public static final byte INDIQUER_DIRECTION=3;// sert principalement � obtenir une r�f�rence vers le croisement indicateur au moment opportun, afin de pouvoir d�cider o� aller ensuite
	
	/**
	 * Attribut codant le type de message, celui-ci stocke les constantes ci-dessus
	 */
	private byte typeMessage;
	
	private int senderID;
	private int receiverID;
	
	
	// TODO: Quand j'ai le temps optimiser ce foutu code (par exemple, faire des sous-classes de messageVANET qui h�ritent, et qui seront les seules � poss�der leurs attributs particuliers)
	
/* ========== Attributs en rapport avec DIFFUSION_TRAJET ==========*/	
	/**
	 * Liste des id des croisements du parcours. Utilis� par DIFFUSION_TRAJET.
	 */
	public List<Integer>parcoursMessage;
	
	/**
	 * Time to live : nombre de sauts que le message est encore autoris� � faire. D�cr�ment� � chaque saut, on v�rifiera si TTL > 0 avant de retransmettre.
	 */
	private int TTLMessage;
	
	/**
	 * Nombre de sauts qu'un message est autoris� � faire � sa cr�ation. Constante utilis�e par les types de message qui se relaient (ex : DIFFUSION_TRAJET)
	 */
	public static final byte TTL_DEPART=4;
	
	
/* ========== Attributs en rapport avec DIRE_QUI_PEUT_PASSER ==========*/	
	/**
	 * Utilis� dans les messages de feux rouges.
	 * Contient l'id de la voie (du Croisement qu'on trouve au bout de cette voie) qui est au vert. Une et une seule voie par Croisement peut �tre au vert.
	 */
	private int voieLibre;
	
	
	/**
	 * Constructeur de message g�n�rique. Initialise les attributs � des valeurs nulles, sauf les 3 plus importants, qui sont donn�s.
	 * Pour initialiser plus pr�cisement un message, utilisez la m�thode init qui correspond au type souhait�, juste apr�s le new.
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
	 * Initialise le message en consid�rant qu'il s'agit d'un message de type DIFFUSION_TRAJET � retransmettre.
	 * On en fait une copie de msg, et on rajoute croisARajouter � this.parcoursMessage (en d�but de liste)
	 * @param msg le message de type DIFFUSION_TRAJET re�u par la voiture, qui servira de mod�le pour initialiser this.
	 * @param croisARajouter si msg.parcoursMessage vaut {B,C,D} et que la voiture appelante est sur la voie AB (croisARajouter vaut donc A), alors
	 * this.parcoursMessage vaudra {A,B,C,D} � la fin de cette m�thode (la liste utilise le sens "d'o�, vers o�").
	 */
	public void initTrajetMessage(AgentsVANETMessage msg, Croisement croisARajouter) {
		if (this.getTypeMessage() == AgentsVANETMessage.DIFFUSION_TRAJET)
		{
			this.parcoursMessage = new LinkedList<Integer>(); // On r�initialise (des fois qu'un idiot ait rajout� � la main des Croisements entre le new et cette m�thode)
			this.parcoursMessage.add(croisARajouter.getUserId());
			this.parcoursMessage.addAll(msg.parcoursMessage);
			
			this.setTTLMessage(msg.getTTLMessage() - 1); // le m�me TTL qu'avant, -1 car on a fait un saut.
		}
		else
			System.out.println("ERREUR : on cherche � initialiser un message de type DIFFUSION_TRAJET qui n'a pas �t� d�clar� comme tel");
	}
	
	/**
	 * M�thode permettant de transcrire les informations du Message 
	 * dans un tableau de Byte
	 * 
	 * Fonction extr�mement importante, elle code en tableau de byte les attributs de l'objet message en vue d'une exp�dition.
	 * 
	 * NOTE: L'ordre est important et va induire un d�codage lors de l'extraction des informations dans cet ordre pr�cis
	 * On mettra toujours en premier le type de message afin de faciliter le d�codage
	 * @return le message sous forme de byte[]
	 */
	public byte[] toByteSequence()
	{
		//TODO : Trouver un moyen d'avoir une taille adapt�e pour le allocate
		if(this.typeMessage==DIRE_QUI_PEUT_PASSER)
			return ByteBuffer.allocate(50).put(this.typeMessage).putInt(this.senderID).putInt(receiverID).putInt(this.voieLibre).array();

		else if (this.typeMessage==DIFFUSION_TRAJET){
			ByteBuffer res = ByteBuffer.allocate(100).put(this.typeMessage).putInt(this.senderID).putInt(receiverID).putInt(this.TTLMessage);
				
			Iterator <Integer> iteratorParcours = this.parcoursMessage.iterator();
			
			res.putInt(this.parcoursMessage.size());
			while (iteratorParcours.hasNext())
				res.putInt(iteratorParcours.next());//FIXME peut poser probl�me de ne pas faire les putInt sur la m�me ligne que le allocate ? (il semblerait pas, � faire confirmer par un expert)
			
			return res.array();
		}
		else if (this.typeMessage==INDIQUER_DIRECTION) 
			return ByteBuffer.allocate(50).put(this.typeMessage).putInt(this.senderID).putInt(receiverID).array();
		
		//else if == ...
		else
		{
			System.out.println("PROBLEME : Un message ne poss�de pas un typeMessage coh�rent");
			return null;
		}
	}
	
	/*
	 * Liste des diff�rents accesseurs en lecture des attributs d'un objet message
	 */
	
	public byte getTypeMessage() {
		return this.typeMessage;
	}	
	 
	public int getTTLMessage(){
		return this.TTLMessage;
	}
	
	/*
	 * Liste des accesseurs en �criture des attributs de l'objet message  
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
