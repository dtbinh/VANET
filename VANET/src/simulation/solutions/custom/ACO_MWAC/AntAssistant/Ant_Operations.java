package simulation.solutions.custom.ACO_MWAC.AntAssistant;

import simulation.solutions.custom.ACO_MWAC.*;

/**
 * Ant Operations (calcul des probas de transistion, heuristiques, evaporation ... etc)
 * @author Yacine LATTAB & Ouerdia SLAOUTI
 */

public class Ant_Operations {
	
	/**
	 * calcul des probas de transition
	 * @param TableEntiere table de routage
	 * @param memory route prise par la fourmi (destination interdite)
	 * @return la nouvelle table de routage
	 */
	public synchronized float [] Probability_Calculus (Routing_table TableEntiere, int [] ConcernedAgent, int [] memory){
		
		float [] Destination_table 			= TableEntiere.get_Destination_table();
		float [] heuristic_table 			= TableEntiere.get_HeuristicValue_table();
		float [] PheromoneValue_table 		= TableEntiere.get_PheromoneValue_table();
		float [] ProbabilityTransition_table = TableEntiere.get_ProbabilityTransition_table();
		float [] PreventDestination_table 	= TableEntiere.get_PreventDestination_table();		
		
		// total numerators 
		float somme=0;
		
		for (int i=0; i < ProbabilityTransition_table.length; i++){
			if ((Ant_Route_Assistant.contains(ConcernedAgent, (int)Destination_table[i])==true)&&(Ant_Route_Assistant.contains(memory, (int)Destination_table[i])==false) && (PreventDestination_table [i]== 0)){
				ProbabilityTransition_table [i] = ((float) (Math.pow(PheromoneValue_table[i], Configuration.alpha) * Math.pow(heuristic_table[i], Configuration.beta)));
				somme += ProbabilityTransition_table [i];
			}
			else ProbabilityTransition_table [i] = 0; 
		}
		
		//Numerators / denominator
		for (int i=0; i < ProbabilityTransition_table.length; i++) ProbabilityTransition_table[i] = ProbabilityTransition_table [i] / somme;
				
		return ProbabilityTransition_table;
	}
	
	
	/**
	 * calcul des valeurs heuristiques
	 * @param EnergyLevel_table table des energies residuelles
	 * @return table de l'heuristique
	 */
	public synchronized float [] Heuristic_Calculs (Routing_table TableEntiere, int [] ConcernedAgent, int [] memory){
		 
		float [] Destination_table 			= TableEntiere.get_Destination_table();
		float [] EnergyLevel_table			= TableEntiere.get_EnergyLevel_table();
		float [] heuristic_table 			= TableEntiere.get_HeuristicValue_table();
		float [] PreventDestination_table 	= TableEntiere.get_PreventDestination_table();	
		
		
		//Total Energy Level
		float somme=0;
			
		for (int i=0; i < EnergyLevel_table.length; i++) 
			if ((Ant_Route_Assistant.contains(ConcernedAgent, (int)Destination_table[i])==true)&&(Ant_Route_Assistant.contains(memory, (int)Destination_table[i])==false) && (PreventDestination_table [i]== 0))
				somme += EnergyLevel_table [i];
			
		for (int i=0; i < EnergyLevel_table.length; i++) 
			if ((Ant_Route_Assistant.contains(ConcernedAgent, (int)Destination_table[i])==true)&&(Ant_Route_Assistant.contains(memory, (int)Destination_table[i])==false) && (PreventDestination_table [i]== 0))
					heuristic_table [i] = EnergyLevel_table [i] / somme;
			else heuristic_table [i] = 0;
		
		return heuristic_table;
	}
	
	
	/**
	 * depot de la phéromone
	 * @param path_length la longueur du chemin
	 * @return quantité de phéromone addionnelle  
	 */
	public synchronized float Additionnal_Pheromone_Calculs (int path_length){
		
		//retourne la valeur calculée
		return (Configuration.Q/path_length);
	}
	
	
	/**
	 * pour initialiser, selon le nombre de sauts, les quantités de pheromone
	 * @param hop le nombre de sauts
	 * @return pheromone initiale
	 */
	public synchronized float Initial_Pheromone_Calculus (int hop){
		
		return  (float) 1 / (hop+1);  //(((hop-Best_Hop)+1)*Configuration.Hop_Redution) ;      
	}
	
	/**
	 * evaporation des quantités de pheromone (peut depasser la quantité initiale)
	 * @return retourne la nouvelle table de routage
	 */
	public synchronized float []  Bad_Evaporation (float [] PheromoneValue_table ){
			
		for (int i=0; i < PheromoneValue_table.length; i++){
			
			PheromoneValue_table [i] = (1-Configuration.roe) * PheromoneValue_table [i];
		}
		return PheromoneValue_table;
	}
	
	
	/**
	 * evaporation des quantités de pheromone (sans depasser la quantité initiale)
	 * @return retourne la nouvelle table de routage
	 */
	public synchronized float [] Good_Evaporation (float [] PheromoneBegin_table, float [] PheromoneValue_table){
		
		for (int i=0; i < PheromoneValue_table.length; i++){
			
			PheromoneValue_table [i] = (1-Configuration.roe) * PheromoneValue_table [i];
			
			//verifier si on a depasser la valeur initiale
			if (PheromoneValue_table [i] < PheromoneBegin_table [i]) PheromoneValue_table [i] = PheromoneBegin_table [i];
		}
		
		return PheromoneValue_table;
	}
	
}

