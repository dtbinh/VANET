package simulation.solutions.custom.ACO_MWAC.AntAssistant;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;

import simulation.solutions.custom.ACO_MWAC.Configuration;
import simulation.solutions.custom.ACO_MWAC.Messages.*;

/**
 * la table de routage d'un agent, cette table est utilisée par les fourmis
 * @author Yacine LATTAB & Ouerdia SLAOUTI
 */
public class Routing_table extends Ant_Operations{

	//pour signaler qu'une destination n'existe pas 
	public final int NotExist = -1;
	
	//seuil minimum de pheromone
	public static float min_pheromone = 0.04f;  

	//modelisation de la table de routage 
	private LinkedList<Entry_routing_table> tableau;

	Random nombre_aleatoire;
	public Routing_table (){

		this.tableau = new LinkedList <Entry_routing_table>();
		this.nombre_aleatoire = new Random();
	}


	public float [] get_Destination_table (){
		return this.get_line(1);
	}
	public float [] get_EnergyLevel_table (){
		return this.get_line(2);
	}
	public float [] get_HeuristicValue_table (){
		return this.get_line(3);
	}
	public float [] get_PheromoneBegin_table (){
		return this.get_line(4);
	}
	public float [] get_PheromoneValue_table (){
		return this.get_line(5);
	}
	public float [] get_ProbabilityTransition_table (){
		return this.get_line(6);
	}
	public float [] get_PreventDestination_table (){
		return this.get_line(7);
	}

	/**
	 * pour obtenir une ligne ou colonne de la table de routage
	 * @param number numero de la ligne ou de la colonne
	 * @return un tableau
	 */
	private float [] get_line(int number){

		float [] line = new float [this.tableau.size()];

		for (int i=0; i <  this.tableau.size(); i++){
			switch (number){
			case 1 : line [i] = this.tableau.get(i).get_Destination(); break;
			case 2 : line [i] = this.tableau.get(i).get_EnergyLevel(); break;
			case 3 : line [i] = this.tableau.get(i).get_HeuristicValue(); break;
			case 4 : line [i] = this.tableau.get(i).get_PheromoneBegin(); break;
			case 5 : line [i] = this.tableau.get(i).get_PheromoneValue(); break;
			case 6 : line [i] = this.tableau.get(i).get_ProbabilityTransition(); break;
			case 7 : line [i] = this.tableau.get(i).get_PreventDestination(); break;
			}
		}
		return line;
	}


	public void set_HeuristicValue_table(float [] table){
		this.set_line(3, table);
	}
	public void set_PheromoneValue_table(float [] table){
		this.set_line(5, table);
	}
	public void set_ProbabilityTransition_table(float [] table){
		this.set_line(6, table);
	}
	//--------************--------------**********---------
	public void Reinit_PreventDestination_table(int Best_Hop){
		for (int i=0; i<this.tableau.size(); i++)
			if ((this.tableau.get(i).get_PreventDestination() == 1) && (this.Initial_Pheromone_Calculus(Best_Hop) <= this.tableau.get(i).get_PheromoneBegin())) this.tableau.get(i).set_PreventDestination(0);
	}

	/**
	 * permet d'initialiser une ligne ou colonne de la table de routage
	 * @param number numero de la ligne ou de la colonne à initialiser
	 * @param line la ligne d'initialisation
	 */
	private void set_line(int number, float [] line){

		for (int i=0; i <  this.tableau.size(); i++){
			switch (number){
			case 1 : this.tableau.get(i).set_Destination			((int)line[i]); break;
			case 2 : this.tableau.get(i).set_EnergyLevel			(line[i]); 		break;
			case 3 : this.tableau.get(i).set_HeuristicValue			(line[i]); 		break;
			case 4 : this.tableau.get(i).set_PheromoneBegin			(line[i]); 		break;
			case 5 : this.tableau.get(i).set_PheromoneValue			(line[i]); 		break;
			case 6 : this.tableau.get(i).set_ProbabilityTransition	(line[i]); 		break;
			//case 7 : this.tableau.get(i).set_PreventDestination		(0); break;
			}
		}
	}

	/**
	 * obtenir une entrée de la table de routage à la position "pos"
	 * @param position position de l'entrée
	 * @return l'entrée effective
	 */
	public Entry_routing_table get_Entry_At(int position){                     			  		

		return (Entry_routing_table) this.tableau.get(position);
	}

	/**
	 * pour verifier l'existance d'un agent voisin dans la table de routage
	 * @param dest l'identité de l'agent 
	 * @return renvoi la position de l'entrée dans laquelle il est contenu
	 */
	public Entry_routing_table find_Entry (int dest)
	{
		for(int i=0;i<this.tableau.size();i++){

			if (this.get_Entry_At(i).get_Destination() == dest) return get_Entry_At(i);
			if(this.get_Entry_At(i).get_Destination() > dest) return null;
		}
		return null;
	}

	/**
	 * insert selon un ordre croissant une entrée dans la table de routage
	 * @param Entry la npouvelle entrée à inserer
	 */					
	public void Insert(Entry_routing_table Entry)
	{	
		int j;

		//recherche la bonne position à laquelle l'entrée doit etre inserée
		for(j=0;j<this.tableau.size();j++){
			if (this.get_Entry_At(j).get_Destination() > Entry.get_Destination()) break;
		}

		//l'insertion effective de l'entrée
		this.tableau.add(j, Entry);				
	}

