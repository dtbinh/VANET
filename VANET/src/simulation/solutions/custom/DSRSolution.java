package simulation.solutions.custom;

import simulation.solutions.Solution;
import simulation.solutions.SolutionDescriber;
import simulation.solutions.SolutionDescriberException;
import simulation.solutions.SolutionException;
import simulation.solutions.custom.DSR.DSRAgent;

/**
 * DSR solution
 * @author Jean-Paul Jamont
 */
public class DSRSolution extends SolutionItem
{
	public DSRSolution()
	{
		super();

		try
		{
			super.setSolution(new Solution(new SolutionDescriber("Jean-Paul Jamont","jean-paul.jamont@iut-valence.fr","Universite Pierre Mendes France","51 rue barthelemy de Laffemas, 26000 Valence","France","DSR - Dynamic Routing Protocol","1a","Implementation of the DSR routing protocol"), Class.forName("simulation.solutions.custom.DSR.DSRAgent")));
		} 
		catch(SolutionException e)
		{
			e.printStackTrace();
		}
		catch(SolutionDescriberException e)
		{
			e.printStackTrace();
		} 
		catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
