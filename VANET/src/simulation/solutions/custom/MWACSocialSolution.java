package simulation.solutions.custom;

import simulation.solutions.*;
import simulation.solutions.custom.MWACSocial.MWACSocialAgent;

public class MWACSocialSolution extends SolutionItem
{
	public MWACSocialSolution()
	{
		super();

		try
		{
			super.setSolution(new Solution(new SolutionDescriber(	"Jean-Paul Jamont",
																	"jean-paul.jamont@iut-valence.fr",
																	"Universite Pierre Mendes France",
																	"51 rue barthelemy de Laffemas, 26000 Valence",
																	"France","MWAC SOCIAL agent",
																	"2a",
																	"Agents use a self-organization based on the MWAC model"), 
																	Class.forName("simulation.solutions.custom.MWACSocial.MWACSocialAgent")));
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