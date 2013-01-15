package simulation.entities;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Vector;


import simulation.messages.*;
import simulation.multiagentSystem.MAS;
import simulation.multiagentSystem.ObjectSystemIdentifier;
import simulation.multiagentSystem.SimulatedObject;
import simulation.views.ViewableEntityInterface;
import simulation.views.entity.BasicViewEntityViewInterface;
import simulation.views.entity.EntityViewInterface;
import simulation.views.entity.basic.BasicView;
import simulation.views.entity.basic.CircleView;
import simulation.battery.BatteryLevelReader;
import simulation.battery.BatteryModel;
import simulation.environment.AttributeNotFoundException;
import simulation.events.*;
import simulation.events.system.ColorModificationEvent;
import simulation.events.system.ObjectCreationEvent;
import simulation.events.system.PositionModificationEvent;
import simulation.events.system.RangeModificationEvent;
import simulation.events.system.ReceivedFrameEvent;
import simulation.events.system.SendedFrameEvent;
import simulation.events.system.UserIdentifierModificationEvent;
import simulation.scenario.CompatibilityResult;
import simulation.scenario.MethodNotFoundException;
import simulation.scenario.ScenarioNotUnderstandedLineException;
import simulation.scenario.VariableNotFoundException;
import simulation.scenario.instruction.CallInstruction;
import simulation.scenario.instruction.Instruction;
import simulation.scenario.instruction.Parameter;
import simulation.utils.*;

/**
 * An object of the environment multiagent system
 * @author Jean-Paul Jamont
 */
public class Object implements ViewableEntityInterface,BasicViewEntityViewInterface{

	/** default initial ratio of available energy */
	final public static double DEFAULT_INITIAL_ENERGY_VALUE = 1;
	
	/** default initial range */
	final public static int DEFAULT_INITIAL_RANGE = 60;
	
	/** default initial bitrate (to compute energy consumption) */
	final public static int BITRATE = 250000;	// 250 kbit/s
	/** default initial byterate */
	final public static double ms_BYTERATE = ((BITRATE/8.0)/1000.0);

	/* Attributes */


	/** Identifier of the object */
	private ObjectSystemIdentifier system_id;
	/** Identifier of the object */
	private int user_id;
	/** Transmission range of the object */
	private int range;
	/** A reference to the multiagent system */
	private MAS mas;
	/** Is the object spyied*/
	private boolean spyied;
	/** Color of the object */
	private Color color;
	/** Battery model */
	private BatteryModel battery;
	/** Object view */
	private EntityViewInterface nativeEntityView;

	/** Constructor
	 * 
	 * @param mas the multiagent system where the object is included 
	 * @param id identifier of the object
	 * @param range transmission range of the object
	 */
	public Object(MAS mas,int id, Integer range,Color color,EntityViewInterface entityView)
	{
		
		if(mas!=null)
			this.system_id=new ObjectSystemIdentifier();
		else
			// This agent is an abstract agent (not managed by the mas)
			this.system_id=new ObjectSystemIdentifier(id);
			
		this.mas=mas;
		this.user_id=id;
		this.range=range;
		this.spyied=false;
		this.color=color;
		this.battery=null;
		this.nativeEntityView=entityView;

		if (mas!=null) 
		{
			mas.notifyEvent(new ObjectCreationEvent(this.getSystemId(),this.user_id));
			mas.notifyEvent(new RangeModificationEvent(this.getSystemId(),range));
		}
	}

	/** Constructor
	 * 
	 * @param mas the multiagent system where the object is included 
	 * @param id identifier of the object
	 * @param range transmission range of the object
	 */
	public Object(MAS mas,int id, Integer range,Color color)
	{
		this(mas,id,range,color,new CircleView(""+id));
	}

	/** Constructor
	 * 
	 * @param mas the multiagent system where the object is included 
	 * @param id identifier of the object
	 * @param energy energy of the object
	 * @param range transmission range of the object
	 */
	public Object(MAS mas,int id,Integer range)
	{
		this(mas,id,range,Color.LIGHT_GRAY);
	}
	public Object(int id,Integer range)
	{
		this(null,id,range,Color.LIGHT_GRAY);
	}
	
	/** returns the object identifier 
	 * @return the identifier
	 */
	public ObjectSystemIdentifier getSystemId()
	{
		return this.system_id;
	}

	/** returns the object identifier 
	 * @return the identifier
	 */
	public int getUserId()
	{
		return this.user_id;
	}

	/** returns the object identifier 
	 * @return the identifier
	 */
	public void setUserId(int new_user_id)
	{
		if(new_user_id!=this.user_id) 
		{
			this.notifyEvent(new UserIdentifierModificationEvent(this.getSystemId(),user_id,new_user_id));
			this.user_id = new_user_id;
		}

	}

