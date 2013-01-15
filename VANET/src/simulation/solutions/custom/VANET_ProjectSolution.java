package simulation.solutions.custom;

import java.util.Vector;

import simulation.solutions.Solution;
import simulation.solutions.SolutionDescriber;
import simulation.solutions.SolutionDescriberException;
import simulation.solutions.SolutionException;
/**
 * Classe d'initialisation de la solution du projet VANET
 * @author Vincent Guilleminot alias Wyvern
 *
 */
public class VANET_ProjectSolution extends SolutionItem{

	public VANET_ProjectSolution(){
		super();
		try{
			@SuppressWarnings("rawtypes")//On reste général pour que l'on puisse y mettre n'importe quel type d'objet
			Vector<Class> agents = new Vector<Class>();
			agents.add(Class.forName("simulation.solutions.custom.PreyPredator.Voiture"));
			agents.add(Class.forName("simulation.solutions.custom.PreyPredator.FeuDeSignalisation"));
			super.setSolution(
					new Solution(
							new SolutionDescriber(
									"Vincent Guilleminot && Charles Collombert",
									//TODO : mettre @ Charles
									"guilleminotvincent@gmail.com",																		
									"IUT de Valence",
									"Drôme",
									"France",
									"VANET Network",
									"1",
									"VANET model"),
							agents
					)
			);
		}catch(SolutionException e){
			e.printStackTrace();
		}catch(SolutionDescriberException e){
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}	
}