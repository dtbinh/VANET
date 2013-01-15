package simulation.views.IHM;

import simulation.views.entity.basic.AgentViewEnumType;
import simulation.utils.Dimension;
import simulation.utils.IntegerPosition;

public interface IHMViewParametersInterface {

	public IntegerPosition getOffset();
	public Dimension getViewAreaDimension();
	public int getLayer();
	public double getScale();
	
	
	// voir si vraiment utile
	public boolean mustUpdateAgentsViewModel();
	public void agentsViewModelUpdated();
	public boolean isVisibleEnvironmentLayerView();
	public boolean isVisibleAgentlayerView();
	public boolean isVisibleEnergylayerView();
	public boolean isVisibleLinkLayerView();
	public boolean isVisibleTextLayerView();
	public boolean isVisibleUserView(int userViewId);
	
	public AgentViewEnumType getTypeOfAgentViews();
}