	/** returns the transmission range of the object
	 * @return the transmission range
	 */
	public int getRange()
	{
		return this.range;
	}

	public float pourcentOfAvailableEnergy()
	{
		if(this.battery==null)
			return 100;
		else
			return this.battery.pourcentOfAvailableEnergy();
	}

	/** returns a reference to the multiagent system
	 * @return a reference to the multiagent system
	 */
	public MAS getMAS()
	{
		return this.mas;
	}

	/**
	 * return the battery of this object
	 * @return the battery
	 */
	public BatteryLevelReader getBattery()
	{
		return this.battery;
	}

	/**
	 * get the position of the object
	 * @return position of the object
	 */
	public IntegerPosition getPosition()
	{
		return this.mas.getSimulatedObject(this.getSystemId()).getPosition();
	}

	/**
	 * get the position of the object
	 * @return position of the object
	 */
	public void setPosition(int x,int y)
	{
		this.setPosition(new IntegerPosition(x,y));
	}

	public void setPosition(IntegerPosition p)
	{
		this.notifyEvent(new PositionModificationEvent(this.getSystemId(),p));
		this.mas.getSimulatedObject(this.getSystemId()).setPosition(p);
	}

	/**
	 * set the battery of this object
	 * set the battery of this object
	 */
	public void setBattery(BatteryModel batteryModel)
	{
		this.battery=batteryModel;
	}


	/** sets the transmission range of the object
	 * @param newRange the transmission range
	 */
	public void setRange(int newRange)
	{
		if (range!=newRange)
		{
			this.range=newRange;
			mas.notifyEvent(new RangeModificationEvent(this.getSystemId(),this.range));
		}
	}



	/** allows to know the color of the object
	 * @return the color of the object
	 */
	public Color getColor()
	{
		return this.color;
	}

	/** sets the color of the object
	 * @param c the color
	 */
	public void setColor(Color c)
	{
		if(!c.equals(this.color))
		{
			if(this.nativeEntityView instanceof BasicView)
			{
				((BasicView) this.nativeEntityView).setBackgroundColor(c);
			}

			this.color=c;
			if (mas!=null)
				mas.notifyEvent(new ColorModificationEvent(this.getSystemId(),c));
			// else
			//	System.err.println("Null MAS reference in Simulation.Entities.Object.notifyEvent");
			
		}
	}

	/**
	 * set an environment attribute
	 * @param name name of the environment attribute
	 * @param value value of this attribute
	 */
	public void setEnvironmentAttribute(String name,java.lang.Object value) throws AttributeNotFoundException
	{
		this.mas.setEnvironmentAttribute(this,name,value);
	}

	/**
	 * get the value of an environment attribute
	 * @param name name of the environment attribute
	 */
	public java.lang.Object getEnvironmentAttribute(String name) throws AttributeNotFoundException
	{
		return this.mas.getEnvironmentAttribute(this,name);
	}


	/** allows to an object to send a message
	 * @param m the message to send
	 */
	public   void sendFrame(Frame frame)
	{
		
		//System.out.println("\n"+this.getSystemId()+"##########################A");
		if (this.battery!=null) this.battery.withdrawEnergyConsumption(BatteryModel.STATE_SENDING, (int) (Object.ms_BYTERATE*((double)frame.getVolume())) );
		//System.out.println("\n"+this.getSystemId()+"##########################B");

		mas.notifyEvent(new SendedFrameEvent(this.getSystemId(),(Frame) frame.clone()) );
		//System.out.println("\n"+this.getSystemId()+"##########################C");
		try
		{
			//System.out.println("\n"+this.getSystemId()+"##########################D");

			mas.dispatchFrame(this.system_id,frame);
			//System.out.println("\n"+this.getSystemId()+"##########################E");

		}
		catch(java.lang.IndexOutOfBoundsException e)
		{
			System.out.println("Frame not sended (simulation finished?)");
		}
		//System.out.println("\n@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

	}


	/** allows to an object to receive a message
	 * @param m the received message
	 */
	public void receivedFrame(Frame frame)
	{
		if (this.battery!=null) this.battery.withdrawEnergyConsumption(BatteryModel.STATE_RECEIVING, (int) (Object.ms_BYTERATE*((double)frame.getVolume())) );
		if (MAS.NOTIFY_FRAME_RECEIVED_EVENT) mas.notifyEvent(new ReceivedFrameEvent(this.getSystemId(),(Frame) frame.clone()) );


	}

