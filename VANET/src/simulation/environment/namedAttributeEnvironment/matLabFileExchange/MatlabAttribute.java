package simulation.environment.namedAttributeEnvironment.matLabFileExchange;

import simulation.environment.namedAttributeEnvironment.NamedAttribute;

/**
 * Attribute of a data in a standard matlab XML named environment
 * @author JPeG
 */
public class MatlabAttribute extends NamedAttribute{

	/** error in the type of the attribute value */
	public final static int ATTRIBUTE_TYPE_ERROR 	= -1;
	/** the type of the attribute value is INTEGER */
	public final static int ATTRIBUTE_TYPE_INTEGER 	= 0;
	/** the type of the attribute value is FLOAT */
	public final static int ATTRIBUTE_TYPE_FLOAT 	= 1;
	/** the type of the attribute value is STRING */
	public final static int ATTRIBUTE_TYPE_STRING 	= 2;

	/** error in the unit of the attribute value*/
	public final static int ATTRIBUTE_UNIT_ERROR 					= -1;
	/** the unit of the attribute value is °C*/
	public final static int ATTRIBUTE_UNIT_CELCIUS			 		= 0;
	/** the unit of the attribute value is seconds*/
	public final static int ATTRIBUTE_UNIT_SECOND 					= 1;
	/** the unit of the attribute value is m^3.s^-1 */
	public final static int ATTRIBUTE_UNIT_FLOW 					= 2;

	/** error in the attribute direction*/
	public final static int ATTRIBUTE_DIRECTION_ERROR 				= -1;
	/** the attribute direction is IN*/
	public final static int ATTRIBUTE_DIRECTION_IN			 		= 0;
	/** the attribute direction is OUT*/
	public final static int ATTRIBUTE_DIRECTION_OUT 				= 1;
	/** the attribute direction is INOUT*/
	public final static int ATTRIBUTE_DIRECTION_INOUT 				= 2;


	/** type of the attribute (ex: Temperature)*/
	private int type;
	/** location of the attribute*/
	private String location;
	/** value of the attribute (ex: 12.5)*/
	private Object value;
	/** unit of the attribute (ex: Celcius)*/
	private int unit;
	/** description concerning the attribute (ex: outside temperature)*/
	private String description;
	/** attribute direction (ex: in, out, inout from the environment simulator i.e. matlab) */
	private int direction;






	/**
	 * Definition of a matlab attribute
	 * @param name name of the created attribute
	 * @param type type of the created attribute
	 * @param value value of the created attribute
	 * @param unit unit of the created attribute
	 * @param description description concerning the created attribute
	 * @param inout direction of the created attribute  
	 */
	public MatlabAttribute(String name,int type, Object value, int unit, String location, String description,int direction)
	{
		super(name);
		this.setType(type);
		this.setValue(value);
		this.setUnit(unit);
		this.setLocation(location);
		this.setDescription(description);
		this.setDirection(direction);
	}
	/**
	 * Definition of a matlab attribute
	 * @param name name of the created attribute
	 * @param type type of the created attribute
	 * @param value value of the created attribute
	 * @param unit unit of the created attribute
	 * @param description description concerning the created attribute
	 * @param inout direction of the created attribute  
	 */
	public MatlabAttribute(String name,String type, String value, String unit, String location, String description,String direction)
	{
		super(name);
		this.setType(type);
		this.setValue(value);
		this.setUnit(unit);
		this.setLocation(location);
		this.setDescription(description);
		this.setDirection(direction);
	}

	/**
	 * set the type of the named attribute 
	 * @param value the type under string representation
	 */
	public void setType(String value)
	{
		String val = value.toUpperCase();
		if(val.equals("INTEGER") || val.equals("INT"))
			this.setType(MatlabAttribute.ATTRIBUTE_TYPE_INTEGER);
		else if(val.equals("FLOAT"))
			this.setType(MatlabAttribute.ATTRIBUTE_TYPE_FLOAT);
		else if(val.equals("STRING"))
			this.setType(MatlabAttribute.ATTRIBUTE_TYPE_STRING);
		else 
			this.setType(MatlabAttribute.ATTRIBUTE_TYPE_ERROR);
		
	}