	/**
	 * fait une copie de la table de routage
	 * @return liste des entrée de la table de routage
	 */
	public LinkedList<Entry_routing_table> clone()
	{
		return this.tableau;
	}

	/**
	 * retrancher une quantité d'energie à un agent
	 * @param dest identité de l'agent 
	 * @param Energy la quantité d'energie à lui retrancher
	 */
	public void Update_Estimate_EnergyLevel (int dest, float Energy){

		//si la destination n'existe pas, on l'insere
		if (this.find_Entry(dest) == null) this.Insert(new Entry_routing_table (dest, Configuration.initial_amount_of_energy , min_pheromone));

		//sinon, on lui retranche la quantité d'energie
		else {
			
			//s'il depasse zero, alors le mettre à zero sinon le mettre à sa valeur estimé
			float x = this.find_Entry(dest).get_EnergyLevel() - Energy;

			if (x > 0) 
				this.find_Entry(dest).set_EnergyLevel(x);
			else 
				this.find_Entry(dest).set_EnergyLevel(0);
		}
	}

	/**
	 * mise à jour du niveau d'energie et du sequence Number 
	 * @param fourmi message de mise à jour
	 */
	public void Update_EnergyLevel (ACO_Message_Update msg)
	{	
		//si l'agent qui a envoyé la fourmi n'existe pas au niveau de la table de routage, on l'insere comme nouvelle entrée
		if (this.find_Entry(msg.getSender()) == null) this.Insert(new Entry_routing_table(msg.getSender(), msg.get_Energy_Level(), min_pheromone));

		//sinon, mettre à jour son niveau d'energie au niveau de la table de routage
		else this.find_Entry(msg.getSender()).set_EnergyLevel(msg.get_Energy_Level());
	}

	/**
	 * initialisation de la pheromone
	 * @param fourmi la fourmi d'initialisation de la pheromone
	 * @return vrai ou faux selon que la pheromone soit initialiser ou non 
	 */
	public void Initialise_Entry_Of_RoutingTable (ACO_Message_Initialization tMsg){

		//si l'agent qui a envoyé la fourmi n'existe pas au niveau de la table de routage, on l'insere
		if (this.find_Entry(tMsg.getSender()) == null)			
			this.Insert(new Entry_routing_table(tMsg.getSender(), tMsg.get_EnergyLevel(), super.Initial_Pheromone_Calculus(tMsg.get_HopCount())));

		else  //si la destination existe, on la met à jour
			this.find_Entry(tMsg.getSender()).set_Entry_routing_table(tMsg.getSender(), tMsg.get_EnergyLevel(), super.Initial_Pheromone_Calculus(tMsg.get_HopCount()));
	}


	/**
	 * methode permettant de mettre à jour le champs "pheromone quantity" de la table de routage
	 * @param dest identité d'un capteur voisin
	 * @param length sa nouvelle quantité de pheromone
	 */
	public void renforce_path (int dest, int length){
		if (this.find_Entry (dest)!= null) {
			this.find_Entry (dest).set_PheromoneValue(this.find_Entry (dest).get_PheromoneValue() + super.Additionnal_Pheromone_Calculs(length));
		}
	}


	/**
	 *evaporation des quantités de pheromone
	 */
	public void pheromone_evaporation (){

		this.set_PheromoneValue_table( super.Bad_Evaporation(this.get_PheromoneValue_table()));
	}

	/**
	 * permet d'obtenir le procahin noeud intermediaire à atteindre
	 * @param dest la destination à atteindre
	 * @return le prochain noeud auquel la trame sera diffusée
	 */
	public int get_Next_Agent(Routing_table Table_Entiere, int [] ConcernedAgent, int [] memory)
	{
		this.set_HeuristicValue_table(super.Heuristic_Calculs(Table_Entiere, ConcernedAgent,memory));
		this.set_ProbabilityTransition_table(super.Probability_Calculus(Table_Entiere, ConcernedAgent, memory));	

		float probas = (float) 0;
		float généré = nombre_aleatoire.nextFloat();				

		//chercher l'agent correspondant
		for (int i=0; i < this.tableau.size(); i++){
			probas += this.get_Entry_At(i).get_ProbabilityTransition();
			if(  généré <= probas ) return this.get_Entry_At(i).get_Destination();
		}
		return NotExist; 
	}

	//permet l'affichage de la table en format HTML
	public String toHTML()
	{
		String res="<B> Routing Table</B> ("+this.tableau.size()+")<BR>";
		res+="<TABLE border=1>";
		res+= "<TR><TD>Agent</TD><TD>Energy</TD><TD>Heuristic</TD><TD>PheromoneBegin</TD><TD>PheromoneValue</TD><TD>Probability</TD><TD>Prevent</TD></TR>";
		ListIterator<Entry_routing_table> iter = this.tableau.listIterator();
		while(iter.hasNext()) res+=iter.next().toHTML();
		res+="</TABLE>";
		return res;
	}

}
