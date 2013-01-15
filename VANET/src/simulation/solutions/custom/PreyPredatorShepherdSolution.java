package simulation.solutions.custom;

import java.util.Vector;

import simulation.entities.Agent;
import simulation.solutions.Solution;
import simulation.solutions.SolutionDescriber;
import simulation.solutions.SolutionDescriberException;
import simulation.solutions.SolutionException;
import simulation.solutions.custom.ClockUsersAndTranslators.UserAgent;
import simulation.solutions.custom.PreyPredator.PreyAgent;

/**
 * Horloge solution
 * @author Jean-Paul Jamont
 */
public class PreyPredatorShepherdSolution extends SolutionItem
{
	public PreyPredatorShepherdSolution()
	{
		super();

		
		try
		{
			Vector<Class> agents = new Vector<Class>();
			agents.add(Class.forName("simulation.solutions.custom.PreyPredatorShepherd.PreyAgent"));
			agents.add(Class.forName("simulation.solutions.custom.PreyPredatorShepherd.PredatorAgent"));
			agents.add(Class.forName("simulation.solutions.custom.PreyPredatorShepherd.ShepherdAgent"));
	
				super.setSolution(
						new Solution(
								new SolutionDescriber(
										"Jean-Paul Jamont & Nacer Hamani" 	/* nom de l'auteurs de la solution */,
										"jean-paul.jamont@iut-valence.fr" 	/* adresse mail du correspondant*/,
										"Universite Pierre Mendes France" 	/* établissement de rattachement*/,
										"51 rue barthelemy de Laffemas, 26000 Valence" /* adresse */,
										"France" 							/* pays */,
										"PREY/PREDATOR/SHEPHERD - Prey/predator toy problem" 			/* Dénomination */,
										"1a" 								/* version */,
										"Implementation of a prey/predator toy problem" /*Description */
								), 
								agents										/* référence vers l'agent */
						)
				);
			
		} 
		catch(SolutionException e)
		{
			e.printStackTrace();
		}
		catch(SolutionDescriberException e)
		{
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
