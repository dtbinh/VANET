package simulation.solutions.custom.VANETNetwork;

import java.util.Vector;

import simulation.solutions.Solution;
import simulation.solutions.SolutionDescriber;
import simulation.solutions.SolutionDescriberException;
import simulation.solutions.SolutionException;
import simulation.solutions.custom.SolutionItem;

public class VanetSolution extends SolutionItem{

	public VanetSolution(){
		super();
		try{
			Vector<Class> agents = new Vector<Class>();
			agents.add(Class.forName("simulation.solutions.custom.VANETNetwork.Voiture"));

			super.setSolution(
					new Solution(
							new SolutionDescriber(
									"Charles Collombert",
									"charles.collombert@hotmail.com	",
									"IUT de Valence",
									"Valence",
									"Valence",
									"Projet Vanet",
									"1",
									"Vanet model"),
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

