package simulation.solutions.custom;

import java.util.Vector;

import simulation.solutions.Solution;
import simulation.solutions.SolutionDescriber;
import simulation.solutions.SolutionDescriberException;
import simulation.solutions.SolutionException;

public class PreyPredatorSolution extends SolutionItem{

	public PreyPredatorSolution(){
		super();
		try{
			Vector<Class> agentes = new Vector<Class>();
			agentes.add(Class.forName("simulation.solutions.custom.PreyPredator.PreyAgent"));
			agentes.add(Class.forName("simulation.solutions.custom.PreyPredator.PredatorAgent"));
			super.setSolution(
					new Solution(
							new SolutionDescriber(
									"Armando Cervantes",
									"armando.cervantes.h@gmail.com",
									"Universidad Politecnica de Sinaloa",
									"Mazatlan",
									"Mexico",
									"Pray-Predator",
									"1",
									"Prey-Predator model"),
							agentes
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