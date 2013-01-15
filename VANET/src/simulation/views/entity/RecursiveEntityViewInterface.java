package simulation.views.entity;

import java.awt.image.BufferedImage;
import java.util.Vector;

import simulation.views.InexistantLevelGraphicalViewException;
import simulation.views.entity.basic.BasicView;

public interface RecursiveEntityViewInterface// extends EntityViewInterface
{
	public abstract BufferedImage graphicalView(double zoom,BasicView view, boolean noText,int layer) throws InexistantLevelGraphicalViewException;
	public abstract BufferedImage graphicalView(double zoom,BasicView view, int layer) throws InexistantLevelGraphicalViewException;
	public abstract int getRole(int layer);
	public abstract Vector<Integer> getViewableLinkedNeighboorsUserIdentifier(int layer);
	
	/**
	public int getElementaryId();
	
	public int getLayerId(int i);
	
	public int getNbOfLayer();
	*/
}
