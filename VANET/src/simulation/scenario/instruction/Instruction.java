package simulation.scenario.instruction;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Vector;

import simulation.scenario.ScenarioNotUnderstandedLineException;

/** models an instruction of the scenario 
 * @author Jean-Paul Jamont
 */
public class Instruction {

	/** the concerned entity*/
	public String entity;

	/** parametrized constructor 
	 * @param entity the entity name
	 */
	public Instruction(String entity)
	{
		this.entity=entity;
	}
	
	/**
	 * Allows to create an Instruction object from a line of the scenario
	 * @param line a line of the scenario
	 * @param varNames the list of the defined variables (defined in the scenario)
	 * @return the instruction
	 * @throws ScenarioNotUnderstandedLineException
	 */
	public static Instruction createInstruction(String line) throws ScenarioNotUnderstandedLineException
	{
		return Instruction.createInstruction(line,new LinkedList<String>());
	}
	public static Instruction createInstruction(String line,LinkedList<String> varNames) throws ScenarioNotUnderstandedLineException
	{
		//System.out.println(line);
		boolean isCall=true;
		// Que deux possibilités : une création d'objet/agent ou un appel de méthode sur ce dernier

		if(line.contains("=new "))
		{
			String[] tab = line.split("=new ");
			if (tab[0].contains("."))
				isCall=true;
			else
			{
				for(int i=2;i<tab.length;i++) tab[1]+=("=new "+tab[i]);
				String varName = tab[0];
				if(containsIllegalCaractere(varName)) throw new ScenarioNotUnderstandedLineException(line,"the line contains illegal characteres");
				tab=tab[1].split("\\(");
				String className = tab[0];
				Vector<Parameter>  paramLst = createParametersList(line,"("+tab[1],varNames);
				isCall=false;
				return new CreationInstruction(varName,className,paramLst);
			}
		}
		if (isCall)
		{
			System.out.print("   L'instruction est un call  ");
			String[] tab = line.split("\\.");
			if (tab.length<=1) throw new ScenarioNotUnderstandedLineException(line,"the suspicious call of method");
			for(int i=2;i<tab.length;i++) tab[1]+=("."+tab[i]);
			String varName = tab[0];	
			if(containsIllegalCaractere(varName)) throw new ScenarioNotUnderstandedLineException(line,"the line contains illegal characteres");
			tab=tab[1].split("\\(");
			String methodName = tab[0];
			Vector<Parameter>  paramLst = createParametersList(line,"("+tab[1],varNames);
			return new CallInstruction(varName,methodName,paramLst);

		}

		return null;
	}

	/**
	 * Allows to create the parameter list of an instruction
	 * @param param the instruction substring which contains the parameters
	 * @param varNames the list of the defined variables (defined in the scenario)
	 * @return a vector which contains the parameters of the instruction
	 * @throws ScenarioNotUnderstandedLineException
	 */
	public static Vector<Parameter>  createParametersList(String line,String param,LinkedList<String> varNames) throws ScenarioNotUnderstandedLineException
	{
		Vector<Parameter> v = new Vector<Parameter>();

		int strLength = param.length();

		if((strLength<=2) || param.charAt(0)!='(' || !param.endsWith(");")) throw new ScenarioNotUnderstandedLineException(param,"the suspicious parameters list format");
		param=param.substring(1,strLength-1);
		System.out.println("PARAM===="+param);
		strLength = param.length();
		int iStart=0; int iEnd;	 String elt;	boolean ok;	char c;
		boolean openQuote = false;
		for(int i=0;i<strLength;i++)
		{
			c=param.charAt(i);
			if(c=='"') openQuote=!openQuote;
			if((c==',' && !openQuote) || i==strLength-1)
			{
				if(c==',')
					iEnd=i;	// On est sur une ,
				else
					iEnd=i;		// On est en fin de chaine
				ok=false;
				elt=param.substring(iStart,iEnd);
				try{v.add(new Parameter(Parameter.INTEGER,new Integer(Integer.parseInt(elt))));ok=true;}catch(Exception e){}
				if (!ok) try{v.add(new Parameter(Parameter.DOUBLE,new Double(Double.parseDouble(elt))));ok=true;}catch(Exception e){}
				if ((!ok) && (elt.length()>=2) && elt.charAt(0)=='"' && elt.charAt(elt.length()-1)=='"') {v.add(new Parameter(Parameter.STRING,new String(elt.substring(1,elt.length()-1)))); ok=true;}
				if ((!ok) && varNameExists(elt,varNames)) {v.add(new Parameter(Parameter.REFERENCE,new String(elt))); ok=true;}
				if ((!ok) && c==')') {ok=true;}
				if(!ok) throw new ScenarioNotUnderstandedLineException(line,"the parameters list  <<"+param+">> is suspicious");
				iStart=i+1;
			}
		}

		//System.out.println("TRAITEMENT DE "+param);
		//for(int i=0;i<v.size();i++) System.out.println(v.get(i));
		return v;
	}

	/**
	 * allows to know if a variable exists
	 * @param elt the variable name
	 * @param varNames the list of the defined variables (defined in the scenario)
	 * @return true if the variable exists, false else.
	 */
	private static boolean varNameExists(String elt,LinkedList<String> varNames) {
		ListIterator<String> iter=varNames.listIterator();
		while(iter.hasNext())
		{
			if (elt.equals(iter.next())) return true;
		}
		return false;
	}

	/** 
	 * Allows to know if the character (of a var name) contains a illegal caractere
	 * @param c the inspected caractere
	 * @return true is the caractere is illegal
	 */
	public  static boolean containsIllegalCaractere(char c)
	{
		if ((c>='a'&&c<='z')||(c>='A'&&c<='Z')||(c>='0'&&c<='9')||(c=='_')) return false;
		return true;
	}

	/** 
	 * Allows to know if the string (the var name) contains  illegals caracteres
	 * @param varName the inspected var name
	 * @return true is one or more illegal charactere has been found
	 */
	public static  boolean containsIllegalCaractere(String varName)
	{
		int size = varName.length();
		for(int i=0;i<size;i++)
			if (containsIllegalCaractere(varName.charAt(i))) return true;
		return false;
	}
}
