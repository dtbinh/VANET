package simulation.statistics;

import simulation.solutions.custom.SolutionItem;
import simulation.statistics.criteria.CriterionInterface;
import simulation.statistics.criteria.SystemCriterion;
import simulation.statistics.criteria.UserCriterion;
import simulation.statistics.system.SystemBasicMeasurement;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;


import simulation.journal.SimulationJournal;

/**
 * manager of the possible criteria which can be processed on the journal
 * @author Jean-Paul Jamont
 */

public class StatisticManager {

	private final int CRITERIA_CONCERNING_MAS 		= 0;
	private final int CRITERIA_CONCERNING_AGENT 	= 1;
	private final int CRITERIA_CONCERNING_OBJECT 	= 2;


	private LinkedList<UserCriterion> userCriteriaList;
	private LinkedList<SystemCriterion> systemCriteriaList;

	private SystemBasicMeasurement measurementSystem;
	private SimulationJournal journal;

	public StatisticManager()
	{
		this(null,null);
	}

	public StatisticManager(SimulationJournal journal,SystemBasicMeasurement measurementSystem)
	{
		this.userCriteriaList=new LinkedList<UserCriterion>();
		this.systemCriteriaList=new LinkedList<SystemCriterion>();
		this.measurementSystem=measurementSystem;
		this.journal=journal;

		this.addSystemCriteria();
	}


	public void addSystemCriteria()
	{
		this.addSystemCriteria(new simulation.statistics.criteria.system.NumberOfEvents());
		this.addSystemCriteria(new simulation.statistics.criteria.system.CumuledVolumeOfReceivedFrames());
		this.addSystemCriteria(new simulation.statistics.criteria.system.CumuledVolumeOfReceivedMessages());
		this.addSystemCriteria(new simulation.statistics.criteria.system.CumuledVolumeOfSendedFrames());
		this.addSystemCriteria(new simulation.statistics.criteria.system.CumuledVolumeOfSendedMessages());
		this.addSystemCriteria(new simulation.statistics.criteria.system.NumberOfReceivedFrames());
		this.addSystemCriteria(new simulation.statistics.criteria.system.NumberOfReceivedMessages());
		this.addSystemCriteria(new simulation.statistics.criteria.system.NumberOfSendedFrames());
		this.addSystemCriteria(new simulation.statistics.criteria.system.NumberOfSendedMessages());				
		this.addSystemCriteria(new simulation.statistics.criteria.system.MeanRange());
		this.addSystemCriteria(new simulation.statistics.criteria.system.AvailableEnergyAmount());
	}

	public void setJournal(SimulationJournal journal)
	{
		this.journal=journal;
	}
	public void setSystemBasicMeasurement(SystemBasicMeasurement measurementSystem)
	{
		this.measurementSystem=measurementSystem;
	}

	public void addUserCriteria(UserCriterion c)
	{
		this.userCriteriaList.add(c);
	}

	public void addSystemCriteria(SystemCriterion c)
	{
		this.systemCriteriaList.add(c);
	}



	private LinkedList<String> extract_name_of_user_criteria(int type)
	{
		CriterionInterface c;	boolean concerning;
		LinkedList<String> res = new LinkedList<String>();
		ListIterator<UserCriterion> iter = this.userCriteriaList.listIterator();

		while(iter.hasNext())
		{	c=iter.next();
		switch(type)
		{
		case CRITERIA_CONCERNING_MAS:	concerning = c.concernsMAS();	break;
		case CRITERIA_CONCERNING_AGENT:	concerning = c.concernsAgent();	break;
		case CRITERIA_CONCERNING_OBJECT:concerning = c.concernsObject();break;
		default: concerning = false;
		}
		if (concerning) res.add(c.getName()+"\t"+c.getDescription());
		}
		return res;	}

