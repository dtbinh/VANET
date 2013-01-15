package simulation.solutions.custom.DSDV;
import java.nio.ByteBuffer;

/**
 * l'entrée de la table de routage devant etre transportée par le message DSDV Update 
 * @author Yacine LATTAB & Ouerdia SLAOUTI
 */
public class Entry_routing_table {

	//la destination pouvant etre atteinte par l'agent
	private int destination;

	// le nombre de sauts separant la source de la destination 
	private int hops;

	//le prochain noeud entre la source et la destination 
	private int next;
	
	//le Sequence Number de la destination
	private int SequenceNumber;


	/**
	 * constructeur initialisation d'une entrée de la table de routage
	 * @param destination la destination pouvant etre atteinte par l'agent
	 * @param hops le nombre de sauts separant la destination de l'agent
	 * @param next le prochain noeud intermediaire entre l'agent et la destination
	 * @param SequenceNumber le numero de sequence de la destination
	 */
	public Entry_routing_table(int destination, int hops, int next, int SequenceNumber)
	{
		this.destination=destination;
		this.hops=hops;
		this.next=next;
		this.SequenceNumber=SequenceNumber;
	}
	
	/**
	 * constructeur initialisation d'une entrée de la table de routage à partir d'une suite de Bytes
	 * @param data suite de bytes
	 */
	//initialisation d'une entrée de la table de routage à partir d'un tableau de bytes
	public Entry_routing_table(byte[] data) 
	{
		ByteBuffer buf=ByteBuffer.wrap(data);
		this.destination=buf.getInt();
		this.hops=buf.getInt();
		this.next=buf.getInt();
		this.SequenceNumber=buf.getInt();
	}

	
	/**
	 * permet de consulter l'attribut Destination de l'entrée
	 * @return la destination
	 */
	public int get_Destination()
	{
		return this.destination;
	}

	/**
	 * permet de savoir le nombre de sauts separant le destinataire du destinateur
	 * @return le nombre de sauts
	 */
	public int get_Hops() {
		return this.hops;
	}
	
	/**
	 * permet de savoir le prochain Noeud intermediaire contenu dans l'entrée
	 * @return le prochain noeud
	 */
	public int get_Next() {
		return this.next;
	}
	
	/**
	 * permet d'obtenir l'attribut sequence Number de l'entrée
	 * @return le Sequence Number
	 */
	public int get_SequenceNumber() {
		return this.SequenceNumber;
	}
	
	/**
	 * permet de mettre à jour le sequence Number relatif à cette entrée
	 * @param SN Sequence Number
	 */
	public void set_SequenceNumber(int SN) {
		 this.SequenceNumber = SN;
	}
	
	/**
	 * retourne la representation du message en une suite de bytes  
	 * @return representation en bytes
	 **/
	public byte[] toByteSequence() {
	
		return ByteBuffer.allocate(16).putInt(this.destination).putInt(this.hops).putInt(this.next).putInt(this.SequenceNumber).array();
	}
	
	/**
	 * retourne la representation du message en String  
	 * @return representation en String 
	 **/
	public String toString(){
		return this.destination+","+this.hops+","+this.next+","+this.SequenceNumber;
	}

}
