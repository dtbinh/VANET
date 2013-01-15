package simulation.solutions.custom;

import simulation.solutions.*;


/**
 * MWAC solution
 * @author Jean-Paul Jamont
 */
public class AntMWACSolution extends SolutionItem
{
	public AntMWACSolution()
	{
		super();

		try
		{
			super.setSolution(
					new Solution(
							new SolutionDescriber
								("Nacer Hamani & Jean-Paul Jamont",
								 "jean-paul.jamont@iut-valence.fr",
								 "Universite Pierre Mendes France",
								 "51 rue barthelemy de Laffemas, 26000 Valence",
								 "France",
								 "Ant MWAC agent",
								 "2a",
								 "Agents use a self-organization based on the Ant MWAC model"
								 ), 
							Class.forName("simulation.solutions.custom.AntMWAC.MWAC.AntMWACAgent"),
							Class.forName("simulation.solutions.custom.AntMWAC.MWAC.Messages.MWACFrame")
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