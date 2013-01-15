package simulation.solutions.custom;

import simulation.solutions.*;
import simulation.solutions.custom.RecMAS.RecursiveAgent.*;

/**
 * MWAC solution
 * @author Jean-Paul Jamont
 */
public class RecursiveMASSolution extends SolutionItem
{
	public RecursiveMASSolution()
	{
		super();

		try
		{
			super.setSolution(
					new Solution(
							new SolutionDescriber
								("Jean-Paul Jamont, Hoang Thi Thanh Ha ",
								 "jean-paul.jamont@iut-valence.fr, httha@yahoo.com",
								 "Universite Pierre Mendes France",
								 "51 rue barthelemy de Laffemas, 26000 Valence",
								 "France","RecMAS MWAC agent",
								 "2a",
								 "Agents use a self-organization based on the recursive multiagent on MWAC model"
								 ), 
							Class.forName("simulation.solutions.custom.RecMAS.RecursiveAgent.RecursiveAgent")
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