	private LinkedList<String> extract_name_of_system_criteria(int type)
	{
		LinkedList<String> res = new LinkedList<String>();
		CriterionInterface c;	boolean concerning;

		ListIterator<SystemCriterion> iter = this.systemCriteriaList.listIterator();

		while(iter.hasNext())
		{	c=iter.next();
		switch(type)
		{
		case CRITERIA_CONCERNING_MAS:	concerning = c.concernsMAS();	break;
		case CRITERIA_CONCERNING_AGENT:	concerning = c.concernsAgent();	break;
		case CRITERIA_CONCERNING_OBJECT:concerning = c.concernsObject();break;
		default: concerning = false;
		}
		if (concerning) res.add(c.getName()+"\t"+c.getDescription());
		}
		return res;	}

	public LinkedList<String> OBJECTS_system_criteria_name()
	{
		return extract_name_of_system_criteria(CRITERIA_CONCERNING_AGENT);
	}

	public LinkedList<String> AGENTS_system_criteria_name()
	{
		return extract_name_of_system_criteria(CRITERIA_CONCERNING_OBJECT);
	}

	public LinkedList<String> MAS_system_criteria_name()
	{
		return extract_name_of_system_criteria(CRITERIA_CONCERNING_MAS);
	}


	public LinkedList<String> OBJECTS_user_criteria_name()
	{
		return extract_name_of_user_criteria(CRITERIA_CONCERNING_AGENT);
	}

	public LinkedList<String> AGENTS_user_criteria_name()
	{
		return extract_name_of_user_criteria(CRITERIA_CONCERNING_OBJECT);
	}

	public LinkedList<String> MAS_user_criteria_name()
	{
		return extract_name_of_user_criteria(CRITERIA_CONCERNING_MAS);
	}

	public AbstractDataBlock getDataBlock(String criteriaName)
	{
		return getDataBlock(criteriaName,-1);
	}

	private AbstractDataBlock getUserDataBlock(String criteriaName,int id)
	{
		UserCriterion c;
		ListIterator<UserCriterion> iter = this.userCriteriaList.listIterator();

		while(iter.hasNext())
		{	c=iter.next();
		if (c.getName().equals(criteriaName))
			if(id==-1)
				return c.getDataBlock(this.journal);
			else
				return c.getDataBlock(this.journal, id);
		}

		return null;
	}

	private AbstractDataBlock getSystemDataBlock(String criteriaName,int id)
	{
		SystemCriterion c;
		ListIterator<SystemCriterion> iter = this.systemCriteriaList.listIterator();

		while(iter.hasNext())
		{	c=iter.next();
		if (c.getName().equals(criteriaName))
			if(id==-1)
				return c.getDataBlock(this.measurementSystem);
			else
				return c.getDataBlock(this.measurementSystem, id);

		}

		return null;
	}

	public AbstractDataBlock getDataBlock(String criteriaName,int id)
	{
		AbstractDataBlock res = getSystemDataBlock(criteriaName,id);
		if (res==null) 
			return getUserDataBlock(criteriaName,id);
		else
			return res;
	}

	/**
	 * add a solution to the statistic manager
	 * @param fileName name of the text file containing the criteria
	 */
	public void addUserSolutionFromFile(String fileName)
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

		addUserSolutions(str);
	}


	/**
	 * add a solution to the statistic manager
	 * @param solutions the criteria classes (each one separate by "\r\n")
	 */
	public void addUserSolutions(String solutions) 
	{
		System.out.println("-------Load implemented statistic criteria-------");
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
					Class solutionItemClass = Class.forName("simulation.statistics.criteria.custom."+solutionItemClassName);  
					UserCriterion solutionItem  = (UserCriterion)solutionItemClass.newInstance();
					this.addUserCriteria(solutionItem);
					System.out.println("Criteria: "+solutionItem.getName()+"\tAuthors:"+solutionItem.getAuthor());


				}
				catch(Exception e)
				{
					System.out.println("simulation.statistics.criteria.custom."+solutionItemClassName+" not loaded!");
					e.printStackTrace();
				}
			}
		}




		System.out.println("----------------------------------------");
	}




}
