package simulation.environment;

import simulation.views.environment.BlankEnvironmentView;
import simulation.views.environment.EnvironmentViewInterface;
import simulation.utils.IntegerPosition;

/**
 * blank simple environment
 * @author JPeG
 */
public class BlankEnvironment extends Environment {

	/** view associate to the environment */
	private EnvironmentViewInterface environmentView;


	/**
	 * parametrized constructor 
	 * @param width width of the environment
	 * @param height height of the environment
	 * @param environmentView  view associated to the environment
	 */
	public BlankEnvironment(int width,int height,EnvironmentViewInterface environmentView)
	{
		this.environmentView=environmentView;
	}

	/**
	 * Parameterized constructor 
	 * @param width width of the environment
	 * @param height height of the environment
	 */
	public BlankEnvironment(int width,int height)
	{
		this(width,height,new BlankEnvironmentView(width,height));
	}

	/**
	 * returns the string representation of this object 
	 */
	public String toString()
	{
		return "Blank environment  [no input, no output]";
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
	public synchronized Object get(String name, IntegerPosition pos) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public synchronized void set(String name, Object value, IntegerPosition pos) {
		// TODO Auto-generated method stub
	}

	@Override
	public Object get(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void set(String name, Object value) {
		// TODO Auto-generated method stub
	}

	@Override
	public EnvironmentViewInterface getEnvironmentView() {
		// TODO Auto-generated method stub
		return this.environmentView;
	}

	@Override
	public boolean exists(String attributeName) {
		// TODO Auto-generated method stub
		return false;
	}
}
