package simulation.solutions.custom;

import java.util.Vector;

import simulation.solutions.Solution;
import simulation.solutions.SolutionDescriber;
import simulation.solutions.SolutionDescriberException;
import simulation.solutions.SolutionException;

public class VanetNetworkSolution extends SolutionItem{

	public VanetNetworkSolution(){
		super();
		try{
			Vector<Class> agents = new Vector<Class>();
			agents.add(Class.forName("simulation.solutions.custom.VANETNetwork.Voiture"));
			agents.add(Class.forName("simulation.solutions.custom.VANETNetwork.Croisement"));
			
			super.setSolution(
					new Solution(
							new SolutionDescriber(
									"Charles Collombert && Vincent Guilleminot(ccollombert/Reykjanes)",
									"charles.collombert@gmail.com && guilleminotvincent@gmail.com",
									"IUT de Valence",
									"Valence",
									"26000",
									"Projet Vanet",
									"3",
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

