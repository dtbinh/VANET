package simulation.environment;

import java.awt.image.BufferedImage;

import simulation.views.environment.EnvironmentViewInterface;
import simulation.entities.Object;
import simulation.utils.IntegerPosition;

/**
 * Implementation of the environment model
 */
public abstract class Environment{

	/** put the environment in pause state */
	public abstract void pause();
	
	/** put the environment in running state (leave the pause state)*/
	public abstract void resume();
	
	/** stop this environment */
	public abstract void stop();
	
	/**
	 * set an attribute value
	 * @param name name of the attribute
	 * @param value value of the attribute
	 * @param pos position of the attribute
	 * @throws AttributeNotFoundException
	 */
	public abstract void set(String name, java.lang.Object value,IntegerPosition pos) throws AttributeNotFoundException;
	
	/**
	 * get an attribute value using its name and its position
	 * @param name name of the attribute
	 * @param pos position of the attribute
	 * @throws AttributeNotFoundException
	 */
	public abstract java.lang.Object get(String name, IntegerPosition pos) throws AttributeNotFoundException;
	
	/**
	 * set an attribute value
	 * @param name name of the attribute
	 * @param value value of the attribute
	 * @throws AttributeNotFoundException
	 */
	public abstract void set(String name, java.lang.Object value) throws AttributeNotFoundException;

	/**
	 * get an attribute value using its name
	 * @param name name of the attribute
	 * @param pos position of the attribute
	 * @throws AttributeNotFoundException
	 */	
	public abstract java.lang.Object get(String name) throws AttributeNotFoundException;;	

	/**
	 * returns the view of this environment
	 * @return the view of this environment
	 */
	public abstract EnvironmentViewInterface getEnvironmentView();
	
	/**
	 * is an environment attribute exists?
	 * @param attributeName the attribute name
	 * @return
	 */
	public abstract boolean exists(String attributeName);
}
