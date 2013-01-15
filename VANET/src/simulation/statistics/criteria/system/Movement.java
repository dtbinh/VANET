package simulation.statistics.criteria.system;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;


import simulation.statistics.*;
import simulation.statistics.criteria.SystemCriterion;
import simulation.statistics.system.SystemBasicMeasurement;
import simulation.statistics.system.SystemDataPerformanceItem;

/**
 * This class allow to generate statistics about the number of events occured in the simulation
 * @author Jean-Paul Jamont
 */ 
public class Movement extends SystemCriterion{


	/**
	 * is this criteria concerning agents?
	 * @return yes if this criteria can be applied to an agent
	 */
	public boolean concernsAgent() {
		return true;
	}

	/**
	 * is this criteria concerning the whole system?
	 * @return yes if this criteria can be applied to the whole mas
	 */
	public boolean concernsMAS() {
		return true;
	}

	/**
	 * is this criteria concerning objects?
	 * @return yes if this criteria can be applied to an object
	 */
	public boolean concernsObject() {
		return true;
	}


	/**
	 * returns the description of the criteria
	 * @return the criteria descriptor
	 */
	public String getDescription() {
		return "Allow to range of Agents/Object or the mean range in the MAS";
	}

	/**
	 * returns the name of the criteria
	 * @return the name of the criteria
	 */
	public String getName() {
		return "Mean range";
	}

	/**
	 * returns the author name
	 * @return the name of the author(s)
	 */
	public String getAuthor() {
		return "Jean-Paul Jamont";
	}

	/**
	 * returns the AbstractDataBlock corresponding of the application of this criteria to the multiagent system
	 * @param  measurementSystem the SystemBasicMeasurement associated to the multiagent system
	 * @return the drawable AbstractDataBlock
	 */
	public AbstractDataBlock getDataBlock(SystemBasicMeasurement measurementSystem) {

		return getDataBlock(measurementSystem,-1);
	}

	/**
	 * returns the AbstractDataBlock corresponding of the application of this criteria to the systems measures for one specified agent
	 * @param  measurementSystem the SystemBasicMeasurement associated to the multiagent system
	 * @param id the agent identifier
	 * @return the drawable AbstractDataBlock
	 */
	public AbstractDataBlock getDataBlock(SystemBasicMeasurement measurementSystem, int id) {
		// TODO Auto-generated method stub

		DataBlock data = new DataBlock("Mean range","Time",DataBlock.TIME_IN_MS,"Range",DataBlock.SIMPLE_VALUE_INT);

		ArrayList<SystemDataPerformanceItem> lst;
		LinkedList<ArrayList<SystemDataPerformanceItem>> sys = measurementSystem.getSystemDataPerformances();
		ListIterator<SystemDataPerformanceItem> iterItem;
		ListIterator<ArrayList<SystemDataPerformanceItem>> iterLst;

		if (id==-1)
		{	
			int dim=measurementSystem.getNbMeasuresInMemory();
			int[] tab=new int[dim];
			int nbAgents=0;
			
			// For each agent...
			iterLst=sys.listIterator();
			while(iterLst.hasNext())
			{
				iterItem=iterLst.next().listIterator();	nbAgents++;
				int i=0;
				// For each measures...
				while(iterItem.hasNext()) {tab[i++]+=iterItem.next().range;}
			}

			// Add the point in the data block
			for(int i=0;i<dim;i++) data.addPoint(measurementSystem.getMeasureTime(i),(double)tab[i]/nbAgents);
		}
		else
		{
			// Nombre d'évenements de id	
			lst=sys.get(id);
			iterItem=lst.listIterator();
			int i=0; int range=0;
			// For each measures...
			while(iterItem.hasNext()) {data.addPoint(measurementSystem.getMeasureTime(i),range=iterItem.next().range); i++;}
			data.addPoint(measurementSystem.getTime(),range);
		}
		return data;
	}

}
