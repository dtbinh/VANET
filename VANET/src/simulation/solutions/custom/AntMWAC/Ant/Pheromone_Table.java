package simulation.solutions.custom.AntMWAC.Ant;


import java.util.LinkedList;
import java.util.ListIterator;



/** la table de pheromone 
 *
 */
public class Pheromone_Table {


	public final int NotExist = -1;
	
	public static float min_pheromone = 0.01f;  

	public  LinkedList<Entry_pheromone_table> tableau;

	/************************************************************************************/
	public Pheromone_Table (){

		this.tableau = new LinkedList <Entry_pheromone_table>();
		
	}

	/************************************************************************************/
	public int mean_hopcount(Pheromone_Table table)
	{
		int somme=0;
		for (int i=0;i<table.tableau.size();i++)
		{
			somme+=table.get_Entry_At(i).get_hopcount();
		}
	if (table.tableau.size()!=0){
		System.out.println("le nombre de saut est "+somme/table.tableau.size());
		
		return somme/table.tableau.size();}
	else return 0;
		
	}
	/************************************************************************************/
	public Entry_pheromone_table get_Entry_At(int position){                     			  		

		return  this.tableau.get(position);
	}

	/************************************************************************************/
	
	public Entry_pheromone_table find_Entry (int dest)
	{
		for(int i=0;i<this.tableau.size();i++){

			if (this.get_Entry_At(i).get_Destination() == dest) return this.get_Entry_At(i);
			if(this.get_Entry_At(i).get_Destination() > dest) return null;
		}
		return null;
	}

	/************************************************************************************/
	/**
	 * insert selon un ordre croissant une entrée dans la table de pheromone 
	 * @param Entry la nouvelle entrée à inserer
	 */
	
	public void Insert(Entry_pheromone_table Entry)
	{	
		int j =0;

		Entry_pheromone_table entree = this.find_Entry(Entry.get_Destination());
		if (entree !=null && entree.get_hopcount()> Entry.get_hopcount() ) 
					
		{      
			for(j=0;j<this.tableau.size();j++){
				if (this.get_Entry_At(j).get_Destination() == entree.get_Destination()) break;
			}
			this.tableau.remove(entree);	
			this.tableau.add(j,Entry);
		}
		else { if (entree ==null)
			for(j=0;j<this.tableau.size();j++){
			if (this.get_Entry_At(j).get_Destination() > Entry.get_Destination()) break;
		    }
		
		this.tableau.add(j, Entry);		
		}
				
	}

	/************************************************************************************/
	
	public LinkedList<Entry_pheromone_table> clone()
	{
		return this.tableau;
	}

	/************************************************************************************/
	
	public String toHTML()
	{
		String res="<B>Table de pheromone</B> ("+this.tableau.size()+")<BR>";
		res+="<TABLE border=1>";
		res+= "<TR><TD>Agent</TD><TD>Energy</TD><TD>PheromoneValue</TD><TD>Prevent</TD><TD>HopCount</TD></TR>";
		ListIterator<Entry_pheromone_table> iter = this.tableau.listIterator();
		while(iter.hasNext()) res+=iter.next().toHTML();
		res+="</TABLE>";
		return res;
	}

}
















