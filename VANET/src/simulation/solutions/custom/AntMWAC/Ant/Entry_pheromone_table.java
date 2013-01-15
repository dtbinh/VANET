package simulation.solutions.custom.AntMWAC.Ant;

/**
* la structure d' une entrée de la table de pheromone 
* 
*/

public class Entry_pheromone_table {
		
	
	private int Destination;
		
	private float EnergyLevel;
	
	private float PheromoneValue;
	
	private int PreventDestination; 

	private  int hopcount ;
	

	/*****************************************************************************************/

	public Entry_pheromone_table (int dest, float energy_level, float PheromonValue ,int hops){
		this.Destination	 		= 				dest;
		this.EnergyLevel 			= 	this.round(energy_level);
		this.PheromoneValue 		=      PheromonValue;
		this.PreventDestination		= 				0;
		this.hopcount               =  hops;
		
	}
	
	
	/********************************************************************************************/
	public Entry_pheromone_table (int dest, float energy_level,int hops ){
		this.Destination	 		= 				dest;
		this.EnergyLevel 			= 	this.round(energy_level);
		this.PheromoneValue 		=      0.01f;
		this.PreventDestination		= 	0;
		this.hopcount               =   hops ;
		
	}
	/*********************************************************************************************/
	public Entry_pheromone_table (int dest, float energy_level, float PheromonValue){
		this.Destination	 		= 				dest;
		this.EnergyLevel 			= 	this.round(energy_level);
		this.PheromoneValue 		=      PheromonValue;
		this.PreventDestination		= 				0;
		this.hopcount               =  0;
		
	}
	
	
	/**********************************************************************************************/
	
	public int get_Destination (){
		return this.Destination;
	}
	
	/**********************************************************************************************/
	public float get_EnergyLevel(){
		return this.EnergyLevel;
	}
	
	/**********************************************************************************************/
	public float get_PheromoneValue(){
		return this.PheromoneValue;
	}
	/**********************************************************************************************/
	public int get_PreventDestination(){
		return this.PreventDestination;
	}
	/**********************************************************************************************/
	public int get_hopcount(){
		return this.hopcount;
	}
	
	 
   /***********************************************************************************************/
	public void set_Destination(int Destination){
		this.Destination = Destination;
	}
	
	/**********************************************************************************************/
	public void set_EnergyLevel (float energy_level){
		this.EnergyLevel = this.round(energy_level);
	}
	
	/**********************************************************************************************/
	public void set_PheromoneValue(float PheromoneValue){
		this.PheromoneValue = this.round(PheromoneValue);
	}
	/**********************************************************************************************/
	
	public void set_PreventDestination (int i){
		 this.PreventDestination = i;
	}
	
	/**********************************************************************************************/
	public void set_hopcount (int i){
		 this.hopcount= i;
	}
	/*************************************************************************************************/
	
	public String toHTML()
	{
		return "<TR><TD>"+this.get_Destination()+"</TD><TD>"
		+this.get_EnergyLevel()+"</TD><TD>"
		+this.get_PheromoneValue()+"</TD><TD>"+this.get_PreventDestination()+"</TD><TD>"+this.get_hopcount()
		+"</TD></TR>";
	}
	
	
	/*************************************************************************************************/
	
	/**
	 * permet d'arrondir à trois chiffres aprés le virgule
	 * @param value la valeur à arrondir
	 * @return la valeur arrondie
	 */
	public float round(float value){
		int i = (int) (value * 10000 + (float)0.5);
		return (float) i/10000;
	}
	
}

