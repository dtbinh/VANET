package simulation.solutions.custom;


import simulation.solutions.Solution;
import simulation.solutions.SolutionDescriber;
import simulation.solutions.SolutionDescriberException;
import simulation.solutions.SolutionException;
import simulation.solutions.custom.ACO_MWAC.ACO_MWAC_Agent;


/**
 * ACO MWAC solution
 * @author Yacine LATTAB & Ouerdia SLAOUTI
 */
public class ACO_MWAC_Solution extends SolutionItem
{
	public ACO_MWAC_Solution() throws ClassNotFoundException
	{
		super();

		try
		{
			super.setSolution(new Solution(new SolutionDescriber("Yacine LATTAB & Ouerdia SLAOUTI","y_lattab@esi.dz, o_slaouti@esi.dz","Ecole Nationale Superieure d'Informatique","Oued Smar, 16000 Alger","Algerie","ACO_MWAC agent ","1a","Ant Colony Optimisation for Multi Wireless Agent Communication"), Class.forName("simulation.solutions.custom.ACO_MWAC.ACO_MWAC_Agent")));
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