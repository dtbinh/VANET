

import simulation.views.IHM.IHMViewParametersInterface;
import simulation.views.entity.basic.AgentViewEnumType;
import simulation.utils.Dimension;
import simulation.utils.IntegerPosition;
import simulation.utils.aDate;

public class IHM_MASGraphicalRepresentation implements IHMViewParametersInterface{

	private static long TIME_BETWEEN_TWO_GRAPHICAL_VIEW = 100;

	private IntegerPosition offset;
	private Dimension viewAreaDimension;
	private int layer;
	private double scale;
	private boolean updateRequired;
	private boolean waitingRequieredUpdate;

	
	private aDate dateOfFirstUpdateRequirement;

	private boolean environmentView = true;
	private boolean agentView = true;
	private boolean energyView = true;
	private boolean connexionView = true;
	private boolean textView = true;
	private boolean userView1 = true;
	private boolean userView2 = true;
	private boolean userView3 = true;
	private boolean userView4 = true;
	private boolean userView5 = true;
	

	private AgentViewEnumType typeOfAgentViews=AgentViewEnumType.CIRCLE;

	private int viewLength = 8;

	private boolean mustUpdateAgentsViewModel=true;




	public IHM_MASGraphicalRepresentation(IntegerPosition offset,Dimension viewAreaDimension,int layer,double scale)
	{
		this.setParameters(offset, viewAreaDimension, layer, scale);
		this.requireGraphicalUpdate();
	}

	/** set parameters of the view area */
	public void set_TIME_BETWEEN_TWO_GRAPHICAL_VIEW(long time)
	{
		this.TIME_BETWEEN_TWO_GRAPHICAL_VIEW=time;
	}
	
	/** set parameters of the view area */
	public void setParameters(IntegerPosition offset,Dimension viewAreaDimension, int layer, double scale)
	{
		//if(layer==-1) {int i=0/0;}
		this.layer=layer;
		this.offset=offset;
		this.viewAreaDimension=viewAreaDimension;
		this.scale=scale;
	}
	/** set parameters of the objects */
	public void setParameters(String calques, String typeOfView,int length)
	{

		AgentViewEnumType oldTypeOfAgentView = this.typeOfAgentViews;
		if (typeOfView.equals("BMP")) this.typeOfAgentViews=AgentViewEnumType.BMP;
		else if (typeOfView.equals("CIRCLE")) this.typeOfAgentViews=AgentViewEnumType.CIRCLE;
		else if (typeOfView.equals("SQUARE")) this.typeOfAgentViews=AgentViewEnumType.SQUARE;
		else if (typeOfView.equals("CROSS")) this.typeOfAgentViews=AgentViewEnumType.CROSS;
		else if (typeOfView.equals("POINT")) this.typeOfAgentViews=AgentViewEnumType.POINT;
		this.mustUpdateAgentsViewModel= (this.typeOfAgentViews!=oldTypeOfAgentView);

		//System.out.println("ACTU VIEWS => "+calques);
		this.textView=(calques.length()!=10 || calques.charAt(0)=='1');
		this.energyView=(calques.length()!=10 || calques.charAt(1)=='1');
		this.connexionView=(calques.length()!=10 || calques.charAt(2)=='1');
		this.agentView=(calques.length()!=10 || calques.charAt(3)=='1');
		this.environmentView=(calques.length()!=10 || calques.charAt(4)=='1');
		this.userView1=(calques.length()!=10 || calques.charAt(5)=='1');
		this.userView2=(calques.length()!=10 || calques.charAt(6)=='1');
		this.userView3=(calques.length()!=10 || calques.charAt(7)=='1');
		this.userView4=(calques.length()!=10 || calques.charAt(8)=='1');
		this.userView5=(calques.length()!=10 || calques.charAt(9)=='1');

		this.viewLength=length;

	}

	public synchronized boolean updatedMASGraphicalRepresentation()
	{
		boolean res= (this.updateRequired||this.waitingRequieredUpdate&&((new aDate()).differenceToMS(this.dateOfFirstUpdateRequirement)>IHM_MASGraphicalRepresentation.TIME_BETWEEN_TWO_GRAPHICAL_VIEW));
		
		if(res&&!this.updateRequired)
		{
			this.waitingRequieredUpdate=false;
			this.dateOfFirstUpdateRequirement=new aDate();
		}
		this.updateRequired=false;
		
		return res;
	}


	public synchronized void requireGraphicalUpdate()
	{


		if(this.dateOfFirstUpdateRequirement==null) 
		{
			this.dateOfFirstUpdateRequirement=new aDate();
			this.updateRequired=true;
			this.waitingRequieredUpdate=false;
		}
		else
		{
			if((new aDate()).differenceToMS(this.dateOfFirstUpdateRequirement)>IHM_MASGraphicalRepresentation.TIME_BETWEEN_TWO_GRAPHICAL_VIEW)
			{
				this.updateRequired=true;
				this.waitingRequieredUpdate=false;
				this.dateOfFirstUpdateRequirement=new aDate();
			}
			else
				this.waitingRequieredUpdate=true;

		}
	}



	public String toString()
	{
		return "Area view : offset="+offset.toString()+"  dim="+viewAreaDimension.toString()+"   layer="+this.layer+"  scale="+scale+"  //  "+(this.updateRequired ? "update required at "+this.dateOfFirstUpdateRequirement : "no update required");
	}

	@Override
	public IntegerPosition getOffset() {
		// TODO Auto-generated method stub
		return this.offset;
	}

	@Override
	public double getScale() {
		// TODO Auto-generated method stub
		return this.scale;
	}
	
	public int getLayer() {
		// TODO Auto-generated method stub
		return this.layer;
	}

	@Override
	public Dimension getViewAreaDimension() {
		// TODO Auto-generated method stub
		return this.viewAreaDimension;
	}

	public boolean mustUpdateAgentsViewModel()
	{
		return this.mustUpdateAgentsViewModel;
	}

	public void agentsViewModelUpdated()
	{
		this.mustUpdateAgentsViewModel=false;
	}

	public boolean isVisibleEnvironmentLayerView()
	{
		return this.environmentView;
	}
	public boolean isVisibleAgentlayerView()
	{
		return this.agentView;
	}
	public boolean isVisibleEnergylayerView()
	{
		return this.energyView;
	}
	public boolean isVisibleLinkLayerView()
	{
		return this.connexionView;
	}
	public boolean isVisibleTextLayerView()
	{
		return this.textView;
	}
	public boolean isVisibleUserView(int id)
	{
		switch(id)
		{
		case 1: return this.userView1;
		case 2: return this.userView1;
		case 3: return this.userView1;
		case 4: return this.userView1;
		case 5: return this.userView1;
		}
		return false;
	}
	public AgentViewEnumType getTypeOfAgentViews()
	{
		return this.typeOfAgentViews;
	}
	public int getViewLength()
	{
		return this.viewLength;
	}



}
