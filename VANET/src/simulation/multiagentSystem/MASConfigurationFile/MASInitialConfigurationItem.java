package simulation.multiagentSystem.MASConfigurationFile;

import simulation.utils.IntegerPosition;

public class MASInitialConfigurationItem {

	/** identifier offset */ 
	private final static int ID_OFFSET = 0;
	/** coordinate offset */ 
	private final static int X_COORDINATE_OFFSET = 1;
	/** coordinate offset */ 
	private final static int Y_COORDINATE_OFFSET = 2;
	/** energy offset */ 
	private final static int ENERGY_OFFSET = 3;
	/** range offset */ 
	private final static int RANGE_OFFSET = 4;
	/** isAgent offset */ 
	private final static int IS_AGENT_OFFSET = 5;
	/** solution name offset */ 
	private final static int SOLUTION_NAME_OFFSET = 6;
	/** solution version offset */ 
	private final static int SOLUTION_VERSION_OFFSET = 7;
	/** object class name offset */ 
	private final static int OBJECT_CLASS_NAME_OFFSET = 8;
	/** object id offset */ 
	private final static int OBJECT_ID_OFFSET = 9;

	/** user_id of the object */
	public int id;
	/** initial coordinate of the agent */
	public IntegerPosition coordinate;
	/** range of the object */
	public int range;
	/** % of available energy of the object */
	public float energy;
	/** is this item an agent or an object */
	public boolean isAgent;
	/** name of the solution where is contained the object code */
	public String solutionName;
	/** version of the solution */
	public String version;
	/** name of the class of this object */
	public String objectClassName;
	/** id of this class (used when the user want use a project with another solution) */
	public int objectId;

	public MASInitialConfigurationItem(String item)
	{
		int i=0;
		String[] args = item.split("\t");
		if(args.length>=5)
		{
			this.id=Integer.parseInt(args[MASInitialConfigurationItem.ID_OFFSET]);
			//System.out.println("##id## "+this.id);
			this.coordinate=new IntegerPosition(Integer.parseInt(args[MASInitialConfigurationItem.X_COORDINATE_OFFSET]),Integer.parseInt(args[MASInitialConfigurationItem.Y_COORDINATE_OFFSET]));
			//System.out.println("##coordinate## "+this.coordinate);
			this.energy=Float.parseFloat(args[MASInitialConfigurationItem.ENERGY_OFFSET]);
			//System.out.println("##energy## "+this.energy);
			this.range=Integer.parseInt(args[MASInitialConfigurationItem.RANGE_OFFSET]);
			//System.out.println("##range## "+this.range);
			if(args.length>=9)
			{
				this.isAgent=Boolean.parseBoolean(args[MASInitialConfigurationItem.IS_AGENT_OFFSET]);
				//System.out.println("##isAgent## "+this.isAgent);
				this.solutionName=args[MASInitialConfigurationItem.SOLUTION_NAME_OFFSET];
				//System.out.println("##solutionName## "+this.solutionName);
				this.version=args[MASInitialConfigurationItem.SOLUTION_VERSION_OFFSET];
				//System.out.println("##version## "+this.solutionName);
				this.objectClassName=args[MASInitialConfigurationItem.OBJECT_CLASS_NAME_OFFSET];
				//System.out.println("##objectClassName## "+this.objectClassName);
				this.objectId=Integer.parseInt(args[MASInitialConfigurationItem.OBJECT_ID_OFFSET]);
				//System.out.println("##objectId## "+this.objectId);
				
			}
			//System.out.println("**"+i++);

		}
		//System.out.println("**"+i++);
		//System.out.println("End of analyzing");
	}

	public MASInitialConfigurationItem(int id, IntegerPosition p, float energy, int range)
	{
		this(id,p,energy,range,true,"","",1);
	}
	public MASInitialConfigurationItem(int id, IntegerPosition p, float energy, int range,boolean isAgent,String solutionName,String objectClassName,int objectId)
	{
		this(id,p.x,p.y,energy,range,isAgent,solutionName,objectClassName,objectId);
	}
	public MASInitialConfigurationItem(int id, int x, int y, float energy, int range,boolean isAgent,String solutionName,String objectClassName,int objectId)
	{
		this.id=id;
		this.coordinate=new IntegerPosition(x,y);
		this.energy=energy;
		this.range=range;
		this.isAgent=isAgent;
		this.solutionName=solutionName;
		this.objectClassName=objectClassName;
		this.objectId=objectId;
	}


	public String toSavedTextLine()
	{
		return this.id+"\t"+this.coordinate.x+"\t"+this.coordinate.y+"\t"+this.energy+"\t"+this.range+"\t"+this.isAgent+"\t"+this.solutionName+"\t"+this.objectClassName+"\t"+this.objectId;
	}
	public String toString()
	{
		if(this.isAgent)
			return "Agent "+this.objectClassName+" #"+this.id+" in "+this.coordinate.toString();
		else
			return "Object "+this.objectClassName+" #"+this.id+" in "+this.coordinate.toString();
			
	}
}
