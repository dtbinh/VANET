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

	//pour signaler qu'une destination donnée n'exist pas dans la table de routage (ne peut etre atteinte)
	public final int NotExist=-1;
	
	//derniere mise à jour de la table de routage
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
	 * overwrite Methode qui permet d'initiliaser la table de routage à partir d'un message d'Introduction
	 * @param msg Introduction Message
	 */
	public void Update (DSDV_Message_Introduction msg)
	{	
		this.Update(new DSDV_Message_Update(msg.getSender(), msg.getReceiver(), new Entry_routing_table (msg.getSender(), 0, msg.getSender(), msg.get_SN())));
	}
	
	/**
	 * methode permettant de mettre à jour la table de routage
	 * et de positionner le champs last_entry pour une diffusion de la derniere entrée mise à jour
	 * @param msg DSDV_Message_Update
	 */
	public void Update (DSDV_Message_Update msg)
	{	
		//initialisation de mise à jour de l'entrée de la table de routage
		this.last_Entry = new Entry_routing_table(msg.get_Entry().get_Destination(), msg.get_Entry().get_Hops()+1, msg.getSender(), msg.get_Entry().get_SequenceNumber());
		
		//i reçoit la position de la destination dans la table de routage, -1 sinon
		int i= search(this.last_Entry.get_Destination());
		
		//si la destination n'y est pas, on insere cette entrée
		if (i==NotExist) {
			this.Insert(this.last_Entry); 
		} 
		else {
			//si la destination y est mais, avec un numero de sequence inferieur
			if (this.get_last_Entry().get_SequenceNumber() > this.tableau.get(i).get_SequenceNumber()){
				//mise à jour, on remplace l'ancienne entrée par la nouvelle
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
	 * methode permettant de chercher une destination donnée dans la table de routage
	 * @param destination la destination desirée
	 * @return renvoie sa position si elle existe, -1 sinon
	 */
	public int search(int destination)
	{
		for(int i=0;i<this.tableau.size();i++){
			//si la destination a été trouvée, on retourne sa position
			if (this.tableau.get(i).get_Destination()==destination) return i;
			
			//sinon, si la destination est depassée, elle n'existe pas, retourner -1
			else if (this.tableau.get(i).get_Destination() > destination) return -1;
		}
		//si la destination n'y est pas, on retourne -1
		return NotExist;
	}
	
	/**
	 * 	insert selon un ordre croissant une entrée dans la table de routage
	 * @param Entry l'entrée de la table de routage
	 */					
	private void Insert(Entry_routing_table Entry)
	{	
		int j;
		//recherche de la bonne position à laquelle l'entrée doit etre inserée
		for(j=0;j<this.tableau.size();j++){
			if (this.tableau.get(j).get_Destination() > Entry.get_Destination()) break;
		}
		//l'insertion effective de l'entrée
		this.tableau.add(j, Entry);				
	}
	
	/**
	 * pour recuperer la derniere entrée qui doit etre diffusé
	 * @return l'entrée
	 */ 
	public Entry_routing_table get_last_Entry(){
		return last_Entry;
	}
	
	/**
	 * pour obtenir le procahin noeud intermediaire entre la source et destination
	 * @param dest la destination à atteindre
	 * @return le prochain noeud auquel la trame sera diffusée
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
	 * methode qui verifie l'eventuelle defaillance d'un noeud voisin à l'agent DSDV
	 * si son sequence Number n'a pas été incrementé depuis longtemps
	 * l'agent le considere defaillant ou hors de sa porté (SN +1 : impair)
	 * @param id identité de l'agent qui va consulter sa table de routage 
	 */
	public void verification_defaillance_voisins(int id){
		
		//unité d'incrementation du noeud auquel la table de routage apparient
		int mon_increment = this.tableau.get(this.search(id)).get_SequenceNumber() - 2*this.tableau.get(this.search(id)).get_Destination();
		
		////unité d'incrementation d'un noeud de la table de routage
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
			
				//si le sequence number des destinataires n'ont pas été mis à jour depuis longtemps
				if ( (son_increment < mon_increment) && (s%2 == 0)) this.tableau.set(i, new Entry_routing_table(d,h,n,s+1));
				else 
					//si je suis tardivement crée, je mets à jour mon increment et mon sequence number
					if ( (son_increment > mon_increment +2) && (s%2 == 0)){
						mon_increment = son_increment;
						this.tableau.set(this.search(id), new Entry_routing_table(id,0,id,(id*2)+mon_increment));
					}
			}
		}
	}
	/**
	 * si le capteur a été tardivement crée, son sequence number doit etre mis à jour 
	 * @param id identité de l'agent
	 * @return l'eventuel nouveau Sequence Number
	 */
	public int eventuelle_mise_à_jour(int id){
		return this.tableau.get(this.search(id)).get_SequenceNumber(); 
	}
		
	/**
	 * fait une copie de la table de routage
	 * @return liste des entrée de la table de routage
	 */
	public LinkedList<Entry_routing_table> clone()
	{
		return this.tableau;
	}

}

