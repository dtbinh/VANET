package simulation.solutions.custom;

import java.util.Vector;

import simulation.solutions.*;
import simulation.solutions.custom.Home.*;

/**
 * MWAC solution
 * @author Jean-Paul Jamont
 */
public class HomeSolution extends SolutionItem
{
	public HomeSolution() throws ClassNotFoundException
	{
		super();
		
		Vector<Class> agents = new Vector<Class>();
		agents.add(Class.forName("simulation.solutions.custom.Home.ConvectorAgent"));
		agents.add(Class.forName("simulation.solutions.custom.Home.FloorAgent"));
		agents.add(Class.forName("simulation.solutions.custom.Home.VentilationAgent"));
		agents.add(Class.forName("simulation.solutions.custom.Home.RoomAgent"));
		agents.add(Class.forName("simulation.solutions.custom.Home.VMCAgent"));
		agents.add(Class.forName("simulation.solutions.custom.Home.PACAgent"));
		try
		{
			super.setSolution(new Solution(new SolutionDescriber("Farid & Jean-Paul","jean-paul.jamont@iut-valence.fr","Universite Pierre Mendes France","51 rue barthelemy de Laffemas, 26000 Valence","France","Home agent","2a","Agents use a self-organization based on the MWAC model"), agents));
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