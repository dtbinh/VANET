package simulation.scenario;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Vector;

import simulation.multiagentSystem.MAS;
import simulation.multiagentSystem.Perf;
import simulation.scenario.instruction.CallInstruction;
import simulation.scenario.instruction.CreationInstruction;
import simulation.scenario.instruction.Instruction;
import simulation.scenario.instruction.Parameter;
import simulation.solutions.ClassNotFoundInSolution;
import simulation.solutions.Solution;

/** scenario manager 
 * @author Jean-Paul Jamont
 */
public class ScenarioManager {

	/** reference to the object which enables an entity (object/agent) addition */
	private InterfaceEntityAddition main;
	/** reference to the multiagent system */
	private MAS mas;
	/** reference to the scenario */
	private Scenario scenario;
	/** reference to the evolved solution */
	private Solution solution;
	/** dictionnary of entities name */
	private LinkedList<EntitiesDictionnaryItem> dictionnary;

	/** basic constructor
	 * @param main reference to the object which enables an entity (object/agent) addition
	 * @param mas reference to the multiagent system 
	 * @param scenario reference to the scenario
	 * @param solution reference to the evolved solution
	 * @throws ScenarioBadTimeFormatException
	 * @throws ScenarioNotUnderstandedLineException
	 */
	public ScenarioManager(InterfaceEntityAddition main,MAS mas,String scenario,Solution solution) throws ScenarioBadTimeFormatException, ScenarioNotUnderstandedLineException 
	{	
		this.main=main;
		this.mas=mas;
		this.scenario=new Scenario(scenario);
		this.solution=solution;
		this.dictionnary = new LinkedList<EntitiesDictionnaryItem>();
	}

	/** 
	 * returns the progress associated to a specific date
	 * @param time the date 
	 * @return the progression (%)
	 */
	public float getProgression(long time)
	{
		return scenario.getProgression(time);
	}

	/**
	 * returns the last line number interval of line processed by the scenario player
	 * @return the line number interval (a 2dimension array)
	 */
	public int[] getLastLineNumberIntervalProcessed()
	{
		return scenario.getLastLineNumberIntervalProcessed();
	}


	/** execute all instruction since the last process to the date date. The processed instruction are deleted
	 * @param date the date limit
	 * @throws FinishedScenarioException
	 * @throws ScenarioNotUnderstandedInstructionException
	 * @throws ClassNotFoundInSolution 
	 */
	public void exec(long date) throws FinishedScenarioException, ScenarioNotUnderstandedInstructionException, IllegalArgumentException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, VariableNotFoundException, MethodNotFoundException, ClassNotFoundInSolution
	{
		
		//Perf p = new Perf();

		LinkedList<Instruction> lst = this.scenario.exec(date);
		if(!lst.isEmpty())
		{
			LinkedList<Instruction> lstInstruction = new LinkedList<Instruction>();

			System.out.println("------DATE="+date+"-----");
			Instruction item;
			ListIterator<Instruction> iter = lst.listIterator();
			while(iter.hasNext())
			{
				item=iter.next();
				if (item instanceof CreationInstruction)
				{
					lstInstruction.add(item);
				}
				else if (item instanceof CallInstruction)
				{
					if(!lstInstruction.isEmpty()) 
					{
						exec_creationInstruction(lstInstruction);
						lstInstruction.clear();
					}

					exec_callInstruction((CallInstruction) item);
				}
			}

			// Last pass
			if(!lstInstruction.isEmpty()) exec_creationInstruction(lstInstruction);
		}

		//System.out.println(">>>>>>>>>>>> Durée : "+p.elapsedToString());

	}

	/** execute an instruction which is the call of a method on an object/agent
	 * @param instruction the call instruction
	 * @throws ClassNotFoundException
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws VariableNotFoundException
	 * @throws MethodNotFoundException
	 */
	private void exec_callInstruction(CallInstruction instruction) throws ClassNotFoundException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, VariableNotFoundException, MethodNotFoundException
	{
		CompatibilityResult param=null;
		String variableName = instruction.entity;
		EntitiesDictionnaryItem dictionnaryItem;

		dictionnaryItem= extractVariableFromDictionnary(variableName);
		Class cl = dictionnaryItem.obj.getClass();
		Method[] tabMethods = cl.getMethods();

		for(int j=0;j<tabMethods.length;j++)
		{
			//			{
			//				System.out.print("**** METHODES ****    (instruction=\""+instruction.method+"\")   (methode inspectée=\""+tabMethods[j].getName()+"\""+" param=");
			//				for(int k=0;k<tabMethods[j].getParameterTypes().length;k++) System.out.print(tabMethods[j].getParameterTypes()[k]+" , ");
			//				System.out.println("");
			//			}
			if(instruction.method.equals(tabMethods[j].getName()))
			{
				param=compatibleMethods(tabMethods[j].getParameterTypes(),instruction.param);

				if (param.res) 
				{
					// On a le bon constructeur
					//System.out.println("!!! La méthode COMPATIBLE est "+tabMethods[j]);
					tabMethods[j].invoke(dictionnaryItem.obj,param.param);
					//main.scenarioEntityAddition(id,((Integer)instruction.param.get(0).value),((Integer)instruction.param.get(1).value),elt.obj);
				}
				else 
				{
					//System.out.println("Constructeur non compatible \n"+tabMethods[j]+"\n"+instruction.param);
				}
			}
		}
		if (!param.res) throw new MethodNotFoundException(cl.getName(),instruction.method,instruction.param.toString());

	}