	/**
	 *  notify an event
	 * @param evt the event which must be notified
	 */
	public void notifyEvent(Event evt)
	{
		if(mas!=null) 
			mas.notifyEvent(evt);
		//else
		//	System.err.println("Null MAS reference in Simulation.Entities.Object.notifyEvent");
	}


	/** returns the string signature of the object
	 * @return the string signature of the object
	 */
	public String toString()
	{
		return "Object #"+this.user_id+":  energy="+String.format("%.2f",this.battery.pourcentOfAvailableEnergy())+"  range="+this.range;
	}

	/** Allows to set/unset the spyied attribute 
	 * <it>RESERVED</it>
	 * @param isSpyied is the object spyied or not
	 */
	public void spyied(boolean isSpyied)
	{
		this.spyied=isSpyied;
	}

	/** Allows to know if the object is or not spyied by the simulator (a window allow to know the state of this object)
	 * @return is the object spyied or not 
	 */
	public boolean isSpyied()
	{
		return this.spyied;
	}	

	/** returns the string signature of the agent which will be viewable under the spy windows
	 * @return the string signature of the agent
	 */
	public String toSpyWindows()
	{
		return "Object #"+this.system_id;
	}

	/**
	 * sleep
	 * @param ms
	 * @throws InterruptedException 
	 */
	public void sleep(int ms) 
	{
		try 
		{
			if (this.battery!=null) this.battery.withdrawEnergyConsumption(BatteryModel.STATE_SLEEP,ms);
			Thread.sleep(ms);
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
	}

	/**
	 * enable execution of a method of this object 
	 * @param cmd the method and its arguments that we must call on this object
	 */
	public void executeCallOfMethods(String cmd) {
		// TODO Auto-generated method stub
		System.out.println("\nAGENT #"+this.getSystemId()+"/"+this.getUserId()+" try to execute "+cmd);
		this.executeCallOfMethods(cmd.split("\r\n"));
	}
	/**
	 * enable execution of a list of methods which must be applied on this object
	 * @param cmds
	 */
	public void executeCallOfMethods(String[] cmds) 
	{
		Instruction instruction;
		for(int i=0;i<cmds.length;i++)
		{
			try {
				System.out.println("\nExecute "+cmds[i]);
				instruction=Instruction.createInstruction(cmds[i]);
				exec_callInstruction((CallInstruction) instruction);
			} 
			catch (ScenarioNotUnderstandedLineException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (VariableNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MethodNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * return a copy of the simulated objects (and agents) included in the mas
	 * @return the array of simulated objetcs
	 */
	public SimulatedObject[] getMASObjectArray()
	{
		return this.mas.getMASObjectArray();
	}
	
	/**
	 * primitive to call an instruction i.e. a call of method
	 * @param instruction
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
		// String variableName = instruction.entity;

		Method[] tabMethods = this.getClass().getMethods();

		for(int j=0;j<tabMethods.length;j++)
		{
			if(instruction.method.equals(tabMethods[j].getName()))
			{
				param=compatibleMethods(tabMethods[j].getParameterTypes(),instruction.param);

				if (param.res) 
				{
					// On a le bon constructeur
					//System.out.println("!!! La méthode COMPATIBLE est "+tabMethods[j]);
					tabMethods[j].invoke(this,param.param);
					//main.scenarioEntityAddition(id,((Integer)instruction.param.get(0).value),((Integer)instruction.param.get(1).value),elt.obj);
				}
				else 
				{
					//System.out.println("Constructeur non compatible \n"+tabMethods[j]+"\n"+instruction.param);
				}
			}
		}
		if (!param.res) throw new MethodNotFoundException(this.getClass().getName(),instruction.method,instruction.param.toString());

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
				//EntitiesDictionnaryItem item=extractVariableFromDictionnary((String)paramScenarioClassName.value);
				//return new Integer(item.id);
				System.out.println("\nSTRANGE!!!!");
				return new Integer(-1);
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
	 * set the native view (i.e. default view) of this object
	 * @param nativeView the native view
	 */
	public void setNativeView(EntityViewInterface nativeView)
	{
		this.nativeEntityView=nativeView;
	}

	@Override
	public EntityViewInterface getView() {
		// TODO Auto-generated method stub
		return this.nativeEntityView;
	}

	@Override
	public BufferedImage graphicalView(double zoom, BasicView view, boolean noText) {
		view.setText(""+this.system_id);
		view.setBackgroundColor(this.color);
		view.setTextColor(Color.BLACK);
		return view.graphicalView(zoom,noText);
	}

	@Override
	public BufferedImage graphicalView(double zoom, BasicView view) {
		// TODO Auto-generated method stub
		return this.graphicalView(zoom, view, false);
	}





}