	/**
	 * set the type of the named attribute 
	 * @param value the type 
	 */
	public void setType(int value)
	{	
		switch(value)
		{
		case MatlabAttribute.ATTRIBUTE_TYPE_INTEGER:
		case MatlabAttribute.ATTRIBUTE_TYPE_FLOAT:
		case MatlabAttribute.ATTRIBUTE_TYPE_STRING:
			this.type=value;
			break;
		default:
			this.type=MatlabAttribute.ATTRIBUTE_TYPE_ERROR;
		}
	}

	/**
	 * set the type of the named attribute 
	 * @param value the type under string representation
	 */
	public void setValue(String value)
	{
		switch(this.type)
		{
		case MatlabAttribute.ATTRIBUTE_TYPE_INTEGER:
			this.setValue(new Integer(value));
			break;
		case MatlabAttribute.ATTRIBUTE_TYPE_FLOAT:
			this.setValue(new Float(value));
			break;
		case MatlabAttribute.ATTRIBUTE_TYPE_STRING:
			this.setValue((Object)value);
			break;
		default:
			this.setValue((Object)null);
		}
	}

	/**
	 * set the type of the named attribute 
	 * @param value the type under string representation
	 */
	public void setValue(Object value)
	{
		this.value=value;
	}

	/**
	 * set the type of the named attribute 
	 * @param value the type under string representation
	 */
	public void setUnit(String value)
	{
		String val = value.toUpperCase();
		if(val.equals("CELCIUS"))
			this.setUnit(MatlabAttribute.ATTRIBUTE_UNIT_CELCIUS);
		else if(val.equals("SECOND"))
			this.setUnit(MatlabAttribute.ATTRIBUTE_UNIT_SECOND);
		else 
			this.setUnit(MatlabAttribute.ATTRIBUTE_UNIT_ERROR);
		
	}

	/**
	 * set the unit of the named attribute 
	 * @param value the unit
	 */
	public void setUnit(int value)
	{	
		switch(value)
		{
		case MatlabAttribute.ATTRIBUTE_UNIT_CELCIUS:
		case MatlabAttribute.ATTRIBUTE_UNIT_SECOND:
			this.unit=value;
			break;
		default:
			this.unit=MatlabAttribute.ATTRIBUTE_UNIT_ERROR;
		}
	}
	
	/**
	 * set the unit of the named attribute 
	 * @param value the unit
	 */
	public void setLocation(String value)
	{	
		this.location=value;
	}

	/**
	 * set the type of the named attribute 
	 * @param value the type under string representation
	 */
	public void setDescription(String value)
	{	
		this.description=value;
	}
	/**
	 * set the type of the named attribute 
	 * @param value the type under string representation
	 */
	public void setDirection(String value)
	{
		String val = value.toUpperCase();
		if(val.equals("IN"))
			this.setDirection(MatlabAttribute.ATTRIBUTE_DIRECTION_IN);
		else if(val.equals("OUT"))
			this.setDirection(MatlabAttribute.ATTRIBUTE_DIRECTION_OUT);
		else if(val.equals("INOUT"))
			this.setDirection(MatlabAttribute.ATTRIBUTE_DIRECTION_INOUT);
		else 
			this.setDirection(MatlabAttribute.ATTRIBUTE_DIRECTION_ERROR);
	}

	/**
	 * set the direction of the named attribute 
	 * @param value the direction
	 */
	public void setDirection(int value)
	{	
		switch(value)
		{
		case MatlabAttribute.ATTRIBUTE_DIRECTION_IN:
		case MatlabAttribute.ATTRIBUTE_DIRECTION_OUT:
		case MatlabAttribute.ATTRIBUTE_DIRECTION_INOUT:
			this.type=value;
			break;
		default:
			this.type=MatlabAttribute.ATTRIBUTE_DIRECTION_ERROR;
		}
	}