	/** execute an instruction which is the creation of an object/agent
	 * @param instruction the instruction of creation
	 * @throws ClassNotFoundException
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws VariableNotFoundException
	 * @throws ClassNotFoundException 
	 */
	private void exec_creationInstruction(CreationInstruction instruction) throws ClassNotFoundInSolution, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException,VariableNotFoundException, ClassNotFoundException
	{
		long tps = 0;

		boolean classFinded = false;
		CompatibilityResult param=null;
		

		Class cl = null;
		try
		{
			this.solution.getTheAgentClass(instruction.className);
			classFinded=true;
		}
		catch(ClassNotFoundInSolution e)
		{
			//e.printStackTrace();
		}
		if(!classFinded) 
			this.solution.getTheObjectClass(instruction.className);
		
		Constructor[] tabConstructors = cl.getConstructors();
		for(int j=0;j<tabConstructors.length;j++)
		{
			param=compatibleConstructors(tabConstructors[j].getParameterTypes(),instruction.param);

			if (param.res) 
			{
				// On a le bon constructeur
				//System.out.println("!!! Le constructeur COMPATIBLE est "+tabConstructors[j]);
				int id=1+this.dictionnary.size();
				param.param[0]=mas;
				param.param[1]=new Integer(id);

				EntitiesDictionnaryItem elt=null;
				elt = new EntitiesDictionnaryItem(id,instruction.entity,(simulation.entities.Object)tabConstructors[j].newInstance(param.param));
				this.dictionnary.add(elt);


				main.scenarioEntityAddition(id,((Integer)instruction.param.get(0).value),((Integer)instruction.param.get(1).value),elt.obj);

			}
			else 
			{
				System.out.println("Constructeur non compatible \n"+tabConstructors[j]+"\n"+instruction.param);
			}
		}
	}
	/** execute a list of instructions which are the creation of an object/agent
	 * @param instruction the instruction of creation
	 * @throws ClassNotFoundException
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws VariableNotFoundException
	 * @throws ClassNotFoundInSolution 
	 */
	private void exec_creationInstruction(LinkedList<Instruction> lst) throws ClassNotFoundException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException,VariableNotFoundException, ClassNotFoundInSolution
	{
		CompatibilityResult param=null;
		Class cl=null;
		Constructor[] tabConstructors=null;

		CreationInstruction instruction=null;

		int id=-1;
		EntitiesDictionnaryItem elt=null;

		ListIterator<Instruction> instructionIterator = lst.listIterator();

		while(instructionIterator.hasNext())
		{
			instruction=(CreationInstruction) instructionIterator.next();

			cl = this.solution.getTheAgentClass(instruction.className);
			tabConstructors= cl.getConstructors();

			for(int j=0;j<tabConstructors.length;j++)
			{

				param=compatibleConstructors(tabConstructors[j].getParameterTypes(),instruction.param);

				if (param.res) 
				{
					// On a le bon constructeur
					//System.out.println("!!! Le constructeur COMPATIBLE est "+tabConstructors[j]);
					id=1+this.dictionnary.size();
					param.param[0]=mas;
					param.param[1]=new Integer(id);

					elt=null;
					elt = new EntitiesDictionnaryItem(id,instruction.entity,(simulation.entities.Object)tabConstructors[j].newInstance(param.param));
					this.dictionnary.add(elt);
					
					main.scenarioEntityAddition(id,((Integer)instruction.param.get(0).value),((Integer)instruction.param.get(1).value),elt.obj);
				}
				else 
				{
					System.out.println("Constructeur non compatible \n"+tabConstructors[j]+"\n"+instruction.param);
				}
			}
		}
	
	}

	/**
	 * try to find a compatible constructor in the inspected entity (agent/object)
	 * @param paramsClass classes of each parameter
	 * @param paramsValues values of each parameter
	 * @return
	 * @throws ClassNotFoundException
	 * @throws VariableNotFoundException
	 */
	private CompatibilityResult compatibleConstructors(Class[] paramsClass,Vector<Parameter> paramsValues) throws ClassNotFoundException,VariableNotFoundException
	{
		// ATTENTION UN CONSTRUCTEUR COMMENCE PAR MAS PUIS ID (MAS,Integer)
		// LE VECTEUR COMMENCE PAR X Y (Integer,Integer)

		CompatibilityResult result = new CompatibilityResult();
		result.res=false;
		// Le nombre de paramétre est-il le même?
		if (paramsClass.length!=paramsValues.size() || paramsClass.length<2) return result;

		// On compare les paramétres
		if(!paramsClass[0].getName().equals(simulation.multiagentSystem.MAS.class.getName())) return result;
		if(!paramsClass[1].getName().equals(Integer.class.getName())) return result;
		if(paramsValues.get(0).type!=Parameter.INTEGER) return result;
		if(paramsValues.get(1).type!=Parameter.INTEGER) return result;


		// Instancie le tableau d'objets
		result.param=new java.lang.Object[paramsClass.length];
		result.res=true;
		for(int i=2;i<paramsClass.length && result.res;i++)
		{	
			// Récupère le paramétre
			result.param[i]=compatibility(paramsClass[i].getName(),paramsValues.get(i));
			if(result.param[i]==null) result.res=false;
		}

		return result;
	}



