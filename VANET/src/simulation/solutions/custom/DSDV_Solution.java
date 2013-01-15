package simulation.solutions.custom;

import simulation.solutions.*;
import simulation.solutions.custom.DSDV.*;

/**
 * MWAC solution
 * @author Yacine LATTAB & Ouerdia SLAOUTI
 */
public class DSDV_Solution extends SolutionItem
{
	public DSDV_Solution() throws ClassNotFoundException
	{
		super();

		try
		{
			super.setSolution(new Solution(new SolutionDescriber("Yacine LATTAB & Ouerdia SLAOUTI","y_lattab@esi.dz & o_slaouti@esi.dz","Ecole Nationale Superieure d'Informatique","Oued Smar, 16000 Alger","Algerie","DSDV agent","1a","Destination Sequenced Distance Vector"), Class.forName("simulation.solutions.custom.DSDV.DSDVAgent")));
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