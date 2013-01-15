package simulation.solutions.custom;

import simulation.solutions.Solution;
import simulation.solutions.SolutionDescriber;
import simulation.solutions.SolutionDescriberException;
import simulation.solutions.SolutionException;
import simulation.solutions.custom.DSRSocial.DSRSocialAgent;


public class DSRSocialSolution extends SolutionItem
{
	public DSRSocialSolution()
	{
		super();

		try
		{
			super.setSolution(new Solution(new SolutionDescriber("Jean-Paul Jamont","jean-paul.jamont@iut-valence.fr","Universite Pierre Mendes France","51 rue barthelemy de Laffemas, 26000 Valence","France","DSR SOCIAL - Dynamic Routing Protocol","1a","Implementation of the DSR routing protocol"), Class.forName("simulation.solutions.custom.DSRSocial.DSRSocialAgent")));
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
