package simulation.scenario;

import java.util.LinkedList;
import java.util.ListIterator;

import simulation.scenario.instruction.CreationInstruction;
import simulation.scenario.instruction.Instruction;

/** this class model a scenario 
 * @author Jean-Paul Jamont
 */
public class Scenario {
	/** names of the variable contained in the scenario */
	private LinkedList<String> varNames;
	/** dated instruction contained in the scenario */
	private LinkedList<DatedInstruction> scenario;
	/** initial size of */
	private int initialSize;
	/** last line number interval processed (computed scenario lines)*/
	private int[] lastLineNumberIntervalProcessed = new int[2];

	/** parametrized constructor
	 * @param scenario the scenario (a string with all the text)
	 * @throws ScenarioBadTimeFormatException
	 * @throws ScenarioNotUnderstandedLineException
	 */
	public Scenario(String scenario) throws ScenarioBadTimeFormatException, ScenarioNotUnderstandedLineException
	{
		this(scenario.split("\r\n"));
	}

	/** parametrized constructor
	 * @param scenario the scenario (an array with all the line of the scenario)
	 * @throws ScenarioBadTimeFormatException
	 * @throws ScenarioNotUnderstandedLineException
	 */
	public Scenario(String[] scenario) throws ScenarioBadTimeFormatException, ScenarioNotUnderstandedLineException
	{
		this.varNames=new LinkedList<String>();
		this.scenario=new LinkedList<DatedInstruction>();
		this.initialSize = scenario.length;

		DatedInstruction item;
		for(int i=0;i<this.initialSize;i++) 
		{	
			System.out.print("\nINSTRUCTION "+i+": "+scenario[i]);
			try
			{ 
				System.out.print(" Oo.oO ");
				item =new DatedInstruction(scenario[i]);
				System.out.print(" .oOo. ");
				this.scenario.add(item); 
				System.out.println("            Is creation? "+(item.instruction instanceof CreationInstruction));
				if(item.instruction instanceof CreationInstruction) varNames.add(((CreationInstruction) item.instruction).entity);
			}
			catch(NumberFormatException e)
			{
				e.printStackTrace();
				throw new ScenarioBadTimeFormatException(scenario[i]);
			}
		}
	}

	/**
	 * returns the last line number interval of line processed by the scenario player
	 * @return the line number interval (a 2dimension array)
	 */
	public int[] getLastLineNumberIntervalProcessed()
	{
		return this.lastLineNumberIntervalProcessed;
	}

	/** return the initial number of instruction (=line) of the scenario 
	 * @return the initial number of line of the scenario
	 */
	public int getInitialSize()
	{
		return this.initialSize;
	}

	/** return the remaining number of instruction which must be computed
	 * @return the remaining number of instruction 
	 */
	public int getCurrentSize()
	{
		return this.scenario.size();
	}

	/** return the initial duration of scenario 
	 * @return the initial duration of the scenario
	 */
	public long getInitialDuration()
	{
		return this.scenario.getLast().date;
	}

	/** 
	 * returns the progress associated to a specific date
	 * @param time the date 
	 * @return the progression (%)
	 */
	public float getProgression(long time)
	{
		if(this.scenario.isEmpty())
			return 100;
		else
			return Math.min(100,100.0f*time/this.scenario.getLast().date);
	}

	/**
	 * return the current progression of the sceario
	 * @return the progression (%)
	 */
	public float getProgression()
	{
		return getProgression(this.scenario.getFirst().date);
	}

	/** execute all instruction since the last process to the date date. The processed instruction are deleted
	 * @param date the date limit
	 * @return the processed instructions
	 * @throws FinishedScenarioException
	 * @throws ScenarioNotUnderstandedInstructionException
	 */
	public LinkedList<Instruction> exec(long date) throws FinishedScenarioException,ScenarioNotUnderstandedInstructionException
	{
		return this.exec(date,true);
	}
	/** execute all instruction since the last process to the date date
	 * @param date the date limit
	 * @param deleteExecutedItem the processed instruction must be deleted, yes or no?
	 * @return the processed instructions
	 * @throws FinishedScenarioException
	 * @throws ScenarioNotUnderstandedInstructionException
	 */	public LinkedList<Instruction> exec(long date,boolean deleteExecutedItem) throws FinishedScenarioException,ScenarioNotUnderstandedInstructionException
	 {
		 LinkedList<Instruction> lst = new LinkedList<Instruction>();
		 DatedInstruction item;
		 boolean finished = false;
		 ListIterator<DatedInstruction> iter = this.scenario.listIterator();
		 while(!finished && iter.hasNext())
		 {
			 item=iter.next();
			 finished=(item.date>date);
			 if(!finished)
			 {
				 lst.add(item.instruction);
				 if (deleteExecutedItem) iter.remove();
			 }
		 }

		 if (lst.isEmpty()) 
		 {
			 if(!finished) throw new FinishedScenarioException();

		 }
		 else
		 {
			 this.lastLineNumberIntervalProcessed[0]=1+this.initialSize-this.scenario.size()-lst.size();
			 this.lastLineNumberIntervalProcessed[1]=this.initialSize-this.scenario.size();
		 }
		 return lst;

	 }



	 /**
	  * returns a string representation 
	 @return the string representation
	  */
	 public String toString()
	 {
		 String res="";	int t=this.scenario.size();
		 for(int i=0;i<t;i++)	res+=this.scenario.get(i).toString()+"\n";
		 return res;
	 }

	 /** this class model the behavior of a dated instruction */
	 private class DatedInstruction
	 {
		 /** date of the instruction */
		 public long date;
		 /** the instruction */
		 public Instruction instruction;

		 /** parametrized constructor
		  * @param date date of the instruction
		  * @param instruction instruction which must be computed at date date
		  * @throws NumberFormatException
		  * @throws ScenarioNotUnderstandedLineException
		  */
		 public DatedInstruction(long date,Instruction instruction)
		 {
			 this.date=date;
			 this.instruction=instruction;
		 }

		 /** parametrized constructor
		  * @param line the line which must be decomposed in a date and in an instruction
		  * @throws NumberFormatException
		  * @throws ScenarioNotUnderstandedLineException
		  */
		 public DatedInstruction(String line) throws NumberFormatException,ScenarioNotUnderstandedLineException
		 {
			 String[] array = line.split(">\t");
			 this.date=Long.parseLong(array[0]);
			 System.out.print(" __DATE__ = "+this.date);
			 this.instruction=Instruction.createInstruction(array[1],varNames);
			 System.out.print(" __instruction__ = "+this.instruction);
		 }

		 /**
		  * returns a string representation 
		 @return the string representation
		  */
		 public String toString()
		 {
			 return "@"+this.date+", "+instruction.toString();
		 }

	 }
}
