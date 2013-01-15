package simulation.solutions.custom.ACO_MWAC.AntAssistant;

/**
* la structure de l'entrée de la table de routage
* @author Yacine LATTAB & Ouerdia SLAOUTI
*/

public class Entry_routing_table {
		
	//identité de la destination
	private int Destination;
		
	//son niveau d'energie estimé
	private float EnergyLevel;
	
	//la valeur heuristique 
	private float HeuristicValue;
	
	//la pheromone initiale selon le nombre de sauts
	private float PheromoneBegin;
	
	//la quatité de pheromone 
	private float PheromoneValue;
	
	//la probabilité de transition
	private float ProbabilityTransition;
	
	//la valeur heuristique 
	private int PreventDestination;
	
	
	/**
	 * permet d'initialiser une entrée de la table de routage
	 * @param dest l'identité de la destination 
	 * @param SN son Sequence Number
	 * @param energy_level son niveau d'enrgie
	 * @param pheromone_quantity la quatité de pheromone
	 */
	public Entry_routing_table (int dest, float energy_level, float PheromoneBegin){
		this.Destination	 		= 				dest;
		this.EnergyLevel 			= 	this.round(energy_level);
		this.HeuristicValue 		= 				0;
		this.PheromoneBegin			=	this.round(PheromoneBegin);
		this.PheromoneValue 		= 	this.PheromoneBegin;
		this.ProbabilityTransition 	= 				0;
		this.PreventDestination		= 				0;
	}
	
	
	public void set_Entry_routing_table (int dest, float energy_level, float PheromoneBegin){
		this.Destination	 		= 				dest;
		this.EnergyLevel 			= 	this.round(energy_level);
		this.HeuristicValue 		= 				0;
		this.PheromoneBegin			=	this.round(PheromoneBegin);
		this.PheromoneValue 		= 	this.PheromoneBegin;
		this.ProbabilityTransition 	= 				0;
		this.PreventDestination		= 				0;
	}
	
	
	
	
	public int get_Destination (){
		return this.Destination;
	}
	public float get_EnergyLevel(){
		return this.EnergyLevel;
	}
	public float get_HeuristicValue(){
		return this.HeuristicValue;
	}	
	public float get_PheromoneBegin(){
		return this.PheromoneBegin;
	}
	public float get_PheromoneValue(){
		return this.PheromoneValue;
	}
	public float get_ProbabilityTransition(){
		return this.ProbabilityTransition;
	}
	public int get_PreventDestination(){
		return this.PreventDestination;
	}
	
	
	public void set_Destination(int Destination){
		this.Destination = Destination;
	}
	public void set_EnergyLevel (float energy_level){
		this.EnergyLevel = this.round(energy_level);
	}
	public void set_HeuristicValue(float heuristic_value){
		this.HeuristicValue = this.round (heuristic_value);
	}	
	public void set_PheromoneBegin(float PheromoneBegin){
		this.PheromoneBegin = this.round (PheromoneBegin);
	}
	public void set_PheromoneValue(float PheromoneValue){
		this.PheromoneValue = this.round(PheromoneValue);
	}
	public void set_ProbabilityTransition(float ProbabilityTransition){
		this.ProbabilityTransition = this.round(ProbabilityTransition);
	}
	public void set_PreventDestination (int i){
		 this.PreventDestination = i;
	}
	
	//affichage en HTML
	public String toHTML()
	{
		return "<TR><TD>"+this.get_Destination()+"</TD><TD>"+this.get_EnergyLevel()+"</TD><TD>"+this.get_HeuristicValue()+"</TD><TD>"+this.get_PheromoneBegin()+"</TD><TD>"+this.get_PheromoneValue()+"</TD><TD>"+this.get_ProbabilityTransition()+"</TD><TD>"+this.get_PreventDestination()+"</TD></TR>";
	}
	
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

