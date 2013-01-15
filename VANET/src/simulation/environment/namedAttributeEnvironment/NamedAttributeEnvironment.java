package simulation.environment.namedAttributeEnvironment;

import java.util.HashMap;

import simulation.views.environment.EnvironmentViewInterface;
import simulation.environment.Environment;
import simulation.utils.IntegerPosition;

/**
 * Model of an environment where measure are named
 * @author JPeG
 *
 */
public abstract class NamedAttributeEnvironment extends Environment{

	/** set of named attributes*/
	public HashMap<String,NamedAttribute> attributes;
	
	/** environment view */
	private EnvironmentViewInterface environmentView;

	/** default constructor */
	public NamedAttributeEnvironment(EnvironmentViewInterface environmentView)
	{
		this.attributes=new HashMap<String,NamedAttribute>();
		this.environmentView=environmentView;
	}

	/** attribute getter 
	 * @param attributeName name of the attribute
	 * @return the attribute 
	 */
	public NamedAttribute getAttribute(String attributeName) throws  NamedAttributeNotFoundException
	{
		NamedAttribute nattr= this.attributes.get(attributeName);
		if(nattr==null) 
			throw new NamedAttributeNotFoundException(attributeName);
		else
			return nattr;
	}

	/** 
	 * is the attribute with the given name exists?
	 * @return yes if the attribute exists, else no.
	 */
	public boolean exists(String attributeName)
	{
		return this.attributes.containsKey(attributeName);
	}
	
	
	/** attribute setter 
	 * @param attributeName name of the attribute
	 * @param attribute attribute values
	 */
	public void setAttribute(String attributeName,NamedAttribute attribute) 
	{
		this.attributes.put(attributeName,attribute);
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public abstract Object get(String name, IntegerPosition pos) throws NamedAttributeNotFoundException;

	@Override
	public abstract void set(String name, Object value, IntegerPosition pos) throws NamedAttributeNotFoundException;

	@Override
	public EnvironmentViewInterface getEnvironmentView() {
		// TODO Auto-generated method stub
		return this.environmentView;
	}
}

