package simulation.statistics.system;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

import javax.swing.Timer;

import simulation.multiagentSystem.MAS;
import simulation.multiagentSystem.ObjectSystemIdentifier;

/** enables a quicker performance measure than the journal based statistics 
 * @author Jean-Paul Jamont
 */
public class SystemBasicMeasurement {
	/** period between two measure*/ 	
	static int PERIOD = 1000;
	/** maximum number of measures (after the period is modified)*/
	final static int MAX_NB_COLONNE = 1000;	// 2000 pour quand meme 244M

	/** time use to make measures */
	private  Timer timer_taskAddMeasure = null;  
	/** time associate action listener */
	private  ActionListener taskAddMeasure = new TaskAddMeasure();

	/** the performance measured by the simulator (the last measure : one by objects)*/
	private ArrayList<SystemDataPerformanceItem> updatedSystemDataPerformances;
	/** the list of performance item (one list by objects) */
	private LinkedList<ArrayList<SystemDataPerformanceItem>>  systemDataPerformances;
	/** date of the measures */
	private long[] measuresDate;
	/** number of measures in memory */
	private int nbMeasuresInMemory;

	/** reference to the multiagent system */
	private MAS mas;

	/** default constructor
	 * @param mas reference to the multiagent system
	 */
	public SystemBasicMeasurement(MAS mas)
	{
		this.mas=mas;
		this.systemDataPerformances=new LinkedList<ArrayList<SystemDataPerformanceItem>>();
		this.updatedSystemDataPerformances=new ArrayList<SystemDataPerformanceItem>();
		this.measuresDate=new long[MAX_NB_COLONNE];
		this.nbMeasuresInMemory=0;
		this.timer_taskAddMeasure = new Timer(PERIOD,taskAddMeasure);
		this.timer_taskAddMeasure.start();
	}

	/** getter to the period
	 * @return the period in ms
	 */
	public int getPeriod()
	{
		return this.PERIOD;
	}

	/** stops the basic measurement system */
	public void stop()
	{
		this.timer_taskAddMeasure.stop();
	}

	/** suspends the measurement system */
	public void pause()
	{
		this.timer_taskAddMeasure.stop();
		takeMeasure();
	}

	/** resumes the measurement system */
	public void resume()
	{
		this.timer_taskAddMeasure.start();
	}


	/** returns the SystemDataPerformanceItem of a specified object/agent
	 * @param id identifier of the object/agent
	 * @return the SystemDataPerformanceItem
	 */
	public SystemDataPerformanceItem get(ObjectSystemIdentifier sys_id)
	{
		while (updatedSystemDataPerformances.size()<=sys_id.getId()) updatedSystemDataPerformances.add(new SystemDataPerformanceItem());
		return updatedSystemDataPerformances.get(sys_id.getId());
	}

	/** take a measure
	 *  updatedSystemDataPerformances => systemDataPerformances
	 */
	public synchronized void takeMeasure()
	{
		int i;
		ArrayList<SystemDataPerformanceItem>  lst;
		ListIterator<SystemDataPerformanceItem> iterData;
		ListIterator<ArrayList<SystemDataPerformanceItem>> iterLst;

		// On corrige le nombre de case
		int nbAgents=updatedSystemDataPerformances.size();

		//System.out.println("\nTake measure avec "+nbAgents+" agents / "+this.systemDataPerformances.size()+"    Nb event in memory:"+nbMeasuresInMemory);
		for(i=this.systemDataPerformances.size();i<nbAgents;i++) 
		{
			//System.out.println("Normalise "+i+" a "+nbMeasureInMemory);
			lst=new ArrayList<SystemDataPerformanceItem>(this.MAX_NB_COLONNE);
			SystemDataPerformanceItem itNull= new SystemDataPerformanceItem();
			for(int j=0;j<this.nbMeasuresInMemory;j++)  lst.add(itNull);
			this.systemDataPerformances.add(lst);
		}


		//System.out.println("size="+this.systemDataPerformances.size()+"    Nb event in memory:"+nbMeasuresInMemory);

		iterLst = this.systemDataPerformances.listIterator();
		iterData = this.updatedSystemDataPerformances.listIterator();
		while(iterData.hasNext() && iterLst.hasNext()) iterLst.next().add((SystemDataPerformanceItem) ((iterData.next()).clone()));
		this.measuresDate[++nbMeasuresInMemory-1]=mas.elapsedSimulationTime();


		//relaunch the timer
		if(this.nbMeasuresInMemory>=MAX_NB_COLONNE)
		{
			//System.out.println("DEBUT Traitement du nombre de colonnes ("+this.nbMeasuresInMemory+")");
			boolean b;
			boolean firstPass = true;
			this.PERIOD+=this.PERIOD;
			ListIterator<SystemDataPerformanceItem> iter;
			iterLst = this.systemDataPerformances.listIterator();
			while(iterLst.hasNext())
			{
				b=false;
				lst=iterLst.next();
				iterData=lst.listIterator();
				while(iterData.hasNext())
				{
					iterData.next();
					b=!b;
					if(b) {iterData.remove(); if (firstPass) this.nbMeasuresInMemory--;}
				}
				firstPass=false;
			}
			for(i=1;i<this.nbMeasuresInMemory;i++) this.measuresDate[i]=this.measuresDate[2*i];
			//System.out.println("FIN Traitement du nombre de colonnes ("+this.nbMeasuresInMemory+")");

		}


	}

	/** return the number of measures in memory
	 * @return the number of measures 
	 */
	public int getNbMeasuresInMemory()
	{
		return this.nbMeasuresInMemory;
	}

	/** return the date (in ms) of the specified measure
	 * @param the number of the measure
	 * @return the date
	 */
	public long getMeasureTime(int i)
	{
		return this.measuresDate[i];
	}

	/** return the date (in ms) of the last measure
	 * @return the date
	 */
	public long getTime()
	{
		return this.measuresDate[this.nbMeasuresInMemory-1];
	}

	/** getter to the system data performances 
	 * @return the system data performances (a linkedlist (size=number of objects) of arraylist of SystemDataPerformanceItem (size=number of measure in memory))
	 */
	public LinkedList<ArrayList<SystemDataPerformanceItem>> getSystemDataPerformances()
	{
		return systemDataPerformances;
	}

	/** return a string signature of the object
	 * @return the string signature
	 */
	public String toString()
	{
		ArrayList<SystemDataPerformanceItem>  lst;
		String res="";

		int nbAgent = this.systemDataPerformances.size();
		//System.out.println(nbAgent+" agents\n");
		if(nbAgent==0) return res;


		int nbMeasureInMemory = this.systemDataPerformances.get(1).size();
		//System.out.println(nbMeasureInMemory+" measures in memory\n");

		for(int j=1;j<nbAgent;j++)
		{

			lst=this.systemDataPerformances.get(j);
			for(int k=0;k<nbMeasureInMemory;k++)
			{
				//System.out.println("j="+j+" k="+k+"   lst=null "+(lst==null));
				//System.out.flush();
				if(lst.get(k)==null) System.out.println("null pour j,k="+j+" "+k);
				res+=lst.get(k).nbEvent+"\t";
			}
			res+="\n";
		}
		return res;
	}


	/** Timer to launch an update of all windows */
	private class TaskAddMeasure implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			//System.out.println("Measure...");
			takeMeasure();
		}
	}


}
