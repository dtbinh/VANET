package simulation.solutions.custom;

import simulation.solutions.*;
import simulation.solutions.custom.TrustedMWAC.*;

/**
 * MWAC solution
 * @author Jean-Paul Jamont
 */
public class TrustedMWACSolution extends SolutionItem
{
	public TrustedMWACSolution()
	{
		super();

		try
		{
			super.setSolution(new Solution(new SolutionDescriber("Jean-Paul Jamont","jean-paul.jamont@iut-valence.fr","Universite Pierre Mendes France","51 rue barthelemy de Laffemas, 26000 Valence","France","Trusted MWAC agent","2a","Agents use a self-organization based on the trusted version of the MWAC model"), Class.forName("simulation.solutions.custom.TrustedMWAC.TrustedMWACAgent")));
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