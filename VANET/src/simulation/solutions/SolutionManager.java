package simulation.solutions;

import simulation.solutions.custom.*;
import java.util.*;
import java.io.*;


/**
 * This class allow the different to manage the differents solutions
 * @author Jean-Paul Jamont
 */ 
public class SolutionManager 
{
	/** The solution collector */	 
	private LinkedList<Solution> solutionCollector;

	/**
	 * Construct the solution manager
	 */
	public SolutionManager()
	{
		solutionCollector = new LinkedList<Solution>();
	}

	/**
	 * add a solution to the solution manager
	 * @param name name of the solution
	 * @param soluce solution 
	 */
	public void addSolution(Solution soluce)
	{
		solutionCollector.add(soluce);
	}

	/**
	 * add a solution to the solution manager
	 * @param fileName name of the text file containing the solution class
	 */
	public void addSolutionFromFile(String fileName)
	{
		String str="";

		try
		{
			String solutionItemClassName;

			// Lecture depuis un fichier
			BufferedReader in = new BufferedReader(new FileReader(fileName));
			while((solutionItemClassName = in.readLine()) != null) str+=solutionItemClassName+"\r\n";
			in.close();				
		} 
		catch(IOException e) 
		{
			e.printStackTrace();
		}

		addSolutions(str);
	}


	/**
	 * add a solution to the solution manager
	 * @param solutions the solutions classes (each one separate by "\r\n")
	 */
	public void addSolutions(String solutions) 
	{
		System.out.println("-------Load implemented solutions-------");
		System.out.println("Given solutions: \n"+ solutions);

		String[] sol = solutions.split("\r\n");

		String solutionItemClassName;

		for (int i=0;i<sol.length;i++)
		{
			solutionItemClassName=sol[i];
			if(!solutionItemClassName.isEmpty())
			{
				System.out.println("-->   <"+solutionItemClassName+">");
				try
				{
					Class solutionItemClass = Class.forName("simulation.solutions.custom."+solutionItemClassName);  
					SolutionItem solutionItem  = (SolutionItem)solutionItemClass.newInstance();

					this.addSolution(solutionItem.getSolution());
					System.out.println("       Solution name :"+solutionItem.getSolution().getImplementationName());
					System.out.println("       Version       :"+solutionItem.getSolution().getVersion());
					System.out.println("       Author(s)     :"+solutionItem.getSolution().getAuthor());
					System.out.println("       Agents        :"+solutionItem.getSolution().agentListToString());
					System.out.println("       Objects       :"+solutionItem.getSolution().objectListToString());
					System.out.println("");
				}
				catch(Exception e)
				{
					System.out.println("simulation.solutions.custom."+solutionItemClassName+" not loaded!");
					e.printStackTrace();
				}
			}
		}




		System.out.println("----------------------------------------");
	}




	/**
	 * Get the number of existing solutions
	 * @return number of solutions
	 */	
	public int getSolutionNumber()
	{
		return solutionCollector.size();
	}

	/**
	 * Get a solution 
	 * @param index index of the extracted solution to exit
	 * @return the choosen solution 
	 */	
	public Solution getSolution(int index)
	{
		return solutionCollector.get(index);
	}

	/**
	 * Get a solution 
	 * @param name Name of the solution
	 * @param version Version of the solution
	 * @return the choosen solution 
	 */	
	public Solution getSolution(String name,String version) throws SolutionNotFoundException
	{
		Solution slt;

		// Search the solution
		for (int i=0;i<solutionCollector.size();i++) 
		{
			// Take a solution
			slt = solutionCollector.get(i);
			// Compare to the searched solution
			if (slt.getImplementationName().equals(name) && slt.getVersion().equals(version)) return slt;
		}

		throw new SolutionNotFoundException();
	}



	/**
	 * Returns a String object representing the specified integer.
	 * @return The string representation
	 */
	public String toString()
	{
		String s = new String();

		for (int i=0;i<solutionCollector.size();i++) s+=solutionCollector.get(i).toString()+"\n";

		return s;
	}

}
