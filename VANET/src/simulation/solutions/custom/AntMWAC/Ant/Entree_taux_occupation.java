package simulation.solutions.custom.AntMWAC.Ant;


/**
 * la structure d'un entree de la table des taux d'occupation des liens 
 *
 */
public class Entree_taux_occupation {
	private int Destination;
	private int  cpt ;   //le taux d'occupation 
	
	
	/**************************************************************/
	public   Entree_taux_occupation (int dest, int taux){
		 
		this.Destination=dest;
		this.cpt =taux ;
	}
	
	
	/**************************************************************/
	public   Entree_taux_occupation (int dest){
		 
		this.Destination=dest;
		this.cpt = 0 ;
	}
	/**************************************************************/
	public  int get_destination ()
	{
		return this.Destination ;
	}
	/**************************************************************/
	public int get_taux ()
	{
		return this.cpt ;
		
	}
	/**************************************************************/
	public void set_destination (int  dest){
		this.Destination =dest;
		
	}
	
	/**************************************************************/
	public void set_taux(int cpt ){
		this.cpt = cpt ;
	}
	/***************************************************************/
	public String toHTML()
	{
		return "<TR><TD>"+this.get_destination()+"</TD><TD>"
		+this.get_taux()+"</TD><TD></TR>";
	}
	
}

