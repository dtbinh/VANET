package simulation.solutions.custom.DSDV;
import java.util.LinkedList;

import simulation.solutions.custom.DSDV.Messages.DSDV_Message_Introduction;
import simulation.solutions.custom.DSDV.Messages.DSDV_Message_Update;


/**
 * The routing table which any sensor contain
 * @author Yacine LATTAB & Ouerdia SLAOUTI
 */

public class Routing_table {

	//la table de routage : liste d'Entry
	private LinkedList<Entry_routing_table> tableau;

	//pour signaler qu'une destination donn�e n'exist pas dans la table de routage (ne peut etre atteinte)
	public final int NotExist=-1;
	
	//derniere mise � jour de la table de routage
	private Entry_routing_table last_Entry;
	
	
	/**
	 * initialisation de la table de routage
	 * @param destination la destination pouvant etre atteinte par l'agent DSDV
	 * @param hops le nombre de sauts separant la destination de l'agent DSDV
	 * @param next le prochain noeud intermediaire entre l'agent et la destination
	 * @param SequenceNumber le numero de sequence de la destination
	 */
	public Routing_table()
	{
		this.tableau=new LinkedList<Entry_routing_table>();
	}
	
	/**
	 * overwrite Methode qui permet d'initiliaser la table de routage � partir d'un message d'Introduction
	 * @param msg Introduction Message
	 */
	public void Update (DSDV_Message_Introduction msg)
	{	
		this.Update(new DSDV_Message_Update(msg.getSender(), msg.getReceiver(), new Entry_routing_table (msg.getSender(), 0, msg.getSender(), msg.get_SN())));
	}
	
	/**
	 * methode permettant de mettre � jour la table de routage
	 * et de positionner le champs last_entry pour une diffusion de la derniere entr�e mise � jour
	 * @param msg DSDV_Message_Update
	 */
	public void Update (DSDV_Message_Update msg)
	{	
		//initialisation de mise � jour de l'entr�e de la table de routage
		this.last_Entry = new Entry_routing_table(msg.get_Entry().get_Destination(), msg.get_Entry().get_Hops()+1, msg.getSender(), msg.get_Entry().get_SequenceNumber());
		
		//i re�oit la position de la destination dans la table de routage, -1 sinon
		int i= search(this.last_Entry.get_Destination());
		
		//si la destination n'y est pas, on insere cette entr�e
		if (i==NotExist) {
			this.Insert(this.last_Entry); 
		} 
		else {
			//si la destination y est mais, avec un numero de sequence inferieur
			if (this.get_last_Entry().get_SequenceNumber() > this.tableau.get(i).get_SequenceNumber()){
				//mise � jour, on remplace l'ancienne entr�e par la nouvelle
				this.tableau.set(i, this.last_Entry); 
			}
			else{
				//si la destination y est mais, avec un numero de sequence identique
				if (this.get_last_Entry().get_SequenceNumber() == this.tableau.get(i).get_SequenceNumber()){
					//si nombre de saut inferieur
					if(this.last_Entry.get_Hops() < this.tableau.get(i).get_Hops()){ 
						this.tableau.set(i, this.last_Entry);  
					}
					//sinon, le nombre de sauts suprieur ou egal, on ne fait rien
					else this.last_Entry=null; 
				}
				//sinon, numero de sequence inferieur, on ne fait rien 
				else this.last_Entry=null; 
			}
		}
	}

	/**
	 * methode permettant de chercher une destination donn�e dans la table de routage
	 * @param destination la destination desir�e
	 * @return renvoie sa position si elle existe, -1 sinon
	 */
	public int search(int destination)
	{
		for(int i=0;i<this.tableau.size();i++){
			//si la destination a �t� trouv�e, on retourne sa position
			if (this.tableau.get(i).get_Destination()==destination) return i;
			
			//sinon, si la destination est depass�e, elle n'existe pas, retourner -1
			else if (this.tableau.get(i).get_Destination() > destination) return -1;
		}
		//si la destination n'y est pas, on retourne -1
		return NotExist;
	}
	
	/**
	 * 	insert selon un ordre croissant une entr�e dans la table de routage
	 * @param Entry l'entr�e de la table de routage
	 */					
	private void Insert(Entry_routing_table Entry)
	{	
		int j;
		//recherche de la bonne position � laquelle l'entr�e doit etre inser�e
		for(j=0;j<this.tableau.size();j++){
			if (this.tableau.get(j).get_Destination() > Entry.get_Destination()) break;
		}
		//l'insertion effective de l'entr�e
		this.tableau.add(j, Entry);				
	}
	
	/**
	 * pour recuperer la derniere entr�e qui doit etre diffus�
	 * @return l'entr�e
	 */ 
	public Entry_routing_table get_last_Entry(){
		return last_Entry;
	}
	
	/**
	 * pour obtenir le procahin noeud intermediaire entre la source et destination
	 * @param dest la destination � atteindre
	 * @return le prochain noeud auquel la trame sera diffus�e
	 */
	public int get_Next_relay(int dest)
	{
		int x = search(dest);
		//si la destination existe dans la table de routage
		if (x!= this.NotExist) return tableau.get(x).get_Next();
		//sinon, la destination n'existe pas dans la table de routage
		else return this.NotExist;
	}
	/**
	 * methode qui verifie l'eventuelle defaillance d'un noeud voisin � l'agent DSDV
	 * si son sequence Number n'a pas �t� increment� depuis longtemps
	 * l'agent le considere defaillant ou hors de sa port� (SN +1 : impair)
	 * @param id identit� de l'agent qui va consulter sa table de routage 
	 */
	public void verification_defaillance_voisins(int id){
		
		//unit� d'incrementation du noeud auquel la table de routage apparient
		int mon_increment = this.tableau.get(this.search(id)).get_SequenceNumber() - 2*this.tableau.get(this.search(id)).get_Destination();
		
		////unit� d'incrementation d'un noeud de la table de routage
		int son_increment;
		
		int d,h,n,s;
		//parcourir la table de routage
		for(int i=0;i<this.tableau.size();i++){
			if(i != id){
				d = this.tableau.get(i).get_Destination();
				h = this.tableau.get(i).get_Hops();
				n = this.tableau.get(i).get_Next();
				s = this.tableau.get(i).get_SequenceNumber();
			
				son_increment = s -(d*2);
			
				//si le sequence number des destinataires n'ont pas �t� mis � jour depuis longtemps
				if ( (son_increment < mon_increment) && (s%2 == 0)) this.tableau.set(i, new Entry_routing_table(d,h,n,s+1));
				else 
					//si je suis tardivement cr�e, je mets � jour mon increment et mon sequence number
					if ( (son_increment > mon_increment +2) && (s%2 == 0)){
						mon_increment = son_increment;
						this.tableau.set(this.search(id), new Entry_routing_table(id,0,id,(id*2)+mon_increment));
					}
			}
		}
	}
	/**
	 * si le capteur a �t� tardivement cr�e, son sequence number doit etre mis � jour 
	 * @param id identit� de l'agent
	 * @return l'eventuel nouveau Sequence Number
	 */
	public int eventuelle_mise_�_jour(int id){
		return this.tableau.get(this.search(id)).get_SequenceNumber(); 
	}
		
	/**
	 * fait une copie de la table de routage
	 * @return liste des entr�e de la table de routage
	 */
	public LinkedList<Entry_routing_table> clone()
	{
		return this.tableau;
	}

}

