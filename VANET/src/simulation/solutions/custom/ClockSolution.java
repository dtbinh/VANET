package simulation.solutions.custom;

import java.util.Vector;

import simulation.entities.Agent;
import simulation.solutions.Solution;
import simulation.solutions.SolutionDescriber;
import simulation.solutions.SolutionDescriberException;
import simulation.solutions.SolutionException;
import simulation.solutions.custom.ClockUsersAndTranslators.UserAgent;

/**
 * Horloge solution
 * @author Jean-Paul Jamont
 */
public class ClockSolution extends SolutionItem
{
	public ClockSolution() throws ClassNotFoundException
	{
		super();

		Vector<Class> agents = new Vector<Class>();
		
		agents.add(Class.forName("simulation.solutions.custom.ClockUsersAndTranslators.ClockAgent"));
		agents.add(Class.forName("simulation.solutions.custom.ClockUsersAndTranslators.UserAgent"));
		agents.add(Class.forName("simulation.solutions.custom.ClockUsersAndTranslators.FrenchToEnglishTranslator"));
		
		try
		{
			super.setSolution(
					new Solution(
							new SolutionDescriber(
									"Jean-Paul Jamont" 					/* nom de l'auteurs de la solution */,
									"jean-paul.jamont@iut-valence.fr" 	/* adresse mail du correspondant*/,
									"Universite Pierre Mendes France" 	/* établissement de rattachement*/,
									"51 rue barthelemy de Laffemas, 26000 Valence" /* adresse */,
									"France" 							/* pays */,
									"HORLOGE - Agent Horloge" 			/* Dénomination */,
									"1a" 								/* version */,
									"Implementation of a user/clock toy problem" /*Description */
							), 
							agents /* référence vers l'agent */
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
		}
	}
}