	/**
	 * try to find a compatible method in the inspected entity (agent/object)
	 * @param paramsClass classes of each parameter
	 * @param paramsValues values of each parameter
	 * @return
	 * @throws ClassNotFoundException
	 * @throws VariableNotFoundException
	 */
	private CompatibilityResult compatibleMethods(Class[] parameters,Vector<Parameter> v) throws ClassNotFoundException,VariableNotFoundException
	{
		// ATTENTION UN CONSTRUCTEUR COMMENCE PAR MAS PUIS ID (MAS,Integer)
		// LE VECTEUR COMMENCE PAR X Y (Integer,Integer)

		CompatibilityResult result = new CompatibilityResult();
		result.res=false;

		// Le nombre de paramétre est-il le même?
		if (parameters.length!=v.size()) return result;

		// On compare les paramétres
		result.res=true;
		result.param=new java.lang.Object[parameters.length];
		for(int i=0;i<parameters.length && result.res;i++)
		{	
			result.param[i]=compatibility(parameters[i].getName(),v.get(i));
			if(result.param[i]==null) result.res=false;
		}

		return result;
	}	


	/**
	 * compute the compatibility between a class and a parameter 
	 * @param paramJavaClassName the string representation of a java class ("java.lang.String", "java.lang.Integer"...)
	 * @param paramScenarioClassName the string representation of a type in the scenario
	 * @return
	 * @throws ClassNotFoundException
	 * @throws VariableNotFoundException 
	 */
	private java.lang.Object compatibility(String paramJavaClassName,Parameter paramScenarioClassName) throws ClassNotFoundException,VariableNotFoundException
	{
		// We wait a String
		if(paramJavaClassName.equals("java.lang.String")) 
		{
			if(paramScenarioClassName.type==Parameter.STRING) return paramScenarioClassName.value;
		}
		// We wait a Integer
		else if(paramJavaClassName.equals("java.lang.Integer") || paramJavaClassName.equals("int") || paramJavaClassName.equals("long")) 
		{
			if(paramScenarioClassName.type==Parameter.INTEGER) 
				return paramScenarioClassName.value;
			else if(paramScenarioClassName.type==Parameter.REFERENCE) 
			{
				EntitiesDictionnaryItem item=extractVariableFromDictionnary((String)paramScenarioClassName.value);
				return new Integer(item.id);
			}
		}
		// We wait a Float
		else if(paramJavaClassName.equals("java.lang.Float") || paramJavaClassName.equals("float")) 
		{
			if(paramScenarioClassName.type==Parameter.DOUBLE) 
				return new Float(((Double)paramScenarioClassName.value).floatValue());
			else if(paramScenarioClassName.type==Parameter.INTEGER)
				return new Float(((Integer)paramScenarioClassName.value).floatValue());
		}
		// We wait a Double
		else if(paramJavaClassName.equals("java.lang.Double") || paramJavaClassName.equals("double")) 
		{
			if(paramScenarioClassName.type==Parameter.DOUBLE)  
				return paramScenarioClassName.value;
			else if (paramScenarioClassName.type==Parameter.INTEGER)
				return new Double(((Integer)paramScenarioClassName.value).doubleValue());
		}
		else	
			throw new ClassNotFoundException("Possible reason of none-matching : class "+paramJavaClassName+" is not processable");

		return null;
	}



	/**
	 * try to find a variable by this name in the dictionnary
	 * @param varName the name of the variable
	 * @return the dictionnary item associated with the variable name
	 * @throws VariableNotFoundException
	 */
	public EntitiesDictionnaryItem extractVariableFromDictionnary(String varName) throws VariableNotFoundException 
	{
		EntitiesDictionnaryItem dictionnaryItem;
		ListIterator<EntitiesDictionnaryItem> iter = dictionnary.listIterator();
		//System.out.println("DICTIONNAIRE SIZE = "+dictionnary.size());
		while(iter.hasNext())
		{
			dictionnaryItem=iter.next();
			//System.out.println("VARIABLE="+varName+"    INSPECTEE="+dictionnaryItem.name);
			if(dictionnaryItem.name.equals(varName)) return dictionnaryItem;

		}

		throw new VariableNotFoundException(varName);
	}

}