	/**
	 * get the type of the attribute
	 * @return the type
	 */
	public int getType()
	{	
		return this.type;
	}

	/**
	 * get the type of the attribute
	 * @return the type
	 */
	public Object getValue()
	{
		return this.value;
	}

	/**
	 * get the type of the attribute
	 * @return the type
	 */
	public int getUnit()
	{	
		return this.unit;
	}

	/**
	 * get the type of the attribute
	 * @return the type
	 */
	public String getDescription()
	{	
		return this.description;
	}

	/**
	 * get the direction of the attribute
	 * @return the direction
	 */
	public int getDirection()
	{	
		return this.direction;
	}

	/**
	 * get the location of the attribute
	 * @return the location
	 */
	public String getLocation()
	{	
		return this.location;
	}
	
	/**
	 * return a string representation of the type of the attribute
	 * @return the string representation of the type
	 */
	public String getStringType()
	{
		switch(this.type)
		{
		case MatlabAttribute.ATTRIBUTE_TYPE_FLOAT:	return "float";
		case MatlabAttribute.ATTRIBUTE_TYPE_INTEGER:return "integer";
		case MatlabAttribute.ATTRIBUTE_TYPE_STRING:	return "string";
		default:	return "error";
		}
	}
	
	/**
	 * return a string representation of the unit of the attribute
	 * @return the string representation of the unit
	 */
	public String getStringUnit()
	{
		switch(this.unit)
		{
		case MatlabAttribute.ATTRIBUTE_UNIT_CELCIUS:	return "celcius";
		case MatlabAttribute.ATTRIBUTE_UNIT_SECOND:		return "second";
		case MatlabAttribute.ATTRIBUTE_UNIT_FLOW:		return "m^3.s^-1";
		default:	return "error";
		}
	}
	
	/**
	 * return a string representation of the direction of the attribute
	 * @return the string representation of the direction
	 */
	public String getStringDirection()
	{
		switch(this.direction)
		{
		case MatlabAttribute.ATTRIBUTE_DIRECTION_IN:	return "in";
		case MatlabAttribute.ATTRIBUTE_DIRECTION_OUT:	return "out";
		case MatlabAttribute.ATTRIBUTE_DIRECTION_INOUT:	return "inout";
		default: return "error";
		}
	}
	
	
	/** 
	 * return the attribute under a string representation
	 * @return the string representation
	 */
	public String toString()
	{
		return "Attribute "+this.getName()+" : type="+this.getStringType()+", unit="+this.getStringUnit()+" , direction="+this.getStringDirection()+" , value="+this.getValue()+" , description="+this.getDescription();
	}

	/** 
	 * return the attribute under a XML string representation
	 * @return the XML string representation
	 */
	public String toXML()
	{
		return this.toXML(-1);
	}
	
	/** 
	 * return the attribute under a XML string representation
	 * @param the number of the data (to respect matlab XML flow)
	 * @return the XML string representation
	 */
	public String toXML(int i)
	{
		String res="";
		res+="<data"+(i==-1 ? "" : i)+">\n";
		res+="\t<name>"+this.getName()+"</name>\n";
		res+="\t<type>"+this.getStringType()+"</type>\n";	
		res+="\t<value>"+this.getValue()+"</value>\n";
		res+="\t<unit>"+this.getStringUnit()+"</unit>\n";
		res+="\t<description>"+this.getDescription()+"</description>\n";
		res+="\t<direction>"+this.getStringDirection()+"</direction>\n";
		res+="\t<location>"+this.getLocation()+"</location>\n";
		res+="</data"+(i==-1 ? "" : i)+">\n";
		return res;
	}

}
